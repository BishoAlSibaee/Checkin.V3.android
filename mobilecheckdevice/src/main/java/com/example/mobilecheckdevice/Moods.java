package com.example.mobilecheckdevice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.sdk.api.IResultCallback;

import java.util.ArrayList;
import java.util.List;

public class Moods extends AppCompatActivity {

    Activity act ;
    List<SceneBean> MoodsScenes ;
    List<SceneBean> livingMood,sleepMood,workMood,romanceMood,readMood,masterOffMood,oppositeMood1,oppositeMood2,oppositeMood3,oppositeMood4,oppositeMood5,oppositeMood6,oppositeMood7,oppositeMood8,otherMood1,otherMood2,otherMood3,otherMood4,otherMood5,otherMood6,otherMood7,otherMood8;
    Button living,sleep,work,romance,read,masterOff,opposite1,opposite2,opposite3,opposite4,opposite5,opposite6,opposite7,opposite8,other1,other2,other3,other4,other5,other6,other7,other8;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moods);
        setActivity();
        setActivityActions();
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setActivity();
    }

    void setActivity() {
        act = this ;
        MoodsScenes = new ArrayList<>() ;
        livingMood = new ArrayList<>() ;
        sleepMood = new ArrayList<>() ;
        workMood = new ArrayList<>() ;
        romanceMood = new ArrayList<>() ;
        readMood = new ArrayList<>() ;
        masterOffMood = new ArrayList<>() ;
        oppositeMood1 = new ArrayList<>() ;
        oppositeMood2 = new ArrayList<>() ;
        oppositeMood3 = new ArrayList<>() ;
        oppositeMood4 = new ArrayList<>() ;
        oppositeMood5 = new ArrayList<>() ;
        oppositeMood6 = new ArrayList<>() ;
        oppositeMood7 = new ArrayList<>() ;
        oppositeMood8 = new ArrayList<>() ;
        otherMood1 = new ArrayList<>() ;
        otherMood2 = new ArrayList<>() ;
        otherMood3 = new ArrayList<>() ;
        otherMood4 = new ArrayList<>() ;
        otherMood5 = new ArrayList<>() ;
        otherMood6 = new ArrayList<>() ;
        otherMood7 = new ArrayList<>() ;
        otherMood8 = new ArrayList<>() ;
        living = findViewById(R.id.livingMood);
        sleep = findViewById(R.id.sleepingMood);
        work = findViewById(R.id.workMood);
        romance = findViewById(R.id.romanceMood);
        read = findViewById(R.id.readingMood);
        masterOff = findViewById(R.id.masterOffMood);
        opposite1 = findViewById(R.id.opposite1Mood);
        opposite2 = findViewById(R.id.opposite2Mood);
        opposite3 = findViewById(R.id.opposite3Mood);
        opposite4 = findViewById(R.id.opposite4Mood);
        opposite5 = findViewById(R.id.opposite5Mood);
        opposite6 = findViewById(R.id.opposite6Mood);
        opposite7 = findViewById(R.id.opposite7Mood);
        opposite8 = findViewById(R.id.opposite8Mood);
        other1 = findViewById(R.id.other1Mood);
        other2 = findViewById(R.id.other2Mood);
        other3 = findViewById(R.id.other3Mood);
        other4 = findViewById(R.id.other4Mood);
        other5 = findViewById(R.id.other5Mood);
        other6 = findViewById(R.id.other6Mood);
        other7 = findViewById(R.id.other7Mood);
        other8 = findViewById(R.id.other8Mood);
        if (RoomManager.MY_SCENES != null && RoomManager.MY_SCENES.size() != 0) {
            for (int i=0; i < RoomManager.MY_SCENES.size();i++) {
                if (RoomManager.MY_SCENES.get(i).getName().contains("Mood")) {
                    MoodsScenes.add(RoomManager.MY_SCENES.get(i));
                }
            }
        }
        if (MoodsScenes.size() > 0) {
            for (int i=0;i<MoodsScenes.size();i++) {
                if (MoodsScenes.get(i).getName().contains("Living")) {
                    livingMood.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Sleep")) {
                    sleepMood.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Work")) {
                    workMood.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Romance")) {
                    romanceMood.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Read")) {
                    readMood.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("MasterOff")) {
                    masterOffMood.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Opposite1")) {
                    oppositeMood1.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Opposite2")) {
                    oppositeMood2.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Opposite3")) {
                    oppositeMood3.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Opposite4")) {
                    oppositeMood4.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Opposite5")) {
                    oppositeMood5.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Opposite6")) {
                    oppositeMood6.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Opposite7")) {
                    oppositeMood7.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Opposite8")) {
                    oppositeMood8.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Other1")) {
                    otherMood1.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Other2")) {
                    otherMood2.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Other3")) {
                    otherMood3.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Other4")) {
                    otherMood4.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Other5")) {
                    otherMood5.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Other6")) {
                    otherMood6.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Other7")) {
                    otherMood7.add(MoodsScenes.get(i));
                }
                if (MoodsScenes.get(i).getName().contains("Other8")) {
                    otherMood8.add(MoodsScenes.get(i));
                }
            }
        }
        if (livingMood.size() > 0) {
            living.setBackgroundResource(R.color.wight);
        }
        else {
            living.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (sleepMood.size() > 0) {
            sleep.setBackgroundResource(R.color.wight);
        }
        else {
            sleep.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (workMood.size() > 0) {
            work.setBackgroundResource(R.color.wight);
        }
        else {
            work.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (romanceMood.size() > 0) {
            romance.setBackgroundResource(R.color.wight);
        }
        else {
            romance.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (readMood.size() > 0) {
            read.setBackgroundResource(R.color.wight);
        }
        else {
            read.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (masterOffMood.size() > 0) {
            masterOff.setBackgroundResource(R.color.wight);
        }
        else {
            masterOff.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (oppositeMood1.size() > 0) {
            opposite1.setBackgroundResource(R.color.wight);
        }
        else {
            opposite1.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (oppositeMood2.size() > 0) {
            opposite2.setBackgroundResource(R.color.wight);
        }
        else {
            opposite2.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (oppositeMood3.size() > 0) {
            opposite3.setBackgroundResource(R.color.wight);
        }
        else {
            opposite3.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (oppositeMood4.size() > 0) {
            opposite4.setBackgroundResource(R.color.wight);
        }
        else {
            opposite4.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (oppositeMood5.size() > 0) {
            opposite5.setBackgroundResource(R.color.wight);
        }
        else {
            opposite5.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (oppositeMood6.size() > 0) {
            opposite6.setBackgroundResource(R.color.wight);
        }
        else {
            opposite6.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (oppositeMood7.size() > 0) {
            opposite7.setBackgroundResource(R.color.wight);
        }
        else {
            opposite7.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (oppositeMood8.size() > 0) {
            opposite8.setBackgroundResource(R.color.wight);
        }
        else {
            opposite8.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (otherMood1.size() > 0) {
            other1.setBackgroundResource(R.color.wight);
        }
        else {
            other1.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (otherMood2.size() > 0) {
            other2.setBackgroundResource(R.color.wight);
        }
        else {
            other2.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (otherMood3.size() > 0) {
            other3.setBackgroundResource(R.color.wight);
        }
        else {
            other3.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (otherMood4.size() > 0) {
            other4.setBackgroundResource(R.color.wight);
        }
        else {
            other4.setBackgroundResource(R.drawable.btn_bg_normal);
        }

        if (otherMood5.size() > 0) {
            other5.setBackgroundResource(R.color.wight);
        }
        else {
            other5.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (otherMood6.size() > 0) {
            other6.setBackgroundResource(R.color.wight);
        }
        else {
            other6.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (otherMood7.size() > 0) {
            other7.setBackgroundResource(R.color.wight);
        }
        else {
            other7.setBackgroundResource(R.drawable.btn_bg_normal);
        }
        if (otherMood8.size() > 0) {
            other8.setBackgroundResource(R.color.wight);
        }
        else {
            other8.setBackgroundResource(R.drawable.btn_bg_normal);
        }
    }

    void setActivityActions() {
        living.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (livingMood.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Living Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Living Mood ")
                            .setMessage("what you want to do with living mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<livingMood.size();i++) {
                                        SceneBean sb = livingMood.get(i) ;
                                        TuyaHomeSdk.newSceneInstance(livingMood.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new MessageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                RoomManager.MY_SCENES.remove(sb);
                                                livingMood.remove(sb);
                                                if (livingMood.size() == 0) {
                                                    dialog.dismiss();
                                                    new MessageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        sleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sleepMood.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Sleep Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Sleep Mood ")
                            .setMessage("what you want to do with living mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<sleepMood.size();i++) {
                                        SceneBean sb = sleepMood.get(i) ;
                                        TuyaHomeSdk.newSceneInstance(sleepMood.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new MessageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                RoomManager.MY_SCENES.remove(sb);
                                                sleepMood.remove(sb);
                                                if (sleepMood.size() == 0) {
                                                    dialog.dismiss();
                                                    new MessageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (workMood.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Work Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Work Mood ")
                            .setMessage("what you want to do with living mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<workMood.size();i++) {
                                        SceneBean sb = workMood.get(i);
                                        TuyaHomeSdk.newSceneInstance(workMood.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new MessageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                RoomManager.MY_SCENES.remove(sb);
                                                workMood.remove(sb);
                                                if (workMood.size() == 0) {
                                                    dialog.dismiss();
                                                    new MessageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        romance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (romanceMood.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Romance Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Romance Mood ")
                            .setMessage("what you want to do with living mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<romanceMood.size();i++) {
                                        SceneBean sb = romanceMood.get(i);
                                        TuyaHomeSdk.newSceneInstance(romanceMood.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new MessageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                RoomManager.MY_SCENES.remove(sb);
                                                romanceMood.remove(sb);
                                                if (romanceMood.size() == 0) {
                                                    dialog.dismiss();
                                                    new MessageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readMood.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Read Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Read Mood ")
                            .setMessage("what you want to do with living mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<readMood.size();i++) {
                                        SceneBean sb = readMood.get(i);
                                        TuyaHomeSdk.newSceneInstance(readMood.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new MessageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                RoomManager.MY_SCENES.remove(sb);
                                                readMood.remove(sb);
                                                if (readMood.size() == 0) {
                                                    dialog.dismiss();
                                                    new MessageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        masterOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (masterOffMood.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","MasterOff Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("MasterOff Mood ")
                            .setMessage("what you want to do with MasterOff mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<masterOffMood.size();i++) {
                                        SceneBean sb = masterOffMood.get(i);
                                        TuyaHomeSdk.newSceneInstance(masterOffMood.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new MessageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                RoomManager.MY_SCENES.remove(sb);
                                                masterOffMood.remove(sb);
                                                if (masterOffMood.size() == 0) {
                                                    dialog.dismiss();
                                                    new MessageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        opposite1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (oppositeMood1.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Opposite1 Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Opposite1 Mood")
                            .setMessage("what you want to do with Opposite1 mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<oppositeMood1.size();i++) {
                                        SceneBean sb = oppositeMood1.get(i);
                                        TuyaHomeSdk.newSceneInstance(oppositeMood1.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new MessageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                RoomManager.MY_SCENES.remove(sb);
                                                oppositeMood1.remove(sb);
                                                if (oppositeMood1.size() == 0) {
                                                    dialog.dismiss();
                                                    new MessageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        opposite2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (oppositeMood2.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Opposite2 Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Opposite2 Mood")
                            .setMessage("what you want to do with Opposite2 mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<oppositeMood2.size();i++) {
                                        SceneBean sb = oppositeMood2.get(i);
                                        TuyaHomeSdk.newSceneInstance(oppositeMood2.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new MessageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                RoomManager.MY_SCENES.remove(sb);
                                                oppositeMood2.remove(sb);
                                                if (oppositeMood2.size() == 0) {
                                                    dialog.dismiss();
                                                    new MessageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        opposite3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (oppositeMood3.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Opposite3 Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Opposite3 Mood")
                            .setMessage("what you want to do with Opposite3 mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<oppositeMood3.size();i++) {
                                        SceneBean sb = oppositeMood3.get(i);
                                        TuyaHomeSdk.newSceneInstance(oppositeMood3.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new MessageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                RoomManager.MY_SCENES.remove(sb);
                                                oppositeMood3.remove(sb);
                                                if (oppositeMood3.size() == 0) {
                                                    dialog.dismiss();
                                                    new MessageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        opposite4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (oppositeMood4.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Opposite4 Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Opposite4 Mood")
                            .setMessage("what you want to do with Opposite4 mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<oppositeMood4.size();i++) {
                                        SceneBean sb = oppositeMood4.get(i);
                                        TuyaHomeSdk.newSceneInstance(oppositeMood4.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new MessageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                RoomManager.MY_SCENES.remove(sb);
                                                oppositeMood4.remove(sb);
                                                if (oppositeMood4.size() == 0) {
                                                    dialog.dismiss();
                                                    new MessageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        opposite5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (oppositeMood5.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Opposite5 Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Opposite5 Mood")
                            .setMessage("what you want to do with Opposite5 mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<oppositeMood5.size();i++) {
                                        SceneBean sb = oppositeMood5.get(i);
                                        TuyaHomeSdk.newSceneInstance(oppositeMood5.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new MessageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                RoomManager.MY_SCENES.remove(sb);
                                                oppositeMood5.remove(sb);
                                                if (oppositeMood5.size() == 0) {
                                                    dialog.dismiss();
                                                    new MessageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        opposite6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (oppositeMood6.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Opposite6 Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Opposite6 Mood")
                            .setMessage("what you want to do with Opposite6 mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<oppositeMood6.size();i++) {
                                        SceneBean sb = oppositeMood6.get(i);
                                        TuyaHomeSdk.newSceneInstance(oppositeMood6.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new MessageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                RoomManager.MY_SCENES.remove(sb);
                                                oppositeMood6.remove(sb);
                                                if (oppositeMood6.size() == 0) {
                                                    dialog.dismiss();
                                                    new MessageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        opposite7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (oppositeMood7.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Opposite7 Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Opposite7 Mood")
                            .setMessage("what you want to do with Opposite7 mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<oppositeMood7.size();i++) {
                                        SceneBean sb = oppositeMood7.get(i);
                                        TuyaHomeSdk.newSceneInstance(oppositeMood7.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new MessageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                RoomManager.MY_SCENES.remove(sb);
                                                oppositeMood7.remove(sb);
                                                if (oppositeMood7.size() == 0) {
                                                    dialog.dismiss();
                                                    new MessageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        opposite8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (oppositeMood8.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Opposite8 Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Opposite8 Mood")
                            .setMessage("what you want to do with Opposite8 mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<oppositeMood8.size();i++) {
                                        SceneBean sb = oppositeMood8.get(i);
                                        TuyaHomeSdk.newSceneInstance(oppositeMood8.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new MessageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                RoomManager.MY_SCENES.remove(sb);
                                                oppositeMood8.remove(sb);
                                                if (oppositeMood8.size() == 0) {
                                                    dialog.dismiss();
                                                    new MessageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        other1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otherMood1.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Other1 Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Other4 Mood")
                            .setMessage("what you want to do with Other1 mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<otherMood1.size();i++) {
                                        SceneBean sb = otherMood1.get(i);
                                        TuyaHomeSdk.newSceneInstance(otherMood1.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new MessageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                RoomManager.MY_SCENES.remove(sb);
                                                otherMood1.remove(sb);
                                                if (otherMood1.size() == 0) {
                                                    dialog.dismiss();
                                                    new MessageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        other2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otherMood2.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Other2 Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Other4 Mood")
                            .setMessage("what you want to do with Other2 mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<otherMood2.size();i++) {
                                        SceneBean sb = otherMood2.get(i);
                                        TuyaHomeSdk.newSceneInstance(otherMood2.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new MessageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                RoomManager.MY_SCENES.remove(sb);
                                                otherMood2.remove(sb);
                                                if (otherMood2.size() == 0) {
                                                    dialog.dismiss();
                                                    new MessageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        other3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otherMood3.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Other3 Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Other4 Mood")
                            .setMessage("what you want to do with Other3 mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<otherMood3.size();i++) {
                                        SceneBean sb = otherMood3.get(i);
                                        TuyaHomeSdk.newSceneInstance(otherMood3.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new MessageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                RoomManager.MY_SCENES.remove(sb);
                                                otherMood3.remove(sb);
                                                if (otherMood3.size() == 0) {
                                                    dialog.dismiss();
                                                    new MessageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        other4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otherMood4.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Other4 Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Other4 Mood")
                            .setMessage("what you want to do with Other4 mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<otherMood4.size();i++) {
                                        SceneBean sb = otherMood4.get(i);
                                        TuyaHomeSdk.newSceneInstance(otherMood4.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new MessageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                RoomManager.MY_SCENES.remove(sb);
                                                otherMood4.remove(sb);
                                                if (otherMood4.size() == 0) {
                                                    dialog.dismiss();
                                                    new MessageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });

        other5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otherMood5.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Other5 Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Other5 Mood")
                            .setMessage("what you want to do with Other5 mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<otherMood5.size();i++) {
                                        SceneBean sb = otherMood5.get(i);
                                        TuyaHomeSdk.newSceneInstance(otherMood5.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new MessageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                RoomManager.MY_SCENES.remove(sb);
                                                otherMood5.remove(sb);
                                                if (otherMood5.size() == 0) {
                                                    dialog.dismiss();
                                                    new MessageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        other6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otherMood6.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Other6 Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Other6 Mood")
                            .setMessage("what you want to do with Other6 mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<otherMood6.size();i++) {
                                        SceneBean sb = otherMood6.get(i);
                                        TuyaHomeSdk.newSceneInstance(otherMood6.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new MessageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                RoomManager.MY_SCENES.remove(sb);
                                                otherMood6.remove(sb);
                                                if (otherMood6.size() == 0) {
                                                    dialog.dismiss();
                                                    new MessageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        other7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otherMood7.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Other4 Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Other7 Mood")
                            .setMessage("what you want to do with Other7 mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<otherMood7.size();i++) {
                                        SceneBean sb = otherMood7.get(i);
                                        TuyaHomeSdk.newSceneInstance(otherMood7.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new MessageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                RoomManager.MY_SCENES.remove(sb);
                                                otherMood7.remove(sb);
                                                if (otherMood7.size() == 0) {
                                                    dialog.dismiss();
                                                    new MessageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
        other8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otherMood8.size() == 0) {
                    Intent i = new Intent(act,MakeMood.class);
                    i.putExtra("ModeName","Other8 Mood");
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Other8 Mood")
                            .setMessage("what you want to do with Other8 mood")
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i=0;i<otherMood8.size();i++) {
                                        SceneBean sb = otherMood8.get(i);
                                        TuyaHomeSdk.newSceneInstance(otherMood8.get(i).getId()).deleteScene(new IResultCallback() {
                                            @Override
                                            public void onError(String code, String error) {
                                                new MessageDialog(error,"Failed",act);
                                            }
                                            @Override
                                            public void onSuccess() {
                                                RoomManager.MY_SCENES.remove(sb);
                                                otherMood8.remove(sb);
                                                if (otherMood8.size() == 0) {
                                                    dialog.dismiss();
                                                    new MessageDialog("Mood deleted","Done",act);
                                                    setActivity();
                                                }
                                            }
                                        });
                                    }
                                }
                            })
                            .create().show();
                }
            }
        });
    }
}