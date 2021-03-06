package net.easynaps.easyfiles.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import net.easynaps.easyfiles.R;
import net.easynaps.easyfiles.activities.MainActivity;
import net.easynaps.easyfiles.activities.superclasses.BasicActivity;
import net.easynaps.easyfiles.activities.superclasses.ThemedActivity;
import net.easynaps.easyfiles.utils.ComputerParcelable;
import net.easynaps.easyfiles.utils.SubnetScanner;
import net.easynaps.easyfiles.utils.color.ColorUsage;
import net.easynaps.easyfiles.utils.provider.UtilitiesProvider;
import net.easynaps.easyfiles.utils.theme.AppTheme;

import java.util.ArrayList;
import java.util.List;

public class SmbSearchDialog extends DialogFragment {
    private UtilitiesProvider utilsProvider;

    private ListViewAdapter listViewAdapter;
    private ArrayList<ComputerParcelable> computers = new ArrayList<>();
    private int accentColor;
    private SubnetScanner subnetScanner;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        utilsProvider = ((BasicActivity) getActivity()).getUtilsProvider();

        accentColor = ((ThemedActivity) getActivity()).getColorPreference().getColor(ColorUsage.ACCENT);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (subnetScanner != null)
            subnetScanner.interrupt();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title(R.string.searchingdevices);
        builder.negativeColor(accentColor);
        builder.negativeText(R.string.cancel);
        builder.onNegative((dialog, which) -> {
            if (subnetScanner != null)
                subnetScanner.interrupt();
            dismiss();
        });
        builder.onPositive((dialog, which) -> {
            if (subnetScanner != null)
                subnetScanner.interrupt();
            if (getActivity() != null && getActivity() instanceof MainActivity) {
                dismiss();
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.showSMBDialog("", "", false);
            }
        });
        builder.positiveText(R.string.use_custom_ip);
        builder.positiveColor(accentColor);
        computers.add(new ComputerParcelable("-1", "-1"));
        listViewAdapter = new ListViewAdapter(getActivity(), computers);
        subnetScanner = new SubnetScanner(getActivity());
        subnetScanner.setObserver(new SubnetScanner.ScanObserver() {
            @Override
            public void computerFound(final ComputerParcelable computer) {
                if (getActivity() != null)
                    getActivity().runOnUiThread(() -> {
                        if (!computers.contains(computer))
                            computers.add(computer);
                        listViewAdapter.notifyDataSetChanged();
                    });
            }

            @Override
            public void searchFinished() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (computers.size() == 1) {
                            dismiss();
                            Toast.makeText(getActivity(), getResources().getString(R.string.nodevicefound), Toast.LENGTH_SHORT).show();
                            MainActivity mainActivity = (MainActivity) getActivity();
                            mainActivity.showSMBDialog("", "", false);
                            return;
                        }
                        computers.remove(computers.size() - 1);
                        listViewAdapter.notifyDataSetChanged();
                    });
                }
            }
        });
        subnetScanner.start();

        builder.adapter(listViewAdapter, null);
        return builder.build();
    }

    private class ListViewAdapter extends RecyclerView.Adapter<ElementViewHolder> {
        private static final int VIEW_PROGRESSBAR = 1;
        private static final int VIEW_ELEMENT = 2;

        private ArrayList<ComputerParcelable> items;
        private LayoutInflater mInflater;
        private Context context;

        public ListViewAdapter(Context context, List<ComputerParcelable> objects) {
            this.context = context;
            items = new ArrayList<>(objects);
            mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public ElementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case VIEW_PROGRESSBAR:
                    view = mInflater.inflate(R.layout.smb_progress_row, parent, false);
                    return new ElementViewHolder(view);
                default:
                case VIEW_ELEMENT:
                    view = mInflater.inflate(R.layout.smb_computers_row, parent, false);
                    return new ElementViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(ElementViewHolder holder, int position) {
            int viewType = getItemViewType(position);
            if (viewType == VIEW_PROGRESSBAR) {
                return;
            }

            ComputerParcelable f = items.get(position);

            holder.rootView.setOnClickListener(v -> {
                if (subnetScanner != null)
                    subnetScanner.interrupt();
                if (getActivity() != null && getActivity() instanceof MainActivity) {
                    dismiss();
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.showSMBDialog(listViewAdapter.items.get(position).name,
                        listViewAdapter.items.get(position).addr, false);
                }
            });

            holder.txtTitle.setText(f.name);
            holder.image.setImageResource(R.drawable.ic_settings_remote_white_48dp);
            if (utilsProvider.getAppTheme().equals(AppTheme.LIGHT))
                holder.image.setColorFilter(Color.parseColor("#666666"));
            holder.txtDesc.setText(f.addr);
        }

        @Override
        public int getItemViewType(int position) {
            ComputerParcelable f = items.get(position);
            if (f.addr.equals("-1")) {
                return VIEW_PROGRESSBAR;
            } else {
                return VIEW_ELEMENT;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

    }

    private static class ElementViewHolder extends RecyclerView.ViewHolder {
        private View rootView;

        private ImageView image;
        private TextView txtTitle;
        private TextView txtDesc;

        ElementViewHolder(View view) {
            super(view);

            rootView = view;

            txtTitle = (TextView) view.findViewById(R.id.firstline);
            image = (ImageView) view.findViewById(R.id.icon);
            txtDesc = (TextView) view.findViewById(R.id.secondLine);
        }

    }

}