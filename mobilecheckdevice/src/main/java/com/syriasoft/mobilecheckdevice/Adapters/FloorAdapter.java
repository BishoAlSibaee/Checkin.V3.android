package com.syriasoft.mobilecheckdevice.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecheckdevice.R;
import com.syriasoft.mobilecheckdevice.Classes.Property.Floor;

import java.util.List;

public class FloorAdapter extends RecyclerView.Adapter<FloorAdapter.Holder> {

    List<Floor> floors;

    public FloorAdapter(List<Floor> floors) {
        this.floors = floors;
    }

    @NonNull
    @Override
    public FloorAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.floor_unit,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FloorAdapter.Holder holder, int position) {
        Floor f = floors.get(position);
        holder.buildingName.setText(f.building.buildingName);
        holder.floor.setText(String.valueOf(f.floorNumber));
    }

    @Override
    public int getItemCount() {
        return floors.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView buildingName,floor;
        public Holder(@NonNull View itemView) {
            super(itemView);
            buildingName = itemView.findViewById(R.id.textView80);
            floor = itemView.findViewById(R.id.textView81);
        }
    }
}
