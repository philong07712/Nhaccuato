package com.example.nhaccuato.models;

import java.util.List;

public class Artist {
    private String name;
    private String description;
    private int year_of_birth;
    private String id;
    private String thumbnail;
    private List<String> songsList;
    private ArtistResponse artistResponse;

    public Artist() {
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getYear_of_birth() {
        return year_of_birth;
    }

    public String getId() {
        return id;
    }

    public List<String> getSongsList() {
        return songsList;
    }

    public ArtistResponse getArtistResponse() {
        return artistResponse;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setYear_of_birth(int year_of_birth) {
        this.year_of_birth = year_of_birth;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSongsList(List<String> songsList) {
        this.songsList = songsList;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
