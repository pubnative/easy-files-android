package net.easynaps.easyfiles.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;

import net.easynaps.easyfiles.R;
import net.easynaps.easyfiles.activities.MainActivity;
import net.easynaps.easyfiles.asynchronous.asynctasks.AsyncTaskResult;
import net.easynaps.easyfiles.asynchronous.asynctasks.ssh.GetSshHostFingerprintTask;
import net.easynaps.easyfiles.asynchronous.asynctasks.ssh.PemToKeyPairTask;
import net.easynaps.easyfiles.asynchronous.asynctasks.ssh.SshAuthenticationTask;
import net.easynaps.easyfiles.database.UtilsHandler;
import net.easynaps.easyfiles.filesystem.ssh.SshClientUtils;
import net.easynaps.easyfiles.filesystem.ssh.SshConnectionPool;
import net.easynaps.easyfiles.fragments.MainFragment;
import net.easynaps.easyfiles.utils.BookSorter;
import net.easynaps.easyfiles.utils.DataUtils;
import net.easynaps.easyfiles.utils.OpenMode;
import net.easynaps.easyfiles.utils.SimpleTextWatcher;
import net.easynaps.easyfiles.utils.application.AppConfig;
import net.easynaps.easyfiles.utils.color.ColorUsage;
import net.easynaps.easyfiles.utils.provider.UtilitiesProvider;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.SecurityUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Collections;

public class SftpConnectDialog extends DialogFragment {
    private static final String TAG = "SftpConnectDialog";

    //Idiotic code
    //FIXME: agree code on
    private static final int SELECT_PEM_INTENT = 0x01010101;

    private UtilitiesProvider utilsProvider;

    private UtilsHandler utilsHandler;

    private Context context;

    private Uri selectedPem = null;

    private KeyPair selectedParsedKeyPair = null;

    private String selectedParsedKeyPairName = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utilsProvider = AppConfig.getInstance().getUtilsProvider();
        utilsHandler = AppConfig.getInstance().getUtilsHandler();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();
        final boolean edit=getArguments().getBoolean("edit",false);
        final View v2 = getActivity().getLayoutInflater().inflate(R.layout.sftp_dialog, null);
        final EditText connectionET = v2.findViewById(R.id.connectionET);
        final EditText addressET = v2.findViewById(R.id.ipET);
        final EditText portET = v2.findViewById(R.id.portET);
        final EditText usernameET = v2.findViewById(R.id.usernameET);
        final EditText passwordET = v2.findViewById(R.id.passwordET);
        final Button selectPemBTN = v2.findViewById(R.id.selectPemBTN);

        // If it's new connection setup, set some default values
        // Otherwise, use given Bundle instance for filling in the blanks
        if(!edit) {
            connectionET.setText(R.string.scp_con);
            portET.setText(Integer.toString(SshConnectionPool.SSH_DEFAULT_PORT));
        } else {
            connectionET.setText(getArguments().getString("name"));
            addressET.setText(getArguments().getString("address"));
            portET.setText(getArguments().getString("port"));
            usernameET.setText(getArguments().getString("username"));
            if(getArguments().getBoolean("hasPassword")) {
                passwordET.setHint(R.string.password_unchanged);
            } else {
                selectedParsedKeyPairName = getArguments().getString("keypairName");
                selectPemBTN.setText(selectedParsedKeyPairName);
            }
        }

        //For convenience, so I don't need to press backspace all the time
        portET.setOnFocusChangeListener((v, hasFocus) -> {
        if(hasFocus)
            portET.selectAll();
        });

        int accentColor = utilsProvider.getColorPreference().getColor(ColorUsage.ACCENT);

