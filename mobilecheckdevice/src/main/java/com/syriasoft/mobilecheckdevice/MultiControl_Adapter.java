package com.syriasoft.mobilecheckdevice;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecheckdevice.R;
import com.tuya.smart.android.device.api.ITuyaDeviceMultiControl;
import com.tuya.smart.android.device.bean.MultiControlBean;
import com.tuya.smart.android.device.bean.MultiControlLinkBean;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MultiControl_Adapter extends RecyclerView.Adapter<MultiControl_Adapter.HOLDER> {

    List<MultiControlLinkBean.MultiGroupBean> MultiControlsList;
    ITuyaDeviceMultiControl iTuyaDeviceMultiControl;

    MultiControl_Adapter(List<MultiControlLinkBean.MultiGroupBean> MultiControlsList) {
        this.MultiControlsList = MultiControlsList;
        iTuyaDeviceMultiControl = TuyaHomeSdk.getDeviceMultiControlInstance();
    }

    @NonNull
    @Override
    public MultiControl_Adapter.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.multi_control_unit,parent ,false);
        return new MultiControl_Adapter.HOLDER(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MultiControl_Adapter.HOLDER holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(MultiControlsList.get(position).getGroupName());
        if (MultiControlsList.get(position).isEnabled()) {
            holder.enabled.setImageResource(android.R.drawable.presence_online);
        }
        else {
            holder.enabled.setImageResource(android.R.drawable.presence_offline);
        }
        for (MultiControlLinkBean.MultiGroupBean.GroupDetailBean gd : MultiControlsList.get(position).getGroupDetail()) {
            TextView deviceName = new TextView(holder.itemView.getContext());
            deviceName.setGravity(Gravity.CENTER);
            deviceName.setText(gd.getDevName());
            deviceName.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white,null));
            TextView dp = new TextView(holder.itemView.getContext());
            dp.setGravity(Gravity.CENTER);
            dp.setText(String.valueOf(gd.getDpId()));
            dp.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.white,null));
            holder.details.addView(deviceName);
            holder.details.addView(dp);
        }
        holder.enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MultiControlsList.get(position).isEnabled()) {
                    iTuyaDeviceMultiControl.disableMultiControl(MultiControlsList.get(position).getId(), new ITuyaResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            new MessageDialog("","disabled",holder.itemView.getContext());
                            LightingDoubleControl.getDevicesMultiControl();
                        }

                        @Override
                        public void onError(String errorCode, String errorMessage) {
                            new MessageDialog(errorMessage+" "+errorCode,"error",holder.itemView.getContext());
                        }
                    });
                }
                else {
                    iTuyaDeviceMultiControl.enableMultiControl(MultiControlsList.get(position).getId(), new ITuyaResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            new MessageDialog("","enabled",holder.itemView.getContext());
                            LightingDoubleControl.getDevicesMultiControl();
                        }

                        @Override
                        public void onError(String errorCode, String errorMessage) {
                            new MessageDialog(errorMessage+" "+errorCode,"error",holder.itemView.getContext());
                        }
                    });
                }
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder b = new AlertDialog.Builder(holder.itemView.getContext());
                b.setTitle("Delete ?").setMessage("are you sure ??").setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        JSONArray arr = new JSONArray();
                        JSONObject multiControlBean = new JSONObject();
                        try {
                            multiControlBean.put("groupName",MultiControlsList.get(position).getGroupName());
                            multiControlBean.put("groupType", 1);
                            multiControlBean.put("groupDetail", arr);
                            multiControlBean.put("id", MultiControlsList.get(position).getId());
                        } catch (JSONException e) {
                            Toast.makeText(holder.itemView.getContext(),"failed "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                        iTuyaDeviceMultiControl.saveDeviceMultiControl(RoomManager.HOME.Home.getHomeId(),multiControlBean.toString() , new ITuyaResultCallback<MultiControlBean>() {
                            @Override
                            public void onSuccess(MultiControlBean result) {
                                LightingDoubleControl.getDevicesMultiControl();
                                dialogInterface.dismiss();
                                new MessageDialog("done" , "done",holder.itemView.getContext());
                            }

                            @Override
                            public void onError(String errorCode, String errorMessage) {
                                new MessageDialog(errorMessage+" "+errorCode , "error",holder.itemView.getContext());
                            }
                        });
                    }
                }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return MultiControlsList.size();
    }

    public static class HOLDER extends RecyclerView.ViewHolder {
        TextView name;
        ImageView enabled;
        LinearLayout details;
        Button enable;
        ImageButton delete;
        public HOLDER(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textView34);
            enabled = itemView.findViewById(R.id.imageView3);
            details = itemView.findViewById(R.id.detailes);
            enable = itemView.findViewById(R.id.button34);
            delete = itemView.findViewById(R.id.imageButton);
        }
    }
}
