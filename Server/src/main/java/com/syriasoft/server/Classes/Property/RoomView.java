package com.syriasoft.server.Classes.Property;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.hotelservicesstandalone.R;
import com.syriasoft.server.ReceptionScreen;
import com.tuya.smart.sdk.api.IResultCallback;

import java.text.MessageFormat;
import java.util.List;

@SuppressLint("ViewConstructor")
public class RoomView extends LinearLayout {

    Context context;
    public Room room;
    public Suite suite;
    ImageView cleanup,laundry,dnd,checkout;
    TextView room_number;
    TextView powerStatus;

    View me;

    public static boolean cleanupFilter,laundryFilter,dndFilter,checkoutFilter,allFilter;
    public static int cleanupCounter,laundryCounter,dndCounter,checkoutCounter;

    public RoomView(Context context,Room room) {
        super(context);
        this.room = room;
        this.context = context;
        RoomView.laundryFilter = false;
        RoomView.cleanupFilter = false;
        RoomView.dndFilter = false;
        RoomView.checkoutFilter = false;
        RoomView.allFilter = true;
    }
    public RoomView(Context context,Suite suite) {
        super(context);
        this.suite = suite;
        this.context = context;
        RoomView.laundryFilter = false;
        RoomView.cleanupFilter = false;
        RoomView.dndFilter = false;
        RoomView.checkoutFilter = false;
        RoomView.allFilter = true;
    }

