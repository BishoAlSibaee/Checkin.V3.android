package com.syriasoft.server.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.hotelservicesstandalone.R;
import com.syriasoft.server.Classes.Property.Bed;
import com.syriasoft.server.ReceptionScreen;
import com.tuya.smart.sdk.api.IResultCallback;

import java.text.MessageFormat;

public class PowerControlDialog {
    Dialog d;
    Context c;
    TextView room;
    Button powerOn,powerOff,powerCard;

    public PowerControlDialog(Context c, Bed b) {
        this.c = c;
        d = new Dialog(this.c);
        d.setContentView(R.layout.power_control_dialog);
        powerOn = d.findViewById(R.id.imageView10);
        powerCard = d.findViewById(R.id.imageView11);
        powerOff = d.findViewById(R.id.imageView12);
        room = d.findViewById(R.id.textView17);
        if (ReceptionScreen.powerOnBeds.contains(b)) {
            powerOn.setVisibility(View.INVISIBLE);
        }
        if (ReceptionScreen.powerOffBeds.contains(b)) {
            powerOff.setVisibility(View.INVISIBLE);
        }
        if (ReceptionScreen.powerCardBeds.contains(b)) {
            powerCard.setVisibility(View.INVISIBLE);
        }
        if (b.isRoom()) {
            if (b.room != null) {
                room.setText(String.valueOf(b.room.RoomNumber));
                if (b.room.getPowerModule() != null) {
                    if (b.room.getPowerModule().dp1 != null && b.room.getPowerModule().dp2 != null) {
                        powerOn.setOnClickListener(view -> {
                            b.room.getPowerModule().powerOnOffline(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    d.dismiss();
                                    new MessageDialog(error,"error",c);
                                }

                                @Override
                                public void onSuccess() {
                                    d.dismiss();
                                }
                            });
                        });
                        powerOff.setOnClickListener(view->{
                            b.room.getPowerModule().powerOffOffline(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    d.dismiss();
                                    new MessageDialog(error,"error",c);
                                }

                                @Override
                                public void onSuccess() {
                                    d.dismiss();
                                }
                            });
                        });
                        powerCard.setOnClickListener(view->{
                            b.room.getPowerModule().powerByCardOffline(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    d.dismiss();
                                    new MessageDialog(error,"error",c);
                                }

                                @Override
                                public void onSuccess() {
                                    d.dismiss();
                                }
                            });
                        });
                    }
                }
            }
        }
        else if (b.isSuite()) {
            if (b.suite != null) {
                room.setText(MessageFormat.format("S{0}", b.suite.SuiteNumber));
                if (b.suite.getPowerModule() != null) {
                    if (b.suite.getPowerModule().dp1 != null && b.suite.getPowerModule().dp2 != null) {
                        powerOn.setOnClickListener(view -> {
                            b.suite.getPowerModule().powerOnOffline(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    d.dismiss();
                                    new MessageDialog(error,"error",c);
                                }

                                @Override
                                public void onSuccess() {
                                    d.dismiss();
                                }
                            });
                        });
                        powerOff.setOnClickListener(view->{
                            b.suite.getPowerModule().powerOffOffline(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    d.dismiss();
                                    new MessageDialog(error,"error",c);
                                }

                                @Override
                                public void onSuccess() {
                                    d.dismiss();
                                }
                            });
                        });
                        powerCard.setOnClickListener(view->{
                            b.suite.getPowerModule().powerByCardOffline(new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    d.dismiss();
                                    new MessageDialog(error,"error",c);
                                }

                                @Override
                                public void onSuccess() {
                                    d.dismiss();
                                }
                            });
                        });
                    }
                }
            }
        }
        d.show();
    }
}
