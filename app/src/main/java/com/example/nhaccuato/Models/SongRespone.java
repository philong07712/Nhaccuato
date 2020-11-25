package com.example.nhaccuato.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SongRespone {
    @SerializedName("music_list")
    @Expose
    private List<Song> mSongList;

    public SongRespone(List<Song> mSongList) {
        this.mSongList = mSongList;
    }

    public List<Song> getSongList() {
        return mSongList;
    }

    public void setSongList(List<Song> songList) {
        this.mSongList = songList;
    }
}
