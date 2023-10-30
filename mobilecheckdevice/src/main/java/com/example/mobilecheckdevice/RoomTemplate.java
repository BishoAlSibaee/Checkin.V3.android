package com.example.mobilecheckdevice;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.tuya.smart.android.device.bean.MultiControlBean;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;

import java.util.List;

public class RoomTemplate {
    private ROOM room ;
    private List<MultiControlBean> multiControls ;
    private List<SceneBean> moods ;
    private List<ScreenButton> screenButtons ;

    public RoomTemplate(@NonNull ROOM room) {
        this.room = room;
    }

    public void setMultiControls(@NonNull List<MultiControlBean> multiControls) {
        this.multiControls = multiControls;
    }

    public void setMoods(@NonNull List<SceneBean> moods) {
        this.moods = moods;
    }

    public void setScreenButtons(@NonNull List<ScreenButton> screenButtons) {
        this.screenButtons = screenButtons;
    }

    public ROOM getRoom() {
        return room;
    }

    public List<MultiControlBean> getMultiControls() {
        return multiControls;
    }

    public List<SceneBean> getMoods() {
        return moods;
    }

    public List<ScreenButton> getScreenButtons() {
        return screenButtons;
    }

    public boolean saveTemplateToDB(DatabaseReference dbr) {
        try{
            dbr.setValue(this);
            return true;
        }catch(Exception e){

        }
        return false;
    }

}
