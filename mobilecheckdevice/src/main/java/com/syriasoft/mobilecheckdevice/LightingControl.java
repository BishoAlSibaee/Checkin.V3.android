package com.syriasoft.mobilecheckdevice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilecheckdevice.R;

public class LightingControl extends AppCompatActivity {

    Activity act ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lighting_control);
        setActivity();
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
    }

    void setActivity() {
        act = this ;
    }

    public void goToDoubleControl(View view) {
        Intent i = new Intent(act, LightingDoubleControl.class);
        startActivity(i);
    }

    public void goToScreenButtons(View view) {
        Intent i = new Intent(act,ScreenButtons.class);
        startActivity(i);
    }

    public void goToMoods(View view) {
        Intent i = new Intent(act,Moods.class);
        startActivity(i);
    }
}