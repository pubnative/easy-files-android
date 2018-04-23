package net.easynaps.easyfiles.asynchronous.asynctasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.cloudrail.si.interfaces.CloudStorage;

import net.easynaps.easyfiles.activities.MainActivity;
import net.easynaps.easyfiles.asynchronous.services.CopyService;
import net.easynaps.easyfiles.database.CryptHandler;
import net.easynaps.easyfiles.database.models.EncryptedEntry;
import net.easynaps.easyfiles.exceptions.ShellNotRunningException;
import net.easynaps.easyfiles.filesystem.HybridFileParcelable;
import net.easynaps.easyfiles.fragments.MainFragment;
import net.easynaps.easyfiles.utils.DataUtils;
import net.easynaps.easyfiles.utils.OpenMode;
import net.easynaps.easyfiles.utils.RootUtils;
import net.easynaps.easyfiles.utils.ServiceWatcherUtil;
import net.easynaps.easyfiles.utils.application.AppConfig;
import net.easynaps.easyfiles.utils.cloud.CloudUtil;
import net.easynaps.easyfiles.utils.files.CryptUtil;
import net.easynaps.easyfiles.utils.files.FileUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class MoveFiles extends AsyncTask<ArrayList<String>, Void, Boolean> {

    private ArrayList<ArrayList<HybridFileParcelable>> files;
    private MainFragment mainFrag;
    private ArrayList<String> paths;
    private Context context;
    private OpenMode mode;

    public MoveFiles(ArrayList<ArrayList<HybridFileParcelable>> files, MainFragment ma, Context context, OpenMode mode) {
        mainFrag = ma;
        this.context = context;
        this.files = files;
        this.mode = mode;
    }

    @Override
    protected Boolean doInBackground(ArrayList<String>... strings) {
        paths = strings[0];

        if (files.size() == 0) return true;

        switch (mode) {
            case SMB:
                for (int i = 0; i < paths.size(); i++) {
                    for (HybridFileParcelable f : files.get(i)) {
                        try {
                            SmbFile source = new SmbFile(f.getPath());
                            SmbFile dest = new SmbFile(paths.get(i) + "/" + f.getName());
                            source.renameTo(dest);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            return false;
                        } catch (SmbException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                }
                break;
            case FILE:
                for (int i = 0; i < paths.size(); i++) {
                    for (HybridFileParcelable f : files.get(i)) {
                        File dest = new File(paths.get(i) + "/" + f.getName());
                        File source = new File(f.getPath());
                        if (!source.renameTo(dest)) {

                            // check if we have root
                            if (mainFrag.getMainActivity().isRootExplorer()) {
                                try {
                                    if (!RootUtils.rename(f.getPath(), paths.get(i) + "/" + f.getName()))
                                        return false;
                                } catch (ShellNotRunningException e) {
                                    e.printStackTrace();
                                    return false;
                                }
                            } else return false;
                        }
                    }
                }
                break;
            case DROPBOX:
            case BOX:
            case ONEDRIVE:
            case GDRIVE:
                for (int i=0; i<paths.size(); i++) {
                    for (HybridFileParcelable baseFile : files.get(i)) {

                        DataUtils dataUtils = DataUtils.getInstance();

                        CloudStorage cloudStorage = dataUtils.getAccount(mode);
                        String targetPath = paths.get(i) + "/" + baseFile.getName();
                        if (baseFile.getMode() == mode) {
                            // source and target both in same filesystem, use API method
                            try {

                                cloudStorage.move(CloudUtil.stripPath(mode, baseFile.getPath()),
                                        CloudUtil.stripPath(mode, targetPath));
                            } catch (Exception e) {
                                return false;
                            }
                        }  else {
                            // not in same filesystem, execute service
                            return false;
                        }
                    }
                }
            default:
                return false;
        }

        return true;
    }

    @Override
    public void onPostExecute(Boolean movedCorrectly) {
        if (movedCorrectly) {
            if (mainFrag != null && mainFrag.getCurrentPath().equals(paths.get(0))) {
                // mainFrag.updateList();
                Intent intent = new Intent(MainActivity.KEY_INTENT_LOAD_LIST);

                intent.putExtra(MainActivity.KEY_INTENT_LOAD_LIST_FILE, paths.get(0));
                context.sendBroadcast(intent);
            }

            for (int i = 0; i < paths.size(); i++) {
                for (HybridFileParcelable f : files.get(i)) {
                    FileUtils.scanFile(f.getPath(), context);
                    FileUtils.scanFile(paths.get(i) + "/" + f.getName(), context);
                }
            }

            // updating encrypted db entry if any encrypted file was moved
            AppConfig.runInBackground(() -> {
                for (int i = 0; i < paths.size(); i++) {
                    for (HybridFileParcelable file : files.get(i)) {
                        if (file.getName().endsWith(CryptUtil.CRYPT_EXTENSION)) {
                            try {

                                CryptHandler cryptHandler = new CryptHandler(context);
                                EncryptedEntry oldEntry = cryptHandler.findEntry(file.getPath());
                                EncryptedEntry newEntry = new EncryptedEntry();
                                newEntry.setId(oldEntry.getId());
                                newEntry.setPassword(oldEntry.getPassword());
                                newEntry.setPath(paths.get(i) + "/" + file.getName());
                                cryptHandler.updateEntry(oldEntry, newEntry);
                            } catch (Exception e) {
                                e.printStackTrace();
                                // couldn't change the entry, leave it alone
                            }
                        }
                    }
                }
            });

        } else {
            for (int i = 0; i < paths.size(); i++) {
                Intent intent = new Intent(context, CopyService.class);
                intent.putExtra(CopyService.TAG_COPY_SOURCES, files.get(i));
                intent.putExtra(CopyService.TAG_COPY_TARGET, paths.get(i));
                intent.putExtra(CopyService.TAG_COPY_MOVE, true);
                intent.putExtra(CopyService.TAG_COPY_OPEN_MODE, mode.ordinal());

                ServiceWatcherUtil.runService(context, intent);
            }
        }
    }
}
