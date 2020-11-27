package com.example.nhaccuato.play;

import com.example.nhaccuato.Models.Song;

import java.util.List;

public interface PassData {
    public void onChange(List<Song> songs, int position);
}
