package net.easynaps.easyfiles.asynchronous.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import net.easynaps.easyfiles.R;
import net.easynaps.easyfiles.activities.MainActivity;
import net.easynaps.easyfiles.filesystem.FileUtil;
import net.easynaps.easyfiles.filesystem.HybridFile;
import net.easynaps.easyfiles.filesystem.HybridFileParcelable;
import net.easynaps.easyfiles.ui.notifications.NotificationConstants;
import net.easynaps.easyfiles.utils.DatapointParcelable;
import net.easynaps.easyfiles.utils.ObtainableServiceBinder;
import net.easynaps.easyfiles.utils.OpenMode;
import net.easynaps.easyfiles.utils.ProgressHandler;
import net.easynaps.easyfiles.utils.ServiceWatcherUtil;
import net.easynaps.easyfiles.utils.files.CryptUtil;
import net.easynaps.easyfiles.utils.files.EncryptDecryptUtils;

import java.util.ArrayList;

public class DecryptService extends AbstractProgressiveService {

    public static final String TAG_SOURCE = "crypt_source";     // source file to encrypt or decrypt
    public static final String TAG_DECRYPT_PATH = "decrypt_path";
    public static final String TAG_OPEN_MODE = "open_mode";

    public static final String TAG_BROADCAST_CRYPT_CANCEL = "crypt_cancel";

    private Context context;
    private IBinder mBinder = new ObtainableServiceBinder<>(this);
    private ProgressHandler progressHandler = new ProgressHandler();
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private volatile float progressPercent = 0f;
    private ProgressListener progressListener;
    // list of data packages, to initiate chart in process viewer fragment
    private ArrayList<DatapointParcelable> dataPackages = new ArrayList<>();
    private ServiceWatcherUtil serviceWatcherUtil;
    private long totalSize = 0l;
    private OpenMode openMode;
    private String decryptPath;
    private HybridFileParcelable baseFile;
    private ArrayList<HybridFile> failedOps = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        registerReceiver(cancelReceiver, new IntentFilter(TAG_BROADCAST_CRYPT_CANCEL));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        baseFile = intent.getParcelableExtra(TAG_SOURCE);

        openMode = OpenMode.values()[intent.getIntExtra(TAG_OPEN_MODE, OpenMode.UNKNOWN.ordinal())];
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra(MainActivity.KEY_INTENT_PROCESS_VIEWER, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notificationBuilder = new NotificationCompat.Builder(this, NotificationConstants.CHANNEL_NORMAL_ID);
        notificationBuilder.setContentIntent(pendingIntent);

        decryptPath = intent.getStringExtra(TAG_DECRYPT_PATH);
        notificationBuilder.setContentTitle(getResources().getString(R.string.crypt_decrypting));
        notificationBuilder.setSmallIcon(R.drawable.ic_folder_lock_open_white_36dp);
        NotificationConstants.setMetadata(context, notificationBuilder, NotificationConstants.TYPE_NORMAL);

        startForeground(NotificationConstants.DECRYPT_ID, notificationBuilder.build());

        super.onStartCommand(intent, flags, startId);

        super.progressHalted();
        new DecryptService.BackgroundTask().execute();

        return START_STICKY;
    }

    class BackgroundTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String baseFileFolder = baseFile.isDirectory()?
                    baseFile.getPath():
                    baseFile.getPath().substring(0, baseFile.getPath().lastIndexOf('/'));

            if (baseFile.isDirectory())  totalSize = baseFile.folderSize(context);
            else totalSize = baseFile.length(context);

            progressHandler.setSourceSize(1);
            progressHandler.setTotalSize(totalSize);
            progressHandler.setProgressListener((fileName, sourceFiles, sourceProgress, totalSize, writtenSize, speed) -> {
                publishResults(fileName, sourceFiles, sourceProgress, totalSize,
                        writtenSize, speed, false, false);
            });
            serviceWatcherUtil = new ServiceWatcherUtil(progressHandler);

            addFirstDatapoint(baseFile.getName(), 1, totalSize, false);// we're using encrypt as move flag false

            if (FileUtil.checkFolder(baseFileFolder, context) == 1) {
                serviceWatcherUtil.watch(DecryptService.this);

                // we're here to decrypt, we'll decrypt at a custom path.
                // the path is to the same directory as in encrypted one in normal case
                // and the cache directory in case we're here because of the viewer
                try {
                    new CryptUtil(context, baseFile, decryptPath, progressHandler, failedOps);
                } catch (Exception e) {
                    e.printStackTrace();
                    failedOps.add(baseFile);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            serviceWatcherUtil.stopWatch();
            generateNotification(failedOps, false);

            Intent intent = new Intent(EncryptDecryptUtils.DECRYPT_BROADCAST);
            intent.putExtra(MainActivity.KEY_INTENT_LOAD_LIST_FILE, "");
            sendBroadcast(intent);
            stopSelf();
        }
    }

    @Override
    protected NotificationManager getNotificationManager() {
        return notificationManager;
    }

    @Override
    protected NotificationCompat.Builder getNotificationBuilder() {
        return notificationBuilder;
    }

    @Override
    protected int getNotificationId() {
        return NotificationConstants.DECRYPT_ID;
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

    @Override
    public boolean isDecryptService() {
        return true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(cancelReceiver);
    }

    private BroadcastReceiver cancelReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //cancel operation
            progressHandler.setCancelled(true);
        }
    };

}
