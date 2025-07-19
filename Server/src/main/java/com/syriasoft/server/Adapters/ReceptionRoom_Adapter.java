package com.syriasoft.server.Adapters;

import android.app.AlertDialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelservicesstandalone.R;
import com.syriasoft.server.Classes.Property.Bed;
import com.syriasoft.server.Classes.Property.Room;
import com.syriasoft.server.Classes.Property.Suite;
import com.tuya.smart.sdk.api.IResultCallback;

import java.text.MessageFormat;
import java.util.List;

public class ReceptionRoom_Adapter extends RecyclerView.Adapter<ReceptionRoom_Adapter.Holder> {

    public List<Bed> beds;

    public ReceptionRoom_Adapter(List<Bed> beds) {
        this.beds = beds;
    }

    @NonNull
    @Override
    public ReceptionRoom_Adapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reception_room_unit,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceptionRoom_Adapter.Holder holder, int position) {
        Bed bed = beds.get(position);
        if (bed.isRoom()) {
            Room room = bed.room;
            holder.bedNumber.setText(String.valueOf(room.RoomNumber));
            if (room.getRoomGateway() != null) {
                if (room.getRoomGateway().currentOnline) {
                    holder.bedNumber.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.tuya_default_panel_color_dialog_confirm,null));
                }
                else {
                    holder.bedNumber.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.design_default_color_error,null));
                }
            }
            if (room.getMainServiceSwitch() != null) {
                holder.serviceLayout.setVisibility(View.VISIBLE);
                if (room.getMainServiceSwitch().cleanup != null) {
                    if (room.getMainServiceSwitch().cleanup.getCurrent()) {
                        holder.cleanup.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.cleanup.setVisibility(View.INVISIBLE);
                    }
                    holder.cleanup.setOnClickListener(view -> {
                        AlertDialog.Builder b = new AlertDialog.Builder(holder.itemView.getContext());
                        b.setTitle(room.RoomNumber+" Cleanup Off").setMessage("Turn "+room.RoomNumber+" Cleanup Off \n are you sure ?").setNegativeButton("no", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
                            if (room.getMainServiceSwitch() != null) {
                                if (room.getMainServiceSwitch().cleanup != null) {
                                    room.cleanupRoomOff(new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }
                            }
                        }).create().show();
                    });
                }
                else {
                    holder.cleanup.setVisibility(View.INVISIBLE);
                }
                if (room.getMainServiceSwitch().laundry != null) {
                    if (room.getMainServiceSwitch().laundry.getCurrent()) {
                        holder.laundry.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.laundry.setVisibility(View.INVISIBLE);
                    }
                    holder.laundry.setOnClickListener(view -> {
                        AlertDialog.Builder b = new AlertDialog.Builder(holder.itemView.getContext());
                        b.setTitle(room.RoomNumber+" Laundry Off").setMessage("Turn "+room.RoomNumber+" Laundry Off \n are you sure ?").setNegativeButton("no", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
                            if (room.getMainServiceSwitch() != null) {
                                if (room.getMainServiceSwitch().laundry != null) {
                                    room.laundryRoomOff(new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }
                            }
                        }).create().show();
                    });
                }
                else {
                    holder.laundry.setVisibility(View.INVISIBLE);
                }
                if (room.getMainServiceSwitch().dnd != null) {
                    if (room.getMainServiceSwitch().dnd.getCurrent()) {
                        holder.dnd.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.dnd.setVisibility(View.INVISIBLE);
                    }
                    holder.dnd.setOnClickListener(view -> {
                        AlertDialog.Builder b = new AlertDialog.Builder(holder.itemView.getContext());
                        b.setTitle(room.RoomNumber+" DND Off").setMessage("Turn "+room.RoomNumber+" DND Off \n are you sure ?").setNegativeButton("no", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
                            if (room.getMainServiceSwitch() != null) {
                                if (room.getMainServiceSwitch().dnd != null) {
                                    room.dndRoomOff(new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }
                            }
                        }).create().show();
                    });
                }
                else {
                    holder.dnd.setVisibility(View.INVISIBLE);
                }
                if (room.getMainServiceSwitch().checkout != null) {
                    if (room.getMainServiceSwitch().checkout.getCurrent()) {
                        holder.checkout.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.checkout.setVisibility(View.INVISIBLE);
                    }
                    holder.checkout.setOnClickListener(view -> {
                        AlertDialog.Builder b = new AlertDialog.Builder(holder.itemView.getContext());
                        b.setTitle(room.RoomNumber+" Checkout Off").setMessage("Turn "+room.RoomNumber+" Checkout Off \n are you sure ?").setNegativeButton("no", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
                            if (room.getMainServiceSwitch() != null) {
                                if (room.getMainServiceSwitch().checkout != null) {
                                    room.checkoutRoomOff(new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }
                            }
                        }).create().show();
                    });
                }
                else {
                    holder.checkout.setVisibility(View.INVISIBLE);
                }
            }
            else {
                holder.serviceLayout.setVisibility(View.INVISIBLE);
            }
            if (room.getPowerModule() != null) {
                holder.powerLayout.setVisibility(View.VISIBLE);
                if (room.getPowerModule().dp1 != null && room.getPowerModule().dp2 != null) {
                    if (room.getPowerModule().dp1.getCurrent() && room.getPowerModule().dp2.getCurrent()) {
                        holder.powerStatus.setText(holder.itemView.getContext().getResources().getString(R.string.powerOn));
                        holder.powerStatus.setTextColor(Color.WHITE);
                    }
                    else if (room.getPowerModule().dp1.getCurrent()) {
                        holder.powerStatus.setText(holder.itemView.getContext().getResources().getString(R.string.powerCard));
                        holder.powerStatus.setTextColor(Color.WHITE);
                    }
                    else if (!room.getPowerModule().dp2.getCurrent()) {
                        holder.powerStatus.setText(holder.itemView.getContext().getResources().getString(R.string.powerOff));
                        holder.powerStatus.setTextColor(Color.WHITE);
                    }
                    holder.powerOn.setOnClickListener(view -> {
                        room.getPowerModule().powerOnOffline(new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    });
                    holder.powerCard.setOnClickListener(view-> {
                        room.getPowerModule().powerByCardOffline(new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    });
                    holder.powerOff.setOnClickListener(view -> {
                        room.getPowerModule().powerOffOffline(new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    });
                }
            }
            else {
                holder.powerLayout.setVisibility(View.INVISIBLE);
                holder.powerStatus.setText(holder.itemView.getContext().getResources().getString(R.string.noPowerController));
                holder.powerStatus.setTextColor(Color.DKGRAY);
            }
        }
        else if (bed.isSuite()) {
            Suite suite = bed.suite;
            holder.bedNumber.setText(MessageFormat.format("S{0}", suite.SuiteNumber));
            holder.powerLayout.setVisibility(View.INVISIBLE);
            if (suite.getSuiteGateway() != null) {
                if (suite.getSuiteGateway().currentOnline) {
                    holder.bedNumber.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.tuya_default_panel_color_dialog_confirm,null));
                }
                else {
                    holder.bedNumber.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.design_default_color_error,null));
                }
            }
            if (suite.getMainServiceSwitch() != null) {
                holder.serviceLayout.setVisibility(View.VISIBLE);
                if (suite.getMainServiceSwitch().cleanup != null) {
                    if (suite.getMainServiceSwitch().cleanup.getCurrent()) {
                        holder.cleanup.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.cleanup.setVisibility(View.INVISIBLE);
                    }
                    holder.cleanup.setOnClickListener(view -> {
                        AlertDialog.Builder b = new AlertDialog.Builder(holder.itemView.getContext());
                        b.setTitle(suite.SuiteNumber+" Cleanup Off").setMessage("Turn "+suite.SuiteNumber+" Cleanup Off \n are you sure ?").setNegativeButton("no", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
                            if (suite.getMainServiceSwitch() != null) {
                                if (suite.getMainServiceSwitch().cleanup != null) {
                                    suite.cleanupSuiteOff(new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }
                            }
                        }).create().show();
                    });
                }
                else {
                    holder.cleanup.setVisibility(View.INVISIBLE);
                }
                if (suite.getMainServiceSwitch().laundry != null) {
                    if (suite.getMainServiceSwitch().laundry.getCurrent()) {
                        holder.laundry.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.laundry.setVisibility(View.INVISIBLE);
                    }
                    holder.laundry.setOnClickListener(view -> {
                        AlertDialog.Builder b = new AlertDialog.Builder(holder.itemView.getContext());
                        b.setTitle(suite.SuiteNumber+" Laundry Off").setMessage("Turn "+suite.SuiteNumber+" Laundry Off \n are you sure ?").setNegativeButton("no", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
                            if (suite.getMainServiceSwitch() != null) {
                                if (suite.getMainServiceSwitch().laundry != null) {
                                    suite.laundrySuiteOff(new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }
                            }
                        }).create().show();
                    });
                }
                else {
                    holder.laundry.setVisibility(View.INVISIBLE);
                }
                if (suite.getMainServiceSwitch().dnd != null) {
                    if (suite.getMainServiceSwitch().dnd.getCurrent()) {
                        holder.dnd.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.dnd.setVisibility(View.INVISIBLE);
                    }
                    holder.dnd.setOnClickListener(view -> {
                        AlertDialog.Builder b = new AlertDialog.Builder(holder.itemView.getContext());
                        b.setTitle(suite.SuiteNumber+" DND Off").setMessage("Turn "+suite.SuiteNumber+" DND Off \n are you sure ?").setNegativeButton("no", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
                            if (suite.getMainServiceSwitch() != null) {
                                if (suite.getMainServiceSwitch().dnd != null) {
                                    suite.dndSuiteOff(new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }
                            }
                        }).create().show();
                    });
                }
                else {
                    holder.dnd.setVisibility(View.INVISIBLE);
                }
                if (suite.getMainServiceSwitch().checkout != null) {
                    if (suite.getMainServiceSwitch().checkout.getCurrent()) {
                        holder.checkout.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.checkout.setVisibility(View.INVISIBLE);
                    }
                    holder.checkout.setOnClickListener(view -> {
                        AlertDialog.Builder b = new AlertDialog.Builder(holder.itemView.getContext());
                        b.setTitle(suite.SuiteNumber+" Checkout Off").setMessage("Turn "+suite.SuiteNumber+" Checkout Off \n are you sure ?").setNegativeButton("no", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
                            if (suite.getMainServiceSwitch() != null) {
                                if (suite.getMainServiceSwitch().checkout != null) {
                                    suite.checkoutSuiteOff(new IResultCallback() {
                                        @Override
                                        public void onError(String code, String error) {

                                        }

                                        @Override
                                        public void onSuccess() {

                                        }
                                    });
                                }
                            }
                        }).create().show();
                    });
                }
                else {
                    holder.checkout.setVisibility(View.INVISIBLE);
                }
            }
            else {
                holder.serviceLayout.setVisibility(View.INVISIBLE);
            }
            if (suite.getPowerModule() != null) {
                holder.powerLayout.setVisibility(View.VISIBLE);
                if (suite.getPowerModule().dp1 != null && suite.getPowerModule().dp2 != null) {
                    if (suite.getPowerModule().dp1.getCurrent() && suite.getPowerModule().dp2.getCurrent()) {
                        holder.powerStatus.setText(holder.itemView.getContext().getResources().getString(R.string.powerOn));
                        holder.powerStatus.setTextColor(Color.WHITE);
                    }
                    else if (suite.getPowerModule().dp1.getCurrent()) {
                        holder.powerStatus.setText(holder.itemView.getContext().getResources().getString(R.string.powerCard));
                        holder.powerStatus.setTextColor(Color.WHITE);
                    }
                    else if (!suite.getPowerModule().dp2.getCurrent()) {
                        holder.powerStatus.setText(holder.itemView.getContext().getResources().getString(R.string.powerOff));
                        holder.powerStatus.setTextColor(Color.WHITE);
                    }
                    holder.powerOn.setOnClickListener(view -> {
                        suite.getPowerModule().powerOnOffline(new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    });
                    holder.powerCard.setOnClickListener(view-> {
                        suite.getPowerModule().powerByCardOffline(new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    });
                    holder.powerOff.setOnClickListener(view -> {
                        suite.getPowerModule().powerOffOffline(new IResultCallback() {
                            @Override
                            public void onError(String code, String error) {

                            }

                            @Override
                            public void onSuccess() {

                            }
                        });
                    });
                }
            }
            else {
                holder.powerLayout.setVisibility(View.INVISIBLE);
                holder.powerStatus.setText(holder.itemView.getContext().getResources().getString(R.string.noPowerController));
                holder.powerStatus.setTextColor(Color.DKGRAY);
            }
        }
    }

    @Override
    public int getItemCount() {
        return beds.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        ImageView cleanup,laundry,dnd,checkout;
        ImageView powerOn,powerCard,powerOff;
        TextView bedNumber,powerStatus;
        LinearLayout powerLayout,serviceLayout;
        public Holder(@NonNull View itemView) {
            super(itemView);
            cleanup = itemView.findViewById(R.id.imageView6);
            laundry = itemView.findViewById(R.id.imageView8);
            dnd = itemView.findViewById(R.id.imageView7);
            checkout = itemView.findViewById(R.id.imageView9);
            powerOn = itemView.findViewById(R.id.imageView10);
            powerCard = itemView.findViewById(R.id.imageView11);
            powerOff = itemView.findViewById(R.id.imageView12);
            bedNumber = itemView.findViewById(R.id.textView17);
            powerStatus = itemView.findViewById(R.id.textView18);
            powerLayout = itemView.findViewById(R.id.powerLayout);
            serviceLayout = itemView.findViewById(R.id.serviceLayout);
        }
    }
}
