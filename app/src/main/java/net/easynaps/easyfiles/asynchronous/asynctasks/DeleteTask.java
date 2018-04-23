package net.easynaps.easyfiles.asynchronous.asynctasks;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.provider.DocumentFile;
import android.widget.Toast;

import com.cloudrail.si.interfaces.CloudStorage;

import net.easynaps.easyfiles.R;
import net.easynaps.easyfiles.activities.MainActivity;
import net.easynaps.easyfiles.database.CryptHandler;
import net.easynaps.easyfiles.exceptions.ShellNotRunningException;
import net.easynaps.easyfiles.filesystem.HybridFileParcelable;
import net.easynaps.easyfiles.fragments.CompressedExplorerFragment;
import net.easynaps.easyfiles.fragments.preference_fragments.PreferencesConstants;
import net.easynaps.easyfiles.ui.notifications.NotificationConstants;
import net.easynaps.easyfiles.utils.DataUtils;
import net.easynaps.easyfiles.utils.OTGUtil;
import net.easynaps.easyfiles.utils.OpenMode;
import net.easynaps.easyfiles.utils.cloud.CloudUtil;
import net.easynaps.easyfiles.utils.files.CryptUtil;
import net.easynaps.easyfiles.utils.files.FileUtils;

import java.util.ArrayList;

public class DeleteTask extends AsyncTask<ArrayList<HybridFileParcelable>, String, Boolean> {

    private ArrayList<HybridFileParcelable> files;
    private Context cd;
    private boolean rootMode;
    private CompressedExplorerFragment compressedExplorerFragment;
    private DataUtils dataUtils = DataUtils.getInstance();

    public DeleteTask(ContentResolver c, Context cd) {
        this.cd = cd;
        rootMode = PreferenceManager.getDefaultSharedPreferences(cd).getBoolean(PreferencesConstants.PREFERENCE_ROOTMODE, false);
    }

    public DeleteTask(ContentResolver c, Context cd, CompressedExplorerFragment compressedExplorerFragment) {
        this.cd = cd;
        rootMode = PreferenceManager.getDefaultSharedPreferences(cd).getBoolean(PreferencesConstants.PREFERENCE_ROOTMODE, false);
        this.compressedExplorerFragment = compressedExplorerFragment;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Toast.makeText(cd, values[0], Toast.LENGTH_SHORT).show();
    }

    protected Boolean doInBackground(ArrayList<HybridFileParcelable>... p1) {
        files = p1[0];
        boolean wasDeleted = true;
        if(files.size()==0)return true;

        if (files.get(0).isOtgFile()) {
            for (HybridFileParcelable file : files) {
                DocumentFile documentFile = OTGUtil.getDocumentFile(file.getPath(), cd, false);
                wasDeleted = documentFile.delete();
            }
        } else if (files.get(0).isDropBoxFile()) {
            CloudStorage cloudStorageDropbox = dataUtils.getAccount(OpenMode.DROPBOX);
            for (HybridFileParcelable baseFile : files) {
                try {
                    cloudStorageDropbox.delete(CloudUtil.stripPath(OpenMode.DROPBOX, baseFile.getPath()));
                } catch (Exception e) {
                    e.printStackTrace();
                    wasDeleted = false;
                    break;
                }
            }
        } else if (files.get(0).isBoxFile()) {
            CloudStorage cloudStorageBox = dataUtils.getAccount(OpenMode.BOX);
            for (HybridFileParcelable baseFile : files) {
                try {
                    cloudStorageBox.delete(CloudUtil.stripPath(OpenMode.BOX, baseFile.getPath()));
                } catch (Exception e) {
                    e.printStackTrace();
                    wasDeleted = false;
                    break;
                }
            }
        } else if (files.get(0).isGoogleDriveFile()) {
            CloudStorage cloudStorageGdrive = dataUtils.getAccount(OpenMode.GDRIVE);
            for (HybridFileParcelable baseFile : files) {
                try {
                    cloudStorageGdrive.delete(CloudUtil.stripPath(OpenMode.GDRIVE, baseFile.getPath()));
                } catch (Exception e) {
                    e.printStackTrace();
                    wasDeleted = false;
                    break;
                }
            }
        } else if (files.get(0).isOneDriveFile()) {
            CloudStorage cloudStorageOnedrive = dataUtils.getAccount(OpenMode.ONEDRIVE);
            for (HybridFileParcelable baseFile : files) {
                try {
                    cloudStorageOnedrive.delete(CloudUtil.stripPath(OpenMode.ONEDRIVE, baseFile.getPath()));
                } catch (Exception e) {
                    e.printStackTrace();
                    wasDeleted = false;
                    break;
                }
            }
        } else {
            for(HybridFileParcelable file : files) {
                try {
                    if (file.delete(cd, rootMode)) {
                        wasDeleted = true;
                    } else {
                        wasDeleted = false;
                        break;
                    }
                } catch (ShellNotRunningException e) {
                    e.printStackTrace();
                    wasDeleted = false;
                    break;
                }
            }
        }

        // delete file from media database
        if(!files.get(0).isSmb()) {
            try {
                for (HybridFileParcelable f : files) {
                    delete(cd,f.getPath());
                }
            } catch (Exception e) {
                for (HybridFileParcelable f : files) {
                    FileUtils.scanFile(f.getPath(), cd);
                }
            }
        }

        // delete file entry from encrypted database
        for (HybridFileParcelable file : files) {
            if (file.getName().endsWith(CryptUtil.CRYPT_EXTENSION)) {
                CryptHandler handler = new CryptHandler(cd);
                handler.clear(file.getPath());
            }
        }

        return wasDeleted;
    }

    @Override
    public void onPostExecute(Boolean wasDeleted) {

        Intent intent = new Intent(MainActivity.KEY_INTENT_LOAD_LIST);
        String path = files.get(0).getParent(cd);
        intent.putExtra(MainActivity.KEY_INTENT_LOAD_LIST_FILE, path);
        cd.sendBroadcast(intent);

        if (!wasDeleted) {
            Toast.makeText(cd, cd.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
        } else if (compressedExplorerFragment == null) {
            Toast.makeText(cd, cd.getResources().getString(R.string.done), Toast.LENGTH_SHORT).show();
        }

        if (compressedExplorerFragment!=null) {
            compressedExplorerFragment.files.clear();
        }

        // cancel any processing notification because of cut/paste operation
        NotificationManager notificationManager = (NotificationManager) cd.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NotificationConstants.COPY_ID);
    }

    private void delete(final Context context, final String file) {
        final String where = MediaStore.MediaColumns.DATA + "=?";
        final String[] selectionArgs = new String[] {
                file
        };
        final ContentResolver contentResolver = context.getContentResolver();
        final Uri filesUri = MediaStore.Files.getContentUri("external");
        // Delete the entry from the media database. This will actually delete media files.
        contentResolver.delete(filesUri, where, selectionArgs);
    }
}



