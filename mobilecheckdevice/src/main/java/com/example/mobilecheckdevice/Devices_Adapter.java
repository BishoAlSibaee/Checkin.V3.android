package com.example.mobilecheckdevice;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.scene.dev.TaskListBean;
import com.tuya.smart.home.sdk.callback.ITuyaResultCallback;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.IResultCallback;
import com.tuya.smart.sdk.api.ITuyaDevice;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;

public class Devices_Adapter extends BaseAdapter {

    List<DeviceBean> list ;
    LayoutInflater inflater ;
    Context c ;

    Devices_Adapter(List<DeviceBean> list ,Context c ) {
        this.list = list ;
        this.c = c ;
        inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.device_unit , null);

        TextView name = convertView.findViewById(R.id.deviceUnit_deviceName);
        TextView order = convertView.findViewById(R.id.order);
        ImageView local = convertView.findViewById(R.id.deviceUnit_local);
        ImageView net = convertView.findViewById(R.id.deviceUnit_net);
        ImageView cloud = convertView.findViewById(R.id.deviceUnit_cloud);
        ITuyaDevice mDevice = TuyaHomeSdk.newDeviceInstance(list.get(position).devId);
        name.setText(list.get(position).getName());

        if (list.get(position).getIsOnline()) {
            net.setImageResource(android.R.drawable.presence_online);
        }
        else {
            net.setImageResource(android.R.drawable.ic_delete);
        }

        String STATUS = "" ;
        List kkk = new ArrayList(list.get(position).getDps().keySet());
        List vvv = new ArrayList(list.get(position).getDps().values());

        for (int i=0;i<kkk.size();i++) {
            STATUS = STATUS+ " ["+kkk.get(i)+" "+vvv.get(i)+"] " ;
        }

        mDevice.registerDevListener(new IDevListener() {
            @Override
            public void onDpUpdate(String devId, String dpStr) {
                order.setText(dpStr);
            }

            @Override
            public void onRemoved(String devId) {

            }

            @Override
            public void onStatusChanged(String devId, boolean online) {
                if (online) {
                    net.setImageResource(android.R.drawable.presence_online);
                }
                else {
                    net.setImageResource(android.R.drawable.ic_delete);
                }
            }

            @Override
            public void onNetworkStatusChanged(String devId, boolean status) {

            }

            @Override
            public void onDevInfoUpdate(String devId) {

            }
        });

