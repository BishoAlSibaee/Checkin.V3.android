package com.syriasoft.mobilecheckdevice;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecheckdevice.R;

import java.util.List;

public class ScreenButtons_Adapter extends RecyclerView.Adapter<ScreenButtons_Adapter.HOLDER> {

    List<SwitchButton> list ;

    ScreenButtons_Adapter(List<SwitchButton> list) {
        this.list = list ;
    }

    @NonNull
    @Override
    public HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.screen_buttons_unit,parent,false);
        HOLDER holder = new HOLDER(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HOLDER holder, @SuppressLint("RecyclerView") int position) {
        //holder.switche.setText(list.get(position).Switch+"");
        holder.button.setText(list.get(position).button+"");
        holder.name.setText(list.get(position).name);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder b = new AlertDialog.Builder(holder.itemView.getContext());
                        b.setTitle("Delete")
                        .setMessage("Do you want To Delete "+list.get(position).name+" Button")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //FullscreenActivity.lightsDB.deleteButtonFromScreen(list.get(position).Switch,list.get(position).button,list.get(position).name);
                                //ScreenButtons.CurrentAdapter = new ScreenButtons_Adapter(FullscreenActivity.lightsDB.getScreenButtons());
                                //ScreenButtons.CurrentButtonsRecycler.setAdapter(ScreenButtons.CurrentAdapter);
                            }
                        }).create().show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HOLDER extends RecyclerView.ViewHolder {
        TextView switche , button , name ;
        public HOLDER(@NonNull View itemView) {
            super(itemView);
            switche = (TextView) itemView.findViewById(R.id.textView60);
            button = (TextView) itemView.findViewById(R.id.textView61);
            name = (TextView) itemView.findViewById(R.id.textView62);
        }
    }
}
