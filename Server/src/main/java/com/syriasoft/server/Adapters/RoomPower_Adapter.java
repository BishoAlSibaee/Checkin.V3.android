package com.syriasoft.server.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelservicesstandalone.R;
import com.syriasoft.server.Classes.Property.Bed;
import com.syriasoft.server.Dialogs.PowerControlDialog;

import java.text.MessageFormat;
import java.util.List;

public class RoomPower_Adapter extends RecyclerView.Adapter<RoomPower_Adapter.Holder> {

    List<Bed> beds;
    public RoomPower_Adapter(List<Bed> beds) {
        this.beds = beds;
    }
    @NonNull
    @Override
    public RoomPower_Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_order_unit,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomPower_Adapter.Holder holder, int position) {
        Bed b = beds.get(position);
        if (b.isRoom()) {
            holder.room.setText(String.valueOf(b.room.RoomNumber));
        }
        else if (b.isSuite()) {
            holder.room.setText(MessageFormat.format("S{0}", b.suite.SuiteNumber));
        }
        holder.itemView.setOnClickListener(view->{
            new PowerControlDialog(holder.itemView.getContext(),b);
        });
    }

    @Override
    public int getItemCount() {
        return beds.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView room;
        public Holder(@NonNull View itemView) {
            super(itemView);
            room = itemView.findViewById(R.id.textView32);
        }
    }
}
