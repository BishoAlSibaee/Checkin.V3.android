package com.syriasoft.projectscontrol.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syriasoft.projectscontrol.R;
import com.tuya.smart.home.sdk.bean.HomeBean;

import java.util.List;

public class HomesAdapter extends RecyclerView.Adapter<HomesAdapter.HOLDER> {

    List<HomeBean> homes ;

    public HomesAdapter(List<HomeBean> homes) {
        this.homes = homes;
    }

    @NonNull
    @Override
    public HomesAdapter.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_unit,parent,false);
        HOLDER holder = new HOLDER(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomesAdapter.HOLDER holder, int position) {
        holder.name.setText(homes.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return homes.size();
    }

    public class HOLDER extends RecyclerView.ViewHolder {
        TextView name ;
        public HOLDER(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textView22);
        }
    }
}
