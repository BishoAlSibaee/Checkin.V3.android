package com.example.mobilecheckdevice;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;
import java.util.List;

public class AddTemplateMood extends AppCompatActivity {

    Activity act ;
    Button S1_1,S1_2,S1_3,S1_4,S2_1,S2_2,S2_3,S2_4,S3_1,S3_2,S3_3,S3_4,S4_1,S4_2,S4_3,S4_4 ,S5_1,S5_2,S5_3,S5_4 ,S6_1,S6_2,S6_3,S6_4 ,S7_1,S7_2,S7_3,S7_4 ,S8_1,S8_2,S8_3,S8_4,Service1,Service2,Service3,Service4,doorSensor,AC;
    SwitchCompat PhysicalButton;
    Spinner MoodsNameSpinner;
    String[] moodNamesArr;
    List<TemplateButton> MoodTaskButtons;
    TemplateButton MoodConditionButton;
    List<Button> MoodButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_template_mood);
        setActivity();
        setActivityActions();
    }

    void setActivity() {
        act = this;
        MoodTaskButtons = new ArrayList<>();
        MoodButtons = new ArrayList<>();
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

        moodNamesArr = new String[] {"Living","Sleep","Work","Romance","Read","MasterOff","Other1","Other2","Other3","Other4","Other5","Other6","Other7","Other8","Opposite1","Opposite2","Opposite3","Opposite4","Opposite5","Opposite6","Opposite7","Opposite8"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(act,R.layout.spinners_item,moodNamesArr);
        MoodsNameSpinner = findViewById(R.id.spinner4);
        MoodsNameSpinner.setAdapter(adapter);
    }

    void setActivityActions() {
        doorSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!PhysicalButton.isChecked()) {
                    new MessageDialog("door sensor must be the condition please turn the switch on ","switch on",act);
                    return;
                }
                try {
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

    View.OnClickListener setOnMoodButtonClickListener(Activity act, String title, String switchName, int dp, Button bu) {
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

    public void createMood(View view) {
        if (MoodTaskButtons.size() == 0) {
            new MessageDialog("please select task buttons",getResources().getString(R.string.selectButtons),act);
            return ;
        }
        try {
            if (ViewTemplate.template.searchMoodByName(MoodsNameSpinner.getSelectedItem().toString())) {
                new MessageDialog("mood name already exists in template","Mood Name ?",act);
                return ;
            }
            TemplateMood tm;
            if (MoodConditionButton == null) {
                tm = new TemplateMood(MoodsNameSpinner.getSelectedItem().toString(), new TemplateButton("", 0), MoodTaskButtons);
            }
            else {
                tm = new TemplateMood(MoodsNameSpinner.getSelectedItem().toString(), MoodConditionButton, MoodTaskButtons);
            }
            tm.saveTemplateMoodToFireBase(ViewTemplate.template.MoodsReference);
            ViewTemplate.template.moods.add(tm);
            new MessageDialog("Mood "+MoodsNameSpinner.getSelectedItem().toString()+" created","Done",act,true);
        }
        catch (Exception e) {
            new MessageDialog(e.getMessage(),"error",act);
        }
    }
}