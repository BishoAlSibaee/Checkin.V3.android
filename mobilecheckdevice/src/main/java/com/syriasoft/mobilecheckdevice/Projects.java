package com.syriasoft.mobilecheckdevice;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.mobilecheckdevice.R;
import com.syriasoft.mobilecheckdevice.Adapters.ProjectsAdapter;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.GetProjectsCallback;
import com.syriasoft.mobilecheckdevice.Classes.PROJECT;

import java.util.List;

public class Projects extends AppCompatActivity {

    Activity act;
    RecyclerView projectsRecycler;
    List<PROJECT> projectsList;
    RequestQueue Q;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);
        setActivity();
        getProjects();
    }

    void setActivity() {
        act = this;
        projectsRecycler = findViewById(R.id.projects);
        LinearLayoutManager manager = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        projectsRecycler.setLayoutManager(manager);
        Q = Volley.newRequestQueue(act);
    }

    void getProjects() {
        PROJECT.getProjects(Q, new GetProjectsCallback() {
            @Override
            public void onSuccess(List<PROJECT> projects) {
                projectsList = projects;
                ProjectsAdapter adapter = new ProjectsAdapter(projectsList);
                projectsRecycler.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                new MessageDialog("getting projects failed \n"+error,"error",act);
            }
        });
    }
}