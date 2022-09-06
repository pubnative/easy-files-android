package net.easynaps.easyfiles.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.util.ViewPreloadSizeProvider;

import net.easynaps.easyfiles.R;
import net.easynaps.easyfiles.activities.MainActivity;
import net.easynaps.easyfiles.activities.superclasses.BasicActivity;
import net.easynaps.easyfiles.activities.superclasses.ThemedActivity;
import net.easynaps.easyfiles.adapters.AppsAdapter;
import net.easynaps.easyfiles.adapters.glide.AppsAdapterPreloadModel;
import net.easynaps.easyfiles.asynchronous.loaders.AppListLoader;
import net.easynaps.easyfiles.utils.GlideConstants;
import net.easynaps.easyfiles.utils.Utils;
import net.easynaps.easyfiles.utils.provider.UtilitiesProvider;
import net.easynaps.easyfiles.utils.theme.AppTheme;

public class AppsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<AppListLoader.AppsDataPair> {

    UtilitiesProvider utilsProvider;
    AppsListFragment app = this;
    AppsAdapter adapter;

    public SharedPreferences Sp;
    ListView vl;
    int asc, sortby;

    int index = 0, top = 0;

    public static final int ID_LOADER_APP_LIST = 0;

    private static final String KEY_INDEX = "index";
    private static final String KEY_TOP = "top";

    private AppsAdapterPreloadModel modelProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utilsProvider = ((BasicActivity) getActivity()).getUtilsProvider();

        setHasOptionsMenu(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getAppbar().setTitle(R.string.apps);
        mainActivity.floatingActionButton.getMenuButton().hide();
        mainActivity.getAppbar().getBottomBar().setVisibility(View.GONE);
        mainActivity.supportInvalidateOptionsMenu();
        vl = getListView();
        Sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        getSortModes();
        ListView vl = getListView();
        vl.setDivider(null);
        if (utilsProvider.getAppTheme().equals(AppTheme.DARK))
            getActivity().getWindow().getDecorView().setBackgroundColor(Utils.getColor(getContext(), R.color.holo_dark_background));
        else if (utilsProvider.getAppTheme().equals(AppTheme.BLACK))
            getActivity().getWindow().getDecorView().setBackgroundColor(Utils.getColor(getContext(), android.R.color.black));

        modelProvider = new AppsAdapterPreloadModel(app);
        ViewPreloadSizeProvider<String> sizeProvider = new ViewPreloadSizeProvider<>();
        ListPreloader<String> preloader = new ListPreloader<>(GlideApp.with(app), modelProvider,
                sizeProvider, GlideConstants.MAX_PRELOAD_APPSADAPTER);

        adapter = new AppsAdapter(getContext(), (ThemedActivity) getActivity(), utilsProvider, modelProvider, sizeProvider,
                R.layout.rowlayout, app);

        getListView().setOnScrollListener(preloader);
        setListAdapter(adapter);
        setListShown(false);
        setEmptyText(getResources().getString(R.string.no_applications));
        getLoaderManager().initLoader(ID_LOADER_APP_LIST, null, this);

        if (savedInstanceState != null) {
            index = savedInstanceState.getInt(KEY_INDEX);
            top = savedInstanceState.getInt(KEY_TOP);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);

        if (vl != null) {
            int index = vl.getFirstVisiblePosition();
            View vi = vl.getChildAt(0);
            int top = (vi == null) ? 0 : vi.getTop();
            b.putInt(KEY_INDEX, index);
            b.putInt(KEY_TOP, top);
        }
    }

    public boolean unin(String pkg) {
        try {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + pkg));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Assigns sort modes
     * A value from 0 to 2 defines sort mode as name/last modified/size in ascending order
     * Values from 3 to 5 defines sort mode as name/last modified/size in descending order
     * <p>
     * Final value of {@link #sortby} varies from 0 to 2
     */
    public void getSortModes() {
        int t = Integer.parseInt(Sp.getString("sortbyApps", "0"));
        if (t <= 2) {
            sortby = t;
            asc = 1;
        } else if (t > 2) {
            asc = -1;
            sortby = t - 3;
        }
    }

    @Override
    public Loader<AppListLoader.AppsDataPair> onCreateLoader(int id, Bundle args) {
        return new AppListLoader(getContext(), sortby, asc);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<AppListLoader.AppsDataPair> loader, AppListLoader.AppsDataPair data) {
        // set new data to adapter
        adapter.setData(data.first);
        modelProvider.setItemList(data.second);

        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }

        if (vl != null)
            vl.setSelectionFromTop(index, top);
    }

    @Override
    public void onLoaderReset(Loader<AppListLoader.AppsDataPair> loader) {
        adapter.setData(null);
    }

}
