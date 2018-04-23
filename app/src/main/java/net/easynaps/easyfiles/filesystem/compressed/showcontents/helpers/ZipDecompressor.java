package net.easynaps.easyfiles.filesystem.compressed.showcontents.helpers;

import android.content.Context;

import net.easynaps.easyfiles.adapters.data.CompressedObjectParcelable;
import net.easynaps.easyfiles.asynchronous.asynctasks.compress.ZipHelperTask;
import net.easynaps.easyfiles.filesystem.compressed.showcontents.Decompressor;
import net.easynaps.easyfiles.utils.OnAsyncTaskFinished;

import java.util.ArrayList;

/**
 * @author Emmanuel
 *         on 20/11/2017, at 17:19.
 */

public class ZipDecompressor extends Decompressor {

    public ZipDecompressor(Context context) {
        super(context);
    }

    @Override
    public ZipHelperTask changePath(String path, boolean addGoBackItem,
                           OnAsyncTaskFinished<ArrayList<CompressedObjectParcelable>> onFinish) {
        return new ZipHelperTask(context, filePath, path, addGoBackItem, onFinish);
    }

}
