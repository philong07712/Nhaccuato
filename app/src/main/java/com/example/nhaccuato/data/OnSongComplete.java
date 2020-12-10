package com.example.nhaccuato.data;

import com.example.nhaccuato.models.Song;

import java.util.List;

public interface OnSongComplete {
    void onComplete(List<Song> list);
}
