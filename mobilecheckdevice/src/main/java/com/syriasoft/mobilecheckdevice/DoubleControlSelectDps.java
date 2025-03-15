package com.syriasoft.mobilecheckdevice;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilecheckdevice.R;
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

public class DoubleControlSelectDps extends AppCompatActivity {

    Activity act ;
    DeviceBean First ,Second ;
    LinearLayout FirstLayout , SecondLayout ;
    int FirstDP=0 , SecondDP=0 ;
    ITuyaDeviceMultiControl iTuyaDeviceMultiControl ;
    TextView fName, sName, fButton, sButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_control_select_dps);
        First = LightingDoubleControl.FIRST ;
        Second = LightingDoubleControl.SECOND ;
        setActivity();
    }

    void setActivity() {
        act = this ;
        FirstLayout = findViewById(R.id.firestDeviceLayout);
        SecondLayout = findViewById(R.id.secondDeviceLayout);
        fName = findViewById(R.id.device1_name);
        sName = findViewById(R.id.device2_name);
        fButton = findViewById(R.id.device1_button);
        sButton = findViewById(R.id.device2_button);
        iTuyaDeviceMultiControl = TuyaHomeSdk.getDeviceMultiControlInstance();
        if(First != null ) {
            fName.setText(First.getName());
            List keys = new ArrayList(First.getDps().keySet());
            for( int i=0; i< keys.size();i++) {
                if (Integer.parseInt(keys.get(i).toString()) < 5) {
                    Button  f = new Button(act);
                    f.setText(keys.get(i).toString());
                    int finalI = i;
                    f.setOnClickListener(v -> {
                        FirstDP = Integer.parseInt(keys.get(finalI).toString());
                        fButton.setText(keys.get(finalI).toString());
                    });
                    FirstLayout.addView(f);
                }
            }
        }
        if(Second != null ) {
            sName.setText(Second.getName());
            List keys = new ArrayList(Second.getDps().keySet());
            for( int i=0; i< keys.size();i++) {
                if (Integer.parseInt(keys.get(i).toString()) < 5) {
                    Button  f = new Button(act);
                    f.setText(keys.get(i).toString());
                    int finalI = i;
                    f.setOnClickListener(v -> {
                        SecondDP = Integer.parseInt(keys.get(finalI).toString());
                        sButton.setText(keys.get(finalI).toString());
                    });
                    SecondLayout.addView(f);
                }
            }
        }
    }

    public void createDoubleControl(View view) {

        if (First != null && Second != null && FirstDP != 0 && SecondDP !=0  ) {
            Random r = new Random();
            int x = r.nextInt(30);
            JSONObject groupdetailes1 = new JSONObject(), groupdetailes2 = new JSONObject();
            try {
                groupdetailes1.put("devId", First.devId);
                groupdetailes1.put("dpId", FirstDP);
                groupdetailes1.put("id", x);
                groupdetailes1.put("enable", true);

            } catch (JSONException e) {
                Toast.makeText(act,"failed "+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
            try {
                groupdetailes2.put("devId", Second.devId);
                groupdetailes2.put("dpId", SecondDP);
                groupdetailes2.put("id", x);
                groupdetailes2.put("enable", true);

            } catch (JSONException e) {
                Toast.makeText(act,"failed "+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
            JSONArray arr = new JSONArray();
            arr.put(groupdetailes2);
            arr.put(groupdetailes1);
            JSONObject multiControlBean = new JSONObject();
            try {
                multiControlBean.put("groupName", RoomManager.Room.RoomNumber + "Lighting" + x);
                multiControlBean.put("groupType", 1);
                multiControlBean.put("groupDetail", arr);
                multiControlBean.put("id", x);
            } catch (JSONException e) {
                Toast.makeText(act,"failed "+e.getMessage(),Toast.LENGTH_SHORT).show();
            }

            iTuyaDeviceMultiControl.saveDeviceMultiControl(MyApp.homeBeans.get(0).getHomeId(), multiControlBean.toString(), new ITuyaResultCallback<MultiControlBean>() {
                @Override
                public void onSuccess(MultiControlBean result) {
                    Toast.makeText(act,"double control created",Toast.LENGTH_SHORT).show();
                    Log.d("switch1Dp1", result.getGroupName());
                    iTuyaDeviceMultiControl.enableMultiControl(x, new ITuyaResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            Log.d("switch1Dp1", result.toString());
                            Toast.makeText(act,"double control enabled",Toast.LENGTH_SHORT).show();
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
        else {
            Toast.makeText(act,"please select the buttons ",Toast.LENGTH_SHORT).show();
        }

    }
}