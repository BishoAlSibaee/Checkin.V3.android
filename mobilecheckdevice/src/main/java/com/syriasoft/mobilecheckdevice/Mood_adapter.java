package com.syriasoft.mobilecheckdevice;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecheckdevice.R;
import com.tuya.smart.home.sdk.bean.scene.SceneBean;

import java.util.List;

public class Mood_adapter extends RecyclerView.Adapter<Mood_adapter.Holder> {

    List<SceneBean> list;

    Mood_adapter(List<SceneBean> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Mood_adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mood_unit,null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Mood_adapter.Holder holder, int position) {
        holder.name.setText(list.get(position).getName());
        if (list.get(position).isEnabled()) {
            holder.active.setImageResource(android.R.drawable.presence_online);
        }
        else {
            holder.active.setImageResource(android.R.drawable.presence_offline);
        }
        holder.itemView.setOnClickListener(Moods.makeAdapterListener((Activity) holder.itemView.getContext(),list.get(position)));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView active;
        public Holder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textView49);
            active = itemView.findViewById(R.id.imageView9);
        }
    }
}
