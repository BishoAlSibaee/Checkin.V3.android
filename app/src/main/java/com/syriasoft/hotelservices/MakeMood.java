package com.syriasoft.hotelservices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.bean.scene.SceneCondition;
import com.tuya.smart.home.sdk.bean.scene.SceneTask;
import com.tuya.smart.home.sdk.bean.scene.condition.rule.BoolRule;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.sdk.api.IResultCallback;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MakeMood extends AppCompatActivity {

    Activity act ;
    String modeName ;
    Button S1_1,S1_2,S1_3,S1_4,S2_1,S2_2,S2_3,S2_4,S3_1,S3_2,S3_3,S3_4,S4_1,S4_2,S4_3,S4_4 ,S5_1,S5_2,S5_3,S5_4 ,S6_1,S6_2,S6_3,S6_4 ,S7_1,S7_2,S7_3,S7_4 ,S8_1,S8_2,S8_3,S8_4,Service1,Service2,Service3,Service4;
    Button MoodButton ;
    MoodBtn BTN ;
    List<Button> SelectedButtons;
    List<MoodBtn> Buttons ;
    SwitchCompat PhisicalButton ;


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
        SelectedButtons = new ArrayList<>();
        Buttons = new ArrayList<>();
        PhisicalButton = findViewById(R.id.switch1);
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
        if (MyApp.Room.getSWITCH1_B() != null ) {
            if (MyApp.Room.getSWITCH1_B().dps.get("4") == null) {
                S1_4.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH1_B().dps.get("3") == null) {
                S1_3.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH1_B().dps.get("2") == null) {
                S1_2.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH1_B().dps.get("1") == null) {
                S1_1.setVisibility(View.INVISIBLE);
            }
        }
        else {
            S1_1.setVisibility(View.INVISIBLE);
            S1_2.setVisibility(View.INVISIBLE);
            S1_3.setVisibility(View.INVISIBLE);
            S1_4.setVisibility(View.INVISIBLE);
        }
        if (MyApp.Room.getSWITCH2_B() != null ) {
            if (MyApp.Room.getSWITCH2_B().dps.get("4") == null) {
                S2_4.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH2_B().dps.get("3") == null) {
                S2_3.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH2_B().dps.get("2") == null) {
                S2_2.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH2_B().dps.get("1") == null) {
                S2_1.setVisibility(View.INVISIBLE);
            }
        }
        else {
            S2_1.setVisibility(View.INVISIBLE);
            S2_2.setVisibility(View.INVISIBLE);
            S2_3.setVisibility(View.INVISIBLE);
            S2_4.setVisibility(View.INVISIBLE);
        }
        if (MyApp.Room.getSWITCH3_B() != null ) {
            if (MyApp.Room.getSWITCH3_B().dps.get("4") == null) {
                S3_4.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH3_B().dps.get("3") == null) {
                S3_3.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH3_B().dps.get("2") == null) {
                S3_2.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH3_B().dps.get("1") == null) {
                S3_1.setVisibility(View.INVISIBLE);
            }
        }
        else {
            S3_1.setVisibility(View.INVISIBLE);
            S3_2.setVisibility(View.INVISIBLE);
            S3_3.setVisibility(View.INVISIBLE);
            S3_4.setVisibility(View.INVISIBLE);
        }
        if (MyApp.Room.getSWITCH4_B() != null ) {
            if (MyApp.Room.getSWITCH4_B().dps.get("4") == null) {
                S4_4.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH4_B().dps.get("3") == null) {
                S4_3.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH4_B().dps.get("2") == null) {
                S4_2.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH4_B().dps.get("1") == null) {
                S4_1.setVisibility(View.INVISIBLE);
            }
        }
        else {
            S4_1.setVisibility(View.INVISIBLE);
            S4_2.setVisibility(View.INVISIBLE);
            S4_3.setVisibility(View.INVISIBLE);
            S4_4.setVisibility(View.INVISIBLE);
        }
        if (MyApp.Room.getSWITCH5_B() != null ) {
            if (MyApp.Room.getSWITCH5_B().dps.get("4") == null) {
                S5_4.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH5_B().dps.get("3") == null) {
                S5_3.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH5_B().dps.get("2") == null) {
                S5_2.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH5_B().dps.get("1") == null) {
                S5_1.setVisibility(View.INVISIBLE);
            }
        }
        else {
            S5_1.setVisibility(View.INVISIBLE);
            S5_2.setVisibility(View.INVISIBLE);
            S5_3.setVisibility(View.INVISIBLE);
            S5_4.setVisibility(View.INVISIBLE);
        }
        if (MyApp.Room.getSWITCH6_B() != null ) {
            if (MyApp.Room.getSWITCH6_B().dps.get("4") == null) {
                S6_4.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH6_B().dps.get("3") == null) {
                S6_3.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH6_B().dps.get("2") == null) {
                S6_2.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH6_B().dps.get("1") == null) {
                S6_1.setVisibility(View.INVISIBLE);
            }
        }
        else {
            S6_1.setVisibility(View.INVISIBLE);
            S6_2.setVisibility(View.INVISIBLE);
            S6_3.setVisibility(View.INVISIBLE);
            S6_4.setVisibility(View.INVISIBLE);
        }
        if (MyApp.Room.getSWITCH7_B() != null ) {
            if (MyApp.Room.getSWITCH7_B().dps.get("4") == null) {
                S7_4.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH7_B().dps.get("3") == null) {
                S7_3.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH7_B().dps.get("2") == null) {
                S7_2.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH7_B().dps.get("1") == null) {
                S7_1.setVisibility(View.INVISIBLE);
            }
        }
        else {
            S7_1.setVisibility(View.INVISIBLE);
            S7_2.setVisibility(View.INVISIBLE);
            S7_3.setVisibility(View.INVISIBLE);
            S7_4.setVisibility(View.INVISIBLE);
        }
        if (MyApp.Room.getSWITCH8_B() != null ) {
            if (MyApp.Room.getSWITCH8_B().dps.get("4") == null) {
                S8_4.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH8_B().dps.get("3") == null) {
                S8_3.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH8_B().dps.get("2") == null) {
                S8_2.setVisibility(View.INVISIBLE);
            }
            if (MyApp.Room.getSWITCH8_B().dps.get("1") == null) {
                S8_1.setVisibility(View.INVISIBLE);
            }
        }
        else {
            S8_1.setVisibility(View.INVISIBLE);
            S8_2.setVisibility(View.INVISIBLE);
            S8_3.setVisibility(View.INVISIBLE);
            S8_4.setVisibility(View.INVISIBLE);
        }
        switch (MyApp.ProjectVariables.cleanupButton) {
            case 1 :
                Service1.setText("Cleanup");
                break;
            case 2 :
                Service2.setText("Cleanup");
                break;
            case 3 :
                Service3.setText("Cleanup");
                break;
            case 4 :
                Service4.setText("Cleanup");
                break;
        }
        switch (MyApp.ProjectVariables.laundryButton) {
            case 1 :
                Service1.setText("Laundry");
                break;
            case 2 :
                Service2.setText("Laundry");
                break;
            case 3 :
                Service3.setText("Laundry");
                break;
            case 4 :
                Service4.setText("Laundry");
                break;
        }
        switch (MyApp.ProjectVariables.checkoutButton) {
            case 1 :
                Service1.setText("Checkout");
                break;
            case 2 :
                Service2.setText("Checkout");
                break;
            case 3 :
                Service3.setText("Checkout");
                break;
            case 4 :
                Service4.setText("Checkout");
                break;
        }
        switch (MyApp.ProjectVariables.dndButton) {
            case 1 :
                Service1.setText("DND");
                break;
            case 2 :
                Service2.setText("DND");
                break;
            case 3 :
                Service3.setText("DND");
                break;
            case 4 :
                Service4.setText("DND");
                break;
        }
    }

    void setActivityActions() {
        PhisicalButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            }
        });
        S1_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    MoodButton = S1_1 ;
                    BTN = new MoodBtn(MyApp.Room.getSWITCH1_B(),1,true);
                    S1_1.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S1_1)) {
                        SelectedButtons.remove(S1_1);
                    }
                }
                else {
                    if (SelectedButtons.contains(S1_1)) {
                        SelectedButtons.remove(S1_1);
                        S1_1.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH1_B() && Buttons.get(i).SwitchButton == 1) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 1 Button 1");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S1_1);
                                S1_1.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH1_B(),1,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S1_1);
                                S1_1.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH1_B(),1,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }

            }
        });
        S1_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH1_B(),2,true);
                    S1_2.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S1_2)) {
                        SelectedButtons.remove(S1_2);
                    }
                }
                else {
                    if (SelectedButtons.contains(S1_2)) {
                        SelectedButtons.remove(S1_2);
                        S1_2.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH1_B() && Buttons.get(i).SwitchButton == 2) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 1 Button 2");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S1_2);
                                S1_2.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH1_B(),2,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S1_2);
                                S1_2.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH1_B(),2,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }

            }
        });
        S1_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH1_B(),3,true);
                    S1_3.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S1_3)) {
                        SelectedButtons.remove(S1_3);
                    }
                }
                else {
                    if (SelectedButtons.contains(S1_3)) {
                        SelectedButtons.remove(S1_3);
                        S1_3.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH1_B() && Buttons.get(i).SwitchButton == 3) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 1 Button 3");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S1_3);
                                S1_3.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH1_B(),3,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S1_3);
                                S1_3.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH1_B(),3,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        S1_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH1_B(),4,true);
                    S1_4.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S1_4)) {
                        SelectedButtons.remove(S1_4);
                    }
                }
                else {
                    if (SelectedButtons.contains(S1_4)) {
                        SelectedButtons.remove(S1_4);
                        S1_4.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH1_B() && Buttons.get(i).SwitchButton == 4) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 1 Button 4");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S1_4);
                                S1_4.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH1_B(),4,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S1_4);
                                S1_4.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH1_B(),4,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        S2_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH2_B(),1,true);
                    S2_1.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S2_1)) {
                        SelectedButtons.remove(S2_1);
                    }
                }
                else {
                    if (SelectedButtons.contains(S2_1)) {
                        SelectedButtons.remove(S2_1);
                        S2_1.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH2_B() && Buttons.get(i).SwitchButton == 1) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 2 Button 1");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S2_1);
                                S2_1.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH2_B(),1,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S2_1);
                                S2_1.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH2_B(),1,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        S2_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH2_B(),2,true);
                    S2_2.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S2_2)) {
                        SelectedButtons.remove(S2_2);
                    }
                }
                else {
                    if (SelectedButtons.contains(S2_2)) {
                        SelectedButtons.remove(S2_2);
                        S2_2.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH2_B() && Buttons.get(i).SwitchButton == 2) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 2 Button 2");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S2_2);
                                S2_2.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH2_B(),2,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S2_2);
                                S2_2.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH2_B(),2,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        S2_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH2_B(),3,true);
                    S2_3.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S2_3)) {
                        SelectedButtons.remove(S2_3);
                    }
                }
                else {
                    if (SelectedButtons.contains(S2_3)) {
                        SelectedButtons.remove(S2_3);
                        S2_3.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH2_B() && Buttons.get(i).SwitchButton == 3) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 2 Button 3");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S2_3);
                                S2_3.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH2_B(),3,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S2_3);
                                S2_3.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH2_B(),3,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        S2_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH2_B(),4,true);
                    S2_4.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S2_4)) {
                        SelectedButtons.remove(S2_4);
                    }
                }
                else {
                    if (SelectedButtons.contains(S2_4)) {
                        SelectedButtons.remove(S2_4);
                        S2_4.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH2_B() && Buttons.get(i).SwitchButton == 4) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 2 Button 4");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S2_4);
                                S2_4.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH2_B(),4,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S2_4);
                                S2_4.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH2_B(),4,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        S3_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH3_B(),1,true);
                    S3_1.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S3_1)) {
                        SelectedButtons.remove(S3_1);
                    }
                }
                else {
                    if (SelectedButtons.contains(S3_1)) {
                        SelectedButtons.remove(S3_1);
                        S3_1.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH3_B() && Buttons.get(i).SwitchButton == 1) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 3 Button 1");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S3_1);
                                S3_1.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH3_B(),1,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S3_1);
                                S3_1.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH3_B(),1,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        S3_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH3_B(),2,true);
                    S3_2.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S3_2)) {
                        SelectedButtons.remove(S3_2);
                    }
                }
                else {
                    if (SelectedButtons.contains(S3_2)) {
                        SelectedButtons.remove(S3_2);
                        S3_2.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH3_B() && Buttons.get(i).SwitchButton == 2) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 3 Button 2");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S3_2);
                                S3_2.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH3_B(),2,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S3_2);
                                S3_2.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH3_B(),2,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        S3_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH3_B(),3,true);
                    S3_3.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S3_3)) {
                        SelectedButtons.remove(S3_3);
                    }
                }
                else {
                    if (SelectedButtons.contains(S3_3)) {
                        SelectedButtons.remove(S3_3);
                        S3_3.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH3_B() && Buttons.get(i).SwitchButton == 3) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 3 Button 3");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S3_3);
                                S3_3.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH3_B(),3,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S3_3);
                                S3_3.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH3_B(),3,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        S3_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH3_B(),4,true);
                    S3_4.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S3_4)) {
                        SelectedButtons.remove(S3_4);
                    }
                }
                else {
                    if (SelectedButtons.contains(S3_4)) {
                        SelectedButtons.remove(S3_4);
                        S3_4.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH3_B() && Buttons.get(i).SwitchButton == 4) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 3 Button 4");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S3_4);
                                S3_4.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH3_B(),4,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S3_4);
                                S3_4.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH3_B(),4,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        S4_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH4_B(),1,true);
                    S4_1.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S4_1)) {
                        SelectedButtons.remove(S4_1);
                    }
                }
                else {
                    if (SelectedButtons.contains(S4_1)) {
                        SelectedButtons.remove(S4_1);
                        S4_1.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH4_B() && Buttons.get(i).SwitchButton == 1) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 4 Button 1");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S4_1);
                                S4_1.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH4_B(),1,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S4_1);
                                S4_1.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH4_B(),1,false));
                                D.dismiss();
                            }
                        });
                        D.show();

                    }
                }
            }
        });
        S4_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH4_B(),2,true);
                    S4_2.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S4_2)) {
                        SelectedButtons.remove(S4_2);
                    }
                }
                else {
                    if (SelectedButtons.contains(S4_2)) {
                        SelectedButtons.remove(S4_2);
                        S4_2.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH4_B() && Buttons.get(i).SwitchButton == 2) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 4 Button 2");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S4_2);
                                S4_2.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH4_B(),2,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S4_2);
                                S4_2.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH4_B(),2,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        S4_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH4_B(),3,true);
                    S4_3.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S4_3)) {
                        SelectedButtons.remove(S4_3);
                    }
                }
                else {
                    if (SelectedButtons.contains(S4_3)) {
                        SelectedButtons.remove(S4_3);
                        S4_3.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH4_B() && Buttons.get(i).SwitchButton == 3) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 4 Button 3");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S4_3);
                                S4_3.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH4_B(),3,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S4_3);
                                S4_3.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH4_B(),3,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        S4_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH4_B(),4,true);
                    S4_4.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S4_4)) {
                        SelectedButtons.remove(S4_4);
                    }
                }
                else {
                    if (SelectedButtons.contains(S4_4)) {
                        SelectedButtons.remove(S4_4);
                        S4_4.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH4_B() && Buttons.get(i).SwitchButton == 4) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 4 Button 4");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S4_4);
                                S4_4.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH4_B(),4,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S4_4);
                                S4_4.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH4_B(),4,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }

            }
        });

        S5_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH5_B(),1,true);
                    S5_1.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S5_1)) {
                        SelectedButtons.remove(S5_1);
                    }
                }
                else {
                    if (SelectedButtons.contains(S5_1)) {
                        SelectedButtons.remove(S5_1);
                        S5_1.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH5_B() && Buttons.get(i).SwitchButton == 1) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 5 Button 1");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S5_1);
                                S5_1.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH5_B(),1,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S5_1);
                                S5_1.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH5_B(),1,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        S5_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH5_B(),2,true);
                    S5_2.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S5_2)) {
                        SelectedButtons.remove(S5_2);
                    }
                }
                else {
                    if (SelectedButtons.contains(S5_2)) {
                        SelectedButtons.remove(S5_2);
                        S5_2.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH5_B() && Buttons.get(i).SwitchButton == 2) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 5 Button 2");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S5_2);
                                S5_2.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH5_B(),2,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S5_2);
                                S5_2.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH5_B(),2,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        S5_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH5_B(),3,true);
                    S5_3.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S5_3)) {
                        SelectedButtons.remove(S5_3);
                    }
                }
                else {
                    if (SelectedButtons.contains(S5_3)) {
                        SelectedButtons.remove(S5_3);
                        S5_3.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH5_B() && Buttons.get(i).SwitchButton == 3) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 5 Button 3");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S5_3);
                                S5_3.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH5_B(),3,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S5_3);
                                S5_3.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH5_B(),3,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        S5_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH5_B(),4,true);
                    S5_4.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S5_4)) {
                        SelectedButtons.remove(S5_4);
                    }
                }
                else {
                    if (SelectedButtons.contains(S5_4)) {
                        SelectedButtons.remove(S5_4);
                        S5_4.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH5_B() && Buttons.get(i).SwitchButton == 4) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 5 Button 4");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S5_4);
                                S5_4.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH5_B(),4,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S5_4);
                                S5_4.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH5_B(),4,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }

            }
        });

        S6_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH6_B(),1,true);
                    S6_1.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S6_1)) {
                        SelectedButtons.remove(S6_1);
                    }
                }
                else {
                    if (SelectedButtons.contains(S6_1)) {
                        SelectedButtons.remove(S6_1);
                        S6_1.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH6_B() && Buttons.get(i).SwitchButton == 1) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 6 Button 1");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S6_1);
                                S6_1.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH6_B(),1,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S6_1);
                                S6_1.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH6_B(),1,false));
                                D.dismiss();
                            }
                        });
                        D.show();

                    }
                }
            }
        });
        S6_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH6_B(),2,true);
                    S6_2.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S6_2)) {
                        SelectedButtons.remove(S6_2);
                    }
                }
                else {
                    if (SelectedButtons.contains(S6_2)) {
                        SelectedButtons.remove(S6_2);
                        S6_2.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH6_B() && Buttons.get(i).SwitchButton == 2) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 6 Button 2");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S6_2);
                                S6_2.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH6_B(),2,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S6_2);
                                S6_2.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH6_B(),2,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        S6_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH6_B(),3,true);
                    S6_3.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S6_3)) {
                        SelectedButtons.remove(S6_3);
                    }
                }
                else {
                    if (SelectedButtons.contains(S6_3)) {
                        SelectedButtons.remove(S6_3);
                        S6_3.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH6_B() && Buttons.get(i).SwitchButton == 3) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 6 Button 3");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S6_3);
                                S6_3.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH6_B(),3,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S6_3);
                                S6_3.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH6_B(),3,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        S6_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH6_B(),4,true);
                    S6_4.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S6_4)) {
                        SelectedButtons.remove(S6_4);
                    }
                }
                else {
                    if (SelectedButtons.contains(S6_4)) {
                        SelectedButtons.remove(S6_4);
                        S6_4.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH6_B() && Buttons.get(i).SwitchButton == 4) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 6 Button 4");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S6_4);
                                S6_4.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH6_B(),4,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S6_4);
                                S6_4.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH6_B(),4,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }

            }
        });

        S7_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH7_B(),1,true);
                    S7_1.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S7_1)) {
                        SelectedButtons.remove(S7_1);
                    }
                }
                else {
                    if (SelectedButtons.contains(S7_1)) {
                        SelectedButtons.remove(S7_1);
                        S7_1.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH7_B() && Buttons.get(i).SwitchButton == 1) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 7 Button 1");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S7_1);
                                S7_1.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH7_B(),1,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S7_1);
                                S7_1.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH7_B(),1,false));
                                D.dismiss();
                            }
                        });
                        D.show();

                    }
                }
            }
        });
        S7_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH7_B(),2,true);
                    S7_2.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S7_2)) {
                        SelectedButtons.remove(S7_2);
                    }
                }
                else {
                    if (SelectedButtons.contains(S7_2)) {
                        SelectedButtons.remove(S7_2);
                        S7_2.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH7_B() && Buttons.get(i).SwitchButton == 2) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 7 Button 2");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S7_2);
                                S7_2.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH7_B(),2,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S7_2);
                                S7_2.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH7_B(),2,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        S7_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH7_B(),3,true);
                    S7_3.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S7_3)) {
                        SelectedButtons.remove(S7_3);
                    }
                }
                else {
                    if (SelectedButtons.contains(S7_3)) {
                        SelectedButtons.remove(S7_3);
                        S7_3.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH7_B() && Buttons.get(i).SwitchButton == 3) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 7 Button 3");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S7_3);
                                S7_3.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH7_B(),3,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S7_3);
                                S7_3.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH7_B(),3,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        S7_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH7_B(),4,true);
                    S7_4.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S7_4)) {
                        SelectedButtons.remove(S7_4);
                    }
                }
                else {
                    if (SelectedButtons.contains(S7_4)) {
                        SelectedButtons.remove(S7_4);
                        S7_4.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH7_B() && Buttons.get(i).SwitchButton == 4) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 7 Button 4");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S7_4);
                                S7_4.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH7_B(),4,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S7_4);
                                S7_4.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH7_B(),4,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }

            }
        });

        S8_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH8_B(),1,true);
                    S8_1.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S8_1)) {
                        SelectedButtons.remove(S8_1);
                    }
                }
                else {
                    if (SelectedButtons.contains(S8_1)) {
                        SelectedButtons.remove(S8_1);
                        S8_1.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH8_B() && Buttons.get(i).SwitchButton == 1) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 8 Button 1");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S8_1);
                                S8_1.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH8_B(),1,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S8_1);
                                S8_1.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH8_B(),1,false));
                                D.dismiss();
                            }
                        });
                        D.show();

                    }
                }
            }
        });
        S8_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH8_B(),2,true);
                    S8_2.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S8_2)) {
                        SelectedButtons.remove(S8_2);
                    }
                }
                else {
                    if (SelectedButtons.contains(S8_2)) {
                        SelectedButtons.remove(S8_2);
                        S8_2.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH8_B() && Buttons.get(i).SwitchButton == 2) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 8 Button 2");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S8_2);
                                S8_2.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH8_B(),2,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S8_2);
                                S8_2.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH8_B(),2,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        S8_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH8_B(),3,true);
                    S8_3.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S8_3)) {
                        SelectedButtons.remove(S8_3);
                    }
                }
                else {
                    if (SelectedButtons.contains(S8_3)) {
                        SelectedButtons.remove(S8_3);
                        S8_3.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH8_B() && Buttons.get(i).SwitchButton == 3) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 8 Button 3");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S8_3);
                                S8_3.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH8_B(),3,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S8_3);
                                S8_3.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH8_B(),3,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        S8_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhisicalButton.isChecked()) {
                    BTN = new MoodBtn(MyApp.Room.getSWITCH8_B(),4,true);
                    S8_4.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhisicalButton.setChecked(false);
                    if (SelectedButtons.contains(S8_4)) {
                        SelectedButtons.remove(S8_4);
                    }
                }
                else {
                    if (SelectedButtons.contains(S8_4)) {
                        SelectedButtons.remove(S8_4);
                        S8_4.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSWITCH8_B() && Buttons.get(i).SwitchButton == 4) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("Switch 8 Button 4");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S8_4);
                                S8_4.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH8_B(),4,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(S8_4);
                                S8_4.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSWITCH8_B(),4,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }

            }
        });

        Service1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Service1.getText().toString().equals("DND")) {
                    if (SelectedButtons.contains(Service1)) {
                        SelectedButtons.remove(Service1);
                        Service1.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSERVICE1_B() && Buttons.get(i).SwitchButton == MyApp.ProjectVariables.dndButton) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("DND");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(Service1);
                                Service1.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSERVICE1_B(),MyApp.ProjectVariables.dndButton,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(Service1);
                                Service1.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSERVICE1_B(),MyApp.ProjectVariables.dndButton,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        Service2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Service2.getText().toString().equals("DND")) {
                    if (SelectedButtons.contains(Service2)) {
                        SelectedButtons.remove(Service2);
                        Service2.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSERVICE1_B() && Buttons.get(i).SwitchButton == MyApp.ProjectVariables.dndButton) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("DND");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(Service2);
                                Service2.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSERVICE1_B(),MyApp.ProjectVariables.dndButton,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(Service2);
                                Service2.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSERVICE1_B(),MyApp.ProjectVariables.dndButton,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        Service3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Service3.getText().toString().equals("DND")) {
                    if (SelectedButtons.contains(Service3)) {
                        SelectedButtons.remove(Service3);
                        Service3.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSERVICE1_B() && Buttons.get(i).SwitchButton == MyApp.ProjectVariables.dndButton) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("DND");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(Service3);
                                Service3.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSERVICE1_B(),MyApp.ProjectVariables.dndButton,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(Service3);
                                Service3.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSERVICE1_B(),MyApp.ProjectVariables.dndButton,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
        Service4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Service4.getText().toString().equals("DND")) {
                    if (SelectedButtons.contains(Service4)) {
                        SelectedButtons.remove(Service4);
                        Service4.setBackgroundResource(R.drawable.btn_bg_selector);
                        for (int i=0;i<Buttons.size();i++) {
                            if (Buttons.get(i).Switch == MyApp.Room.getSERVICE1_B() && Buttons.get(i).SwitchButton == MyApp.ProjectVariables.dndButton) {
                                Buttons.remove(i);
                            }
                        }
                    }
                    else {
                        Dialog D = new Dialog(act);
                        D.setContentView(R.layout.mood_button_status);
                        TextView title = D.findViewById(R.id.textView69);
                        RadioButton on = D.findViewById(R.id.radioButton);
                        RadioButton off = D.findViewById(R.id.radioButton2);
                        title.setText("DND");
                        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(Service4);
                                Service4.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSERVICE1_B(),MyApp.ProjectVariables.dndButton,true));
                                D.dismiss();
                            }
                        });
                        off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SelectedButtons.add(Service4);
                                Service4.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                                Buttons.add(new MoodBtn(MyApp.Room.getSERVICE1_B(),MyApp.ProjectVariables.dndButton,false));
                                D.dismiss();
                            }
                        });
                        D.show();
                    }
                }
            }
        });
    }

    public void createMod(View view) {
        if (Buttons.size() == 0) {
            new messageDialog("please select buttons first","No Buttons",act);
            return ;
        }
        List<SceneCondition> conds = null;
        List<SceneTask> tasks = new ArrayList<>();
        if (BTN != null) {
            conds = new ArrayList<>();
            BoolRule rule = BoolRule.newInstance("dp"+BTN.SwitchButton, true);
            SceneCondition cond = SceneCondition.createDevCondition(BTN.Switch, String.valueOf(BTN.SwitchButton),rule);
            conds.add(cond);
        }
        final int[] counter = {0};
        for (int i=0;i<Buttons.size();i++) {
            HashMap<String, Object> taskMap = new HashMap<>();
            taskMap.put(String.valueOf(Buttons.get(i).SwitchButton), Buttons.get(i).status);
            SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(Buttons.get(i).Switch.devId, taskMap);
            tasks.add(task);
            int finalI = i;
            TuyaHomeSdk.getSceneManagerInstance().createScene(
                    MyApp.HOME.getHomeId(),
                    MyApp.Room.RoomNumber+modeName+i,
                    false,
                    FullscreenActivity.IMAGES.get(0),
                    conds,
                    tasks,
                    null,
                    SceneBean.MATCH_TYPE_AND,
                    new ITuyaResultCallback<SceneBean>() {
                        @Override
                        public void onSuccess(SceneBean sceneBean) {
                            Log.d("MoodCreation", "createScene Success");
                            TuyaHomeSdk.newSceneInstance(sceneBean.getId()).enableScene(sceneBean.getId(), new IResultCallback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d("MoodCreation", "enable Scene Success");
                                            counter[0]++;
                                            MyApp.MY_SCENES.add(sceneBean);
                                            if (Buttons.size() == counter[0]) {
                                                new messageDialog("Scene created","Done",act);
                                            }
                                        }
                                        @Override
                                        public void onError(String errorCode, String errorMessage) {
                                            Log.d("MoodCreation", errorMessage + " " + errorCode);
                                            new messageDialog(errorMessage + " " + errorCode,"Failed",act);

                                        }
                                    });
                        }
                        @Override
                        public void onError(String errorCode, String errorMessage) {
                            Log.d("MoodCreation", errorMessage + " " + errorCode);
                            new messageDialog(errorMessage + " " + errorCode,"Failed",act);
                        }
                    });
        }


    }

}