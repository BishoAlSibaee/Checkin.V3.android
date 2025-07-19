package com.syriasoft.projectseditor.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.syriasoft.projectseditor.Classes.PROJECT;
import com.syriasoft.projectseditor.R;

import java.text.MessageFormat;
import java.util.List;

public class projectsAdapter extends RecyclerView.Adapter<projectsAdapter.Holder> {

    List<PROJECT> projects;

    public projectsAdapter(List<PROJECT> projects) {
        this.projects = projects;
    }

    @NonNull
    @Override
    public projectsAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.projects_adapter,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull projectsAdapter.Holder holder, int position) {
        PROJECT p = projects.get(position);
        Log.d("7Problem",p.projectName+" "+p.myControlDevices.size());
        holder.name.setText(p.projectName);
        holder.devices.setText(MessageFormat.format("{0} devices", p.myControlDevices.size()));
        if (!p.myControlDevices.isEmpty()) {
            RecyclerView devicesRecycler = new RecyclerView(holder.itemView.getContext());
            devicesRecycler.setLayoutManager(new GridLayoutManager(holder.itemView.getContext(),4));
            devicesAdapter adapter = new devicesAdapter(p.myControlDevices);
            devicesRecycler.setAdapter(adapter);
            LinearLayout main = (LinearLayout) holder.itemView;
            main.addView(devicesRecycler);
        }

    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView name,devices;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textView2);
            devices = itemView.findViewById(R.id.textView3);
        }
    }
}
