package com.example.mobilecheckdevice;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecheckdevice.Interface.CreteMoodsCallBack;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.bean.scene.SceneCondition;
import com.tuya.smart.home.sdk.bean.scene.SceneTask;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.sdk.api.IResultCallback;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Moods extends AppCompatActivity {

    Activity act ;
    public static List<SceneBean> MoodsScenes ;
    static List<SceneBean> livingMood,sleepMood,workMood,romanceMood,readMood,masterOffMood,lightsOnMood,oppositeMood1,oppositeMood2,oppositeMood3,oppositeMood4,oppositeMood5,oppositeMood6,oppositeMood7,oppositeMood8,otherMood1,otherMood2,otherMood3,otherMood4,otherMood5,otherMood6,otherMood7,otherMood8;
    static RecyclerView MoodsRecycler;


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
        setScenes(act);
    }

    void setActivity() {
        act = this ;
        MoodsRecycler = findViewById(R.id.moodsRecycler);
        MoodsScenes = new ArrayList<>() ;
        livingMood = new ArrayList<>() ;
        sleepMood = new ArrayList<>() ;
        workMood = new ArrayList<>() ;
        romanceMood = new ArrayList<>() ;
        readMood = new ArrayList<>() ;
        masterOffMood = new ArrayList<>() ;
        lightsOnMood = new ArrayList<>() ;
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
    }

    void setActivityActions() {
        Button living,sleep,work,romance,read,masterOff,lightsOn,opposite1,opposite2,opposite3,opposite4,opposite5,opposite6,opposite7,opposite8,other1,other2,other3,other4,other5,other6,other7,other8;
        living = findViewById(R.id.livingMood);
        sleep = findViewById(R.id.sleepingMood);
        work = findViewById(R.id.workMood);
        romance = findViewById(R.id.romanceMood);
        read = findViewById(R.id.readingMood);
        masterOff = findViewById(R.id.masterOffMood);
        lightsOn = findViewById(R.id.lightsOnMood);
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
        living.setOnClickListener(v -> {
            if (livingMood.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","Living Mood");
                startActivity(i);
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(act);
//                builder.setTitle("Living Mood ")
//                        .setMessage("what you want to do with living mood")
//                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton("delete", (dialog, which) -> {
//                            for (int i=0;i<livingMood.size();i++) {
//                                SceneBean sb = livingMood.get(i) ;
//                                TuyaHomeSdk.newSceneInstance(livingMood.get(i).getId()).deleteScene(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new MessageDialog(error,"Failed",act);
//                                    }
//                                    @Override
//                                    public void onSuccess() {
//                                        RoomManager.MY_SCENES.remove(sb);
//                                        livingMood.remove(sb);
//                                        if (livingMood.size() == 0) {
//                                            dialog.dismiss();
//                                            new MessageDialog("Mood deleted","Done",act);
//                                            setActivity();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .create().show();
//            }
        });
        sleep.setOnClickListener(v -> {
            if (sleepMood.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","Sleep Mood");
                startActivity(i);
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(act);
//                builder.setTitle("Sleep Mood ")
//                        .setMessage("what you want to do with living mood")
//                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton("delete", (dialog, which) -> {
//                            for (int i=0;i<sleepMood.size();i++) {
//                                SceneBean sb = sleepMood.get(i) ;
//                                TuyaHomeSdk.newSceneInstance(sleepMood.get(i).getId()).deleteScene(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new MessageDialog(error,"Failed",act);
//                                    }
//                                    @Override
//                                    public void onSuccess() {
//                                        RoomManager.MY_SCENES.remove(sb);
//                                        sleepMood.remove(sb);
//                                        if (sleepMood.size() == 0) {
//                                            dialog.dismiss();
//                                            new MessageDialog("Mood deleted","Done",act);
//                                            setActivity();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .create().show();
//            }
        });
        work.setOnClickListener(v -> {
            if (workMood.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","Work Mood");
                startActivity(i);
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(act);
//                builder.setTitle("Work Mood ")
//                        .setMessage("what you want to do with living mood")
//                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton("delete", (dialog, which) -> {
//                            for (int i=0;i<workMood.size();i++) {
//                                SceneBean sb = workMood.get(i);
//                                TuyaHomeSdk.newSceneInstance(workMood.get(i).getId()).deleteScene(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new MessageDialog(error,"Failed",act);
//                                    }
//                                    @Override
//                                    public void onSuccess() {
//                                        RoomManager.MY_SCENES.remove(sb);
//                                        workMood.remove(sb);
//                                        if (workMood.size() == 0) {
//                                            dialog.dismiss();
//                                            new MessageDialog("Mood deleted","Done",act);
//                                            setActivity();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .create().show();
//            }
        });
        romance.setOnClickListener(v -> {
            if (romanceMood.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","Romance Mood");
                startActivity(i);
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(act);
//                builder.setTitle("Romance Mood ")
//                        .setMessage("what you want to do with living mood")
//                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton("delete", (dialog, which) -> {
//                            for (int i=0;i<romanceMood.size();i++) {
//                                SceneBean sb = romanceMood.get(i);
//                                TuyaHomeSdk.newSceneInstance(romanceMood.get(i).getId()).deleteScene(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new MessageDialog(error,"Failed",act);
//                                    }
//                                    @Override
//                                    public void onSuccess() {
//                                        RoomManager.MY_SCENES.remove(sb);
//                                        romanceMood.remove(sb);
//                                        if (romanceMood.size() == 0) {
//                                            dialog.dismiss();
//                                            new MessageDialog("Mood deleted","Done",act);
//                                            setActivity();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .create().show();
//            }
        });
        read.setOnClickListener(v -> {
            if (readMood.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","Read Mood");
                startActivity(i);
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(act);
//                builder.setTitle("Read Mood ")
//                        .setMessage("what you want to do with living mood")
//                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton("delete", (dialog, which) -> {
//                            for (int i=0;i<readMood.size();i++) {
//                                SceneBean sb = readMood.get(i);
//                                TuyaHomeSdk.newSceneInstance(readMood.get(i).getId()).deleteScene(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new MessageDialog(error,"Failed",act);
//                                    }
//                                    @Override
//                                    public void onSuccess() {
//                                        RoomManager.MY_SCENES.remove(sb);
//                                        readMood.remove(sb);
//                                        if (readMood.size() == 0) {
//                                            dialog.dismiss();
//                                            new MessageDialog("Mood deleted","Done",act);
//                                            setActivity();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .create().show();
//            }
        });
        masterOff.setOnClickListener(v -> {
            if (masterOffMood.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","MasterOff Mood");
                startActivity(i);
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(act);
//                builder.setTitle("MasterOff Mood ")
//                        .setMessage("what you want to do with MasterOff mood")
//                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton("delete", (dialog, which) -> {
//                            for (int i=0;i<masterOffMood.size();i++) {
//                                SceneBean sb = masterOffMood.get(i);
//                                TuyaHomeSdk.newSceneInstance(masterOffMood.get(i).getId()).deleteScene(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new MessageDialog(error,"Failed",act);
//                                    }
//                                    @Override
//                                    public void onSuccess() {
//                                        RoomManager.MY_SCENES.remove(sb);
//                                        masterOffMood.remove(sb);
//                                        if (masterOffMood.size() == 0) {
//                                            dialog.dismiss();
//                                            new MessageDialog("Mood deleted","Done",act);
//                                            setActivity();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .create().show();
//            }
        });
        lightsOn.setOnClickListener(view -> {
            if (lightsOnMood.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","LightsOn");
                startActivity(i);
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(act);
//                builder.setTitle("LightsOn Mood ")
//                        .setMessage("what you want to do with LightsOn mood")
//                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton("delete", (dialog, which) -> {
//                            for (int i=0;i<lightsOnMood.size();i++) {
//                                SceneBean sb = lightsOnMood.get(i);
//                                TuyaHomeSdk.newSceneInstance(lightsOnMood.get(i).getId()).deleteScene(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new MessageDialog(error,"Failed",act);
//                                    }
//                                    @Override
//                                    public void onSuccess() {
//                                        RoomManager.MY_SCENES.remove(sb);
//                                        lightsOnMood.remove(sb);
//                                        if (lightsOnMood.size() == 0) {
//                                            dialog.dismiss();
//                                            new MessageDialog("Mood deleted","Done",act);
//                                            setActivity();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .create().show();
//            }
        });
        opposite1.setOnClickListener(view -> {
            if (oppositeMood1.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","Opposite1 Mood");
                startActivity(i);
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(act);
//                builder.setTitle("Opposite1 Mood")
//                        .setMessage("what you want to do with Opposite1 mood")
//                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton("delete", (dialog, which) -> {
//                            for (int i=0;i<oppositeMood1.size();i++) {
//                                SceneBean sb = oppositeMood1.get(i);
//                                TuyaHomeSdk.newSceneInstance(oppositeMood1.get(i).getId()).deleteScene(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new MessageDialog(error,"Failed",act);
//                                    }
//                                    @Override
//                                    public void onSuccess() {
//                                        RoomManager.MY_SCENES.remove(sb);
//                                        oppositeMood1.remove(sb);
//                                        if (oppositeMood1.size() == 0) {
//                                            dialog.dismiss();
//                                            new MessageDialog("Mood deleted","Done",act);
//                                            setActivity();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .create().show();
//            }
        });
        opposite2.setOnClickListener(view -> {
            if (oppositeMood2.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","Opposite2 Mood");
                startActivity(i);
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(act);
//                builder.setTitle("Opposite2 Mood")
//                        .setMessage("what you want to do with Opposite2 mood")
//                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton("delete", (dialog, which) -> {
//                            for (int i=0;i<oppositeMood2.size();i++) {
//                                SceneBean sb = oppositeMood2.get(i);
//                                TuyaHomeSdk.newSceneInstance(oppositeMood2.get(i).getId()).deleteScene(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new MessageDialog(error,"Failed",act);
//                                    }
//                                    @Override
//                                    public void onSuccess() {
//                                        RoomManager.MY_SCENES.remove(sb);
//                                        oppositeMood2.remove(sb);
//                                        if (oppositeMood2.size() == 0) {
//                                            dialog.dismiss();
//                                            new MessageDialog("Mood deleted","Done",act);
//                                            setActivity();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .create().show();
//            }
        });
        opposite3.setOnClickListener(view -> {
            if (oppositeMood3.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","Opposite3 Mood");
                startActivity(i);
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(act);
//                builder.setTitle("Opposite3 Mood")
//                        .setMessage("what you want to do with Opposite3 mood")
//                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton("delete", (dialog, which) -> {
//                            for (int i=0;i<oppositeMood3.size();i++) {
//                                SceneBean sb = oppositeMood3.get(i);
//                                TuyaHomeSdk.newSceneInstance(oppositeMood3.get(i).getId()).deleteScene(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new MessageDialog(error,"Failed",act);
//                                    }
//                                    @Override
//                                    public void onSuccess() {
//                                        RoomManager.MY_SCENES.remove(sb);
//                                        oppositeMood3.remove(sb);
//                                        if (oppositeMood3.size() == 0) {
//                                            dialog.dismiss();
//                                            new MessageDialog("Mood deleted","Done",act);
//                                            setActivity();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .create().show();
//            }
        });
        opposite4.setOnClickListener(view -> {
            if (oppositeMood4.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","Opposite4 Mood");
                startActivity(i);
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(act);
                builder.setTitle("Opposite4 Mood")
                        .setMessage("what you want to do with Opposite4 mood")
                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                        .setPositiveButton("delete", (dialog, which) -> {
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
                        })
                        .create().show();
            }
        });
        opposite5.setOnClickListener(view -> {
            if (oppositeMood5.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","Opposite5 Mood");
                startActivity(i);
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(act);
//                builder.setTitle("Opposite5 Mood")
//                        .setMessage("what you want to do with Opposite5 mood")
//                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton("delete", (dialog, which) -> {
//                            for (int i=0;i<oppositeMood5.size();i++) {
//                                SceneBean sb = oppositeMood5.get(i);
//                                TuyaHomeSdk.newSceneInstance(oppositeMood5.get(i).getId()).deleteScene(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new MessageDialog(error,"Failed",act);
//                                    }
//                                    @Override
//                                    public void onSuccess() {
//                                        RoomManager.MY_SCENES.remove(sb);
//                                        oppositeMood5.remove(sb);
//                                        if (oppositeMood5.size() == 0) {
//                                            dialog.dismiss();
//                                            new MessageDialog("Mood deleted","Done",act);
//                                            setActivity();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .create().show();
//            }
        });
        opposite6.setOnClickListener(view -> {
            if (oppositeMood6.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","Opposite6 Mood");
                startActivity(i);
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(act);
//                builder.setTitle("Opposite6 Mood")
//                        .setMessage("what you want to do with Opposite6 mood")
//                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton("delete", (dialog, which) -> {
//                            for (int i=0;i<oppositeMood6.size();i++) {
//                                SceneBean sb = oppositeMood6.get(i);
//                                TuyaHomeSdk.newSceneInstance(oppositeMood6.get(i).getId()).deleteScene(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new MessageDialog(error,"Failed",act);
//                                    }
//                                    @Override
//                                    public void onSuccess() {
//                                        RoomManager.MY_SCENES.remove(sb);
//                                        oppositeMood6.remove(sb);
//                                        if (oppositeMood6.size() == 0) {
//                                            dialog.dismiss();
//                                            new MessageDialog("Mood deleted","Done",act);
//                                            setActivity();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .create().show();
//            }
        });
        opposite7.setOnClickListener(view -> {
            if (oppositeMood7.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","Opposite7 Mood");
                startActivity(i);
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(act);
//                builder.setTitle("Opposite7 Mood")
//                        .setMessage("what you want to do with Opposite7 mood")
//                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton("delete", (dialog, which) -> {
//                            for (int i=0;i<oppositeMood7.size();i++) {
//                                SceneBean sb = oppositeMood7.get(i);
//                                TuyaHomeSdk.newSceneInstance(oppositeMood7.get(i).getId()).deleteScene(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new MessageDialog(error,"Failed",act);
//                                    }
//                                    @Override
//                                    public void onSuccess() {
//                                        RoomManager.MY_SCENES.remove(sb);
//                                        oppositeMood7.remove(sb);
//                                        if (oppositeMood7.size() == 0) {
//                                            dialog.dismiss();
//                                            new MessageDialog("Mood deleted","Done",act);
//                                            setActivity();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .create().show();
//            }
        });
        opposite8.setOnClickListener(view -> {
            if (oppositeMood8.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","Opposite8 Mood");
                startActivity(i);
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(act);
//                builder.setTitle("Opposite8 Mood")
//                        .setMessage("what you want to do with Opposite8 mood")
//                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton("delete", (dialog, which) -> {
//                            for (int i=0;i<oppositeMood8.size();i++) {
//                                SceneBean sb = oppositeMood8.get(i);
//                                TuyaHomeSdk.newSceneInstance(oppositeMood8.get(i).getId()).deleteScene(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new MessageDialog(error,"Failed",act);
//                                    }
//                                    @Override
//                                    public void onSuccess() {
//                                        RoomManager.MY_SCENES.remove(sb);
//                                        oppositeMood8.remove(sb);
//                                        if (oppositeMood8.size() == 0) {
//                                            dialog.dismiss();
//                                            new MessageDialog("Mood deleted","Done",act);
//                                            setActivity();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .create().show();
//            }
        });
        other1.setOnClickListener(view -> {
            if (otherMood1.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","Other1 Mood");
                startActivity(i);
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(act);
//                builder.setTitle("Other4 Mood")
//                        .setMessage("what you want to do with Other1 mood")
//                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton("delete", (dialog, which) -> {
//                            for (int i=0;i<otherMood1.size();i++) {
//                                SceneBean sb = otherMood1.get(i);
//                                TuyaHomeSdk.newSceneInstance(otherMood1.get(i).getId()).deleteScene(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new MessageDialog(error,"Failed",act);
//                                    }
//                                    @Override
//                                    public void onSuccess() {
//                                        RoomManager.MY_SCENES.remove(sb);
//                                        otherMood1.remove(sb);
//                                        if (otherMood1.size() == 0) {
//                                            dialog.dismiss();
//                                            new MessageDialog("Mood deleted","Done",act);
//                                            setActivity();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .create().show();
//            }
        });
        other2.setOnClickListener(view -> {
            if (otherMood2.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","Other2 Mood");
                startActivity(i);
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(act);
//                builder.setTitle("Other4 Mood")
//                        .setMessage("what you want to do with Other2 mood")
//                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton("delete", (dialog, which) -> {
//                            for (int i=0;i<otherMood2.size();i++) {
//                                SceneBean sb = otherMood2.get(i);
//                                TuyaHomeSdk.newSceneInstance(otherMood2.get(i).getId()).deleteScene(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new MessageDialog(error,"Failed",act);
//                                    }
//                                    @Override
//                                    public void onSuccess() {
//                                        RoomManager.MY_SCENES.remove(sb);
//                                        otherMood2.remove(sb);
//                                        if (otherMood2.size() == 0) {
//                                            dialog.dismiss();
//                                            new MessageDialog("Mood deleted","Done",act);
//                                            setActivity();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .create().show();
//            }
        });
        other3.setOnClickListener(view -> {
            if (otherMood3.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","Other3 Mood");
                startActivity(i);
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(act);
//                builder.setTitle("Other4 Mood")
//                        .setMessage("what you want to do with Other3 mood")
//                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton("delete", (dialog, which) -> {
//                            for (int i=0;i<otherMood3.size();i++) {
//                                SceneBean sb = otherMood3.get(i);
//                                TuyaHomeSdk.newSceneInstance(otherMood3.get(i).getId()).deleteScene(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new MessageDialog(error,"Failed",act);
//                                    }
//                                    @Override
//                                    public void onSuccess() {
//                                        RoomManager.MY_SCENES.remove(sb);
//                                        otherMood3.remove(sb);
//                                        if (otherMood3.size() == 0) {
//                                            dialog.dismiss();
//                                            new MessageDialog("Mood deleted","Done",act);
//                                            setActivity();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .create().show();
//            }
        });
        other4.setOnClickListener(view -> {
            if (otherMood4.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","Other4 Mood");
                startActivity(i);
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(act);
//                builder.setTitle("Other4 Mood")
//                        .setMessage("what you want to do with Other4 mood")
//                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton("delete", (dialog, which) -> {
//                            for (int i=0;i<otherMood4.size();i++) {
//                                SceneBean sb = otherMood4.get(i);
//                                TuyaHomeSdk.newSceneInstance(otherMood4.get(i).getId()).deleteScene(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new MessageDialog(error,"Failed",act);
//                                    }
//                                    @Override
//                                    public void onSuccess() {
//                                        RoomManager.MY_SCENES.remove(sb);
//                                        otherMood4.remove(sb);
//                                        if (otherMood4.size() == 0) {
//                                            dialog.dismiss();
//                                            new MessageDialog("Mood deleted","Done",act);
//                                            setActivity();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .create().show();
//            }
        });
        other5.setOnClickListener(view -> {
            if (otherMood5.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","Other5 Mood");
                startActivity(i);
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(act);
//                builder.setTitle("Other5 Mood")
//                        .setMessage("what you want to do with Other5 mood")
//                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton("delete", (dialog, which) -> {
//                            for (int i=0;i<otherMood5.size();i++) {
//                                SceneBean sb = otherMood5.get(i);
//                                TuyaHomeSdk.newSceneInstance(otherMood5.get(i).getId()).deleteScene(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new MessageDialog(error,"Failed",act);
//                                    }
//                                    @Override
//                                    public void onSuccess() {
//                                        RoomManager.MY_SCENES.remove(sb);
//                                        otherMood5.remove(sb);
//                                        if (otherMood5.size() == 0) {
//                                            dialog.dismiss();
//                                            new MessageDialog("Mood deleted","Done",act);
//                                            setActivity();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .create().show();
//            }
        });
        other6.setOnClickListener(view -> {
            if (otherMood6.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","Other6 Mood");
                startActivity(i);
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(act);
//                builder.setTitle("Other6 Mood")
//                        .setMessage("what you want to do with Other6 mood")
//                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton("delete", (dialog, which) -> {
//                            for (int i=0;i<otherMood6.size();i++) {
//                                SceneBean sb = otherMood6.get(i);
//                                TuyaHomeSdk.newSceneInstance(otherMood6.get(i).getId()).deleteScene(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new MessageDialog(error,"Failed",act);
//                                    }
//                                    @Override
//                                    public void onSuccess() {
//                                        RoomManager.MY_SCENES.remove(sb);
//                                        otherMood6.remove(sb);
//                                        if (otherMood6.size() == 0) {
//                                            dialog.dismiss();
//                                            new MessageDialog("Mood deleted","Done",act);
//                                            setActivity();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .create().show();
//            }
        });
        other7.setOnClickListener(view -> {
            if (otherMood7.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","Other4 Mood");
                startActivity(i);
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(act);
//                builder.setTitle("Other7 Mood")
//                        .setMessage("what you want to do with Other7 mood")
//                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton("delete", (dialog, which) -> {
//                            for (int i=0;i<otherMood7.size();i++) {
//                                SceneBean sb = otherMood7.get(i);
//                                TuyaHomeSdk.newSceneInstance(otherMood7.get(i).getId()).deleteScene(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new MessageDialog(error,"Failed",act);
//                                    }
//                                    @Override
//                                    public void onSuccess() {
//                                        RoomManager.MY_SCENES.remove(sb);
//                                        otherMood7.remove(sb);
//                                        if (otherMood7.size() == 0) {
//                                            dialog.dismiss();
//                                            new MessageDialog("Mood deleted","Done",act);
//                                            setActivity();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .create().show();
//            }
        });
        other8.setOnClickListener(view -> {
            if (otherMood8.size() == 0) {
                Intent i = new Intent(act,MakeMood.class);
                i.putExtra("ModeName","Other8 Mood");
                startActivity(i);
            }
//            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(act);
//                builder.setTitle("Other8 Mood")
//                        .setMessage("what you want to do with Other8 mood")
//                        .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
//                        .setPositiveButton("delete", (dialog, which) -> {
//                            for (int i=0;i<otherMood8.size();i++) {
//                                SceneBean sb = otherMood8.get(i);
//                                TuyaHomeSdk.newSceneInstance(otherMood8.get(i).getId()).deleteScene(new IResultCallback() {
//                                    @Override
//                                    public void onError(String code, String error) {
//                                        new MessageDialog(error,"Failed",act);
//                                    }
//                                    @Override
//                                    public void onSuccess() {
//                                        RoomManager.MY_SCENES.remove(sb);
//                                        otherMood8.remove(sb);
//                                        if (otherMood8.size() == 0) {
//                                            dialog.dismiss();
//                                            new MessageDialog("Mood deleted","Done",act);
//                                            setActivity();
//                                        }
//                                    }
//                                });
//                            }
//                        })
//                        .create().show();
//            }
        });
    }

    static void setMoods(Activity act) {
        GridLayoutManager manager = new GridLayoutManager(act,6);
        MoodsRecycler.setLayoutManager(manager);
        Mood_adapter adapter = new Mood_adapter(MoodsScenes);
        MoodsRecycler.setAdapter(adapter);
    }

    static View.OnClickListener makeAdapterListener(Activity act,SceneBean mood) {
        return view -> {
            Dialog d = new Dialog(act);
            d.setContentView(R.layout.mood_dialog);
            d.setCancelable(false);
            Window w = d.getWindow();
            w.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            w.setBackgroundDrawableResource(R.color.transparent);
            TextView name = d.findViewById(R.id.textView50);
            TextView cond = d.findViewById(R.id.textView58);
            LinearLayout tasks = d.findViewById(R.id.tasksLayout);
            ImageButton cancel = d.findViewById(R.id.button43);
            Button rename = d.findViewById(R.id.button44);
            Button delete = d.findViewById(R.id.button45);
            Button edit = d.findViewById(R.id.button46);
            Button enable = d.findViewById(R.id.button47);
            if (mood.isEnabled()) {
                enable.setText(act.getResources().getString(R.string.disable));
            }
            else {
                enable.setText(act.getResources().getString(R.string.enable));
            }
            name.setText(mood.getName());
            if (mood.getConditions() != null) {
                String conditions = "";
                for (SceneCondition sc: mood.getConditions()) {
                    conditions = conditions + mood.getConditions().get(0).getEntityName()+" "+mood.getConditions().get(0).getEntitySubIds();
                    if (sc != mood.getConditions().get(mood.getConditions().size()-1)) {
                        conditions = conditions + "\n" ;
                    }
                }
                cond.setText(MessageFormat.format("Conditions is: \n {0}", conditions));
                cond.setTextColor(act.getResources().getColor(R.color.teal_700,null));
            }
            else {
                cond.setVisibility(View.GONE);
            }
            if (mood.getActions() != null) {
                for (SceneTask t : mood.getActions()) {
                    TextView tv = new TextView(act);
                    tv.setText(MessageFormat.format("{0} {1}", t.getEntityName(),t.getExecutorProperty().toString()));
                    tv.setTextColor(act.getResources().getColor(R.color.white,null));
                    tv.setTextSize(16);
                    tv.setGravity(Gravity.CENTER);
                    tasks.addView(tv);
                }
            }

            cancel.setOnClickListener(view1 -> d.dismiss());
            rename.setOnClickListener(view12 -> {
                AlertDialog.Builder b = new AlertDialog.Builder(act);
                final EditText input = new EditText(act);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setText(mood.getName());
                b.setView(input);
                b.setTitle("Rename Mood "+mood.getName());
                b.setMessage("rename mood "+mood.getName());
                b.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
                b.setPositiveButton("Yes", (dialogInterface, i) -> {
                    if (input.getText() == null || input.getText().toString().isEmpty()){
                        Toast.makeText(act,"enter new name",Toast.LENGTH_LONG).show();
                        return;
                    }
                    mood.setName(input.getText().toString());
                    TuyaHomeSdk.newSceneInstance(mood.getId()).modifyScene(mood, new ITuyaResultCallback<SceneBean>() {
                        @Override
                        public void onSuccess(SceneBean result) {
                            dialogInterface.dismiss();
                            d.dismiss();
                            Moods.setMoods(act);
                            new MessageDialog("renamed","Done",act);
                        }

                        @Override
                        public void onError(String errorCode, String errorMessage) {
                            new MessageDialog(errorCode+" "+errorMessage,"error",act);
                        }
                    });
                });
                b.create().show();
            });
            delete.setOnClickListener(view13 -> {
                AlertDialog.Builder b = new AlertDialog.Builder(act);
                b.setTitle("Delete "+mood.getName()+" mood ?");
                b.setMessage("are you sure ??");
                b.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
                b.setPositiveButton("Yes", (dialogInterface, i) -> TuyaHomeSdk.newSceneInstance(mood.getId()).deleteScene(new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        new MessageDialog(code+" "+error,"error",act);
                    }

                    @Override
                    public void onSuccess() {
                        dialogInterface.dismiss();
                        d.dismiss();
                        RoomManager.MY_SCENES.remove(mood);
                        MyApp.SCENES.remove(mood);
                        RoomManager.SCENES.remove(mood);
                        setScenes(act);
                        new MessageDialog("deleted","Done",act);
                    }
                }));
                b.create().show();
            });
            edit.setOnClickListener(view14 -> {

            });
            enable.setOnClickListener(view15 -> {
                if (mood.isEnabled()) {
                    TuyaHomeSdk.newSceneInstance(mood.getId()).disableScene(mood.getId(), new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {
                            new MessageDialog(code+" "+error,"error",act);
                        }

                        @Override
                        public void onSuccess() {
                            mood.setEnabled(false);
                            setMoods(act);
                            enable.setText(act.getResources().getString(R.string.enable));
                        }
                    });
                }
                else {
                    TuyaHomeSdk.newSceneInstance(mood.getId()).enableScene(mood.getId(), new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {
                            new MessageDialog(code+" "+error,"error",act);
                        }

                        @Override
                        public void onSuccess() {
                            mood.setEnabled(true);
                            setMoods(act);
                            enable.setText(act.getResources().getString(R.string.disable));
                        }
                    });
                }
            });
            d.show();
        };
    }

    void getScenes() {
        LoadingDialog loading = new LoadingDialog(act);
        MyApp.SCENES.clear();
        final int[] ind = {0};
        for (int i=0; i<MyApp.ProjectHomes.size();i++) {
            Timer t = new Timer();
            int finalI = i;
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    getHomeScenes(MyApp.ProjectHomes.get(finalI).Home, new CreteMoodsCallBack() {
                        @Override
                        public void onSuccess(List<SceneBean> moods) {
                            ind[0]++;
                            MyApp.SCENES.addAll(moods);
                            if (ind[0] == MyApp.ProjectHomes.size()) {
                                loading.stop();
                                setScenes(act);
                            }
                        }

                        @Override
                        public void onFail(String error) {
                            loading.stop();
                        }
                    });
                }
            }, (long) i * 1000);

        }
    }

    void getHomeScenes(HomeBean h, CreteMoodsCallBack callBack) {
        TuyaHomeSdk.getSceneManagerInstance().getSceneList(h.getHomeId(), new ITuyaResultCallback<List<SceneBean>>() {
            @Override
            public void onSuccess(List<SceneBean> result) {
                callBack.onSuccess(result);
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                callBack.onFail(errorCode+" "+errorMessage);
            }
        });
    }

    static void setScenes(Activity act) {
        Button living,sleep,work,romance,read,masterOff,lightsOn,opposite1,opposite2,opposite3,opposite4,opposite5,opposite6,opposite7,opposite8,other1,other2,other3,other4,other5,other6,other7,other8;
        living = act.findViewById(R.id.livingMood);
        sleep = act.findViewById(R.id.sleepingMood);
        work = act.findViewById(R.id.workMood);
        romance = act.findViewById(R.id.romanceMood);
        read = act.findViewById(R.id.readingMood);
        masterOff = act.findViewById(R.id.masterOffMood);
        lightsOn = act.findViewById(R.id.lightsOnMood);
        opposite1 = act.findViewById(R.id.opposite1Mood);
        opposite2 = act.findViewById(R.id.opposite2Mood);
        opposite3 = act.findViewById(R.id.opposite3Mood);
        opposite4 = act.findViewById(R.id.opposite4Mood);
        opposite5 = act.findViewById(R.id.opposite5Mood);
        opposite6 = act.findViewById(R.id.opposite6Mood);
        opposite7 = act.findViewById(R.id.opposite7Mood);
        opposite8 = act.findViewById(R.id.opposite8Mood);
        other1 = act.findViewById(R.id.other1Mood);
        other2 = act.findViewById(R.id.other2Mood);
        other3 = act.findViewById(R.id.other3Mood);
        other4 = act.findViewById(R.id.other4Mood);
        other5 = act.findViewById(R.id.other5Mood);
        other6 = act.findViewById(R.id.other6Mood);
        other7 = act.findViewById(R.id.other7Mood);
        other8 = act.findViewById(R.id.other8Mood);
        MoodsScenes.clear();
        livingMood.clear();
        sleepMood.clear();
        workMood.clear();
        romanceMood.clear();
        readMood.clear();
        masterOffMood.clear();
        lightsOnMood.clear();
        oppositeMood1.clear();
        oppositeMood2.clear();
        oppositeMood3.clear();
        oppositeMood4.clear();
        oppositeMood5.clear();
        oppositeMood6.clear();
        oppositeMood7.clear();
        oppositeMood8.clear();
        otherMood1.clear();
        otherMood2.clear();
        otherMood3.clear();
        otherMood4.clear();
        otherMood5.clear();
        otherMood6.clear();
        otherMood7.clear();
        otherMood8.clear();
        if (MyApp.SCENES != null && MyApp.SCENES.size() != 0) {
            for (int i=0; i < MyApp.SCENES.size();i++) {
                try {
                    String roomNumber = getRoomNumberFromDeviceName(MyApp.SCENES.get(i));
                    int num = Integer.parseInt(roomNumber);
                    if (num == RoomManager.Room.RoomNumber) {
                        MoodsScenes.add(MyApp.SCENES.get(i));
                    }
                }
                catch (Exception e) {
                    Log.d("moodsException"+MyApp.SCENES.get(i).getName() , e.getMessage());
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
                if (MoodsScenes.get(i).getName().contains("LightsOn")) {
                    lightsOnMood.add(MoodsScenes.get(i));
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
            living.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            living.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        if (sleepMood.size() > 0) {
            sleep.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            sleep.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        if (workMood.size() > 0) {
            work.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            work.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        if (romanceMood.size() > 0) {
            romance.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            romance.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        if (readMood.size() > 0) {
            read.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            read.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        if (masterOffMood.size() > 0) {
            masterOff.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            masterOff.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        if (oppositeMood1.size() > 0) {
            opposite1.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            opposite1.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        if (lightsOnMood.size() > 0) {
            lightsOn.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            lightsOn.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        if (oppositeMood2.size() > 0) {
            opposite2.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            opposite2.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        if (oppositeMood3.size() > 0) {
            opposite3.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            opposite3.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        if (oppositeMood4.size() > 0) {
            opposite4.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            opposite4.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        if (oppositeMood5.size() > 0) {
            opposite5.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            opposite5.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        if (oppositeMood6.size() > 0) {
            opposite6.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            opposite6.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        if (oppositeMood7.size() > 0) {
            opposite7.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            opposite7.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        if (oppositeMood8.size() > 0) {
            opposite8.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            opposite8.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        if (otherMood1.size() > 0) {
            other1.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            other1.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        if (otherMood2.size() > 0) {
            other2.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            other2.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        if (otherMood3.size() > 0) {
            other3.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            other3.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        if (otherMood4.size() > 0) {
            other4.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            other4.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        if (otherMood5.size() > 0) {
            other5.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            other5.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        if (otherMood6.size() > 0) {
            other6.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            other6.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        if (otherMood7.size() > 0) {
            other7.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            other7.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        if (otherMood8.size() > 0) {
            other8.setBackgroundResource(R.drawable.button_trans);
        }
        else {
            other8.setBackgroundResource(R.drawable.btn_bg_selector);
        }
        setMoods(act);
    }

    static String getRoomNumberFromDeviceName(SceneBean d) {
        String n = "";
        if (d.getName().contains("Living")) {
            n = d.getName().split("Living")[0];
        }
        else if (d.getName().contains("Sleep")) {
            n = d.getName().split("Sleep")[0];
        }
        else if (d.getName().contains("Read")) {
            n = d.getName().split("Read")[0];
        }
        else if (d.getName().contains("Work")) {
            n = d.getName().split("Work")[0];
        }
        else if (d.getName().contains("Master")) {
            n = d.getName().split("Master")[0];
        }
        else if (d.getName().contains("Opposite")) {
            n = d.getName().split("Opposite")[0];
        }
        else if (d.getName().contains("Roman")) {
            n = d.getName().split("Romance")[0];
        }
        else if (d.getName().contains("Other")) {
            n = d.getName().split("Other")[0];
        }
        else if (d.getName().contains("Service")) {
            n = d.getName().split("Service")[0];
        }
        else if (d.getName().contains("Light")) {
            n = d.getName().split("Light")[0];
        }
        return n;
    }
}