package hotelservices.syriasoft.cleanup;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.syriasoft.hotelservices.R;
import com.ttlock.bl.sdk.api.TTLockClient;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class ROOMS extends AppCompatActivity {
    static ROOMS_ADAPTER adapter;
    static  Activity act;
    static LinearLayout FloorsLayout;
    static List<ROOM> list;
    RecyclerView rooms ;
    LinearLayout HOME,ROOMS_BTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_o_o_m_s);
        setActivity();
        setTheRooms();
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        if (ActivityCompat.checkSelfPermission(act, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                b.setTitle("Accept Bluetooth Permission").setMessage("please accept Bluetooth permission ")
                        .setPositiveButton("ok", (dialog, which) -> {
                            if (ActivityCompat.checkSelfPermission(act, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
                            }
                        }).create().show();
            }
        }
        else {
            ensureBluetoothIsEnabled();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    b.setTitle("Accept Bluetooth Permission").setMessage("please accept Bluetooth permission ")
                            .setPositiveButton("ok", (dialog, which) -> {
                                if (ActivityCompat.checkSelfPermission(act, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
                                }
                            }).create().show();
                }
            }
            else {
                ensureBluetoothIsEnabled();
            }
        }
    }

    void setActivity() {
        act = this;
        FloorsLayout = findViewById(R.id.floorsLayout);
        if (MyApp.Rooms.size() == 0) {
            new messageDialog("no rooms detected","no rooms" ,act);
        }
        list = MyApp.Rooms;
        rooms = findViewById(R.id.rooms_recycler);
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        manager.offsetChildrenHorizontal(2);
        manager.offsetChildrenVertical(2);
        adapter = new ROOMS_ADAPTER(list);
        rooms.setLayoutManager(manager);
        HOME = findViewById(R.id.homeLayout);
        ROOMS_BTN = findViewById(R.id.roomsLayout);
        HOME.setOnClickListener(v -> {
            act.finish();
        });
    }

    static void setTheRooms() {
        FloorsLayout.removeAllViews();
        list = MyApp.Rooms;
        List<List<ROOM>> MainList = new ArrayList<>();
        for (int i=0;i<list.size();i++) {
            if (i == 0) {
                MainList.add(new ArrayList<>());
                MainList.get(i).add(list.get(i));
            }
            else {
                if (list.get(i).FloorId == MainList.get(MainList.size()-1).get(MainList.get(MainList.size()-1).size()-1).FloorId) {
                    MainList.get(MainList.size()-1).add(list.get(i));
                }
                else {
                    MainList.add(new ArrayList<>());
                    MainList.get(MainList.size()-1).add(list.get(i));
                }
            }
        }
        Log.d("MainList",MainList.size()+" ");
        for (int i=0;i<MainList.size();i++) {
            FloorsLayout.addView(setFloor(MainList.get(i)));
        }
    }

    static LinearLayout setFloor(List<ROOM> list) {
        LinearLayout L = new LinearLayout(act);
        L.setOrientation(LinearLayout.VERTICAL);
        L.setPadding(0,2,0,2);
        Button B = new Button(act);
        B.setText(MessageFormat.format("Building {0}  Floor {1} ({2} Rooms)", list.get(0).Building, list.get(0).Floor, list.size()));
        B.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        B.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        B.setBackgroundResource(R.color.gray2);
        B.setTextColor(act.getResources().getColor(R.color.white,null));
        B.setAllCaps(false);
        RecyclerView R = new RecyclerView(act);
        GridLayoutManager manager = new GridLayoutManager(act, 4);
        manager.offsetChildrenHorizontal(2);
        manager.offsetChildrenVertical(2);
        R.setLayoutManager(manager);
        ROOMS_ADAPTER adapter = new ROOMS_ADAPTER(list);
        R.setAdapter(adapter);
        R.setVisibility(View.GONE);
        R.setPadding(0,10,0,0);
        B.setOnClickListener(view -> {
            if (R.getVisibility() == View.VISIBLE) {
                R.setVisibility(View.GONE);
            }
            else {
                R.setVisibility(View.VISIBLE);
            }
        });
        L.addView(B);
        L.addView(R);
        return L ;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void ensureBluetoothIsEnabled() {
        if (!TTLockClient.getDefault().isBLEEnabled(act)) {
            TTLockClient.getDefault().requestBleEnable(act);
        }
    }
}