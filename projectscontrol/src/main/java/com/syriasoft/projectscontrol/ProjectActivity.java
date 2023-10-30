package com.syriasoft.projectscontrol;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.syriasoft.projectscontrol.Adapters.HomesAdapter;
import com.syriasoft.projectscontrol.RequestCallBacks.RequestCallback;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.home.sdk.bean.HomeBean;
import com.tuya.smart.home.sdk.callback.ITuyaGetHomeListCallback;
import com.tuya.smart.home.sdk.callback.ITuyaHomeResultCallback;

import java.util.ArrayList;
import java.util.List;

public class ProjectActivity extends AppCompatActivity {

    Activity act;
    List<HomeBean> Homes ;
    RecyclerView homesRecycler;
    static String New_Home_Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        setActivity();
        loginTuya(MyApp.SelectedProject.TuyaUser, MyApp.SelectedProject.TuyaPassword, new RequestCallback() {
            @Override
            public void onSuccess(String result) {
                getProjectHomes();
            }

            @Override
            public void onFailed(String error) {

            }
        });
    }

    void setActivity() {
        act = this ;
        TextView name = findViewById(R.id.textView9);
        name.setText(MyApp.SelectedProject.projectName);
        TextView city = findViewById(R.id.textView10);
        city.setText(MyApp.SelectedProject.city);
        TextView salesman = findViewById(R.id.textView100);
        salesman.setText(MyApp.SelectedProject.salesman);
        TextView tuyaUser = findViewById(R.id.textView102);
        tuyaUser.setText(MyApp.SelectedProject.TuyaUser);
        TextView tuyaPassword = findViewById(R.id.textView1001);
        tuyaPassword.setText(MyApp.SelectedProject.TuyaPassword);
        TextView url = findViewById(R.id.textView1025);
        url.setText(MyApp.SelectedProject.url);
        TextView devices = findViewById(R.id.textView1029);
        devices.setText(String.valueOf(MyApp.SelectedProject.ServerDevices.size()));
        TextView buildings = findViewById(R.id.textView10019);
        buildings.setText(String.valueOf(MyApp.SelectedProject.Buildings.size()));
        TextView floors = findViewById(R.id.textView10290);
        floors.setText(String.valueOf(MyApp.SelectedProject.AllFloors.size()));
        TextView rooms = findViewById(R.id.textView100190);
        rooms.setText(String.valueOf(MyApp.SelectedProject.AllRooms.size()));
        homesRecycler = findViewById(R.id.homesRecycler);
        homesRecycler.setLayoutManager(new GridLayoutManager(act,4));
    }

    void loginTuya(String user, String password, RequestCallback callback) {
        TuyaHomeSdk.getUserInstance().loginWithEmail("966", user, password, new ILoginCallback() {
            @Override
            public void onSuccess(User user) {
                Log.d("tuyaResponse","login "+user.getEmail());
                callback.onSuccess(user.getEmail());
            }

            @Override
            public void onError(String code, String error) {
                Log.d("tuyaResponse","login "+error);
                callback.onFailed(error);
            }
        });
    }

    void getProjectHomes() {
        TuyaHomeSdk.getHomeManagerInstance().queryHomeList(new ITuyaGetHomeListCallback() {
            @Override
            public void onSuccess(List<HomeBean> homeBeans) {
                Log.d("tuyaResponse","get homes "+homeBeans.size());
                Homes = homeBeans;
                HomesAdapter adapter = new HomesAdapter(Homes);
                homesRecycler.setAdapter(adapter);
            }

            @Override
            public void onError(String errorCode, String error) {
                Log.d("tuyaResponse","get homes "+error);
            }
        });
    }

    public void addNewHome(View view) {
        addNewHomeDialog d = new addNewHomeDialog(act, MyApp.SelectedProject.projectName);
        d.show();
    }

    static void createTuyaHome(String pName, RequestCallback callback) {
        List<String> rooms = new ArrayList<>();
        TuyaHomeSdk.getHomeManagerInstance().createHome(pName, 0, 0, "", rooms, new ITuyaHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean bean) {
                callback.onSuccess(bean.getName());
            }
            @Override
            public void onError(String errorCode, String errorMsg) {
                callback.onFailed(errorCode+" "+errorMsg);
            }
        });
    }
}