package com.example.nhaccuato.data;

import com.example.nhaccuato.models.Artist;
import com.example.nhaccuato.models.Song;

import java.util.List;

public interface OnArtistComplete {
    void onComplete(List<Artist> list);
}
