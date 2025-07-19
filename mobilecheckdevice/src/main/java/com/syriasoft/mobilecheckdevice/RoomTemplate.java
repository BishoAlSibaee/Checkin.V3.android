package com.syriasoft.mobilecheckdevice;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.tuya.smart.android.device.bean.MultiControlBean;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;

import java.util.List;

public class RoomTemplate {
    private ROOM room ;
    private List<MultiControlBean> multiControls ;
    private List<SceneBean> moods ;
    private List<SwitchButton> switchButtons;

    public RoomTemplate(@NonNull ROOM room) {
        this.room = room;
    }

    public void setMultiControls(@NonNull List<MultiControlBean> multiControls) {
        this.multiControls = multiControls;
    }

    public void setMoods(@NonNull List<SceneBean> moods) {
        this.moods = moods;
    }

    public void setScreenButtons(@NonNull List<SwitchButton> switchButtons) {
        this.switchButtons = switchButtons;
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

    public List<SwitchButton> getScreenButtons() {
        return switchButtons;
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
