package com.syriasoft.projectscontrol;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class Device extends AppCompatActivity {

    int ID;
    ServerDevice DEVICE;
    PROJECT Project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        setActivity();
        setBuildingsFloorsRooms();
    }

    void setActivity() {
        ID = getIntent().getExtras().getInt("id");
        DEVICE = MainActivity.SelectedDevicesList.get(ID);
        Project = MainActivity.SelectedProject;
        TextView ProjectName = findViewById(R.id.textView4);
        ProjectName.setText(String.format("%s", DEVICE.ProjectName));
        TextView DeviceName = findViewById(R.id.textView5);
        DeviceName.setText(String.format("%s", DEVICE.name));
        TextView ControlRooms = findViewById(R.id.textView7);
        ControlRooms.setText(DEVICE.roomsIds);
        TextView LastUpdate = findViewById(R.id.textView70);
        Calendar c = Calendar.getInstance(Locale.getDefault());
        c.setTimeInMillis(DEVICE.checkValue);
        String Date = c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DAY_OF_MONTH);
        String Time = c.get(Calendar.HOUR)+":"+c.get(Calendar.MINUTE);
        LastUpdate.setText(String.format("%s %s", Date, Time));
    }

    void setBuildingsFloorsRooms() {
        for (BUILDING b : Project.Buildings) {
            b.getBuildingFloors(Project.AllFloors);
            for (FLOOR f:b.Floors) {
                f.getFloorRooms(Project.AllRooms);
            }
        }
        Log.d("projectBFR",Project.Buildings.size()+" "+Project.AllFloors.size()+" "+Project.AllRooms.size());
    }
}