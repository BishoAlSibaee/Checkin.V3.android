package com.syriasoft.mobilecheckdevice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecheckdevice.R;

public class ViewTemplate extends AppCompatActivity {

    Activity act;
    int Index;
    RecyclerView MultiRecycler,MoodSRecycler,RoomsRecycler;
    public static Template template;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_template);
        Index = getIntent().getExtras().getInt("index");
        template = ProjectTemplates.Templates.get(Index);
        setActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshMultiControls(act);
        refreshMoods(act);
    }

    void setActivity() {
        act = this;
        MultiRecycler = findViewById(R.id.multiRecycler);
        MoodSRecycler = findViewById(R.id.moodRecycler);
        RoomsRecycler = findViewById(R.id.roomsRecycler);
        TextView templateName = findViewById(R.id.textView44);
        templateName.setText(template.name);
        GridLayoutManager moodManager = new GridLayoutManager(act,6);
        GridLayoutManager multiManager = new GridLayoutManager(act,6);
        GridLayoutManager roomsManager = new GridLayoutManager(act,6);
        MoodSRecycler.setLayoutManager(moodManager);
        MultiRecycler.setLayoutManager(multiManager);
        RoomsRecycler.setLayoutManager(roomsManager);
        TemplateRoom_adapter roomsAdapter = new TemplateRoom_adapter(template.rooms);
        RoomsRecycler.setAdapter(roomsAdapter);
    }

    public static void refreshMultiControls(Activity act) {
        TemplateMultiControl_adapter multiAdapter = new TemplateMultiControl_adapter(template.multiControls);
        RecyclerView MultiRecycler = act.findViewById(R.id.multiRecycler);
        MultiRecycler.setAdapter(multiAdapter);
    }

    public static void refreshMoods(Activity act) {
        RecyclerView MoodSRecycler = act.findViewById(R.id.moodRecycler);
        TemplateMood_adapter moodAdapter = new TemplateMood_adapter(template.moods);
        MoodSRecycler.setAdapter(moodAdapter);
    }

    public void addTemplateToRoom(View view) {
        Intent i = new Intent(act, ApplyTemplateToRooms.class);
        startActivity(i);
    }

    public void goToAddMultiControl(View view) {
        Intent i = new Intent(act,AddTemplateMultiControl.class);
        startActivity(i);
    }

    public void goToAddMood(View view) {
        Intent i = new Intent(act,AddTemplateMood.class);
        startActivity(i);
    }
}