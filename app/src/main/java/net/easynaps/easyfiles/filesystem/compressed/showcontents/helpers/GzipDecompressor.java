package net.easynaps.easyfiles.filesystem.compressed.showcontents.helpers;

import android.content.Context;

import net.easynaps.easyfiles.adapters.data.CompressedObjectParcelable;
import net.easynaps.easyfiles.asynchronous.asynctasks.compress.CompressedHelperTask;
import net.easynaps.easyfiles.asynchronous.asynctasks.compress.GzipHelperTask;
import net.easynaps.easyfiles.filesystem.compressed.showcontents.Decompressor;
import net.easynaps.easyfiles.utils.OnAsyncTaskFinished;

import java.util.ArrayList;

public class GzipDecompressor extends Decompressor {

    public GzipDecompressor(Context context) {
        super(context);
    }

    @Override
    public CompressedHelperTask changePath(String path, boolean addGoBackItem,
                                           OnAsyncTaskFinished<ArrayList<CompressedObjectParcelable>> onFinish) {
        return new GzipHelperTask(filePath, path, addGoBackItem, onFinish);
    }

}
