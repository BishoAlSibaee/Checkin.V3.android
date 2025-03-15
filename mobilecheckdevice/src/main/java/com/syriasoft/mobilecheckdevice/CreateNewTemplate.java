package com.syriasoft.mobilecheckdevice;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecheckdevice.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CreateNewTemplate extends AppCompatActivity {

    Activity act ;
    List<ROOM> roomsHasTemplate;
    Button mood,multi;
    LinearLayout MoodsLayout,MultiLayout,MoodsRecyclerLayout,MultiRecyclerLayout;
    Button createTemplate;
    EditText TemplateName;
    RecyclerView moodsRecycler,multiRecycler;
    List<TemplateButton> MoodTaskButtons;
    TemplateButton MoodConditionButton;
    List<Button> MoodButtons;
    List<TemplateMood> TemplateMoods;

    List<Button> MultiButtons;
    List<TemplateButton> MultiControlSelectedButtons;
    List<TemplateMultiControl> TemplateMultiControls;
    Spinner MoodsNameSpinner;
    String[] moodNamesArr;

    Button S1_1,S1_2,S1_3,S1_4,S2_1,S2_2,S2_3,S2_4,S3_1,S3_2,S3_3,S3_4,S4_1,S4_2,S4_3,S4_4 ,S5_1,S5_2,S5_3,S5_4 ,S6_1,S6_2,S6_3,S6_4 ,S7_1,S7_2,S7_3,S7_4 ,S8_1,S8_2,S8_3,S8_4,Service1,Service2,Service3,Service4,doorSensor,AC;
    SwitchCompat PhysicalButton;

    Button S1_1C,S1_2C,S1_3C,S1_4C,S2_1C,S2_2C,S2_3C,S2_4C,S3_1C,S3_2C,S3_3C,S3_4C,S4_1C,S4_2C,S4_3C,S4_4C ,S5_1C,S5_2C,S5_3C,S5_4C ,S6_1C,S6_2C,S6_3C,S6_4C ,S7_1C,S7_2C,S7_3C,S7_4C ,S8_1C,S8_2C,S8_3C,S8_4C;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_template);
        setActivity();
        setActivityActions();
    }

    void setActivity() {
        act = this ;
        roomsHasTemplate = new ArrayList<>();
        MoodTaskButtons = new ArrayList<>();
        MultiButtons = new ArrayList<>();
        TemplateMoods = new ArrayList<>();
        MoodButtons = new ArrayList<>();
        TemplateMultiControls = new ArrayList<>();
        MultiControlSelectedButtons = new ArrayList<>();
        mood = findViewById(R.id.radioButton3);
        multi = findViewById(R.id.radioButton4);
        MoodsLayout = findViewById(R.id.moodLayout);
        MultiLayout = findViewById(R.id.multiControlLayout);
        MoodsRecyclerLayout = findViewById(R.id.moodsRecyclerLayout);
        MultiRecyclerLayout = findViewById(R.id.multiControlRecyclerLayout);
        createTemplate = findViewById(R.id.button29ee);
        TemplateName = findViewById(R.id.editTextTextPersonName3);
        TemplateName.setText(MyApp.ProjectVariables.projectName+"Template");
        MoodsLayout.setVisibility(View.GONE);
        MultiLayout.setVisibility(View.GONE);
        createTemplate.setVisibility(View.GONE);
        TemplateName.setVisibility(View.GONE);
        moodsRecycler = findViewById(R.id.moodRecycler);
        multiRecycler = findViewById(R.id.multiRecycler);
        PhysicalButton = findViewById(R.id.switch1);
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
        doorSensor = findViewById(R.id.button19237Av);
        AC = findViewById(R.id.button19237Av0);

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

        moodNamesArr = new String[] {"Living","Sleep","Work","Romance","Read","MasterOff","LightsOn","Other1","Other2","Other3","Other4","Other5","Other6","Other7","Other8","Opposite1","Opposite2","Opposite3","Opposite4","Opposite5","Opposite6","Opposite7","Opposite8"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(act,R.layout.spinners_item,moodNamesArr);
        MoodsNameSpinner = findViewById(R.id.spinner4);
        MoodsNameSpinner.setAdapter(adapter);
        GridLayoutManager manager = new GridLayoutManager(act,8);
        moodsRecycler.setLayoutManager(manager);

        S1_1C = findViewById(R.id.button2721);
        S1_2C = findViewById(R.id.button1921);
        S1_3C = findViewById(R.id.button1721);
        S1_4C = findViewById(R.id.button2821);
        S2_1C = findViewById(R.id.button27231);
        S2_2C = findViewById(R.id.button19231);
        S2_3C = findViewById(R.id.button17231);
        S2_4C = findViewById(R.id.button28231);
        S3_1C = findViewById(R.id.button272361);
        S3_2C = findViewById(R.id.button192361);
        S3_3C = findViewById(R.id.button172361);
        S3_4C = findViewById(R.id.button282361);
        S4_1C = findViewById(R.id.button272371);
        S4_2C = findViewById(R.id.button192371);
        S4_3C = findViewById(R.id.button172371);
        S4_4C = findViewById(R.id.button282371);
        S5_1C = findViewById(R.id.button27251);
        S5_2C = findViewById(R.id.button192r1);
        S5_3C = findViewById(R.id.button17261);
        S5_4C = findViewById(R.id.button28421);
        S6_1C = findViewById(R.id.button2723j1);
        S6_2C = findViewById(R.id.button1923j1);
        S6_3C = findViewById(R.id.button1723j1);
        S6_4C = findViewById(R.id.button2823j1);
        S7_1C = findViewById(R.id.button27236k1);
        S7_2C = findViewById(R.id.button19236k1);
        S7_3C = findViewById(R.id.button17236k1);
        S7_4C = findViewById(R.id.button28236k1);
        S8_1C = findViewById(R.id.button27237a1);
        S8_2C = findViewById(R.id.button19237A1);
        S8_3C = findViewById(R.id.button17237a1);
        S8_4C = findViewById(R.id.button28237a1);
        GridLayoutManager managerMulti = new GridLayoutManager(act,8);
        multiRecycler.setLayoutManager(managerMulti);
    }

    void setActivityActions() {
        mood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoodsLayout.setVisibility(View.VISIBLE);
                MultiLayout.setVisibility(View.GONE);
                MoodsRecyclerLayout.setVisibility(View.GONE);
                MultiRecyclerLayout.setVisibility(View.GONE);
                createTemplate.setVisibility(View.GONE);
                TemplateName.setVisibility(View.GONE);
            }
        });
        multi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoodsLayout.setVisibility(View.GONE);
                MultiLayout.setVisibility(View.VISIBLE);
                MoodsRecyclerLayout.setVisibility(View.GONE);
                MultiRecyclerLayout.setVisibility(View.GONE);
                createTemplate.setVisibility(View.GONE);
                TemplateName.setVisibility(View.GONE);
            }
        });

        // Mood Buttons Actions_________________________________________________________________________________________________
        doorSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PhysicalButton.isChecked()) {
                    new MessageDialog("door sensor must be the condition please turn the switch on ","switch on",act);
                    return;
                }
                try {
                    Log.d("doorSensor", RoomManager.Room.getDOORSENSOR_B().dps.keySet().toArray()[0].toString());
                    int x = Integer.parseInt(RoomManager.Room.getDOORSENSOR_B().dps.keySet().toArray()[0].toString());
                    createPhysicalButtonStatusDialogSelector(act, "Door Sensor ",doorSensor,"DoorSensor",1);
                }
                catch (Exception e) {
                    new MessageDialog(e.getMessage(),"error",act);
                }
            }
        });
        S1_1.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 1 Button 1","Switch1",1,S1_1));
        S1_2.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 1 Button 2","Switch1",2,S1_2));
        S1_3.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 1 Button 3","Switch1",3,S1_3));
        S1_4.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 1 Button 4","Switch1",4,S1_4));
        S2_1.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 2 Button 1","Switch2",1,S2_1));
        S2_2.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 2 Button 2","Switch2",2,S2_2));
        S2_3.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 2 Button 3","Switch2",3,S2_3));
        S2_4.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 2 Button 4","Switch2",4,S2_4));
        S3_1.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 3 Button 1","Switch3",1,S3_1));
        S3_2.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 3 Button 2","Switch3",2,S3_2));
        S3_3.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 3 Button 3","Switch3",3,S3_3));
        S3_4.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 3 Button 4","Switch3",4,S3_4));
        S4_1.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 4 Button 1","Switch4",1,S4_1));
        S4_2.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 4 Button 2","Switch4",2,S4_2));
        S4_3.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 4 Button 3","Switch4",3,S4_3));
        S4_4.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 4 Button 4","Switch4",4,S4_4));
        S5_1.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 5 Button 1","Switch5",1,S5_1));
        S5_2.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 5 Button 2","Switch5",2,S5_2));
        S5_3.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 5 Button 3","Switch5",3,S5_3));
        S5_4.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 5 Button 4","Switch5",4,S5_4));
        S6_1.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 6 Button 1","Switch6",1,S6_1));
        S6_2.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 6 Button 2","Switch6",2,S6_2));
        S6_3.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 6 Button 3","Switch6",3,S6_3));
        S6_4.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 6 Button 4","Switch6",4,S6_4));
        S7_1.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 7 Button 1","Switch7",1,S7_1));
        S7_2.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 7 Button 2","Switch7",2,S7_2));
        S7_3.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 7 Button 3","Switch7",3,S7_3));
        S7_4.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 7 Button 4","Switch7",4,S7_4));
        S8_1.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 8 Button 1","Switch8",1,S8_1));
        S8_2.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 8 Button 2","Switch8",2,S8_2));
        S8_3.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 8 Button 3","Switch8",3,S8_3));
        S8_4.setOnClickListener(setOnMoodButtonClickListener(act,"Switch 8 Button 4","Switch8",4,S8_4));
        Service1.setOnClickListener(v -> {
            if (Service1.getText().toString().equals("DND")) {
                TemplateButton TB = new TemplateButton("ServiceSwitch",MyApp.ProjectVariables.dndButton);
                int x = TemplateButton.searchTemplateButton(MoodTaskButtons,TB);
                if (x == -1) {
                    createButtonStatusDialogSelector(act,"Service Switch DND",Service1,"ServiceSwitch",MyApp.ProjectVariables.dndButton);
                }
                else {
                    MoodTaskButtons.remove(x);
                    Service1.setBackgroundResource(R.drawable.btn_bg_normal);
                }
            }
        });
        Service2.setOnClickListener(v -> {
            if (Service2.getText().toString().equals("DND")) {
                TemplateButton TB = new TemplateButton("ServiceSwitch",MyApp.ProjectVariables.dndButton);
                int x = TemplateButton.searchTemplateButton(MoodTaskButtons,TB);
                if (x == -1) {
                    createButtonStatusDialogSelector(act,"Service Switch DND",Service2,"ServiceSwitch",MyApp.ProjectVariables.dndButton);
                }
                else {
                    MoodTaskButtons.remove(x);
                    Service2.setBackgroundResource(R.drawable.btn_bg_normal);
                }
            }
        });
        Service3.setOnClickListener(v -> {
            if (Service3.getText().toString().equals("DND")) {
                TemplateButton TB = new TemplateButton("ServiceSwitch",MyApp.ProjectVariables.dndButton);
                int x = TemplateButton.searchTemplateButton(MoodTaskButtons,TB);
                if (x == -1) {
                    createButtonStatusDialogSelector(act,"Service Switch DND",Service3,"ServiceSwitch",MyApp.ProjectVariables.dndButton);
                }
                else {
                    MoodTaskButtons.remove(x);
                    Service3.setBackgroundResource(R.drawable.btn_bg_normal);
                }
            }
        });
        Service4.setOnClickListener(v -> {
            if (Service4.getText().toString().equals("DND")) {
                TemplateButton TB = new TemplateButton("ServiceSwitch",MyApp.ProjectVariables.dndButton);
                int x = TemplateButton.searchTemplateButton(MoodTaskButtons,TB);
                if (x == -1) {
                    createButtonStatusDialogSelector(act,"Service Switch DND",Service4,"ServiceSwitch",MyApp.ProjectVariables.dndButton);
                }
                else {
                    MoodTaskButtons.remove(x);
                    Service4.setBackgroundResource(R.drawable.btn_bg_normal);
                }
            }
        });
        AC.setOnClickListener(setOnMoodButtonClickListener(act,"AC Power","AC",1,AC));

        // MultiControl Buttons Actions_______________________________________________________________________________________

        S1_1C.setOnClickListener(setButtonListener(S1_1C,"Switch1",1));
        S1_2C.setOnClickListener(setButtonListener(S1_2C,"Switch1",2));
        S1_3C.setOnClickListener(setButtonListener(S1_3C,"Switch1",3));
        S1_4C.setOnClickListener(setButtonListener(S1_4C,"Switch1",4));

        S2_1C.setOnClickListener(setButtonListener(S2_1C,"Switch2",1));
        S2_2C.setOnClickListener(setButtonListener(S2_2C,"Switch2",2));
        S2_3C.setOnClickListener(setButtonListener(S2_3C,"Switch2",3));
        S2_4C.setOnClickListener(setButtonListener(S2_4C,"Switch2",4));

        S3_1C.setOnClickListener(setButtonListener(S3_1C,"Switch3",1));
        S3_2C.setOnClickListener(setButtonListener(S3_2C,"Switch3",2));
        S3_3C.setOnClickListener(setButtonListener(S3_3C,"Switch3",3));
        S3_4C.setOnClickListener(setButtonListener(S3_4C,"Switch3",4));

        S4_1C.setOnClickListener(setButtonListener(S4_1C,"Switch4",1));
        S4_2C.setOnClickListener(setButtonListener(S4_2C,"Switch4",2));
        S4_3C.setOnClickListener(setButtonListener(S4_3C,"Switch4",3));
        S4_4C.setOnClickListener(setButtonListener(S4_4C,"Switch4",4));

        S5_1C.setOnClickListener(setButtonListener(S5_1C,"Switch5",1));
        S5_2C.setOnClickListener(setButtonListener(S5_2C,"Switch5",2));
        S5_3C.setOnClickListener(setButtonListener(S5_3C,"Switch5",3));
        S5_4C.setOnClickListener(setButtonListener(S5_4C,"Switch5",4));

        S6_1C.setOnClickListener(setButtonListener(S6_1C,"Switch6",1));
        S6_2C.setOnClickListener(setButtonListener(S6_2C,"Switch6",2));
        S6_3C.setOnClickListener(setButtonListener(S6_3C,"Switch6",3));
        S6_4C.setOnClickListener(setButtonListener(S6_4C,"Switch6",4));

        S7_1C.setOnClickListener(setButtonListener(S7_1C,"Switch7",1));
        S7_2C.setOnClickListener(setButtonListener(S7_2C,"Switch7",2));
        S7_3C.setOnClickListener(setButtonListener(S7_3C,"Switch7",3));
        S7_4C.setOnClickListener(setButtonListener(S7_4C,"Switch7",4));

        S8_1C.setOnClickListener(setButtonListener(S8_1C,"Switch8",1));
        S8_2C.setOnClickListener(setButtonListener(S8_2C,"Switch8",2));
        S8_3C.setOnClickListener(setButtonListener(S8_3C,"Switch8",3));
        S8_4C.setOnClickListener(setButtonListener(S8_4C,"Switch8",4));
    }

    public void addTemplate(View view) {
        if (TemplateName.getText() == null || TemplateName.getText().toString().isEmpty()) {
            new MessageDialog("enter template name","Template Name ?",act);
            return ;
        }
        if (TemplateMoods.size() > 0) {
            for (TemplateMood tm : TemplateMoods) {
                tm.saveTemplateMoodToFireBase(Rooms.RoomTemplates.child(TemplateName.getText().toString()).child("Moods"));
            }
        }
        if (TemplateMultiControls.size() > 0) {
            for (TemplateMultiControl tmc :TemplateMultiControls) {
                tmc.saveMultiControlToFireBase(Rooms.RoomTemplates.child(TemplateName.getText().toString()).child("MultiControls"));
            }
        }
        Rooms.RoomTemplates.child(TemplateName.getText().toString()).child("Rooms").setValue("");
        new MessageDialog("template saved","Template Saved",act,true);
    }

    public void createMulti(View view) {
        if (MultiControlSelectedButtons.size() == 0) {
            new MessageDialog(getResources().getString(R.string.pleaseSelectButtons),getResources().getString(R.string.selectButtons),act);
            return ;
        }
        if (MultiControlSelectedButtons.size() == 1) {
            new MessageDialog(getResources().getString(R.string.pleaseSelectOtherButtonsInMultiControl),getResources().getString(R.string.selectOtherButtons),act);
            return ;
        }
        int x = new Random().nextInt(100);
        int y = new Random().nextInt(100);
        TemplateMultiControls.add(new TemplateMultiControl(String.valueOf(x)+y,MultiControlSelectedButtons));
        setMultiControlRecycler();
        clearMultiControl();
        Back();
    }

    public void createMood(View view) {
        if (MoodConditionButton == null){
            TemplateMoods.add(new TemplateMood(MoodsNameSpinner.getSelectedItem().toString(),new TemplateButton("",0),MoodTaskButtons));
        }
        else {
            TemplateMoods.add(new TemplateMood(MoodsNameSpinner.getSelectedItem().toString(),MoodConditionButton,MoodTaskButtons));
        }
        setMoodsRecycler();
        clearMood();
        Back();
    }

    View.OnClickListener setOnMoodButtonClickListener000(String switchName,int dp,Button b) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PhysicalButton.isChecked()) {
                    MoodConditionButton = new TemplateButton(switchName,dp);
                    b.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
                    PhysicalButton.setChecked(false);
                    Log.d("addButton","green "+MoodTaskButtons.size());
                }
                else {
                    TemplateButton TB = new TemplateButton(switchName,dp);
                    int x = TemplateButton.searchTemplateButton(MoodTaskButtons,TB);
                    if (x == -1) {
                        MoodTaskButtons.add(TB);
                        b.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                        for (TemplateButton t:MoodTaskButtons) {
                            Log.d("addButton", t.SwitchName+" "+t.DP);
                        }
                    }
                    else {
                        MoodTaskButtons.remove(x);
                        b.setBackgroundResource(R.drawable.btn_bg_normal);
                        for (TemplateButton t:MoodTaskButtons) {
                            Log.d("addButton", t.SwitchName+" "+t.DP);
                        }
                    }
                }
            }
        };
    }

    void createButtonStatusDialogSelector(Activity act, String titleText, Button b,String switchName, int dp) {
        Dialog D = new Dialog(act);
        D.setContentView(R.layout.mood_button_status);
        TextView title = D.findViewById(R.id.textView69);
        RadioButton on = D.findViewById(R.id.radioButton);
        RadioButton off = D.findViewById(R.id.radioButton2);
        title.setText(titleText);
        on.setOnCheckedChangeListener((buttonView, isChecked) -> {
            TemplateButton TB = new TemplateButton(switchName,dp,true);
            MoodTaskButtons.add(TB);
            MoodButtons.add(b);
            b.setBackgroundResource(R.drawable.btn_bg_normal_selected);
            D.dismiss();
        });
        off.setOnCheckedChangeListener((buttonView, isChecked) -> {
            TemplateButton TB = new TemplateButton(switchName,dp,false);
            MoodTaskButtons.add(TB);
            MoodButtons.add(b);
            b.setBackgroundResource(R.drawable.btn_bg_normal_selected);
            D.dismiss();
        });
        D.show();
    }

    void createPhysicalButtonStatusDialogSelector(Activity act, String titleText, Button b,String switchName,int dp){
        Dialog D = new Dialog(act);
        D.setContentView(R.layout.mood_button_status);
        TextView title = D.findViewById(R.id.textView69);
        RadioButton on = D.findViewById(R.id.radioButton);
        RadioButton off = D.findViewById(R.id.radioButton2);
        title.setText(titleText);
        on.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MoodConditionButton = new TemplateButton(switchName,dp,true);
            b.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
            PhysicalButton.setChecked(false);
            MoodButtons.add(b);
            D.dismiss();
        });
        off.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MoodConditionButton = new TemplateButton(switchName,dp,false);
            b.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
            PhysicalButton.setChecked(false);
            MoodButtons.add(b);
            D.dismiss();
        });
        D.show();
    }

    View.OnClickListener setOnMoodButtonClickListener(Activity act,String title,String switchName,int dp,Button bu) {
        return view -> {
            if (PhysicalButton.isChecked()) {
                createPhysicalButtonStatusDialogSelector(act,title,bu,switchName,dp);
            }
            else {
                TemplateButton TB = new TemplateButton(switchName,dp);
                int x = TemplateButton.searchTemplateButton(MoodTaskButtons,TB);
                if (x == -1) {
                    createButtonStatusDialogSelector(act,title,bu,switchName,dp);
                }
                else {
                    MoodTaskButtons.remove(x);
                    bu.setBackgroundResource(R.drawable.btn_bg_normal);
                    MoodButtons.remove(bu);
                }
            }
        };
    }

    void setMoodsRecycler() {
        TemplateMood_adapter adapter = new TemplateMood_adapter(TemplateMoods);
        moodsRecycler.setAdapter(adapter);
    }

    void setMultiControlRecycler() {
        TemplateMultiControl_adapter adapter = new TemplateMultiControl_adapter(TemplateMultiControls);
        multiRecycler.setAdapter(adapter);
    }

    void clearMood() {
        MoodConditionButton = null;
        MoodTaskButtons.clear();
        for (Button b : MoodButtons) {
            b.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        MoodButtons.clear();
    }

    void clearMultiControl() {
        MultiControlSelectedButtons.clear();
        for (Button b :MultiButtons ) {
            b.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        MultiButtons.clear();
    }

    void Back() {
        MoodsLayout.setVisibility(View.GONE);
        MultiLayout.setVisibility(View.GONE);
        MoodsRecyclerLayout.setVisibility(View.VISIBLE);
        MultiRecyclerLayout.setVisibility(View.VISIBLE);
        createTemplate.setVisibility(View.VISIBLE);
        TemplateName.setVisibility(View.VISIBLE);
    }

    public void goBack(View view) {
        Back();
    }


    View.OnClickListener setButtonListener(Button b, String switchName, int dp) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("multiButton","clicked "+MultiControlSelectedButtons.size());
                TemplateButton TB = new TemplateButton(switchName,dp);
                int x = TemplateButton.searchTemplateButton(MultiControlSelectedButtons,TB);
                if (x == -1) {
                    MultiControlSelectedButtons.add(TB);
                    MultiButtons.add(b);
                    b.setBackgroundResource(R.drawable.btn_bg_normal_selected);
                    Log.d("multiButton","red");
                }
                else {
                    MultiButtons.remove(b);
                    MultiControlSelectedButtons.remove(x);
                    b.setBackgroundResource(R.drawable.btn_bg_selector);
                    Log.d("multiButton","blue");
                }
            }
        };
    }
}