package net.easynaps.easyfiles.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import net.easynaps.easyfiles.GlideApp;
import net.easynaps.easyfiles.R;
import net.easynaps.easyfiles.adapters.data.CompressedObjectParcelable;
import net.easynaps.easyfiles.adapters.holders.CompressedItemViewHolder;
import net.easynaps.easyfiles.filesystem.HybridFileParcelable;
import net.easynaps.easyfiles.filesystem.compressed.CompressedHelper;
import net.easynaps.easyfiles.filesystem.compressed.showcontents.Decompressor;
import net.easynaps.easyfiles.fragments.CompressedExplorerFragment;
import net.easynaps.easyfiles.ui.views.CircleGradientDrawable;
import net.easynaps.easyfiles.utils.AnimUtils;
import net.easynaps.easyfiles.utils.OpenMode;
import net.easynaps.easyfiles.utils.Utils;
import net.easynaps.easyfiles.utils.color.ColorUtils;
import net.easynaps.easyfiles.utils.provider.UtilitiesProvider;
import net.easynaps.easyfiles.utils.theme.AppTheme;

import java.util.ArrayList;

public class CompressedExplorerAdapter extends RecyclerView.Adapter<CompressedItemViewHolder> {

    private static final int TYPE_HEADER = 0, TYPE_ITEM = 1;

    public boolean stoppedAnimation = false;

    private Context context;
    private UtilitiesProvider utilsProvider;
    private Drawable folder;
    private ArrayList<CompressedObjectParcelable> items;
    private CompressedExplorerFragment compressedExplorerFragment;
    private Decompressor decompressor;
    private LayoutInflater mInflater;
    private boolean[] itemsChecked;
    private int offset = 0;

