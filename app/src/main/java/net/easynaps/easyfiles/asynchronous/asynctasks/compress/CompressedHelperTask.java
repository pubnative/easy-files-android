package net.easynaps.easyfiles.asynchronous.asynctasks.compress;

import android.os.AsyncTask;

import net.easynaps.easyfiles.adapters.data.CompressedObjectParcelable;
import net.easynaps.easyfiles.utils.OnAsyncTaskFinished;

import java.util.ArrayList;
import java.util.Collections;

public abstract class CompressedHelperTask extends AsyncTask<Void, Void, ArrayList<CompressedObjectParcelable>> {

    private boolean createBackItem;
    private OnAsyncTaskFinished<ArrayList<CompressedObjectParcelable>> onFinish;

    CompressedHelperTask(boolean goBack,
                         OnAsyncTaskFinished<ArrayList<CompressedObjectParcelable>> l) {
        createBackItem = goBack;
        onFinish = l;
    }

    @Override
    protected final ArrayList<CompressedObjectParcelable> doInBackground(Void... voids) {
        ArrayList<CompressedObjectParcelable> elements = new ArrayList<>();
        if (createBackItem) elements.add(0, new CompressedObjectParcelable());

        addElements(elements);

        Collections.sort(elements, new CompressedObjectParcelable.Sorter());

        return elements;
    }

    @Override
    protected final void onPostExecute(ArrayList<CompressedObjectParcelable> zipEntries) {
        super.onPostExecute(zipEntries);
        onFinish.onAsyncTaskFinished(zipEntries);
    }

    abstract void addElements(ArrayList<CompressedObjectParcelable> elements);

}