        //Use system provided action to get Uri to PEM.
        //If MaterialDialog.Builder can be upgraded we may use their file selection dialog too
        selectPemBTN.setOnClickListener(v -> {
        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, SELECT_PEM_INTENT);
        });

        //Define action for buttons
        final MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(context);
        dialogBuilder.title((R.string.scp_con));
        dialogBuilder.autoDismiss(false);
        dialogBuilder.customView(v2, true);
        dialogBuilder.theme(utilsProvider.getAppTheme().getMaterialDialogTheme());
        dialogBuilder.negativeText(R.string.cancel);
        dialogBuilder.positiveText(edit ? R.string.update : R.string.create);
        dialogBuilder.positiveColor(accentColor);
        dialogBuilder.negativeColor(accentColor);
        dialogBuilder.neutralColor(accentColor);
        dialogBuilder.onPositive((dialog, which) -> {

            final String connectionName = connectionET.getText().toString();
            final String hostname = addressET.getText().toString();
            final int port = Integer.parseInt(portET.getText().toString());
            final String username = usernameET.getText().toString();
            final String password = passwordET.getText() != null ?
                    passwordET.getText().toString() : null;

            String sshHostKey = utilsHandler.getSshHostKey(deriveSftpPathFrom(hostname, port,
                    username, password, selectedParsedKeyPair));

            if (sshHostKey != null) {
                authenticateAndSaveSetup(connectionName, hostname, port, sshHostKey, username,
                        password, selectedParsedKeyPairName, selectedParsedKeyPair, edit);
            } else {
                new GetSshHostFingerprintTask(hostname, port, taskResult -> {
                    PublicKey hostKey = taskResult.result;
                    if (hostKey != null) {
                        final String hostKeyFingerprint = SecurityUtils.getFingerprint(hostKey);
                        StringBuilder sb = new StringBuilder(hostname);
                        if (port != SshConnectionPool.SSH_DEFAULT_PORT && port > 0)
                            sb.append(':').append(port);

                        final String hostAndPort = sb.toString();

                        new AlertDialog.Builder(context).setTitle(R.string.ssh_host_key_verification_prompt_title)
                                .setMessage(String.format(getResources().getString(R.string.ssh_host_key_verification_prompt),
                                        hostAndPort, hostKey.getAlgorithm(), hostKeyFingerprint))
                                .setCancelable(true)
                                .setPositiveButton(R.string.yes, (dialog1, which1) -> {
                                    //This closes the host fingerprint verification dialog
                                    dialog1.dismiss();
                                    if (authenticateAndSaveSetup(connectionName, hostname, port,
                                            hostKeyFingerprint, username, password,
                                            selectedParsedKeyPairName, selectedParsedKeyPair, edit)) {
                                        dialog1.dismiss();
                                        Log.d(TAG, "Saved setup");
                                        dismiss();
                                    }
                                }).setNegativeButton(R.string.no, (dialog1, which1) -> dialog1.dismiss()).show();
                    }
                }).execute();
            }
        }).onNegative((dialog, which) -> dialog.dismiss());

        //If we are editing connection settings, give new actions for neutral and negative buttons
        if(edit) {
            Log.d(TAG, "Edit? " + edit);
            dialogBuilder.negativeText(R.string.delete).onNegative((dialog, which) -> {

            final String connectionName = connectionET.getText().toString();
            final String hostname = addressET.getText().toString();
            final int port = Integer.parseInt(portET.getText().toString());
            final String username = usernameET.getText().toString();

            final String path = deriveSftpPathFrom(hostname, port, username,
                    getArguments().getString("password", null), selectedParsedKeyPair);
            int i = DataUtils.getInstance().containsServer(new String[]{connectionName, path});

            if (i != -1) {
                DataUtils.getInstance().removeServer(i);

                AppConfig.runInBackground(() -> {
                    utilsHandler.removeSftpPath(connectionName, path);
                });
                ((MainActivity) getActivity()).getDrawer().refreshDrawer();
            }
            dialog.dismiss();
            }).neutralText(R.string.cancel).onNeutral((dialog, which) -> dialog.dismiss());
        }

        MaterialDialog dialog = dialogBuilder.build();

        // Some validations to make sure the Create/Update button is clickable only when required
        // setting values are given
        final View okBTN = dialog.getActionButton(DialogAction.POSITIVE);
        if(!edit)
            okBTN.setEnabled(false);

        TextWatcher validator = new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                int port = portET.getText().toString().length() > 0 ? Integer.parseInt(portET.getText().toString()) : -1;
                okBTN.setEnabled(
                        (connectionET.getText().length() > 0
                     && addressET.getText().length() > 0
                     && port > 0 && port < 65536
                     && usernameET.getText().length() > 0
                     && (passwordET.getText().length() > 0 || selectedParsedKeyPair != null))
                );
            }
        };

        addressET.addTextChangedListener(validator);
        portET.addTextChangedListener(validator);
        usernameET.addTextChangedListener(validator);
        passwordET.addTextChangedListener(validator);

        return dialog;
    }

    /**
     * Set the PEM key for authentication when the Intent to browse file returned.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(SELECT_PEM_INTENT == requestCode && Activity.RESULT_OK == resultCode)
        {
            selectedPem = data.getData();
            Log.d(TAG, "Selected PEM: " + selectedPem.toString() + "/ "
                    + selectedPem.getLastPathSegment());
            
            try {
                InputStream selectedKeyContent = context.getContentResolver()
                        .openInputStream(selectedPem);
                new PemToKeyPairTask(selectedKeyContent, result -> {
                    if(result.result != null)
                    {
                        selectedParsedKeyPair = result.result;
                        selectedParsedKeyPairName = selectedPem.getLastPathSegment()
                                .substring(selectedPem.getLastPathSegment().indexOf('/')+1);
                        MDButton okBTN = ((MaterialDialog)getDialog())
                                .getActionButton(DialogAction.POSITIVE);
                        okBTN.setEnabled(okBTN.isEnabled() || true);

                        Button selectPemBTN = getDialog().findViewById(R.id.selectPemBTN);
                        selectPemBTN.setText(selectedParsedKeyPairName);
                    }
                }).execute();

            } catch(FileNotFoundException e) {
                Log.e(TAG, "File not found", e);
            } catch(IOException shouldNotHappen) {}
        }
    }

    private boolean authenticateAndSaveSetup(String connectionName, String hostname,
                                          int port, String hostKeyFingerprint,
                                          String username, String password,
                                          String selectedParsedKeyPairName,
                                          KeyPair selectedParsedKeyPair, boolean isEdit) {

        if(isEdit)
            password = getArguments().getString("password", null);

        final String path = deriveSftpPathFrom(hostname, port, username, password,
                selectedParsedKeyPair);

        final String encryptedPath = SshClientUtils.encryptSshPathAsNecessary(path);

        if(!isEdit) {
            try {
                AsyncTaskResult<SSHClient> taskResult = new SshAuthenticationTask(hostname, port,
                        hostKeyFingerprint, username, password, selectedParsedKeyPair).execute().get();
                SSHClient result = taskResult.result;
                if(result != null) {

                    if(DataUtils.getInstance().containsServer(path) == -1) {
                        DataUtils.getInstance().addServer(new String[]{connectionName, path});
                        ((MainActivity) getActivity()).getDrawer().refreshDrawer();

                        utilsHandler.addSsh(connectionName, encryptedPath, hostKeyFingerprint,
                                selectedParsedKeyPairName, getPemContents());

                        MainFragment ma = ((MainActivity)getActivity()).getCurrentMainFragment();
                        ma.loadlist(path, false, OpenMode.UNKNOWN);
                        dismiss();

                    } else {
                        Snackbar.make(getActivity().findViewById(R.id.content_frame),
                                getResources().getString(R.string.connection_exists), Snackbar.LENGTH_SHORT).show();
                        dismiss();
                    }
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            DataUtils.getInstance().removeServer(DataUtils.getInstance().containsServer(path));
            DataUtils.getInstance().addServer(new String[]{connectionName, path});
            Collections.sort(DataUtils.getInstance().getServers(), new BookSorter());
            ((MainActivity) getActivity()).getDrawer().refreshDrawer();

            AppConfig.runInBackground(() -> {
                utilsHandler.updateSsh(connectionName,
                        getArguments().getString("name"), encryptedPath,
                        selectedParsedKeyPairName, getPemContents());
            });

            dismiss();
            return true;
        }
    }

    //Decide the SSH URL depends on password/selected KeyPair
    private String deriveSftpPathFrom(String hostname, int port, String username, String password,
                                      KeyPair selectedParsedKeyPair) {
        return (selectedParsedKeyPair != null || password == null) ?
                String.format("ssh://%s@%s:%d", username, hostname, port) :
                String.format("ssh://%s:%s@%s:%d", username, password, hostname, port);
    }

    //Read the PEM content from InputStream to String.
    private String getPemContents() {
        if(selectedPem == null)
            return null;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getContentResolver().openInputStream(selectedPem)));
            StringBuilder sb = new StringBuilder();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch(FileNotFoundException e){
            return null;
        } catch(IOException e) {
            return null;
        }
    }
}
