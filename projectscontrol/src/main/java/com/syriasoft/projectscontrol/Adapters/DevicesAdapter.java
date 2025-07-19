package com.syriasoft.projectscontrol.Adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syriasoft.projectscontrol.Device;
import com.syriasoft.projectscontrol.MainActivity;
import com.syriasoft.projectscontrol.R;
import com.syriasoft.projectscontrol.ServerDevice;

import java.util.List;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.Holder> {

    List<ServerDevice> list;

    public DevicesAdapter(List<ServerDevice> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public DevicesAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.server_devices_unit,null);
        Holder h = new Holder(v);
        return h;
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesAdapter.Holder holder, @SuppressLint("RecyclerView") int position) {
        holder.project.setText(list.get(position).ProjectName);
        holder.deviceName.setText(list.get(position).name);
        holder.roomsIds.setText(list.get(position).roomsIds);
        holder.Status.setText(String.valueOf(list.get(position).status));
        holder.itemView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        if (list.get(position).working) {
            holder.working.setImageResource(android.R.drawable.presence_online);
        }
        else {
            holder.working.setImageResource(android.R.drawable.presence_invisible);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.SelectedProject = list.get(position).Project;
                Intent i = new Intent(holder.itemView.getContext(), Device.class);
                i.putExtra("id",position);
                holder.itemView.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        public TextView project , roomsIds , deviceName , Status ;
        public ImageView working ;
        public Holder(@NonNull View itemView) {
            super(itemView);
            project = itemView.findViewById(R.id.project);
            deviceName = itemView.findViewById(R.id.deviceName);
            roomsIds = itemView.findViewById(R.id.roomsIds);
            Status = itemView.findViewById(R.id.deviceStatus);
            working = itemView.findViewById(R.id.imageView);
        }
    }
}
