package com.example.hotelservicesstandalone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class Rooms_Adapter_Base extends BaseAdapter
{
    List<ROOM> list ;
    LayoutInflater inflater ;
    Context c ;

    public Rooms_Adapter_Base(List<ROOM> list , Context c ) {
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

        TextView Room = convertView.findViewById(R.id.room_unit_roomNumber);
        ImageView lock = convertView.findViewById(R.id.room_unit_lock);
        ImageView power = convertView.findViewById(R.id.room_unit_power);
        ImageView gateway = convertView.findViewById(R.id.room_unit_gateway);
        ImageView ac = convertView.findViewById(R.id.room_unit_ac);

        Room.setText(String.valueOf(list.get(position).RoomNumber));
        if (list.get(position).lock == 1) {
            lock.setImageResource(R.drawable.lock);
        }
        if (list.get(position).Thermostat == 1 ) {
            ac.setImageResource(R.drawable.ac);
        }
        if(list.get(position).PowerSwitch == 1  ) {
            power.setImageResource(R.drawable.power);
        }
        if ( list.get(position).ZBGateway  == 1 ) {
            gateway.setImageResource(R.drawable.gateway);
        }

        return convertView;
    }
}
