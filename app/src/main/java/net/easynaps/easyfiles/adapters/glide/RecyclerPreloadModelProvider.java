package net.easynaps.easyfiles.adapters.glide;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;

import net.easynaps.easyfiles.GlideApp;
import net.easynaps.easyfiles.GlideRequest;
import net.easynaps.easyfiles.adapters.data.IconDataParcelable;

import java.util.Collections;
import java.util.List;

public class RecyclerPreloadModelProvider implements ListPreloader.PreloadModelProvider<IconDataParcelable> {

    private Fragment fragment;
    private List<IconDataParcelable> urisToLoad;
    private boolean showThumbs;

    public RecyclerPreloadModelProvider(@NonNull Fragment fragment, @NonNull List<IconDataParcelable> uris,
                                        boolean showThumbs) {
        this.fragment = fragment;
        urisToLoad = uris;
        this.showThumbs = showThumbs;
    }

    @Override
    @NonNull
    public List<IconDataParcelable> getPreloadItems(int position) {
        IconDataParcelable iconData = urisToLoad.get(position);
        if (iconData == null) return Collections.emptyList();
        return Collections.singletonList(iconData);
    }

    @Override
    @Nullable
    public RequestBuilder<Drawable> getPreloadRequestBuilder(IconDataParcelable iconData) {
        if(!showThumbs) {
            return GlideApp.with(fragment).asDrawable().fitCenter().load(iconData.image);
        } else {
            GlideRequest<Drawable> request = GlideApp.with(fragment).asDrawable().centerCrop();

            if (iconData.type == IconDataParcelable.IMAGE_FROMFILE) {
                return request.load(iconData.path);
            } else {
                return request.load(iconData.image);
            }
        }
    }
    
}
