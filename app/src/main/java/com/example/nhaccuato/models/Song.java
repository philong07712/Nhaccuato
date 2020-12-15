package com.example.nhaccuato.models;

public class Song {
    private String nameSong;
    private String description;
    private String idSong;
    private String idArtist;
    private String nameArtist;
    private String thumbnail;
    private String imgArtist;
    private String song;

    public Song() {
    }

    public Song(String song, String nameSong, String idSong, String idArtist, String nameArtist, String thumbnailUri) {
        this.song = song;
        this.nameSong = nameSong;
        this.idSong = idSong;
        this.idArtist = idArtist;
        this.nameArtist = nameArtist;
        this.thumbnail = thumbnailUri;
    }

    public String getNameSong() {
        return nameSong;
    }

    public String getDescription() {
        return description;
    }

    public String getIdSong() {
        return idSong;
    }

    public String getIdArtist() {
        return idArtist;
    }

    public String getNameArtist() {
        return nameArtist;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getImgArtist() {
        return imgArtist;
    }

    public String getSong() {
        return song;
    }

    public void setNameSong(String nameSong) {
        this.nameSong = nameSong;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIdSong(String idSong) {
        this.idSong = idSong;
    }

    public void setIdArtist(String idArtist) {
        this.idArtist = idArtist;
    }

    public void setNameArtist(String nameArtist) {
        this.nameArtist = nameArtist;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setImgArtist(String imgArtist) {
        this.imgArtist = imgArtist;
    }

    public void setSong(String song) {
        this.song = song;
    }

}
