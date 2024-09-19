package com.example.hotelservicesstandalone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hotelservicesstandalone.Classes.Devices.CheckinDevice;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IDevListener;
import com.tuya.smart.sdk.api.ITuyaDevice;

import java.util.List;

public class Devices_Adapter extends BaseAdapter {

    List<CheckinDevice> list;
    LayoutInflater inflater ;
    Context c ;

    Devices_Adapter(List<CheckinDevice> list ,Context c ) {
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
        CheckinDevice d = list.get(position);
        convertView = inflater.inflate(R.layout.device_unit , null);

        TextView name = convertView.findViewById(R.id.deviceUnit_deviceName);
        TextView order = convertView.findViewById(R.id.order);
        ImageView net = convertView.findViewById(R.id.deviceUnit_net);
        ITuyaDevice mDevice = TuyaHomeSdk.newDeviceInstance(d.devId);
        name.setText(d.getName());

        if (d.getIsOnline()) {
            net.setImageResource(android.R.drawable.presence_online);
        }
        else {
            net.setImageResource(android.R.drawable.ic_delete);
        }

//        String STATUS = "" ;
//        List kkk = null;
//        List vvv = null;
//        if (d.getDps() != null) {
//            kkk = new ArrayList(d.getDps().keySet());
//            vvv = new ArrayList(d.getDps().values());
//        }
//
//        for (int i=0;i<kkk.size();i++) {
//            STATUS = MessageFormat.format("{0} [{1} {2}] ", STATUS, kkk.get(i), vvv.get(i));
//        }

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

        return convertView;

    }
}
