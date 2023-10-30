package com.example.mobilecheckdevice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.smart.android.device.api.ITuyaDeviceMultiControl;
import com.tuya.smart.android.device.bean.MultiControlBean;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LightingDoubleControl extends AppCompatActivity {

    Activity act ;
    LinearLayout FirstLayout , SecondLayout ;
    List<DeviceBean> FirstDeviceList , SecondDeviceList ;
    RecyclerView FirstDevicesRec , SecondDevicesRec;
    LinearLayoutManager fManager , sManager ;
    DoubleControlFirst_Adapter fAdapter;
    DoubleControlSecond_Adapter sAdapter;
    public static DeviceBean FIRST , SECOND ;
    Button S1_1,S1_2,S1_3,S1_4,S2_1,S2_2,S2_3,S2_4,S3_1,S3_2,S3_3,S3_4,S4_1,S4_2,S4_3,S4_4 ,S5_1,S5_2,S5_3,S5_4 ,S6_1,S6_2,S6_3,S6_4 ,S7_1,S7_2,S7_3,S7_4 ,S8_1,S8_2,S8_3,S8_4,Service1,Service2,Service3,Service4,doorSensor;
    List<Button> SelectedButtons;
    List<MoodBtn> SelectedMoodButtons ;
    ITuyaDeviceMultiControl iTuyaDeviceMultiControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lighting_double_control);
        setActivity();
        setActivityActions();
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
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

        iTuyaDeviceMultiControl = TuyaHomeSdk.getDeviceMultiControlInstance();
        SelectedMoodButtons = new ArrayList<>();
        SelectedButtons = new ArrayList<>();
        doorSensor = findViewById(R.id.button19237Av);
        S1_1 = findViewById(R.id.button272);
        S1_2 = findViewById(R.id.button192);
        S1_3 = findViewById(R.id.button172);
        S1_4 = findViewById(R.id.button282);
        S2_1 = findViewById(R.id.button2723);
        S2_2 = findViewById(R.id.button1923);
        S2_3 = findViewById(R.id.button1723);
        S2_4 = findViewById(R.id.button2823);
        S3_1 = findViewById(R.id.button27236);
        S3_2 = findViewById(R.id.button19236);
        S3_3 = findViewById(R.id.button17236);
        S3_4 = findViewById(R.id.button28236);
        S4_1 = findViewById(R.id.button27237);
        S4_2 = findViewById(R.id.button19237);
        S4_3 = findViewById(R.id.button17237);
        S4_4 = findViewById(R.id.button28237);
        S5_1 = findViewById(R.id.button2725);
        S5_2 = findViewById(R.id.button192r);
        S5_3 = findViewById(R.id.button1726);
        S5_4 = findViewById(R.id.button2842);
        S6_1 = findViewById(R.id.button2723j);
        S6_2 = findViewById(R.id.button1923j);
        S6_3 = findViewById(R.id.button1723j);
        S6_4 = findViewById(R.id.button2823j);
        S7_1 = findViewById(R.id.button27236k);
        S7_2 = findViewById(R.id.button19236k);
        S7_3 = findViewById(R.id.button17236k);
        S7_4 = findViewById(R.id.button28236k);
        S8_1 = findViewById(R.id.button27237a);
        S8_2 = findViewById(R.id.button19237A);
        S8_3 = findViewById(R.id.button17237a);
        S8_4 = findViewById(R.id.button28237a);
        Service1 = findViewById(R.id.button27);
        Service2 = findViewById(R.id.button19);
        Service3 = findViewById(R.id.button17);
        Service4 = findViewById(R.id.button28);

        if (RoomManager.Room.getSWITCH1_B() != null ) {
            if (RoomManager.Room.getSWITCH1_B().dps.get("4") == null) {
                S1_4.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH1_B().dps.get("3") == null) {
                S1_3.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH1_B().dps.get("2") == null) {
                S1_2.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH1_B().dps.get("1") == null) {
                S1_1.setVisibility(View.INVISIBLE);
            }
        }
        else {
            S1_1.setVisibility(View.INVISIBLE);
            S1_2.setVisibility(View.INVISIBLE);
            S1_3.setVisibility(View.INVISIBLE);
            S1_4.setVisibility(View.INVISIBLE);
        }
        if (RoomManager.Room.getSWITCH2_B() != null ) {
            if (RoomManager.Room.getSWITCH2_B().dps.get("4") == null) {
                S2_4.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH2_B().dps.get("3") == null) {
                S2_3.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH2_B().dps.get("2") == null) {
                S2_2.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH2_B().dps.get("1") == null) {
                S2_1.setVisibility(View.INVISIBLE);
            }
        }
        else {
            S2_1.setVisibility(View.INVISIBLE);
            S2_2.setVisibility(View.INVISIBLE);
            S2_3.setVisibility(View.INVISIBLE);
            S2_4.setVisibility(View.INVISIBLE);
        }
        if (RoomManager.Room.getSWITCH3_B() != null ) {
            if (RoomManager.Room.getSWITCH3_B().dps.get("4") == null) {
                S3_4.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH3_B().dps.get("3") == null) {
                S3_3.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH3_B().dps.get("2") == null) {
                S3_2.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH3_B().dps.get("1") == null) {
                S3_1.setVisibility(View.INVISIBLE);
            }
        }
        else {
            S3_1.setVisibility(View.INVISIBLE);
            S3_2.setVisibility(View.INVISIBLE);
            S3_3.setVisibility(View.INVISIBLE);
            S3_4.setVisibility(View.INVISIBLE);
        }
        if (RoomManager.Room.getSWITCH4_B() != null ) {
            if (RoomManager.Room.getSWITCH4_B().dps.get("4") == null) {
                S4_4.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH4_B().dps.get("3") == null) {
                S4_3.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH4_B().dps.get("2") == null) {
                S4_2.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH4_B().dps.get("1") == null) {
                S4_1.setVisibility(View.INVISIBLE);
            }
        }
        else {
            S4_1.setVisibility(View.INVISIBLE);
            S4_2.setVisibility(View.INVISIBLE);
            S4_3.setVisibility(View.INVISIBLE);
            S4_4.setVisibility(View.INVISIBLE);
        }
        if (RoomManager.Room.getSWITCH5_B() != null ) {
            if (RoomManager.Room.getSWITCH5_B().dps.get("4") == null) {
                S5_4.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH5_B().dps.get("3") == null) {
                S5_3.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH5_B().dps.get("2") == null) {
                S5_2.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH5_B().dps.get("1") == null) {
                S5_1.setVisibility(View.INVISIBLE);
            }
        }
        else {
            S5_1.setVisibility(View.INVISIBLE);
            S5_2.setVisibility(View.INVISIBLE);
            S5_3.setVisibility(View.INVISIBLE);
            S5_4.setVisibility(View.INVISIBLE);
        }
        if (RoomManager.Room.getSWITCH6_B() != null ) {
            if (RoomManager.Room.getSWITCH6_B().dps.get("4") == null) {
                S6_4.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH6_B().dps.get("3") == null) {
                S6_3.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH6_B().dps.get("2") == null) {
                S6_2.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH6_B().dps.get("1") == null) {
                S6_1.setVisibility(View.INVISIBLE);
            }
        }
        else {
            S6_1.setVisibility(View.INVISIBLE);
            S6_2.setVisibility(View.INVISIBLE);
            S6_3.setVisibility(View.INVISIBLE);
            S6_4.setVisibility(View.INVISIBLE);
        }
        if (RoomManager.Room.getSWITCH7_B() != null ) {
            if (RoomManager.Room.getSWITCH7_B().dps.get("4") == null) {
                S7_4.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH7_B().dps.get("3") == null) {
                S7_3.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH7_B().dps.get("2") == null) {
                S7_2.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH7_B().dps.get("1") == null) {
                S7_1.setVisibility(View.INVISIBLE);
            }
        }
        else {
            S7_1.setVisibility(View.INVISIBLE);
            S7_2.setVisibility(View.INVISIBLE);
            S7_3.setVisibility(View.INVISIBLE);
            S7_4.setVisibility(View.INVISIBLE);
        }
        if (RoomManager.Room.getSWITCH8_B() != null ) {
            if (RoomManager.Room.getSWITCH8_B().dps.get("4") == null) {
                S8_4.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH8_B().dps.get("3") == null) {
                S8_3.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH8_B().dps.get("2") == null) {
                S8_2.setVisibility(View.INVISIBLE);
            }
            if (RoomManager.Room.getSWITCH8_B().dps.get("1") == null) {
                S8_1.setVisibility(View.INVISIBLE);
            }
        }
        else {
            S8_1.setVisibility(View.INVISIBLE);
            S8_2.setVisibility(View.INVISIBLE);
            S8_3.setVisibility(View.INVISIBLE);
            S8_4.setVisibility(View.INVISIBLE);
        }
        //setDevicesButtons();
    }

    void setActivityActions() {
        S1_1.setOnClickListener(setButtonListener(S1_1,RoomManager.Room.getSWITCH1_B(),1));
        S1_2.setOnClickListener(setButtonListener(S1_2,RoomManager.Room.getSWITCH1_B(),2));
        S1_3.setOnClickListener(setButtonListener(S1_3,RoomManager.Room.getSWITCH1_B(),3));
        S1_4.setOnClickListener(setButtonListener(S1_4,RoomManager.Room.getSWITCH1_B(),4));

        S2_1.setOnClickListener(setButtonListener(S2_1,RoomManager.Room.getSWITCH2_B(),1));
        S2_2.setOnClickListener(setButtonListener(S2_2,RoomManager.Room.getSWITCH2_B(),2));
        S2_3.setOnClickListener(setButtonListener(S2_3,RoomManager.Room.getSWITCH2_B(),3));
        S2_4.setOnClickListener(setButtonListener(S2_4,RoomManager.Room.getSWITCH2_B(),4));

        S3_1.setOnClickListener(setButtonListener(S3_1,RoomManager.Room.getSWITCH3_B(),1));
        S3_2.setOnClickListener(setButtonListener(S3_2,RoomManager.Room.getSWITCH3_B(),2));
        S3_3.setOnClickListener(setButtonListener(S3_3,RoomManager.Room.getSWITCH3_B(),3));
        S3_4.setOnClickListener(setButtonListener(S3_4,RoomManager.Room.getSWITCH3_B(),4));

        S4_1.setOnClickListener(setButtonListener(S4_1,RoomManager.Room.getSWITCH4_B(),1));
        S4_2.setOnClickListener(setButtonListener(S4_2,RoomManager.Room.getSWITCH4_B(),2));
        S4_3.setOnClickListener(setButtonListener(S4_3,RoomManager.Room.getSWITCH4_B(),3));
        S4_4.setOnClickListener(setButtonListener(S4_4,RoomManager.Room.getSWITCH4_B(),4));

        S5_1.setOnClickListener(setButtonListener(S5_1,RoomManager.Room.getSWITCH5_B(),1));
        S5_2.setOnClickListener(setButtonListener(S5_2,RoomManager.Room.getSWITCH5_B(),2));
        S5_3.setOnClickListener(setButtonListener(S5_3,RoomManager.Room.getSWITCH5_B(),3));
        S5_4.setOnClickListener(setButtonListener(S5_4,RoomManager.Room.getSWITCH5_B(),4));

        S6_1.setOnClickListener(setButtonListener(S6_1,RoomManager.Room.getSWITCH6_B(),1));
        S6_2.setOnClickListener(setButtonListener(S6_2,RoomManager.Room.getSWITCH6_B(),2));
        S6_3.setOnClickListener(setButtonListener(S6_3,RoomManager.Room.getSWITCH6_B(),3));
        S6_4.setOnClickListener(setButtonListener(S6_4,RoomManager.Room.getSWITCH6_B(),4));

        S7_1.setOnClickListener(setButtonListener(S7_1,RoomManager.Room.getSWITCH7_B(),1));
        S7_2.setOnClickListener(setButtonListener(S7_2,RoomManager.Room.getSWITCH7_B(),2));
        S7_3.setOnClickListener(setButtonListener(S7_3,RoomManager.Room.getSWITCH7_B(),3));
        S7_4.setOnClickListener(setButtonListener(S7_4,RoomManager.Room.getSWITCH7_B(),4));

        S8_1.setOnClickListener(setButtonListener(S8_1,RoomManager.Room.getSWITCH8_B(),1));
        S8_2.setOnClickListener(setButtonListener(S8_2,RoomManager.Room.getSWITCH8_B(),2));
        S8_3.setOnClickListener(setButtonListener(S8_3,RoomManager.Room.getSWITCH8_B(),3));
        S8_4.setOnClickListener(setButtonListener(S8_4,RoomManager.Room.getSWITCH8_B(),4));
    }

    void setDevicesButtons() {
        if (RoomManager.Room.getSWITCH1_B() != null ) {
            FirstDeviceList.add(RoomManager.Room.getSWITCH1_B());
            SecondDeviceList.add(RoomManager.Room.getSWITCH1_B());
        }
        if (RoomManager.Room.getSWITCH2_B() != null) {
            FirstDeviceList.add(RoomManager.Room.getSWITCH2_B());
            SecondDeviceList.add(RoomManager.Room.getSWITCH2_B());
        }
        if (RoomManager.Room.getSWITCH3_B() != null) {
            FirstDeviceList.add(RoomManager.Room.getSWITCH3_B());
            SecondDeviceList.add(RoomManager.Room.getSWITCH3_B());
        }
        if (RoomManager.Room.getSWITCH4_B() != null) {
            FirstDeviceList.add(RoomManager.Room.getSWITCH4_B());
            SecondDeviceList.add(RoomManager.Room.getSWITCH4_B());
        }
        if (RoomManager.Room.getSWITCH5_B() != null) {
            FirstDeviceList.add(RoomManager.Room.getSWITCH5_B());
            SecondDeviceList.add(RoomManager.Room.getSWITCH5_B());
        }
        if (RoomManager.Room.getSWITCH6_B() != null) {
            FirstDeviceList.add(RoomManager.Room.getSWITCH6_B());
            SecondDeviceList.add(RoomManager.Room.getSWITCH6_B());
        }
        if (RoomManager.Room.getSWITCH7_B() != null) {
            FirstDeviceList.add(RoomManager.Room.getSWITCH7_B());
            SecondDeviceList.add(RoomManager.Room.getSWITCH7_B());
        }
        if (RoomManager.Room.getSWITCH8_B() != null) {
            FirstDeviceList.add(RoomManager.Room.getSWITCH8_B());
            SecondDeviceList.add(RoomManager.Room.getSWITCH8_B());
        }
        if (RoomManager.Room.getSERVICE1_B() != null) {
            FirstDeviceList.add(RoomManager.Room.getSERVICE1_B());
            SecondDeviceList.add(RoomManager.Room.getSERVICE1_B());
        }
        fAdapter = new DoubleControlFirst_Adapter(FirstDeviceList);
        FirstDevicesRec.setAdapter(fAdapter);
        sAdapter = new DoubleControlSecond_Adapter(SecondDeviceList);
        SecondDevicesRec.setAdapter(sAdapter);
    }

    public void nextToSelectDps(View view) {
        if (FIRST == null ) {
            Toast.makeText(act,"select first device",Toast.LENGTH_SHORT).show();
            return;
        }
        if (SECOND == null ) {
            Toast.makeText(act,"select second device",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(act, DoubleControlSelectDps.class);
        startActivity(i);
    }

    View.OnClickListener setButtonListener(Button b,DeviceBean d,int dbId) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SelectedButtons.contains(b)) {
                    SelectedButtons.remove(b);
                    b.setBackgroundResource(R.drawable.btn_bg_selector);
                    for (MoodBtn mb :SelectedMoodButtons) {
                        if (mb.Switch == d) {
                            SelectedMoodButtons.remove(mb);
                            break;
                        }
                    }
                }
                else {
                    SelectedButtons.add(b);
                    b.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                    SelectedMoodButtons.add(new MoodBtn(d,dbId,true));
                }
            }
        };
    }

    void createMultiControl() {
        if (SelectedMoodButtons.size() == 0) {
            new MessageDialog(getResources().getString(R.string.pleaseSelectButtons),getResources().getString(R.string.selectButtons),act);
            return ;
        }
        if (SelectedMoodButtons.size() == 1) {
            new MessageDialog(getResources().getString(R.string.pleaseSelectOtherButtonsInMultiControl),getResources().getString(R.string.selectOtherButtons),act);
            return ;
        }

        Random r = new Random();
        int x = r.nextInt(30);
        JSONArray arr = new JSONArray();
        for (MoodBtn mb : SelectedMoodButtons) {
            JSONObject groupDetails = new JSONObject() ;
            try {
                groupDetails.put("devId", mb.Switch.devId);
                groupDetails.put("dpId", mb.SwitchButton);
                groupDetails.put("id", x);
                groupDetails.put("enable", true);
            } catch (JSONException e) {
                Toast.makeText(act,"failed "+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
            arr.put(groupDetails);
        }

            JSONObject multiControlBean = new JSONObject();
            try {
                multiControlBean.put("groupName", RoomManager.Room.RoomNumber + "Lighting" + x);
                multiControlBean.put("groupType", 1);
                multiControlBean.put("groupDetail", arr);
                multiControlBean.put("id", x);
            } catch (JSONException e) {
                Toast.makeText(act,"failed "+e.getMessage(),Toast.LENGTH_SHORT).show();
            }

            iTuyaDeviceMultiControl.saveDeviceMultiControl(MyApp.HOME.getHomeId(), multiControlBean.toString(), new ITuyaResultCallback<MultiControlBean>() {
                @Override
                public void onSuccess(MultiControlBean result) {
                    Toast.makeText(act,"double control created",Toast.LENGTH_SHORT).show();
                    Log.d("switch1Dp1", result.getGroupName());
                    iTuyaDeviceMultiControl.enableMultiControl(x, new ITuyaResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            Log.d("switch1Dp1", result.toString());
                            Toast.makeText(act,"double control enabled",Toast.LENGTH_SHORT).show();
                            for (Button b : SelectedButtons) {
                                b.setBackgroundResource(R.drawable.btn_bg_selector);
                            }
                            SelectedButtons.clear();
                            SelectedMoodButtons.clear();
                        }

                        @Override
                        public void onError(String errorCode, String errorMessage) {
                            Log.d("switch1Dp1", errorMessage);
                            Toast.makeText(act,"failed "+errorMessage,Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(String errorCode, String errorMessage) {
                    Toast.makeText(act,"failed "+errorMessage + " " +errorCode,Toast.LENGTH_SHORT).show();
                    Log.d("switch1Dp1", errorMessage + "here "+errorCode+" "+x);
                }
            });
    }

    public void create(View view) {
        createMultiControl();
    }
}