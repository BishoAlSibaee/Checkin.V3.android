package com.example.mobilecheckdevice;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IDeviceListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.Map;
import java.util.Objects;

public class RGBControlDialog {

    private final Dialog D ;
    private final ITuyaDevice tDevice;
    RGBController controller;
    boolean isOn = false;

    RGBControlDialog(Context act, DeviceBean device,RGBController controller) {
        D = new Dialog(act);
        tDevice = TuyaHomeSdk.newDeviceInstance(device.devId);
        this.controller = controller;
        D.setContentView(R.layout.r_g_b_control_dialog);
        D.setCancelable(false);
        Window w = D.getWindow();
        w.setBackgroundDrawableResource(R.color.transparent);
        TextView Name = D.findViewById(R.id.textView32r);
        Button close = D.findViewById(R.id.button33);
        close.setOnClickListener(view -> close());
        ImageButton OnOff = D.findViewById(R.id.button192r);
        isOn = Boolean.parseBoolean(Objects.requireNonNull(device.getDps().get(String.valueOf(controller.powerDp))).toString());
        OnOff.setOnClickListener(view -> {
            if (isOn) {
                tDevice.publishDps("{\""+controller.powerDp+"\": false}", new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {

                    }

                    @Override
                    public void onSuccess() {

                    }
                });
            }
            else {
                tDevice.publishDps("{\""+controller.powerDp+"\": true}", new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {

                    }

                    @Override
                    public void onSuccess() {

                    }
                });
            }
        });
        Name.setText(device.getName());
        SeekBar bright,color;
        bright = D.findViewById(R.id.seekBar);
        color = D.findViewById(R.id.seekBar2);

        bright.setMax(controller.brightMax);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            bright.setMin(controller.brightMin);
        }
        bright.setProgress(Integer.parseInt(Objects.requireNonNull(device.dps.get(String.valueOf(controller.brightnessDp))).toString()));

        color.setMax(controller.colorMax);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            color.setMin(controller.colorMin);
        }
        color.setProgress(Integer.parseInt(Objects.requireNonNull(device.dps.get(String.valueOf(controller.colorDp))).toString()));

        bright.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tDevice.publishDps("{\""+controller.brightnessDp+"\": "+seekBar.getProgress()+"}", new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        Log.d("rgbAction",code+" "+error);
                    }

                    @Override
                    public void onSuccess() {
                        Log.d("rgbAction","success "+bright.getProgress());
                    }
                });
            }
        });

        color.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tDevice.publishDps("{\""+controller.colorDp+"\": "+seekBar.getProgress()+"}", new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        Log.d("rgbAction",code+" "+error);
                    }

                    @Override
                    public void onSuccess() {
                        Log.d("rgbAction","success "+color.getProgress());
                    }
                });
            }
        });

        tDevice.registerDeviceListener(new IDeviceListener() {
            @Override
            public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                Log.d("rgbAction",dpStr.toString());
                if (dpStr.get("switch_led") != null) {
                    isOn = Boolean.parseBoolean(Objects.requireNonNull(dpStr.get("switch_led")).toString());
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

    void show() {
        D.show();
    }

    void close() {
        tDevice.unRegisterDevListener();
        D.dismiss();
    }
}
