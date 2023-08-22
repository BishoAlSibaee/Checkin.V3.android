package com.syriasoft.cleanup;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ttlock.bl.sdk.api.TTLockClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ROOMS extends AppCompatActivity {
    static ROOMS_ADAPTER adapter;
    static  Activity act;
    static LinearLayout FloorsLayout;
    static List<ROOM> list;
    RecyclerView rooms ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_o_o_m_s);
        setActivity();
        setTheRooms();
    }

    void setActivity() {
        act = this;
        FloorsLayout = findViewById(R.id.floorsLayout);
        if (MyApp.Rooms.size() == 0) {
            new messageDialog("no rooms detected","no rooms" ,act);
        }
        list = MyApp.Rooms;
        rooms = findViewById(R.id.rooms_recycler);
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        manager.offsetChildrenHorizontal(2);
        manager.offsetChildrenVertical(2);
        ensureBluetoothIsEnabled();
        adapter = new ROOMS_ADAPTER(list);
        rooms.setLayoutManager(manager);
    }

    static void setTheRooms() {
        FloorsLayout.removeAllViews();
        list = MyApp.Rooms;
        List<List<ROOM>> MainList = new ArrayList<>();
        for (int i=0;i<list.size();i++) {
            if (i == 0) {
                MainList.add(new ArrayList<>());
                MainList.get(i).add(list.get(i));
            }
            else {
                if (list.get(i).FloorId == MainList.get(MainList.size()-1).get(MainList.get(MainList.size()-1).size()-1).FloorId) {
                    MainList.get(MainList.size()-1).add(list.get(i));
                }
                else {
                    MainList.add(new ArrayList<>());
                    MainList.get(MainList.size()-1).add(list.get(i));
                }
            }
        }
        Log.d("MainList",MainList.size()+" ");
        for (int i=0;i<MainList.size();i++) {
            FloorsLayout.addView(setFloor(MainList.get(i)));
        }
    }

    static LinearLayout setFloor(List<ROOM> list) {
        LinearLayout L = new LinearLayout(act);
        L.setOrientation(LinearLayout.VERTICAL);
        Button B = new Button(act);
        B.setText("Building "+list.get(0).Building+" "+" Floor "+list.get(0).Floor);
        B.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        B.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        RecyclerView R = new RecyclerView(act);
        GridLayoutManager manager = new GridLayoutManager(act, 4);
        manager.offsetChildrenHorizontal(2);
        manager.offsetChildrenVertical(2);
        R.setLayoutManager(manager);
        ROOMS_ADAPTER adapter = new ROOMS_ADAPTER(list);
        R.setAdapter(adapter);
        R.setVisibility(View.GONE);
        B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (R.getVisibility() == View.VISIBLE) {
                    R.setVisibility(View.GONE);
                }
                else {
                    R.setVisibility(View.VISIBLE);
                }
            }
        });
        L.addView(B);
        L.addView(R);
        return L ;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainActivity.getRooms();
    }

    public void ensureBluetoothIsEnabled() {
        if (!TTLockClient.getDefault().isBLEEnabled(act)) {
            TTLockClient.getDefault().requestBleEnable(act);
        }
    }
}