package com.example.mobilecheckdevice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RoomTemplates extends AppCompatActivity {

    DatabaseReference RoomTemplates ;
    Activity act ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_templates);
        setActivity();
    }

    void setActivity() {
        RoomTemplates = Rooms.RoomTemplates;
        RoomTemplates.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null)
                Log.d("gettingTemplates",snapshot.getValue().toString());
                Iterable<DataSnapshot> templates = snapshot.getChildren();
                for (DataSnapshot child : templates) {
                    Button b = new Button(act);
                    b.setText(child.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addNewTemplate(View view) {
        Intent i = new Intent(act,CreateNewTemplate.class);
        startActivity(i);
    }
}