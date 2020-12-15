package com.example.nhaccuato.detail;

import com.example.nhaccuato.models.Artist;
import com.example.nhaccuato.models.Song;

import java.io.Serializable;
import java.util.List;

public class DetailSerializable implements Serializable {
    private transient String idArtist;
    private transient String artistThumbnail;
    private transient List<Song> songList;
    private transient List<Artist> artistList;

    public DetailSerializable(String idArtist, String artistThumbnail, List<Song> songList, List<Artist> artistList) {
        this.idArtist = idArtist;
        this.songList = songList;
        this.artistList = artistList;
        this.artistThumbnail = artistThumbnail;
    }

    public String getIdArtist() {
        return idArtist;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public List<Artist> getArtistList() {
        return artistList;
    }

    public String getArtistThumbnail() {
        return artistThumbnail;
    }
}
