package com.syriasoft.mobilecheckdevice.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecheckdevice.R;
import com.syriasoft.mobilecheckdevice.Activities.ProjectActivity;
import com.syriasoft.mobilecheckdevice.Classes.PROJECT;
import com.syriasoft.mobilecheckdevice.MyApp;

import java.util.List;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.Holder> {

    List<PROJECT> projects;

    public ProjectsAdapter(List<PROJECT> projects) {
        this.projects = projects;
    }
    @NonNull
    @Override
    public ProjectsAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_unit,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectsAdapter.Holder holder, int position) {
        PROJECT p = projects.get(position);
        holder.projectsName.setText(p.projectName);
        holder.hotelName.setText(p.TuyaUser);
        holder.itemView.setOnClickListener(view -> {
            MyApp.SelectedProject = p;
            Intent i = new Intent(holder.itemView.getContext(), ProjectActivity.class);
            holder.itemView.getContext().startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView projectsName,hotelName;
        public Holder(@NonNull View itemView) {
            super(itemView);
            projectsName = itemView.findViewById(R.id.textView73);
            hotelName = itemView.findViewById(R.id.textView77);
        }
    }
}
