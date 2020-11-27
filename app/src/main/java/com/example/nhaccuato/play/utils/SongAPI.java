package com.example.nhaccuato.play.utils;


import com.example.nhaccuato.Models.ArtistResponse;
import com.example.nhaccuato.Models.Song;
import com.example.nhaccuato.Models.SongResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface SongAPI {
    @Headers("Content-Type: application/json")
    @GET("api/songs")
    Flowable<List<SongResponse>> getListSongPlay();

    @GET("api/songs")
    Flowable<List<SongResponse>> getListSongList();

    @GET("api/artists")
    Flowable<List<ArtistResponse>> getListArtist();
}
