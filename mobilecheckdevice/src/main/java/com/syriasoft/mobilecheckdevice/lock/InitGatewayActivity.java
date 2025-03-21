package com.syriasoft.mobilecheckdevice.lock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ttlock.bl.sdk.api.ExtendedBluetoothDevice;
import com.ttlock.bl.sdk.gateway.api.GatewayClient;
import com.ttlock.bl.sdk.gateway.callback.InitGatewayCallback;
import com.ttlock.bl.sdk.gateway.model.ConfigureGatewayInfo;
import com.ttlock.bl.sdk.gateway.model.DeviceInfo;
import com.ttlock.bl.sdk.gateway.model.GatewayError;
import com.ttlock.bl.sdk.util.GsonUtil;
import com.ttlock.bl.sdk.util.LogUtil;
import com.ttlock.bl.sdk.util.NetworkUtil;
import com.example.mobilecheckdevice.R;
import retrofit2.Call;
import retrofit2.Callback;


public class InitGatewayActivity extends AppCompatActivity {

   // private ActivityInitGatewayBinding binding;
    private ConfigureGatewayInfo configureGatewayInfo;
    private ExtendedBluetoothDevice device;
    //private ChooseNetDialog dialog;
    Activity act = this ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_gateway);
        device = getIntent().getParcelableExtra(ExtendedBluetoothDevice.class.getName());
        configureGatewayInfo = new ConfigureGatewayInfo();
        initView();
        initListener();
    }

    private void initView() {
        if (NetworkUtil.isWifiConnected(this)) {
            TextView wifiName = (TextView) findViewById(R.id.wifi_name);
            wifiName.setText(NetworkUtil.getWifiSSid(this));
        }
    }

    private void uploadGatewayDetail(DeviceInfo deviceInfo, int gatewayId) {
        TextView wifiName = (TextView) findViewById(R.id.wifi_name);
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.uploadGatewayDetail(ApiService.CLIENT_ID, AuthActivity.acc.getAccess_token(), gatewayId, deviceInfo.getModelNum(), deviceInfo.hardwareRevision, deviceInfo.getFirmwareRevision(), wifiName.getText().toString(), System.currentTimeMillis());
        LogUtil.d("call server isSuccess api");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                String json = response.body();
                if (!TextUtils.isEmpty(json)) {
                    ServerError error = GsonUtil.toObject(json, ServerError.class);
                    if (error.errcode == 0)
                    {
                        Intent i = new Intent(act , UserGatewayActivity.class );
                    }
                        //startTargetActivity(UserGatewayActivity.class);
                     //ToastMaker.MakeToast(error.errmsg,act);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //ToastMaker.MakeToast(t.getMessage(),act);
                LogUtil.d("t.getMessage():" + t.getMessage());
            }
        });
    }

    private void isInitSuccess(final DeviceInfo deviceInfo) {
        ApiService apiService = RetrofitAPIManager.provideClientApi();
        Call<String> call = apiService.gatewayIsInitSuccess(ApiService.CLIENT_ID, AuthActivity.acc.getAccess_token(), device.getAddress(), System.currentTimeMillis());
        LogUtil.d("call server isSuccess api");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                String json = response.body();
                if (!TextUtils.isEmpty(json)) {
                    GatewayObj gatewayObj = GsonUtil.toObject(json, GatewayObj.class);
                    if (gatewayObj.errcode == 0)
                        uploadGatewayDetail(deviceInfo, gatewayObj.getGatewayId());
                    //else ToastMaker.MakeToast(gatewayObj.errmsg,act);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //ToastMaker.MakeToast(t.getMessage(),act);
                LogUtil.d("t.getMessage():" + t.getMessage());
            }
        });
    }

    private void initListener() {
        Button b = (Button) findViewById(R.id.btn_init_gateway);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configureGatewayInfo.uid = AuthActivity.acc.getUid();
                configureGatewayInfo.userPwd = AuthActivity.acc.getMd5Pwd();
                TextView wifiName = (TextView) findViewById(R.id.wifi_name);
                EditText wifiPwd = (EditText) findViewById(R.id.wifi_pwd);
                configureGatewayInfo.ssid = wifiName.getText().toString().trim();
//            configureGatewayInfo.plugName = binding.gatewayName.getText().toString().trim();
                configureGatewayInfo.wifiPwd = wifiPwd.getText().toString().trim();
                configureGatewayInfo.plugName = device.getAddress();

                GatewayClient.getDefault().initGateway(configureGatewayInfo, new InitGatewayCallback() {
                    @Override
                    public void onInitGatewaySuccess(DeviceInfo deviceInfo) {
                        LogUtil.d("gateway init success");
                        isInitSuccess(deviceInfo);
                    }

                    @Override
                    public void onFail(GatewayError error) {
                        //ToastMaker.MakeToast(error.getDescription(),act);
                        finish();
                    }
                });
            }
        });
      /*  binding.btnInitGateway.setOnClickListener(v -> {
            configureGatewayInfo.uid = MyApplication.getmInstance().getAccountInfo().getUid();
            configureGatewayInfo.userPwd = MyApplication.getmInstance().getAccountInfo().getMd5Pwd();

            configureGatewayInfo.ssid = binding.wifiName.getText().toString().trim();
//            configureGatewayInfo.plugName = binding.gatewayName.getText().toString().trim();
            configureGatewayInfo.wifiPwd = binding.wifiPwd.getText().toString().trim();


            configureGatewayInfo.plugName = device.getAddress();

            GatewayClient.getDefault().initGateway(configureGatewayInfo, new InitGatewayCallback() {
                @Override
                public void onInitGatewaySuccess(DeviceInfo deviceInfo) {
                    LogUtil.d("gateway init success");
                    isInitSuccess(deviceInfo);
                }

                @Override
                public void onFail(GatewayError error) {
                    ToastMaker.MakeToast(error.getDescription(),act);
                    finish();
                }
            });
        });*/

       /* binding.rlWifiName.setOnClickListener(v -> {
            chooseWifiDialog();
        });*/
    }

   /* private void chooseWifiDialog() {
        if (dialog == null) {
            dialog = new ChooseNetDialog(this);
            dialog.setOnSelectListener(new ChooseNetDialog.OnSelectListener() {
                @Override
                public void onSelect(WiFi wiFi) {
                    binding.wifiName.setText(wiFi.ssid);
                }
            });
        }
        dialog.show();
        GatewayClient.getDefault().scanWiFiByGateway(device.getAddress(), new ScanWiFiByGatewayCallback() {
            @Override
            public void onScanWiFiByGateway(List<WiFi> wiFis) {
                dialog.updateWiFi(wiFis);
            }

            @Override
            public void onScanWiFiByGatewaySuccess(){
                ToastMaker.MakeToast("scan completed",act);
            }

            @Override
            public void onFail(GatewayError error) {
                ToastMaker.MakeToast(error.getDescription(),act);
            }
        });
    }*/

    public static void launch(Activity activity, ExtendedBluetoothDevice device) {
        Intent intent = new Intent(activity, InitGatewayActivity.class);
        intent.putExtra(ExtendedBluetoothDevice.class.getName(), device);
        activity.startActivity(intent);
    }
}
