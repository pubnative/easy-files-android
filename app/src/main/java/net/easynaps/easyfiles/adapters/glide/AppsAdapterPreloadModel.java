package net.easynaps.easyfiles.adapters.glide;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;


import java.util.Collections;
import java.util.List;

public class AppsAdapterPreloadModel implements ListPreloader.PreloadModelProvider<String> {

    private RequestBuilder<Drawable> request;
    private List<String> items;

    public AppsAdapterPreloadModel(Fragment f) {
        request = Glide.with(f).asDrawable().fitCenter();
    }

    public void setItemList(List<String> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public List<String> getPreloadItems(int position) {
        if(items == null) return Collections.emptyList();
        else return Collections.singletonList(items.get(position));
    }

    @Nullable
    @Override
    public RequestBuilder getPreloadRequestBuilder(String item) {
        return request.clone().load(item);
    }

    public void loadApkImage(String item, ImageView v) {
        request.load(item).into(v);
    }
}
