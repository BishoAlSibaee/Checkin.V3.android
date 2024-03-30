package com.example.mobilecheckdevice;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class EditTemplateMultiControl extends AppCompatActivity {

    Activity act;
    int index;
    TemplateMultiControl MultiControl;
    Button S1_1,S1_2,S1_3,S1_4,S2_1,S2_2,S2_3,S2_4,S3_1,S3_2,S3_3,S3_4,S4_1,S4_2,S4_3,S4_4 ,S5_1,S5_2,S5_3,S5_4 ,S6_1,S6_2,S6_3,S6_4 ,S7_1,S7_2,S7_3,S7_4 ,S8_1,S8_2,S8_3,S8_4,Service1,Service2,Service3,Service4,doorSensor;
    List<Button> SelectedButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_template_multi_control);
        index = getIntent().getExtras().getInt("index");
        MultiControl = ViewTemplate.template.multiControls.get(index);
        setActivity();
        setActivityActions();
    }

    void setActivity() {
        act = this;
        SelectedButtons = new ArrayList<>();
        TextView TemplateName = findViewById(R.id.textView57);
        TemplateName.setText(ViewTemplate.template.name);
        TextView MultiControlName = findViewById(R.id.textView53);
        MultiControlName.setText(MultiControl.name);
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
        drawMultiControlOnButtons();
    }

    void setActivityActions() {
        S1_1.setOnClickListener(setButtonListener(S1_1,new TemplateButton("Switch1",1)));
        S1_2.setOnClickListener(setButtonListener(S1_2,new TemplateButton("Switch1",2)));
        S1_3.setOnClickListener(setButtonListener(S1_3,new TemplateButton("Switch1",3)));
        S1_4.setOnClickListener(setButtonListener(S1_4,new TemplateButton("Switch1",4)));

        S2_1.setOnClickListener(setButtonListener(S2_1,new TemplateButton("Switch2",1)));
        S2_2.setOnClickListener(setButtonListener(S2_2,new TemplateButton("Switch2",2)));
        S2_3.setOnClickListener(setButtonListener(S2_3,new TemplateButton("Switch2",3)));
        S2_4.setOnClickListener(setButtonListener(S2_4,new TemplateButton("Switch2",4)));

        S3_1.setOnClickListener(setButtonListener(S3_1,new TemplateButton("Switch3",1)));
        S3_2.setOnClickListener(setButtonListener(S3_2,new TemplateButton("Switch3",2)));
        S3_3.setOnClickListener(setButtonListener(S3_3,new TemplateButton("Switch3",3)));
        S3_4.setOnClickListener(setButtonListener(S3_4,new TemplateButton("Switch3",4)));

        S4_1.setOnClickListener(setButtonListener(S4_1,new TemplateButton("Switch4",1)));
        S4_2.setOnClickListener(setButtonListener(S4_2,new TemplateButton("Switch4",2)));
        S4_3.setOnClickListener(setButtonListener(S4_3,new TemplateButton("Switch4",3)));
        S4_4.setOnClickListener(setButtonListener(S4_4,new TemplateButton("Switch4",4)));

        S5_1.setOnClickListener(setButtonListener(S5_1,new TemplateButton("Switch5",1)));
        S5_2.setOnClickListener(setButtonListener(S5_2,new TemplateButton("Switch5",2)));
        S5_3.setOnClickListener(setButtonListener(S5_3,new TemplateButton("Switch5",3)));
        S5_4.setOnClickListener(setButtonListener(S5_4,new TemplateButton("Switch5",4)));

        S6_1.setOnClickListener(setButtonListener(S6_1,new TemplateButton("Switch6",1)));
        S6_2.setOnClickListener(setButtonListener(S6_2,new TemplateButton("Switch6",2)));
        S6_3.setOnClickListener(setButtonListener(S6_3,new TemplateButton("Switch6",3)));
        S6_4.setOnClickListener(setButtonListener(S6_4,new TemplateButton("Switch6",4)));

        S7_1.setOnClickListener(setButtonListener(S7_1,new TemplateButton("Switch7",1)));
        S7_2.setOnClickListener(setButtonListener(S7_2,new TemplateButton("Switch7",2)));
        S7_3.setOnClickListener(setButtonListener(S7_3,new TemplateButton("Switch7",3)));
        S7_4.setOnClickListener(setButtonListener(S7_4,new TemplateButton("Switch7",4)));

        S8_1.setOnClickListener(setButtonListener(S8_1,new TemplateButton("Switch8",1)));
        S8_2.setOnClickListener(setButtonListener(S8_2,new TemplateButton("Switch8",2)));
        S8_3.setOnClickListener(setButtonListener(S8_3,new TemplateButton("Switch8",3)));
        S8_4.setOnClickListener(setButtonListener(S8_4,new TemplateButton("Switch8",4)));
    }

    void drawMultiControlOnButtons() {
        for (TemplateButton tb: MultiControl.multiControlButtons) {
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

    void setButtonClicked(Button b) {
        b.setBackgroundResource(R.drawable.btn_bg_normal_selected);
        SelectedButtons.add(b);
    }

    void setButtonUnClicked(Button b) {
        b.setBackgroundResource(R.drawable.btn_bg_selector);
        SelectedButtons.remove(b);
    }

    View.OnClickListener setButtonListener(Button b,TemplateButton TB) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SelectedButtons.contains(b)) {
                    setButtonUnClicked(b);
                    int x = TemplateButton.searchTemplateButton(MultiControl.multiControlButtons,TB);
                    if (x!= -1) {
                        MultiControl.multiControlButtons.remove(x);
                    }
                }
                else {
                    setButtonClicked(b);
                    MultiControl.multiControlButtons.add(TB);
                }
            }
        };
    }

    public void saveMultiControl(View view) {
        Log.d("editMulti",MultiControl.multiControlButtons.size()+" "+SelectedButtons.size());
        if (MultiControl.multiControlButtons.size() == 0) {
            new MessageDialog(getResources().getString(R.string.pleaseSelectButtons),getResources().getString(R.string.selectButtons),act);
            return ;
        }
        if (MultiControl.multiControlButtons.size() == 1) {
            new MessageDialog(getResources().getString(R.string.pleaseSelectOtherButtonsInMultiControl),getResources().getString(R.string.selectOtherButtons),act);
            return ;
        }
        try {
            MultiControl.saveMultiControlToFireBase(ViewTemplate.template.MultiReference);
            new MessageDialog("edited successfully ","Done",act,true);
        }
        catch (Exception e) {
            new MessageDialog(e.getMessage(),"error",act);
        }
    }
}