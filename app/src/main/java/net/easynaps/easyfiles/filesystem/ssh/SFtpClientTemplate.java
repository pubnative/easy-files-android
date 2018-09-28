package net.easynaps.easyfiles.filesystem.ssh;

import androidx.annotation.NonNull;

import net.schmizz.sshj.sftp.SFTPClient;

import java.io.IOException;

public abstract class SFtpClientTemplate
{
    public final String url;

    public final boolean closeClientOnFinish;

    public SFtpClientTemplate(@NonNull String url)
    {
        this(url, true);
    }

    /**
     * If closeClientOnFinish is set to true, calling code needs to handle closing of {@link SFTPClient}
     * session.
     *
     * @param url SSH connection URL, in the form of <code>ssh://&lt;username&gt;:&lt;password&gt;@&lt;host&gt;:&lt;port&gt;</code> or <code>ssh://&lt;username&gt;@&lt;host&gt;:&lt;port&gt;</code>
     * @param closeClientOnFinish
     */
    public SFtpClientTemplate(@NonNull String url, boolean closeClientOnFinish)
    {
        this.url = url;
        this.closeClientOnFinish = closeClientOnFinish;
    }

    /**
     * Implement logic here.
     *
     * @param client {@link SFTPClient} instance, with connection opened and authenticated, and
     *                                SSH session had been set up.
     * @param <T> Requested return type
     * @return Result of the execution of the type requested
     * @throws IOException
     */
    public abstract <T> T execute(@NonNull SFTPClient client) throws IOException;
}
