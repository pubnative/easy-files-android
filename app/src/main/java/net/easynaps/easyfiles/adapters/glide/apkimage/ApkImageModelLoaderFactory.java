package net.easynaps.easyfiles.adapters.glide.apkimage;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

public class ApkImageModelLoaderFactory implements ModelLoaderFactory<String, Drawable> {

    private PackageManager packageManager;

    public ApkImageModelLoaderFactory(PackageManager packageManager) {
        this.packageManager = packageManager;
    }

    @Override
    public ModelLoader<String, Drawable> build(MultiModelLoaderFactory multiFactory) {
        return new ApkImageModelLoader(packageManager);
    }

    @Override
    public void teardown() {

    }
}
