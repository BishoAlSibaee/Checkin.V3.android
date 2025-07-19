package com.syriasoft.house_keeping;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.syriasoft.hotelservices.R;
import com.syriasoft.house_keeping.Interface.RestaurantOrderCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RestaurantOrders extends AppCompatActivity {

    static RestaurantOrdersAdapter adapter;
    static List<restaurant_order_unit> list = new ArrayList<>();
    static Activity act;
    static ListView orders;
    static ProgressBar p;
    static List<ROOM> Rooms;
    public final String SHARED_PREF_NAME = "MyPref";
    private FirebaseDatabase database;
    private ValueEventListener[] RESTAURANTListener;
    String TYPE,NAME,PHOTO;
    static FACILITY THE_FACILITY;
    private GridView ORDERS;
    Button logout;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor ;
    public static restaurant_order_unit SELECTED_ORDER ;
    public static ROOM SELECTED_ROOM ;
    RequestQueue Q;
    Gson g;
    List<StringRequest> Requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_orders);
        setActivity();
        setActivityActions();
        getRooms();
    }

    void setActivity() {
        act = this;
        Q = Volley.newRequestQueue(act);
        ORDERS = findViewById(R.id.gridView);
        Rooms = new ArrayList<>();
        Requests = new ArrayList<>();
        p = findViewById(R.id.progressBar3);
        TextView facilityName = findViewById(R.id.facility_Name);
        ImageView facilityImage = findViewById(R.id.facility_image);
        orders = findViewById(R.id.restaurant_orders);
        logout = findViewById(R.id.button5);
        database = FirebaseDatabase.getInstance("https://checkin-62774-default-rtdb.asia-southeast1.firebasedatabase.app/");
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        int FACILITY_ID = Integer.parseInt(sharedPreferences.getString("FacilityId", null));
        int typeId = Integer.parseInt(sharedPreferences.getString("FacilityTypeId", null));
        TYPE = sharedPreferences.getString("FacilityType", null);
        NAME = sharedPreferences.getString("FacilityName", null);
        PHOTO = sharedPreferences.getString("FacilityPhoto", null);
        THE_FACILITY = new FACILITY(FACILITY_ID,1, typeId,TYPE,NAME,0,PHOTO);
        facilityName.setText(THE_FACILITY.Name);
        Picasso.get().load(THE_FACILITY.photo).into(facilityImage);
        g = new Gson();
    }

    void setActivityActions() {
        logout.setOnClickListener(this::sgnOut);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.button2) {
            Button x = findViewById(R.id.button5);
            sgnOut(x);
        }
        return super.onOptionsItemSelected(item);
    }

    public void getRestaurantOrders() {
        p.setVisibility(View.VISIBLE);
        String url = MyApp.MyProject.url + "facilitys/getRestOrders";
        StringRequest re = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("ordersResp" ,response);
            p.setVisibility(View.GONE);
            try {
                JSONObject result = new JSONObject(response);
                if (result.getString("result").equals("success")) {
                    list.clear();
                    JSONArray arr = new JSONArray(result.getString("orders"));
                    if (arr.length() > 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject row = arr.getJSONObject(i);
                            restaurant_order_unit order = new restaurant_order_unit(row.getInt("id"), row.getInt("Hotel"), row.getInt("Facility"), row.getInt("Reservation"), row.getInt("room"), row.getInt("RorS"), row.getInt("roomId"), row.getLong("dateTime"), row.getDouble("total"), row.getInt("status"));
                            list.add(order);
                        }
                    }
                    adapter = new RestaurantOrdersAdapter(list, act);
                    ORDERS.setAdapter(adapter);
                }
                else {
                    new messageDialog(result.getString("error"),"failed",act);
                }
            } catch (JSONException e) {
                Log.d("ordersResp" ,e.getMessage());
                p.setVisibility(View.GONE);
                new messageDialog(e.getMessage(),"failed",act);
            }
        }, error -> {
            Log.d("ordersResp" ,error.toString());
            p.setVisibility(View.GONE);
            new messageDialog(error.toString(),"failed",act);
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> par = new HashMap<>();
                par.put("facility_id", String.valueOf(THE_FACILITY.id));
                return par;
            }
        };
        Q.add(re);
    }

    private void getRooms() {
        String url = MyApp.MyProject.url + "roomsManagement/getRooms" ;
        LoadingDialog loading = new LoadingDialog(act);
        StringRequest re = new StringRequest(Request.Method.GET, url, response -> {
            Log.d("rooms", response);
            loading.close();
            try {
                JSONArray arr = new JSONArray(response);
                Rooms.clear();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject row = arr.getJSONObject(i);
                    ROOM room = new ROOM(row.getInt("id"), row.getInt("RoomNumber"), row.getInt("hotel"), row.getInt("Building"), row.getInt("building_id"), row.getInt("Floor"), row.getInt("floor_id"), row.getString("RoomType"), row.getInt("SuiteStatus"), row.getInt("SuiteNumber"), row.getInt("SuiteId"), row.getInt("ReservationNumber"), row.getInt("roomStatus"), row.getInt("Tablet"), row.getString("dep"), row.getInt("Cleanup"), row.getInt("Laundry"), row.getInt("RoomService"), row.getInt("Checkout"), row.getInt("Restaurant"), row.getInt("SOS"), row.getInt("DND"), row.getInt("PowerSwitch"), row.getInt("DoorSensor"), row.getInt("MotionSensor"), row.getInt("Thermostat"), row.getInt("ZBGateway"), row.getInt("CurtainSwitch"), row.getInt("ServiceSwitch"), row.getInt("lock"), row.getInt("Switch1"), row.getInt("Switch2"), row.getInt("Switch3"), row.getInt("Switch4"), row.getString("LockGateway"), row.getString("LockName"), row.getInt("powerStatus"), row.getInt("curtainStatus"), row.getInt("doorStatus"), row.getInt("temp"), row.getString("token"),row.getInt("guestIs"));
                    room.setFireRoom(database.getReference(MyApp.MyProject.projectName + "/B" + room.Building + "/F" + room.Floor + "/R" + room.RoomNumber));
                    Rooms.add(room);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (Rooms.size() > 0) {
                RESTAURANTListener = new ValueEventListener[Rooms.size()];
                setInitialListener();
            }
        }, error -> loading.close());
        Q.add(re);
    }

    public void sgnOut(View view) {
        for (int i = 0; i < Rooms.size(); i++) {
            Rooms.get(i).getFireRoom().child("Restaurant").removeEventListener(RESTAURANTListener[i]);
        }
        saveUserDataToSharedPreferences(null);
        Intent i = new Intent(act, LogIn.class);
        startActivity(i);
        act.finish();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void setInitialListener() {
        if (MyApp.My_USER.department.equals("Restaurant") || MyApp.My_USER.department.equals("CoffeeShop")) {
            int[] index = {0};
            for (int i = 0; i < Rooms.size(); i++) {
                final int finalI = i;
                Rooms.get(i).getFireRoom().child("Restaurant").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        index[0]++;
                        if (dataSnapshot.getValue() != null) {
                            Log.d("restOrder",dataSnapshot.getValue().toString());
                            long value = Long.parseLong(dataSnapshot.getValue().toString());
                            int searchResult = restaurant_order_unit.searchRestaurantOrder(list,Rooms.get(finalI).RoomNumber,THE_FACILITY.id);
                            if (value > 0) {
                                if (searchResult == -1) {
                                    Requests.add(makeGetRestaurantOrderRequest(Rooms.get(finalI).RoomNumber, THE_FACILITY.id, new RestaurantOrderCallback() {
                                        @Override
                                        public void onSuccess(restaurant_order_unit order) {
                                            int searchResult1 = restaurant_order_unit.searchRestaurantOrder(list,order.room,order.facility);
                                            Log.d("runGets"," "+searchResult1);
                                            if (searchResult1 == -1) {
                                                list.add(order);
                                                adapter = new RestaurantOrdersAdapter(list, act);
                                                ORDERS.setAdapter(adapter);
                                            }
                                        }

                                        @Override
                                        public void onFail(String error) {

                                        }
                                    }));
                                }
                            }
                            else {
                                if (searchResult != -1) {
                                    list.remove(searchResult);
                                    adapter = new RestaurantOrdersAdapter(list, act);
                                    ORDERS.setAdapter(adapter);
                                }
                            }
                        }
                        if (index[0] == Rooms.size()) {
                            runOrdersGet();
                            setListener();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        }
    }

    private void setListener() {
        if (MyApp.My_USER.department.equals("Restaurant") || MyApp.My_USER.department.equals("CoffeeShop")) {
            for (int i = 0; i < Rooms.size(); i++) {
                final int finalI = i;
                RESTAURANTListener[i] = Rooms.get(i).getFireRoom().child("Restaurant").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Log.d("restOrder",dataSnapshot.getValue().toString());
                            long value = Long.parseLong(dataSnapshot.getValue().toString());
                            int searchResult = restaurant_order_unit.searchRestaurantOrder(list,Rooms.get(finalI).RoomNumber,THE_FACILITY.id);
                            if (value > 0) {
                                if (searchResult == -1) {
                                    getRestaurantOrder(Rooms.get(finalI).RoomNumber, THE_FACILITY.id, new RestaurantOrderCallback() {
                                        @Override
                                        public void onSuccess(restaurant_order_unit order) {
                                            int searchResult1 = restaurant_order_unit.searchRestaurantOrder(list,order.room,order.facility);
                                            if (searchResult1 == -1) {
                                                list.add(order);
                                                adapter = new RestaurantOrdersAdapter(list, act);
                                                ORDERS.setAdapter(adapter);
                                            }
                                        }

                                        @Override
                                        public void onFail(String error) {

                                        }
                                    });
                                }
                            }
                            else {
                                if (searchResult != -1) {
                                    list.remove(searchResult);
                                    adapter = new RestaurantOrdersAdapter(list, act);
                                    ORDERS.setAdapter(adapter);
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
    }


    void saveUserDataToSharedPreferences(User u) {
        if (u == null) {
            editor.remove("user");
            editor.apply();
        }
        else {
            String stringUser = g.toJson(u);
            editor.putString("user",stringUser);
            editor.apply();
        }
    }

    public void getRestaurantOrder(int roomNumber,int facilityId,RestaurantOrderCallback callback) {
        String url = MyApp.MyProject.url + "facilitys/getRestOrder";
        StringRequest re = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("ordersResp" ,response);
            try {
                JSONObject result = new JSONObject(response);
                if (result.getString("result").equals("success")) {
                    JSONObject row = new JSONObject(result.getString("order"));
                    restaurant_order_unit order = new restaurant_order_unit(row.getInt("id"), row.getInt("Hotel"), row.getInt("Facility"), row.getInt("Reservation"), row.getInt("room"), row.getInt("RorS"), row.getInt("roomId"), row.getLong("dateTime"), row.getDouble("total"), row.getInt("status"));
                    callback.onSuccess(order);
                }
                else {
                    callback.onFail(result.getString("error"));
                }
            } catch (JSONException e) {
                callback.onFail(e.getMessage());
            }
        }, error -> {
            callback.onFail(error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> par = new HashMap<>();
                par.put("facility_id", String.valueOf(facilityId));
                par.put("room_number",String.valueOf(roomNumber));
                return par;
            }
        };
        Q.add(re);
    }

    StringRequest makeGetRestaurantOrderRequest(int roomNumber,int facilityId,RestaurantOrderCallback callback) {
        String url = MyApp.MyProject.url + "facilitys/getRestOrder";
        return  new StringRequest(Request.Method.POST, url, response -> {
            Log.d("ordersResp" ,response);
            try {
                JSONObject result = new JSONObject(response);
                if (result.getString("result").equals("success")) {
                    JSONObject row = new JSONObject(result.getString("order"));
                    restaurant_order_unit order = new restaurant_order_unit(row.getInt("id"), row.getInt("Hotel"), row.getInt("Facility"), row.getInt("Reservation"), row.getInt("room"), row.getInt("RorS"), row.getInt("roomId"), row.getLong("dateTime"), row.getDouble("total"), row.getInt("status"));
                    callback.onSuccess(order);
                }
                else {
                    callback.onFail(result.getString("error"));
                }
            } catch (JSONException e) {
                callback.onFail(e.getMessage());
            }
        }, error -> {
            callback.onFail(error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> par = new HashMap<>();
                par.put("facility_id", String.valueOf(facilityId));
                par.put("room_number",String.valueOf(roomNumber));
                return par;
            }
        };
    }

    void runOrdersGet() {
        for (int i=0;i<Requests.size();i++) {
            StringRequest x = Requests.get(i);
            Timer t = new Timer();
            int finalI = i;
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    Q.add(x);
                    Requests.remove(x);
                    Log.d("runGets",finalI+" run "+Requests.size());
                }
            }, (long) i * 2 * 1000);
        }
//        int[] index = {0};
//        while (index[0] < Requests.size()) {
//            Timer t = new Timer();
//            t.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    Log.d("runGets",index[0]+" run");
//                    StringRequest x = Requests.get(index[0]);
//                    Q.add(x);
//                    Requests.remove(x);
//                }
//            }, (long) index[0] * 2 * 1000);
//        }
    }

}
