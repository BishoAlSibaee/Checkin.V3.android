package com.syriasoft.mobilecheckdevice;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mobilecheckdevice.R;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.Objects;

public class CurtainControlDialog {

    private final Dialog D ;
    private final ITuyaDevice tDevice;

    public CurtainControlDialog(Context act, DeviceBean device, CurtainController controller) {
        D = new Dialog(act);
        tDevice = TuyaHomeSdk.newDeviceInstance(device.devId);
        D.setContentView(R.layout.curtain_control_dialog);
        D.setCancelable(false);
        Window w = D.getWindow();
        w.setBackgroundDrawableResource(R.color.transparent);
        TextView Name = D.findViewById(R.id.textView32r);
        Button close = D.findViewById(R.id.button33);
        close.setOnClickListener(view -> close());
        Name.setText(device.getName());
        Button openC,closeC,stopC,continueC;
        openC = D.findViewById(R.id.button192r);
        openC.setOnClickListener(view -> tDevice.publishDps("{\""+controller.controlDp+"\": \""+controller.open+"\"}", new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                Log.d("curtainControl",code+" "+error);
            }

            @Override
            public void onSuccess() {
                Log.d("curtainControl","success");
            }
        }));
        closeC = D.findViewById(R.id.button192rq);
        closeC.setOnClickListener(view -> tDevice.publishDps("{\""+controller.controlDp+"\": \""+controller.close+"\"}", new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                Log.d("curtainControl",code+" "+error);
            }

            @Override
            public void onSuccess() {
                Log.d("curtainControl","success");
            }
        }));
        stopC = D.findViewById(R.id.button192rw);
        stopC.setOnClickListener(view -> tDevice.publishDps("{\""+controller.controlDp+"\": \""+controller.stop+"\"}", new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                Log.d("curtainControl",code+" "+error);
            }

            @Override
            public void onSuccess() {
                Log.d("curtainControl","success");
            }
        }));
        continueC = D.findViewById(R.id.button192re);
        continueC.setOnClickListener(view -> tDevice.publishDps("{\""+controller.controlDp+"\": \""+controller.continue_+"\"}", new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                Log.d("curtainControl",code+" "+error);
            }

            @Override
            public void onSuccess() {
                Log.d("curtainControl","success");
            }
        }));
        if (controller.open == null) {
            openC.setVisibility(View.GONE);
        }
        if (controller.close == null) {
            closeC.setVisibility(View.GONE);
        }
        if (controller.stop == null) {
            stopC.setVisibility(View.GONE);
        }
        if (controller.continue_ == null) {
            continueC.setVisibility(View.GONE);
        }
        SeekBar percentage = D.findViewById(R.id.seekBar);
        percentage.setMax(controller.percentageMax);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            percentage.setMin(controller.percentageMin);
        }
        if (device.getDps().get(String.valueOf(controller.percentageDp)) != null) {
            percentage.setProgress(Integer.parseInt(Objects.requireNonNull(device.getDps().get(String.valueOf(controller.percentageDp))).toString()));
            percentage.incrementProgressBy(10);
            percentage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    tDevice.publishDps("{\""+controller.percentageDp+"\": "+percentage.getProgress()+"}", new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {
                            Log.d("curtainControl",code+" "+error);
                        }

                        @Override
                        public void onSuccess() {
                            Log.d("curtainControl","success");
                        }
                    });
                }
            });
        }
        if (controller.percentageDp == 0) {
            percentage.setVisibility(View.GONE);
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
