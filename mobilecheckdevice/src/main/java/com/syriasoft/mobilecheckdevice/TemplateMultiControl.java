package com.syriasoft.mobilecheckdevice;

import com.syriasoft.mobilecheckdevice.Interface.RequestCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class TemplateMultiControl {

    String name;
    List<TemplateButton> multiControlButtons;

    TemplateMultiControl(String name,List<TemplateButton> multiControlButtons) {
        this.name = name ;
        this.multiControlButtons = new ArrayList<>();
        for (TemplateButton tb : multiControlButtons) {
            this.multiControlButtons.add(new TemplateButton(tb.SwitchName,tb.DP));
        }
    }

    public void saveMultiControlToFireBase(DatabaseReference reference) {
        reference.child(this.name).child("Buttons").removeValue();
        for (int i=0;i<multiControlButtons.size();i++) {
            reference.child(this.name).child("Buttons").child(String.valueOf(i+1)).child("SwitchName").setValue(multiControlButtons.get(i).SwitchName);
            reference.child(this.name).child("Buttons").child(String.valueOf(i+1)).child("DP").setValue(multiControlButtons.get(i).DP);
        }
    }

    public static TemplateMultiControl getTemplateMultiControlsFromFireBase(DataSnapshot mu) {
        String name = mu.getKey();
        Iterable<DataSnapshot> buttons = mu.child("Buttons").getChildren();
        List<TemplateButton> bbbb = new ArrayList<>();
        for (DataSnapshot dataSnapshot : buttons) {
            bbbb.add(new TemplateButton(dataSnapshot.child("SwitchName").getValue().toString(),Integer.parseInt(dataSnapshot.child("DP").getValue().toString())));
        }
        return new TemplateMultiControl(name,bbbb);
    }

    public void deleteTemplateMultiControl(DatabaseReference reference, RequestCallback callback) {
        try {
            reference.removeValue();
            callback.onSuccess();
        }
        catch (Exception e) {
            callback.onFail(e.getMessage());
        }
    }
}
