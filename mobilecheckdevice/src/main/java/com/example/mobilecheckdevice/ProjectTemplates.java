package com.example.mobilecheckdevice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProjectTemplates extends AppCompatActivity {

    static DatabaseReference ProjectTemplates;
    Activity act ;
    public static List<Template> Templates;
    static RecyclerView TemplatesRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_templates);
        setActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTemplates();
    }

    void setActivity() {
        act = this;
        Templates = new ArrayList<>();
        ProjectTemplates = Rooms.RoomTemplates;
        TemplatesRecycler = findViewById(R.id.templatesRecycler);
        GridLayoutManager manager = new GridLayoutManager(act,6);
        TemplatesRecycler.setLayoutManager(manager);
    }

    public void addNewTemplate(View view) {
        Intent i = new Intent(act,CreateNewTemplate.class);
        startActivity(i);
    }

    public static void getTemplates() {
        Templates.clear();
        ProjectTemplates.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Templates.clear();
                    Log.d("gettingTemplates", ProjectTemplates.getKey());
                    Iterable<DataSnapshot> templates = snapshot.getChildren();
                    for (DataSnapshot child : templates) {
                        String ROOMS = "" ;
                        if (child.child("Rooms").getValue() != null) {
                            ROOMS = Objects.requireNonNull(child.child("Rooms").getValue()).toString();
                        }
                        List<TemplateMood> MOODS = new ArrayList<>();
                        List<TemplateMultiControl> MULTIS = new ArrayList<>();
                        String templateName = "";
                        Log.d("gettingTemplates",child.getKey());
                        templateName = child.getKey();
                        if (child.child("Moods").getValue() != null) {
                            Iterable<DataSnapshot> moods = child.child("Moods").getChildren();
                            for (DataSnapshot mod:moods) {
                                MOODS.add(TemplateMood.getTemplateMoodFromFireBase(mod));
                            }
                        }
                        if (child.child("MultiControls").getValue() != null) {
                            Iterable<DataSnapshot> multis = child.child("MultiControls").getChildren();
                            for (DataSnapshot mult:multis) {
                                MULTIS.add(TemplateMultiControl.getTemplateMultiControlsFromFireBase(mult));
                            }
                        }
                        Template ttttt = new Template(templateName,MOODS,MULTIS,child.getRef());
                        if (!ROOMS.equals("")) {
                            ttttt.setTemplateRooms(ROOMS);
                        }
                        Templates.add(ttttt);
                    }
                    Template_adapter adapter = new Template_adapter(Templates);
                    TemplatesRecycler.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("gettingTemplates",error.getMessage());
            }
        });
        Template_adapter adapter = new Template_adapter(Templates);
        TemplatesRecycler.setAdapter(adapter);
    }
}