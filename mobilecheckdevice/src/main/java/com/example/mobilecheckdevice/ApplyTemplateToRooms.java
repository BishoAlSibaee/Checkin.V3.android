package com.example.mobilecheckdevice;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecheckdevice.Interface.RequestCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class ApplyTemplateToRooms extends AppCompatActivity {

    Activity act ;
    Template template;
    RecyclerView RoomsRecycler,OldRoomsRecycler;
    List<ROOM> SelectedRooms;
    View.OnClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_template_to_rooms);
        setActivity();
    }

    void setActivity() {
        act = this;
        template = ViewTemplate.template;
        SelectedRooms = new ArrayList<>();
        TextView caption = findViewById(R.id.textView46);
        caption.setText(MessageFormat.format("Apply {0} Template to Rooms", template.name));
        RoomsRecycler = findViewById(R.id.roomsRecycler);
        OldRoomsRecycler = findViewById(R.id.oldRoomsRecycler);
        GridLayoutManager manager = new GridLayoutManager(act,8);
        GridLayoutManager managerOld = new GridLayoutManager(act,8);
        RoomsRecycler.setLayoutManager(manager);
        OldRoomsRecycler.setLayoutManager(managerOld);
        listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout l = (LinearLayout) view;
                LinearLayout l1 = (LinearLayout) l.getChildAt(0);
                TextView t = (TextView) l1.getChildAt(0);
                for (int i=0;i<SelectedRooms.size();i++) {
                    if (SelectedRooms.get(i).RoomNumber == Integer.parseInt(t.getText().toString())) {
                        SelectedRooms.remove(i);
                        TemplateRoom_adapter adapter = new TemplateRoom_adapter(SelectedRooms,this);
                        RoomsRecycler.setAdapter(adapter);
                        break;
                    }
                }
            }
        };
        TemplateRoom_adapter adapter = new TemplateRoom_adapter(SelectedRooms,listener);
        RoomsRecycler.setAdapter(adapter);
        TemplateRoom_adapter adapterOld = new TemplateRoom_adapter(template.rooms);
        OldRoomsRecycler.setAdapter(adapterOld);
    }

    public void addRoom(View view) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout l = (LinearLayout) view;
                LinearLayout l1 = (LinearLayout) l.getChildAt(0);
                TextView t = (TextView) l1.getChildAt(0);
                if (ROOM.searchRoomInList(template.rooms,Integer.parseInt(t.getText().toString())) != null) {
                    Toast.makeText(act,"room already in template rooms",Toast.LENGTH_SHORT).show();
                    return;
                }
                ROOM r = ROOM.searchRoomInList(SelectedRooms,Integer.parseInt(t.getText().toString()));
                if (r == null) {
                    ROOM rrr = ROOM.searchRoomInList(MyApp.ROOMS,Integer.parseInt(t.getText().toString()));
                    if (template.checkRoomCorrespond(rrr).correspond) {
                        SelectedRooms.add(rrr);
                        refreshRooms();
                    }
                    else {
                        new MessageDialog(template.checkRoomCorrespond(rrr).error,"Room Number "+rrr.RoomNumber+" in UnCorrespond with "+template.name,act);
                    }
                }
                else {
                    SelectedRooms.remove(r);
                    refreshRooms();
                }
            }
        };
        SelectRoomDialog d = new SelectRoomDialog(act,MyApp.ROOMS,listener);
        d.show();
    }

    public void refreshRooms() {
        TemplateRoom_adapter adapter = new TemplateRoom_adapter(SelectedRooms,listener);
        RoomsRecycler.setAdapter(adapter);
    }

    public void saveRooms(View view) {
        if (SelectedRooms.size() == 0) {
            new MessageDialog("please add new rooms ","select rooms",act);
            return;
        }
        Log.d("templateRooms" , "rooms: "+SelectedRooms.size()+" moods: "+template.moods.size()+" multi: "+template.multiControls.size());
        ProgressDialog p = new ProgressDialog(act,"Apply "+template.name+" On Rooms",SelectedRooms.size());
        p.show();
        final int[] ind = {0};
        final int[] pr = {0};
        while (ind[0] < SelectedRooms.size()) {
            Log.d("templateRooms" , "ind: "+ind[0]+" pr: "+pr[0]);
            template.applyTemplateToRoom(SelectedRooms.get(ind[0]), new RequestCallback() {
                @Override
                public void onSuccess() {
                    Log.d("templateRooms" , "result "+pr[0]+": done");
                    pr[0]++;
                    p.setProgress(pr[0]);
                    if (pr[0] == SelectedRooms.size()) {
                        new MessageDialog("Done","Done",act);
                        saveRoomsToTemplate();
                    }
                }

                @Override
                public void onFail(String error) {
                    Log.d("templateRooms" , "result "+pr[0]+": error "+error);
                    p.close();
                    new MessageDialog(error,"error",act);
                }
            });
            ind[0]++;
        }
    }

    void saveRoomsToTemplate() {
        String rooms = "";
        for (int i=0 ; i<SelectedRooms.size();i++) {
            if (i == 0) {
                if (template.rooms.size() == 0) {
                    rooms = String.valueOf(SelectedRooms.get(i).RoomNumber);
                }
                else {
                    rooms = "-"+SelectedRooms.get(i).RoomNumber;
                }
            }
            else {
                rooms = rooms+"-"+SelectedRooms.get(i).RoomNumber;
            }

        }
        Log.d("selectedRooms",rooms);
        String finalRooms = rooms;
        template.RoomsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String old = snapshot.getValue().toString();
                    String newRooms = old + finalRooms;
                    template.RoomsReference.setValue(newRooms);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        template.rooms.addAll(SelectedRooms);
        SelectedRooms.clear();
        TemplateRoom_adapter adapter = new TemplateRoom_adapter(SelectedRooms,listener);
        RoomsRecycler.setAdapter(adapter);
        TemplateRoom_adapter adapterOld = new TemplateRoom_adapter(template.rooms);
        OldRoomsRecycler.setAdapter(adapterOld);
    }

}