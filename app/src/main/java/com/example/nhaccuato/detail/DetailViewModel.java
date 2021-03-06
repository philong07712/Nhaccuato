package com.example.nhaccuato.detail;

import android.content.Context;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

public class DetailViewModel extends ViewModel {

    public static Context mContext;

    public DetailViewModel() {
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    @BindingAdapter("app:load_image_detail_song")
    public static void setImageSong(ImageView image, String songthumbnail) {
        Glide.with(mContext)
                .load(songthumbnail)
                .centerCrop()
                .fitCenter()
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                .into(image);
    }
}