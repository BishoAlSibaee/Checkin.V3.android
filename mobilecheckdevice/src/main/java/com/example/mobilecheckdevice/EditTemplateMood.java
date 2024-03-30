package com.example.mobilecheckdevice;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;
import java.util.List;

public class EditTemplateMood extends AppCompatActivity {

    Activity act;
    int index;
    Button S1_1,S1_2,S1_3,S1_4,S2_1,S2_2,S2_3,S2_4,S3_1,S3_2,S3_3,S3_4,S4_1,S4_2,S4_3,S4_4 ,S5_1,S5_2,S5_3,S5_4 ,S6_1,S6_2,S6_3,S6_4 ,S7_1,S7_2,S7_3,S7_4 ,S8_1,S8_2,S8_3,S8_4,Service1,Service2,Service3,Service4,doorSensor,AC;
    SwitchCompat PhysicalButton;
    List<Button> MoodButtons;
    TemplateMood MOOD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_template_mood);
        index = getIntent().getExtras().getInt("index");
        MOOD = ViewTemplate.template.moods.get(index);
        setActivity();
        setActivityActions();
        drawMoodButtons();
    }

    void setActivity() {
        act = this;
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
                    createPhysicalButtonStatusDialogSelector(act, "Door Sensor ",doorSensor,new TemplateButton("DoorSensor",1));
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
                int x = TemplateButton.searchTemplateButton(MOOD.tasks,TB);
                if (x == -1) {
                    createButtonStatusDialogSelector(act,"Service Switch DND",Service1,new TemplateButton("ServiceSwitch",MyApp.ProjectVariables.dndButton));
                }
                else {
                    MOOD.tasks.remove(x);
                    Service1.setBackgroundResource(R.drawable.btn_bg_normal);
                }
            }
        });
        Service2.setOnClickListener(v -> {
            if (Service2.getText().toString().equals("DND")) {
                TemplateButton TB = new TemplateButton("ServiceSwitch",MyApp.ProjectVariables.dndButton);
                int x = TemplateButton.searchTemplateButton(MOOD.tasks,TB);
                if (x == -1) {
                    createButtonStatusDialogSelector(act,"Service Switch DND",Service2,new TemplateButton("ServiceSwitch",MyApp.ProjectVariables.dndButton));
                }
                else {
                    MOOD.tasks.remove(x);
                    Service2.setBackgroundResource(R.drawable.btn_bg_normal);
                }
            }
        });
        Service3.setOnClickListener(v -> {
            if (Service3.getText().toString().equals("DND")) {
                TemplateButton TB = new TemplateButton("ServiceSwitch",MyApp.ProjectVariables.dndButton);
                int x = TemplateButton.searchTemplateButton(MOOD.tasks,TB);
                if (x == -1) {
                    createButtonStatusDialogSelector(act,"Service Switch DND",Service3,new TemplateButton("ServiceSwitch",MyApp.ProjectVariables.dndButton));
                }
                else {
                    MOOD.tasks.remove(x);
                    Service3.setBackgroundResource(R.drawable.btn_bg_normal);
                }
            }
        });
        Service4.setOnClickListener(v -> {
            if (Service4.getText().toString().equals("DND")) {
                TemplateButton TB = new TemplateButton("ServiceSwitch",MyApp.ProjectVariables.dndButton);
                int x = TemplateButton.searchTemplateButton(MOOD.tasks,TB);
                if (x == -1) {
                    createButtonStatusDialogSelector(act,"Service Switch DND",Service4,new TemplateButton("ServiceSwitch",MyApp.ProjectVariables.dndButton));
                }
                else {
                    MOOD.tasks.remove(x);
                    Service4.setBackgroundResource(R.drawable.btn_bg_normal);
                }
            }
        });
        AC.setOnClickListener(setOnMoodButtonClickListener(act,"AC Power","AC",1,AC));
    }

    void drawMoodButtons() {
        if (MOOD.conditionButton != null) {
            if (MOOD.conditionButton.SwitchName.equals("Switch1")) {
                switch (MOOD.conditionButton.DP) {
                    case 1:
                        setConditionButtonClicked(S1_1);
                        break;
                    case 2:
                        setConditionButtonClicked(S1_2);
                        break;
                    case 3:
                        setConditionButtonClicked(S1_3);
                        break;
                    case 4:
                        setConditionButtonClicked(S1_4);
                        break;
                }
            } else
            if (MOOD.conditionButton.SwitchName.equals("Switch2")) {
                switch (MOOD.conditionButton.DP) {
                    case 1:
                        setConditionButtonClicked(S2_1);
                        break;
                    case 2:
                        setConditionButtonClicked(S2_2);
                        break;
                    case 3:
                        setConditionButtonClicked(S2_3);
                        break;
                    case 4:
                        setConditionButtonClicked(S2_4);
                        break;
                }
            } else
            if (MOOD.conditionButton.SwitchName.equals("Switch3")) {
                switch (MOOD.conditionButton.DP) {
                    case 1:
                        setConditionButtonClicked(S3_1);
                        break;
                    case 2:
                        setConditionButtonClicked(S3_2);
                        break;
                    case 3:
                        setConditionButtonClicked(S3_3);
                        break;
                    case 4:
                        setConditionButtonClicked(S3_4);
                        break;
                }
            } else
            if (MOOD.conditionButton.SwitchName.equals("Switch4")) {
                switch (MOOD.conditionButton.DP) {
                    case 1:
                        setConditionButtonClicked(S4_1);
                        break;
                    case 2:
                        setConditionButtonClicked(S4_2);
                        break;
                    case 3:
                        setConditionButtonClicked(S4_3);
                        break;
                    case 4:
                        setConditionButtonClicked(S4_4);
                        break;
                }
            } else
            if (MOOD.conditionButton.SwitchName.equals("Switch5")) {
                switch (MOOD.conditionButton.DP) {
                    case 1:
                        setConditionButtonClicked(S5_1);
                        break;
                    case 2:
                        setConditionButtonClicked(S5_2);
                        break;
                    case 3:
                        setConditionButtonClicked(S5_3);
                        break;
                    case 4:
                        setConditionButtonClicked(S5_4);
                        break;
                }
            } else
            if (MOOD.conditionButton.SwitchName.equals("Switch6")) {
                switch (MOOD.conditionButton.DP) {
                    case 1:
                        setConditionButtonClicked(S6_1);
                        break;
                    case 2:
                        setConditionButtonClicked(S6_2);
                        break;
                    case 3:
                        setConditionButtonClicked(S6_3);
                        break;
                    case 4:
                        setConditionButtonClicked(S6_4);
                        break;
                }
            } else
            if (MOOD.conditionButton.SwitchName.equals("Switch7")) {
                switch (MOOD.conditionButton.DP) {
                    case 1:
                        setConditionButtonClicked(S7_1);
                        break;
                    case 2:
                        setConditionButtonClicked(S7_2);
                        break;
                    case 3:
                        setConditionButtonClicked(S7_3);
                        break;
                    case 4:
                        setConditionButtonClicked(S7_4);
                        break;
                }
            } else
            if (MOOD.conditionButton.SwitchName.equals("Switch8")) {
                switch (MOOD.conditionButton.DP) {
                    case 1:
                        setConditionButtonClicked(S8_1);
                        break;
                    case 2:
                        setConditionButtonClicked(S8_2);
                        break;
                    case 3:
                        setConditionButtonClicked(S8_3);
                        break;
                    case 4:
                        setConditionButtonClicked(S8_4);
                        break;
                }
            } else
            if (MOOD.conditionButton.SwitchName.equals("DoorSensor")) {
                setConditionButtonClicked(doorSensor);
            } else
            if (MOOD.conditionButton.SwitchName.equals("AC")) {
                setConditionButtonClicked(AC);
            }
        }
        if (MOOD.tasks.size() > 0) {
            for (TemplateButton tb: MOOD.tasks) {
                if (tb.SwitchName.equals("Switch1")) {
                    switch (tb.DP) {
                        case 1:
                            setButtonClicked(S1_1);
                            break;
                        case 2:
                            setButtonClicked(S1_2);
                            break;
                        case 3:
                            setButtonClicked(S1_3);
                            break;
                        case 4:
                            setButtonClicked(S1_4);
                            break;
                    }
                }
                if (tb.SwitchName.equals("Switch2")) {
                    switch (tb.DP) {
                        case 1:
                            setButtonClicked(S2_1);
                            break;
                        case 2:
                            setButtonClicked(S2_2);
                            break;
                        case 3:
                            setButtonClicked(S2_3);
                            break;
                        case 4:
                            setButtonClicked(S2_4);
                            break;
                    }
                }
                if (tb.SwitchName.equals("Switch3")) {
                    switch (tb.DP) {
                        case 1:
                            setButtonClicked(S3_1);
                            break;
                        case 2:
                            setButtonClicked(S3_2);
                            break;
                        case 3:
                            setButtonClicked(S3_3);
                            break;
                        case 4:
                            setButtonClicked(S3_4);
                            break;
                    }
                }
                if (tb.SwitchName.equals("Switch4")) {
                    switch (tb.DP) {
                        case 1:
                            setButtonClicked(S4_1);
                            break;
                        case 2:
                            setButtonClicked(S4_2);
                            break;
                        case 3:
                            setButtonClicked(S4_3);
                            break;
                        case 4:
                            setButtonClicked(S4_4);
                            break;
                    }
                }
                if (tb.SwitchName.equals("Switch5")) {
                    switch (tb.DP) {
                        case 1:
                            setButtonClicked(S5_1);
                            break;
                        case 2:
                            setButtonClicked(S5_2);
                            break;
                        case 3:
                            setButtonClicked(S5_3);
                            break;
                        case 4:
                            setButtonClicked(S5_4);
                            break;
                    }
                }
                if (tb.SwitchName.equals("Switch6")) {
                    switch (tb.DP) {
                        case 1:
                            setButtonClicked(S6_1);
                            break;
                        case 2:
                            setButtonClicked(S6_2);
                            break;
                        case 3:
                            setButtonClicked(S6_3);
                            break;
                        case 4:
                            setButtonClicked(S6_4);
                            break;
                    }
                }
                if (tb.SwitchName.equals("Switch7")) {
                    switch (tb.DP) {
                        case 1:
                            setButtonClicked(S7_1);
                            break;
                        case 2:
                            setButtonClicked(S7_2);
                            break;
                        case 3:
                            setButtonClicked(S7_3);
                            break;
                        case 4:
                            setButtonClicked(S7_4);
                            break;
                    }
                }
                if (tb.SwitchName.equals("Switch8")) {
                    switch (tb.DP) {
                        case 1:
                            setButtonClicked(S8_1);
                            break;
                        case 2:
                            setButtonClicked(S8_2);
                            break;
                        case 3:
                            setButtonClicked(S8_3);
                            break;
                        case 4:
                            setButtonClicked(S8_4);
                            break;
                    }
                }
            }
        }
    }

    void setButtonClicked(Button b) {
        b.setBackgroundResource(R.drawable.btn_bg_normal_selected);
        MoodButtons.add(b);
    }

    void setButtonUnClicked(Button b) {
        b.setBackgroundResource(R.drawable.btn_bg_selector);
        MoodButtons.remove(b);
    }

    void setConditionButtonClicked(Button b) {
        b.setBackgroundResource(R.drawable.btn_bg_normal_selected0);
        MoodButtons.add(b);
    }

    void setConditionButtonUnClicked(Button b) {
        b.setBackgroundResource(R.drawable.btn_bg_selector);
        MoodButtons.remove(b);
    }

    void createButtonStatusDialogSelector(Activity act, String titleText, Button b,TemplateButton TB) {
        Dialog D = new Dialog(act);
        D.setContentView(R.layout.mood_button_status);
        TextView title = D.findViewById(R.id.textView69);
        RadioButton on = D.findViewById(R.id.radioButton);
        RadioButton off = D.findViewById(R.id.radioButton2);
        title.setText(titleText);
        on.setOnCheckedChangeListener((buttonView, isChecked) -> {
            TB.status = true;
            MOOD.tasks.add(TB);
            setButtonClicked(b);
            D.dismiss();
        });
        off.setOnCheckedChangeListener((buttonView, isChecked) -> {
            TB.status = false;
            MOOD.tasks.add(TB);
            setButtonClicked(b);
            D.dismiss();
        });
        D.show();
    }

    void createPhysicalButtonStatusDialogSelector(Activity act, String titleText, Button b,TemplateButton TB){
        Dialog D = new Dialog(act);
        D.setContentView(R.layout.mood_button_status);
        TextView title = D.findViewById(R.id.textView69);
        RadioButton on = D.findViewById(R.id.radioButton);
        RadioButton off = D.findViewById(R.id.radioButton2);
        title.setText(titleText);
        on.setOnCheckedChangeListener((buttonView, isChecked) -> {
            TB.status = true;
            MOOD.conditionButton = TB;
            PhysicalButton.setChecked(false);
            setConditionButtonClicked(b);
            D.dismiss();
        });
        off.setOnCheckedChangeListener((buttonView, isChecked) -> {
            TB.status = false;
            MOOD.conditionButton = TB;
            PhysicalButton.setChecked(false);
            setConditionButtonClicked(b);
            D.dismiss();
        });
        D.show();
    }

    View.OnClickListener setOnMoodButtonClickListener(Activity act, String title, String switchName, int dp, Button bu) {
        return view -> {
            if (PhysicalButton.isChecked()) {
                TemplateButton TB = new TemplateButton(switchName,dp);
                createPhysicalButtonStatusDialogSelector(act,title,bu,TB);
            }
            else {
                TemplateButton TB = new TemplateButton(switchName,dp);
                int x = TemplateButton.searchTemplateButton(MOOD.tasks,TB);
                if (x == -1) {
                    if (MOOD.conditionButton != null) {
                        if (TB.SwitchName.equals(MOOD.conditionButton.SwitchName) && TB.DP == MOOD.conditionButton.DP) {
                            setConditionButtonUnClicked(bu);
                            MOOD.conditionButton = null;
                        }
                        else {
                            createButtonStatusDialogSelector(act,title,bu,TB);
                        }
                    }
                }
                else {
                    MOOD.tasks.remove(x);
                    setButtonUnClicked(bu);
                }
            }
        };
    }

    public void editMood(View view) {
        if (MOOD.conditionButton != null) {
            Log.d("editMood","cond name: "+MOOD.conditionButton.SwitchName+" "+MOOD.conditionButton.DP+" task: "+MOOD.tasks.size());
        }
        else {
            Log.d("editMood","cond name: null  task: "+MOOD.tasks.size());
        }
        if (MOOD.tasks.size() == 0) {
            new MessageDialog("please select task buttons",getResources().getString(R.string.selectButtons),act);
            return ;
        }
        try {
            MOOD.saveTemplateMoodToFireBase(ViewTemplate.template.MoodsReference);
            new MessageDialog("Mood "+MOOD.name+" created","Done",act,true);
        }
        catch (Exception e) {
            new MessageDialog(e.getMessage(),"error",act);
        }
    }
}