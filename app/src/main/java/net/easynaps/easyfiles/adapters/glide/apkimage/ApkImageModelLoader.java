package net.easynaps.easyfiles.adapters.glide.apkimage;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;

public class ApkImageModelLoader implements ModelLoader<String, Drawable> {

    private PackageManager packageManager;

    public ApkImageModelLoader(PackageManager packageManager) {
        this.packageManager = packageManager;
    }

    @Nullable
    @Override
    public LoadData<Drawable> buildLoadData(String s, int width, int height, Options options) {
        return new LoadData<>(new ObjectKey(s), new ApkImageDataFetcher(packageManager, s));
    }

    @Override
    public boolean handles(String s) {
        return s.substring(s.length()-4, s.length()).toLowerCase().equals(".apk");
    }

}
