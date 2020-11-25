package com.example.nhaccuato.play;

import com.example.nhaccuato.Models.SongRespone;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SongAPI {
    @GET("data.json")
    Observable<SongRespone> getSongList(
            @Query("fbclid") String endpoint
    );
}
