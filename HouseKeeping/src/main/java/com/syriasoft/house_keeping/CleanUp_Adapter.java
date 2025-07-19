package com.syriasoft.house_keeping;

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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

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

public class CleanUp_Adapter extends BaseAdapter {

    List<cleanOrder> list;
    LayoutInflater inflater;
    Context co;
    Activity act;

    CleanUp_Adapter(List<cleanOrder> list, Context c) {
        this.list = sortList(list);
        inflater = (LayoutInflater.from(c));
        this.co = c;
        act = (Activity) c;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.order_unit, null);
        TextView room = convertView.findViewById(R.id.cleanOrder_room);
        TextView dep = convertView.findViewById(R.id.cleanOrder_orderType);
        TextView date = convertView.findViewById(R.id.cleanOrder_orderDate);
        TextView vv = convertView.findViewById(R.id.textView3);
        ImageView img = convertView.findViewById(R.id.imageView2);
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(list.get(position).date);
        int month = c.get(Calendar.MONTH) + 1;
        date.setText(MessageFormat.format("{0}/{1}/{2} {3}:{4}", c.get(Calendar.DAY_OF_MONTH), month, c.get(Calendar.YEAR), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)));
        room.setText(list.get(position).roomNumber);
        ROOM RR = searchRoomByNumber(Integer.parseInt(list.get(position).roomNumber),MainActivity.Rooms);
        if (RR != null) {
            if (RR.roomStatus == 1) {
                room.setTextColor(Color.WHITE);
                room.setBackgroundColor(co.getColor(R.color.greenRoom));
                img.setBackgroundColor(co.getColor(R.color.greenRoom));
            }
            else if (RR.roomStatus == 2) {
                room.setTextColor(Color.WHITE);
                room.setBackgroundColor(co.getColor(R.color.redRoom));
                room.setBackgroundResource(R.color.redRoom);
                vv.setBackgroundColor(co.getColor(R.color.redRoom));
                img.setBackgroundColor(co.getColor(R.color.redRoom));
            }
            else if (RR.roomStatus == 3) {
                room.setTextColor(Color.WHITE);
                room.setBackgroundColor(co.getColor(R.color.blueRoom));
                vv.setBackgroundColor(co.getColor(R.color.blueRoom));
                img.setBackgroundColor(co.getColor(R.color.blueRoom));
            }
            else if (RR.roomStatus == 4) {
                room.setTextColor(Color.WHITE);
                room.setBackgroundColor(Color.GRAY);
                vv.setBackgroundColor(Color.GRAY);
                img.setBackgroundColor(Color.GRAY);
            }
            RR.getFireRoom().child("SuiteStatus").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        if (dataSnapshot.getValue().toString().equals("2")) {
                            vv.setVisibility(View.VISIBLE);
                            vv.setText("S");
                            vv.setTextColor(Color.WHITE);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        switch (list.get(position).dep) {
            case "Cleanup":
                img.setImageResource(R.drawable.cleanup_btn);
                dep.setText(list.get(position).dep);
                convertView.setOnLongClickListener(v -> {
                    dep.setBackgroundColor(co.getColor(R.color.transparentGray));
                    date.setBackgroundColor(co.getColor(R.color.transparentGray));
                    final Dialog d = new Dialog(co);
                    d.setContentView(R.layout.confermation_dialog);
                    d.setCancelable(false);
                    Window w = d.getWindow();
                    w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    AVLoadingIndicatorView progress = d.findViewById(R.id.progressBar5);
                    TextView message = d.findViewById(R.id.confermationDialog_Text);
                    message.setText(MyApp.getResourceString(R.string.confermationDialog_text));
                    Button cancel = d.findViewById(R.id.confermationDialog_cancel);
                    cancel.setOnClickListener(v110 -> {
                        d.dismiss();
                        dep.setBackgroundColor(co.getColor(R.color.white));
                        date.setBackgroundColor(co.getColor(R.color.white));
                    });
                    Button ok = d.findViewById(R.id.messageDialog_ok);
                    ok.setOnClickListener(v17 -> {
                        if (RR != null) {
                            if (RR.roomStatus == 3) {
                                progress.setVisibility(View.VISIBLE);
                                prepareRoom(MainActivity.Q, String.valueOf(RR.id), new VolleyCallback() {
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
                                finishServiceOrder(MainActivity.Q, String.valueOf(RR.id), "Cleanup", new VolleyCallback() {
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
                    });
                    d.show();
                    return false;
                });
                break;
            case "Laundry":
                img.setImageResource(R.drawable.laundry_btn);
                dep.setText(list.get(position).dep);
                convertView.setOnLongClickListener(v -> {
                    dep.setBackgroundColor(co.getColor(R.color.transparentGray));
                    date.setBackgroundColor(co.getColor(R.color.transparentGray));
                    final Dialog d = new Dialog(co);
                    d.setContentView(R.layout.confermation_dialog);
                    d.setCancelable(false);
                    Window w = d.getWindow();
                    w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    AVLoadingIndicatorView progress = d.findViewById(R.id.progressBar5);
                    TextView message = d.findViewById(R.id.confermationDialog_Text);
                    message.setText(MyApp.getResourceString(R.string.confermationDialog_text));
                    Button cancel = d.findViewById(R.id.confermationDialog_cancel);
                    cancel.setOnClickListener(v1 -> {
                        d.dismiss();
                        dep.setBackgroundColor(co.getColor(R.color.white));
                        date.setBackgroundColor(co.getColor(R.color.white));
                    });
                    Button ok = d.findViewById(R.id.messageDialog_ok);
                    ok.setOnClickListener(v12 -> {
                        if (RR != null) {
                            progress.setVisibility(View.VISIBLE);
                            finishServiceOrder(MainActivity.Q, String.valueOf(RR.id), "Laundry", new VolleyCallback() {
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
                    return false;
                });
                break;
            case "RoomService":
                img.setImageResource(R.drawable.roomservice);
                dep.setText(list.get(position).roomServiceText);
                convertView.setOnLongClickListener(v -> {
                    dep.setBackgroundColor(co.getColor(R.color.transparentGray));
                    date.setBackgroundColor(co.getColor(R.color.transparentGray));
                    final Dialog d = new Dialog(co);
                    d.setContentView(R.layout.confermation_dialog);
                    d.setCancelable(false);
                    Window w = d.getWindow();
                    w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    AVLoadingIndicatorView progress = d.findViewById(R.id.progressBar5);
                    TextView message = d.findViewById(R.id.confermationDialog_Text);
                    message.setText(MyApp.getResourceString(R.string.confermationDialog_text));
                    Button cancel = d.findViewById(R.id.confermationDialog_cancel);
                    cancel.setOnClickListener(v13 -> {
                        d.dismiss();
                        dep.setBackgroundColor(co.getColor(R.color.white));
                        date.setBackgroundColor(co.getColor(R.color.white));
                    });
                    Button ok = d.findViewById(R.id.messageDialog_ok);
                    ok.setOnClickListener(v14 -> {
                        if (RR != null) {
                            progress.setVisibility(View.VISIBLE);
                            finishServiceOrder(MainActivity.Q, String.valueOf(RR.id), "RoomService", new VolleyCallback() {
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
                    return false;
                });
                break;
            case "SOS":
                img.setImageResource(R.drawable.sos_btn);
                dep.setText(list.get(position).dep);
                convertView.setOnLongClickListener(v -> {
                    dep.setBackgroundColor(co.getColor(R.color.transparentGray));
                    date.setBackgroundColor(co.getColor(R.color.transparentGray));
                    final Dialog d = new Dialog(co);
                    d.setContentView(R.layout.confermation_dialog);
                    d.setCancelable(false);
                    Window w = d.getWindow();
                    w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    AVLoadingIndicatorView progress = d.findViewById(R.id.progressBar5);
                    TextView message = d.findViewById(R.id.confermationDialog_Text);
                    message.setText(MyApp.getResourceString(R.string.confermationDialog_text));
                    Button cancel = d.findViewById(R.id.confermationDialog_cancel);
                    cancel.setOnClickListener(v15 -> {
                        d.dismiss();
                        dep.setBackgroundColor(co.getColor(R.color.white));
                        date.setBackgroundColor(co.getColor(R.color.white));
                    });
                    Button ok = d.findViewById(R.id.messageDialog_ok);
                    ok.setOnClickListener(v19 -> {
                        if (RR != null) {
                            progress.setVisibility(View.VISIBLE);
                            finishServiceOrder(MainActivity.Q, String.valueOf(RR.id), "SOS", new VolleyCallback() {
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
                    return false;
                });
                break;
            case "MiniBarCheck":
                img.setImageResource(R.drawable.minibar);
                img.setPadding(10, 10, 10, 10);
                dep.setText(list.get(position).dep);
                convertView.setOnLongClickListener(v -> {
                    dep.setBackgroundColor(co.getColor(R.color.transparentGray));
                    date.setBackgroundColor(co.getColor(R.color.transparentGray));
                    final Dialog d = new Dialog(co);
                    d.setContentView(R.layout.confermation_dialog);
                    d.setCancelable(false);
                    Window w = d.getWindow();
                    w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    AVLoadingIndicatorView progress = d.findViewById(R.id.progressBar5);
                    TextView message = d.findViewById(R.id.confermationDialog_Text);
                    message.setText(MyApp.getResourceString(R.string.confermationDialog_text));
                    Button cancel = d.findViewById(R.id.confermationDialog_cancel);
                    cancel.setOnClickListener(v18 -> {
                        d.dismiss();
                        dep.setBackgroundColor(co.getColor(R.color.white));
                        date.setBackgroundColor(co.getColor(R.color.white));
                    });
                    Button ok = d.findViewById(R.id.messageDialog_ok);
                    ok.setOnClickListener(v16 -> {
                        if (RR != null) {
                            progress.setVisibility(View.VISIBLE);
                            finishServiceOrder(MainActivity.Q, String.valueOf(RR.id), "MiniBarCheck", new VolleyCallback() {
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
                    return false;
                });
                break;
            default:
                img.setVisibility(View.GONE);
                break;
        }
        return convertView;
    }

    ROOM searchRoomByNumber(int roomNumber, List<ROOM> rooms) {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).RoomNumber == roomNumber) {
                return rooms.get(i);
            }
        }
        return null;
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

    void finishServiceOrder(RequestQueue Q,String room_id,String type,VolleyCallback callback) {
        String url = MyApp.MyProject.url + "reservations/finishServiceOrder" ;
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
                params.put("jobnumber", String.valueOf(MyApp.My_USER.id));
                params.put("order_type",type);
                params.put("my_token",MyApp.Token);
                return params;
            }
        };
        Q.add(r);
    }

    void prepareRoom(RequestQueue Q,String room_id,VolleyCallback callback) {
        String url = MyApp.MyProject.url + "reservations/prepareRoom" ;
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