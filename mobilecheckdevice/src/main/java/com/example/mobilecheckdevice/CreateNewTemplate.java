package com.example.mobilecheckdevice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;

import com.tuya.smart.home.sdk.bean.scene.SceneBean;

import java.util.ArrayList;
import java.util.List;

public class CreateNewTemplate extends AppCompatActivity {

    Activity act ;
    List<ROOM> roomsHasMoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_template);
        setActivity();
    }

    void setActivity() {
        act = this ;
        roomsHasMoods = new ArrayList<>();
    }

    void getRoomsHasMoodsAndDoubleControls () {
        for (ROOM r :MyApp.ROOMS) {
            for (SceneBean s : MyApp.SCENES) {
                if (s.getName().contains("Living") || s.getName().contains("Sleep") || s.getName().contains("Romance") || s.getName().contains("Read") || s.getName().contains("MasterOff") || s.getName().contains("MasterOn")) {
                    if (s.getName().contains(String.valueOf(r.RoomNumber))) {
                        roomsHasMoods.add(r);
                    }
                }
            }
        }
    }
}