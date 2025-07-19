package com.syriasoft.mobilecheckdevice.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecheckdevice.R;
import com.syriasoft.mobilecheckdevice.Classes.Property.Building;

import java.util.List;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.Holder> {

    List<Building> buildings;

    public BuildingAdapter(List<Building> buildings) {
        this.buildings = buildings;
    }

    @NonNull
    @Override
    public BuildingAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.building_unit,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BuildingAdapter.Holder holder, int position) {
        Building b = buildings.get(position);
        holder.buildingName.setText(b.buildingName);
    }

    @Override
    public int getItemCount() {
        return buildings.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView buildingName;
        public Holder(@NonNull View itemView) {
            super(itemView);
            buildingName = itemView.findViewById(R.id.textView79);
        }
    }
}