    public CompressedExplorerAdapter(Context c, UtilitiesProvider utilsProvider,
                                     ArrayList<CompressedObjectParcelable> items,
                                     CompressedExplorerFragment compressedExplorerFragment,
                                     Decompressor decompressor) {
        setHasStableIds(true);

        this.utilsProvider = utilsProvider;
        this.items = items;
        this.decompressor = decompressor;

        itemsChecked = new boolean[items.size()];

        context = c;
        if (c == null) return;
        folder = c.getResources().getDrawable(R.drawable.ic_grid_folder_new);
        this.compressedExplorerFragment = compressedExplorerFragment;
        mInflater = (LayoutInflater) c.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    public void toggleChecked(boolean check) {
        int k = 0;

        for (int i = k; i < items.size(); i++) {
            itemsChecked[i] = check;
            notifyItemChanged(i);
        }
    }

    public ArrayList<Integer> getCheckedItemPositions() {
        ArrayList<Integer> checkedItemPositions = new ArrayList<>();

        for (int i = 0; i < itemsChecked.length; i++) {
            if (itemsChecked[i]) {
                (checkedItemPositions).add(i);
            }
        }

        return checkedItemPositions;
    }

    /**
     * called as to toggle selection of any item in adapter
     *
     * @param position  the position of the item
     * @param imageView the circular {@link net.pubnative.easyfiles.ui.views.CircleGradientDrawable} that is to be animated
     */
    private void toggleChecked(int position, ImageView imageView) {
        compressedExplorerFragment.stopAnim();
        stoppedAnimation = true;

        Animation animation;
        if (itemsChecked[position]) {
            animation = AnimationUtils.loadAnimation(context, R.anim.check_out);
        } else {
            animation = AnimationUtils.loadAnimation(context, R.anim.check_in);
        }

        if (imageView != null) {
            imageView.setAnimation(animation);
        } else {
            // TODO: we don't have the check icon object probably because of config change
        }

        itemsChecked[position] = !itemsChecked[position];

        notifyDataSetChanged();
        if (!compressedExplorerFragment.selection || compressedExplorerFragment.mActionMode == null) {
            compressedExplorerFragment.selection = true;
            /*compressedExplorerFragment.mActionMode = compressedExplorerFragment.getActivity().startActionMode(
                   compressedExplorerFragment.mActionModeCallback);*/
            compressedExplorerFragment.mActionMode = compressedExplorerFragment.mainActivity.getAppbar().getToolbar().startActionMode(compressedExplorerFragment.mActionModeCallback);
        }
        compressedExplorerFragment.mActionMode.invalidate();
        if (getCheckedItemPositions().size() == 0) {
            compressedExplorerFragment.selection = false;
            compressedExplorerFragment.mActionMode.finish();
            compressedExplorerFragment.mActionMode = null;
        }
    }

    private void animate(CompressedItemViewHolder holder) {
        holder.rl.clearAnimation();
        Animation localAnimation = AnimationUtils.loadAnimation(compressedExplorerFragment.getActivity(), R.anim.fade_in_top);
        localAnimation.setStartOffset(this.offset);
        holder.rl.startAnimation(localAnimation);
        this.offset = (30 + this.offset);
    }

    public void generateZip(ArrayList<CompressedObjectParcelable> arrayList) {
        offset = 0;
        stoppedAnimation = false;
        items = arrayList;
        notifyDataSetChanged();
        itemsChecked = new boolean[items.size()];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    @Override
    public CompressedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = mInflater.inflate(R.layout.rowlayout, parent, false);
            v.findViewById(R.id.picture_icon).setVisibility(View.INVISIBLE);
            return new CompressedItemViewHolder(v);
        } else if(viewType == TYPE_ITEM) {
            View v = mInflater.inflate(R.layout.rowlayout, parent, false);
            CompressedItemViewHolder vh = new CompressedItemViewHolder(v);
            ImageButton about = (ImageButton) v.findViewById(R.id.properties);
            about.setVisibility(View.INVISIBLE);
            return vh;
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void onBindViewHolder(final CompressedItemViewHolder holder, int position) {
        if (!stoppedAnimation) {
            animate(holder);
        }

        final CompressedObjectParcelable rowItem = items.get(position);
        GradientDrawable gradientDrawable = (GradientDrawable) holder.genericIcon.getBackground();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            holder.checkImageView.setBackground(new CircleGradientDrawable(compressedExplorerFragment.accentColor,
                    utilsProvider.getAppTheme(), compressedExplorerFragment.getResources().getDisplayMetrics()));
        } else
            holder.checkImageView.setBackgroundDrawable(new CircleGradientDrawable(compressedExplorerFragment.accentColor,
                    utilsProvider.getAppTheme(), compressedExplorerFragment.getResources().getDisplayMetrics()));

        if (rowItem.type == CompressedObjectParcelable.TYPE_GOBACK) {
            GlideApp.with(compressedExplorerFragment).load(R.drawable.ic_arrow_left_white_24dp).into(holder.genericIcon);
            gradientDrawable.setColor(Utils.getColor(context, R.color.goback_item));
            holder.txtTitle.setText("..");
            holder.txtDesc.setText("");
            holder.date.setText(R.string.goback);
        } else {
            GlideApp.with(compressedExplorerFragment).load(rowItem.iconData.image).into(holder.genericIcon);

            final StringBuilder stringBuilder = new StringBuilder(rowItem.name);
            if (compressedExplorerFragment.showLastModified)
                holder.date.setText(Utils.getDate(rowItem.date, compressedExplorerFragment.year));
            if (rowItem.directory) {
                holder.genericIcon.setImageDrawable(folder);
                gradientDrawable.setColor(Color.parseColor(compressedExplorerFragment.iconskin));
                if (stringBuilder.toString().length() > 0) {
                    if (rowItem.name.endsWith(CompressedHelper.SEPARATOR)) {
                        stringBuilder.deleteCharAt(rowItem.name.length() - 1);
                    }
                    try {
                        holder.txtTitle.setText(stringBuilder.toString().substring(stringBuilder.toString().lastIndexOf("/") + 1));
                    } catch (Exception e) {
                        holder.txtTitle.setText(rowItem.name.substring(0, rowItem.name.lastIndexOf("/")));
                    }
                }
            } else {
                if (compressedExplorerFragment.showSize)
                    holder.txtDesc.setText(Formatter.formatFileSize(context, rowItem.size));
                holder.txtTitle.setText(rowItem.name.substring(rowItem.name.lastIndexOf("/") + 1));
                if (compressedExplorerFragment.coloriseIcons) {
                    ColorUtils.colorizeIcons(context, rowItem.filetype, gradientDrawable,
                            Color.parseColor(compressedExplorerFragment.iconskin));
                } else gradientDrawable.setColor(Color.parseColor(compressedExplorerFragment.iconskin));
            }
        }

        holder.rl.setOnLongClickListener(view -> {
            if (rowItem.type != CompressedObjectParcelable.TYPE_GOBACK) {
                toggleChecked(position, holder.checkImageView);
            }
            return true;
        });
        holder.genericIcon.setOnClickListener(view -> {
            if (rowItem.type != CompressedObjectParcelable.TYPE_GOBACK) {
                toggleChecked(position, holder.checkImageView);
            } else {
                compressedExplorerFragment.goBack();
            }
        });
        if (utilsProvider.getAppTheme().equals(AppTheme.LIGHT)) {
            holder.rl.setBackgroundResource(R.drawable.safr_ripple_white);
        } else {
            holder.rl.setBackgroundResource(R.drawable.safr_ripple_black);
        }
        holder.rl.setSelected(false);
        if (itemsChecked[position]) {
            //holder.genericIcon.setImageDrawable(compressedExplorerFragment.getResources().getDrawable(R.drawable.abc_ic_cab_done_holo_dark));
            holder.checkImageView.setVisibility(View.VISIBLE);
            gradientDrawable.setColor(Utils.getColor(context, R.color.goback_item));
            holder.rl.setSelected(true);
        } else holder.checkImageView.setVisibility(View.INVISIBLE);

        holder.rl.setOnClickListener(p1 -> {
            if (rowItem.type == CompressedObjectParcelable.TYPE_GOBACK)
                compressedExplorerFragment.goBack();
            else {
                if (compressedExplorerFragment.selection) {
                    toggleChecked(position, holder.checkImageView);
                } else {
                    final StringBuilder stringBuilder = new StringBuilder(rowItem.name);
                    if (rowItem.name.endsWith(CompressedHelper.SEPARATOR))
                        stringBuilder.deleteCharAt(rowItem.name.length() - 1);

                    if (rowItem.directory) {
                        compressedExplorerFragment.changePath(stringBuilder.toString());
                    } else {
                        String fileName = compressedExplorerFragment.compressedFile.getName().substring(0,
                                compressedExplorerFragment.compressedFile.getName().lastIndexOf("."));
                        String archiveCacheDirPath = compressedExplorerFragment.getActivity().getExternalCacheDir().getPath() +
                                CompressedHelper.SEPARATOR + fileName;

                        HybridFileParcelable file = new HybridFileParcelable(archiveCacheDirPath
                                + CompressedHelper.SEPARATOR
                                + rowItem.name.replaceAll("\\\\", CompressedHelper.SEPARATOR));
                        file.setMode(OpenMode.FILE);
                        // this file will be opened once service finishes up it's extraction
                        compressedExplorerFragment.files.add(file);
                        // setting flag for binder to know
                        compressedExplorerFragment.isOpen = true;

                        Toast.makeText(compressedExplorerFragment.getContext(),
                                compressedExplorerFragment.getContext().getResources().getString(R.string.please_wait),
                                Toast.LENGTH_SHORT).show();
                        decompressor.decompress(compressedExplorerFragment.getActivity().getExternalCacheDir().getPath(),
                                new String[]{rowItem.name});
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onViewDetachedFromWindow(CompressedItemViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.rl.clearAnimation();
        holder.txtTitle.setSelected(false);
    }

    @Override
    public void onViewAttachedToWindow(CompressedItemViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        AnimUtils.marqueeAfterDelay(2000, holder.txtTitle);
    }

    @Override
    public boolean onFailedToRecycleView(CompressedItemViewHolder holder) {
        holder.rl.clearAnimation();
        holder.txtTitle.setSelected(false);
        return super.onFailedToRecycleView(holder);
    }

    private boolean isPositionHeader(int position) {
        return false;// TODO:
    }

}

