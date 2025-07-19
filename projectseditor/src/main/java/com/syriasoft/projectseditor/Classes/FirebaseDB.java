package com.syriasoft.projectseditor.Classes;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDB {

    private final String firebaseDBUrl = "https://checkin-62774-default-rtdb.asia-southeast1.firebasedatabase.app";
    private static FirebaseDB FBD;
    FirebaseDatabase fbDB;

    public FirebaseDB() {
        if (fbDB == null) {
            fbDB = FirebaseDatabase.getInstance(firebaseDBUrl);
        }
    }

    public static FirebaseDB getInstance() {
        if (FBD == null) {
            FBD = new FirebaseDB();
        }
        return FBD;
    }

    public DatabaseReference createDBReference(String url) {
        return FBD.fbDB.getReference(url);
    }

}
