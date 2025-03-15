package com.syriasoft.mobilecheckdevice;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecheckdevice.R;

import java.util.List;

public class Rooms_Adapter extends RecyclerView.Adapter<Rooms_Adapter.Holder>
{
    List<ROOM> list ;

    public Rooms_Adapter(List<ROOM> list) {
        this.list = list;
        Log.d("RoomsFromAdapter" , this.list.size()+"");
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_unit,parent ,false);
        return new Holder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        if (list.get(position).RoomNumber == 505) {
            Log.d("emptyRoom",list.get(position).RoomNumber+" "+list.get(position).PowerSwitch+" "+list.get(position).ZBGateway);
        }
        holder.Room.setText(String.valueOf(list.get(position).RoomNumber));

        if (list.get(position).lock == 1) {
            holder.lock.setImageResource(R.drawable.lock_exists);
        }
        else if (list.get(position).lock == 0) {
            holder.lock.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        }
        if (list.get(position).Thermostat == 1 ) {
            holder.ac.setImageResource(R.drawable.ac_exists);
        }
        else if (list.get(position).Thermostat == 0) {
            holder.lock.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        }
        if(list.get(position).PowerSwitch == 1) {
            holder.power.setImageResource(R.drawable.power_exists);
        }
        else if (list.get(position).PowerSwitch == 0) {
            holder.lock.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        }
        if ( list.get(position).ZBGateway  == 1) {
            holder.gateway.setImageResource(R.drawable.gateway_exists);
        }
        else if (list.get(position).ZBGateway  == 0) {
            holder.lock.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        }
        holder.itemView.setOnClickListener(v -> {
            MyApp.SelectedRoom = list.get(position);
            if (list.get(position).isRoomUnInstalled()) {
                if (!Rooms.SelectedHome.Home.getName().contains("P0001")) {
                    if (Rooms.SelectedHome.Devices.size() >= 200) {
                        new MessageDialog("this home has 200 or more devices \n please select empty home or create new one","Home is Full",holder.itemView.getContext());
                        return;
                    }
                }
            }
            Intent i = new Intent(holder.itemView.getContext() , RoomManager.class);
            holder.itemView.getContext().startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView Room ;
        ImageView lock , power , gateway , ac ;

        public Holder(@NonNull View itemView) {
            super(itemView);
            Room = itemView.findViewById(R.id.room_unit_roomNumber);
            lock = itemView.findViewById(R.id.room_unit_lock);
            power = itemView.findViewById(R.id.room_unit_power);
            gateway = itemView.findViewById(R.id.room_unit_gateway);
            ac = itemView.findViewById(R.id.room_unit_ac);
        }
    }
}
