package net.easynaps.easyfiles.filesystem.ssh;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import net.easynaps.easyfiles.R;
import net.easynaps.easyfiles.activities.MainActivity;
import net.easynaps.easyfiles.filesystem.HybridFileParcelable;
import net.easynaps.easyfiles.ui.icons.MimeTypes;
import net.easynaps.easyfiles.utils.SmbUtil;
import net.easynaps.easyfiles.utils.application.AppConfig;
import net.easynaps.easyfiles.utils.cloud.CloudStreamer;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.SFTPClient;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import static net.easynaps.easyfiles.filesystem.ssh.SshConnectionPool.SSH_URI_PREFIX;

public abstract class SshClientUtils
{
    private static final String TAG = "SshClientUtils";

    /**
     * Execute the given SshClientTemplate.
     *
     * This template pattern is borrowed from Spring Framework, to simplify code on operations
     * using SftpClientTemplate.
     *
     * @param template {@link SshClientTemplate} to execute
     * @param <T> Type of return value
     * @return Template execution results
     */
    public static final <T> T execute(@NonNull SshClientTemplate template) {
        SSHClient client = null;
        T retval = null;
        try {
            client = SshConnectionPool.getInstance().getConnection(template.url);
            if(client != null)
                retval = template.execute(client);
            else
                throw new RuntimeException("Unable to execute template");
        } catch(Exception e) {
            Log.e(TAG, "Error executing template method", e);
        } finally {
            if(client != null && template.closeClientOnFinish) {
                tryDisconnect(client);
            }
        }
        return retval;
    }

    /**
     * Execute the given template with SshClientTemplate.
     *
     * @param template {@link SshClientSessionTemplate} to execute
     * @param <T> Type of return value
     * @return Template execution results
     */
    public static final <T> T execute(@NonNull final SshClientSessionTemplate template) {
        return execute(new SshClientTemplate(template.url, false) {
            @Override
            public T execute(SSHClient client) throws IOException {
                Session session = null;
                T retval = null;
                try {
                    session = client.startSession();
                    retval = template.execute(session);
                } catch(IOException e) {
                    Log.e(TAG, "Error executing template method", e);
                } finally {
                    if(session != null && session.isOpen()) {
                        try {
                            session.close();
                        } catch(IOException e) {
                            Log.w(TAG, "Error closing SFTP client", e);
                        }
                    }
                }
                return retval;
            }
        });
    }

    /**
     * Execute the given template with SshClientTemplate.
     *
     * @param template {@link SFtpClientTemplate} to execute
     * @param <T> Type of return value
     * @return Template execution results
     */
    public static final <T> T execute(@NonNull final SFtpClientTemplate template) {
        return execute(new SshClientTemplate(template.url, false) {
            @Override
            public T execute(SSHClient client) throws IOException {
                SFTPClient sftpClient = null;
                T retval = null;
                try {
                    sftpClient = client.newSFTPClient();
                    retval = template.execute(sftpClient);
                } catch(IOException e) {
                    Log.e(TAG, "Error executing template method", e);
                } finally {
                    if(sftpClient != null && template.closeClientOnFinish) {
                        try {
                            sftpClient.close();
                        } catch(IOException e) {
                            Log.w(TAG, "Error closing SFTP client", e);
                        }
                    }
                }
                return retval;
            }
        });
    }

    /**
     * Convenience method to call {@link net.pubnative.easyfiles.utils.SmbUtil#getSmbEncryptedPath(Context, String)} if the given
     * SSH URL contains the password (assuming the password is encrypted).
     *
     * @param fullUri SSH URL
     * @return SSH URL with the password (if exists) encrypted
     */
    public static final String encryptSshPathAsNecessary(@NonNull String fullUri) {
        String uriWithoutProtocol = fullUri.substring(SSH_URI_PREFIX.length(), fullUri.indexOf('@'));
        try {
            return (uriWithoutProtocol.indexOf(':') > 0) ?
                    SmbUtil.getSmbEncryptedPath(AppConfig.getInstance(), fullUri) :
                    fullUri;
        } catch(IOException | GeneralSecurityException e){
            Log.e(TAG, "Error encrypting path", e);
            return fullUri;
        }
    }

