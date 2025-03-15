package com.syriasoft.mobilecheckdevice;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilecheckdevice.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScreenButtons extends AppCompatActivity {

    Activity act ;
    Button S1_1,S1_2,S1_3,S1_4,S2_1,S2_2,S2_3,S2_4,S3_1,S3_2,S3_3,S3_4,S4_1,S4_2,S4_3,S4_4 ,S5_1,S5_2,S5_3,S5_4 ,S6_1,S6_2,S6_3,S6_4 ,S7_1,S7_2,S7_3,S7_4 ,S8_1,S8_2,S8_3,S8_4;
    List<SwitchButton> Buttons;
    SwitchButton selectedButton;
    static String NewName;
    RenameButtonDialog d;

//    public static RecyclerView CurrentButtonsRecycler , SwitchesButtons , SwitchesRecycler ;
//    List<ScreenButton> CurrentButtons ;
//    LinearLayoutManager CurrentManager , ButtonsManager , SwitchesManager ;
//    List<DeviceBean> Switches ;
//    public static List<String> Buttons ;
//    public static ScreenButtons_Adapter CurrentAdapter ;
//    public static ScreenButtonsSwitches_Adapter SwitchesAdapter ;
//    public static ScreenButtonsButtons_Adapter ButtonsAdapter ;
//    public static DeviceBean SelectedSwitch ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_buttons);
        Log.d("buttonNames","activity");
        setActivity();
        setActivityActions();
        getButtons();
    }

    void setActivity() {
        act = this ;
        Buttons = new ArrayList<>();
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
    }

    void setActivityActions() {
        S1_1.setOnClickListener(setButtonListener("Switch1",1));
        S1_2.setOnClickListener(setButtonListener("Switch1",2));
        S1_3.setOnClickListener(setButtonListener("Switch1",3));
        S1_4.setOnClickListener(setButtonListener("Switch1",4));

        S2_1.setOnClickListener(setButtonListener("Switch2",1));
        S2_2.setOnClickListener(setButtonListener("Switch2",2));
        S2_3.setOnClickListener(setButtonListener("Switch2",3));
        S2_4.setOnClickListener(setButtonListener("Switch2",4));

        S3_1.setOnClickListener(setButtonListener("Switch3",1));
        S3_2.setOnClickListener(setButtonListener("Switch3",2));
        S3_3.setOnClickListener(setButtonListener("Switch3",3));
        S3_4.setOnClickListener(setButtonListener("Switch3",4));

        S4_1.setOnClickListener(setButtonListener("Switch4",1));
        S4_2.setOnClickListener(setButtonListener("Switch4",2));
        S4_3.setOnClickListener(setButtonListener("Switch4",3));
        S4_4.setOnClickListener(setButtonListener("Switch4",4));

        S5_1.setOnClickListener(setButtonListener("Switch5",1));
        S5_2.setOnClickListener(setButtonListener("Switch5",2));
        S5_3.setOnClickListener(setButtonListener("Switch5",3));
        S5_4.setOnClickListener(setButtonListener("Switch5",4));

        S6_1.setOnClickListener(setButtonListener("Switch6",1));
        S6_2.setOnClickListener(setButtonListener("Switch6",2));
        S6_3.setOnClickListener(setButtonListener("Switch6",3));
        S6_4.setOnClickListener(setButtonListener("Switch6",4));

        S7_1.setOnClickListener(setButtonListener("Switch7",1));
        S7_2.setOnClickListener(setButtonListener("Switch7",2));
        S7_3.setOnClickListener(setButtonListener("Switch7",3));
        S7_4.setOnClickListener(setButtonListener("Switch7",4));

        S8_1.setOnClickListener(setButtonListener("Switch8",1));
        S8_2.setOnClickListener(setButtonListener("Switch8",2));
        S8_3.setOnClickListener(setButtonListener("Switch8",3));
        S8_4.setOnClickListener(setButtonListener("Switch8",4));
    }

    void getButtons() {
        Log.d("buttonNames","start");
        Buttons.clear();
        DatabaseReference roomButtons = Rooms.database.getReference("/"+MyApp.My_PROJECT.projectName+"/B"+RoomManager.Room.Building+"/F"+RoomManager.Room.Floor+"/R"+RoomManager.Room.RoomNumber+"/Buttons");
        roomButtons.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Log.d("buttonNames",snapshot.getValue().toString());
                    for(DataSnapshot dss:snapshot.getChildren()) {
                        if (dss.child("1").getValue() != null) {
                            SwitchButton b = new SwitchButton(dss.getKey(),1, Objects.requireNonNull(dss.child("1").getValue()).toString());
                            Buttons.add(b);
                            drawButton(b);
                        }
                        if (dss.child("2").getValue() != null) {
                            SwitchButton b = new SwitchButton(dss.getKey(),2, Objects.requireNonNull(dss.child("2").getValue()).toString());
                            Buttons.add(b);
                            drawButton(b);
                        }
                        if (dss.child("3").getValue() != null) {
                            SwitchButton b = new SwitchButton(dss.getKey(),3, Objects.requireNonNull(dss.child("3").getValue()).toString());
                            Buttons.add(b);
                            drawButton(b);
                        }
                        if (dss.child("4").getValue() != null) {
                            SwitchButton b = new SwitchButton(dss.getKey(),3, Objects.requireNonNull(dss.child("4").getValue()).toString());
                            Buttons.add(b);
                            drawButton(b);
                        }
                    }
                }
                else {
                    Log.d("buttonNames","null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("buttonNames",error.getMessage());
            }
        });
    }

    View.OnClickListener setButtonListener(String switchName,int button) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedButton = new SwitchButton(switchName,button,"");
                View.OnClickListener l = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (NewName == null || NewName.isEmpty()) {
                            new MessageDialog("please enter the new name","name?",act);
                            return;
                        }
                        for (SwitchButton b:Buttons) {
                            if (b.SwitchName.equals(selectedButton.SwitchName) && b.button == selectedButton.button) {
                                b.name = selectedButton.name;
                                new MessageDialog("Renamed successfully","Done",act);
                                selectedButton = null;
                                NewName = "";
                                d.close();
                                Log.d("buttonsAre",Buttons.size()+"");
                                return;
                            }
                        }
                        selectedButton.name = NewName;
                        SwitchButton bbb = new SwitchButton(selectedButton.SwitchName,selectedButton.button,selectedButton.name);
                        Buttons.add(bbb);
                        drawButton(bbb);
                        selectedButton = null;
                        NewName = "";
                        d.close();
                        new MessageDialog("Renamed Successfully","Done",act);
                        Log.d("buttonsAre",Buttons.size()+"");
                    }
                };
                d = new RenameButtonDialog(act,switchName+" "+button,l);
                d.show();
            }
        };
    }

    void drawButton(SwitchButton b) {
        switch (b.SwitchName) {
            case "Switch1":
                if (b.button == 1) {
                    S1_1.setText(b.name);
                }
                else if (b.button == 2) {
                    S1_2.setText(b.name);
                }
                else if (b.button == 3) {
                    S1_3.setText(b.name);
                }
                else if (b.button == 4) {
                    S1_4.setText(b.name);
                }
                break;
            case "Switch2":
                if (b.button == 1) {
                    S2_1.setText(b.name);
                }
                else if (b.button == 2) {
                    S2_2.setText(b.name);
                }
                else if (b.button == 3) {
                    S2_3.setText(b.name);
                }
                else if (b.button == 4) {
                    S2_4.setText(b.name);
                }
                break;
            case "Switch3":
                if (b.button == 1) {
                    S3_1.setText(b.name);
                }
                else if (b.button == 2) {
                    S3_2.setText(b.name);
                }
                else if (b.button == 3) {
                    S3_3.setText(b.name);
                }
                else if (b.button == 4) {
                    S3_4.setText(b.name);
                }
                break;
            case "Switch4":
                if (b.button == 1) {
                    S4_1.setText(b.name);
                }
                else if (b.button == 2) {
                    S4_2.setText(b.name);
                }
                else if (b.button == 3) {
                    S4_3.setText(b.name);
                }
                else if (b.button == 4) {
                    S4_4.setText(b.name);
                }
                break;
            case "Switch5":
                if (b.button == 1) {
                    S5_1.setText(b.name);
                }
                else if (b.button == 2) {
                    S5_2.setText(b.name);
                }
                else if (b.button == 3) {
                    S5_3.setText(b.name);
                }
                else if (b.button == 4) {
                    S5_4.setText(b.name);
                }
                break;
            case "Switch6":
                if (b.button == 1) {
                    S6_1.setText(b.name);
                }
                else if (b.button == 2) {
                    S6_2.setText(b.name);
                }
                else if (b.button == 3) {
                    S6_3.setText(b.name);
                }
                else if (b.button == 4) {
                    S6_4.setText(b.name);
                }
                break;
            case "Switch7":
                if (b.button == 1) {
                    S7_1.setText(b.name);
                }
                else if (b.button == 2) {
                    S7_2.setText(b.name);
                }
                else if (b.button == 3) {
                    S7_3.setText(b.name);
                }
                else if (b.button == 4) {
                    S7_4.setText(b.name);
                }
                break;
            case "Switch8":
                if (b.button == 1) {
                    S8_1.setText(b.name);
                }
                else if (b.button == 2) {
                    S8_2.setText(b.name);
                }
                else if (b.button == 3) {
                    S8_3.setText(b.name);
                }
                else if (b.button == 4) {
                    S8_4.setText(b.name);
                }
                break;
        }
    }

    public void saveButtons(View view) {
        if (Buttons.size() == 0) {
            new MessageDialog("please rename button","rename button",act);
            return;
        }
        for (int i=0;i<Buttons.size();i++) {
            DatabaseReference roomButtons = Rooms.database.getReference("/"+MyApp.My_PROJECT.projectName+"/B"+RoomManager.Room.Building+"/F"+RoomManager.Room.Floor+"/R"+RoomManager.Room.RoomNumber+"/Buttons");
            roomButtons.child(Buttons.get(i).SwitchName).child(String.valueOf(Buttons.get(i).button)).setValue(Buttons.get(i).name);
            if(i+1 == Buttons.size()) {
                new MessageDialog("Done","Done",act);
                getButtons();
            }
        }
    }
}