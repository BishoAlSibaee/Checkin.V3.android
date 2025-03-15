package com.syriasoft.server.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelservicesstandalone.R;
import com.syriasoft.server.Classes.Property.Bed;

import java.text.MessageFormat;
import java.util.List;

public class RoomOffline_Adapter extends RecyclerView.Adapter<RoomOffline_Adapter.Holder> {

    List<Bed> beds;

    public RoomOffline_Adapter(List<Bed> beds) {
        this.beds = beds;
    }
    @NonNull
    @Override
    public RoomOffline_Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_order_unit,parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomOffline_Adapter.Holder holder, int position) {
        Bed b = beds.get(position);
        if (b.isRoom()) {
            holder.room.setText(String.valueOf(b.room.RoomNumber));
        }
        else if (b.isSuite()) {
            holder.room.setText(MessageFormat.format("S{0}", b.suite.SuiteNumber));
        }
    }

    @Override
    public int getItemCount() {
        return beds.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView room;
        public Holder(@NonNull View itemView) {
            super(itemView);
            room = itemView.findViewById(R.id.textView32);
        }
    }
}
