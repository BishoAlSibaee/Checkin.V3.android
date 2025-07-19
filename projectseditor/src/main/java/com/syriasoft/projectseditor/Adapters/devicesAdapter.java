package com.syriasoft.projectseditor.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syriasoft.projectseditor.Classes.ControlDevice;
import com.syriasoft.projectseditor.R;

import java.util.Calendar;
import java.util.List;

public class devicesAdapter extends RecyclerView.Adapter<devicesAdapter.Holder> {

    List<ControlDevice> devices;

    public devicesAdapter(List<ControlDevice> devices) {
        this.devices = devices;
    }

    @NonNull
    @Override
    public devicesAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.devices_unit,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull devicesAdapter.Holder holder, int position) {
        ControlDevice d = devices.get(position);
        holder.project.setText(d.myProject.projectName);
        holder.name.setText(d.name);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(d.lastWorking);
        String dateTime = c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DAY_OF_MONTH)+" "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND);
        holder.lastWorking.setText(dateTime);
        if (d.isWorking) {
            holder.status.setImageResource(android.R.drawable.presence_online);
        }
        else {
            holder.status.setImageResource(android.R.drawable.presence_busy);
        }
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView project,name,lastWorking;
        ImageView status;
        public Holder(@NonNull View itemView) {
            super(itemView);
            project = itemView.findViewById(R.id.textView6);
            name = itemView.findViewById(R.id.textView4);
            lastWorking = itemView.findViewById(R.id.textView5);
            status = itemView.findViewById(R.id.imageView);
        }
    }
}
