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

public class TemplateMood_adapter extends RecyclerView.Adapter<TemplateMood_adapter.HOLDER> {

    List<TemplateMood> list;

    TemplateMood_adapter(List<TemplateMood> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public TemplateMood_adapter.HOLDER onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_mood_unit,null);
        return new HOLDER(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TemplateMood_adapter.HOLDER holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(list.get(position).name);
        holder.condDevice.setText(list.get(position).conditionButton.SwitchName);
        holder.condDp.setText(String.valueOf(list.get(position).conditionButton.DP));
        if (list.get(position).conditionButton.status) {
            holder.condStatus.setText("On");
        }
        else {
            holder.condStatus.setText("Off");
        }
        TemplateMoodTasks_adapter adapter = new TemplateMoodTasks_adapter(list.get(position).tasks);
        holder.tasksRecycler.setAdapter(adapter);
        if (ViewTemplate.template.rooms.size() > 0) {
            holder.edit.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
        }
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder b = new AlertDialog.Builder(holder.itemView.getContext());
                b.setTitle("delete "+list.get(position).name+" mood");
                b.setMessage("are you sure to delete "+list.get(position).name+" mood ?");
                b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        list.get(position).deleteMood(ViewTemplate.template.MoodsReference.child(list.get(position).name), new RequestCallback() {
                            @Override
                            public void onSuccess() {
                                dialogInterface.dismiss();
                                ViewTemplate.template.moods.remove(position);
                                ViewTemplate.refreshMoods((Activity) holder.itemView.getContext());
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
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(holder.itemView.getContext(),EditTemplateMood.class);
                i.putExtra("index",position);
                holder.itemView.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HOLDER extends RecyclerView.ViewHolder {
        TextView name,condDevice,condDp,condStatus ;
        LinearLayout tasksLayout;
        ImageButton delete,edit;
        RecyclerView tasksRecycler;
        public HOLDER(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textView38);
            condDevice = itemView.findViewById(R.id.textView39);
            condDp = itemView.findViewById(R.id.textView40);
            condStatus = itemView.findViewById(R.id.textView41);
            tasksLayout = itemView.findViewById(R.id.tasksLyout);
            tasksRecycler = itemView.findViewById(R.id.tasksRecycler);
            LinearLayoutManager manager = new LinearLayoutManager(itemView.getContext(),RecyclerView.VERTICAL,false);
            tasksRecycler.setLayoutManager(manager);
            delete = itemView.findViewById(R.id.imageButton5);
            edit = itemView.findViewById(R.id.imageButton4);
        }
    }
}
