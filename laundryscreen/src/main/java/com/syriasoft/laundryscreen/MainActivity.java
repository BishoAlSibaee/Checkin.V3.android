/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.syriasoft.laundryscreen;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends Activity
{
    static  List<ServiceOrder> list = new ArrayList<>()  ;
    public static String URL = "https://samples.checkin.ratco-solutions.com/api/";
    public static String Project = "apiTest";
    static Activity act  ;
    static  GridView gridView;
    static ServiceOrder_Adapter adapter ;
    private final String getRoomsUrl = URL+"roomsManagement/getRooms" ;
    static List<ROOM> Rooms ;
    static public List<DatabaseReference> FireRooms ;
    private FirebaseDatabase database ;
    private ValueEventListener[] LaundryListener;
    private MediaPlayer mp ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        act = this ;
        gridView = (GridView)findViewById(R.id.gridview);
        adapter = new ServiceOrder_Adapter(list,act);
        gridView.setAdapter(adapter);
        Rooms = new ArrayList<>();
        FireRooms = new ArrayList<>();
        mp = MediaPlayer.create(this, R.raw.my_sound);
        database = FirebaseDatabase.getInstance("https://hotelservices-ebe66.firebaseio.com/");
        hideSystemUI();
        getRooms();
    }

    @Override
    public void onResume() {
        super.onResume();
        //getOrders();
    }

    private void getRooms() {
        StringRequest re = new StringRequest(Request.Method.GET, getRoomsUrl, response -> {
            Log.d("rooms" , response);
            try {
                JSONArray arr = new JSONArray(response);
                list.clear();
                FireRooms.clear();
                for (int i=0;i<arr.length();i++) {
                    JSONObject row = arr.getJSONObject(i);
                    int id = row.getInt("id");
                    int rNum = row.getInt("RoomNumber");
                    int Hotel = row.getInt("hotel");
                    int b = row.getInt("Building");
                    int bId = row.getInt("building_id");
                    int f = row.getInt("Floor");
                    int fId = row.getInt("floor_id");
                    String rType = row.getString("RoomType");
                    int ss = row.getInt("SuiteStatus");
                    int sn = row.getInt("SuiteNumber");
                    int si = row.getInt("SuiteId");
                    int rn = row.getInt("ReservationNumber");
                    int rs = row.getInt("roomStatus");
                    int t = row.getInt("Tablet");
                    String dep = row.getString("dep");
                    int c = row.getInt("Cleanup");
                    int l = row.getInt("Laundry");
                    int roomS = row.getInt("RoomService");
                    int ch = row.getInt("Checkout");
                    int res = row.getInt("Restaurant");
                    int sos = row.getInt("SOS");
                    int dnd = row.getInt("DND");
                    int PowerSwitch = row.getInt("PowerSwitch");
                    int DoorSensor = row.getInt("DoorSensor");
                    int MotionSensor = row.getInt("MotionSensor");
                    int Thermostat = row.getInt("Thermostat");
                    int zbgateway = row.getInt("ZBGateway");
                    int CurtainSwitch = row.getInt("CurtainSwitch");
                    int ServiceSwitch = row.getInt("ServiceSwitch");
                    int lock = row.getInt("lock");
                    int Switch1 = row.getInt("Switch1");
                    int Switch2 = row.getInt("Switch2");
                    int Switch3 = row.getInt("Switch3");
                    int Switch4 = row.getInt("Switch4");
                    String LockGateway = row.getString("LockGateway");
                    String LockName = row.getString("LockName");
                    int po = row.getInt("powerStatus");
                    int cu = row.getInt("curtainStatus");
                    int doo = row.getInt("doorStatus");
                    int temp = row.getInt("temp");
                    String token =row.getString("token");
                    ROOM room = new ROOM(id,rNum,Hotel,b,bId,f,fId,rType,ss,sn,si,rn,rs,t,dep,c,l,roomS,ch,res,sos,dnd,PowerSwitch,DoorSensor,MotionSensor,Thermostat,zbgateway,CurtainSwitch,ServiceSwitch,lock,Switch1,Switch2,Switch3,Switch4,LockGateway,LockName,po,cu,doo,temp,token);
                    room.printRoomOnLog();
                    Rooms.add(room);
                    FireRooms.add(database.getReference(Project+"/B"+room.Building+"/F"+room.Floor+"/R"+room.RoomNumber));
                }
                Log.d("roomCount" , "room "+Rooms.size()+" fires "+FireRooms.size());
            }
            catch (JSONException e) {
                Log.d("rooms" , e.getMessage());
            }
            LaundryListener = new ValueEventListener[Rooms.size()];
            setRoomsListeners();
        }, error -> {
        });
        Volley.newRequestQueue(act).add(re);
    }

    private void setRoomsListeners() {
        for ( int i=0; i < FireRooms.size() ; i++) {
            final int finalI = i;
            LaundryListener[i] = FireRooms.get(i).child("Laundry").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("laundryAction" , Rooms.get(finalI).RoomNumber+" ");
                    if (Long.parseLong(Objects.requireNonNull(dataSnapshot.getValue()).toString()) != 0) {
                            boolean status = false;
                            for (int j = 0; j < list.size(); j++) {
                                if (list.get(j).roomNumber.equals(String.valueOf(Rooms.get(finalI).RoomNumber)) && list.get(j).dep.equals("Laundry")) {
                                    status = true;
                                }
                            }
                            if (!status) {
                                Calendar x = Calendar.getInstance(Locale.getDefault());
                                long timee = x.getTimeInMillis();
                                list.add(new ServiceOrder(String.valueOf(Rooms.get(finalI).RoomNumber), String.valueOf(1), "Laundry", "", timee));
                                adapter.notifyDataSetChanged();
                                mp.start();
                            }

                    } else {
                        for (int x = 0; x < list.size(); x++) {
                            if (Integer.parseInt(list.get(x).roomNumber) == Rooms.get(finalI).RoomNumber && list.get(x).dep.equals("Laundry")) {
                                list.remove(x);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

}

//     static void addItem (ServiceOrder o ) {
//        list.add(o);
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                adapter.notifyDataSetChanged();
//                gridView.invalidate();
//                Toast.makeText(act , "New Order",Toast.LENGTH_LONG).show();
//            }
//        });
//
//    }
//
//    public void sendRegistrationToServer(final String token) {
//        String url = "https://bait-elmoneh.online/hotel-service/registLaundryScreenToken.php";
//        StringRequest r = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                if (response.equals("1")) {
//
//                }
//                else {
//
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error)
//            {
//
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> params = new HashMap<String,String>();
//                params.put("token",token);
//                params.put("userNumber",String.valueOf(1));
//                return params;
//            }
//        };
//
//        Volley.newRequestQueue(this ).add(r);
//
//    }
//
//    public static void deleteOrder(final int room) {
//        for (int i =0; i<list.size();i++)
//        {
//            if (Integer.parseInt(list.get(i).roomNumber) == room )
//            {
//                list.remove(i);
//            }
//        }
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                adapter.notifyDataSetChanged();
//                gridView.invalidate();
//                Toast.makeText(act , "Order Cancelled from Room " + room ,Toast.LENGTH_LONG).show();
//            }
//        });
//
//    }
//
//    static void getOrders() {
//        list.clear();
//        StringRequest req = new StringRequest(Request.Method.POST, "", new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response)
//            {
//
//                if (response.equals("0"))
//                {
//                    Toast.makeText(act,"No Orders" ,Toast.LENGTH_LONG).show();
//                }
//                else
//                {
//                    try {
//                        list.clear();
//                        JSONArray arr = new JSONArray(response);
//                        for ( int i=0 ; i < arr.length() ; i++ )
//                        {
//                            JSONObject order = arr.getJSONObject(i);
//                            String roomNumber = order.getString("roomNumber");
//                            String id = order.getString("id");
//                            String dep = order.getString("dep");
//                            long date = order.getLong("dateTime");
//                            String RoomServiceOrder = order.getString("orderText");
//
//                            ServiceOrder o = new ServiceOrder( roomNumber , id , dep ,RoomServiceOrder, date );
//                            list.add(o);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        //Toast.makeText(act,e.getMessage() ,Toast.LENGTH_LONG).show();
//                    }
//                    adapter = new ServiceOrder_Adapter(list , act);
//                    gridView.setAdapter(adapter);
//                    //alertActivity();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error)
//            {
//                Toast.makeText(act,error.getMessage() ,Toast.LENGTH_LONG).show();
//            }
//        })
//        {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError
//            {
//                Map<String,String> params = new HashMap<String,String>();
//                params.put( "dep" , "Laundry");
//                return params;
//            }
//        };
//        Volley.newRequestQueue(act).add(req);
//    }