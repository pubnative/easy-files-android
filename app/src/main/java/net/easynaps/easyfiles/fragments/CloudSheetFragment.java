package net.easynaps.easyfiles.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import net.easynaps.easyfiles.R;
import net.easynaps.easyfiles.activities.MainActivity;
import net.easynaps.easyfiles.database.CloudContract;
import net.easynaps.easyfiles.ui.dialogs.SftpConnectDialog;
import net.easynaps.easyfiles.ui.dialogs.SmbSearchDialog;
import net.easynaps.easyfiles.utils.OpenMode;
import net.easynaps.easyfiles.utils.Utils;
import net.easynaps.easyfiles.utils.theme.AppTheme;

/**
 * Created by vishal on 18/2/17.
 *
 * Class represents implementation of a new cloud connection sheet dialog
 */

public class CloudSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private View rootView;
    private LinearLayout mSmbLayout, mScpLayout, mDropboxLayout, mBoxLayout, mGoogleDriveLayout, mOnedriveLayout
            , mGetCloudLayout;

    public static final String TAG_FRAGMENT = "cloud_fragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        rootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_sheet_cloud, null);

        if (((MainActivity) getActivity()).getAppTheme().equals(AppTheme.DARK)) {
            rootView.setBackgroundColor(Utils.getColor(getContext(), R.color.holo_dark_background));
        } else if (((MainActivity) getActivity()).getAppTheme().equals(AppTheme.BLACK)) {
            rootView.setBackgroundColor(Utils.getColor(getContext(), android.R.color.black));
        } else {
            rootView.setBackgroundColor(Utils.getColor(getContext(), android.R.color.white));
        }

        mSmbLayout = (LinearLayout) rootView.findViewById(R.id.linear_layout_smb);
        mScpLayout = (LinearLayout) rootView.findViewById(R.id.linear_layout_scp);
        mBoxLayout = (LinearLayout) rootView.findViewById(R.id.linear_layout_box);
        mDropboxLayout = (LinearLayout) rootView.findViewById(R.id.linear_layout_dropbox);
        mGoogleDriveLayout = (LinearLayout) rootView.findViewById(R.id.linear_layout_google_drive);
        mOnedriveLayout = (LinearLayout) rootView.findViewById(R.id.linear_layout_onedrive);
        mGetCloudLayout = (LinearLayout) rootView.findViewById(R.id.linear_layout_get_cloud);

        if (isCloudProviderAvailable(getContext())) {

            mBoxLayout.setVisibility(View.VISIBLE);
            mDropboxLayout.setVisibility(View.VISIBLE);
            mGoogleDriveLayout.setVisibility(View.VISIBLE);
            mOnedriveLayout.setVisibility(View.VISIBLE);
            mGetCloudLayout.setVisibility(View.GONE);
        }

        mSmbLayout.setOnClickListener(this);
        mScpLayout.setOnClickListener(this);
        mBoxLayout.setOnClickListener(this);
        mDropboxLayout.setOnClickListener(this);
        mGoogleDriveLayout.setOnClickListener(this);
        mOnedriveLayout.setOnClickListener(this);
        mGetCloudLayout.setOnClickListener(this);

        dialog.setContentView(rootView);
    }

    /**
     * Determines whether cloud provider is installed or not
     * @param context
     * @return
     */
    public static final boolean isCloudProviderAvailable(Context context) {

        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(CloudContract.APP_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onClick(View v) {

        Log.d(TAG_FRAGMENT, "Clicked: " + v.getId());

        switch (v.getId()) {
            case R.id.linear_layout_smb:
                dismiss();
                SmbSearchDialog smbDialog=new SmbSearchDialog();
                smbDialog.show(getActivity().getFragmentManager(), "tab");
                return;
            case R.id.linear_layout_scp:
                dismiss();
                SftpConnectDialog sftpConnectDialog = new SftpConnectDialog();
                Bundle args = new Bundle();
                args.putBoolean("edit", false);
                sftpConnectDialog.setArguments(args);
                sftpConnectDialog.show(getActivity().getFragmentManager(), "tab");
                return;
            case R.id.linear_layout_box:
                ((MainActivity) getActivity()).addConnection(OpenMode.BOX);
                break;
            case R.id.linear_layout_dropbox:
                ((MainActivity) getActivity()).addConnection(OpenMode.DROPBOX);
                break;
            case R.id.linear_layout_google_drive:
                ((MainActivity) getActivity()).addConnection(OpenMode.GDRIVE);
                break;
            case R.id.linear_layout_onedrive:
                ((MainActivity) getActivity()).addConnection(OpenMode.ONEDRIVE);
                break;
            case R.id.linear_layout_get_cloud:
                Intent cloudPluginIntent = new Intent(Intent.ACTION_VIEW);
                cloudPluginIntent.setData(Uri.parse("market://details?id=net.pubnative.easyfilescloud"));
                startActivity(cloudPluginIntent);
                break;
        }

        // dismiss this sheet dialog
        dismiss();
    }

    public interface CloudConnectionCallbacks {
        void addConnection(OpenMode service);
        void deleteConnection(OpenMode service);
    }
}
