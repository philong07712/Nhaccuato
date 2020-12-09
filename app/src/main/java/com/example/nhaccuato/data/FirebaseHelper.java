package com.example.nhaccuato.data;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class FirebaseHelper {
    public static void getAllSong() {
        Log.i("TAG", "getAllSong: ");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection("Song");
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.i("TAG", "getAllSong: id " + document.getId() + "=>" + document.getData());
                }
            } else {
                task.getException().printStackTrace();
            }
        });
    }
}
