package com.example.nhaccuato.list;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.nhaccuato.data.FirebaseHelper;
import com.example.nhaccuato.models.Song;
import com.example.nhaccuato.models.SongResponse;
import com.example.nhaccuato.play.utils.SongService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class ListViewModel extends ViewModel {



    public Song song = new Song();
    public static Context mContext;
    private SongService mService = new SongService();

    private CompositeDisposable mCompositeDisposal = new CompositeDisposable();
    private Flowable<List<SongResponse>> mSongResponeFlowable;
    private MutableLiveData<List<Song>> songLiveData;

    public ListViewModel() {
        mSongResponeFlowable = mService.getListSongResponseList();
        songLiveData = new MutableLiveData<>();
        songLiveData.setValue(new ArrayList<>());
    }

    public LiveData<List<Song>> getSongLiveData() {
        return songLiveData;
    }

    public void loadSong() {
        FirebaseHelper.getAllSong();
    }

    public Flowable<List<SongResponse>> getmSongResponeFlowable() {
        return mSongResponeFlowable;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        mCompositeDisposal.clear();
    }

    @BindingAdapter("app:load_image_list_artist")
    public static void setImageArtist(ImageView image, String url) {
        Glide.with(mContext)
                .load(url)
                .centerCrop()
                .fitCenter()
                .into(image);
    }

    @BindingAdapter("app:load_image_list_song")
    public static void setImageSong(ImageView image, String url) {
        Glide.with(mContext)
                .load(url)
                .centerCrop()
                .fitCenter()
                .into(image);
    }

    @BindingAdapter("app:load_background_color")
    public static void setBackgroundColor(CardView view, String url) {
        Glide.with(mContext)
                .asBitmap()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@Nullable Palette palette) {
                                Palette.Swatch vibrant = palette.getVibrantSwatch();
                                Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                                if (vibrant != null){
                                    view.setCardBackgroundColor(mutedSwatch.getRgb());
                                } else{
                                    view.setCardBackgroundColor(mutedSwatch.getRgb());
                                }
                            }
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

}