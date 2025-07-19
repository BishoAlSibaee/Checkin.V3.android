package com.syriasoft.server.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.syriasoft.server.Classes.Property.Room;
import com.example.hotelservicesstandalone.R;

import java.util.List;

public class Rooms_Adapter_Base extends BaseAdapter
{
    List<Room> list ;
    LayoutInflater inflater ;
    Context c ;

    public Rooms_Adapter_Base(List<Room> list , Context c ) {
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
        Room room = list.get(position);
        convertView = inflater.inflate(R.layout.room_unit , null);

        TextView Room = convertView.findViewById(R.id.room_unit_roomNumber);
        ImageView lock = convertView.findViewById(R.id.room_unit_lock);
        ImageView power = convertView.findViewById(R.id.room_unit_power);
        ImageView gateway = convertView.findViewById(R.id.room_unit_gateway);
        ImageView ac = convertView.findViewById(R.id.room_unit_ac);

        Room.setText(String.valueOf(room.RoomNumber));
        if (room.isHasLock()) {
            lock.setImageResource(R.drawable.lock);
        }
        if (room.isHasAC()) {
            ac.setImageResource(R.drawable.ac);
        }
        if (room.isHasPower()) {
            power.setImageResource(R.drawable.power_);
        }
        if (room.isHasGateway()) {
            gateway.setImageResource(R.drawable.gateway);
        }

        return convertView;
    }
}
