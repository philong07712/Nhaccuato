package com.example.nhaccuato.play;

import android.content.Context;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.example.nhaccuato.R;
import com.example.nhaccuato.models.Song;
import com.example.nhaccuato.models.SongResponse;
import com.example.nhaccuato.play.utils.SongService;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class PlayViewModel extends ViewModel {
    private static final int BLUR_RADIUS = 60;
    private static final int BLUR_SAMPLING = 3;
    private SongService songService = new SongService();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public Song song = new Song();
    public static Context mContext;
    private final String TAG = PlayViewModel.class.getSimpleName();

    // Todo: Fields
    // Song service to get data
    private SongService mService = new SongService();
    // media service to play the song

    private CompositeDisposable mCompositeDisposal = new CompositeDisposable();
    private Flowable<List<SongResponse>> mSongResponeFlowable;
    // variables
    private List<Song> mSongs = new ArrayList<>();
    // Todo: Constructor
    public PlayViewModel() {
        mSongResponeFlowable = mService.getListSongResponsePlay();
    }


    // Todo: public method
    public void init() {
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public Flowable<List<SongResponse>> getmSongResponeFlowable() {
        return mSongResponeFlowable;
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        mCompositeDisposal.clear();
    }
    @BindingAdapter("app:load_image_play")
    public static void setImage(ImageView image, Song song) {
        Glide.with(mContext)
                .load(song.getThumbnail()).centerCrop()
                .placeholder(R.drawable.ic_baseline_music_note_orange)
                .fitCenter().into(image);
    }

    @BindingAdapter("app:load_image_play_bg")
    public static void setImageBackground(ImageView image, Song song) {
        if (song == null) return;
        DrawableCrossFadeFactory fadeFactory = new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
        Drawable preImage = image.getDrawable();
        Glide.with(mContext)
                .load(song.getThumbnail())
                .transition(new DrawableTransitionOptions().crossFade(fadeFactory))
                .centerCrop()
//                .placeholder(preImage)
                .error(R.drawable.background_list)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(image);
    }
}
