package com.example.mobilecheckdevice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.List;

public class Device_Adapter extends RecyclerView.Adapter<Device_Adapter.Holder> {

    List<DeviceBean> list;

    Device_Adapter(List<DeviceBean> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Device_Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_unit,parent ,false);
        return new Holder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull Device_Adapter.Holder holder, @SuppressLint("RecyclerView") int position) {
        DeviceBean device = list.get(position);
        holder.name.setText(list.get(position).getName());
        if (list.get(position).getIsOnline()) {
            holder.online.setImageResource(android.R.drawable.presence_online);
        }
        else {
            holder.online.setImageResource(android.R.drawable.presence_busy);
        }
        TuyaHomeSdk.newDeviceInstance(list.get(position).devId).registerDevListener(new IDevListener() {
            @Override
            public void onDpUpdate(String devId, String dpStr) {
                holder.order.setText(dpStr);
            }

            @Override
            public void onRemoved(String devId) {

            }

            @Override
            public void onStatusChanged(String devId, boolean online) {
                if (online) {
                    holder.online.setImageResource(android.R.drawable.presence_online);
                }
                else {
                    holder.online.setImageResource(android.R.drawable.presence_busy);
                }
            }

            @Override
            public void onNetworkStatusChanged(String devId, boolean status) {

            }

            @Override
            public void onDevInfoUpdate(String devId) {

            }
        });
        holder.itemView.setOnLongClickListener(view -> {
            String DeviceName = list.get(position).name;
            view.setBackgroundColor(Color.LTGRAY);
            Dialog d = new Dialog(holder.itemView.getContext());
            d.setContentView(R.layout.rename_device_dialog);
            Spinner s = d.findViewById(R.id.devicerenamespinner);
            Spinner rr = d.findViewById(R.id.roomsspinner);
            String[] Types = new String[]{"Power", "ZGatway", "AC", "DoorSensor", "MotionSensor", "Curtain", "ServiceSwitch", "Switch1", "Switch2", "Switch3","Switch4","Switch5","Switch6","Switch7","Switch8","Shutter1","Shutter2","Shutter3","IR","Lock"};
            String[] the_rooms = new String[Rooms.ROOMS.size()];
            ROOM Room = null ;
            for (int i = 0; i < Rooms.ROOMS.size(); i++) {
                String room = getRoomNumberFromDeviceName(list.get(position));
                if (room != null && !room.isEmpty()) {
                    try{
                        if (Integer.parseInt(room) == Rooms.ROOMS.get(i).RoomNumber) {
                            Room = Rooms.ROOMS.get(i);
                        }
                    }catch(Exception e){
                        Log.d("roomSearchFailed",e.getMessage());
                    }
                }
                the_rooms[i] = String.valueOf(Rooms.ROOMS.get(i).RoomNumber);
            }
            ArrayAdapter<String> a = new ArrayAdapter<>(holder.itemView.getContext(), R.layout.spinners_item, Types);
            ArrayAdapter<String> r = new ArrayAdapter<>(holder.itemView.getContext(), R.layout.spinners_item, the_rooms);
            s.setAdapter(a);
            rr.setAdapter(r);
            TextView title = d.findViewById(R.id.RenameDialog_title);
            title.setText(String.format("Modify %s Device %s", list.get(position).getName(), list.get(position).getIsOnline().toString()));
            Button cancel = d.findViewById(R.id.cancel_diallog);
            Button rename = d.findViewById(R.id.DoTheRename);
            Button delete = d.findViewById(R.id.deleteDevice);
            cancel.setOnClickListener(v -> d.dismiss());
            rename.setOnClickListener(v -> {
                ITuyaDevice Device = TuyaHomeSdk.newDeviceInstance(list.get(position).getDevId());
                Device.renameDevice(rr.getSelectedItem().toString() + s.getSelectedItem().toString(), new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        Toast.makeText(holder.itemView.getContext(), "Error. " + error, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess() {
                        if (holder.itemView.getContext().getClass().getName().equals("com.example.mobilecheckdevice.RoomManager")) {
                            RoomManager.resetRoomDevices((Activity) holder.itemView.getContext());
                        }
                        list.get(position).setName(rr.getSelectedItem().toString() + s.getSelectedItem().toString());
                        Rooms.refreshSystem();
                        Toast.makeText(holder.itemView.getContext(), "Device Renamed .", Toast.LENGTH_LONG).show();
                        d.dismiss();
                    }
                });
            });
            ROOM finalRoom = Room;
            delete.setOnClickListener(v -> {
                ITuyaDevice Device = TuyaHomeSdk.newDeviceInstance(list.get(position).getDevId());
                Device.removeDevice(new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        Toast.makeText(holder.itemView.getContext(), "Error. " + error, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess() {
                        try {
                            if (finalRoom != null) {
                                finalRoom.setRoomDeviceByName(DeviceName,null);
                            }
                            if (holder.itemView.getContext().getClass().getName().equals("com.example.mobilecheckdevice.RoomManager")) {
                                RoomManager.RoomDevices.remove(position);
                                RoomManager.resetRoomDevices((Activity) holder.itemView.getContext());
                            }
                            Rooms.refreshSystem();
                            Toast.makeText(holder.itemView.getContext(), "Device Deleted .", Toast.LENGTH_LONG).show();
                            d.dismiss();
                        }
                        catch(Exception e) {
                            new MessageDialog(e.getMessage(),e.getMessage(),holder.itemView.getContext());
                        }
                    }
                });
            });
            d.show();
            d.setOnDismissListener(dialog -> view.setBackgroundResource(R.drawable.button_trans));
            return false;
        });
        holder.itemView.setOnClickListener(view->{
            Log.d("SelectedDeviceInfo","name: "+list.get(position).getName()+" catCode: "+list.get(position).getCategoryCode()+" category: "+list.get(position).getDeviceCategory()+" id "+list.get(position).devId);
            if (list.get(position).getIsOnline()) {
                holder.online.setImageResource(android.R.drawable.presence_online);
                if (list.get(position).getCategoryCode().contains("zig_kg") || list.get(position).getCategoryCode().contains("zig_pc") || list.get(position).getCategoryCode().contains("zig_tdq")) {
                    new SwitchControlDialog(holder.itemView.getContext(),list.get(position)).show();
                    if (device.dps.get("27") != null) {
                        TuyaHomeSdk.newDeviceInstance(device.devId).publishDps("{\"27\": \"memory\"}", new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {
                                Log.d("powerMemory",error);
                            }

                            @Override
                            public void onSuccess() {
                                Log.d("powerMemory","done");
                            }
                        });
                    }
                }
                else if (list.get(position).getCategoryCode().contains("hotelms_4z")) {
                    new LockControlDialog(holder.itemView.getContext(),list.get(position)).show();
                }
                else if (list.get(position).getCategoryCode().contains("zig_wxkg")) {
                    Log.d("batterySwitch","yes");
                    TuyaHomeSdk.getSceneManagerInstance().getDeviceConditionOperationList(list.get(position).devId, new ITuyaResultCallback<List<TaskListBean>>() {
                        @Override
                        public void onSuccess(List<TaskListBean> result) {
                            for (int i=0 ; i<result.size();i++) {
                                Log.d("batterySwitch",result.get(i).getName()+" "+result.get(i).getDpId()+" "+result.get(i).getSchemaBean().name+" "+result.get(i).getSchemaBean().mode+" "+result.get(i).getSchemaBean().schemaType+" "+result.get(i).getSchemaBean().property);
                            }
                        }

                        @Override
                        public void onError(String errorCode, String errorMessage) {

                        }
                    });
                }
                else if (list.get(position).getCategoryCode().contains("zig_cl")) {
                    CurtainController controller = new CurtainController();
                    TuyaHomeSdk.getSceneManagerInstance().getDeviceConditionOperationList(list.get(position).devId, new ITuyaResultCallback<List<TaskListBean>>() {
                        @Override
                        public void onSuccess(List<TaskListBean> result) {
                            for (int i=0 ; i<result.size();i++) {
                                Log.d("curtainS","name: "+result.get(i).getName()+" , dp: "+result.get(i).getDpId()+" , type: "+result.get(i).getSchemaBean().type+" , property: "+result.get(i).getSchemaBean().property+" , max: "+result.get(i).getValueSchemaBean().max+" , min: "+result.get(i).getValueSchemaBean().min+" , step: "+result.get(i).getValueSchemaBean().step+" , unit: "+result.get(i).getValueSchemaBean().unit);
                                if (result.get(i).getName().contains("Device control") || result.get(i).getName().contains("Device Control") || result.get(i).getName().contains("Control")) {
                                    controller.controlDp = (int) result.get(i).getDpId();
                                    Object[] k = result.get(i).getTasks().keySet().toArray();
                                    for (Object o : k) {
                                        Log.d("curtainS", "keys: "+ o.toString());
                                        if (o.toString().contains("open") || o.toString().contains("Open") || o.toString().contains("OPEN")) {
                                            controller.open = o.toString();
                                        }
                                        else if (o.toString().contains("close") || o.toString().contains("Close") || o.toString().contains("CLOSE")) {
                                            controller.close = o.toString();
                                        }
                                        else if (o.toString().contains("stop") || o.toString().contains("Stop") || o.toString().contains("STOP")) {
                                            controller.stop = o.toString();
                                        }
                                        else if (o.toString().contains("continue") || o.toString().contains("Continue") || o.toString().contains("CONTINUE")) {
                                            controller.continue_ = o.toString();
                                        }
                                    }
                                }
                                else if (result.get(i).getName().contains("Percentage control") || result.get(i).getName().contains("Percentage Control")) {
                                    controller.percentageDp = (int) result.get(i).getDpId();
                                    controller.percentageMax = result.get(i).getValueSchemaBean().max;
                                    controller.percentageMin = result.get(i).getValueSchemaBean().min;
                                    controller.percentageStep = result.get(i).getValueSchemaBean().step;
                                    controller.percentageUnit = result.get(i).getValueSchemaBean().unit;
                                }
                            }
                            new CurtainControlDialog(holder.itemView.getContext(),list.get(position),controller).show();
                        }

                        @Override
                        public void onError(String errorCode, String errorMessage) {

                        }
                    });
                }
                else if (list.get(position).getCategoryCode().contains("zig_dj")) {
                    RGBController controller = new RGBController();
                    TuyaHomeSdk.getSceneManagerInstance().getDeviceConditionOperationList(list.get(position).devId, new ITuyaResultCallback<List<TaskListBean>>() {
                        @Override
                        public void onSuccess(List<TaskListBean> result) {
                            for (int i=0;i<result.size();i++) {
                                Log.d("RGBAdapter","name: "+result.get(i).getName()+" , dp: "+result.get(i).getDpId()+" , type: "+result.get(i).getSchemaBean().type+" , property: "+result.get(i).getSchemaBean().property+" , max: "+result.get(i).getValueSchemaBean().max+" , min: "+result.get(i).getValueSchemaBean().min+" , step: "+result.get(i).getValueSchemaBean().step+" , unit: "+result.get(i).getValueSchemaBean().unit);
                                if (result.get(i).getName().contains("ON/OFF")) {
                                    controller.powerDp = (int) result.get(i).getDpId();
                                }
                                else if (result.get(i).getName().contains("Brightness")) {
                                    controller.brightnessDp = (int) result.get(i).getDpId();
                                    controller.brightMax = result.get(i).getValueSchemaBean().max;
                                    controller.brightMin = result.get(i).getValueSchemaBean().min;
                                    controller.brightStep = result.get(i).getValueSchemaBean().step;
                                    controller.brightUnit = result.get(i).getValueSchemaBean().unit;
                                }
                                else if (result.get(i).getName().contains("Color")) {
                                    controller.colorDp = (int) result.get(i).getDpId();
                                    controller.colorMax = result.get(i).getValueSchemaBean().max;
                                    controller.colorMin = result.get(i).getValueSchemaBean().min;
                                    controller.colorStep = result.get(i).getValueSchemaBean().step;
                                    controller.colorUnit = result.get(i).getValueSchemaBean().unit;
                                }
                                Object[] keys = result.get(i).getTasks().keySet().toArray();
                                Object[] values = result.get(i).getTasks().values().toArray();
                                for (int j=0;j<keys.length;j++) {
                                    Log.d("RGBAdapter","key: "+keys[j]+" , values: "+values[j]);
                                }
                            }
                            new RGBControlDialog(holder.itemView.getContext(),list.get(position),controller).show();
                        }

                        @Override
                        public void onError(String errorCode, String errorMessage) {

                        }
                    });
                }
            }
            else {
                Toast.makeText(holder.itemView.getContext(),"offline",Toast.LENGTH_SHORT).show();
                holder.online.setImageResource(android.R.drawable.presence_busy);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView name,order;
        ImageView online;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.deviceUnit_deviceName);
            order = itemView.findViewById(R.id.order);
            online = itemView.findViewById(R.id.deviceUnit_net);
        }
    }

    static String getRoomNumberFromDeviceName(DeviceBean d) {
        String n = "";
        if (d.getName().contains("Z")) {
            n = d.getName().split("Z")[0];
        }
        else if (d.getName().contains("P")) {
            n = d.getName().split("P")[0];
        }
        else if (d.getName().contains("M")) {
            n = d.getName().split("M")[0];
        }
        else if (d.getName().contains("D")) {
            n = d.getName().split("D")[0];
        }
        else if (d.getName().contains("S")) {
            n = d.getName().split("S")[0];
        }
        else if (d.getName().contains("A")) {
            n = d.getName().split("A")[0];
        }
        else if (d.getName().contains("C")) {
            n = d.getName().split("C")[0];
        }
        else if (d.getName().contains("L")) {
            n = d.getName().split("L")[0];
        }
        return n;
    }
}
