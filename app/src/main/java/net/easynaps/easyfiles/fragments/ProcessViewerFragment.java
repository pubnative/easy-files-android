package net.easynaps.easyfiles.fragments;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.CardView;
import android.text.Html;
import android.text.Spanned;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import net.easynaps.easyfiles.R;
import net.easynaps.easyfiles.activities.MainActivity;
import net.easynaps.easyfiles.asynchronous.services.AbstractProgressiveService;
import net.easynaps.easyfiles.asynchronous.services.CopyService;
import net.easynaps.easyfiles.asynchronous.services.DecryptService;
import net.easynaps.easyfiles.asynchronous.services.EncryptService;
import net.easynaps.easyfiles.asynchronous.services.ExtractService;
import net.easynaps.easyfiles.asynchronous.services.ZipService;
import net.easynaps.easyfiles.utils.DatapointParcelable;
import net.easynaps.easyfiles.utils.ObtainableServiceBinder;
import net.easynaps.easyfiles.utils.Utils;
import net.easynaps.easyfiles.utils.color.ColorUsage;
import net.easynaps.easyfiles.utils.files.FileUtils;
import net.easynaps.easyfiles.utils.theme.AppTheme;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ProcessViewerFragment extends Fragment {

    /**
     * Helps defining the result type for {@link #processResults(DatapointParcelable, int)}
     * to process
     */
    private static final int SERVICE_COPY = 0, SERVICE_EXTRACT = 1, SERVICE_COMPRESS = 2,
            SERVICE_ENCRYPT = 3, SERVICE_DECRYPT = 4;

    private boolean isInitialized = false;
    private MainActivity mainActivity;
    private int accentColor, primaryColor;
    private ImageButton mCancelButton;
    private ImageView mProgressImage;
    private View rootView;
    private CardView mCardView;
    private LineChart mLineChart;
    private LineData mLineData = new LineData();
    private long time = 0L;
    private TextView mProgressTypeText, mProgressFileNameText,
            mProgressBytesText, mProgressFileText,  mProgressSpeedText, mProgressTimer;

    private ServiceConnection mCopyConnection, mExtractConnection, mCompressConnection,
            mEncryptConnection, mDecryptConnection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.processparent, container, false);
        setRetainInstance(false);

        mainActivity = (MainActivity) getActivity();

        accentColor = mainActivity.getColorPreference().getColor(ColorUsage.ACCENT);
        primaryColor = mainActivity.getColorPreference().getColor(ColorUsage.getPrimary(MainActivity.currentTab));
        if (mainActivity.getAppTheme().equals(AppTheme.DARK) || mainActivity.getAppTheme().equals(AppTheme.BLACK))
            rootView.setBackgroundResource((R.color.cardView_background));
        mainActivity.updateViews(new ColorDrawable(primaryColor));
        mainActivity.getAppbar().setTitle(R.string.process_viewer);
        mainActivity.floatingActionButton.getMenuButton().hide();
        
        mainActivity.supportInvalidateOptionsMenu();

        mCardView = (CardView) rootView.findViewById(R.id.card_view);

        mLineChart = (LineChart) rootView.findViewById(R.id.progress_chart);
        mProgressImage = (ImageView) rootView.findViewById(R.id.progress_image);
        mCancelButton = (ImageButton) rootView.findViewById(R.id.delete_button);
        mProgressTypeText = (TextView) rootView.findViewById(R.id.text_view_progress_type);
        mProgressFileNameText = (TextView) rootView.findViewById(R.id.text_view_progress_file_name);
        mProgressBytesText = (TextView) rootView.findViewById(R.id.text_view_progress_bytes);
        mProgressFileText = (TextView) rootView.findViewById(R.id.text_view_progress_file);
        mProgressSpeedText = (TextView) rootView.findViewById(R.id.text_view_progress_speed);
        mProgressTimer = (TextView) rootView.findViewById(R.id.text_view_progress_timer);

        if (mainActivity.getAppTheme().equals(AppTheme.DARK) || mainActivity.getAppTheme().equals(AppTheme.BLACK)) {

            mCancelButton.setImageResource(R.drawable.ic_action_cancel);
            mCardView.setCardBackgroundColor(Utils.getColor(getContext(), R.color.cardView_foreground));
            mCardView.setCardElevation(0f);
        }

        mCopyConnection = new CustomServiceConnection(this, mLineChart, SERVICE_COPY);
        mExtractConnection = new CustomServiceConnection(this, mLineChart, SERVICE_EXTRACT);
        mCompressConnection = new CustomServiceConnection(this, mLineChart, SERVICE_COMPRESS);
        mEncryptConnection = new CustomServiceConnection(this, mLineChart, SERVICE_ENCRYPT);
        mDecryptConnection = new CustomServiceConnection(this, mLineChart, SERVICE_DECRYPT);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = new Intent(getActivity(), CopyService.class);
        getActivity().bindService(intent, mCopyConnection, 0);

        Intent intent1 = new Intent(getActivity(), ExtractService.class);
        getActivity().bindService(intent1, mExtractConnection, 0);

        Intent intent2 = new Intent(getActivity(), ZipService.class);
        getActivity().bindService(intent2, mCompressConnection, 0);

        Intent intent3 = new Intent(getActivity(), EncryptService.class);
        getActivity().bindService(intent3, mEncryptConnection, 0);

        Intent intent4 = new Intent(getActivity(), DecryptService.class);
        getActivity().bindService(intent4, mDecryptConnection, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unbindService(mCopyConnection);
        getActivity().unbindService(mExtractConnection);
        getActivity().unbindService(mCompressConnection);
        getActivity().unbindService(mEncryptConnection);
        getActivity().unbindService(mDecryptConnection);
    }

    public void processResults(final DatapointParcelable dataPackage, int serviceType) {
        if (dataPackage != null) {
            String name = dataPackage.name;
            long total = dataPackage.totalSize;
            long doneBytes = dataPackage.byteProgress;
            boolean move = dataPackage.move;

            if (!isInitialized) {

                // initializing views for the first time
                chartInit(total);

                // setting progress image
                setupDrawables(serviceType, move);
                isInitialized = true;
            }

            addEntry(FileUtils.readableFileSizeFloat(doneBytes),
                    FileUtils.readableFileSizeFloat(dataPackage.speedRaw));

            mProgressFileNameText.setText(name);

            Spanned bytesText = Html.fromHtml(getResources().getString(R.string.written)
                    + " <font color='" + accentColor + "'><i>" + Formatter.formatFileSize(getContext(), doneBytes)
                    + " </font></i>" + getResources().getString(R.string.out_of) + " <i>"
                    + Formatter.formatFileSize(getContext(), total) + "</i>");
            mProgressBytesText.setText(bytesText);

            Spanned fileProcessedSpan = Html.fromHtml(getResources().getString(R.string.processing_file)
                    + " <font color='" + accentColor + "'><i>" + (dataPackage.sourceProgress)
                    + " </font></i>" + getResources().getString(R.string.of) + " <i>"
                    + dataPackage.sourceFiles + "</i>");
            mProgressFileText.setText(fileProcessedSpan);

            Spanned speedSpan = Html.fromHtml(getResources().getString(R.string.current_speed)
                    + ": <font color='" + accentColor + "'><i>"
                    + Formatter.formatFileSize(getContext(), dataPackage.speedRaw)
                    + "/s</font></i>");
            mProgressSpeedText.setText(speedSpan);

            Spanned timerSpan = Html.fromHtml(getResources().getString(R.string.service_timer)
                    + ": <font color='" + accentColor + "'><i>"
                    + formatTimer(++time)
                    + "</font></i>");

            mProgressTimer.setText(timerSpan);

            if(dataPackage.completed) mCancelButton.setVisibility(View.GONE);
        }
    }

    /**
     * Formats input to plain mm:ss format
     * @param timer
     * @return
     */
    private String formatTimer(long timer) {
        final long min = TimeUnit.SECONDS.toMinutes(timer);
        final long sec = TimeUnit.SECONDS.toSeconds(timer - TimeUnit.MINUTES.toMillis(min));
        return String.format("%02d:%02d", min, sec);
    }

    /**
     * Setup drawables and click listeners based on the SERVICE_* constants
     */
    private void setupDrawables(int serviceType, boolean isMove) {
        switch (serviceType) {
            case SERVICE_COPY:
                if (mainActivity.getAppTheme().equals(AppTheme.DARK) || mainActivity.getAppTheme().equals(AppTheme.BLACK)) {

                    mProgressImage.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_content_copy_white_36dp));
                } else {
                    mProgressImage.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_content_copy_grey600_36dp));
                }
                mProgressTypeText.setText(isMove ? getResources().getString(R.string.moving)
                        : getResources().getString(R.string.copying));
                cancelBroadcast(new Intent(CopyService.TAG_BROADCAST_COPY_CANCEL));
                break;
            case SERVICE_EXTRACT:
                if (mainActivity.getAppTheme().equals(AppTheme.DARK) || mainActivity.getAppTheme().equals(AppTheme.BLACK)) {

                    mProgressImage.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_zip_box_white_36dp));
                } else {
                    mProgressImage.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_zip_box_grey600_36dp));
                }
                mProgressTypeText.setText(getResources().getString(R.string.extracting));
                cancelBroadcast(new Intent(ExtractService.TAG_BROADCAST_EXTRACT_CANCEL));
                break;
            case SERVICE_COMPRESS:
                if (mainActivity.getAppTheme().equals(AppTheme.DARK) || mainActivity.getAppTheme().equals(AppTheme.BLACK)) {

                    mProgressImage.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_zip_box_white_36dp));
                } else {
                    mProgressImage.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_zip_box_grey600_36dp));
                }
                mProgressTypeText.setText(getResources().getString(R.string.compressing));
                cancelBroadcast(new Intent(ZipService.KEY_COMPRESS_BROADCAST_CANCEL));
                break;
            case SERVICE_ENCRYPT:
                if (mainActivity.getAppTheme().equals(AppTheme.DARK) || mainActivity.getAppTheme().equals(AppTheme.BLACK)) {

                    mProgressImage.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_folder_lock_white_36dp));
                } else {
                    mProgressImage.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_folder_lock_grey600_36dp));
                }
                mProgressTypeText.setText(getResources().getString(R.string.crypt_encrypting));
                cancelBroadcast(new Intent(EncryptService.TAG_BROADCAST_CRYPT_CANCEL));
                break;
            case SERVICE_DECRYPT:
                if (mainActivity.getAppTheme().equals(AppTheme.DARK) || mainActivity.getAppTheme().equals(AppTheme.BLACK)) {

                    mProgressImage.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_folder_lock_open_white_36dp));
                } else {
                    mProgressImage.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_folder_lock_open_grey600_36dp));
                }
                mProgressTypeText.setText(getResources().getString(R.string.crypt_decrypting));
                cancelBroadcast(new Intent(EncryptService.TAG_BROADCAST_CRYPT_CANCEL));
                break;
        }
    }

    /**
     * Setup click listener to cancel button click for various intent types
     * @param intent
     */
    private void cancelBroadcast(final Intent intent) {

        mCancelButton.setOnClickListener(v -> {
            Toast.makeText(getActivity(),
                    getResources().getString(R.string.stopping), Toast.LENGTH_LONG).show();
            getActivity().sendBroadcast(intent);
            mProgressTypeText.setText(getResources().getString(R.string.cancelled));
            mProgressSpeedText.setText("");
            mProgressFileText.setText("");
            mProgressBytesText.setText("");
            mProgressFileNameText.setText("");

            mProgressTypeText.setTextColor(Utils.getColor(getContext(), android.R.color.holo_red_light));
        });
    }

    /**
     * Add a new entry dynamically to the chart, initializes a {@link LineDataSet} if not done so
     * @param xValue the x-axis value, the number of bytes processed till now
     * @param yValue the y-axis value, bytes processed per sec
     */
    private void addEntry(float xValue, float yValue) {
        ILineDataSet dataSet = mLineData.getDataSetByIndex(0);

        if (dataSet == null) {// adding set for first time
            dataSet = createDataSet();
            mLineData.addDataSet(dataSet);
        }

        dataSet.addEntry(new Entry(xValue, yValue));

        mLineData.notifyDataChanged();
        mLineChart.notifyDataSetChanged();
        mLineChart.invalidate();
    }

    /**
     * Creates an instance for {@link LineDataSet} which will store the entries
     * @return
     */
    private LineDataSet createDataSet() {
        LineDataSet lineDataset = new LineDataSet(new ArrayList<Entry>(), null);

        lineDataset.setLineWidth(1.75f);
        lineDataset.setCircleRadius(5f);
        lineDataset.setCircleHoleRadius(2.5f);
        lineDataset.setColor(Color.WHITE);
        lineDataset.setCircleColor(Color.WHITE);
        lineDataset.setHighLightColor(Color.WHITE);
        lineDataset.setDrawValues(false);
        lineDataset.setCircleColorHole(accentColor);

        return lineDataset;
    }

    /**
     * Initialize chart for the first time
     * @param totalBytes maximum value for x-axis
     */
    private void chartInit(long totalBytes) {
        mLineChart.setBackgroundColor(accentColor);
        mLineChart.getLegend().setEnabled(false);

        // no description text
        mLineChart.getDescription().setEnabled(false);

        XAxis xAxis = mLineChart.getXAxis();
        YAxis yAxisLeft = mLineChart.getAxisLeft();
        mLineChart.getAxisRight().setEnabled(false);
        yAxisLeft.setTextColor(Color.WHITE);
        yAxisLeft.setAxisLineColor(Color.TRANSPARENT);
        yAxisLeft.setTypeface(Typeface.DEFAULT_BOLD);
        yAxisLeft.setGridColor(Utils.getColor(getContext(), R.color.white_translucent));

        xAxis.setAxisMaximum(FileUtils.readableFileSizeFloat(totalBytes));
        xAxis.setAxisMinimum(0.0f);
        xAxis.setAxisLineColor(Color.TRANSPARENT);
        xAxis.setGridColor(Color.TRANSPARENT);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTypeface(Typeface.DEFAULT_BOLD);
        mLineChart.setData(mLineData);
        mLineChart.invalidate();
    }

    private static class CustomServiceConnection implements ServiceConnection {

        private ProcessViewerFragment fragment;
        private LineChart lineChart;
        private int serviceType;

        public CustomServiceConnection(ProcessViewerFragment frag, LineChart lineChart, int serviceType) {
            fragment = frag;
            this.lineChart = lineChart;
            this.serviceType = serviceType;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ObtainableServiceBinder<? extends AbstractProgressiveService> binder =
                    (ObtainableServiceBinder<? extends AbstractProgressiveService>) service;
            AbstractProgressiveService specificService = binder.getService();

            for (int i = 0; i < specificService.getDataPackageSize(); i++) {
                DatapointParcelable dataPackage = specificService.getDataPackage(i);
                fragment.processResults(dataPackage, serviceType);
            }

            // animate the chart a little after initial values have been applied
            lineChart.animateXY(500, 500);

            specificService.setProgressListener(new AbstractProgressiveService.ProgressListener() {
                @Override
                public void onUpdate(final DatapointParcelable dataPackage) {
                    if (fragment.getActivity() == null) {
                        // callback called when we're not inside the app
                        return;
                    }
                    fragment.getActivity().runOnUiThread(() -> fragment.processResults(dataPackage, serviceType));
                }

                @Override
                public void refresh() {}
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {}
    }

}
