package com.example.nhaccuato.data;

import android.util.Log;

import com.example.nhaccuato.models.Artist;
import com.example.nhaccuato.models.Song;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {
    public static void  getAllSong(OnSongComplete listener) {
        List<Song> list = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("Song");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Song song = new Song();
                    list.add(song);
                    song.setDescription((String) document.get("description"));
                    song.setIdSong((String) document.get("id"));
                    song.setIdArtist((String) document.get("idArtist"));
                    song.setNameSong((String) document.get("name"));
                    song.setNameArtist((String) document.get("nameArtist"));
                    song.setSong((String) document.get("song"));
                    song.setThumbnail((String) document.get("thumbnail"));
                }
            } else {
                task.getException().printStackTrace();
            }
            listener.onComplete(list);
        });
    }

    public static void getAllArtists(OnArtistComplete listener) {
        List<Artist> list = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("Artist");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Artist artist = new Artist();
                    list.add(artist);
                    artist.setName((String) document.get("name"));
                    artist.setYear_of_birth(((Long) document.get("birth")).intValue());
                    artist.setId((String) document.get("id"));
                    artist.setDescription((String) document.get("description"));
                    artist.setThumbnail((String) document.get("thumbnail"));

//                    Map<String, String> songMap = (Map<String, String>) document.getData().get("songs");
                    List<String> songs = (List<String>) document.getData().get("songs");
//                    for (String key : songMap.keySet()) {
//                        songs.add(songMap.get(key));
//                    }
                    artist.setSongsList(songs);
                }
            } else {
                task.getException().printStackTrace();
            }
            listener.onComplete(list);
        });
    }
}
