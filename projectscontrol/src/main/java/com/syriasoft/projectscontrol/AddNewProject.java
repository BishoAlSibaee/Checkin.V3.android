package com.syriasoft.projectscontrol;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.syriasoft.projectscontrol.RequestCallBacks.RequestCallback;
import com.syriasoft.projectscontrol.RequestCallBacks.TuyaUserCallback;
import com.tuya.smart.android.user.api.IRegisterCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IResultCallback;

import java.util.HashMap;
import java.util.Map;

public class AddNewProject extends AppCompatActivity {

    Activity act;
    LoadingDialog l;
    static String pName,City,Salesman,Email,Password,Url,pId,cId;
    static String Code;
    static String addProjectUrl = "https://www.ratco-solutions.com/Checkin/Test/php/addNewProject.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_project);
        setActivity();
    }

    void setActivity() {
        act = this;
        EditText projectName = findViewById(R.id.editTextTextPersonName);
        projectName.setHint(getResources().getString(R.string.projectName)+getResources().getString(R.string.required));
        projectName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pName = projectName.getText().toString();
            }
        });
        EditText city = findViewById(R.id.editTextTextPersonName1);
        city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                City = city.getText().toString();
            }
        });
        EditText salesman = findViewById(R.id.editTextTextPersonName2);
        salesman.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Salesman = salesman.getText().toString();
            }
        });
        EditText email = findViewById(R.id.editTextTextPersonName3);
        email.setHint(getResources().getString(R.string.tuyaUserEmail)+getResources().getString(R.string.required));
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Email = email.getText().toString();
            }
        });
        EditText password = findViewById(R.id.editTextTextPersonName4);
        password.setHint(getResources().getString(R.string.tuyaUserPassword)+getResources().getString(R.string.required));
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Password = password.getText().toString();
            }
        });
        EditText url = findViewById(R.id.editTextTextPersonName5);
        url.setHint(getResources().getString(R.string.url)+getResources().getString(R.string.required));
        url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Url = url.getText().toString();
            }
        });
        EditText ratcoProjectId = findViewById(R.id.editTextTextPersonName6);
        ratcoProjectId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pId = ratcoProjectId.getText().toString();
            }
        });
        EditText ratcoClientId = findViewById(R.id.editTextTextPersonName7);
        ratcoClientId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                cId = ratcoClientId.getText().toString();
            }
        });
    }

    public void addProject(View view) {
        l = new LoadingDialog(act,"sending verification code");
        l.show();
        sendTuyaVerificationCodeToEmail(Email, new RequestCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("addProject","code "+result);
                l.close();
                new VerificationCodeDialog(act,"Code sent to your email . please get it and fill it ").show();
            }

            @Override
            public void onFailed(String error) {
                Log.d("addProject","code "+error);
                l.close();
                if (!error.equals("User already exists")) {
                    new MessageDialog(error,"error",act);
                    return;
                }
                l = new LoadingDialog(act,"sending verification code");
                l.show();
                createNewProject(act,Email, pName, City, Salesman, Password, Url, pId, cId, new RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        l.close();
                        new MessageDialog("project created","Done",act);
                    }

                    @Override
                    public void onFailed(String error) {
                        l.close();
                        new MessageDialog(error,"error",act);
                    }
                });
            }
        });
    }

    void sendTuyaVerificationCodeToEmail(String email, RequestCallback callback) {
        TuyaHomeSdk.getUserInstance().sendVerifyCodeWithUserName(email, "", "966", 1, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                callback.onFailed(error);
            }

            @Override
            public void onSuccess() {
                callback.onSuccess("sent");
            }
        });
    }

    static void verifyTuyaVerificationCode(String email,String code , RequestCallback callback) {
        TuyaHomeSdk.getUserInstance().checkCodeWithUserName(email, "", "966", code, 1, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                callback.onFailed(error);
            }

            @Override
            public void onSuccess() {
                callback.onSuccess("done");
            }
        });
    }

    static void createTuyaAccount(String email, String password, String code, TuyaUserCallback callback) {
        TuyaHomeSdk.getUserInstance().registerAccountWithEmail("966", email, password, code, new IRegisterCallback() {
            @Override
            public void onSuccess(User user) {
                callback.onSuccess(user);
            }

            @Override
            public void onError(String code, String error) {
                callback.onFail(error);
            }
        });
    }

    static void createNewProject(Activity act,String email,String pName,String city,String salesman,String password,String url,String pId,String cId,RequestCallback callback) {
        StringRequest request = new StringRequest(Request.Method.POST, addProjectUrl, (response)->{
            Log.d("addProject",response);
            callback.onSuccess(response);
            }, (error) -> {
            Log.d("addProject",error.toString());
            callback.onFailed(error.toString());
            })
        {
            @NonNull
            @Override
            protected Map<String, String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("email",email);
                params.put("projectName",pName);
                params.put("city",city);
                params.put("salesman",salesman);
                params.put("password",password);
                params.put("url",url);
                if (!pId.isEmpty()) {
                    params.put("projectId",pId);
                }
                if (!cId.isEmpty()) {
                    params.put("clientId",cId);
                }
                return params;
            }
        };

        Volley.newRequestQueue(act).add(request);
    }

    public static void ContinueWithVerificationCode(Activity act, String Email, String Password, String pName, String City, String Salesman, String Url, String pId, String cId) {
        if (Code == null) {
            new MessageDialog("no code entered","code?",act);
            return;
        }
        final LoadingDialog[] l = {new LoadingDialog(act, "verify code ..")};
        l[0].show();
        verifyTuyaVerificationCode(Email, Code, new RequestCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("addProject","verify "+result);
                l[0].close();
                l[0] = new LoadingDialog(act,"creating tuya account");
                l[0].show();
                createTuyaAccount(Email, Password, Code, new TuyaUserCallback() {
                    @Override
                    public void onSuccess(User user) {
                        Log.d("addProject","account "+result);
                        l[0].close();
                        l[0] = new LoadingDialog(act,"adding project");
                        l[0].show();
                        createNewProject(act,Email, pName, City, Salesman, Password, Url, pId, cId, new RequestCallback() {
                            @Override
                            public void onSuccess(String result) {
                                l[0].close();
                                new MessageDialog("project created","Done",act);
                            }

                            @Override
                            public void onFailed(String error) {
                                l[0].close();
                                new MessageDialog(error,"error",act);
                            }
                        });
                    }

                    @Override
                    public void onFail(String error) {
                        l[0].close();
                        new MessageDialog(error,"error",act);
                    }
                });
            }

            @Override
            public void onFailed(String error) {
                l[0].close();
                new MessageDialog(error,"error",act);
            }
        });
    }
}