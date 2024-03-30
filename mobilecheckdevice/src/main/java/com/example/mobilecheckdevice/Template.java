package com.example.mobilecheckdevice;

import android.util.Log;

import com.example.mobilecheckdevice.Interface.CreateMoodCallBack;
import com.example.mobilecheckdevice.Interface.CreateMultiControlCallBack;
import com.example.mobilecheckdevice.Interface.CreateMultiControlsCallBack;
import com.example.mobilecheckdevice.Interface.CreteMoodsCallBack;
import com.example.mobilecheckdevice.Interface.HomeBeanCallBack;
import com.example.mobilecheckdevice.Interface.RequestCallback;
import com.google.firebase.database.DatabaseReference;
import com.tuya.smart.android.device.bean.MultiControlBean;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;
import com.tuya.smart.home.sdk.bean.scene.SceneCondition;
import com.tuya.smart.home.sdk.bean.scene.SceneTask;
import com.tuya.smart.home.sdk.bean.scene.condition.rule.BoolRule;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.bean.DeviceBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Template {
    String name;
    List<ROOM> rooms;
    List<TemplateMood> moods;
    List<TemplateMultiControl> multiControls;
    DatabaseReference TemplateReference,MoodsReference,MultiReference,RoomsReference;


    Template(String name,List<TemplateMood> moods,List<TemplateMultiControl> multiControls,DatabaseReference TemplateReference) {
        this.name = name;
        this.moods = moods;
        this.multiControls = multiControls;
        rooms = new ArrayList<>();
        this.TemplateReference = TemplateReference;
        this.MoodsReference = this.TemplateReference.child("Moods");
        this.MultiReference = this.TemplateReference.child("MultiControls");
        this.RoomsReference = this.TemplateReference.child("Rooms");
    }

    public void setTemplateRooms(String rooms) {
        String[] roomsArray = rooms.split("-");
        for (String r : roomsArray) {
            for (ROOM R :MyApp.ROOMS) {
                if (Integer.parseInt(r) == R.RoomNumber) {
                    this.rooms.add(R);
                    break;
                }
            }
        }
        Log.d("gettingTemplateRooms", String.valueOf(this.rooms.size()));
    }

    void createTemplateMoodInRoom(TemplateMood md, ROOM room,CreateMoodCallBack callBack) {
        CheckInMood checkInMood = convertTemplateMoodToCheckInMood(md,room);
        List<SceneCondition> condS = null;
        List<SceneTask> tasks = new ArrayList<>();
        if (checkInMood.conditionDevice != null) {
            condS = new ArrayList<>();
            BoolRule rule = BoolRule.newInstance("dp"+checkInMood.conditionDp, checkInMood.conditionStatus);
            SceneCondition cond = SceneCondition.createDevCondition(checkInMood.conditionDevice, String.valueOf(checkInMood.conditionDp),rule);
            condS.add(cond);
        }
        for (int i=0;i<checkInMood.tasksDevices.size();i++) {
            HashMap<String, Object> taskMap = new HashMap<>();
            taskMap.put(String.valueOf(checkInMood.tasksDevices.get(i).taskDp), checkInMood.tasksDevices.get(i).taskStatus);
            SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(checkInMood.tasksDevices.get(i).taskDevice.devId, taskMap);
            tasks.add(task);
        }
        List<SceneCondition> finalCondS = condS;
        ROOM.getRoomHome(room, MyApp.ProjectHomes, new HomeBeanCallBack() {
            @Override
            public void onSuccess(HomeBean homeBean) {
                TuyaHomeSdk.getSceneManagerInstance().createScene(
                        homeBean.getHomeId(),
                        room.RoomNumber+checkInMood.moodName,
                        false,
                        Rooms.IMAGES.get(0),
                        finalCondS,
                        tasks,
                        null,
                        SceneBean.MATCH_TYPE_AND,
                        new ITuyaResultCallback<SceneBean>() {
                            @Override
                            public void onSuccess(SceneBean sceneBean) {
                                Log.d("MoodCreation", "create Scene Success");
                                callBack.onSuccess(sceneBean);
                            }
                            @Override
                            public void onError(String errorCode, String errorMessage) {
                                Log.d("MoodCreation", errorMessage + " " + errorCode);
                                callBack.onFail(errorMessage);
                            }
                        });
            }

            @Override
            public void onFail(String error) {
                callBack.onFail("getting room home error : "+error);
            }
        });
    }

    void createMoodInRoom(CheckInMood mood,ROOM room,CreateMoodCallBack callBack) {
        List<SceneCondition> condS = null;
        List<SceneTask> tasks = new ArrayList<>();
        if (mood.conditionDevice != null) {
            condS = new ArrayList<>();
            BoolRule rule = BoolRule.newInstance("dp"+mood.conditionDp, mood.conditionStatus);
            SceneCondition cond = SceneCondition.createDevCondition(mood.conditionDevice, String.valueOf(mood.conditionDp),rule);
            condS.add(cond);
        }
        for (int i=0;i<mood.tasksDevices.size();i++) {
            HashMap<String, Object> taskMap = new HashMap<>();
            taskMap.put(String.valueOf(mood.tasksDevices.get(i).taskDp), mood.tasksDevices.get(i).taskStatus);
            SceneTask task = TuyaHomeSdk.getSceneManagerInstance().createDpTask(mood.tasksDevices.get(i).taskDevice.devId, taskMap);
            tasks.add(task);
        }
        List<SceneCondition> finalCondS = condS;
        ROOM.getRoomHome(room, MyApp.ProjectHomes, new HomeBeanCallBack() {
            @Override
            public void onSuccess(HomeBean homeBean) {
                TuyaHomeSdk.getSceneManagerInstance().createScene(
                        homeBean.getHomeId(),
                        room.RoomNumber+mood.moodName,
                        false,
                        Rooms.IMAGES.get(0),
                        finalCondS,
                        tasks,
                        null,
                        SceneBean.MATCH_TYPE_AND,
                        new ITuyaResultCallback<SceneBean>() {
                            @Override
                            public void onSuccess(SceneBean sceneBean) {
                                Log.d("MoodCreation", "create Scene Success");
                                callBack.onSuccess(sceneBean);
                            }
                            @Override
                            public void onError(String errorCode, String errorMessage) {
                                Log.d("MoodCreation", errorMessage + " " + errorCode);
                                callBack.onFail(errorMessage);
                            }
                        });
            }

            @Override
            public void onFail(String error) {
                callBack.onFail("getting room home error : "+error);
            }
        });
    }

    void createMultiControlInRoom(CheckInMultiControl multi,ROOM room,CreateMultiControlCallBack callBack) {
        JSONArray arr = new JSONArray();
        for (CheckInMultiControlDevice mb : multi.multiControl) {
            JSONObject groupDetails = new JSONObject() ;
            try {
                groupDetails.put("devId", mb.device.devId);
                groupDetails.put("dpId", mb.dp);
                groupDetails.put("id", multi.name);
                groupDetails.put("enable", true);
            } catch (JSONException e) {
                callBack.onFail(e.getMessage());
            }
            arr.put(groupDetails);
        }

        JSONObject multiControlBean = new JSONObject();
        try {
            multiControlBean.put("groupName", room.RoomNumber+"MC"+multi.name);
            multiControlBean.put("groupType", 1);
            multiControlBean.put("groupDetail", arr);
            multiControlBean.put("id", multi.name);
        } catch (JSONException e) {
            callBack.onFail(e.getMessage());
        }

        ROOM.getRoomHome(room, MyApp.ProjectHomes, new HomeBeanCallBack() {
            @Override
            public void onSuccess(HomeBean homeBean) {
                Log.d("applyTemplateXX","template multi: home is " +homeBean.getName());
                TuyaHomeSdk.getDeviceMultiControlInstance().saveDeviceMultiControl(homeBean.getHomeId(), multiControlBean.toString(), new ITuyaResultCallback<MultiControlBean>() {
                    @Override
                    public void onSuccess(MultiControlBean result) {
                        callBack.onSuccess(result);
                    }

                    @Override
                    public void onError(String errorCode, String errorMessage) {
                        callBack.onFail(errorMessage);
                    }
                });
            }

            @Override
            public void onFail(String error) {
                callBack.onFail(error);
            }
        });
    }

    void createTemplateMultiInRoom(TemplateMultiControl mc, ROOM room, CreateMultiControlCallBack callBack) {
        CheckInMultiControl checkInMultiControl = Template.convertTemplateMultiToCheckInMulti(mc,room);
        JSONArray arr = new JSONArray();
        int x = new Random().nextInt(1000);
        for (CheckInMultiControlDevice mb : checkInMultiControl.multiControl) {
            JSONObject groupDetails = new JSONObject() ;
            try {
                groupDetails.put("devId", mb.device.devId);
                groupDetails.put("dpId", mb.dp);
                groupDetails.put("id", x);
                groupDetails.put("enable", true);
            } catch (JSONException e) {
                callBack.onFail(e.getMessage());
            }
            arr.put(groupDetails);
        }

        JSONObject multiControlBean = new JSONObject();
        try {
            multiControlBean.put("groupName", room.RoomNumber+x);
            multiControlBean.put("groupType", 1);
            multiControlBean.put("groupDetail", arr);
            multiControlBean.put("id", x);
        } catch (JSONException e) {
            callBack.onFail(e.getMessage());
        }

        ROOM.getRoomHome(room, MyApp.ProjectHomes, new HomeBeanCallBack() {
                    @Override
                    public void onSuccess(HomeBean homeBean) {
                        TuyaHomeSdk.getDeviceMultiControlInstance().saveDeviceMultiControl(homeBean.getHomeId(), multiControlBean.toString(), new ITuyaResultCallback<MultiControlBean>() {
                            @Override
                            public void onSuccess(MultiControlBean result) {
                                enableMultiControl(result, new RequestCallback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onFail(String error) {

                                    }
                                });
                                callBack.onSuccess(result);
                            }

                            @Override
                            public void onError(String errorCode, String errorMessage) {
                                callBack.onFail(errorMessage);
                            }
                        });
                    }

                    @Override
                    public void onFail(String error) {
                        callBack.onFail(error);
                    }
                });
    }

    void enableMood(SceneBean sceneBean, RequestCallback callback) {
        TuyaHomeSdk.newSceneInstance(sceneBean.getId()).enableScene(sceneBean.getId(), new IResultCallback() {
            @Override
            public void onSuccess() {
                Log.d("MoodCreation", "enable Scene Success");
                callback.onSuccess();
            }
            @Override
            public void onError(String errorCode, String errorMessage) {
                Log.d("MoodCreation", errorMessage + " " + errorCode);
                callback.onFail(errorMessage);
            }
        });
    }

    void enableMultiControl(MultiControlBean multi,RequestCallback callback) {
        TuyaHomeSdk.getDeviceMultiControlInstance().enableMultiControl(multi.getId(), new ITuyaResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                callback.onSuccess();
            }

            @Override
            public void onError(String errorCode, String errorMessage) {
                callback.onFail(errorMessage);
            }
        });
    }

    void createAllTemplateMoodsInRoom(ROOM room, CreteMoodsCallBack callBack) {
        List<SceneBean> MOODS = new ArrayList<>();
        for (int i=0;i<this.moods.size();i++) {
            createTemplateMoodInRoom(this.moods.get(i), room, new CreateMoodCallBack() {
                @Override
                public void onSuccess(SceneBean mood) {
                    enableMood(mood, new RequestCallback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFail(String error) {

                        }
                    });
                    MOODS.add(mood);
                    if (MOODS.size() == moods.size()) {
                        callBack.onSuccess(MOODS);
                    }
                }

                @Override
                public void onFail(String error) {
                    callBack.onFail(error);
                }
            });
        }
    }

    void createAllTemplateMultiControlsInRoom(ROOM room, CreateMultiControlsCallBack callBack) {
        List<MultiControlBean> multiS = new ArrayList<>();
        for (int i=0;i<multiControls.size();i++) {
            createTemplateMultiInRoom(multiControls.get(i), room, new CreateMultiControlCallBack() {
                @Override
                public void onSuccess(MultiControlBean multi) {
                    multiS.add(multi);
                    if (multiS.size() == multiControls.size()) {
                        callBack.onSuccess(multiS);
                    }
                }

                @Override
                public void onFail(String error) {
                    callBack.onFail(error);
                }
            });
        }
    }

    void applyTemplateMoodsToRoom (ROOM room,RequestCallback callback) {
        List<CheckInMood> Moods = Template.convertTemplateMoodsToCheckInMoods(moods,room);
        List<SceneBean> scenes = new ArrayList<>();
        Log.d("applyTemplateXX","template moods: "+moods.size()+" checkin moods"+Moods.size());
        for (CheckInMood chM : Moods) {
            createMoodInRoom(chM, room, new CreateMoodCallBack() {
                @Override
                public void onSuccess(SceneBean mood) {
                    enableMood(mood, new RequestCallback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFail(String error) {

                        }
                    });
                    scenes.add(mood);
                    if (scenes.size() == Moods.size()) {
                        Log.d("applyTemplateXX","total moods: "+scenes.size());
                        callback.onSuccess();
                    }
                }

                @Override
                public void onFail(String error) {
                    callback.onFail("creating mood "+chM.moodName+" failed \n"+error);
                }
            });
        }
    }

    void applyTemplateMultiControlsToRoom(ROOM room,RequestCallback callback) {
        List<CheckInMultiControl> MultiControls = Template.convertTemplateMultiSToCheckInMultiS(multiControls,room);
        List<MultiControlBean> mmm = new ArrayList<>();
        Log.d("applyTemplateXX","template multi: "+multiControls.size()+" checkin multi "+MultiControls.size());
        int i=0;
        for (CheckInMultiControl chM : MultiControls) {
            Log.d("applyTemplateXX",i+"");
            i++;
            createMultiControlInRoom(chM, room, new CreateMultiControlCallBack() {
                @Override
                public void onSuccess(MultiControlBean multi) {
                    mmm.add(multi);
                    if (mmm.size() == MultiControls.size()) {
                        Log.d("applyTemplateXX","total multi: "+mmm.size());
                        callback.onSuccess();
                    }
                }

                @Override
                public void onFail(String error) {
                    callback.onFail("creating multi control "+chM.name+" failed \n"+error);
                }
            });
        }
    }

    void applyTemplateToRoom(ROOM room, RequestCallback callback) {
        if (moods.size() > 0 && multiControls.size() > 0) {
            TemplateError moodRes = new TemplateError(false,"");
            TemplateError multiRes = new TemplateError(false,"");
            applyTemplateMoodsToRoom(room, new RequestCallback() {
                @Override
                public void onSuccess() {
                    Log.d("applyTemplateXX","moods result: done");
                    moodRes.correspond = true;
                    if (multiRes.correspond) {
                        if (multiRes.error.isEmpty()) {
                            callback.onSuccess();
                        }
                    }
                }

                @Override
                public void onFail(String error) {
                    Log.d("applyTemplateXX","moods result: error "+error);
                    moodRes.correspond = true;
                    if (multiRes.correspond) {
                        if (multiRes.error.isEmpty()) {
                            callback.onFail("Multi Control Created \nMood create failed "+error);
                        }
                        else {
                            callback.onFail("Multi Control Create Failed \n"+multiRes.error+"\n Mood create failed \n"+error);
                        }
                    }
                }
            });
            applyTemplateMultiControlsToRoom(room, new RequestCallback() {
                @Override
                public void onSuccess() {
                    Log.d("applyTemplateXX","multi result: done");
                    multiRes.correspond = true;
                    if (moodRes.correspond) {
                        if (moodRes.error.isEmpty()) {
                            callback.onSuccess();
                        }
                    }
                }

                @Override
                public void onFail(String error) {
                    Log.d("applyTemplateXX","multi result: error "+error);
                    multiRes.correspond = true;
                    if (moodRes.correspond) {
                        if (moodRes.error.isEmpty()) {
                            callback.onFail("Mood created \nMulti Create Failed "+error);
                        }
                        else {
                            callback.onFail("Mood create Failed \n"+moodRes.error+"\n Multi Create Failed \n"+error);
                        }
                    }
                }
            });
        }
        else if (moods.size() == 0 && multiControls.size() > 0) {
            applyTemplateMultiControlsToRoom(room, new RequestCallback() {
                @Override
                public void onSuccess() {
                    Log.d("applyTemplateXX","multi result: done");
                    callback.onSuccess();
                }

                @Override
                public void onFail(String error) {
                    Log.d("applyTemplateXX","multi result: error "+error);
                    callback.onFail(error);
                }
            });
        }
        else if (moods.size() > 0 ) {
            applyTemplateMoodsToRoom(room, new RequestCallback() {
                @Override
                public void onSuccess() {
                    Log.d("applyTemplateXX","moods result: done");
                    callback.onSuccess();
                }

                @Override
                public void onFail(String error) {
                    Log.d("applyTemplateXX","moods result: error "+error);
                    callback.onFail(error);
                }
            });
        }
        else {
            callback.onFail("template empty");
        }
    }

    boolean searchMoodByName(String templateName) {
        for (TemplateMood tm :moods) {
            if (tm.name.equals(templateName)) {
                return true;
            }
        }
        return false;
    }

    // Check Room Corresponding with template _________________________________________________________________________

    TemplateError checkRoomMoodCorrespond(ROOM room, TemplateMood mood) {
        DeviceBean d = room.searchDeviceNameInRoomDevices(mood.conditionButton.SwitchName);
        if (d == null) {
            return new TemplateError(false,mood.conditionButton.SwitchName+" condition device unavailable in room number "+room.RoomNumber+" to make mood "+mood.name);
        }
        else {
            if (!Template.isDPInDevice(d,mood.conditionButton.DP)) {
                return new TemplateError(false,"button number "+mood.conditionButton.DP+" is unavailable in "+mood.conditionButton.SwitchName+" in room number "+room.RoomNumber+" to make mood "+mood.name);
            }
        }
        for (TemplateButton chT: mood.tasks) {
            DeviceBean dd = room.searchDeviceNameInRoomDevices(chT.SwitchName);
            if (dd == null) {
                return new TemplateError(false,mood.name+" task device "+chT.SwitchName+" is unavailable in room number "+room.RoomNumber+" to make mood "+mood.name);
            }
            else {
                if (!Template.isDPInDevice(dd, chT.DP)) {
                    return new TemplateError(false,"button number "+chT.DP+" is unavailable in "+chT.SwitchName+" in room number "+room.RoomNumber+" to make mood "+mood.name);
                }
            }
        }
        return new TemplateError(true,"");
    }

    TemplateError checkRoomMultiControlCorrespond(ROOM room, TemplateMultiControl multi) {
        for (TemplateButton chT: multi.multiControlButtons) {
            if (!room.searchDeviceNameInRoom(chT.SwitchName)) {
                return new TemplateError(false,multi.name+" multi device "+chT.SwitchName+" is unavailable in room number "+room.RoomNumber+" to make multi control "+multi.name);
            }
        }
        return new TemplateError(true,"");
    }

    TemplateError checkRoomCorrespond(ROOM room) {
        for (TemplateMood tm : moods) {
            TemplateError x = checkRoomMoodCorrespond(room,tm);
            if (!x.correspond) {
                return x;
            }
        }
        for (TemplateMultiControl tmc:multiControls) {
            TemplateError y = checkRoomMultiControlCorrespond(room,tmc);
            if (!y.correspond) {
                return y;
            }
        }
        return new TemplateError(true,"");
    }


    // static functions ______________________________________________________________________________________________

    public static DeviceBean getMoodConditionDevice(TemplateMood mood,ROOM room) {
        List<DeviceBean> devices = ROOM.getRoomDevices(room);
        DeviceBean D = null;
        if (mood.conditionButton.SwitchName.equals("") && mood.conditionButton.DP == 0) {
            return D ;
        }
        for (DeviceBean d : devices) {
            if (d.getName().contains(mood.conditionButton.SwitchName)) {
                return d ;
            }
        }
        return null;
    }

    public static boolean isDPInDevice(DeviceBean deviceBean, int DP) {
        return deviceBean.getDps().get(String.valueOf(DP)) != null;
    }

    public static List<CheckInTask> getMoodTasksDevices(TemplateMood mood,ROOM room) {
        List<DeviceBean> roomDevices = ROOM.getRoomDevices(room);
        List<CheckInTask> checkInTasks = new ArrayList<>();
        for (TemplateButton tb :mood.tasks) {
            for (DeviceBean d :roomDevices) {
                if (d.getName().contains(tb.SwitchName)) {
                    checkInTasks.add(new CheckInTask(d,tb.DP,tb.status));
                    break;
                }
            }
        }
        return checkInTasks;
    }

    public static List<CheckInMultiControlDevice> getMultiControlDevices(TemplateMultiControl multi,ROOM room) {
        List<DeviceBean> roomDevices = ROOM.getRoomDevices(room);
        List<CheckInMultiControlDevice> multiDevices = new ArrayList<>();
        for (TemplateButton tb :multi.multiControlButtons) {
            for (DeviceBean d :roomDevices) {
                if (d.getName().contains(tb.SwitchName)) {
                    multiDevices.add(new CheckInMultiControlDevice(d,tb.DP));
                    break;
                }
            }
        }
        return multiDevices;
    }

    public static List<CheckInMood> convertTemplateMoodsToCheckInMoods(List<TemplateMood> moods,ROOM room) {
        List<CheckInMood> checkInMoods = new ArrayList<>();
        if (moods.size() != 0) {
            for (TemplateMood md : moods) {
                checkInMoods.add(new CheckInMood(md.name,Template.getMoodConditionDevice(md,room),md.conditionButton.DP,md.conditionButton.status,Template.getMoodTasksDevices(md,room)));
            }
        }
        return checkInMoods;
    }

    public static CheckInMood convertTemplateMoodToCheckInMood(TemplateMood md,ROOM room) {
        return new CheckInMood(md.name,Template.getMoodConditionDevice(md,room),md.conditionButton.DP,md.conditionButton.status,Template.getMoodTasksDevices(md,room));
    }

    public static List<CheckInMultiControl> convertTemplateMultiSToCheckInMultiS(List<TemplateMultiControl> multiS,ROOM room) {
        List<CheckInMultiControl> checkInMultiS = new ArrayList<>();
        if (multiS.size() != 0) {
            for (TemplateMultiControl mc : multiS) {
                checkInMultiS.add(new CheckInMultiControl(mc.name,Template.getMultiControlDevices(mc,room)));
            }
        }
        return checkInMultiS;
    }

    public static CheckInMultiControl convertTemplateMultiToCheckInMulti(TemplateMultiControl multi,ROOM room) {
        return new CheckInMultiControl(multi.name,Template.getMultiControlDevices(multi,room));
    }
}

class TemplateError {
    boolean correspond;
    String error;

    public TemplateError(boolean correspond, String error) {
        this.correspond = correspond;
        this.error = error;
    }
}