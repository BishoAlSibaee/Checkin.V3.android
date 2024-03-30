package hotelservices.syriasoft.cleanup.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.syriasoft.hotelservices.R;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hotelservices.syriasoft.cleanup.MainActivity;
import hotelservices.syriasoft.cleanup.MyApp;
import hotelservices.syriasoft.cleanup.ROOM;
import hotelservices.syriasoft.cleanup.VolleyCallback;
import hotelservices.syriasoft.cleanup.cleanOrder;
import hotelservices.syriasoft.cleanup.messageDialog;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.Holder> {

    List<cleanOrder> list;

    public OrdersAdapter(List<cleanOrder> list) {
        this.list = sortList(list);
    }

    @NonNull
    @Override
    public OrdersAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_unit,null);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.Holder holder, @SuppressLint("RecyclerView") int position) {
        ROOM RR = searchRoomByNumber(Integer.parseInt(list.get(position).roomNumber), MyApp.Rooms);
        Context co = holder.itemView.getContext();
        if (list.get(position).room != null) {
            if (RR.roomStatus == 1) {
                holder.room.setTextColor(Color.WHITE);
                holder.room.setBackgroundColor(co.getColor(R.color.greenRoom));
                holder.img.setBackgroundColor(co.getColor(R.color.greenRoom));
                holder.dep.setBackgroundColor(co.getColor(R.color.greenRoom));
                holder.dep.setTextColor(Color.WHITE);
                holder.date.setBackgroundColor(co.getColor(R.color.greenRoom));
                holder.date.setTextColor(Color.WHITE);
            }
            else if (RR.roomStatus == 2) {
                holder.room.setTextColor(Color.WHITE);
                holder.room.setBackgroundColor(co.getColor(R.color.redRoom));
                holder.room.setBackgroundResource(R.color.redRoom);
                holder.SuitStatus.setBackgroundColor(co.getColor(R.color.redRoom));
                holder.img.setBackgroundColor(co.getColor(R.color.redRoom));
                holder.dep.setBackgroundColor(co.getColor(R.color.redRoom));
                holder.dep.setTextColor(Color.WHITE);
                holder.date.setBackgroundColor(co.getColor(R.color.redRoom));
                holder.date.setTextColor(Color.WHITE);
            }
            else if (RR.roomStatus == 3) {
                holder.room.setTextColor(Color.WHITE);
                holder.room.setBackgroundColor(co.getColor(R.color.blueRoom));
                holder.SuitStatus.setBackgroundColor(co.getColor(R.color.blueRoom));
                holder.img.setBackgroundColor(co.getColor(R.color.blueRoom));
                holder.dep.setBackgroundColor(co.getColor(R.color.blueRoom));
                holder.dep.setTextColor(Color.WHITE);
                holder.date.setBackgroundColor(co.getColor(R.color.blueRoom));
                holder.date.setTextColor(Color.WHITE);
            }
            else if (RR.roomStatus == 4) {
                holder.room.setTextColor(Color.WHITE);
                holder.room.setBackgroundColor(Color.GRAY);
                holder.SuitStatus.setBackgroundColor(Color.GRAY);
                holder.img.setBackgroundColor(Color.GRAY);
                holder.dep.setBackgroundColor(Color.GRAY);
                holder.dep.setTextColor(Color.WHITE);
                holder.date.setBackgroundColor(Color.WHITE);
                holder.date.setTextColor(Color.WHITE);
            }
            RR.getFireRoom().child("SuiteStatus").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        if (dataSnapshot.getValue().toString().equals("2")) {
                            holder.SuitStatus.setVisibility(View.VISIBLE);
                            holder.SuitStatus.setText("S");
                            holder.SuitStatus.setTextColor(Color.WHITE);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(list.get(position).date);
        int month = c.get(Calendar.MONTH) + 1;
        String DATE = c.get(Calendar.DAY_OF_MONTH)+"/"+month+"/"+c.get(Calendar.YEAR)+" "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE);
        holder.date.setText(DATE);
        if (list.get(position).dep.equals("RoomService")) {
            holder.dep.setText(list.get(position).roomServiceText);
        }
        else {
            holder.dep.setText(list.get(position).dep);
        }
        switch (list.get(position).dep) {
            case "Cleanup":
                holder.img.setImageResource(R.drawable.cleanup_btn);
                break;
            case "Laundry":
                holder.img.setImageResource(R.drawable.laundry_btn);
                break;
            case "RoomService":
                holder.img.setImageResource(R.drawable.roomservice);
                break;
            case "SOS" :
                holder.img.setImageResource(R.drawable.sos_btn);
                break;
            case "MiniBar" :
                holder.img.setImageResource(R.drawable.minibar);
                break;
        }
        holder.room.setText(list.get(position).roomNumber);
        holder.itemView.setOnLongClickListener(v -> {
            if (RR == null) {
                Toast.makeText(co,"Room is Not Found",Toast.LENGTH_SHORT).show();
            }
            else {
                try {
                    createConfirmOrderDialog(co, RR, holder.dep, holder.date,holder.dep.getText().toString());
                }
                catch (Exception e) {
                    Toast.makeText(co,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        });
        holder.itemView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView room,dep,date, SuitStatus;
        ImageView img;
        public Holder(@NonNull View itemView) {
            super(itemView);
            room = itemView.findViewById(R.id.cleanOrder_room);
            dep = itemView.findViewById(R.id.cleanOrder_orderType);
            date = itemView.findViewById(R.id.cleanOrder_orderDate);
            SuitStatus = itemView.findViewById(R.id.textView3);
            img = itemView.findViewById(R.id.imageView2);
        }
    }

    private List<cleanOrder> sortList(List<cleanOrder> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 1; j < (list.size() - i); j++) {
                if (list.get(j - 1).date > list.get(j).date) {
                    Collections.swap(list, j, j - 1);
                }
            }
        }

        return list;
    }

    ROOM searchRoomByNumber(int roomNumber, List<ROOM> rooms) {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).RoomNumber == roomNumber) {
                return rooms.get(i);
            }
        }
        return null;
    }

    void createConfirmOrderDialog(Context co,ROOM RR,TextView dep,TextView date,String orderType) {
        Activity act = (Activity) co;
        Dialog d = new Dialog(co);
        d.setContentView(R.layout.confermation_dialog);
        d.setCancelable(false);
        Window w = d.getWindow();
        w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        AVLoadingIndicatorView progress = d.findViewById(R.id.progressBar5);
        TextView title = d.findViewById(R.id.textView2);
        title.setText(MessageFormat.format("{0} {1}", RR.RoomNumber, orderType));
        TextView message = d.findViewById(R.id.confermationDialog_Text);
        message.setText(MyApp.getResourceString(R.string.confermationDialog_text));
        Button cancel = d.findViewById(R.id.confermationDialog_cancel);
        cancel.setOnClickListener(v110 -> {
            d.dismiss();
        });
        Button ok = d.findViewById(R.id.messageDialog_ok);
        ok.setOnClickListener(v17 -> {
            if (orderType.equals("Cleanup")) {
                if (RR.roomStatus == 3) {
                    progress.setVisibility(View.VISIBLE);
                    prepareRoom(MainActivity.Q, String.valueOf(RR.id),new VolleyCallback() {
                        @Override
                        public void onSuccess(String res) {
                            d.dismiss();
                            progress.setVisibility(View.INVISIBLE);
                            Toast.makeText(act, "Order Done", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailed(String error) {
                            progress.setVisibility(View.INVISIBLE);
                            new messageDialog(error, "failed", act);
                        }
                    });
                }
                else {
                    progress.setVisibility(View.VISIBLE);
                    finishServiceOrder(MainActivity.Q, String.valueOf(RR.id),"Cleanup", new VolleyCallback() {
                        @Override
                        public void onSuccess(String res) {
                            progress.setVisibility(View.INVISIBLE);
                            d.dismiss();
                            Toast.makeText(act, "Order Done", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailed(String error) {
                            progress.setVisibility(View.INVISIBLE);
                            new messageDialog(error, "failed", act);
                        }
                    });
                }
            }
            else if (orderType.equals("Laundry") || orderType.equals("SOS") || orderType.equals("MiniBar")) {
                progress.setVisibility(View.VISIBLE);
                finishServiceOrder(MainActivity.Q, String.valueOf(RR.id),orderType, new VolleyCallback() {
                    @Override
                    public void onSuccess(String res) {
                        progress.setVisibility(View.INVISIBLE);
                        d.dismiss();
                        Toast.makeText(act, "Order Done", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(String error) {
                        progress.setVisibility(View.INVISIBLE);
                        new messageDialog(error, "failed", act);
                    }
                });
            }
            else {
                progress.setVisibility(View.VISIBLE);
                finishServiceOrder(MainActivity.Q, String.valueOf(RR.id),"RoomService", new VolleyCallback() {
                    @Override
                    public void onSuccess(String res) {
                        progress.setVisibility(View.INVISIBLE);
                        d.dismiss();
                        Toast.makeText(act, "Order Done", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(String error) {
                        progress.setVisibility(View.INVISIBLE);
                        new messageDialog(error, "failed", act);
                    }
                });
            }
        });
        d.show();
    }

    void finishServiceOrder(RequestQueue Q, String room_id, String type, VolleyCallback callback) {
        String url = MyApp.MyProject.Url + "reservations/finishServiceOrder" ;
        StringRequest r = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("finishOrder",response);
            try {
                JSONObject result = new JSONObject(response);
                if (result.getString("result").equals("success")) {
                    callback.onSuccess("success");
                }
                else {
                    callback.onFailed(result.getString("error"));
                }
            } catch (JSONException e) {
                Log.d("finishOrder",e.getMessage());
                callback.onFailed(e.getMessage());
            }
        }, error -> {
            Log.d("finishOrder",error.toString());
            callback.onFailed(error.toString());
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("room_id",room_id);
                params.put("jobnumber", String.valueOf(MyApp.My_USER.jobNumber));
                params.put("order_type",type);
                params.put("my_token",MyApp.Token);
                return params;
            }
        };
        Q.add(r);
    }

    void prepareRoom(RequestQueue Q,String room_id,VolleyCallback callback) {
        String url = MyApp.MyProject.Url + "reservations/prepareRoom" ;
        StringRequest r = new StringRequest(Request.Method.POST, url, response -> {
            Log.d("finishOrder",response);
            try {
                JSONObject result = new JSONObject(response);
                if (result.getString("result").equals("success")) {
                    callback.onSuccess("success");
                }
                else {
                    callback.onFailed(result.getString("error"));
                }
            } catch (JSONException e) {
                Log.d("finishOrder",e.getMessage());
                callback.onFailed(e.getMessage());
            }
        }, error -> {
            Log.d("finishOrder",error.toString());
            callback.onFailed(error.toString());
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("room_id",room_id);
                params.put("job_number", String.valueOf(MyApp.My_USER.jobNumber));
                params.put("my_token",MyApp.Token);
                return params;
            }
        };
        Q.add(r);
    }
}
