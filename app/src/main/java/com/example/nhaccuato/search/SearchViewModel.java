package com.example.nhaccuato.search;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.ViewModel;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.nhaccuato.MainActivity;
import com.example.nhaccuato.R;
import com.example.nhaccuato.detail.DetailFragment;
import com.example.nhaccuato.detail.DetailSerializable;
import com.example.nhaccuato.models.Artist;
import com.example.nhaccuato.models.Song;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {

    public static Context mContext;
    public static Activity mActivity;

    private String idSong = null;

    public SearchViewModel() {
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }

    @BindingAdapter({"app:load_image_item_artist_search", "app:load_image_item_song_search"})
    public static void setImageItemSearch(ImageView image, Artist artist, Song song) {
        if (artist != null) {
            Glide.with(mContext)
                    .load(artist.getThumbnail())
                    .centerCrop()
                    .fitCenter()
                    .placeholder(R.drawable.ic_baseline_music_note_orange)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                    .into(image);
        } else if (song != null) {
            Glide.with(mContext)
                    .load(song.getThumbnail())
                    .error(Glide.with(mContext).load(song.getThumbnail()).placeholder(R.drawable.ic_baseline_music_note_orange))
                    .centerCrop()
                    .fitCenter()
                    .placeholder(R.drawable.ic_baseline_music_note_orange)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                    .into(image);
        }

        //Event for item
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (artist != null) {
                    DetailSerializable songSerializable = new DetailSerializable(artist.getId(), artist.getThumbnail(), SearchFragment.songs, SearchFragment.artists);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("serializable", songSerializable);
                    DetailFragment detailArtistFragment = new DetailFragment();
                    detailArtistFragment.setArguments(bundle);
                    ((AppCompatActivity) v.getContext()).getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_bottom)
                            .add(R.id.fragment_search, detailArtistFragment, "Detail")
                            .addToBackStack(null)
                            .commit();
                }
                else {
                    List<Song> songs = new ArrayList<>();
                    songs.add(song);
                    ((MainActivity) mActivity).mPassData.onChange(songs, 0);
                }
            }
        });
    }
}