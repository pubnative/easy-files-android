package net.easynaps.easyfiles.asynchronous.services;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import net.easynaps.easyfiles.R;
import net.easynaps.easyfiles.activities.MainActivity;
import net.easynaps.easyfiles.filesystem.FileUtil;
import net.easynaps.easyfiles.filesystem.HybridFileParcelable;
import net.easynaps.easyfiles.ui.notifications.NotificationConstants;
import net.easynaps.easyfiles.utils.DatapointParcelable;
import net.easynaps.easyfiles.utils.ObtainableServiceBinder;
import net.easynaps.easyfiles.utils.ProgressHandler;
import net.easynaps.easyfiles.utils.ServiceWatcherUtil;
import net.easynaps.easyfiles.utils.files.FileUtils;
import net.easynaps.easyfiles.utils.files.GenericCopyUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipService extends AbstractProgressiveService {

    public static final String KEY_COMPRESS_PATH = "zip_path";
    public static final String KEY_COMPRESS_FILES = "zip_files";
    public static final String KEY_COMPRESS_BROADCAST_CANCEL = "zip_cancel";

    private final IBinder mBinder = new ObtainableServiceBinder<>(this);

    private CompressAsyncTask asyncTask;


    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private ProgressHandler progressHandler = new ProgressHandler();
    private volatile float progressPercent = 0f;
    private ProgressListener progressListener;
    // list of data packages, to initiate chart in process viewer fragment
    private ArrayList<DatapointParcelable> dataPackages = new ArrayList<>();

    @Override
    public void onCreate() {
        registerReceiver(receiver1, new IntentFilter(KEY_COMPRESS_BROADCAST_CANCEL));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        String mZipPath = intent.getStringExtra(KEY_COMPRESS_PATH);

        ArrayList<HybridFileParcelable> baseFiles = intent.getParcelableArrayListExtra(KEY_COMPRESS_FILES);

        File zipFile = new File(mZipPath);

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (!zipFile.exists()) {
            try {
                zipFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra(MainActivity.KEY_INTENT_PROCESS_VIEWER, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        mBuilder = new NotificationCompat.Builder(this, NotificationConstants.CHANNEL_NORMAL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(getResources().getString(R.string.compressing))
                .setSmallIcon(R.drawable.ic_zip_box_grey600_36dp);

        NotificationConstants.setMetadata(this, mBuilder, NotificationConstants.TYPE_NORMAL);
        startForeground(NotificationConstants.ZIP_ID, mBuilder.build());

        super.onStartCommand(intent, flags, startId);
        super.progressHalted();
        asyncTask = new CompressAsyncTask(this, baseFiles, mZipPath);
        asyncTask.execute();
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    protected NotificationManager getNotificationManager() {
        return mNotifyManager;
    }

    @Override
    protected NotificationCompat.Builder getNotificationBuilder() {
        return mBuilder;
    }

    @Override
    protected int getNotificationId() {
        return NotificationConstants.ZIP_ID;
    }

    @Override
    protected float getPercentProgress() {
        return progressPercent;
    }

    @Override
    protected void setPercentProgress(float progress) {
        progressPercent = progress;
    }

    public ProgressListener getProgressListener() {
        return progressListener;
    }

    @Override
    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    protected ArrayList<DatapointParcelable> getDataPackages() {
        return dataPackages;
    }

    @Override
    protected ProgressHandler getProgressHandler() {
        return progressHandler;
    }

    public class CompressAsyncTask extends AsyncTask<Void, Void, Void> {

        @SuppressLint("StaticFieldLeak")
        private ZipService zipService;
        private ZipOutputStream zos;
        private String zipPath;
        private ServiceWatcherUtil watcherUtil;
        private long totalBytes = 0L;
        private ArrayList<HybridFileParcelable> baseFiles;

        public CompressAsyncTask(ZipService zipService, ArrayList<HybridFileParcelable> baseFiles, String zipPath) {
            this.zipService = zipService;
            this.baseFiles = baseFiles;
            this.zipPath = zipPath;
        }

        protected Void doInBackground(Void... p1) {
            // setting up service watchers and initial data packages
            // finding total size on background thread (this is necessary condition for SMB!)
            totalBytes = FileUtils.getTotalBytes(baseFiles, zipService.getApplicationContext());

            progressHandler.setSourceSize(baseFiles.size());
            progressHandler.setTotalSize(totalBytes);
            progressHandler.setProgressListener((fileName, sourceFiles, sourceProgress, totalSize, writtenSize, speed) -> {
                publishResults(fileName, sourceFiles, sourceProgress, totalSize,
                        writtenSize, speed, false, false);
            });


            zipService.addFirstDatapoint(baseFiles.get(0).getName(), baseFiles.size(), totalBytes, false);

            execute(zipService.getApplicationContext(), FileUtils.hybridListToFileArrayList(baseFiles), zipPath);
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressHandler.setCancelled(true);
            File zipFile = new File(zipPath);
            if (zipFile.exists()) zipFile.delete();
        }

        @Override
        public void onPostExecute(Void a) {
            watcherUtil.stopWatch();
            Intent intent = new Intent(MainActivity.KEY_INTENT_LOAD_LIST);
            intent.putExtra(MainActivity.KEY_INTENT_LOAD_LIST_FILE, zipPath);
            zipService.sendBroadcast(intent);
            zipService.stopSelf();
        }

        public void execute(final @NonNull Context context, ArrayList<File> baseFiles, String zipPath) {
            OutputStream out;
            File zipDirectory = new File(zipPath);
            watcherUtil = new ServiceWatcherUtil(progressHandler);
            watcherUtil.watch(ZipService.this);

            try {
                out = FileUtil.getOutputStream(zipDirectory, context);
                zos = new ZipOutputStream(new BufferedOutputStream(out));

                int fileProgress = 0;
                for (File file : baseFiles) {
                    if (isCancelled()) return;

                    progressHandler.setFileName(file.getName());
                    progressHandler.setSourceFilesProcessed(++fileProgress);
                    compressFile(file, "");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    zos.flush();
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void compressFile(File file, String path) throws IOException, NullPointerException {
            if (!file.isDirectory()) {
                if (progressHandler.getCancelled()) return;

                byte[] buf = new byte[GenericCopyUtil.DEFAULT_BUFFER_SIZE];
                int len;
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
                zos.putNextEntry(new ZipEntry(path + "/" + file.getName()));
                while ((len = in.read(buf)) > 0) {
                    zos.write(buf, 0, len);
                    ServiceWatcherUtil.position += len;
                }
                in.close();
                return;
            }

            if (file.list() == null) return;

            for (File currentFile : file.listFiles()) {
                compressFile(currentFile, path + File.separator + file.getName());
            }
        }
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */

    private BroadcastReceiver receiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            asyncTask.cancel(true);
        }
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(receiver1);
    }

}
