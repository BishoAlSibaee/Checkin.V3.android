package com.syriasoft.hotelservices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.callback.ControlLockCallback;
import com.ttlock.bl.sdk.constant.ControlAction;
import com.ttlock.bl.sdk.entity.ControlLockResult;
import com.ttlock.bl.sdk.entity.LockError;
import com.tuya.smart.sdk.api.IResultCallback;
import com.wang.avi.AVLoadingIndicatorView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RESTAURANTS extends AppCompatActivity {
    public Activity act ;
    RecyclerView rests ;
    private List<RESTAURANT_UNIT> list ;
    public TextView time , date,dndText;
    public ImageView dndImage,dndIcon,leftArrow,rightArrow,restaurantIcon ;
    private DatabaseReference myRefSos;
    static Runnable backHomeThread ;
    public static long Current = 0 ;
    static long x = 0 ;
    static Handler H ;
    WindowInsetsControllerCompat windowInsetsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.MODEL.equals("YC-55P") || Build.MODEL.equals("YS4B")) {
            setContentView(R.layout.restaurants_small_ys4b);
        }
        else {
            setContentView(R.layout.restaurants);
        }
        setActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (x == 0 ){
            backHomeThread.run();
        }
    }

    void setActivity() {
        act = this ;
        MyApp.restaurantActivities.add(act);
        list = FullscreenActivity.Restaurants;
        date = (TextView) findViewById(R.id.mainDate);
        time = (TextView) findViewById(R.id.mainTime);
        dndImage = findViewById(R.id.DND_Image);
        dndIcon = findViewById(R.id.DND_Icon);
        dndText = findViewById(R.id.DND_Text);
        restaurantIcon = findViewById(R.id.imageView2);
        leftArrow = findViewById(R.id.leftSlide2);
        rightArrow = findViewById(R.id.imageView12);
        LinearLayoutManager manager = new LinearLayoutManager(act, RecyclerView.HORIZONTAL, false);
        rests = findViewById(R.id.restaurants_recycler);
        rests.setLayoutManager(manager);
        TextView CAPTION = findViewById(R.id.CAPTION2);
        CAPTION.setText(getResources().getString(R.string.restaurant));
        DatabaseReference myRefRestaurant = FullscreenActivity.myRefRestaurant;
        myRefRestaurant.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!Objects.requireNonNull(snapshot.getValue()).toString().equals("0")) {
                    FullscreenActivity.RestaurantStatus = true ;
                    restaurantOn(act);
                }
                else {
                    restaurantOff(act);
                    FullscreenActivity.RestaurantStatus = false ;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
        DatabaseReference myRefDND = FullscreenActivity.myRefDND;
        myRefDND.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Long.parseLong(Objects.requireNonNull(snapshot.getValue()).toString()) > 0 ) {
                    dndOn();
                    FullscreenActivity.DNDStatus = true ;
                }
                else {
                    dndOff();
                    FullscreenActivity.DNDStatus = false ;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
        myRefSos = FullscreenActivity.myRefSos;
        myRefSos.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (!Objects.requireNonNull(snapshot.getValue()).toString().equals("0"))
                {
                    sosOn();
                }
                else
                {
                    sosOff();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference myRefLaundry = FullscreenActivity.myRefLaundry;
        myRefLaundry.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (!Objects.requireNonNull(snapshot.getValue()).toString().equals("0") )
                {
                    laundryOn();
                }
                else
                {
                    laundryOff();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference myRefCleanup = FullscreenActivity.myRefCleanup;
        myRefCleanup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (!Objects.requireNonNull(snapshot.getValue()).toString().equals("0"))
                {
                    cleanupOn();
                }
                else
                {
                    cleanupOff();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference myRefCheckout = FullscreenActivity.myRefCheckout;
        myRefCheckout.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!Objects.requireNonNull(snapshot.getValue()).toString().equals("0")) {
                    checkoutOn();
                }
                else {
                    checkoutOff();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference myRefRoomService = FullscreenActivity.myRefRoomService;
        myRefRoomService.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ( !Objects.requireNonNull(snapshot.getValue()).toString().equals("0") ) {
                    roomServiceOn();
                }
                else {
                    roomServiceOff();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (windowInsetsController != null) {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        }
        if (windowInsetsController != null) {
            windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE);
        }
        blink();
        FullscreenActivity.RestaurantActivities.add(act);
        LinearLayout mainLayout = findViewById(R.id.main_layout);
        mainLayout.setOnClickListener(v -> x=0);
        backHomeThread = new Runnable() {
            @Override
            public void run() {
                H = new Handler();
                x = x+1000 ;
                Log.d("backThread" , x+"");
                H.postDelayed(this,1000);
                if (x >= 60000){
                    LinearLayout v = (LinearLayout) findViewById(R.id.home_Btn);
                    runOnUiThread(() -> {
                        backToMain(v);
                        H.removeCallbacks(backHomeThread);
                        x=0;
                    });
                }

            }
        };
        backHomeThread.run();
        RESTAURANTS_ADAPTER adapter = new RESTAURANTS_ADAPTER(list);
        rests.setAdapter(adapter);
        rests.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            Log.d("currentRest" , Current+" ");
            x=0;
            if (list.size() > 1) {
                if (Current == 0) {
                    leftArrow.setImageResource(R.drawable.subtraction_4);
                    rightArrow.setImageResource(R.drawable.subtraction_3);
                }
                else if (Current+1 == list.size()) {
                    leftArrow.setImageResource(R.drawable.subtraction_15);
                    rightArrow.setImageResource(R.drawable.subtraction_14);
                }
                else {
                    leftArrow.setImageResource(R.drawable.subtraction_15);
                    rightArrow.setImageResource(R.drawable.subtraction_3);
                }
            }
            else {
                leftArrow.setVisibility(View.INVISIBLE);
                rightArrow.setVisibility(View.INVISIBLE);
            }

        });
        KeepScreenFull();
        setLockButton();
    }

    public void backToMain(View view) {
        if (MyApp.restaurantActivities.size() > 0 ){
            for (Activity a:MyApp.restaurantActivities){
                a.finish();
            }
        }
        MyApp.restaurantActivities.clear();
        H.removeCallbacks(backHomeThread);
    }

    private void hideSystemUI() {
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }

    private void KeepScreenFull() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this,300);
                hideSystemUI();
            }
        }).start();
    }

    private void blink() {
        final Calendar x = Calendar.getInstance(Locale.getDefault());
        final Handler handler = new Handler();
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.post(() -> {
                Log.d("Time is : ",x.getTime().toString());
                String currentTime = x.get(Calendar.HOUR_OF_DAY)+":"+x.get(Calendar.MINUTE)+":"+x.get(Calendar.SECOND);
                time.setText(currentTime);
                String currentDate = x.get(Calendar.DAY_OF_MONTH)+ "-" + (x.get(Calendar.MONTH)+1)+"-" + x.get(Calendar.YEAR);
                date.setText(currentDate);
                //hideSystemUI();
                blink();
            });
        }).start();
    }

    private void roomServiceOn(){

        ImageView roomServiceIcon = (ImageView) findViewById(R.id.imageView7);
        roomServiceIcon.setVisibility(View.VISIBLE);

    }
    private void roomServiceOff(){
        ImageView roomServiceIcon = (ImageView) findViewById(R.id.imageView7);
        roomServiceIcon.setVisibility(View.GONE);
    }

    private void checkoutOn(){
        ImageView checkoutIcon = (ImageView) findViewById(R.id.imageView20);
        checkoutIcon.setVisibility(View.VISIBLE);

    }
    private void checkoutOff(){
        ImageView checkoutIcon = (ImageView) findViewById(R.id.imageView20);
        checkoutIcon.setVisibility(View.GONE);
    }

    private  void laundryOn(){
        ImageView checkoutIcon = (ImageView) findViewById(R.id.imageView10);
        checkoutIcon.setVisibility(View.VISIBLE);
    }
    private  void laundryOff(){
        ImageView checkoutIcon = (ImageView) findViewById(R.id.imageView10);
        checkoutIcon.setVisibility(View.GONE);
    }

    private void cleanupOn(){
        ImageView checkoutIcon = (ImageView) findViewById(R.id.imageView9);
        checkoutIcon.setVisibility(View.VISIBLE);
    }
    private void cleanupOff(){
        ImageView checkoutIcon = (ImageView) findViewById(R.id.imageView9);
        checkoutIcon.setVisibility(View.GONE);
    }

    private void dndOn(){
        dndImage.setImageResource(R.drawable.union_6);
        dndIcon.setVisibility(View.VISIBLE);
        dndText.setTextColor(getResources().getColor(R.color.red,null));
    }
    private void dndOff(){
        dndImage.setImageResource(R.drawable.union_2);
        dndIcon.setVisibility(View.GONE);
        dndText.setTextColor(getResources().getColor(R.color.light_blue_A200,null));
    }

    private void sosOn(){
        ImageView checkoutImage = (ImageView) findViewById(R.id.SOS_Image);
        checkoutImage.setImageResource(R.drawable.group_54);
        ImageView checkoutIcon = (ImageView) findViewById(R.id.SOS_Icon);
        checkoutIcon.setVisibility(View.VISIBLE);
        TextView text = (TextView) findViewById(R.id.SOS_Text);
        text.setTextColor(getResources().getColor(R.color.red,null));
    }
    private void sosOff(){
        ImageView checkoutImage = (ImageView) findViewById(R.id.SOS_Image);
        checkoutImage.setImageResource(R.drawable.group_33);
        ImageView checkoutIcon = (ImageView) findViewById(R.id.SOS_Icon);
        checkoutIcon.setVisibility(View.GONE);
        TextView text = (TextView) findViewById(R.id.SOS_Text);
        text.setTextColor(getResources().getColor(R.color.light_blue_A200,null));
    }

    private static void restaurantOn(Activity act) {
        ImageView restaurantIcon = act.findViewById(R.id.imageView2);
        restaurantIcon.setVisibility(View.VISIBLE);
    }
    private static void restaurantOff(Activity act) {
        ImageView restaurantIcon = act.findViewById(R.id.imageView2);
        restaurantIcon.setVisibility(View.GONE);
    }

    public void setDND(View view) {
        if (MyApp.Room.getSERVICE1_B() != null) {
            if (MyApp.Room.getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.dndButton)) != null) {
                if (Boolean.parseBoolean(Objects.requireNonNull(MyApp.Room.getSERVICE1_B().dps.get(String.valueOf(MyApp.ProjectVariables.dndButton))).toString())) {
                    MyApp.Room.getSERVICE1().publishDps("{\" "+MyApp.ProjectVariables.dndButton+"\":false}", new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {

                        }

                        @Override
                        public void onSuccess() {

                        }
                    });
                }
                else {
                    MyApp.Room.getSERVICE1().publishDps("{\" "+MyApp.ProjectVariables.dndButton+"\":true}", new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {

                        }

                        @Override
                        public void onSuccess() {

                        }
                    });
                }
            }
        }
    }

    public void OpenTheDoor(View view) {
        AVLoadingIndicatorView doorLoading = act.findViewById(R.id.loadingIcon);
        ImageView doorImage = act.findViewById(R.id.imageView17);
        if (MyApp.BluetoothLock != null) {
            doorImage.setVisibility(View.GONE);
            doorLoading.setVisibility(View.VISIBLE);
            String url = MyApp.ProjectURL + "roomsManagement/addClientDoorOpen";
            StringRequest req = new StringRequest(Request.Method.POST, url, response -> {
                try {
                    JSONObject result = new JSONObject(response);
                        if (result.getString("result").equals("success")) {
                            TTLockClient.getDefault().controlLock(ControlAction.UNLOCK, MyApp.BluetoothLock.getLockData(), MyApp.BluetoothLock.getLockMac(),new ControlLockCallback() {
                                @Override
                                public void onControlLockSuccess(ControlLockResult controlLockResult) {
                                    ToastMaker.MakeToast("door opened",act);
                                    doorImage.setVisibility(View.VISIBLE);
                                    doorLoading.setVisibility(View.GONE);
                                }
                                @Override
                                public void onFail(LockError error) {
                                    ToastMaker.MakeToast(error.getErrorMsg(),act);
                                    doorImage.setVisibility(View.VISIBLE);
                                    doorLoading.setVisibility(View.GONE);
                                }
                            });
                        }
                } catch (JSONException e) {
                    ToastMaker.MakeToast(e.getMessage(),act);
                    doorImage.setVisibility(View.VISIBLE);
                    doorLoading.setVisibility(View.GONE);
                }
            }, error -> {
                ToastMaker.MakeToast(error.toString(),act);
                doorImage.setVisibility(View.VISIBLE);
                doorLoading.setVisibility(View.GONE);
            });
            Volley.newRequestQueue(act).add(req);
        }
        else {
            if (MyApp.Room.getLOCK_B() != null) {
                doorImage.setVisibility(View.GONE);
                doorLoading.setVisibility(View.VISIBLE);
                String url = MyApp.ProjectURL + "roomsManagement/addClientDoorOpen";
                StringRequest req = new StringRequest(Request.Method.POST, url, response -> {
                    Log.d("doorOpenResp" , response);
                    try {
                        JSONObject result = new JSONObject(response);
                        result.getString("result");
                        if (result.getString("result").equals("success")) {
                            ZigbeeLock.getTokenFromApi(MyApp.cloudClientId, MyApp.cloudSecret, act, new RequestOrder() {
                                @Override
                                public void onSuccess(String token) {
                                    Log.d("doorOpenResp" , "token "+token);
                                    ZigbeeLock.getTicketId(token, MyApp.cloudClientId, MyApp.cloudSecret, MyApp.Room.getLOCK_B().devId, act, new RequestOrder() {
                                        @Override
                                        public void onSuccess(String ticket) {
                                            Log.d("doorOpenResp" , "ticket "+ticket);
                                            ZigbeeLock.unlockWithoutPassword(token, ticket, MyApp.cloudClientId, MyApp.cloudSecret, MyApp.Room.getLOCK_B().devId, act, new RequestOrder() {
                                                @Override
                                                public void onSuccess(String res) {
                                                    Log.d("doorOpenResp" , "res "+res);
                                                    ToastMaker.MakeToast("door opened",act);
                                                    doorImage.setVisibility(View.VISIBLE);
                                                    doorLoading.setVisibility(View.GONE);
                                                }

                                                @Override
                                                public void onFailed(String error) {
                                                    Log.d("openDoorResp" , "res "+error);
                                                    ToastMaker.MakeToast(error,act);
                                                    doorImage.setVisibility(View.VISIBLE);
                                                    doorLoading.setVisibility(View.GONE);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailed(String error) {
                                            Log.d("doorOpenResp" , "ticket "+error);
                                            ToastMaker.MakeToast(error,act);
                                            doorImage.setVisibility(View.VISIBLE);
                                            doorLoading.setVisibility(View.GONE);
                                        }
                                    });
                                }

                                @Override
                                public void onFailed(String error) {
                                    Log.d("doorOpenResp" , "token "+error);
                                    ToastMaker.MakeToast(error,act);
                                    doorImage.setVisibility(View.VISIBLE);
                                    doorLoading.setVisibility(View.GONE);
                                }
                            });
                        }
                        else {
                            ToastMaker.MakeToast(result.getString("error"),act);
                            doorImage.setVisibility(View.VISIBLE);
                            doorLoading.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        Log.d("doorOpenResp" , e.getMessage());
                        ToastMaker.MakeToast(e.getMessage(),act);
                        doorImage.setVisibility(View.VISIBLE);
                        doorLoading.setVisibility(View.GONE);
                    }
                }, error -> {
                    Log.d("doorOpenResp" , error.toString());
                    ToastMaker.MakeToast(error.toString(),act);
                    doorImage.setVisibility(View.VISIBLE);
                    doorLoading.setVisibility(View.GONE);
                }){
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String,String> params = new HashMap<>();
                        params.put("room_id", String.valueOf(MyApp.Room.id));
                        return params;
                    }
                };
                Volley.newRequestQueue(act).add(req);
            }
            else {
                new messageDialog("no lock detected in this room ","failed",act);
            }
        }
    }

    public void SOS(View view) {
        if (FullscreenActivity.CURRENT_ROOM_STATUS == 2) {
            if (!FullscreenActivity.SosStatus) {
                final Dialog d = new Dialog(act);
                d.setContentView(R.layout.confermation_dialog);
                TextView message = (TextView) d.findViewById(R.id.confermationDialog_Text);
                message.setText(getResources().getString(R.string.sendSOSOrder));
                Button cancel = (Button)d.findViewById(R.id.confermationDialog_cancel);
                cancel.setOnClickListener(v -> d.dismiss());
                Button ok = (Button)d.findViewById(R.id.messageDialog_ok);
                ok.setOnClickListener(v -> {
                    FullscreenActivity.SosStatus = true ;
                    sosOn();
                    Calendar c = Calendar.getInstance(Locale.getDefault());
                    myRefSos.setValue(c.getTimeInMillis());
                    d.dismiss();
                    String url = MyApp.ProjectURL + "reservations/addSOSOrder";
                    StringRequest addOrder = new StringRequest(Request.Method.POST, url , response -> Log.d("sosResp" , response), error -> Log.d("sosResp" , error.toString())) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String,String> params = new HashMap<>();
                            params.put("room_id" ,String.valueOf(MyApp.Room.id));
                            return params;
                        }
                    };
                    Volley.newRequestQueue(act).add(addOrder);
                });
                d.show();
            }
            else {
                FullscreenActivity.SosStatus = false ;
                sosOff();
                myRefSos.setValue(0);
                String url = MyApp.ProjectURL + "reservations/cancelServiceOrderControlDevice"+FullscreenActivity.sosCounter;
                StringRequest removeOrder = new StringRequest(Request.Method.POST, url , response -> Log.d("sosResp" , response), error -> {
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String,String> params = new HashMap<>();
                        params.put("room_id" , String.valueOf(MyApp.Room.id));
                        params.put("order_type" , "SOS");
                        return params;
                    }
                };
                Volley.newRequestQueue(act).add(removeOrder);
                FullscreenActivity.sosCounter++ ;
                if (FullscreenActivity.sosCounter == 5) {
                    FullscreenActivity.sosCounter = 1 ;
                }
            }
        }
        else {
            ToastMaker.MakeToast("This Room Is Vacant" , act);
        }
    }

    void setLockButton() {
        LinearLayout doorLayout = findViewById(R.id.Door_Button);
        if (MyApp.BluetoothLock == null && MyApp.Room.getLOCK_B() == null) {
            doorLayout.setVisibility(View.GONE);
        }
        else {
            doorLayout.setVisibility(View.VISIBLE);
        }
    }

}