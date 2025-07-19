package com.syriasoft.mobilecheckdevice;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecheckdevice.R;

import java.util.ArrayList;
import java.util.List;

public class SelectRoomDialog {

    Dialog D ;
    List<ROOM> selectedRooms;


    SelectRoomDialog(Activity act, List<ROOM> rooms,View.OnClickListener listener) {
        D = new Dialog(act);
        D.setContentView(R.layout.select_room_dialog);
        Window w = D.getWindow();
        w.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        w.setBackgroundDrawableResource(R.color.transparent);
        selectedRooms = new ArrayList<>();
        RecyclerView roomsR = D.findViewById(R.id.theRoomsRecycler);
        ImageButton close = D.findViewById(R.id.imageView7);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });
        GridLayoutManager manager = new GridLayoutManager(act,8);
        roomsR.setLayoutManager(manager);
        TemplateRoom_adapter adapter = new TemplateRoom_adapter(rooms,listener);
        roomsR.setAdapter(adapter);
    }

    void show() {
        D.show();
    }

    void close() {
        D.dismiss();
    }
}
