package com.mob.gochat.view.base;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.lxj.xpopup.interfaces.XPopupImageLoader;

import java.io.File;

public class ImageLoader implements XPopupImageLoader {

    @Override
    public void loadImage(int position, @NonNull Object uri, @NonNull ImageView imageView) {
        Glide.with(imageView)
                .load(uri)
                .apply(new RequestOptions().override(Target.SIZE_ORIGINAL))
                .into(imageView);
    }

    @Override
    public File getImageFile(@NonNull Context context, @NonNull Object uri) {
        return null;
    }
}
