package com.syriasoft.mobilecheckdevice;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobilecheckdevice.R;
import com.syriasoft.mobilecheckdevice.Interface.RequestOrder;
import com.syriasoft.mobilecheckdevice.Classes.ZigbeeLock;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IDeviceListener;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LockControlDialog {

    private Dialog D;
    private DeviceBean Lock;
    private ITuyaDevice tLock;

    LockControlDialog(Context c , DeviceBean d) {
        this.D = new Dialog(c);
        this.Lock = d ;
        tLock = TuyaHomeSdk.newDeviceInstance(Lock.devId);
        D.setContentView(R.layout.lock_control_dialog);
        D.setCancelable(false);
        Window w = D.getWindow();
        w.setBackgroundDrawableResource(R.color.transparent);
        TextView Name = D.findViewById(R.id.textView32r);
        Name.setText(Lock.getName());
        ImageView img = D.findViewById(R.id.imageView6);
        Button close = D.findViewById(R.id.button33);
        close.setOnClickListener(view -> close());
        Button open = D.findViewById(R.id.button2725);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ZigbeeLock.getTokenFromApi(MyApp.cloudClientId, MyApp.cloudSecret, c, new RequestOrder() {
                    @Override
                    public void onSuccess(String token) {
                        Log.d("doorOpenResp" , "token "+token);
                        ZigbeeLock.getTicketId(token, MyApp.cloudClientId, MyApp.cloudSecret, Lock.devId, c, new RequestOrder() {
                            @Override
                            public void onSuccess(String ticket) {
                                Log.d("doorOpenResp" , "ticket "+ticket);
                                ZigbeeLock.unlockWithoutPassword(token, ticket, MyApp.cloudClientId, MyApp.cloudSecret, Lock.devId, c, new RequestOrder() {
                                    @Override
                                    public void onSuccess(String res) {
                                        Log.d("doorOpenResp" , "res "+res);
                                    }

                                    @Override
                                    public void onFailed(String error) {
                                        Log.d("openDoorResp" , "res "+error);
                                    }
                                });
                            }

                            @Override
                            public void onFailed(String error) {
                                Log.d("doorOpenResp" , "ticket "+error);
                            }
                        });
                    }

                    @Override
                    public void onFailed(String error) {
                        Log.d("doorOpenResp" , "token "+error);
                    }
                });
            }
        });
        tLock.registerDeviceListener(new IDeviceListener() {
            @Override
            public void onDpUpdate(String devId, Map<String, Object> dpStr) {
                Log.d("doorOpenLis" , dpStr.toString());
                if (dpStr.get("remote_no_dp_key") != null) {
                    img.setImageResource(R.drawable.lock_open);
                    Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            img.setImageResource(R.drawable.lock_close);
                        }
                    },2000);
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
        D.dismiss();
    }
}
