package com.syriasoft.mobilecheckdevice;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecheckdevice.R;
import com.tuya.smart.sdk.bean.DeviceBean;

import java.util.List;

public class DoubleControlSecond_Adapter extends RecyclerView.Adapter<DoubleControlSecond_Adapter.HOLDER> {

    List<DeviceBean> list ;
    DoubleControlSecond_Adapter(List<DeviceBean> list){
        this.list = list ;
    }

    @NonNull
    @Override
    public HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.double_control_unit,parent,false);
        HOLDER holder = new HOLDER(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull HOLDER holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(list.get(position).getName());
        if (list.get(position).getDps().keySet().contains("1") && list.get(position).getDps().keySet().contains("2") && list.get(position).getDps().keySet().contains("3") && list.get(position).getDps().keySet().contains("4")) {
            holder.dps.setText("4 Buttons");
        }
        else if (list.get(position).getDps().keySet().contains("1") && list.get(position).getDps().keySet().contains("2") && list.get(position).getDps().keySet().contains("3")) {
            holder.dps.setText("3 Buttons");
        }
        else if (list.get(position).getDps().keySet().contains("1") && list.get(position).getDps().keySet().contains("2")) {
            holder.dps.setText("2 Buttons");
        }
        else if (list.get(position).getDps().keySet().contains("1") ) {
            holder.dps.setText("1 Buttons");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LightingDoubleControl.FIRST != null ) {
                    if (list.get(position) == LightingDoubleControl.FIRST) {
                        Toast.makeText(holder.itemView.getContext(),"don't select same first device " ,Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        LightingDoubleControl.SECOND = list.get(position);
                        Toast.makeText(holder.itemView.getContext(),list.get(position).getName()+" selected as second device",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(holder.itemView.getContext(),"please select first device  " ,Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HOLDER extends RecyclerView.ViewHolder {
        TextView name , dps ;
        public HOLDER(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.dev_name);
            dps = (TextView) itemView.findViewById(R.id.dev_dps);
        }
    }
}
