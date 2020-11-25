package com.example.nhaccuato.play;

import com.example.nhaccuato.Models.SongRespone;
import com.example.nhaccuato.RetrofitHandler;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Retrofit;

public class SongService {
    private static final String TAG = "SongService";
    private Retrofit retrofit;
    private SongAPI api;

    public SongService() {
        this.retrofit = RetrofitHandler.getInstance().getSongRetrofit();
        api = this.retrofit.create(SongAPI.class);
    }

    public Observable<SongRespone> getSongRespone() {
        return api.getSongList("IwAR07nN-lOIn2Ewd1rrsl2WeHG6jUQ6diZOoPQDpiE8jP7tR7a3tyc67YXmk");
    }

}