package com.example.nhaccuato.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SongResponse {
    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("description")
    @Expose
    public String description;

    @SerializedName("id")
    @Expose
    public String id;
}
