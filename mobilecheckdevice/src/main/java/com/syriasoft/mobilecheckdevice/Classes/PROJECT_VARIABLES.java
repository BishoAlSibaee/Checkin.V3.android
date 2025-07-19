package com.syriasoft.mobilecheckdevice.Classes;

import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.syriasoft.mobilecheckdevice.Classes.Interfaces.GetStops;
import com.syriasoft.mobilecheckdevice.Interface.RequestCallback;
import com.syriasoft.mobilecheckdevice.MyApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class PROJECT_VARIABLES {
    public static int id;
    public static String projectName;
    public static int Hotel ;
    public static int Temp ;
    public static int Interval;
    public static int DoorWarning;
    public static int CheckinModeActive;
    public static int CheckinModeTime;
    public static String CheckinActions;
    public static int CheckoutModeActive;
    public static int CheckoutModeTime;
    public static String CheckoutActions;
    public static String WelcomeMessage;
    public static String Logo;
    public static int PoweroffClientIn;
    public static int PoweroffAfterHK;
    public static int ACSenarioActive;
    public static String OnClientBack;
    public static int HKCleanTime;
    public static String TuyaClientId;
    public static String TuyaClientSecret;
    public static int GuestApp;
    public static int PowerSaver;
    public static String CheckoutTime;
    public static int cleanupButton,laundryButton,dndButton,checkoutButton;
    public static int refreshSystemTime = 24;
    public static JSONObject ServiceSwitchButtons ;
    public static ClientBackActions clientBackActions;
    public static CheckoutMood checkoutMood;
    public static CheckInMood checkInMood;
    public static boolean isGettingDevicesData;
    private static DatabaseReference ProjectVariablesRef;


    public static void getProjectVariables(RequestQueue Q,LocalDataStore storage, RequestCallback callback) {
        if (PROJECT_VARIABLES.getIsProjectVariablesSaved(storage)) {
            Log.d("bootingOp","variables locally");
            try {
                getProjectVariablesFromStorage(storage);
                callback.onSuccess();
            } catch (JSONException e) {
                callback.onFail(e.getMessage());
            }
        }
        else {
            Log.d("bootingOp","variables internet");
            String url = MyApp.My_PROJECT.url + "roomsManagement/getProjectVariables";
            StringRequest re = new StringRequest(Request.Method.GET, url, response -> {
                Log.d("gettingProjectVariables",response);
                try {
                    PROJECT_VARIABLES.setProjectVariables(response);
                    callback.onSuccess();
                } catch (JSONException e) {
                    if (e.getMessage() != null) {
                        Log.d("gettingProjectVariables", e.getMessage());
                    }
                    callback.onFail(e.getMessage());
                }
            }, error ->{
                Log.d("gettingProjectVariables",error.toString());
                callback.onFail(error.toString());
            });
            Q.add(re);
        }
    }

    public static void getProjectVariables(PROJECT p, RequestQueue Q, RequestCallback callback) {
        String url = p.url + "roomsManagement/getProjectVariables";
        StringRequest re = new StringRequest(Request.Method.GET, url, response -> {
            Log.d("gettingProjectVariables",response);
            try {
                PROJECT_VARIABLES.setProjectVariables(response);
                callback.onSuccess();
            } catch (JSONException e) {
                if (e.getMessage() != null) {
                    Log.d("gettingProjectVariables", e.getMessage());
                }
                callback.onFail(e.getMessage());
            }
        }, error ->{
            Log.d("gettingProjectVariables",error.toString());
            callback.onFail(error.toString());
        });
        Q.add(re);
    }
    public static void setProjectVariables(String response) throws JSONException {
        JSONObject row = new JSONObject(response);
        PROJECT_VARIABLES.id = row.getInt("id");
        PROJECT_VARIABLES.projectName = row.getString("projectName");
        PROJECT_VARIABLES.Hotel = row.getInt("Hotel");
        PROJECT_VARIABLES.Temp = row.getInt("Temp");
        PROJECT_VARIABLES.Interval = row.getInt("Interval");
        PROJECT_VARIABLES.DoorWarning = row.getInt("DoorWarning");
        PROJECT_VARIABLES.CheckinModeActive = row.getInt("CheckinModeActive");
        PROJECT_VARIABLES.CheckinModeTime = row.getInt("CheckInModeTime");
        PROJECT_VARIABLES.CheckinActions = row.getString("CheckinActions");
        PROJECT_VARIABLES.CheckoutModeTime = row.getInt("CheckOutModeTime");
        PROJECT_VARIABLES.CheckoutActions = row.getString("CheckoutActions");
        PROJECT_VARIABLES.CheckoutModeActive = row.getInt("CheckoutModeActive");
        PROJECT_VARIABLES.WelcomeMessage = row.getString("WelcomeMessage");
        PROJECT_VARIABLES.Logo = row.getString("Logo");
        PROJECT_VARIABLES.PoweroffClientIn = row.getInt("PoweroffClientIn");
        PROJECT_VARIABLES.PoweroffAfterHK = row.getInt("PoweroffAfterHK");
        PROJECT_VARIABLES.ACSenarioActive = row.getInt("ACSenarioActive");
        PROJECT_VARIABLES.OnClientBack = row.getString("OnClientBack");
        PROJECT_VARIABLES.HKCleanTime = row.getInt("HKCleanupTime");
        PROJECT_VARIABLES.TuyaClientId = row.getString("TuyaClientId");
        PROJECT_VARIABLES.TuyaClientSecret = row.getString("TuyaClientSecret");
        PROJECT_VARIABLES.GuestApp = row.getInt("GuestApp");
        PROJECT_VARIABLES.PowerSaver = row.getInt("PowerSaver");
        PROJECT_VARIABLES.CheckoutTime = row.getString("CheckoutTime");
        PROJECT_VARIABLES.checkInMood = new CheckInMood(PROJECT_VARIABLES.CheckoutActions,PROJECT_VARIABLES.CheckinModeActive);
        PROJECT_VARIABLES.checkoutMood = new CheckoutMood(PROJECT_VARIABLES.CheckoutActions,PROJECT_VARIABLES.CheckoutModeActive);
        PROJECT_VARIABLES.clientBackActions = new ClientBackActions(PROJECT_VARIABLES.OnClientBack);
        setServiceSwitchButtons(new JSONObject(row.getString("ServiceSwitchButtons")));
    }

    public static void setProjectVariablesFirebaseListeners(DatabaseReference ProjectVariablesRef) {
        PROJECT_VARIABLES.ProjectVariablesRef = ProjectVariablesRef;
        ProjectVariablesRef.child("CheckinModeActive").setValue(PROJECT_VARIABLES.CheckinModeActive);
        ProjectVariablesRef.child("CheckinModeActive").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    PROJECT_VARIABLES.CheckinModeActive = Integer.parseInt(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("CheckoutModeActive").setValue(PROJECT_VARIABLES.CheckoutModeActive);
        ProjectVariablesRef.child("CheckoutModeActive").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    PROJECT_VARIABLES.CheckoutModeActive = Integer.parseInt(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("ACSenarioActive").setValue(PROJECT_VARIABLES.ACSenarioActive);
        ProjectVariablesRef.child("ACSenarioActive").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    PROJECT_VARIABLES.setAcScenarioActive(Integer.parseInt(snapshot.getValue().toString()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("CheckInModeTime").setValue(PROJECT_VARIABLES.CheckinModeTime);
        ProjectVariablesRef.child("CheckInModeTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    PROJECT_VARIABLES.CheckinModeTime = Integer.parseInt(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("CheckOutModeTime").setValue(PROJECT_VARIABLES.CheckoutModeTime);
        ProjectVariablesRef.child("CheckOutModeTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    PROJECT_VARIABLES.CheckoutModeTime = Integer.parseInt(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("CheckinActions").setValue(PROJECT_VARIABLES.CheckinActions);
        ProjectVariablesRef.child("CheckinActions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    PROJECT_VARIABLES.CheckinActions = snapshot.getValue().toString();
                    PROJECT_VARIABLES.checkInMood = new CheckInMood(PROJECT_VARIABLES.CheckinActions,PROJECT_VARIABLES.CheckinModeActive);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("CheckoutActions").setValue(PROJECT_VARIABLES.CheckoutActions);
        ProjectVariablesRef.child("CheckoutActions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    PROJECT_VARIABLES.CheckoutActions = snapshot.getValue().toString();
                    PROJECT_VARIABLES.checkoutMood = new CheckoutMood(PROJECT_VARIABLES.CheckoutActions,PROJECT_VARIABLES.CheckoutModeActive);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("DoorWarning").setValue(PROJECT_VARIABLES.DoorWarning);
        ProjectVariablesRef.child("DoorWarning").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    PROJECT_VARIABLES.DoorWarning = 1000 * 60 * Integer.parseInt(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("HKCleanupTime").setValue(PROJECT_VARIABLES.HKCleanTime);
        ProjectVariablesRef.child("HKCleanupTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    PROJECT_VARIABLES.HKCleanTime = Integer.parseInt(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("Interval").setValue(PROJECT_VARIABLES.Interval);
        ProjectVariablesRef.child("Interval").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    PROJECT_VARIABLES.Interval = 1000 * 60 * Integer.parseInt(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("PoweroffAfterHK").setValue(PROJECT_VARIABLES.PoweroffAfterHK);
        ProjectVariablesRef.child("PoweroffAfterHK").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    PROJECT_VARIABLES.PoweroffAfterHK = Integer.parseInt(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("OnClientBack").setValue(PROJECT_VARIABLES.OnClientBack);
        ProjectVariablesRef.child("OnClientBack").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    PROJECT_VARIABLES.OnClientBack = snapshot.getValue().toString();
                    PROJECT_VARIABLES.clientBackActions = new ClientBackActions(PROJECT_VARIABLES.OnClientBack);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("Temp").setValue(PROJECT_VARIABLES.Temp);
        ProjectVariablesRef.child("Temp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    PROJECT_VARIABLES.Temp = Integer.parseInt(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("RefreshSystemTime").setValue(24);
        ProjectVariablesRef.child("GettingDevicesData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    ProjectVariablesRef.child("GettingDevicesData").setValue("0");
                    PROJECT_VARIABLES.isGettingDevicesData = false;
                }
                else {
                    PROJECT_VARIABLES.isGettingDevicesData = snapshot.getValue().toString().equals("1");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ProjectVariablesRef.child("DevicesListenersWorking").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    setDevicesListenersWorking(1);
                }
                else {
                    Tuya.ListenersWorking = snapshot.getValue().toString().equals("1");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    boolean getAcSenarioActive() {
        return ACSenarioActive == 1;
    }

    public static void setAcScenarioActive(int ac) {
        ACSenarioActive = ac ;
    }

    public static boolean isAcScenarioActive() {
        return ACSenarioActive == 1;
    }

    public static void setDevicesListenersWorking(int working) {
        ProjectVariablesRef.child("DevicesListenersWorking").setValue(working);
    }

    boolean getCheckoutModeActive() {
        return CheckoutModeActive == 1;
    }

    boolean getCheckinModeActive() {
        return CheckinModeActive == 1;
    }

    public JSONObject getServiceSwitchButtons() {
        return ServiceSwitchButtons;
    }

    public static void setServiceSwitchButtons(JSONObject serviceSwitchButtons) throws JSONException {
        ServiceSwitchButtons = serviceSwitchButtons;
        if (serviceSwitchButtons.getInt("cleanup") != 0) {
            cleanupButton = serviceSwitchButtons.getInt("cleanup");
        }
        if (serviceSwitchButtons.getInt("laundry") != 0) {
            laundryButton = serviceSwitchButtons.getInt("laundry");
        }
        if (serviceSwitchButtons.getInt("dnd") != 0) {
            dndButton = serviceSwitchButtons.getInt("dnd");
        }
        if (serviceSwitchButtons.getInt("checkout") != 0) {
            checkoutButton = serviceSwitchButtons.getInt("checkout");
        }
    }

    public static boolean getIsPowerOffAfterCheckout() {
        return PoweroffAfterHK == 0 ;
    }

    public static void setGettingDevicesData(boolean status) {
        if (status) {
            ProjectVariablesRef.child("GettingDevicesData").setValue("1");
        }
        else {
            ProjectVariablesRef.child("GettingDevicesData").setValue("0");
        }
    }

    public static boolean getIsProjectVariablesSaved(LocalDataStore storage) {
        return storage.getBoolean("projectVariablesSaved");
    }

    public static void saveProjectVariablesToStorage(LocalDataStore storage) {
        storage.saveInteger(PROJECT_VARIABLES.id,"projectVariablesId");
        storage.saveInteger(PROJECT_VARIABLES.ACSenarioActive,"projectVariablesACSenarioActive");
        storage.saveInteger(PROJECT_VARIABLES.CheckinModeActive,"projectVariablesCheckinModeActive");
        storage.saveInteger(PROJECT_VARIABLES.CheckinModeTime,"projectVariablesCheckinModeTime");
        storage.saveInteger(PROJECT_VARIABLES.CheckoutModeActive,"projectVariablesCheckoutModeActive");
        storage.saveInteger(PROJECT_VARIABLES.CheckoutModeTime,"projectVariablesCheckoutModeTime");
        storage.saveInteger(PROJECT_VARIABLES.checkoutButton,"projectVariablescheckoutButton");
        storage.saveInteger(PROJECT_VARIABLES.cleanupButton,"projectVariablescleanupButton");
        storage.saveInteger(PROJECT_VARIABLES.dndButton,"projectVariablesdndButton");
        storage.saveInteger(PROJECT_VARIABLES.DoorWarning,"projectVariablesDoorWarning");
        storage.saveInteger(PROJECT_VARIABLES.GuestApp,"projectVariablesGuestApp");
        storage.saveInteger(PROJECT_VARIABLES.HKCleanTime,"projectVariablesHKCleanTime");
        storage.saveInteger(PROJECT_VARIABLES.Interval,"projectVariablesInterval");
        storage.saveInteger(PROJECT_VARIABLES.laundryButton,"projectVariableslaundryButton");
        storage.saveInteger(PROJECT_VARIABLES.PoweroffAfterHK,"projectVariablesPoweroffAfterHK");
        storage.saveInteger(PROJECT_VARIABLES.PoweroffClientIn,"projectVariablesPoweroffClientIn");
        storage.saveInteger(PROJECT_VARIABLES.PowerSaver,"projectVariablesPowerSaver");
        storage.saveInteger(PROJECT_VARIABLES.Temp,"projectVariablesTemp");
        storage.saveString(PROJECT_VARIABLES.CheckinActions,"projectVariablesCheckinActions");
        storage.saveString(PROJECT_VARIABLES.CheckoutActions,"projectVariablesCheckoutActions");
        storage.saveString(PROJECT_VARIABLES.CheckoutTime,"projectVariablesCheckoutTime");
        storage.saveString(PROJECT_VARIABLES.OnClientBack,"projectVariablesOnClientBack");
        storage.saveString(PROJECT_VARIABLES.projectName,"projectVariablesprojectName");
        storage.saveString(PROJECT_VARIABLES.Logo,"projectVariablesLogo");
        storage.saveObject(PROJECT_VARIABLES.checkInMood,"projectVariablescheckInMood");
        storage.saveObject(PROJECT_VARIABLES.checkoutMood,"projectVariablescheckoutMood");
        storage.saveObject(PROJECT_VARIABLES.clientBackActions,"projectVariablesclientBackActions");
        storage.saveObject(PROJECT_VARIABLES.ServiceSwitchButtons,"projectVariablesServiceSwitchButtons");
        storage.saveBoolean(PROJECT_VARIABLES.isGettingDevicesData,"projectVariablesisGettingDevicesData");
        storage.saveBoolean(true,"projectVariablesSaved");
    }

    public static void getProjectVariablesFromStorage(LocalDataStore storage) throws JSONException {
        PROJECT_VARIABLES.id = storage.getInteger("projectVariablesId");
        PROJECT_VARIABLES.ACSenarioActive = storage.getInteger("projectVariablesACSenarioActive");
        PROJECT_VARIABLES.CheckinModeActive = storage.getInteger("projectVariablesCheckinModeActive");
        PROJECT_VARIABLES.CheckinModeTime = storage.getInteger("projectVariablesCheckinModeTime");
        PROJECT_VARIABLES.CheckoutModeActive = storage.getInteger("projectVariablesCheckoutModeActive");
        PROJECT_VARIABLES.CheckoutModeTime = storage.getInteger("projectVariablesCheckoutModeTime");
        PROJECT_VARIABLES.checkoutButton = storage.getInteger("projectVariablescheckoutButton");
        PROJECT_VARIABLES.cleanupButton = storage.getInteger("projectVariablescleanupButton");
        PROJECT_VARIABLES.dndButton = storage.getInteger("projectVariablesdndButton");
        PROJECT_VARIABLES.DoorWarning = storage.getInteger("projectVariablesDoorWarning");
        PROJECT_VARIABLES.GuestApp = storage.getInteger("projectVariablesGuestApp");
        PROJECT_VARIABLES.HKCleanTime = storage.getInteger("projectVariablesHKCleanTime");
        PROJECT_VARIABLES.Interval = storage.getInteger("projectVariablesInterval");
        PROJECT_VARIABLES.laundryButton = storage.getInteger("projectVariableslaundryButton");
        PROJECT_VARIABLES.PoweroffAfterHK = storage.getInteger("projectVariablesPoweroffAfterHK");
        PROJECT_VARIABLES.PoweroffClientIn = storage.getInteger("projectVariablesPoweroffClientIn");
        PROJECT_VARIABLES.PowerSaver = storage.getInteger("projectVariablesPowerSaver");
        PROJECT_VARIABLES.Temp = storage.getInteger("projectVariablesTemp");
        PROJECT_VARIABLES.isGettingDevicesData = storage.getBoolean("projectVariablesisGettingDevicesData");
        PROJECT_VARIABLES.CheckinActions = storage.getString("projectVariablesCheckinActions");
        PROJECT_VARIABLES.CheckoutActions = storage.getString("projectVariablesCheckoutActions");
        PROJECT_VARIABLES.CheckoutTime = storage.getString("projectVariablesCheckoutTime");
        PROJECT_VARIABLES.OnClientBack = storage.getString("projectVariablesOnClientBack");
        PROJECT_VARIABLES.projectName = storage.getString("projectVariablesprojectName");
        PROJECT_VARIABLES.Logo = storage.getString("projectVariablesLogo");
        PROJECT_VARIABLES.checkInMood = (CheckInMood) storage.getObject("projectVariablescheckInMood", CheckInMood.class);
        PROJECT_VARIABLES.checkoutMood = (CheckoutMood) storage.getObject("projectVariablescheckoutMood", CheckoutMood.class);
        PROJECT_VARIABLES.clientBackActions = (ClientBackActions) storage.getObject("projectVariablesclientBackActions", ClientBackActions.class);
        PROJECT_VARIABLES.ServiceSwitchButtons = (JSONObject) storage.getObject("projectVariablesServiceSwitchButtons",JSONObject.class);
        setServiceSwitchButtons(PROJECT_VARIABLES.ServiceSwitchButtons);
    }

    public static void deleteProjectVariablesFromStorage(LocalDataStore storage) {
        storage.deleteObject("projectVariablesId");
        storage.deleteObject("projectVariablesACSenarioActive");
        storage.deleteObject("projectVariablesCheckinModeActive");
        storage.deleteObject("projectVariablesCheckinModeTime");
        storage.deleteObject("projectVariablesCheckoutModeActive");
        storage.deleteObject("projectVariablesCheckoutModeTime");
        storage.deleteObject("projectVariablescheckoutButton");
        storage.deleteObject("projectVariablescleanupButton");
        storage.deleteObject("projectVariablesdndButton");
        storage.deleteObject("projectVariablesDoorWarning");
        storage.deleteObject("projectVariablesGuestApp");
        storage.deleteObject("projectVariablesHKCleanTime");
        storage.deleteObject("projectVariablesInterval");
        storage.deleteObject("projectVariableslaundryButton");
        storage.deleteObject("projectVariablesPoweroffAfterHK");
        storage.deleteObject("projectVariablesPoweroffClientIn");
        storage.deleteObject("projectVariablesPowerSaver");
        storage.deleteObject("projectVariablesTemp");
        storage.deleteObject("projectVariablesCheckinActions");
        storage.deleteObject("projectVariablesCheckoutActions");
        storage.deleteObject("projectVariablesCheckoutTime");
        storage.deleteObject("projectVariablesOnClientBack");
        storage.deleteObject("projectVariablesprojectName");
        storage.deleteObject("projectVariablesLogo");
        storage.deleteObject("projectVariablescheckInMood");
        storage.deleteObject("projectVariablescheckoutMood");
        storage.deleteObject("projectVariablesclientBackActions");
        storage.deleteObject("projectVariablesServiceSwitchButtons");
        storage.deleteObject("projectVariablesisGettingDevicesData");
        storage.deleteObject("projectVariablesSaved");
    }

    public static void getServerStops(GetStops callback) {
        final int[] counter = {0};
        List<String> times= new ArrayList<>();
        final ServerStop[] stop = new ServerStop[1];
        ProjectVariablesRef.child("Stops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("counter").getValue() != null) {
                    counter[0] = Integer.parseInt(Objects.requireNonNull(snapshot.child("counter").getValue()).toString());
                }
                else {
                    ProjectVariablesRef.child("Stops").child("counter").setValue(0);
                }
                if (snapshot.child("times").getValue() != null) {
                    for (DataSnapshot s : snapshot.child("times").getChildren()) {
                        times.add(Objects.requireNonNull(s.getValue()).toString());
                    }
                }
                stop[0] = new ServerStop(counter[0],times);
                callback.onSuccess(stop[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toString());
            }
        });
    }

    public static void addServerStop() {
        ProjectVariablesRef.child("Stops").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String dateTime = Calendar.getInstance().get(Calendar.YEAR)+"-"+Calendar.getInstance().get(Calendar.MONTH)+"-"+(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+1)+" "+Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+":"+Calendar.getInstance().get(Calendar.MINUTE);
                if (snapshot.child("counter").getValue() != null) {
                    int count =  Integer.parseInt(Objects.requireNonNull(snapshot.child("counter").getValue()).toString());
                    count++;
                    ProjectVariablesRef.child("Stops").child("counter").setValue(count);
                    ProjectVariablesRef.child("Stops").child("times").child(String.valueOf(count)).setValue(dateTime);
                }
                else {
                    ProjectVariablesRef.child("Stops").child("counter").setValue(1);
                    ProjectVariablesRef.child("Stops").child("times").child("1").setValue(dateTime);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
