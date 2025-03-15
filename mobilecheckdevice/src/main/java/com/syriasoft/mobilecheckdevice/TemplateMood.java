package com.syriasoft.mobilecheckdevice;

import com.syriasoft.mobilecheckdevice.Interface.RequestCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class TemplateMood {

    String name;
    TemplateButton conditionButton;
    List<TemplateButton> tasks;

    public TemplateMood(String name,TemplateButton conditionButton, List<TemplateButton> tasks) {
        this.name = name ;
        this.conditionButton = conditionButton;
        this.tasks = new ArrayList<>();
        for (TemplateButton tb : tasks) {
            this.tasks.add(new TemplateButton(tb.SwitchName,tb.DP,tb.status));
        }
    }

    public void saveTemplateMoodToFireBase(DatabaseReference reference) {
        reference.child(this.name).removeValue();
        reference.child(this.name).child("Condition").child("SwitchName").setValue(conditionButton.SwitchName);
        reference.child(this.name).child("Condition").child("DP").setValue(conditionButton.DP);
        reference.child(this.name).child("Condition").child("Status").setValue(conditionButton.status);
        for (int i=0;i<this.tasks.size();i++) {
            reference.child(this.name).child("Tasks").child(String.valueOf(i+1)).child("SwitchName").setValue(tasks.get(i).SwitchName);
            reference.child(this.name).child("Tasks").child(String.valueOf(i+1)).child("DP").setValue(tasks.get(i).DP);
            reference.child(this.name).child("Tasks").child(String.valueOf(i+1)).child("Status").setValue(tasks.get(i).status);
        }
    }

    public static TemplateMood getTemplateMoodFromFireBase(DataSnapshot reference) {
        String name = reference.getKey();
        String conditionButtonSwitchName = reference.child("Condition").child("SwitchName").getValue().toString();
        int conditionButtonDP = Integer.parseInt(reference.child("Condition").child("DP").getValue().toString());
        boolean condStatus = false;
        if (reference.child("Condition").child("Status").getValue() != null) {
            condStatus = Boolean.parseBoolean(reference.child("Condition").child("Status").getValue().toString());
        }
        Iterable<DataSnapshot> tasks = reference.child("Tasks").getChildren();
        List<TemplateButton> TASKS = new ArrayList<>();
        for (DataSnapshot ts : tasks) {
            TASKS.add(new TemplateButton(ts.child("SwitchName").getValue().toString(),Integer.parseInt(ts.child("DP").getValue().toString()),Boolean.parseBoolean(ts.child("Status").getValue().toString())));
        }
        return new TemplateMood(name,new TemplateButton(conditionButtonSwitchName,conditionButtonDP,condStatus),TASKS);
    }

    public void deleteMood(DatabaseReference reference, RequestCallback callback) {
        try {
            reference.removeValue();
            callback.onSuccess();
        }
        catch (Exception e) {
            callback.onFail(e.getMessage());
        }
    }
}
