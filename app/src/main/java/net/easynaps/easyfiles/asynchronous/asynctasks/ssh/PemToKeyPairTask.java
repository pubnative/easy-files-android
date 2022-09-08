package net.easynaps.easyfiles.asynchronous.asynctasks.ssh;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hierynomus.sshj.userauth.keyprovider.OpenSSHKeyV1KeyFile;

import net.easynaps.easyfiles.R;
import net.easynaps.easyfiles.asynchronous.asynctasks.AsyncTaskResult;
import net.easynaps.easyfiles.utils.application.AppConfig;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import net.schmizz.sshj.userauth.keyprovider.OpenSSHKeyFile;
import net.schmizz.sshj.userauth.keyprovider.PuTTYKeyFile;
import net.schmizz.sshj.userauth.password.PasswordFinder;
import net.schmizz.sshj.userauth.password.Resource;

import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.KeyPair;
import java.security.Provider;
import java.security.Security;

/**
 * {@link AsyncTask} to convert given {@link InputStream} into {@link KeyPair} which is requird by
 * sshj, using {@link JcaPEMKeyConverter}.
 *
 * @see JcaPEMKeyConverter
 * @see KeyProvider
 * @see OpenSSHKeyV1KeyFile
 * @see PuTTYKeyFile
 * @see net.easynaps.easyfiles.filesystem.ssh.SshConnectionPool#create(Uri)
 * @see net.schmizz.sshj.SSHClient#authPublickey(String, KeyProvider...)
 */
public class PemToKeyPairTask extends AsyncTask<Void, Void, AsyncTaskResult<KeyPair>>
{
    private static final String TAG = "PemToKeyPairTask";

    private final PemToKeyPairConverter[] converters = {
        new JcaPemToKeyPairConverter(),
        new OpenSshPemToKeyPairConverter(),
        new OpenSshV1PemToKeyPairConverter(),
        new PuttyPrivateKeyToKeyPairConverter()
    };

    private final byte[] pemFile;

    private final AsyncTaskResult.Callback<AsyncTaskResult<KeyPair>> callback;

    private final PasswordFinder passwordFinder;

    public PemToKeyPairTask(@NonNull InputStream pemFile, AsyncTaskResult.Callback<AsyncTaskResult<KeyPair>> callback) throws IOException {
        this(IOUtils.readFully(pemFile).toByteArray(), callback, null);
    }

    public PemToKeyPairTask(@NonNull String pemContent, AsyncTaskResult.Callback<AsyncTaskResult<KeyPair>> callback) {
        this(pemContent.getBytes(), callback, null);
    }

    public PemToKeyPairTask(@NonNull byte[] pemContent, AsyncTaskResult.Callback<AsyncTaskResult<KeyPair>> callback,
                            String keyPassphrase) {
        this.pemFile = pemContent;
        this.callback = callback;
        if(keyPassphrase == null)
            passwordFinder = null;
        else
            passwordFinder = new PasswordFinder() {
                @Override
                public char[] reqPassword(Resource<?> resource) {
                    return keyPassphrase.toCharArray();
                }

                @Override
                public boolean shouldRetry(Resource<?> resource) {
                    return false;
                }
            };
    }

    @Override
    protected AsyncTaskResult<KeyPair> doInBackground(Void... voids) {
        AsyncTaskResult<KeyPair> retval = null;
        for(Provider provider : Security.getProviders())
            Log.d(TAG, "Provider: " + provider.getName());

        try {
            for(PemToKeyPairConverter converter : converters) {
                KeyPair keyPair = converter.convert(new String(pemFile));
                if(keyPair != null) {
                    retval = new AsyncTaskResult<KeyPair>(keyPair);
                    break;
                }
            }
            if(retval == null)
                throw new IOException("No converter available to parse selected PEM");
        } catch (IOException e) {
            Log.e(TAG, "IOException reading PEM", e);
            retval = new AsyncTaskResult<KeyPair>(e);
        }

        return retval;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<KeyPair> result) {
        if(result.exception != null) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(AppConfig.getInstance().getActivityContext());
            EditText textfield = new EditText(builder.getContext());
            textfield.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.customView(textfield, false).title(R.string.ssh_key_prompt_passphrase)
                    .positiveText(R.string.ok)
                    .onPositive(((dialog, which) -> {
                        new PemToKeyPairTask(pemFile, callback, textfield.getText().toString()).execute();
                        dialog.dismiss();
                })).negativeText(R.string.cancel)
                    .onNegative(((dialog, which) -> {
                        dialog.dismiss();
                        toastOnParseError(result);
            }));

            builder.show();
        }
        if(callback != null) {
            callback.onResult(result);
        }
    }

    private void toastOnParseError(AsyncTaskResult<KeyPair> result){
        Toast.makeText(AppConfig.getInstance().getActivityContext(),
                String.format(AppConfig.getInstance().getResources().getString(R.string.ssh_pem_key_parse_error),
                        result.exception.getLocalizedMessage()), Toast.LENGTH_LONG).show();
    }

    private interface PemToKeyPairConverter {
        KeyPair convert(String source);
    }

    private class JcaPemToKeyPairConverter implements PemToKeyPairConverter {
        @Override
        public KeyPair convert(String source) {
            PEMParser pemParser = new PEMParser(new StringReader(source));
            try {
                PEMKeyPair keyPair = (PEMKeyPair) pemParser.readObject();
                JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
                return converter.getKeyPair(keyPair);
            } catch (Exception ignored) {
                return null;
            }
        }
    }

    private class OpenSshPemToKeyPairConverter implements PemToKeyPairConverter {
        @Override
        public KeyPair convert(String source) {
            OpenSSHKeyFile converter = new OpenSSHKeyFile();
            converter.init(new StringReader(source), passwordFinder);
            try {
                return new KeyPair(converter.getPublic(), converter.getPrivate());
            } catch (Exception ignored) {
                return null;
            }
        }
    }

    private class OpenSshV1PemToKeyPairConverter implements PemToKeyPairConverter {
        @Override
        public KeyPair convert(String source) {
            OpenSSHKeyV1KeyFile converter = new OpenSSHKeyV1KeyFile();
            converter.init(new StringReader(source), passwordFinder);
            try {
                return new KeyPair(converter.getPublic(), converter.getPrivate());
            } catch (Exception ignored) {
                return null;
            }
        }
    }

    private class PuttyPrivateKeyToKeyPairConverter implements PemToKeyPairConverter {
        @Override
        public KeyPair convert(String source) {
            PuTTYKeyFile converter = new PuTTYKeyFile();
            converter.init(new StringReader(source), passwordFinder);
            try {
                return new KeyPair(converter.getPublic(), converter.getPrivate());
            } catch (Exception ignored) {
                ignored.printStackTrace();
                return null;
            }
        }
    }
}
