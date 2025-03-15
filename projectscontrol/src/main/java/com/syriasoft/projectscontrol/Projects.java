package com.syriasoft.projectscontrol;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (act.checkSelfPermission(Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                act.requestPermissions(new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM},10);
            }
        }
        RecyclerView projectsR = findViewById(R.id.projectsRecycler);
        LinearLayoutManager manager = new LinearLayoutManager(act,RecyclerView.VERTICAL,false);
        projectsR.setLayoutManager(manager);
        ProjectsAdapter adapter = new ProjectsAdapter(MyApp.Projects);
        projectsR.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {

        }
    }

    public void goToAddProject(View view) {
        Intent i = new Intent(act,AddNewProject.class);
        startActivity(i);
    }
}