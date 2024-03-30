package com.example.mobilecheckdevice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecheckdevice.Interface.RequestCallback;

import java.util.List;

public class TemplateMultiControl_adapter extends RecyclerView.Adapter<TemplateMultiControl_adapter.HOLDER> {

    List<TemplateMultiControl> list;

    TemplateMultiControl_adapter(List<TemplateMultiControl> list) {
        this.list = list ;
    }

    @NonNull
    @Override
    public TemplateMultiControl_adapter.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_multicontrol_unit,null);
        return new HOLDER(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TemplateMultiControl_adapter.HOLDER holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(list.get(position).name);
        LinearLayoutManager manager = new LinearLayoutManager(holder.itemView.getContext(),RecyclerView.VERTICAL,false);
        holder.buttons.setLayoutManager(manager);
        MultiControlButton_adapter adapter = new MultiControlButton_adapter(list.get(position).multiControlButtons);
        holder.buttons.setAdapter(adapter);
        holder.itemView.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        if (ViewTemplate.template.rooms.size() > 0) {
            holder.edit.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
        }
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(holder.itemView.getContext(),EditTemplateMultiControl.class);
                i.putExtra("index",position);
                holder.itemView.getContext().startActivity(i);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder b = new AlertDialog.Builder(holder.itemView.getContext());
                b.setTitle("delete "+list.get(position).name+" multi control");
                b.setMessage("are you sure to delete "+list.get(position).name+" multi control ?");
                b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        list.get(position).deleteTemplateMultiControl(ViewTemplate.template.MultiReference.child(list.get(position).name), new RequestCallback() {
                            @Override
                            public void onSuccess() {
                                dialogInterface.dismiss();
                                ViewTemplate.template.multiControls.remove(position);
                                ViewTemplate.refreshMultiControls((Activity) holder.itemView.getContext());
                                new MessageDialog("deleted","Done",holder.itemView.getContext());
                            }

                            @Override
                            public void onFail(String error) {
                                new MessageDialog(error,"error",holder.itemView.getContext());
                            }
                        });
                    }
                });
                b.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HOLDER extends RecyclerView.ViewHolder {
        TextView name ;
        ImageButton edit,delete;
        RecyclerView buttons;
        public HOLDER(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textView42);
            buttons = itemView.findViewById(R.id.buttonsRecycler);
            edit = itemView.findViewById(R.id.button39);
            delete = itemView.findViewById(R.id.imageButton2);
        }
    }
}
