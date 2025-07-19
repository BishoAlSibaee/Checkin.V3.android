package com.syriasoft.projectscontrol.Adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syriasoft.projectscontrol.MyApp;
import com.syriasoft.projectscontrol.PROJECT;
import com.syriasoft.projectscontrol.ProjectActivity;
import com.syriasoft.projectscontrol.R;

import java.util.List;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.Holder>{

    List<PROJECT> projects ;

    public ProjectsAdapter(List<PROJECT> projects) {
        this.projects = projects;
    }

    @NonNull
    @Override
    public ProjectsAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_unit,null);
        Holder h = new Holder(v);
        return h;
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectsAdapter.Holder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(projects.get(position).projectName);
        holder.itemView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.SelectedProject = projects.get(position);
                Intent i = new Intent(holder.itemView.getContext(), ProjectActivity.class);
                holder.itemView.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView name ;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textView8);
        }
    }
}
