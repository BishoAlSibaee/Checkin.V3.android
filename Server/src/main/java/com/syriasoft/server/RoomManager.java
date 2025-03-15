package com.syriasoft.server;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.hotelservicesstandalone.R;
import com.syriasoft.server.Classes.DefaultExceptionHandler;

import java.text.MessageFormat;
import java.util.Objects;

public class RoomManager extends AppCompatActivity {
    private com.syriasoft.server.Classes.Property.Room Room ;
    RequestQueue REQ ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_manager);
        setActivity();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        PendingIntent pIntent = PendingIntent.getActivity(MyApp.app.getBaseContext(), 0,new Intent(getIntent()), PendingIntent.FLAG_IMMUTABLE);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this,pIntent));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setActivity() {
        Activity act = this;
        REQ = Volley.newRequestQueue(act);
        int ID = Objects.requireNonNull(getIntent().getExtras()).getInt("RoomId");
        for (int i = 0; i<MyApp.ROOMS.size(); i++) {
            if (MyApp.ROOMS.get(i).id == ID) {
                Room = MyApp.ROOMS.get(i) ;
            }
        }
        TextView caption = findViewById(R.id.roomManager_caption);
        caption.setText(MessageFormat.format("Manage Room : {0}", Room.RoomNumber));
    }
}