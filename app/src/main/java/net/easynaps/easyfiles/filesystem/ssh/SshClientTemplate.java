package net.easynaps.easyfiles.filesystem.ssh;

import android.support.annotation.NonNull;

import net.schmizz.sshj.SSHClient;

import java.io.IOException;

public abstract class SshClientTemplate
{
    public final String url;

    public final boolean closeClientOnFinish;

    /**
     * Constructor, with closeClientOnFinish set to true (that the connection must close after <code>execute</code>.
     *
     * @param url SSH connection URL, in the form of <code>ssh://&lt;username&gt;:&lt;password&gt;@&lt;host&gt;:&lt;port&gt;</code> or <code>ssh://&lt;username&gt;@&lt;host&gt;:&lt;port&gt;</code>
     */
    public SshClientTemplate(@NonNull String url)
    {
        this(url, true);
    }

    /**
     * Constructor.
     *
     * @param url SSH connection URL, in the form of <code>ssh://&lt;username&gt;:&lt;password&gt;@&lt;host&gt;:&lt;port&gt;</code> or <code>ssh://&lt;username&gt;@&lt;host&gt;:&lt;port&gt;</code>
     * @param closeClientOnFinish if set to false, connection will be kept on after <code>execute</code> finishes. This is useful for other calls to reuse the connection.
     */
    public SshClientTemplate(@NonNull String url, boolean closeClientOnFinish)
    {
        this.url = url;
        this.closeClientOnFinish = closeClientOnFinish;
    }

    /**
     * Implement logic here.
     *
     * @param client {@link SSHClient} instance, with connection opened and authenticated
     * @param <T> Requested return type
     * @return Result of the execution of the type requested
     * @throws IOException
     */
    public abstract <T> T execute(@NonNull SSHClient client) throws IOException;
}
