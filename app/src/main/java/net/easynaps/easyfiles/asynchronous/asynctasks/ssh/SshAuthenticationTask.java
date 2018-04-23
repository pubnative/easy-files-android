package net.easynaps.easyfiles.asynchronous.asynctasks.ssh;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.Toast;

import net.easynaps.easyfiles.R;
import net.easynaps.easyfiles.asynchronous.asynctasks.AsyncTaskResult;
import net.easynaps.easyfiles.filesystem.ssh.CustomSshJConfig;
import net.easynaps.easyfiles.utils.application.AppConfig;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.DisconnectReason;
import net.schmizz.sshj.common.KeyType;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.userauth.UserAuthException;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static net.easynaps.easyfiles.filesystem.ssh.SshConnectionPool.SSH_CONNECT_TIMEOUT;

/**
 * {@link AsyncTask} for authenticating with SSH server to verify if parameters are correct.
 *
 * Used by {@link net.pubnative.easyfiles.ui.dialogs.SftpConnectDialog}.
 *
 * @see SSHClient
 * @see SSHClient#authPassword(String, String)
 * @see SSHClient#authPublickey(String, KeyProvider...)
 * @see net.pubnative.easyfiles.ui.dialogs.SftpConnectDialog#authenticateAndSaveSetup(String, String, int, String, String, String, String, KeyPair, boolean)
 * @see net.pubnative.easyfiles.filesystem.ssh.SshConnectionPool#create(Uri)
 */
public class SshAuthenticationTask extends AsyncTask<Void, Void, AsyncTaskResult<SSHClient>>
{
    private final String hostname;
    private final int port;
    private final String hostKey;

    private final String username;
    private final String password;
    private final KeyPair privateKey;

    /**
     * Constructor.
     *
     * @param hostname hostname, required
     * @param port port, must be unsigned integer
     * @param hostKey SSH host fingerprint, required
     * @param username login username, required
     * @param password login password, required if using password authentication
     * @param privateKey login {@link KeyPair}, required if using key-based authentication
     */
    public SshAuthenticationTask(@NonNull String hostname,
                                 @NonNull int port,
                                 @NonNull String hostKey,
                                 @NonNull String username,
                                 String password,
                                 KeyPair privateKey)
    {
        this.hostname = hostname;
        this.port = port;
        this.hostKey = hostKey;
        this.username = username;
        this.password = password;
        this.privateKey = privateKey;
    }

    @Override
    protected AsyncTaskResult<SSHClient> doInBackground(Void... voids) {

        final SSHClient sshClient = new SSHClient(new CustomSshJConfig());
        sshClient.addHostKeyVerifier(hostKey);
        sshClient.setConnectTimeout(SSH_CONNECT_TIMEOUT);

        try {
            sshClient.connect(hostname, port);
            if(password != null && !"".equals(password)) {
                sshClient.authPassword(username, password);
                return new AsyncTaskResult<SSHClient>(sshClient);
            }
            else
            {
                sshClient.authPublickey(username, new KeyProvider() {
                    @Override
                    public PrivateKey getPrivate() throws IOException {
                        return privateKey.getPrivate();
                    }

                    @Override
                    public PublicKey getPublic() throws IOException {
                        return privateKey.getPublic();
                    }

                    @Override
                    public KeyType getType() throws IOException {
                        return KeyType.fromKey(getPublic());
                    }
                });
                return new AsyncTaskResult<SSHClient>(sshClient);
            }

        } catch (UserAuthException e) {
            e.printStackTrace();
            return new AsyncTaskResult<SSHClient>(e);
        } catch (TransportException e) {
            e.printStackTrace();
            return new AsyncTaskResult<SSHClient>(e);
        } catch (IOException e) {
            e.printStackTrace();
            return new AsyncTaskResult<SSHClient>(e);
        }
    }

    //If authentication failed, use Toast to notify user.
    @Override
    protected void onPostExecute(AsyncTaskResult<SSHClient> result) {

        if(result.exception != null) {
            if(SocketException.class.isAssignableFrom(result.exception.getClass())
                    || SocketTimeoutException.class.isAssignableFrom(result.exception.getClass())) {
                Toast.makeText(AppConfig.getInstance(),
                        String.format(AppConfig.getInstance().getResources().getString(R.string.ssh_connect_failed),
                                hostname, port, result.exception.getLocalizedMessage()),
                        Toast.LENGTH_LONG).show();
                return;
            }
            else if(TransportException.class.isAssignableFrom(result.exception.getClass()))
            {
                DisconnectReason disconnectReason = TransportException.class.cast(result.exception).getDisconnectReason();
                if(DisconnectReason.HOST_KEY_NOT_VERIFIABLE.equals(disconnectReason)) {
                    new AlertDialog.Builder(AppConfig.getInstance().getActivityContext())
                            .setTitle(R.string.ssh_connect_failed_host_key_changed_title)
                            .setMessage(R.string.ssh_connect_failed_host_key_changed_message)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
                return;
            }
            else if(password != null) {
                Toast.makeText(AppConfig.getInstance(), R.string.ssh_authentication_failure_password, Toast.LENGTH_LONG).show();
                return;
            }
            else if(privateKey != null) {
                Toast.makeText(AppConfig.getInstance(), R.string.ssh_authentication_failure_key, Toast.LENGTH_LONG).show();
                return;
            }
        }
    }
}