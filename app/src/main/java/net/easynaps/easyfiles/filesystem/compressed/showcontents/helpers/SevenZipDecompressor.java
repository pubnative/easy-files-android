package net.easynaps.easyfiles.filesystem.compressed.showcontents.helpers;

import android.content.Context;

import net.easynaps.easyfiles.adapters.data.CompressedObjectParcelable;
import net.easynaps.easyfiles.asynchronous.asynctasks.compress.SevenZipHelperTask;
import net.easynaps.easyfiles.filesystem.compressed.showcontents.Decompressor;
import net.easynaps.easyfiles.utils.OnAsyncTaskFinished;

import java.util.ArrayList;

public class SevenZipDecompressor extends Decompressor {
    public SevenZipDecompressor(Context context) {
        super(context);
    }

    @Override
    public SevenZipHelperTask changePath(String path, boolean addGoBackItem,
                                         OnAsyncTaskFinished<ArrayList<CompressedObjectParcelable>> onFinish) {
        return new SevenZipHelperTask(filePath, path, addGoBackItem, onFinish);
    }
}
