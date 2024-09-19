package com.example.mobilecheckdevice;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.bean.scene.SceneCondition;
import com.tuya.smart.home.sdk.bean.scene.SceneTask;
import com.tuya.smart.home.sdk.bean.scene.condition.rule.BoolRule;
import com.tuya.smart.home.sdk.bean.scene.condition.rule.EnumRule;
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MakeMood extends AppCompatActivity {

    Activity act ;
    String modeName ;
    Button S1_1,S1_2,S1_3,S1_4,S2_1,S2_2,S2_3,S2_4,S3_1,S3_2,S3_3,S3_4,S4_1,S4_2,S4_3,S4_4 ,S5_1,S5_2,S5_3,S5_4 ,S6_1,S6_2,S6_3,S6_4 ,S7_1,S7_2,S7_3,S7_4 ,S8_1,S8_2,S8_3,S8_4,Shutter1Open,Shutter1Close,Shutter2Open,Shutter2Close,Shutter3Open,Shutter3Close,Service1,Service2,Service3,Service4,doorSensor,doorSensorClose,AC,AddDelay;
    List<Button> SelectedConditionButtons;
    List<MoodBtn> ConditionMoodButtons;
    List<Button> SelectedTaskButtons;
    List<MoodBtn> TaskMoodButtons;
    SwitchCompat PhysicalButton;
    Spinner Seconds,Minutes;
    TextView DelayCaption;
    LinearLayout delayLayout,addedDelayLayout;
    int powerId;
    RadioButton OR , AND;
    int Selected_MATCH_TYPE = SceneBean.MATCH_TYPE_OR ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_mood);
        modeName = getIntent().getExtras().getString("ModeName");
        setActivity();
        setActivityActions();
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
    }

    void setActivity() {
        act = this ;
        TextView ModeName = findViewById(R.id.textView68);
        ModeName.setText(modeName);
        LinearLayout AndOr = findViewById(R.id.andOrLayout);
        AndOr.setVisibility(View.GONE);
        addedDelayLayout = findViewById(R.id.addedDelayLayout);
        delayLayout = findViewById(R.id.delayLayoutContent);
        delayLayout.setVisibility(View.GONE);
        DelayCaption = findViewById(R.id.textView76);
        AddDelay = findViewById(R.id.button48);
        OR = findViewById(R.id.radioButton6);
        AND = findViewById(R.id.radioButton5);
        SelectedTaskButtons = new ArrayList<>();
        TaskMoodButtons = new ArrayList<>();
        SelectedConditionButtons = new ArrayList<>();
        ConditionMoodButtons = new ArrayList<>();
        PhysicalButton = findViewById(R.id.switch1);
        doorSensor = findViewById(R.id.button19237Av);
        doorSensorClose = findViewById(R.id.button31);

        String[] secondsArr = new String[60];
        String[] minutesArr = new String[61];
        for (int i=0;i<secondsArr.length;i++) {
            secondsArr[i] = String.valueOf(i+1);
        }
        for (int i=0;i<minutesArr.length;i++) {
            minutesArr[i] = String.valueOf(i);
        }
        Seconds = findViewById(R.id.spinner5);
        Seconds.setAdapter(new ArrayAdapter<>(act,R.layout.spinners_item,secondsArr));
        Minutes = findViewById(R.id.spinner6);
        Minutes.setAdapter(new ArrayAdapter<>(act,R.layout.spinners_item,minutesArr));

        AC = findViewById(R.id.button19237Av0);
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

        Shutter1Open = findViewById(R.id.button19237Ac);
        Shutter1Close = findViewById(R.id.button17237ad);

        Shutter2Open = findViewById(R.id.button19237Ac00);
        Shutter2Close = findViewById(R.id.button17237ad00);

        Shutter3Open = findViewById(R.id.button19237Ac1);
        Shutter3Close = findViewById(R.id.button17237ad1);

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
        if (RoomManager.Room.getAC_B() != null) {
            AC.setVisibility(View.VISIBLE);
            TuyaHomeSdk.getSceneManagerInstance().getDeviceConditionOperationList(RoomManager.Room.getAC_B().devId, new ITuyaResultCallback<List<TaskListBean>>() {
                @Override
                public void onSuccess(List<TaskListBean> result) {
                    for (int i=0 ; i<result.size();i++) {
                        if (result.get(i).getName().equals("Power") || result.get(i).getName().equals("switch") || result.get(i).getName().equals("Switch")) {
                            powerId = (int) result.get(i).getDpId();
                            Log.d("acPower",powerId+"");
                            break;
                        }
                    }
                }
                @Override
                public void onError(String errorCode, String errorMessage) {

                }
            });
        }
        else {
            AC.setVisibility(View.INVISIBLE);
        }
        if (RoomManager.Room.getCURTAIN_B() != null) {
            LinearLayout curtainLayout = findViewById(R.id.CurtainLayout);
            curtainLayout.setVisibility(View.VISIBLE);
            getCurtainDetails(RoomManager.Room.getCURTAIN_B(),curtainLayout);
        }
        else {
            LinearLayout curtainLayout = findViewById(R.id.CurtainLayout);
            curtainLayout.setVisibility(View.GONE);
        }
        if (RoomManager.Room.getSHUTTER1() == null) {
            LinearLayout shutterLayout = findViewById(R.id.shutter1Layout);
            shutterLayout.setVisibility(View.GONE);
        }
        else {
            LinearLayout shutterLayout = findViewById(R.id.shutter1Layout);
            shutterLayout.setVisibility(View.VISIBLE);
            if (RoomManager.Room.getSHUTTER1().dps.get("1") == null) {
                Shutter1Close.setVisibility(View.GONE);
            }
            if (RoomManager.Room.getSHUTTER1().dps.get("2") == null) {
                Shutter1Open.setVisibility(View.GONE);
            }
        }
        if (RoomManager.Room.getSHUTTER2() == null) {
            LinearLayout shutterLayout = findViewById(R.id.shutter2Layout);
            shutterLayout.setVisibility(View.GONE);
        }
        else {
            LinearLayout shutterLayout = findViewById(R.id.shutter2Layout);
            shutterLayout.setVisibility(View.VISIBLE);
            if (RoomManager.Room.getSHUTTER2().dps.get("1") == null) {
                Shutter2Close.setVisibility(View.GONE);
            }
            if (RoomManager.Room.getSHUTTER2().dps.get("2") == null) {
                Shutter2Open.setVisibility(View.GONE);
            }
        }
        if (RoomManager.Room.getSHUTTER3() == null) {
            LinearLayout shutterLayout = findViewById(R.id.shutter3Layout);
            shutterLayout.setVisibility(View.GONE);
        }
        else {
            LinearLayout shutterLayout = findViewById(R.id.shutter3Layout);
            shutterLayout.setVisibility(View.VISIBLE);
            if (RoomManager.Room.getSHUTTER3().dps.get("1") == null) {
                Shutter3Close.setVisibility(View.GONE);
            }
            if (RoomManager.Room.getSHUTTER3().dps.get("2") == null) {
                Shutter3Open.setVisibility(View.GONE);
            }
        }
        if (RoomManager.Room.getDOORSENSOR_B() != null) {
            LinearLayout doorSensorLayout = findViewById(R.id.DoorSensorLayout);
            doorSensorLayout.setVisibility(View.VISIBLE);
        }
        else {
            LinearLayout doorSensorLayout = findViewById(R.id.DoorSensorLayout);
            doorSensorLayout.setVisibility(View.GONE);
        }
        switch (MyApp.ProjectVariables.cleanupButton) {
            case 1 :
                Service1.setText(getResources().getString(R.string.cleanup));
                break;
            case 2 :
                Service2.setText(getResources().getString(R.string.cleanup));
                break;
            case 3 :
                Service3.setText(getResources().getString(R.string.cleanup));
                break;
            case 4 :
                Service4.setText(getResources().getString(R.string.cleanup));
                break;
        }
        switch (MyApp.ProjectVariables.laundryButton) {
            case 1 :
                Service1.setText(getResources().getString(R.string.laundry));
                break;
            case 2 :
                Service2.setText(getResources().getString(R.string.laundry));
                break;
            case 3 :
                Service3.setText(getResources().getString(R.string.laundry));
                break;
            case 4 :
                Service4.setText(getResources().getString(R.string.laundry));
                break;
        }
        switch (MyApp.ProjectVariables.checkoutButton) {
            case 1 :
                Service1.setText(getResources().getString(R.string.checkout));
                break;
            case 2 :
                Service2.setText(getResources().getString(R.string.checkout));
                break;
            case 3 :
                Service3.setText(getResources().getString(R.string.checkout));
                break;
            case 4 :
                Service4.setText(getResources().getString(R.string.checkout));
                break;
        }
        switch (MyApp.ProjectVariables.dndButton) {
            case 1 :
                Service1.setText(getResources().getString(R.string.dnd));
                break;
            case 2 :
                Service2.setText(getResources().getString(R.string.dnd));
                break;
            case 3 :
                Service3.setText(getResources().getString(R.string.dnd));
                break;
            case 4 :
                Service4.setText(getResources().getString(R.string.dnd));
                break;
        }
    }

    void setActivityActions() {
        AddDelay.setOnClickListener(view -> {
            int seconds = Integer.parseInt(Seconds.getSelectedItem().toString());
            int minutes = Integer.parseInt(Minutes.getSelectedItem().toString());
            MoodBtn mb = new MoodBtn(minutes,seconds);
            TaskMoodButtons.add(mb);
            Button b = new Button(act);
            b.setText(MessageFormat.format("delay {0} m :{1} s", minutes, seconds));
            addedDelayLayout.addView(b);
            b.setBackgroundResource(R.drawable.button_red);
            b.setPadding(5,0,5,0);
            SelectedTaskButtons.add(b);
            b.setOnClickListener(view1 -> {
                if (SelectedTaskButtons.contains(b)) {
                    SelectedTaskButtons.remove(b);
                    addedDelayLayout.removeView(b);
                    for (int i = 0; i< TaskMoodButtons.size(); i++) {
                        if (TaskMoodButtons.get(i) == mb) {
                            TaskMoodButtons.remove(i);
                            break;
                        }
                    }
                }
            });
        });
        DelayCaption.setOnClickListener(view -> {
            if (delayLayout.getVisibility() == View.VISIBLE) {
                delayLayout.setVisibility(View.GONE);
            }
            else {
                delayLayout.setVisibility(View.VISIBLE);
            }
        });
        OR.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                AND.setChecked(false);
                Selected_MATCH_TYPE = SceneBean.MATCH_TYPE_OR;
            }
        });
        AND.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                OR.setChecked(false);
                Selected_MATCH_TYPE = SceneBean.MATCH_TYPE_AND;
            }
        });
        OR.setChecked(true);

        PhysicalButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            LinearLayout AndOr = findViewById(R.id.andOrLayout);
            LinearLayout DelayLayout = findViewById(R.id.delayLayout);
            if (isChecked) {
                AndOr.setVisibility(View.VISIBLE);
                DelayLayout.setVisibility(View.GONE);
            }
            else {
                AndOr.setVisibility(View.GONE);
                DelayLayout.setVisibility(View.VISIBLE);
            }
        });

        doorSensor.setOnClickListener(view -> {
            if (!PhysicalButton.isChecked()) {
                new MessageDialog("door sensor must be the condition please turn the switch on ","switch on",act);
                return;
            }
            try {
                Log.d("doorSensor", RoomManager.Room.getDOORSENSOR_B().dps.keySet().toArray()[0].toString());
                int x = Integer.parseInt(RoomManager.Room.getDOORSENSOR_B().dps.keySet().toArray()[0].toString());
                createPhysicalButtonStatusDialogSelector(act, "Door Sensor ", doorSensor, RoomManager.Room.getDOORSENSOR_B(), x);
            }
            catch (Exception e) {
                new MessageDialog(e.getMessage(),"error",act);
            }
        });
        doorSensorClose.setOnClickListener(view->{
            if (!PhysicalButton.isChecked()) {
                new MessageDialog("door sensor must be the condition please turn the switch on ","switch on",act);
                return;
            }
            try {
                Log.d("doorSensor", RoomManager.Room.getDOORSENSOR_B().dps.keySet().toArray()[0].toString());
                int x = Integer.parseInt(RoomManager.Room.getDOORSENSOR_B().dps.keySet().toArray()[0].toString());
                createPhysicalButtonStatusDialogSelector(act, "Door Sensor ", doorSensor, RoomManager.Room.getDOORSENSOR_B(), x);
            }
            catch (Exception e) {
                new MessageDialog(e.getMessage(),"error",act);
            }
        });

        S1_1.setOnClickListener(createButtonClickListener(act,"Switch 1 Button 1",S1_1,RoomManager.Room.getSWITCH1_B(),1));
        S1_2.setOnClickListener(createButtonClickListener(act,"Switch 1 Button 2",S1_2,RoomManager.Room.getSWITCH1_B(),2));
        S1_3.setOnClickListener(createButtonClickListener(act,"Switch 1 Button 3",S1_3,RoomManager.Room.getSWITCH1_B(),3));
        S1_4.setOnClickListener(createButtonClickListener(act,"Switch 1 Button 4",S1_4,RoomManager.Room.getSWITCH1_B(),4));

        S2_1.setOnClickListener(createButtonClickListener(act,"Switch 2 Button 1",S2_1,RoomManager.Room.getSWITCH2_B(),1));
        S2_2.setOnClickListener(createButtonClickListener(act,"Switch 2 Button 2",S2_2,RoomManager.Room.getSWITCH2_B(),2));
        S2_3.setOnClickListener(createButtonClickListener(act,"Switch 2 Button 3",S2_3,RoomManager.Room.getSWITCH2_B(),3));
        S2_4.setOnClickListener(createButtonClickListener(act,"Switch 2 Button 4",S2_4,RoomManager.Room.getSWITCH2_B(),4));

        S3_1.setOnClickListener(createButtonClickListener(act,"Switch 3 Button 1",S3_1,RoomManager.Room.getSWITCH3_B(),1));
        S3_2.setOnClickListener(createButtonClickListener(act,"Switch 3 Button 2",S3_2,RoomManager.Room.getSWITCH3_B(),2));
        S3_3.setOnClickListener(createButtonClickListener(act,"Switch 3 Button 3",S3_3,RoomManager.Room.getSWITCH3_B(),3));
        S3_4.setOnClickListener(createButtonClickListener(act,"Switch 3 Button 4",S3_4,RoomManager.Room.getSWITCH3_B(),4));

        S4_1.setOnClickListener(createButtonClickListener(act,"Switch 4 Button 1",S4_1,RoomManager.Room.getSWITCH4_B(),1));
        S4_2.setOnClickListener(createButtonClickListener(act,"Switch 4 Button 2",S4_2,RoomManager.Room.getSWITCH4_B(),2));
        S4_3.setOnClickListener(createButtonClickListener(act,"Switch 4 Button 3",S4_3,RoomManager.Room.getSWITCH4_B(),3));
        S4_4.setOnClickListener(createButtonClickListener(act,"Switch 4 Button 4",S4_4,RoomManager.Room.getSWITCH4_B(),4));

        S5_1.setOnClickListener(createButtonClickListener(act,"Switch 5 Button 1",S5_1,RoomManager.Room.getSWITCH5_B(),1));
        S5_2.setOnClickListener(createButtonClickListener(act,"Switch 5 Button 2",S5_2,RoomManager.Room.getSWITCH5_B(),2));
        S5_3.setOnClickListener(createButtonClickListener(act,"Switch 5 Button 3",S5_3,RoomManager.Room.getSWITCH5_B(),3));
        S5_4.setOnClickListener(createButtonClickListener(act,"Switch 5 Button 4",S5_4,RoomManager.Room.getSWITCH5_B(),4));

        S6_1.setOnClickListener(createButtonClickListener(act,"Switch 6 Button 1",S6_1,RoomManager.Room.getSWITCH6_B(),1));
        S6_2.setOnClickListener(createButtonClickListener(act,"Switch 6 Button 2",S6_2,RoomManager.Room.getSWITCH6_B(),2));
        S6_3.setOnClickListener(createButtonClickListener(act,"Switch 6 Button 3",S6_3,RoomManager.Room.getSWITCH6_B(),3));
        S6_4.setOnClickListener(createButtonClickListener(act,"Switch 6 Button 4",S6_4,RoomManager.Room.getSWITCH6_B(),4));

        S7_1.setOnClickListener(createButtonClickListener(act,"Switch 7 Button 1",S7_1,RoomManager.Room.getSWITCH7_B(),1));
        S7_2.setOnClickListener(createButtonClickListener(act,"Switch 7 Button 2",S7_2,RoomManager.Room.getSWITCH7_B(),2));
        S7_3.setOnClickListener(createButtonClickListener(act,"Switch 7 Button 3",S7_3,RoomManager.Room.getSWITCH7_B(),3));
        S7_4.setOnClickListener(createButtonClickListener(act,"Switch 7 Button 4",S7_4,RoomManager.Room.getSWITCH7_B(),4));

        S8_1.setOnClickListener(createButtonClickListener(act,"Switch 8 Button 1",S8_1,RoomManager.Room.getSWITCH8_B(),1));
        S8_2.setOnClickListener(createButtonClickListener(act,"Switch 8 Button 2",S8_2,RoomManager.Room.getSWITCH8_B(),2));
        S8_3.setOnClickListener(createButtonClickListener(act,"Switch 8 Button 3",S8_3,RoomManager.Room.getSWITCH8_B(),3));
        S8_4.setOnClickListener(createButtonClickListener(act,"Switch 8 Button 4",S8_4,RoomManager.Room.getSWITCH8_B(),4));

        Shutter1Open.setOnClickListener(createButtonClickListener(act,"Shutter1 Button open", Shutter1Open,RoomManager.Room.getSHUTTER1(),2));
        Shutter1Close.setOnClickListener(createButtonClickListener(act,"Shutter1 Button close", Shutter1Close,RoomManager.Room.getSHUTTER1(),1));

        Shutter2Open.setOnClickListener(createButtonClickListener(act,"Shutter2 Button open", Shutter2Open,RoomManager.Room.getSHUTTER2(),2));
        Shutter2Close.setOnClickListener(createButtonClickListener(act,"Shutter2 Button close", Shutter2Close,RoomManager.Room.getSHUTTER2(),1));

        Shutter3Open.setOnClickListener(createButtonClickListener(act,"Shutter3 Button open", Shutter3Open,RoomManager.Room.getSHUTTER3(),2));
        Shutter3Close.setOnClickListener(createButtonClickListener(act,"Shutter3 Button close", Shutter3Close,RoomManager.Room.getSHUTTER3(),1));

        Service1.setOnClickListener(v -> {
            if (Service1.getText().toString().equals("DND")) {
                if (SelectedTaskButtons.contains(Service1)) {
                    SelectedTaskButtons.remove(Service1);
                    Service1.setBackgroundResource(R.drawable.btn_bg_selector);
                    for (int i = 0; i< TaskMoodButtons.size(); i++) {
                        if (TaskMoodButtons.get(i).Switch == RoomManager.Room.getSERVICE1_B() && TaskMoodButtons.get(i).SwitchButton == MyApp.ProjectVariables.dndButton) {
                            TaskMoodButtons.remove(i);
                            break;
                        }
                    }
                }
                else {
                    Dialog D = new Dialog(act);
                    D.setContentView(R.layout.mood_button_status);
                    TextView title = D.findViewById(R.id.textView69);
                    RadioButton on = D.findViewById(R.id.radioButton);
                    RadioButton off = D.findViewById(R.id.radioButton2);
                    title.setText(getResources().getString(R.string.dnd));
                    on.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        SelectedTaskButtons.add(Service1);
                        Service1.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                        TaskMoodButtons.add(new MoodBtn(RoomManager.Room.getSERVICE1_B(),MyApp.ProjectVariables.dndButton,true));
                        D.dismiss();
                    });
                    off.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        SelectedTaskButtons.add(Service1);
                        Service1.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                        TaskMoodButtons.add(new MoodBtn(RoomManager.Room.getSERVICE1_B(),MyApp.ProjectVariables.dndButton,false));
                        D.dismiss();
                    });
                    D.show();
                }
            }
        });
        Service2.setOnClickListener(v -> {
            if (Service2.getText().toString().equals("DND")) {
                if (SelectedTaskButtons.contains(Service2)) {
                    SelectedTaskButtons.remove(Service2);
                    Service2.setBackgroundResource(R.drawable.btn_bg_selector);
                    for (int i = 0; i< TaskMoodButtons.size(); i++) {
                        if (TaskMoodButtons.get(i).Switch == RoomManager.Room.getSERVICE1_B() && TaskMoodButtons.get(i).SwitchButton == MyApp.ProjectVariables.dndButton) {
                            TaskMoodButtons.remove(i);
                            break;
                        }
                    }
                }
                else {
                    Dialog D = new Dialog(act);
                    D.setContentView(R.layout.mood_button_status);
                    TextView title = D.findViewById(R.id.textView69);
                    RadioButton on = D.findViewById(R.id.radioButton);
                    RadioButton off = D.findViewById(R.id.radioButton2);
                    title.setText(getResources().getString(R.string.dnd));
                    on.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        SelectedTaskButtons.add(Service2);
                        Service2.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                        TaskMoodButtons.add(new MoodBtn(RoomManager.Room.getSERVICE1_B(),MyApp.ProjectVariables.dndButton,true));
                        D.dismiss();
                    });
                    off.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        SelectedTaskButtons.add(Service2);
                        Service2.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                        TaskMoodButtons.add(new MoodBtn(RoomManager.Room.getSERVICE1_B(),MyApp.ProjectVariables.dndButton,false));
                        D.dismiss();
                    });
                    D.show();
                }
            }
        });
        Service3.setOnClickListener(v -> {
            if (Service3.getText().toString().equals("DND")) {
                if (SelectedTaskButtons.contains(Service3)) {
                    SelectedTaskButtons.remove(Service3);
                    Service3.setBackgroundResource(R.drawable.btn_bg_selector);
                    for (int i = 0; i< TaskMoodButtons.size(); i++) {
                        if (TaskMoodButtons.get(i).Switch == RoomManager.Room.getSERVICE1_B() && TaskMoodButtons.get(i).SwitchButton == MyApp.ProjectVariables.dndButton) {
                            TaskMoodButtons.remove(i);
                            break;
                        }
                    }
                }
                else {
                    Dialog D = new Dialog(act);
                    D.setContentView(R.layout.mood_button_status);
                    TextView title = D.findViewById(R.id.textView69);
                    RadioButton on = D.findViewById(R.id.radioButton);
                    RadioButton off = D.findViewById(R.id.radioButton2);
                    title.setText(getResources().getString(R.string.dnd));
                    on.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        SelectedTaskButtons.add(Service3);
                        Service3.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                        TaskMoodButtons.add(new MoodBtn(RoomManager.Room.getSERVICE1_B(),MyApp.ProjectVariables.dndButton,true));
                        D.dismiss();
                    });
                    off.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        SelectedTaskButtons.add(Service3);
                        Service3.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                        TaskMoodButtons.add(new MoodBtn(RoomManager.Room.getSERVICE1_B(),MyApp.ProjectVariables.dndButton,false));
                        D.dismiss();
                    });
                    D.show();
                }
            }
        });
        Service4.setOnClickListener(v -> {
            if (Service4.getText().toString().equals("DND")) {
                if (SelectedTaskButtons.contains(Service4)) {
                    SelectedTaskButtons.remove(Service4);
                    Service4.setBackgroundResource(R.drawable.btn_bg_selector);
                    for (int i = 0; i< TaskMoodButtons.size(); i++) {
                        if (TaskMoodButtons.get(i).Switch == RoomManager.Room.getSERVICE1_B() && TaskMoodButtons.get(i).SwitchButton == MyApp.ProjectVariables.dndButton) {
                            TaskMoodButtons.remove(i);
                            break;
                        }
                    }
                }
                else {
                    Dialog D = new Dialog(act);
                    D.setContentView(R.layout.mood_button_status);
                    TextView title = D.findViewById(R.id.textView69);
                    RadioButton on = D.findViewById(R.id.radioButton);
                    RadioButton off = D.findViewById(R.id.radioButton2);
                    title.setText(getResources().getString(R.string.dnd));
                    on.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        SelectedTaskButtons.add(Service4);
                        Service4.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                        TaskMoodButtons.add(new MoodBtn(RoomManager.Room.getSERVICE1_B(),MyApp.ProjectVariables.dndButton,true));
                        D.dismiss();
                    });
                    off.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        SelectedTaskButtons.add(Service4);
                        Service4.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                        TaskMoodButtons.add(new MoodBtn(RoomManager.Room.getSERVICE1_B(),MyApp.ProjectVariables.dndButton,false));
                        D.dismiss();
                    });
                    D.show();
                }
            }
        });

        AC.setOnClickListener(createButtonClickListener(act,"AC Power",AC,RoomManager.Room.getAC_B(),powerId));
    }

    public void createMod(View view) {
        if (TaskMoodButtons.size() == 0) {
            new MessageDialog("please select buttons first","No Buttons",act);
            return ;
        }
        LoadingDialog loading = new LoadingDialog(act);
        List<SceneCondition> condS = null;
        List<SceneTask> tasks = new ArrayList<>();
        if (ConditionMoodButtons.size() > 0) {
            condS = new ArrayList<>();
            for (int i=0;i<ConditionMoodButtons.size();i++) {
                if (ConditionMoodButtons.get(i).statusString == null) {
                    BoolRule rule = BoolRule.newInstance("dp"+ConditionMoodButtons.get(i).SwitchButton, ConditionMoodButtons.get(i).status);
                    SceneCondition cond = SceneCondition.createDevCondition(ConditionMoodButtons.get(i).Switch, String.valueOf(ConditionMoodButtons.get(i).SwitchButton),rule);
                    condS.add(cond);
                }
                else {
                    EnumRule rr = EnumRule.newInstance("dp"+ConditionMoodButtons.get(i).SwitchButton,ConditionMoodButtons.get(i).statusString);
                    SceneCondition cond = SceneCondition.createDevCondition(ConditionMoodButtons.get(i).Switch, String.valueOf(ConditionMoodButtons.get(i).SwitchButton),rr);
                    condS.add(cond);
                }
            }
        }
        for (int i = 0; i< TaskMoodButtons.size(); i++) {
            HashMap<String, Object> taskMap = new HashMap<>();
            if (TaskMoodButtons.get(i).delay) {
                SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDelayTask(TaskMoodButtons.get(i).minutes,TaskMoodButtons.get(i).seconds);
                tasks.add(task);
            }
            else if (TaskMoodButtons.get(i).statusString == null) {
                taskMap.put(String.valueOf(TaskMoodButtons.get(i).SwitchButton), TaskMoodButtons.get(i).status);
                SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(TaskMoodButtons.get(i).Switch.devId, taskMap);
                tasks.add(task);
            }
            else {
                taskMap.put(String.valueOf(TaskMoodButtons.get(i).SwitchButton), TaskMoodButtons.get(i).statusString);
                SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(TaskMoodButtons.get(i).Switch.devId, taskMap);
                tasks.add(task);
            }
        }
        TuyaHomeSdk.getSceneManagerInstance().createScene(
                RoomManager.Room.Home.getHomeId(),
                RoomManager.Room.RoomNumber+modeName,
                false,
                RoomManager.IMAGES.get(0),
                condS,
                tasks,
                null,
                Selected_MATCH_TYPE,
                new ITuyaResultCallback<SceneBean>() {
                    @Override
                    public void onSuccess(SceneBean sceneBean) {
                        Log.d("MoodCreation", "create Scene Success");
                        Moods.MoodsScenes.add(sceneBean);
                        TuyaHomeSdk.newSceneInstance(sceneBean.getId()).enableScene(sceneBean.getId(), new IResultCallback() {
                            @Override
                            public void onSuccess() {
                                loading.stop();
                                Log.d("MoodCreation", "enable Scene Success");
                                RoomManager.MY_SCENES.add(sceneBean);
                                MyApp.SCENES.add(sceneBean);
                                new MessageDialog("Scene created","Done",act,true);
                            }
                            @Override
                            public void onError(String errorCode, String errorMessage) {
                                loading.stop();
                                Log.d("MoodCreation", errorMessage + " " + errorCode);
                                new MessageDialog(errorMessage + " " + errorCode,"Failed",act);

                            }
                        });
                    }
                    @Override
                    public void onError(String errorCode, String errorMessage) {
                        loading.stop();
                        Log.d("MoodCreation", errorMessage + " " + errorCode);
                        new MessageDialog(errorMessage + " " + errorCode,"Failed",act);
                    }
                });
//        ROOM.getRoomHome(RoomManager.Room, MyApp.ProjectHomes, new HomeBeanCallBack() {
//            @Override
//            public void onSuccess(HomeBean homeBean) {
//                List<SceneCondition> condS = null;
//                List<SceneTask> tasks = new ArrayList<>();
//                if (ConditionMoodButtons.size() > 0) {
//                    condS = new ArrayList<>();
//                    for (int i=0;i<ConditionMoodButtons.size();i++) {
//                        if (ConditionMoodButtons.get(i).statusString == null) {
//                            BoolRule rule = BoolRule.newInstance("dp"+ConditionMoodButtons.get(i).SwitchButton, ConditionMoodButtons.get(i).status);
//                            SceneCondition cond = SceneCondition.createDevCondition(ConditionMoodButtons.get(i).Switch, String.valueOf(ConditionMoodButtons.get(i).SwitchButton),rule);
//                            condS.add(cond);
//                        }
//                        else {
//                            EnumRule rr = EnumRule.newInstance("dp"+ConditionMoodButtons.get(i).SwitchButton,ConditionMoodButtons.get(i).statusString);
//                            SceneCondition cond = SceneCondition.createDevCondition(ConditionMoodButtons.get(i).Switch, String.valueOf(ConditionMoodButtons.get(i).SwitchButton),rr);
//                            condS.add(cond);
//                        }
//                    }
//                }
//                for (int i = 0; i< TaskMoodButtons.size(); i++) {
//                    HashMap<String, Object> taskMap = new HashMap<>();
//                    if (TaskMoodButtons.get(i).statusString == null) {
//                        taskMap.put(String.valueOf(TaskMoodButtons.get(i).SwitchButton), TaskMoodButtons.get(i).status);
//                        SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(TaskMoodButtons.get(i).Switch.devId, taskMap);
//                        tasks.add(task);
//                    }
//                    else {
//                        taskMap.put(String.valueOf(TaskMoodButtons.get(i).SwitchButton), TaskMoodButtons.get(i).statusString);
//                        SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(TaskMoodButtons.get(i).Switch.devId, taskMap);
//                        tasks.add(task);
//                    }
//                }
//                TuyaHomeSdk.getSceneManagerInstance().createScene(
//                        homeBean.getHomeId(),
//                        RoomManager.Room.RoomNumber+modeName,
//                        false,
//                        RoomManager.IMAGES.get(0),
//                        condS,
//                        tasks,
//                        null,
//                        Selected_MATCH_TYPE,
//                        new ITuyaResultCallback<SceneBean>() {
//                            @Override
//                            public void onSuccess(SceneBean sceneBean) {
//                                Log.d("MoodCreation", "create Scene Success");
//                                Moods.MoodsScenes.add(sceneBean);
//                                TuyaHomeSdk.newSceneInstance(sceneBean.getId()).enableScene(sceneBean.getId(), new IResultCallback() {
//                                    @Override
//                                    public void onSuccess() {
//                                        loading.stop();
//                                        Log.d("MoodCreation", "enable Scene Success");
//                                        RoomManager.MY_SCENES.add(sceneBean);
//                                        MyApp.SCENES.add(sceneBean);
//                                        new MessageDialog("Scene created","Done",act,true);
//                                    }
//                                    @Override
//                                    public void onError(String errorCode, String errorMessage) {
//                                        loading.stop();
//                                        Log.d("MoodCreation", errorMessage + " " + errorCode);
//                                        new MessageDialog(errorMessage + " " + errorCode,"Failed",act);
//
//                                    }
//                                });
//                            }
//                            @Override
//                            public void onError(String errorCode, String errorMessage) {
//                                loading.stop();
//                                Log.d("MoodCreation", errorMessage + " " + errorCode);
//                                new MessageDialog(errorMessage + " " + errorCode,"Failed",act);
//                            }
//                        });
//            }
//
//            @Override
//            public void onFail(String error) {
//                loading.stop();
//                new MessageDialog(error,"Failed",act);
//            }
//        });
    }

    void createButtonStatusDialogSelector(Activity act, String titleText, Button bb, DeviceBean d,int buttonNumber) {
        if (d.getCategoryCode().equals("zig_wxkg")) {
            new MessageDialog("this device should be a condition not an action","must be condition",act);
        }
        else if (d.getCategoryCode().equals("zig_cl")) {
            Dialog D = new Dialog(act);
            D.setContentView(R.layout.moos_dp_value_dialog);
            TextView title = D.findViewById(R.id.textView69);
            title.setText(titleText);
            LinearLayout options = D.findViewById(R.id.optionsLayout);
            TuyaHomeSdk.getSceneManagerInstance().getDeviceConditionOperationList(d.devId, new ITuyaResultCallback<List<TaskListBean>>() {
                @Override
                public void onSuccess(List<TaskListBean> result) {
                    for (TaskListBean t:result) {
                        if (buttonNumber == t.getDpId()) {
                            Object[] keys = t.getTasks().keySet().toArray();
                            for (Object o:keys) {
                                RadioButton r = new RadioButton(act);
                                r.setText(String.valueOf(o));
                                r.setTextColor(getResources().getColor(R.color.white,null));
                                r.setOnCheckedChangeListener((compoundButton, b) -> {
                                    if (b) {
                                        SelectedTaskButtons.add(bb);
                                        bb.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                        TaskMoodButtons.add(new MoodBtn(d,buttonNumber,String.valueOf(o)));
                                        D.dismiss();
                                    }
                                });
                                options.addView(r);
                            }
                            D.show();
                        }
                    }
                }

                @Override
                public void onError(String errorCode, String errorMessage) {

                }
            });
        }
        else {
            Dialog D = new Dialog(act);
            D.setContentView(R.layout.mood_button_status);
            TextView title = D.findViewById(R.id.textView69);
            RadioButton on = D.findViewById(R.id.radioButton);
            RadioButton off = D.findViewById(R.id.radioButton2);
            title.setText(titleText);
            on.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SelectedTaskButtons.add(bb);
                bb.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                TaskMoodButtons.add(new MoodBtn(d,buttonNumber,true));
                D.dismiss();
            });
            off.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SelectedTaskButtons.add(bb);
                bb.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                TaskMoodButtons.add(new MoodBtn(d,buttonNumber,false));
                D.dismiss();
            });
            D.show();
        }
    }

    void createPhysicalButtonStatusDialogSelector(Activity act, String titleText, Button bb, DeviceBean d,int buttonNumber) {
        if (d.getCategoryCode().equals("zig_wxkg")) {
            Dialog D = new Dialog(act);
            D.setContentView(R.layout.moos_dp_value_dialog);
            TextView title = D.findViewById(R.id.textView69);
            title.setText(titleText);
            LinearLayout options = D.findViewById(R.id.optionsLayout);
            TuyaHomeSdk.getSceneManagerInstance().getDeviceConditionOperationList(d.devId, new ITuyaResultCallback<List<TaskListBean>>() {
                @Override
                public void onSuccess(List<TaskListBean> result) {
                    for (TaskListBean t:result) {
                        if (buttonNumber == t.getDpId()) {
                            Object[] keys = t.getTasks().keySet().toArray();
                            for (Object o:keys) {
                                RadioButton r = new RadioButton(act);
                                r.setText(String.valueOf(o));
                                r.setTextColor(getResources().getColor(R.color.white,null));
                                r.setOnCheckedChangeListener((compoundButton, b) -> {
                                    if (b) {
                                        SelectedConditionButtons.add(bb);
                                        ConditionMoodButtons.add(new MoodBtn(d,buttonNumber,true));
                                        bb.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                                        SelectedTaskButtons.remove(bb);
                                        D.dismiss();
                                    }
                                });
                                options.addView(r);
                            }
                            D.show();
                        }
                    }
                }

                @Override
                public void onError(String errorCode, String errorMessage) {

                }
            });
        }
        else {
            Dialog D = new Dialog(act);
            D.setContentView(R.layout.mood_button_status);
            TextView title = D.findViewById(R.id.textView69);
            RadioButton on = D.findViewById(R.id.radioButton);
            RadioButton off = D.findViewById(R.id.radioButton2);
            title.setText(titleText);
            on.setOnCheckedChangeListener((buttonView, isChecked) -> {
                //MoodButton = bb ;
                SelectedConditionButtons.add(bb);
                //BTN = new MoodBtn(d,buttonNumber,true);
                ConditionMoodButtons.add(new MoodBtn(d,buttonNumber,true));
                bb.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                //PhysicalButton.setChecked(false);
                SelectedTaskButtons.remove(bb);
                D.dismiss();
            });
            off.setOnCheckedChangeListener((buttonView, isChecked) -> {
                //MoodButton = bb ;
                SelectedConditionButtons.add(bb);
                //BTN = new MoodBtn(d,buttonNumber,false);
                ConditionMoodButtons.add(new MoodBtn(d,buttonNumber,true));
                bb.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                //PhysicalButton.setChecked(false);
                SelectedTaskButtons.remove(bb);
                D.dismiss();
            });
            D.show();
        }
    }

    View.OnClickListener createButtonClickListener(Activity act,String title,Button bu,DeviceBean d,int buNumber) {
        return view -> {
            if (PhysicalButton.isChecked()) {
                if (SelectedConditionButtons.contains(bu)) {
                    SelectedConditionButtons.remove(bu);
                    bu.setBackgroundResource(R.drawable.btn_bg_selector);
                    for (int i = 0; i< ConditionMoodButtons.size(); i++) {
                        if (ConditionMoodButtons.get(i).Switch == d && ConditionMoodButtons.get(i).SwitchButton == buNumber) {
                            ConditionMoodButtons.remove(i);
                            break;
                        }
                    }
                }
                else {
                    createPhysicalButtonStatusDialogSelector(act,title,bu,d,buNumber);
                }
            }
            else {
                if (SelectedTaskButtons.contains(bu)) {
                    SelectedTaskButtons.remove(bu);
                    bu.setBackgroundResource(R.drawable.btn_bg_selector);
                    for (int i = 0; i< TaskMoodButtons.size(); i++) {
                        if (TaskMoodButtons.get(i).Switch == d && TaskMoodButtons.get(i).SwitchButton == buNumber) {
                            TaskMoodButtons.remove(i);
                            break;
                        }
                    }
                }
                else {
                    createButtonStatusDialogSelector(act,title,bu,d,buNumber);
                }
            }
        };
    }

    void getCurtainDetails(DeviceBean d,LinearLayout layout) {
        TuyaHomeSdk.getSceneManagerInstance().getDeviceConditionOperationList(d.devId, new ITuyaResultCallback<List<TaskListBean>>() {
            @Override
            public void onSuccess(List<TaskListBean> result) {
                for (TaskListBean t:result) {
                    if (t.getName().equals("Control")) {
                        Log.d("deviceDetails",t.getName()+" "+t.getSchemaBean().property+" "+t.getSchemaBean().name+" "+t.getSchemaBean().id+" "+t.getSchemaBean().type+" "+t.getOperators().get(0)+" "+t.getTasks().keySet());
                        Button b = new Button(act);
                        b.setBackgroundResource(R.drawable.btn_bg_normal);
                        b.setText(t.getName());
                        b.setTextColor(getResources().getColor(R.color.white,null));
                        b.setOnClickListener(createButtonClickListener(act,"Curtain "+t.getDpId(),b,d,(int)t.getDpId()));
                        layout.addView(b);
                    }
                }
            }

            @Override
            public void onError(String errorCode, String errorMessage) {

            }
        });
    }
}