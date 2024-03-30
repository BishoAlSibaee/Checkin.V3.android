package hotelservices.syriasoft.cleanup.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.syriasoft.hotelservices.R;

import java.util.Calendar;
import java.util.List;

import hotelservices.syriasoft.cleanup.MyApp;
import hotelservices.syriasoft.cleanup.ROOM;
import hotelservices.syriasoft.cleanup.cleanOrder;

public class OrdersGridAdapter extends OrdersAdapter {
    public OrdersGridAdapter(List<cleanOrder> list) {
        super(list);
    }

    @NonNull
    @Override
    public OrdersGridAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_unit_2,null);
        return new OrdersGridAdapter.Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersGridAdapter.Holder holder, int position) {
        ROOM RR = searchRoomByNumber(Integer.parseInt(list.get(position).roomNumber), MyApp.Rooms);
        Context co = holder.itemView.getContext();
        if (list.get(position).room != null) {
            if (RR.roomStatus == 1) {
                holder.room.setTextColor(Color.WHITE);
                holder.room.setBackgroundColor(co.getColor(R.color.greenRoom));
                holder.img.setBackgroundColor(co.getColor(R.color.greenRoom));
                holder.dep.setBackgroundColor(co.getColor(R.color.greenRoom));
                holder.dep.setTextColor(Color.WHITE);
                holder.date.setBackgroundColor(co.getColor(R.color.greenRoom));
                holder.date.setTextColor(Color.WHITE);
            }
            else if (RR.roomStatus == 2) {
                holder.room.setTextColor(Color.WHITE);
                holder.room.setBackgroundColor(co.getColor(R.color.redRoom));
                holder.room.setBackgroundResource(R.color.redRoom);
                holder.SuitStatus.setBackgroundColor(co.getColor(R.color.redRoom));
                holder.img.setBackgroundColor(co.getColor(R.color.redRoom));
                holder.dep.setBackgroundColor(co.getColor(R.color.redRoom));
                holder.dep.setTextColor(Color.WHITE);
                holder.date.setBackgroundColor(co.getColor(R.color.redRoom));
                holder.date.setTextColor(Color.WHITE);
            }
            else if (RR.roomStatus == 3) {
                holder.room.setTextColor(Color.WHITE);
                holder.room.setBackgroundColor(co.getColor(R.color.blueRoom));
                holder.SuitStatus.setBackgroundColor(co.getColor(R.color.blueRoom));
                holder.img.setBackgroundColor(co.getColor(R.color.blueRoom));
                holder.dep.setBackgroundColor(co.getColor(R.color.blueRoom));
                holder.dep.setTextColor(Color.WHITE);
                holder.date.setBackgroundColor(co.getColor(R.color.blueRoom));
                holder.date.setTextColor(Color.WHITE);
            }
            else if (RR.roomStatus == 4) {
                holder.room.setTextColor(Color.WHITE);
                holder.room.setBackgroundColor(Color.GRAY);
                holder.SuitStatus.setBackgroundColor(Color.GRAY);
                holder.img.setBackgroundColor(Color.GRAY);
                holder.dep.setBackgroundColor(Color.GRAY);
                holder.dep.setTextColor(Color.WHITE);
                holder.date.setBackgroundColor(Color.WHITE);
                holder.date.setTextColor(Color.WHITE);
            }
            RR.getFireRoom().child("SuiteStatus").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        if (dataSnapshot.getValue().toString().equals("2")) {
                            holder.SuitStatus.setVisibility(View.VISIBLE);
                            holder.SuitStatus.setText("S");
                            holder.SuitStatus.setTextColor(Color.WHITE);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(list.get(position).date);
        int month = c.get(Calendar.MONTH) + 1;
        String DATE = c.get(Calendar.DAY_OF_MONTH)+"/"+month+"/"+c.get(Calendar.YEAR)+" "+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE);
        holder.date.setText(DATE);
        if (list.get(position).dep.equals("RoomService")) {
            holder.dep.setText(list.get(position).roomServiceText);
        }
        else {
            holder.dep.setText(list.get(position).dep);
        }
        switch (list.get(position).dep) {
            case "Cleanup":
                holder.img.setImageResource(R.drawable.cleanup_btn);
                break;
            case "Laundry":
                holder.img.setImageResource(R.drawable.laundry_btn);
                break;
            case "RoomService":
                holder.img.setImageResource(R.drawable.roomservice);
                break;
            case "SOS" :
                holder.img.setImageResource(R.drawable.sos_btn);
                break;
            case "MiniBar" :
                holder.img.setImageResource(R.drawable.minibar);
                break;
        }
        holder.room.setText(list.get(position).roomNumber);
        holder.itemView.setOnLongClickListener(v -> {
            if (RR == null) {
                Toast.makeText(co,"Room is Not Found",Toast.LENGTH_SHORT).show();
            }
            else {
                try {
                    createConfirmOrderDialog(co, RR, holder.dep, holder.date,holder.dep.getText().toString());
                }
                catch (Exception e) {
                    Toast.makeText(co,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        });
        holder.itemView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }
}
