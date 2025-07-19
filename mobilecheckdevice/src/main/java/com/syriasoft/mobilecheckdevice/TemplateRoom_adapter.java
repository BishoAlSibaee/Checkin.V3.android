package com.syriasoft.mobilecheckdevice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecheckdevice.R;

import java.util.List;

public class TemplateRoom_adapter extends RecyclerView.Adapter<TemplateRoom_adapter.HOLDER> {

    List<ROOM> list ;
    View.OnClickListener listener;

    TemplateRoom_adapter(List<ROOM> list) {
        this.list= list;
    }

    TemplateRoom_adapter(List<ROOM> list,View.OnClickListener listener) {
        this.list= list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TemplateRoom_adapter.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_room_unit,null);
        return new HOLDER(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TemplateRoom_adapter.HOLDER holder, int position) {
        holder.roomNumber.setText(String.valueOf(list.get(position).RoomNumber));
        if (this.listener != null) {
            holder.itemView.setOnClickListener(this.listener);

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HOLDER extends RecyclerView.ViewHolder {
        TextView roomNumber;
        public HOLDER(@NonNull View itemView) {
            super(itemView);
            roomNumber = itemView.findViewById(R.id.textView45);
        }
    }
}
