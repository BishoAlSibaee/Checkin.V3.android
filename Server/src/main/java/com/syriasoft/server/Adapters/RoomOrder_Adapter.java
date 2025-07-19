package com.syriasoft.server.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelservicesstandalone.R;
import com.syriasoft.server.Classes.Property.Bed;
import com.syriasoft.server.Classes.Property.Room;
import com.syriasoft.server.Classes.Property.Suite;
import com.syriasoft.server.ReceptionScreen;
import com.tuya.smart.sdk.api.IResultCallback;

import java.text.MessageFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RoomOrder_Adapter extends RecyclerView.Adapter<RoomOrder_Adapter.Holder> {

    List<Bed> beds;

    public RoomOrder_Adapter(List<Bed> beds) {
        this.beds = beds;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_order_unit,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Bed b = beds.get(position);
        if (b.isRoom()) {
            Room room = b.room;
            holder.room.setText(String.valueOf(b.room.RoomNumber));
            if (beds == ReceptionScreen.cleanupBeds) {
                holder.itemView.setOnClickListener(view->{
                    //resetCloseTimer();
                    AlertDialog.Builder bb = new AlertDialog.Builder(holder.itemView.getContext());
                    bb.setTitle(room.RoomNumber+" Cleanup Off").setMessage("Turn "+room.RoomNumber+" Cleanup Off \n are you sure ?").setNegativeButton("no", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
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
                                room.getMainServiceSwitch().cleanup.setCurrent(false);
                                ReceptionScreen.setCleanupLists((Activity) holder.itemView.getContext());
                                ReceptionScreen.refreshCleanup((Activity) holder.itemView.getContext());

                            }
                        }
                    }).create().show();
                });
            }
            if (beds == ReceptionScreen.laundryBeds) {
                holder.itemView.setOnClickListener(view->{
                    //resetCloseTimer();
                    AlertDialog.Builder bb = new AlertDialog.Builder(holder.itemView.getContext());
                    bb.setTitle(room.RoomNumber+" Laundry Off").setMessage("Turn "+room.RoomNumber+" Laundry Off \n are you sure ?").setNegativeButton("no", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
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
                                room.getMainServiceSwitch().laundry.setCurrent(false);
                                ReceptionScreen.setLaundryLists((Activity) holder.itemView.getContext());
                                ReceptionScreen.refreshLaundry((Activity) holder.itemView.getContext());
                            }
                        }
                    }).create().show();
                });
            }
            if (beds == ReceptionScreen.checkoutBeds) {
                holder.itemView.setOnClickListener(view->{
                    //resetCloseTimer();
                    AlertDialog.Builder bb = new AlertDialog.Builder(holder.itemView.getContext());
                    bb.setTitle(room.RoomNumber+" Checkout Off").setMessage("Turn "+room.RoomNumber+" Checkout Off \n are you sure ?").setNegativeButton("no", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
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
                                room.getMainServiceSwitch().checkout.setCurrent(false);
                                ReceptionScreen.setCheckoutLists((Activity) holder.itemView.getContext());
                                ReceptionScreen.refreshCheckout((Activity) holder.itemView.getContext());
                            }
                        }
                    }).create().show();
                });
            }
            if (beds == ReceptionScreen.dndBeds) {
                holder.itemView.setOnClickListener(view->{
                    //resetCloseTimer();
                    AlertDialog.Builder bb = new AlertDialog.Builder(holder.itemView.getContext());
                    bb.setTitle(room.RoomNumber+" DND Off").setMessage("Turn "+room.RoomNumber+" DND Off \n are you sure ?").setNegativeButton("no", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
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
                                room.getMainServiceSwitch().dnd.setCurrent(false);
                                ReceptionScreen.setDndLists((Activity) holder.itemView.getContext());
                                ReceptionScreen.refreshDND((Activity) holder.itemView.getContext());
                            }
                        }
                    }).create().show();
                });
            }
        }
        else if (b.isSuite()) {
            Suite suite = b.suite;
            holder.room.setText(MessageFormat.format("S{0}", b.suite.SuiteNumber));
            if (beds == ReceptionScreen.cleanupBeds) {
                holder.itemView.setOnClickListener(view->{
                    //resetCloseTimer();
                    AlertDialog.Builder bb = new AlertDialog.Builder(holder.itemView.getContext());
                    bb.setTitle(suite.SuiteNumber+" Cleanup Off").setMessage("Turn "+suite.SuiteNumber+" Cleanup Off \n are you sure ?").setNegativeButton("no", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
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
            if (beds == ReceptionScreen.laundryBeds) {
                holder.itemView.setOnClickListener(view->{
                    //resetCloseTimer();
                    AlertDialog.Builder bb = new AlertDialog.Builder(holder.itemView.getContext());
                    bb.setTitle(suite.SuiteNumber+" Laundry Off").setMessage("Turn "+suite.SuiteNumber+" Laundry Off \n are you sure ?").setNegativeButton("no", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
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
            if (beds == ReceptionScreen.checkoutBeds) {
                holder.itemView.setOnClickListener(view->{
                    //resetCloseTimer();
                    AlertDialog.Builder bb = new AlertDialog.Builder(holder.itemView.getContext());
                    bb.setTitle(suite.SuiteNumber+" Checkout Off").setMessage("Turn "+suite.SuiteNumber+" Checkout Off \n are you sure ?").setNegativeButton("no", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
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
            if (beds == ReceptionScreen.dndBeds) {
                holder.itemView.setOnClickListener(view->{
                    //resetCloseTimer();
                    AlertDialog.Builder bb = new AlertDialog.Builder(holder.itemView.getContext());
                    bb.setTitle(suite.SuiteNumber+" DND Off").setMessage("Turn "+suite.SuiteNumber+" DND Off \n are you sure ?").setNegativeButton("no", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("Yes", (dialogInterface, i) -> {
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
        }
    }

    @Override
    public int getItemCount() {
        return beds.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView room ;
        public Holder(@NonNull View itemView) {
            super(itemView);
            room = itemView.findViewById(R.id.textView32);
        }
    }

    void resetCloseTimer() {
        Log.d("closeTimer","reset");
        if (ReceptionScreen.terminateTimer != null) {
            ReceptionScreen.terminateTimer.cancel();
            ReceptionScreen.terminateTimer = new Timer();
            ReceptionScreen.terminateTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Process.killProcess(Process.myPid());
                }
            },1000 * 60 * 1);
        }
        else {
            ReceptionScreen.terminateTimer = new Timer();
            ReceptionScreen.terminateTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Process.killProcess(Process.myPid());
                }
            },1000 * 60 * 1);
        }
    }
}
