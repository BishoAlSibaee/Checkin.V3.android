package com.syriasoft.mobilecheckdevice;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.mobilecheckdevice.R;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IDeviceListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.Map;
import java.util.Objects;

public class SwitchControlDialog {
    private final Dialog D ;
    private final DeviceBean Device;
    private final ITuyaDevice tDevice;

    SwitchControlDialog(Context act, DeviceBean device) {
        D = new Dialog(act);
        Device = device ;
        tDevice = TuyaHomeSdk.newDeviceInstance(Device.devId);
        D.setContentView(R.layout.switch_control_dialog);
        D.setCancelable(false);
        Window w = D.getWindow();
        w.setBackgroundDrawableResource(R.color.transparent);
        TextView Name = D.findViewById(R.id.textView32r);
        Button close = D.findViewById(R.id.button33);
        close.setOnClickListener(view -> close());
        Button B1 = D.findViewById(R.id.button2725);
        Button B2 = D.findViewById(R.id.button192r);
        Button B3 = D.findViewById(R.id.button1726);
        Button B4 = D.findViewById(R.id.button2842);
        B1.setVisibility(View.GONE);
        B2.setVisibility(View.GONE);
        B3.setVisibility(View.GONE);
        B4.setVisibility(View.GONE);
        boolean[] v1,v2,v3,v4 ;
        v1= new boolean[]{false};v2=new boolean[]{false};v3=new boolean[]{false};v4=new boolean[]{false};
        if (Device != null) {
            Name.setText(Device.getName());
            if (Device.dps.get("1") != null) {
                B1.setVisibility(View.VISIBLE);
                v1[0] = Boolean.parseBoolean(Objects.requireNonNull(Device.dps.get("1")).toString());
                if (v1[0])
                    B1.setBackgroundResource(R.drawable.btn_bg_selector);
                else
                    B1.setBackgroundResource(R.drawable.btn_bg_pressed);
                B1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (v1[0]) {
                            tDevice.publishDps("{\"1\": false}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                        else {
                            tDevice.publishDps("{\"1\": true}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                    }
                });
            }
            if (Device.dps.get("2") != null) {
                B2.setVisibility(View.VISIBLE);
                v2[0] = Boolean.parseBoolean(Objects.requireNonNull(Device.dps.get("2")).toString());
                if (v2[0])
                    B2.setBackgroundResource(R.drawable.btn_bg_selector);
                else
                    B2.setBackgroundResource(R.drawable.btn_bg_pressed);
                B2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (v2[0]) {
                            tDevice.publishDps("{\"2\": false}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                        else {
                            tDevice.publishDps("{\"2\": true}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                    }
                });
            }
            if (Device.dps.get("3") != null) {
                B3.setVisibility(View.VISIBLE);
                v3[0] = Boolean.parseBoolean(Objects.requireNonNull(Device.dps.get("3")).toString());
                if (v3[0])
                    B3.setBackgroundResource(R.drawable.btn_bg_selector);
                else
                    B3.setBackgroundResource(R.drawable.btn_bg_pressed);
                B3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (v3[0]) {
                            tDevice.publishDps("{\"3\": false}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                        else {
                            tDevice.publishDps("{\"3\": true}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                    }
                });
            }
            if (Device.dps.get("4") != null) {
                B4.setVisibility(View.VISIBLE);
                v4[0] = Boolean.parseBoolean(Objects.requireNonNull(Device.dps.get("4")).toString());
                if (v4[0])
                    B4.setBackgroundResource(R.drawable.btn_bg_selector);
                else
                    B4.setBackgroundResource(R.drawable.btn_bg_pressed);
                B4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (v4[0]) {
                            tDevice.publishDps("{\"4\": false}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                        else {
                            tDevice.publishDps("{\"4\": true}", new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {

                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                    }
                });
            }
            tDevice.registerDeviceListener(new IDeviceListener() {
                @Override
                public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                    Log.d("controlDialog",dpStr.toString());
                    if (dpStr.get("switch_1") != null) {
                        if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_1")).toString())) {
                            B1.setBackgroundResource(R.drawable.btn_bg_selector);
                            v1[0] = true;
                        }
                        else {
                            B1.setBackgroundResource(R.drawable.btn_bg_pressed);
                            v1[0] = false;
                        }
                    }
                    if (dpStr.get("switch_2") != null) {
                        if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_2")).toString())) {
                            B2.setBackgroundResource(R.drawable.btn_bg_selector);
                            v2[0] = true;
                        }
                        else {
                            B2.setBackgroundResource(R.drawable.btn_bg_pressed);
                            v2[0] = false;
                        }
                    }
                    if (dpStr.get("switch_3") != null) {
                        if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_3")).toString())) {
                            B3.setBackgroundResource(R.drawable.btn_bg_selector);
                            v3[0] = true;
                        }
                        else {
                            B3.setBackgroundResource(R.drawable.btn_bg_pressed);
                            v3[0] = false;
                        }
                    }
                    if (dpStr.get("switch_4") != null) {
                        if (Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_4")).toString())) {
                            B4.setBackgroundResource(R.drawable.btn_bg_selector);
                            v4[0] = true;
                        }
                        else {
                            B4.setBackgroundResource(R.drawable.btn_bg_pressed);
                            v4[0] = false;
                        }
                    }
                }

                @Override
                public void onRemoved(String devId) {

                }

                @Override
                public void onStatusChanged(String devId, boolean online) {

                }

                @Override
                public void onNetworkStatusChanged(String devId, boolean status) {

                }

                @Override
                public void onDevInfoUpdate(String devId) {

                }
            });
        }
    }

    void show() {
        D.show();
    }

    void close() {
        tDevice.unRegisterDevListener();
        D.dismiss();
    }
}
