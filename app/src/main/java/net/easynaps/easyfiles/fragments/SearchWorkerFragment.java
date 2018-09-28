package net.easynaps.easyfiles.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import net.easynaps.easyfiles.asynchronous.asynctasks.SearchAsyncTask;
import net.easynaps.easyfiles.filesystem.HybridFileParcelable;
import net.easynaps.easyfiles.utils.OpenMode;

public class SearchWorkerFragment extends Fragment {

    public static final String KEY_PATH = "path";
    public static final String KEY_INPUT = "input";
    public static final String KEY_OPEN_MODE = "open_mode";
    public static final String KEY_ROOT_MODE = "root_mode";
    public static final String KEY_REGEX = "regex";
    public static final String KEY_REGEX_MATCHES = "matches";

    public SearchAsyncTask mSearchAsyncTask;

    private static final String TAG = "SearchWorkerFragment";

    private HelperCallbacks mCallbacks;

    // interface for activity to communicate with asynctask
    public interface HelperCallbacks {
        void onPreExecute(String query);
        void onPostExecute(String query);
        void onProgressUpdate(HybridFileParcelable val, String query);
        void onCancelled();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // hold instance of activity as there is a change in device configuration
        mCallbacks = (HelperCallbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        String mPath = getArguments().getString(KEY_PATH);
        String mInput = getArguments().getString(KEY_INPUT);
        OpenMode mOpenMode = OpenMode.getOpenMode(getArguments().getInt(KEY_OPEN_MODE));
        boolean mRootMode = getArguments().getBoolean(KEY_ROOT_MODE);
        boolean isRegexEnabled = getArguments().getBoolean(KEY_REGEX);
        boolean isMatchesEnabled = getArguments().getBoolean(KEY_REGEX_MATCHES);

        mSearchAsyncTask = new SearchAsyncTask(getActivity(), mCallbacks, mInput, mOpenMode,
                mRootMode, isRegexEnabled, isMatchesEnabled);
        mSearchAsyncTask.execute(mPath);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // to avoid activity instance leak while changing activity configurations
        mCallbacks = null;
    }

}
