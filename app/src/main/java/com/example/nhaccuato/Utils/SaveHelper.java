package com.example.nhaccuato.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.nhaccuato.models.Song;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SaveHelper {
    private static final String OFFLINE_LIST_KEY = "Offline Song List";

    private static final String PREVIOUS_SONG_POS = "Previous Song Position";
    private static final String PREVIOUS_LIST_KEY = "Previous Song List";


    public static void saveOfflineSong(Context context, Map<String, Song> songs) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(songs);
        editor.putString(OFFLINE_LIST_KEY, json);
        editor.apply();
    }

    public static Map<String, Song> loadOfflineSong(Context context) {
        Map<String, Song> songs = new HashMap<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(OFFLINE_LIST_KEY, null);
        Type type = new TypeToken<Map<String, Song>>() {}.getType();
        songs = gson.fromJson(json, type);
        if (songs == null) {
            return new HashMap<>();
        }
        return songs;
    }

    public static void saveSong(Context context, List<Song> songs) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(songs);
        editor.putString(PREVIOUS_LIST_KEY, json);
        editor.apply();
    }

    public static List<Song> loadSong(Context context) {
        List<Song> songs = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(PREVIOUS_LIST_KEY, null);
        Type type = new TypeToken<List<Song>>() {}.getType();
        songs = gson.fromJson(json, type);
        if (songs == null) {
            return new ArrayList<>();
        }
        return songs;
    }

    public static void saveCurrentSongPosition(Context context, int position) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREVIOUS_SONG_POS, position);
        editor.apply();
    }

    public static int loadCurrentSongPosition(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(PREVIOUS_SONG_POS, 0);
    }

    public static void deleteData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
    }
}