    public View createRoomView() {
        LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams x = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1);
        x.setMargins(5,5,5,0);
        l.setPadding(5,5,5,5);
        l.setBackgroundResource(R.color.colorAccent);
        l.setLayoutParams(x);
        cleanup = new ImageView(context);
        laundry = new ImageView(context);
        dnd = new ImageView(context);
        checkout = new ImageView(context);
        room_number = new TextView(context);
        room_number.setText(String.valueOf(room.RoomNumber));
        room_number.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        room_number.setTextColor(Color.WHITE);
        room_number.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        room_number.setTextSize(20);
        room_number.setTypeface(Typeface.DEFAULT_BOLD);
        l.addView(room_number);
        LinearLayout.LayoutParams c = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        c.gravity = Gravity.CENTER;
        cleanup.setLayoutParams(c);
        LinearLayout.LayoutParams lau = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        lau.gravity = Gravity.CENTER;
        laundry.setLayoutParams(lau);
        LinearLayout.LayoutParams dn = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        dn.gravity = Gravity.CENTER;
        dnd.setLayoutParams(dn);
        LinearLayout.LayoutParams ch = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        ch.gravity = Gravity.CENTER;
        checkout.setLayoutParams(ch);
        cleanup.setImageResource(R.drawable.cleanup_btn);
        laundry.setImageResource(R.drawable.laundry_btn);
        dnd.setImageResource(R.drawable.dnd);
        checkout.setImageResource(R.drawable.checkout);
        cleanup.setVisibility(GONE);
        laundry.setVisibility(GONE);
        dnd.setVisibility(GONE);
        checkout.setVisibility(GONE);
        cleanup.setPadding(5,5,5,5);
        laundry.setPadding(5,5,5,5);
        dnd.setPadding(5,5,5,5);
        laundry.setPadding(5,5,5,5);
        laundry.setOnClickListener(view -> {
            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setTitle(room.RoomNumber+" Laundry Off").setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (room != null) {
                        if (room.getMainServiceSwitch() != null) {
                            if (room.getMainServiceSwitch().laundry != null) {
                                room.laundryRoomOff(new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                            }
                        }
                    }
                }
            }).create().show();

        });
        cleanup.setOnClickListener(view->{
            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setTitle(room.RoomNumber+" Cleanup Off").setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (room != null) {
                        if (room.getMainServiceSwitch() != null) {
                            if (room.getMainServiceSwitch().cleanup != null) {
                                room.cleanupRoomOff(new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                            }
                        }
                    }
                }
            }).create().show();

        });
        dnd.setOnClickListener(view->{
            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setTitle(room.RoomNumber+" DND Off").setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (room != null) {
                        if (room.getMainServiceSwitch() != null) {
                            if (room.getMainServiceSwitch().dnd != null) {
                                room.dndRoomOff(new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                            }
                        }
                    }
                }
            }).create().show();
        });
        checkout.setOnClickListener(view->{
            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setTitle(room.RoomNumber+" Checkout Off").setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (room != null) {
                                if (room.getMainServiceSwitch() != null) {
                                    if (room.getMainServiceSwitch().checkout != null) {
                                        room.checkoutRoomOff(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {

                                            }

                                            @Override
                                            public void onSuccess() {

                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }).create().show();

        });
        LinearLayout imagesLayout = new LinearLayout(context);
        imagesLayout.setOrientation(LinearLayout.HORIZONTAL);
        imagesLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));
        imagesLayout.addView(cleanup);
        imagesLayout.addView(laundry);
        imagesLayout.addView(dnd);
        imagesLayout.addView(checkout);
        imagesLayout.setPadding(5,10,5,10);
        LinearLayout buttonsLayout = new LinearLayout(context);
        buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonsLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,100));
        ImageButton powerOff = new ImageButton(context);
        ImageButton powerCard = new ImageButton(context);
        ImageButton powerOn = new ImageButton(context);
        powerOff.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1));
        powerCard.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1));
        powerOn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1));
        powerOff.setImageResource(R.drawable.power_off);
        powerOn.setImageResource(R.drawable.power_);
        powerCard.setImageResource(R.drawable.card);
        powerOn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        powerOff.setScaleType(ImageView.ScaleType.FIT_CENTER);
        powerCard.setScaleType(ImageView.ScaleType.FIT_CENTER);
        powerOff.setOnClickListener(view ->{
            room.getPowerModule().powerOffOffline(new IResultCallback() {
                @Override
                public void onError(String code, String error) {

                }

                @Override
                public void onSuccess() {

                }
            });
        });
        powerCard.setOnClickListener(view->{
            room.getPowerModule().powerByCardOffline(new IResultCallback() {
                @Override
                public void onError(String code, String error) {

                }

                @Override
                public void onSuccess() {

                }
            });
        });
        powerOn.setOnClickListener(view->{
            room.getPowerModule().powerOnOffline(new IResultCallback() {
                @Override
                public void onError(String code, String error) {

                }

                @Override
                public void onSuccess() {

                }
            });
        });
        buttonsLayout.addView(powerOn);
        buttonsLayout.addView(powerCard);
        buttonsLayout.addView(powerOff);
        powerStatus = new TextView(context);
        powerStatus.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        powerStatus.setTextColor(Color.WHITE);
        powerStatus.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        if (!room.isHasPower()) {
            buttonsLayout.setVisibility(INVISIBLE);
            powerStatus.setVisibility(INVISIBLE);
        }
        l.addView(imagesLayout);
        l.addView(powerStatus);
        l.addView(buttonsLayout);
        me = l;
        return l;
    }

    public View createSuiteView() {
        LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams x = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1);
        x.setMargins(5,5,5,0);
        l.setPadding(5,5,5,5);
        l.setBackgroundResource(R.color.colorAccent);
        l.setLayoutParams(x);
        cleanup = new ImageView(context);
        laundry = new ImageView(context);
        dnd = new ImageView(context);
        checkout = new ImageView(context);
        room_number = new TextView(context);
        room_number.setText(MessageFormat.format("S{0}", suite.SuiteNumber));
        room_number.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        room_number.setTextColor(Color.WHITE);
        room_number.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        room_number.setTextSize(20);
        room_number.setTypeface(Typeface.DEFAULT_BOLD);
        l.addView(room_number);
        LinearLayout.LayoutParams c = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        c.gravity = Gravity.CENTER;
        cleanup.setLayoutParams(c);
        LinearLayout.LayoutParams lau = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        lau.gravity = Gravity.CENTER;
        laundry.setLayoutParams(lau);
        LinearLayout.LayoutParams dn = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        dn.gravity = Gravity.CENTER;
        dnd.setLayoutParams(dn);
        LinearLayout.LayoutParams ch = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1);
        ch.gravity = Gravity.CENTER;
        checkout.setLayoutParams(ch);
        cleanup.setImageResource(R.drawable.cleanup_btn);
        laundry.setImageResource(R.drawable.laundry_btn);
        dnd.setImageResource(R.drawable.dnd);
        checkout.setImageResource(R.drawable.checkout);
        cleanup.setVisibility(GONE);
        laundry.setVisibility(GONE);
        dnd.setVisibility(GONE);
        checkout.setVisibility(GONE);
        cleanup.setPadding(5,5,5,5);
        laundry.setPadding(5,5,5,5);
        dnd.setPadding(5,5,5,5);
        laundry.setPadding(5,5,5,5);
        laundry.setOnClickListener(view -> {
            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setTitle(suite.SuiteNumber+" Laundry Off").setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (suite != null) {
                        if (suite.getMainServiceSwitch() != null) {
                            if (suite.getMainServiceSwitch().laundry != null) {
                                suite.laundrySuiteOff(new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                            }
                        }
                    }
                }
            }).create().show();

        });
        cleanup.setOnClickListener(view->{
            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setTitle(suite.SuiteNumber+" Cleanup Off").setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (suite != null) {
                        if (suite.getMainServiceSwitch() != null) {
                            if (suite.getMainServiceSwitch().cleanup != null) {
                                suite.cleanupSuiteOff(new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                            }
                        }
                        else {
                            Toast.makeText(context,"device null",Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }).create().show();

        });
        dnd.setOnClickListener(view->{
            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setTitle(suite.SuiteNumber+" DND Off").setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (suite != null) {
                        if (suite.getMainServiceSwitch() != null) {
                            if (suite.getMainServiceSwitch().dnd != null) {
                                suite.dndSuiteOff(new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                            }
                        }
                    }
                }
            }).create().show();
        });
        checkout.setOnClickListener(view->{
            AlertDialog.Builder b = new AlertDialog.Builder(context);
            b.setTitle(suite.SuiteNumber+" Checkout Off").setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (suite != null) {
                        if (suite.getMainServiceSwitch() != null) {
                            if (suite.getMainServiceSwitch().checkout != null) {
                                suite.checkoutSuiteOff(new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {

                                    }

                                    @Override
                                    public void onSuccess() {

                                    }
                                });
                            }
                        }
                    }
                }
            }).create().show();

        });
        LinearLayout imagesLayout = new LinearLayout(context);
        imagesLayout.setOrientation(LinearLayout.HORIZONTAL);
        imagesLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));
        imagesLayout.addView(cleanup);
        imagesLayout.addView(laundry);
        imagesLayout.addView(dnd);
        imagesLayout.addView(checkout);
        imagesLayout.setPadding(5,10,5,10);
        LinearLayout buttonsLayout = new LinearLayout(context);
        buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonsLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,100));
        ImageButton powerOff = new ImageButton(context);
        ImageButton powerCard = new ImageButton(context);
        ImageButton powerOn = new ImageButton(context);
        powerOff.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1));
        powerCard.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1));
        powerOn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1));
        powerOff.setImageResource(R.drawable.power_off);
        powerOn.setImageResource(R.drawable.power_);
        powerCard.setImageResource(R.drawable.card);
        powerOn.setScaleType(ImageView.ScaleType.FIT_CENTER);
        powerOff.setScaleType(ImageView.ScaleType.FIT_CENTER);
        powerCard.setScaleType(ImageView.ScaleType.FIT_CENTER);
        powerOff.setOnClickListener(view ->{
            suite.getPowerModule().powerOffOffline(new IResultCallback() {
                @Override
                public void onError(String code, String error) {

                }

                @Override
                public void onSuccess() {

                }
            });
        });
        powerCard.setOnClickListener(view->{
            suite.getPowerModule().powerByCardOffline(new IResultCallback() {
                @Override
                public void onError(String code, String error) {

                }

                @Override
                public void onSuccess() {

                }
            });
        });
        powerOn.setOnClickListener(view->{
            suite.getPowerModule().powerOnOffline(new IResultCallback() {
                @Override
                public void onError(String code, String error) {

                }

                @Override
                public void onSuccess() {

                }
            });
        });
        buttonsLayout.addView(powerOn);
        buttonsLayout.addView(powerCard);
        buttonsLayout.addView(powerOff);
        powerStatus = new TextView(context);
        powerStatus.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        powerStatus.setTextColor(Color.WHITE);
        powerStatus.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        if (!suite.isHasPower()) {
            buttonsLayout.setVisibility(INVISIBLE);
            powerStatus.setVisibility(INVISIBLE);
        }
        l.addView(imagesLayout);
        l.addView(powerStatus);
        l.addView(buttonsLayout);
        me = l;
        return l;
    }

    public void hideCleanup() {
        cleanup.setVisibility(GONE);
    }

    public void viewCleanup() {
        if (allFilter || cleanupFilter) {
            cleanup.setVisibility(VISIBLE);
        }
    }

    public void hideLaundry() {
        laundry.setVisibility(GONE);
    }

    public void viewLaundry() {
        if (allFilter || laundryFilter) {
            laundry.setVisibility(VISIBLE);
        }
    }

    public void hideDND() {
        dnd.setVisibility(GONE);
    }

    public void viewDND() {
        if (allFilter || dndFilter) {
            dnd.setVisibility(VISIBLE);
        }
    }

    public void hideCheckout() {
        checkout.setVisibility(GONE);
    }

    public void viewCheckout() {
        if (allFilter || checkoutFilter) {
            checkout.setVisibility(VISIBLE);
        }
    }

    public void setPowerStatusOn() {
        powerStatus.setText("Power On");
    }

    public void setPowerStatusOff() {
        powerStatus.setText("Power Off");
    }

    public void setPowerStatusCard() {
        powerStatus.setText("Power By Card");
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }

    public static void filterCleanup(List<RoomView> rvs) {
        cleanupFilter = true;
        allFilter = false;
        laundryFilter = false;
        dndFilter = false;
        checkoutFilter = false;
        for (RoomView rv : rvs) {
            rv.hideCheckout();
            rv.hideDND();
            rv.hideLaundry();
            if (rv.room != null) {
                if (rv.room.getMainServiceSwitch() != null) {
                    if (rv.room.getMainServiceSwitch().cleanup != null) {
                        if (rv.room.getMainServiceSwitch().cleanup.getCurrent()) {
                            rv.viewCleanup();
                            rv.me.setVisibility(VISIBLE);
                        }
                        else {
                            rv.me.setVisibility(GONE);
                        }
                    }
                    else {
                        rv.me.setVisibility(GONE);
                    }
                }
                else {
                    rv.me.setVisibility(GONE);
                }
            }
            if (rv.suite != null) {
                Log.d("suiteProblem",rv.suite.SuiteNumber+" not null");
                if (rv.suite.getMainServiceSwitch() != null) {
                    Log.d("suiteProblem",rv.suite.SuiteNumber+" service not null");
                    if (rv.suite.getMainServiceSwitch().cleanup != null) {
                        Log.d("suiteProblem",rv.suite.SuiteNumber+" cleanup not null");
                        if (rv.suite.getMainServiceSwitch().cleanup.getCurrent()) {
                            Log.d("suiteProblem",rv.suite.SuiteNumber+" cleanup true");
                            rv.viewCleanup();
                            rv.me.setVisibility(VISIBLE);
                        }
                        else {
                            Log.d("suiteProblem",rv.suite.SuiteNumber+" cleanup false");
                            rv.me.setVisibility(GONE);
                        }
                    }
                    else {
                        rv.me.setVisibility(GONE);
                    }
                }
                else {
                    rv.me.setVisibility(GONE);
                }
            }
        }
    }

    public static void filterLaundry(@NonNull List<RoomView> rvs) {
        cleanupFilter = false;
        allFilter = false;
        laundryFilter = true;
        dndFilter = false;
        checkoutFilter = false;
        for (RoomView rv : rvs) {
            rv.hideCheckout();
            rv.hideDND();
            rv.hideCleanup();
            if (rv.room != null) {
                if (rv.room.getMainServiceSwitch() != null) {
                    if (rv.room.getMainServiceSwitch().laundry != null) {
                        if (rv.room.getMainServiceSwitch().laundry.getCurrent()) {
                            rv.viewLaundry();
                            rv.me.setVisibility(VISIBLE);
                        }
                        else {
                            rv.me.setVisibility(GONE);
                        }
                    }
                    else {
                        rv.me.setVisibility(GONE);
                    }
                }
                else {
                    rv.me.setVisibility(GONE);
                }
            }
            if (rv.suite != null) {
                if (rv.suite.getMainServiceSwitch() != null) {
                    if (rv.suite.getMainServiceSwitch().laundry != null) {
                        if (rv.suite.getMainServiceSwitch().laundry.getCurrent()) {
                            rv.viewLaundry();
                            rv.me.setVisibility(VISIBLE);
                        }
                        else {
                            rv.me.setVisibility(GONE);
                        }
                    }
                    else {
                        rv.me.setVisibility(GONE);
                    }
                }
                else {
                    rv.me.setVisibility(GONE);
                }
            }
        }
    }

    public static void filterDnd(List<RoomView> rvs) {
        cleanupFilter = false;
        allFilter = false;
        laundryFilter = false;
        dndFilter = true;
        checkoutFilter = false;
        for (RoomView rv : rvs) {
            rv.hideCheckout();
            rv.hideLaundry();
            rv.hideCleanup();
            if (rv.room != null) {
                if (rv.room.getMainServiceSwitch() != null) {
                    if (rv.room.getMainServiceSwitch().dnd != null) {
                        if (rv.room.getMainServiceSwitch().dnd.getCurrent()) {
                            rv.viewDND();
                            rv.me.setVisibility(VISIBLE);
                        }
                        else {
                            rv.me.setVisibility(GONE);
                        }
                    }
                    else {
                        rv.me.setVisibility(GONE);
                    }
                }
                else {
                    rv.me.setVisibility(GONE);
                }
            }
            if (rv.suite != null) {
                if (rv.suite.getMainServiceSwitch() != null) {
                    if (rv.suite.getMainServiceSwitch().dnd != null) {
                        if (rv.suite.getMainServiceSwitch().dnd.getCurrent()) {
                            rv.viewDND();
                            rv.me.setVisibility(VISIBLE);
                        }
                        else {
                            rv.me.setVisibility(GONE);
                        }
                    }
                    else {
                        rv.me.setVisibility(GONE);
                    }
                }
                else {
                    rv.me.setVisibility(GONE);
                }
            }
        }
    }

    public static void filterCheckout(List<RoomView> rvs) {
        cleanupFilter = false;
        allFilter = false;
        laundryFilter = false;
        dndFilter = false;
        checkoutFilter = true;
        for (RoomView rv : rvs) {
            rv.hideDND();
            rv.hideLaundry();
            rv.hideCleanup();
            if (rv.room != null) {
                if (rv.room.getMainServiceSwitch() != null) {
                    if (rv.room.getMainServiceSwitch().checkout != null) {
                        if (rv.room.getMainServiceSwitch().checkout.getCurrent()) {
                            rv.viewCheckout();
                            rv.me.setVisibility(VISIBLE);
                        }
                        else {
                            rv.me.setVisibility(GONE);
                        }
                    }
                    else {
                        rv.me.setVisibility(GONE);
                    }
                }
                else {
                    rv.me.setVisibility(GONE);
                }
            }
            if (rv.suite != null) {
                if (rv.suite.getMainServiceSwitch() != null) {
                    if (rv.suite.getMainServiceSwitch().checkout != null) {
                        if (rv.suite.getMainServiceSwitch().checkout.getCurrent()) {
                            rv.viewCheckout();
                            rv.me.setVisibility(VISIBLE);
                        }
                        else {
                            rv.me.setVisibility(GONE);
                        }
                    }
                    else {
                        rv.me.setVisibility(GONE);
                    }
                }
                else {
                    rv.me.setVisibility(GONE);
                }
            }
        }
    }

    public static void filterAll(List<RoomView> rvs) {
        cleanupFilter = false;
        allFilter = true;
        laundryFilter = false;
        dndFilter = false;
        checkoutFilter = false;
        for (RoomView rv : rvs) {
            rv.me.setVisibility(VISIBLE);
            if (rv.room != null) {
                if (rv.room.getMainServiceSwitch() != null) {
                    if (rv.room.getMainServiceSwitch().dnd != null) {
                        if (rv.room.getMainServiceSwitch().dnd.getCurrent()) {
                            rv.viewDND();
                        }
                    }
                    if (rv.room.getMainServiceSwitch().laundry != null) {
                        if (rv.room.getMainServiceSwitch().laundry.getCurrent()) {
                            rv.viewLaundry();
                        }
                    }
                    if (rv.room.getMainServiceSwitch().cleanup != null) {
                        if (rv.room.getMainServiceSwitch().cleanup.getCurrent()) {
                            rv.viewCleanup();
                        }
                    }
                    if (rv.room.getMainServiceSwitch().checkout != null) {
                        if (rv.room.getMainServiceSwitch().checkout.getCurrent()) {
                            rv.viewCheckout();
                        }
                    }
                }
            }
            if (rv.suite != null) {
                if (rv.suite.getMainServiceSwitch() != null) {
                    if (rv.suite.getMainServiceSwitch().dnd != null) {
                        if (rv.suite.getMainServiceSwitch().dnd.getCurrent()) {
                            rv.viewDND();
                        }
                    }
                    if (rv.suite.getMainServiceSwitch().laundry != null) {
                        if (rv.suite.getMainServiceSwitch().laundry.getCurrent()) {
                            rv.viewLaundry();
                        }
                    }
                    if (rv.suite.getMainServiceSwitch().cleanup != null) {
                        if (rv.suite.getMainServiceSwitch().cleanup.getCurrent()) {
                            rv.viewCleanup();
                        }
                    }
                    if (rv.suite.getMainServiceSwitch().checkout != null) {
                        if (rv.suite.getMainServiceSwitch().checkout.getCurrent()) {
                            rv.viewCheckout();
                        }
                    }
                }
            }
        }
    }

    public void setCounters(List<RoomView> rvs) {
        cleanupCounter = 0;
        laundryCounter = 0;
        dndCounter = 0;
        checkoutCounter = 0;
        for (RoomView rv : rvs) {
            if (rv.room != null) {
                if (rv.room.getMainServiceSwitch() != null) {
                    if (rv.room.getMainServiceSwitch().dnd != null) {
                        if (rv.room.getMainServiceSwitch().dnd.getCurrent()) {
                            dndCounter++;
                        }
                    }
                    if (rv.room.getMainServiceSwitch().laundry != null) {
                        if (rv.room.getMainServiceSwitch().laundry.getCurrent()) {
                            laundryCounter++;
                        }
                    }
                    if (rv.room.getMainServiceSwitch().cleanup != null) {
                        if (rv.room.getMainServiceSwitch().cleanup.getCurrent()) {
                            cleanupCounter++;
                        }
                    }
                    if (rv.room.getMainServiceSwitch().checkout != null) {
                        if (rv.room.getMainServiceSwitch().checkout.getCurrent()) {
                            checkoutCounter++;
                        }
                    }
                }
            }
            if (rv.suite != null) {
                if (rv.suite.getMainServiceSwitch() != null) {
                    if (rv.suite.getMainServiceSwitch().dnd != null) {
                        if (rv.suite.getMainServiceSwitch().dnd.getCurrent()) {
                            dndCounter++;
                        }
                    }
                    if (rv.suite.getMainServiceSwitch().laundry != null) {
                        if (rv.suite.getMainServiceSwitch().laundry.getCurrent()) {
                            laundryCounter++;
                        }
                    }
                    if (rv.suite.getMainServiceSwitch().cleanup != null) {
                        if (rv.suite.getMainServiceSwitch().cleanup.getCurrent()) {
                            cleanupCounter++;
                        }
                    }
                    if (rv.suite.getMainServiceSwitch().checkout != null) {
                        if (rv.suite.getMainServiceSwitch().checkout.getCurrent()) {
                            checkoutCounter++;
                        }
                    }
                }
            }
        }
        ReceptionScreen.setCounters((Activity) context);
    }

    public void setRoomOnline() {
        room_number.setTextColor(Color.GREEN);
    }

    public void setRoomOffline() {
        room_number.setTextColor(Color.RED);
    }

    public static void clearCounters() {
        cleanupCounter = 0;
        laundryCounter = 0;
        dndCounter = 0;
        checkoutCounter = 0;
    }
}
