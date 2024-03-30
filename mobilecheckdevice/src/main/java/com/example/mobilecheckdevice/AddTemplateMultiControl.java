package com.example.mobilecheckdevice;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AddTemplateMultiControl extends AppCompatActivity {

    Activity act;
    Button S1_1C,S1_2C,S1_3C,S1_4C,S2_1C,S2_2C,S2_3C,S2_4C,S3_1C,S3_2C,S3_3C,S3_4C,S4_1C,S4_2C,S4_3C,S4_4C ,S5_1C,S5_2C,S5_3C,S5_4C ,S6_1C,S6_2C,S6_3C,S6_4C ,S7_1C,S7_2C,S7_3C,S7_4C ,S8_1C,S8_2C,S8_3C,S8_4C;
    List<Button> MultiButtons;
    List<TemplateButton> MultiControlSelectedButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_template_multi_control);
        setActivity();
        setActivityActions();
    }

    void setActivity() {
        act = this;
        MultiButtons = new ArrayList<>();
        MultiControlSelectedButtons = new ArrayList<>();
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
    }

    void setActivityActions() {
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
        try {
            TemplateMultiControl tmc = new TemplateMultiControl(String.valueOf(x)+y,MultiControlSelectedButtons);
            tmc.saveMultiControlToFireBase(ViewTemplate.template.MultiReference);
            ViewTemplate.template.multiControls.add(tmc);
            new MessageDialog("multi control "+x+y+" created","Done",act,true);
        }
        catch (Exception e){
            new MessageDialog(e.getMessage(),"error",act);
        }
    }
}