        View finalConvertView = convertView;
        convertView.setOnLongClickListener(view -> {
            view.setBackgroundColor(Color.DKGRAY);
            Dialog d = new Dialog(finalConvertView.getContext());
            d.setContentView(R.layout.rename_device_dialog);
            Spinner s = d.findViewById(R.id.devicerenamespinner);
            Spinner rr = d.findViewById(R.id.roomsspinner);
            String[] Types = new String[]{"Power", "ZGatway", "AC", "DoorSensor", "MotionSensor", "Curtain", "ServiceSwitch", "Switch1", "Switch2", "Switch3","Switch4","Switch5","Switch6","Switch7","Switch8","IR","Lock"};
            String[] therooms = new String[Rooms.ROOMS.size()];
            for (int i = 0; i < Rooms.ROOMS.size(); i++) {
                therooms[i] = String.valueOf(Rooms.ROOMS.get(i).RoomNumber);
            }
            ArrayAdapter<String> a = new ArrayAdapter<>(finalConvertView.getContext(), R.layout.spinners_item, Types);
            ArrayAdapter<String> r = new ArrayAdapter<>(finalConvertView.getContext(), R.layout.spinners_item, therooms);
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
                        Toast.makeText(finalConvertView.getContext(), "Error. " + error, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess() {
                        Rooms.refreshSystem();
                        Toast.makeText(finalConvertView.getContext(), "Device Renamed .", Toast.LENGTH_LONG).show();
                        d.dismiss();
                    }
                });
            });
            delete.setOnClickListener(v -> {
                ITuyaDevice Device = TuyaHomeSdk.newDeviceInstance(list.get(position).getDevId());
                Device.removeDevice(new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        Toast.makeText(finalConvertView.getContext(), "Error. " + error, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess() {
                        try {
                            Rooms.refreshSystem();
                            Toast.makeText(finalConvertView.getContext(), "Device Deleted .", Toast.LENGTH_LONG).show();
                            d.dismiss();
                        }
                        catch(Exception e) {
                            new MessageDialog(e.getMessage(),e.getMessage(),finalConvertView.getContext());
                        }
                    }
                });
            });
            d.show();
            d.setOnDismissListener(dialog -> view.setBackgroundColor(Color.LTGRAY));
            return false;

        });
        convertView.setOnClickListener(v -> {
            Log.d("IR" , list.get(position).getCategoryCode());
            Log.d("SelectedDeviceInfo","name: "+list.get(position).getName()+" dps: "+list.get(position).getDps()+" category: "+list.get(position).getDeviceCategory()+" id "+list.get(position).devId);
            if (list.get(position).getIsOnline()) {
                Toast.makeText(finalConvertView.getContext(),"online "+list.get(position).devId,Toast.LENGTH_SHORT).show();
                net.setImageResource(android.R.drawable.presence_online);
                    Log.d("rgbDevice","found");
//                    TuyaHomeSdk.newDeviceInstance(list.get(position).devId).publishDps("{\"1\": true}", new IResultCallback() {
//                        @Override
//                        public void onError(String code, String error) {
//                            Log.d("rgbDevice",error+" 1");
//                        }
//
//                        @Override
//                        public void onSuccess() {
//                            Log.d("rgbDevice","success 1");
//                        }
//                    });
//                    TuyaHomeSdk.newDeviceInstance(list.get(position).devId).publishDps("{\"34\": true}", new IResultCallback() {
//                        @Override
//                        public void onError(String code, String error) {
//                            Log.d("rgbDevice",error+" 34");
//                        }
//
//                        @Override
//                        public void onSuccess() {
//                            Log.d("rgbDevice","success 34");
//                        }
//                    });
//                    TuyaHomeSdk.newDeviceInstance(list.get(position).devId).publishDps("{\"33\": \"ff5500\"}", new IResultCallback() {
//                        @Override
//                        public void onError(String code, String error) {
//                            Log.d("rgbDevice",error+" error 3 "+code);
//                        }
//
//                        @Override
//                        public void onSuccess() {
//                            Log.d("rgbDevice","success 3");
//                        }
//                    });
//                    TuyaHomeSdk.newDeviceInstance(list.get(position).devId).publishDps("{\"35\": \"ff5500\"}", new IResultCallback() {
//                        @Override
//                        public void onError(String code, String error) {
//                            Log.d("rgbDevice",error+" error 3 "+code);
//                        }
//
//                        @Override
//                        public void onSuccess() {
//                            Log.d("rgbDevice","success 3");
//                        }
//                    })
                    TuyaHomeSdk.getSceneManagerInstance().getDeviceConditionOperationList(list.get(position).devId, new ITuyaResultCallback<List<TaskListBean>>() {
                        @Override
                        public void onSuccess(List<TaskListBean> result) {
                            for (TaskListBean t:result) {
                                Log.d("rgbDevice",t.getDpId()+" "+t.getType()+" "+t.getSchemaBean().name+" "+t.getSchemaBean().property+" "+t.getSchemaBean().type+" "+t.getSchemaBean().schemaType+" "+t.getSchemaBean().code+" "+t.getSchemaBean().mode+" "+t.getSchemaBean().id);
                            }
                        }

                        @Override
                        public void onError(String errorCode, String errorMessage) {

                        }
                    });

                    TuyaHomeSdk.newDeviceInstance(list.get(position).devId).publishDps("{\"1\" :true}", new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {
                            Log.d("rgbDevice","1 "+error);
                        }

                        @Override
                        public void onSuccess() {
                            Log.d("rgbDevice","1 success");
                        }
                    });

                    TuyaHomeSdk.newDeviceInstance(list.get(position).devId).publishDps("{\"3\" :\"500\"}", new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {
                            Log.d("rgbDevice","3 "+error);
                        }

                        @Override
                        public void onSuccess() {
                            Log.d("rgbDevice","3 success");
                        }
                    });

                    TuyaHomeSdk.newDeviceInstance(list.get(position).devId).publishDps("{\"4\" :\"500\"}", new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {
                            Log.d("rgbDevice","4 "+error);
                        }

                        @Override
                        public void onSuccess() {
                            Log.d("rgbDevice","4 success");
                        }
                    });


            }
            else {
                Toast.makeText(finalConvertView.getContext(),"offline "+list.get(position).devId,Toast.LENGTH_SHORT).show();
                net.setImageResource(android.R.drawable.ic_delete);
            }
        });

        return convertView;

    }
}
