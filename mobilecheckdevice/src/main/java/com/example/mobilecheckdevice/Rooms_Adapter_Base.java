package com.example.mobilecheckdevice;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class Rooms_Adapter_Base extends BaseAdapter
{
    List<ROOM> list;
    LayoutInflater inflater;
    Context c;

    public Rooms_Adapter_Base(List<ROOM> list , Context c )
    {
        this.list = list ;
        this.c = c ;
        inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.room_unit , null);

        if (list.get(position).isRoomUnInstalled()) {
            LinearLayout l = convertView.findViewById(R.id.insideLayout);
            l.setBackgroundResource(R.drawable.locks_background);
        }

        TextView Room = convertView.findViewById(R.id.room_unit_roomNumber);
        ImageView lock = convertView.findViewById(R.id.room_unit_lock);
        ImageView power = convertView.findViewById(R.id.room_unit_power);
        ImageView gateway = convertView.findViewById(R.id.room_unit_gateway);
        ImageView ac = convertView.findViewById(R.id.room_unit_ac);

        Room.setText(String.valueOf(list.get(position).RoomNumber));
        if (list.get(position).lock == 1) {
            lock.setImageResource(R.drawable.lock_exists);
        }
        if (list.get(position).Thermostat == 1 ) {
            ac.setImageResource(R.drawable.ac_exists);
        }
        if(list.get(position).PowerSwitch == 1  ) {
            power.setImageResource(R.drawable.power_exists);
        }
        if ( list.get(position).ZBGateway  == 1 ) {
            gateway.setImageResource(R.drawable.gateway_exists);
        }
        convertView.setOnClickListener(v -> {
            MyApp.SelectedRoom = list.get(position);
            if (list.get(position).isRoomUnInstalled()) {
                if (!Rooms.SelectedHome.Home.getName().contains("P0001")) {
                    if (Rooms.SelectedHome.Devices.size() >= 200) {
                        new MessageDialog("this home has 200 or more devices \n please select empty home or create new one", "Home is Full", c);
                        return;
                    }
                }
            }
            Intent i = new Intent(c , RoomManager.class);
            i.putExtra("RoomId" , list.get(position).id);
            c.startActivity(i);
        });

        return convertView;
    }
}
