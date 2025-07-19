package com.syriasoft.mobilecheckdevice;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilecheckdevice.R;

import java.util.List;

public class Template_adapter extends RecyclerView.Adapter<Template_adapter.HOLDER> {

    List<Template> list;

    Template_adapter(List<Template> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Template_adapter.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_unit,null);
        return new HOLDER(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Template_adapter.HOLDER holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(list.get(position).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(holder.itemView.getContext(),ViewTemplate.class);
                i.putExtra("index",position);
                holder.itemView.getContext().startActivity(i);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder b = new AlertDialog.Builder(holder.itemView.getContext());
                b.setTitle("delete template "+list.get(position).name).setMessage("are you sure ??")
                                .setPositiveButton(holder.itemView.getContext().getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        list.get(position).TemplateReference.removeValue();
                                        ProjectTemplates.getTemplates();
                                        new MessageDialog("deleted","deleted",holder.itemView.getContext());
                                    }
                                }).setNegativeButton(holder.itemView.getContext().getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HOLDER extends RecyclerView.ViewHolder {
        TextView name;
        ImageButton edit , delete;
        public HOLDER(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textView43);
            delete = itemView.findViewById(R.id.imageButton3);
        }
    }
}
