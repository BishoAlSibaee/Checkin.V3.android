package com.example.mobilecheckdevice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MultiControlButton_adapter extends RecyclerView.Adapter<MultiControlButton_adapter.HOLDER> {

    List<TemplateButton> list;

    MultiControlButton_adapter(List<TemplateButton> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MultiControlButton_adapter.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_multicontrol_button_unit,null);
        return new HOLDER(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MultiControlButton_adapter.HOLDER holder, int position) {
        holder.switchName.setText(list.get(position).SwitchName);
        holder.dp.setText(String.valueOf(list.get(position).DP));
        holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HOLDER extends RecyclerView.ViewHolder {
        TextView switchName,dp;
        public HOLDER(@NonNull View itemView) {
            super(itemView);
            switchName = itemView.findViewById(R.id.textView39);
            dp = itemView.findViewById(R.id.textView40);
        }
    }
}
