package com.syriasoft.hotelservices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;

public class LightingDoubleControl extends AppCompatActivity {

    Activity act ;
    LinearLayout FirstLayout , SecondLayout ;
    List<DeviceBean> FirstDeviceList , SecondDeviceList ;
    RecyclerView FirstDevicesRec , SecondDevicesRec;
    LinearLayoutManager fManager , sManager ;
    DoubleControlFirst_Adapter fAdapter;
    DoubleControlSecond_Adapter sAdapter;
    public static DeviceBean FIRST , SECOND ;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lighting_double_control);
        setActivity();
    }

    void setActivity() {
        act = this ;
        FirstLayout = findViewById(R.id.FirstDeviceLayout);
        SecondLayout = findViewById(R.id.SecondDeviceLayout);
        FirstDevicesRec = findViewById(R.id.FirstDevicesRecycler);
        SecondDevicesRec = findViewById(R.id.SecondDevicesRecycler);
        fManager = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        sManager = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        FirstDevicesRec.setLayoutManager(fManager);
        SecondDevicesRec.setLayoutManager(sManager);
        FirstDeviceList = new ArrayList<>();
        SecondDeviceList = new ArrayList<>();
        setDevicesButtons();
    }

    void setDevicesButtons() {
        if (FullscreenActivity.THE_ROOM.getSWITCH1_B() != null ) {
            FirstDeviceList.add(FullscreenActivity.THE_ROOM.getSWITCH1_B());
            SecondDeviceList.add(FullscreenActivity.THE_ROOM.getSWITCH1_B());
        }
        if (FullscreenActivity.THE_ROOM.getSWITCH2_B() != null) {
            FirstDeviceList.add(FullscreenActivity.THE_ROOM.getSWITCH2_B());
            SecondDeviceList.add(FullscreenActivity.THE_ROOM.getSWITCH2_B());
        }
        if (FullscreenActivity.THE_ROOM.getSWITCH3_B() != null) {
            FirstDeviceList.add(FullscreenActivity.THE_ROOM.getSWITCH3_B());
            SecondDeviceList.add(FullscreenActivity.THE_ROOM.getSWITCH3_B());
        }
        if (FullscreenActivity.THE_ROOM.getSWITCH4_B() != null) {
            FirstDeviceList.add(FullscreenActivity.THE_ROOM.getSWITCH4_B());
            SecondDeviceList.add(FullscreenActivity.THE_ROOM.getSWITCH4_B());
        }
        if (FullscreenActivity.THE_ROOM.getSERVICE1_B() != null) {
            FirstDeviceList.add(FullscreenActivity.THE_ROOM.getSERVICE1_B());
            SecondDeviceList.add(FullscreenActivity.THE_ROOM.getSERVICE1_B());
        }
        fAdapter = new DoubleControlFirst_Adapter(FirstDeviceList);
        FirstDevicesRec.setAdapter(fAdapter);
        sAdapter = new DoubleControlSecond_Adapter(SecondDeviceList);
        SecondDevicesRec.setAdapter(sAdapter);
    }

    public void nextToSelectDps(View view) {
        if (FIRST == null ) {
            ToastMaker.MakeToast("select first device",act);
            return;
        }
        if (SECOND == null ) {
            ToastMaker.MakeToast("select second device",act);
            return;
        }
        Intent i = new Intent(act,DoubleControlSelectDps.class);
        startActivity(i);
    }
}