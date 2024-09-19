package com.example.hotelservicesstandalone.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hotelservicesstandalone.Classes.Devices.CheckinDevice;
import com.example.hotelservicesstandalone.R;

import java.util.List;

public class Devices_Adapter extends BaseAdapter {

    List<CheckinDevice> list;
    LayoutInflater inflater ;
    Context c ;

    public Devices_Adapter(List<CheckinDevice> list, Context c) {
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
        ImageView net = convertView.findViewById(R.id.deviceUnit_net);
        name.setText(d.getName());

        if (d.getIsOnline()) {
            net.setImageResource(android.R.drawable.presence_online);
        }
        else {
            net.setImageResource(android.R.drawable.ic_delete);
        }

//        mDevice.registerDevListener(new IDevListener() {
//            @Override
//            public void onDpUpdate(String devId, String dpStr) {
//                order.setText(dpStr);
//            }
//
//            @Override
//            public void onRemoved(String devId) {
//
//            }
//
//            @Override
//            public void onStatusChanged(String devId, boolean online) {
//                if (online) {
//                    net.setImageResource(android.R.drawable.presence_online);
//                }
//                else {
//                    net.setImageResource(android.R.drawable.ic_delete);
//                }
//            }
//
//            @Override
//            public void onNetworkStatusChanged(String devId, boolean status) {
//
//            }
//
//            @Override
//            public void onDevInfoUpdate(String devId) {
//
//            }
//        });

        return convertView;

    }
}
