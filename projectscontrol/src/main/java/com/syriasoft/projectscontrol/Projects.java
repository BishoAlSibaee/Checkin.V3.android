package com.syriasoft.projectscontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.syriasoft.projectscontrol.Adapters.ProjectsAdapter;

public class Projects extends AppCompatActivity {

    Activity act ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);
        setActivity();
    }

    void setActivity() {
        act = this ;
        RecyclerView projectsR = findViewById(R.id.projectsRecycler);
        LinearLayoutManager manager = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        projectsR.setLayoutManager(manager);
        ProjectsAdapter adapter = new ProjectsAdapter(MyApp.Projects);
        projectsR.setAdapter(adapter);
    }

    public void goToAddProject(View view) {
        Intent i = new Intent(act,AddNewProject.class);
        startActivity(i);
    }
}