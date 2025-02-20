package vn.tapbi.zazip.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import vn.tapbi.zazip.glide.apkimage.ApkImageModelLoaderFactory;

@GlideModule
public class GlideManagerModule extends AppGlideModule {
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        registry.prepend(String.class, Drawable.class, new ApkImageModelLoaderFactory(context));
//        registry.prepend(String.class, Bitmap.class, new CloudIconModelFactory(context));
    }
}