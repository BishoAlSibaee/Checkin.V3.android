package hotelservices.syriasoft.cleanup;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.syriasoft.hotelservices.R;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.callback.ControlLockCallback;
import com.ttlock.bl.sdk.constant.ControlAction;
import com.ttlock.bl.sdk.entity.ControlLockResult;
import com.ttlock.bl.sdk.entity.LockError;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.enums.TYDevicePublishModeEnum;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ROOMS_ADAPTER extends RecyclerView.Adapter<ROOMS_ADAPTER.HOLDER> {

    List<ROOM> list;

    public ROOMS_ADAPTER(List<ROOM> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ROOMS_ADAPTER.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rooms_unit, parent, false);
        return new HOLDER(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final HOLDER holder, @SuppressLint("RecyclerView") final int position) {
        holder.room.setText(String.valueOf(list.get(position).RoomNumber));
        if (list.get(position).getGuestIs() == 0) {
            holder.guest.setText(MyApp.getResourceString(R.string.out));
        }
        else if (list.get(position).getGuestIs() == 1) {
            holder.guest.setText(MyApp.getResourceString(R.string.in));
        }
        if (list.get(position).roomStatus == 1) {
            holder.itemView.setBackgroundResource(R.drawable.green_room);
            holder.guest.setVisibility(View.INVISIBLE);
        }
        else if (list.get(position).roomStatus == 2) {
            holder.itemView.setBackgroundResource(R.drawable.red_room);
        }
        else if (list.get(position).roomStatus == 3) {
            holder.itemView.setBackgroundResource(R.drawable.blue_room);
            holder.guest.setVisibility(View.INVISIBLE);
        }
        else if (list.get(position).roomStatus == 4) {
            holder.itemView.setBackgroundResource(R.drawable.gray_room);
            holder.guest.setVisibility(View.INVISIBLE);
        }
//        holder.itemView.setOnClickListener(v -> {
//            final Dialog d = new Dialog(holder.itemView.getContext());
//            d.setContentView(R.layout.room_dialog);
//            d.setCancelable(false);
//            Window w = d.getWindow();
//            w.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            TextView text = d.findViewById(R.id.room_dialog_text);
//            ImageView powerImage = d.findViewById(R.id.imageView7);
//            if (list.get(position).getPOWER() != null) {
//                if (Objects.requireNonNull(list.get(position).getPOWER().getDps().get("1")).toString().equals("true") && Objects.requireNonNull(list.get(position).getPOWER().getDps().get("2")).toString().equals("true")) {
//                    powerImage.setImageResource(R.drawable.ic_baseline_power_24);
//                } else {
//                    powerImage.setImageResource(R.drawable.ic_baseline_power_off_24);
//                }
//            }
//            text.setText(MessageFormat.format("Room: {0}", list.get(position).RoomNumber));
//            Button door = d.findViewById(R.id.room_dialog_door);
//            door.setTextSize(holder.itemView.getContext().getResources().getDimension(R.dimen.roomDialogButtonsText));
//            Button power = d.findViewById(R.id.room_dialog_power);
//            power.setTextSize(holder.itemView.getContext().getResources().getDimension(R.dimen.roomDialogButtonsText));
//            Button powerOff = d.findViewById(R.id.button4);
//            powerOff.setTextSize(holder.itemView.getContext().getResources().getDimension(R.dimen.roomDialogButtonsText));
//            ImageView close = d.findViewById(R.id.imageView6);
//            ProgressBar p = d.findViewById(R.id.progressBar4);
//            p.setVisibility(View.INVISIBLE);
//            door.setOnClickListener(v14 -> {
//                if (list.get(position).getLOCK() != null) {
//                    Log.d("doorOpenResp" , "b lock not null");
//                    String url = MyApp.URL + "roomsManagement/addUserDoorOpen";
//                    p.setVisibility(View.VISIBLE);
//                    StringRequest req = new StringRequest(Request.Method.POST, url, response -> {
//                        try {
//                            JSONObject result = new JSONObject(response);
//                            result.getString("result");
//                            if (result.getString("result").equals("success")) {
//                                TTLockClient.getDefault().controlLock(ControlAction.UNLOCK, list.get(position).getLOCK().getLockData(), list.get(position).getLOCK().getLockMac(), new ControlLockCallback() {
//                                    @Override
//                                    public void onControlLockSuccess(ControlLockResult controlLockResult) {
//                                        p.setVisibility(View.INVISIBLE);
//                                        Toast.makeText(holder.itemView.getContext(), "door opened", Toast.LENGTH_SHORT).show();
//                                    }
//
//                                    @Override
//                                    public void onFail(LockError error) {
//                                        p.setVisibility(View.INVISIBLE);
//                                        Toast.makeText(holder.itemView.getContext(), error.getErrorMsg(), Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        } catch (JSONException e) {
//                            p.setVisibility(View.INVISIBLE);
//                            Toast.makeText(holder.itemView.getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
//                        }
//                    }, error -> {
//                        p.setVisibility(View.INVISIBLE);
//                        Toast.makeText(holder.itemView.getContext(),error.toString(),Toast.LENGTH_SHORT).show();
//                    }){
//                        @Override
//                        protected Map<String, String> getParams() {
//                            Map<String,String> params = new HashMap<>();
//                            params.put("room_id", String.valueOf(list.get(position).id));
//                            params.put("user_id",String.valueOf(MyApp.My_USER.id));
//                            return params;
//                        }
//                    };
//                    Volley.newRequestQueue(holder.itemView.getContext()).add(req);
//                }
//                else {
//                    if (list.get(position).getLOCK_T() != null) {
//                        Log.d("doorOpenResp" , "b lock null ");
//                        String url = MyApp.URL + "roomsManagement/addUserDoorOpen";
//                        p.setVisibility(View.VISIBLE);
//                        StringRequest req = new StringRequest(Request.Method.POST, url, response -> {
//                            Log.d("doorOpenResp" , response+ " "+list.get(position).id);
//                            try {
//                                JSONObject result = new JSONObject(response);
//                                    if (result.getString("result").equals("success")) {
//                                        ZigbeeLock.getTokenFromApi(MyApp.cloudClientId, MyApp.cloudSecret, holder.itemView.getContext(), new RequestOrder() {
//                                            @Override
//                                            public void onSuccess(String token) {
//                                                Log.d("doorOpenResp" , "token "+token);
//                                                ZigbeeLock.getTicketId(token, MyApp.cloudClientId, MyApp.cloudSecret,list.get(position).getLOCK_T().devId, holder.itemView.getContext(), new RequestOrder() {
//                                                    @Override
//                                                    public void onSuccess(String ticket) {
//                                                        Log.d("doorOpenResp" , "ticket "+ticket);
//                                                        ZigbeeLock.unlockWithoutPassword(token, ticket, MyApp.cloudClientId, MyApp.cloudSecret,list.get(position).getLOCK_T().devId, holder.itemView.getContext(), new RequestOrder() {
//                                                            @Override
//                                                            public void onSuccess(String res) {
//                                                                p.setVisibility(View.INVISIBLE);
//                                                                Toast.makeText(holder.itemView.getContext(),"door opened",Toast.LENGTH_SHORT).show();
//                                                            }
//
//                                                            @Override
//                                                            public void onFailed(String error) {
//                                                                p.setVisibility(View.INVISIBLE);
//                                                                Toast.makeText(holder.itemView.getContext(),error,Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        });
//                                                    }
//                                                    @Override
//                                                    public void onFailed(String error) {
//                                                        p.setVisibility(View.INVISIBLE);
//                                                        Toast.makeText(holder.itemView.getContext(),error,Toast.LENGTH_SHORT).show();
//                                                    }
//                                                });
//                                            }
//                                            @Override
//                                            public void onFailed(String error) {
//                                                p.setVisibility(View.INVISIBLE);
//                                                Toast.makeText(holder.itemView.getContext(),error,Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                    }
//                                    else {
//                                        p.setVisibility(View.INVISIBLE);
//                                        Toast.makeText(holder.itemView.getContext(),"error",Toast.LENGTH_SHORT).show();
//                                    }
//
//                            } catch (JSONException e) {
//                                p.setVisibility(View.INVISIBLE);
//                                Toast.makeText(holder.itemView.getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
//                            }
//                        }, error -> {
//                            p.setVisibility(View.INVISIBLE);
//                            Toast.makeText(holder.itemView.getContext(),error.toString(),Toast.LENGTH_SHORT).show();
//                        }){
//                            @Override
//                            protected Map<String, String> getParams() {
//                                Map<String,String> params = new HashMap<>();
//                                params.put("room_id", String.valueOf(list.get(position).id));
//                                params.put("user_id",String.valueOf(MyApp.My_USER.id));
//                                return params;
//                            }
//                        };
//                        Volley.newRequestQueue(holder.itemView.getContext()).add(req);
//                    }
//                    else {
//                        new messageDialog("no lock detected in this room ","failed",holder.itemView.getContext());
//                    }
//                }
//            });
//            power.setOnClickListener(v13 -> {
//                if (list.get(position).getPOWER() != null) {
//                    Log.d("powerAction","start");
//                    Log.d("powerAction",list.get(position).getPOWER().getIsOnline()+" ");
//                    p.setVisibility(View.VISIBLE);
//                    Timer t = new Timer();
//                    t.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            p.setVisibility(View.INVISIBLE);
//                            new messageDialog("power on failed","Failed",holder.itemView.getContext());
//                        }
//                    },7000);
//                    ITuyaDevice dd = TuyaHomeSdk.newDeviceInstance(list.get(position).getPOWER().devId);
//                    dd.registerDevListener(new IDevListener() {
//                        @Override
//                        public void onDpUpdate(String devId, String dpStr) {
//                            Log.d("powerAction",dpStr);
//                            t.cancel();
//                            p.setVisibility(View.INVISIBLE);
//                            new messageDialog("Power At Room " + list.get(position).RoomNumber + " is On ", "Room " + list.get(position).RoomNumber + " Power On", holder.itemView.getContext());
//                            dd.unRegisterDevListener();
//                        }
//
//                        @Override
//                        public void onRemoved(String devId) {
//
//                        }
//
//                        @Override
//                        public void onStatusChanged(String devId, boolean online) {
//
//                        }
//
//                        @Override
//                        public void onNetworkStatusChanged(String devId, boolean status) {
//
//                        }
//
//                        @Override
//                        public void onDevInfoUpdate(String devId) {
//
//                        }
//                    });
//                    int Minutes = MyApp.ProjectVariables.HKCleanTime;
//                    Minutes = Minutes * 60;
//                    if (MyApp.ProjectVariables.PoweroffAfterHK == 1) {
//                        dd.publishDps("{\"1\":true,\"2\": true,\"8\":"+Minutes+"}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
//                            @Override
//                            public void onError(String code, String error) {
//                                Log.d("powerAction",code+" "+error);
//                            }
//
//                            @Override
//                            public void onSuccess() {
//                                Log.d("powerAction","sent");
//                            }
//                        });
//                    }
//                    else if(MyApp.ProjectVariables.PoweroffAfterHK == 0) {
//                        if (list.get(position).roomStatus == 2) {
//                            dd.publishDps("{\"1\":true,\"2\": true,\"8\":"+Minutes+"}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
//                                @Override
//                                public void onError(String code, String error) {
//                                    Log.d("powerAction",code+" "+error);
//                                }
//
//                                @Override
//                                public void onSuccess() {
//                                    Log.d("powerAction","sent");
//                                }
//                            });
//                        }
//                        else {
//                            dd.publishDps("{\"1\":true,\"2\":true,\"8\":"+Minutes+",\"7\":"+Minutes+"}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
//                                @Override
//                                public void onError(String code, String error) {
//                                    Log.d("powerAction",code+" "+error);
//                                }
//
//                                @Override
//                                public void onSuccess() {
//                                    Log.d("powerAction","sent");
//                                }
//                            });
//                        }
//                    }
//                }
//            });
//            powerOff.setOnClickListener(v12 -> {
//                if (list.get(position).getPOWER() != null) {
//                    p.setVisibility(View.VISIBLE);
//                    Timer t = new Timer();
//                    t.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            p.setVisibility(View.INVISIBLE);
//                            new messageDialog("power off failed","Failed",holder.itemView.getContext());
//                        }
//                    },7000);
//                    ITuyaDevice dd = TuyaHomeSdk.newDeviceInstance(list.get(position).getPOWER().devId);
//                    dd.registerDevListener(new IDevListener() {
//                        @Override
//                        public void onDpUpdate(String devId, String dpStr) {
//                            t.cancel();
//                            p.setVisibility(View.INVISIBLE);
//                            new messageDialog("Power At Room " + list.get(position).RoomNumber + " is Off ", "Room " + list.get(position).RoomNumber + " Power Off", holder.itemView.getContext());
//                            dd.unRegisterDevListener();
//                        }
//
//                        @Override
//                        public void onRemoved(String devId) {
//
//                        }
//
//                        @Override
//                        public void onStatusChanged(String devId, boolean online) {
//
//                        }
//
//                        @Override
//                        public void onNetworkStatusChanged(String devId, boolean status) {
//
//                        }
//
//                        @Override
//                        public void onDevInfoUpdate(String devId) {
//
//                        }
//                    });
//                    if (MyApp.ProjectVariables.PoweroffAfterHK == 1) {
//                        dd.publishDps("{\"1\":true,\"2\":false}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
//                            @Override
//                            public void onError(String code, String error) {
//                                Log.d("powerAction",code+" "+error);
//                            }
//
//                            @Override
//                            public void onSuccess() {
//
//                            }
//                        });
//                    }
//                    else if (MyApp.ProjectVariables.PoweroffAfterHK == 0) {
//                        if (list.get(position).roomStatus == 2) {
//                            dd.publishDps("{\"1\":true,\"2\":false}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
//                                @Override
//                                public void onError(String code, String error) {
//                                    Log.d("powerAction",code+" "+error);
//                                }
//
//                                @Override
//                                public void onSuccess() {
//
//                                }
//                            });
//                        }
//                        else {
//                            dd.publishDps("{\"1\":false,\"2\":false}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
//                                @Override
//                                public void onError(String code, String error) {
//                                    Log.d("powerAction",code+" "+error);
//                                }
//
//                                @Override
//                                public void onSuccess() {
//
//                                }
//                            });
//                        }
//                    }
//
//                }
//            });
//            close.setOnClickListener(v1 -> d.dismiss());
//            if (list.get(position).getPOWER() == null) {
//                power.setActivated(false);
//                power.setClickable(false);
//                powerOff.setActivated(false);
//                powerOff.setClickable(false);
//                power.setTextColor(Color.GRAY);
//                powerOff.setTextColor(Color.GRAY);
//            }
//            if (list.get(position).getLOCK() == null && list.get(position).getLOCK_T() == null) {
//                door.setActivated(false);
//                door.setClickable(false);
//                door.setTextColor(Color.GRAY);
//            }
//            d.show();
//        });
        holder.room.setOnClickListener(v -> {
            final Dialog d = new Dialog(holder.itemView.getContext());
            d.setContentView(R.layout.room_dialog);
            d.setCancelable(false);
            Window w = d.getWindow();
            w.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView text = d.findViewById(R.id.room_dialog_text);
            ImageView powerImage = d.findViewById(R.id.imageView7);
            if (list.get(position).getPOWER() != null) {
                if (Objects.requireNonNull(list.get(position).getPOWER().getDps().get("1")).toString().equals("true") && Objects.requireNonNull(list.get(position).getPOWER().getDps().get("2")).toString().equals("true")) {
                    powerImage.setImageResource(R.drawable.ic_baseline_power_24);
                }
                else {
                    powerImage.setImageResource(R.drawable.ic_baseline_power_off_24);
                }
            }
            text.setText(MessageFormat.format("Room: {0}", list.get(position).RoomNumber));
            Button door = d.findViewById(R.id.room_dialog_door);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                door.setAutoSizeTextTypeUniformWithConfiguration(10,20,1,TypedValue.COMPLEX_UNIT_DIP);
            }
            Button power = d.findViewById(R.id.room_dialog_power);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                power.setAutoSizeTextTypeUniformWithConfiguration(10,20,1,TypedValue.COMPLEX_UNIT_DIP);
            }
            Button powerOff = d.findViewById(R.id.button4);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                powerOff.setAutoSizeTextTypeUniformWithConfiguration(10,20,1,TypedValue.COMPLEX_UNIT_DIP);
            }
            ImageView close = d.findViewById(R.id.imageView6);
            ProgressBar p = d.findViewById(R.id.progressBar4);
            p.setVisibility(View.INVISIBLE);
            door.setOnClickListener(v14 -> {
                if (list.get(position).getLOCK() != null) {
                    Log.d("doorOpenResp" , "b lock not null");
                    String url = MyApp.MyProject.Url + "roomsManagement/addUserDoorOpen";
                    p.setVisibility(View.VISIBLE);
                    StringRequest req = new StringRequest(Request.Method.POST, url, response -> {
                        try {
                            JSONObject result = new JSONObject(response);
                            result.getString("result");
                            if (result.getString("result").equals("success")) {
                                TTLockClient.getDefault().controlLock(ControlAction.UNLOCK, list.get(position).getLOCK().getLockData(), list.get(position).getLOCK().getLockMac(), new ControlLockCallback() {
                                    @Override
                                    public void onControlLockSuccess(ControlLockResult controlLockResult) {
                                        p.setVisibility(View.INVISIBLE);
                                        Toast.makeText(holder.itemView.getContext(), "door opened", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFail(LockError error) {
                                        p.setVisibility(View.INVISIBLE);
                                        Toast.makeText(holder.itemView.getContext(), error.getErrorMsg(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            p.setVisibility(View.INVISIBLE);
                            Toast.makeText(holder.itemView.getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }, error -> {
                        p.setVisibility(View.INVISIBLE);
                        Toast.makeText(holder.itemView.getContext(),error.toString(),Toast.LENGTH_SHORT).show();
                    }){
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String,String> params = new HashMap<>();
                            params.put("room_id", String.valueOf(list.get(position).id));
                            params.put("user_id",String.valueOf(MyApp.My_USER.id));
                            return params;
                        }
                    };
                    Volley.newRequestQueue(holder.itemView.getContext()).add(req);
                }
                else {
                    if (list.get(position).getLOCK_T() != null) {
                        Log.d("doorOpenResp" , "b lock null ");
                        String url = MyApp.MyProject.Url + "roomsManagement/addUserDoorOpen";
                        p.setVisibility(View.VISIBLE);
                        StringRequest req = new StringRequest(Request.Method.POST, url, response -> {
                            Log.d("doorOpenResp" , response+ " "+list.get(position).id);
                            try {
                                JSONObject result = new JSONObject(response);
                                if (result.getString("result").equals("success")) {
                                    ZigbeeLock.getTokenFromApi(MyApp.cloudClientId, MyApp.cloudSecret, holder.itemView.getContext(), new RequestOrder() {
                                        @Override
                                        public void onSuccess(String token) {
                                            Log.d("doorOpenResp" , "token "+token);
                                            ZigbeeLock.getTicketId(token, MyApp.cloudClientId, MyApp.cloudSecret,list.get(position).getLOCK_T().devId, holder.itemView.getContext(), new RequestOrder() {
                                                @Override
                                                public void onSuccess(String ticket) {
                                                    Log.d("doorOpenResp" , "ticket "+ticket);
                                                    ZigbeeLock.unlockWithoutPassword(token, ticket, MyApp.cloudClientId, MyApp.cloudSecret,list.get(position).getLOCK_T().devId, holder.itemView.getContext(), new RequestOrder() {
                                                        @Override
                                                        public void onSuccess(String res) {
                                                            p.setVisibility(View.INVISIBLE);
                                                            Toast.makeText(holder.itemView.getContext(),"door opened",Toast.LENGTH_SHORT).show();
                                                        }

                                                        @Override
                                                        public void onFailed(String error) {
                                                            p.setVisibility(View.INVISIBLE);
                                                            Toast.makeText(holder.itemView.getContext(),error,Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                                @Override
                                                public void onFailed(String error) {
                                                    p.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(holder.itemView.getContext(),error,Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                        @Override
                                        public void onFailed(String error) {
                                            p.setVisibility(View.INVISIBLE);
                                            Toast.makeText(holder.itemView.getContext(),error,Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else {
                                    p.setVisibility(View.INVISIBLE);
                                    Toast.makeText(holder.itemView.getContext(),"error",Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                p.setVisibility(View.INVISIBLE);
                                Toast.makeText(holder.itemView.getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }, error -> {
                            p.setVisibility(View.INVISIBLE);
                            Toast.makeText(holder.itemView.getContext(),error.toString(),Toast.LENGTH_SHORT).show();
                        }){
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String,String> params = new HashMap<>();
                                params.put("room_id", String.valueOf(list.get(position).id));
                                params.put("user_id",String.valueOf(MyApp.My_USER.id));
                                return params;
                            }
                        };
                        Volley.newRequestQueue(holder.itemView.getContext()).add(req);
                    }
                    else {
                        new messageDialog("no lock detected in this room ","failed",holder.itemView.getContext());
                    }
                }
            });
            power.setOnClickListener(v13 -> {
                if (list.get(position).getPOWER() != null) {
                    Log.d("powerAction","start");
                    list.get(position).getPOWER().setIsOnline(true);
                    Log.d("powerAction",list.get(position).getPOWER().getIsOnline()+" "+list.get(position).getPOWER().getName()+" "+list.get(position).getPOWER().devId);
                    p.setVisibility(View.VISIBLE);
                    Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            p.setVisibility(View.INVISIBLE);
                            new messageDialog("power on failed","Failed",holder.itemView.getContext());
                        }
                    },7000);
                    ITuyaDevice dd = TuyaHomeSdk.newDeviceInstance(list.get(position).getPOWER().devId);
                    dd.registerDevListener(new IDevListener() {
                        @Override
                        public void onDpUpdate(String devId, String dpStr) {
                            Log.d("powerAction",dpStr);
                            t.cancel();
                            p.setVisibility(View.INVISIBLE);
                            new messageDialog("Power At Room " + list.get(position).RoomNumber + " is On ", "Room " + list.get(position).RoomNumber + " Power On", holder.itemView.getContext());
                            powerImage.setImageResource(R.drawable.ic_baseline_power_24);
                            dd.unRegisterDevListener();
                        }

                        @Override
                        public void onRemoved(String devId) {

                        }

                        @Override
                        public void onStatusChanged(String devId, boolean online) {

                        }

                        @Override
                        public void onNetworkStatusChanged(String devId, boolean status) {

                        }

                        @Override
                        public void onDevInfoUpdate(String devId) {

                        }
                    });
                    int Minutes = MyApp.ProjectVariables.HKCleanTime;
                    Minutes = Minutes * 60;
                    if (MyApp.ProjectVariables.PoweroffAfterHK == 1) {
                        dd.publishDps("{\"1\":true,\"2\":true,\"8\":"+Minutes+"}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {
                                Log.d("powerAction",code+" "+error);
                            }

                            @Override
                            public void onSuccess() {
                                Log.d("powerAction","sent");
                            }
                        });
                    }
                    else if(MyApp.ProjectVariables.PoweroffAfterHK == 0) {
                        if (list.get(position).roomStatus == 2) {
                            dd.publishDps("{\"1\":true,\"2\":true,\"8\":"+Minutes+"}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    Log.d("powerAction",code+" "+error);
                                }

                                @Override
                                public void onSuccess() {
                                    Log.d("powerAction","sent");
                                }
                            });
                        }
                        else {
                            dd.publishDps("{\"1\":true,\"2\":true,\"8\":"+Minutes+",\"7\":"+Minutes+"}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    Log.d("powerAction",code+" "+error);
                                }

                                @Override
                                public void onSuccess() {
                                    Log.d("powerAction","sent");
                                }
                            });
                        }
                    }
                }
            });
            powerOff.setOnClickListener(v12 -> {
                if (list.get(position).getPOWER() != null) {
                    p.setVisibility(View.VISIBLE);
                    Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            p.setVisibility(View.INVISIBLE);
                            //new messageDialog("power off failed","Failed",holder.itemView.getContext());
                        }
                    },7000);
                    ITuyaDevice dd = TuyaHomeSdk.newDeviceInstance(list.get(position).getPOWER().devId);
                    dd.registerDevListener(new IDevListener() {
                        @Override
                        public void onDpUpdate(String devId, String dpStr) {
                            t.cancel();
                            p.setVisibility(View.INVISIBLE);
                            new messageDialog("Power At Room " + list.get(position).RoomNumber + " is Off ", "Room " + list.get(position).RoomNumber + " Power Off", holder.itemView.getContext());
                            powerImage.setImageResource(R.drawable.ic_baseline_power_off_24);
                            dd.unRegisterDevListener();
                        }

                        @Override
                        public void onRemoved(String devId) {

                        }

                        @Override
                        public void onStatusChanged(String devId, boolean online) {

                        }

                        @Override
                        public void onNetworkStatusChanged(String devId, boolean status) {

                        }

                        @Override
                        public void onDevInfoUpdate(String devId) {

                        }
                    });
                    if (MyApp.ProjectVariables.PoweroffAfterHK == 1) {
                        dd.publishDps("{\"1\": true,\"2\":false}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {
                                Log.d("powerAction",code+" "+error);
                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    }
                    else if (MyApp.ProjectVariables.PoweroffAfterHK == 0) {
                        if (list.get(position).roomStatus == 2) {
                            dd.publishDps("{\"1\":true,\"2\":false}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    Log.d("powerAction",code+" "+error);
                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                        else {
                            dd.publishDps("{\"1\":false,\"2\":false}", TYDevicePublishModeEnum.TYDevicePublishModeHttp, new IResultCallback() {
                                @Override
                                public void onError(String code, String error) {
                                    Log.d("powerAction",code+" "+error);
                                }

                                @Override
                                public void onSuccess() {

                                }
                            });
                        }
                    }
                }
            });
            close.setOnClickListener(v1 -> d.dismiss());
            if (list.get(position).getPOWER() == null) {
                power.setActivated(false);
                power.setClickable(false);
                powerOff.setActivated(false);
                powerOff.setClickable(false);
                power.setTextColor(Color.GRAY);
                powerOff.setTextColor(Color.GRAY);
            }
            if (list.get(position).getLOCK() == null && list.get(position).getLOCK_T() == null) {
                door.setActivated(false);
                door.setClickable(false);
                door.setTextColor(Color.GRAY);
            }
            d.show();
        });
        list.get(position).getFireRoom().child("ClientIn").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    list.get(position).setGuestIs(Integer.parseInt(dataSnapshot.getValue().toString()));
                    if (list.get(position).getGuestIs() == 0) {
                        holder.guest.setText(MyApp.getResourceString(R.string.out));
                    }
                    else if (list.get(position).getGuestIs() == 1) {
                        holder.guest.setText(MyApp.getResourceString(R.string.in));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        list.get(position).getFireRoom().child("roomStatus").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    list.get(position).roomStatus = Integer.parseInt(dataSnapshot.getValue().toString());
                    if (list.get(position).roomStatus == 1) {
                        holder.itemView.setBackgroundResource(R.drawable.green_room);
                        holder.guest.setVisibility(View.INVISIBLE);
                    }
                    else if (list.get(position).roomStatus == 2) {
                        holder.itemView.setBackgroundResource(R.drawable.red_room);
                    }
                    else if (list.get(position).roomStatus == 3) {
                        holder.itemView.setBackgroundResource(R.drawable.blue_room);
                        holder.guest.setVisibility(View.INVISIBLE);
                    }
                    else if (list.get(position).roomStatus == 4) {
                        holder.itemView.setBackgroundResource(R.drawable.gray_room);
                        holder.guest.setVisibility(View.INVISIBLE);
                    }
                    if (list.get(position).getGuestIs() == 0) {
                        holder.guest.setText(MyApp.getResourceString(R.string.out));
                    }
                    else if (list.get(position).getGuestIs() == 1) {
                        holder.guest.setText(MyApp.getResourceString(R.string.in));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class HOLDER extends RecyclerView.ViewHolder {
        Button room;
        TextView guest;
        public HOLDER(@NonNull View itemView) {
            super(itemView);
            room = itemView.findViewById(R.id.rooms_roomBtn);
            guest = itemView.findViewById(R.id.textView13);
        }
    }

}