    /**
     * Convenience method to call {@link SmbUtil#getSmbDecryptedPath(Context, String)} if the given
     * SSH URL contains the password (assuming the password is encrypted).
     *
     * @param fullUri SSH URL
     * @return SSH URL with the password (if exists) decrypted
     */
    public static final String decryptSshPathAsNecessary(@NonNull String fullUri) {
        String uriWithoutProtocol = fullUri.substring(SSH_URI_PREFIX.length(), fullUri.indexOf('@'));
        try {
            return (uriWithoutProtocol.indexOf(':') > 0) ?
                    SmbUtil.getSmbDecryptedPath(AppConfig.getInstance(), fullUri) :
                    fullUri;
        } catch(IOException | GeneralSecurityException e){
            Log.e(TAG, "Error decrypting path", e);
            return fullUri;
        }
    }

    /**
     * Convenience method to extract the Base URL from the given SSH URL.
     *
     * For example, given <code>ssh://user:password@127.0.0.1:22/home/user/foo/bar</code>, this
     * method returns <code>ssh://user:password@127.0.0.1:22</code>.
     *
     * @param fullUri Full SSH URL
     * @return The remote path part of the full SSH URL
     */
    public static final String extractBaseUriFrom(@NonNull String fullUri) {
        String uriWithoutProtocol = fullUri.substring(SSH_URI_PREFIX.length());
        return uriWithoutProtocol.indexOf('/') == -1 ?
            fullUri :
            fullUri.substring(0, uriWithoutProtocol.indexOf('/') + SSH_URI_PREFIX.length());
    }

    /**
     * Convenience method to extract the remote path from the given SSH URL.
     *
     * For example, given <code>ssh://user:password@127.0.0.1:22/home/user/foo/bar</code>, this
     * method returns <code>/home/user/foo/bar</code>.
     *
     * @param fullUri Full SSH URL
     * @return The remote path part of the full SSH URL
     */
    public static final String extractRemotePathFrom(@NonNull String fullUri) {
        String uriWithoutProtocol = fullUri.substring(SSH_URI_PREFIX.length());
        return uriWithoutProtocol.indexOf('/') == -1 ?
            "/" :
            uriWithoutProtocol.substring(uriWithoutProtocol.indexOf('/'));
    }

    /**
     * Disconnects the given {@link SSHClient} but wrap all exceptions beneath, so callers are free
     * from the hassles of handling thrown exceptions.
     *
     * @param client {@link SSHClient} instance
     */
    public static final void tryDisconnect(SSHClient client) {
        if(client != null && client.isConnected()){
            try {
                client.disconnect();
            } catch (IOException e) {
                Log.w(TAG, "Error closing SSHClient connection", e);
            }
        }
    }

    public static void launchSftp(final HybridFileParcelable baseFile, final MainActivity activity) {
        final CloudStreamer streamer = CloudStreamer.getInstance();

        new Thread(() -> {
            try {
                streamer.setStreamSrc(baseFile.getInputStream(activity), baseFile.getName(), baseFile.length(activity));
                activity.runOnUiThread(() -> {
                    try {
                        File file = new File(SshClientUtils.extractRemotePathFrom(baseFile.getPath()));
                        Uri uri = Uri.parse(CloudStreamer.URL + Uri.fromFile(file).getEncodedPath());
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setDataAndType(uri, MimeTypes.getMimeType(baseFile.getPath(), baseFile.isDirectory()));
                        PackageManager packageManager = activity.getPackageManager();
                        List<ResolveInfo> resInfos = packageManager.queryIntentActivities(i, 0);
                        if (resInfos != null && resInfos.size() > 0)
                            activity.startActivity(i);
                        else
                            Toast.makeText(activity,
                                    activity.getResources().getString(R.string.smb_launch_error),
                                    Toast.LENGTH_SHORT).show();
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {

                e.printStackTrace();
            }
        }).start();
    }
}
