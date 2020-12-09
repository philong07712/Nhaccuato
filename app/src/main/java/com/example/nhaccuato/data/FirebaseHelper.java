package com.example.nhaccuato.data;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseHelper {
    public static void getAllSong() {
        Log.i("TAG", "getAllSong: ");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Artist").document();
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                Log.i("TAG", "getAllSong: " + documentSnapshot);
            } else {
                task.getException().printStackTrace();
            }
        });
    }
}
