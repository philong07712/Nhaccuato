package com.example.nhaccuato.play.utils;

import com.example.nhaccuato.Models.ArtistResponse;
import com.example.nhaccuato.Models.SongResponse;
import com.example.nhaccuato.RetrofitHandler;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Retrofit;

public class SongService {

    // This class look like SongRepo
    private static final String TAG = "SongService";
    private Retrofit retrofit;
    private SongAPI api;

    public SongService() {
        this.retrofit = RetrofitHandler.getInstance().getSongRetrofit();
        api = this.retrofit.create(SongAPI.class);
    }

    // Lấy data từ api
    public Flowable<List<SongResponse>> getListSongResponsePlay() {
        return api.getListSongPlay();
    }

    public Flowable<List<SongResponse>> getListSongResponseList() {
        return api.getListSongList();
    }

    public Flowable<List<ArtistResponse>> getListArtistResponse(){
        return api.getListArtist();
    }
}
