package com.syriasoft.server.Classes;

import android.util.Log;

import com.syriasoft.server.Classes.Devices.CheckinSwitch;
import com.syriasoft.server.Classes.Property.Room;
import com.syriasoft.server.Interface.RequestCallback;
import com.syriasoft.server.MyApp;
import com.tuya.smart.sdk.api.IResultCallback;

import java.util.Timer;
import java.util.TimerTask;

public class CloseCurtains {

    public static void start(Room room) {
        Log.d("closeCurtain","close curtain run");
       room.powerOnRoom(new IResultCallback() {
           @Override
           public void onError(String code, String error) {

           }

           @Override
           public void onSuccess() {
               Log.d("closeCurtain","power on success");
               Timer t = new Timer();
               t.schedule(new TimerTask() {
                   @Override
                   public void run() {
                       if (MyApp.My_PROJECT.projectName.equals("P0003")) {
                           if (room.isHasSwitch()) {
                               for (CheckinSwitch cs : room.switches) {
                                   if (cs.device.name.equals(room.RoomNumber+"Switch6")) {
                                       cs.dp2.turnOn(new IResultCallback() {
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
                       else if (MyApp.My_PROJECT.projectName.equals("apiTest")) {
                           if (room.isHasSwitch()) {
                               for (CheckinSwitch cs : room.switches) {
                                   if (cs.device.name.equals(room.RoomNumber+"Switch1")) {
                                       cs.dp2.turnOn(new IResultCallback() {
                                           @Override
                                           public void onError(String code, String error) {

                                           }

                                           @Override
                                           public void onSuccess() {
                                               Log.d("closeCurtain","done");
                                           }
                                       });
                                   }
                               }
                           }
                       }
                   }
               },5000);
                room.powerByCardAfterMinutes(1, new RequestCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFail(String error) {

                    }
                });
           }
       });
    }
}
