package com.example.mobilecheckdevice;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;

public class ScreenButtons extends AppCompatActivity {

    Activity act ;
    public static RecyclerView CurrentButtonsRecycler , SwitchesButtons , SwitchesRecycler ;
    List<ScreenButton> CurrentButtons ;
    LinearLayoutManager CurrentManager , ButtonsManager , SwitchesManager ;
    List<DeviceBean> Switches ;
    public static List<String> Buttons ;
    public static ScreenButtons_Adapter CurrentAdapter ;
    public static ScreenButtonsSwitches_Adapter SwitchesAdapter ;
    public static ScreenButtonsButtons_Adapter ButtonsAdapter ;
    public static DeviceBean SelectedSwitch ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_buttons);
        setActivity();
    }

    void setActivity() {
        act = this ;
        CurrentButtons = new ArrayList<>();
        Buttons = new ArrayList<>();
        Switches = new ArrayList<>();
        CurrentButtonsRecycler = findViewById(R.id.currentButtonsRecycler);
        SwitchesButtons = findViewById(R.id.buttonsRecycler);
        SwitchesRecycler = findViewById(R.id.switchesRecycler);
        ButtonsManager = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        CurrentManager = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        SwitchesManager = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        CurrentButtonsRecycler.setLayoutManager(CurrentManager);
        SwitchesButtons.setLayoutManager(ButtonsManager);
        SwitchesRecycler.setLayoutManager(SwitchesManager);
        CurrentButtonsRecycler.setNestedScrollingEnabled(false);
        SwitchesButtons.setNestedScrollingEnabled(false);
        SwitchesRecycler.setNestedScrollingEnabled(false);
        //CurrentAdapter = new ScreenButtons_Adapter(FullscreenActivity.lightsDB.getScreenButtons());
        CurrentButtonsRecycler.setAdapter(CurrentAdapter);
//        if (RoomManager..THE_ROOM.getSWITCH1_B() != null ) {
//            Switches.add(FullscreenActivity.THE_ROOM.getSWITCH1_B());
//        }
//        if (FullscreenActivity.THE_ROOM.getSWITCH2_B() != null ) {
//            Switches.add(FullscreenActivity.THE_ROOM.getSWITCH2_B());
//        }
//        if (FullscreenActivity.THE_ROOM.getSWITCH3_B() != null ) {
//            Switches.add(FullscreenActivity.THE_ROOM.getSWITCH3_B());
//        }
//        if (FullscreenActivity.THE_ROOM.getSWITCH4_B() != null ) {
//            Switches.add(FullscreenActivity.THE_ROOM.getSWITCH4_B());
//        }
//        if (FullscreenActivity.THE_ROOM.getSWITCH5_B() != null ) {
//            Switches.add(FullscreenActivity.THE_ROOM.getSWITCH5_B());
//        }
//        if (FullscreenActivity.THE_ROOM.getSWITCH6_B() != null ) {
//            Switches.add(FullscreenActivity.THE_ROOM.getSWITCH6_B());
//        }
//        if (FullscreenActivity.THE_ROOM.getSWITCH7_B() != null ) {
//            Switches.add(FullscreenActivity.THE_ROOM.getSWITCH7_B());
//        }
//        if (FullscreenActivity.THE_ROOM.getSWITCH8_B() != null ) {
//            Switches.add(FullscreenActivity.THE_ROOM.getSWITCH8_B());
//        }
        SwitchesAdapter = new ScreenButtonsSwitches_Adapter(Switches);
        SwitchesRecycler.setAdapter(SwitchesAdapter);
    }
}