package net.easynaps.easyfiles.filesystem.compressed.showcontents.helpers;

import android.content.Context;

import net.easynaps.easyfiles.adapters.data.CompressedObjectParcelable;
import net.easynaps.easyfiles.asynchronous.asynctasks.compress.TarHelperTask;
import net.easynaps.easyfiles.filesystem.compressed.showcontents.Decompressor;
import net.easynaps.easyfiles.utils.OnAsyncTaskFinished;

import java.util.ArrayList;

/**
 * @author Emmanuel Messulam <emmanuelbendavid@gmail.com>
 *         on 2/12/2017, at 00:36.
 */

public class TarDecompressor extends Decompressor {

    public TarDecompressor(Context context) {
        super(context);
    }

    @Override
    public TarHelperTask changePath(String path, boolean addGoBackItem, OnAsyncTaskFinished<ArrayList<CompressedObjectParcelable>> onFinish) {
        return new TarHelperTask(filePath, path, addGoBackItem, onFinish);
    }

}
