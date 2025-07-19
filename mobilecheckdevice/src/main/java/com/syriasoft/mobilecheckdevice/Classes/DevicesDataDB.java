package com.syriasoft.mobilecheckdevice.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import androidx.annotation.Nullable;
import com.syriasoft.mobilecheckdevice.Classes.Devices.BoolKeyValue;
import com.syriasoft.mobilecheckdevice.Classes.Devices.CheckinDevice;
import com.syriasoft.mobilecheckdevice.Classes.Devices.DeviceDP;
import com.syriasoft.mobilecheckdevice.Classes.Devices.DeviceDPBool;
import com.syriasoft.mobilecheckdevice.Classes.Devices.DeviceDPEnum;
import com.syriasoft.mobilecheckdevice.Classes.Devices.DeviceDPValue;
import com.syriasoft.mobilecheckdevice.Classes.Devices.EnumKeyValue;
import com.syriasoft.mobilecheckdevice.Classes.Devices.ValueKeyValue;
import com.syriasoft.mobilecheckdevice.Classes.Enumes.DpTypes;
import com.syriasoft.mobilecheckdevice.Interface.RequestCallback;

import java.util.ArrayList;
import java.util.List;

public class DevicesDataDB extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DevicesData";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " + FeedEntry.TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FeedEntry.Device_ID + " VARCHAR (60) ," +
                    FeedEntry.Device_Name + " VARCHAR (40)," +
                    FeedEntry.DP_Name + " VARCHAR (40)," +
                    FeedEntry.DP_ID + " INTEGER ," +
                    FeedEntry.DP_Type + " VARCHAR (40)," +
                    FeedEntry.TrueName + " VARCHAR (40)," +
                    FeedEntry.FalseName + " VARCHAR (40)," +
                    FeedEntry.Key + " VARCHAR (40)," +
                    FeedEntry.Value + " VARCHAR (40)," +
                    FeedEntry.Max + " INTEGER," +
                    FeedEntry.Min + " INTEGER," +
                    FeedEntry.Unit + " VARCHAR (40)," +
                    FeedEntry.Step + " INTEGER," +
                    FeedEntry.Scale + " INTEGER )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
    SQLiteDatabase db;

    static int ind =0;

    public DevicesDataDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        db = sqLiteDatabase;
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL("CREATE TABLE IF NOT EXISTS enums (id INTEGER PRIMARY KEY AUTOINCREMENT , dp_id INTEGER , keye VARCHAR (50) , value VARCHAR (50))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public boolean insertDeviceData(CheckinDevice cd) {
        boolean res = true;
        for (DeviceDP dp : cd.deviceDPS) {
            boolean rowRes = false;
            if (dp.dpType == DpTypes.bool) {
                rowRes = insertBooleanDp((DeviceDPBool) dp);
            }
            else if (dp.dpType == DpTypes.value) {
                rowRes = insertValueDp((DeviceDPValue) dp);
            }
            else if (dp.dpType == DpTypes.Enum) {
                rowRes = insertEnumDp((DeviceDPEnum) dp);
            }
            if (!rowRes) {
                res = false;
                break;
            }
        }
        Log.d("bootingOp","saving "+cd.device.name);
        return res;
    }

    public boolean getDeviceData(CheckinDevice cd) {
        Cursor c = db.rawQuery("SELECT * FROM "+ FeedEntry.TABLE_NAME+" WHERE "+ FeedEntry.Device_ID+" LIKE ?",new String[]{cd.device.devId});
        while(c.moveToNext()) {
            if (!getDeviceDP(c,cd)) {
                c.close();
                return false;
            }
        }
        c.close();
        return true;
    }

    boolean getDeviceDP(Cursor c,CheckinDevice cd) {
        boolean res = false;
        String type = c.getString(5);
        if(type.equals(DpTypes.bool.toString())) {
            DeviceDPBool dpb = new DeviceDPBool(c.getInt(4),c.getString(3),DpTypes.bool,cd);
            dpb.boolValues = new BoolKeyValue(c.getString(6),c.getString(7));
            res = cd.deviceDPS.add(dpb);
        }
        else if (type.equals(DpTypes.value.toString())) {
            DeviceDPValue dpb = new DeviceDPValue(c.getInt(4),c.getString(3),DpTypes.value,cd);
            dpb.valueKeyValue = new ValueKeyValue(c.getInt(11),c.getInt(10),c.getInt(13),c.getString(12),c.getInt(14));
            res = cd.deviceDPS.add(dpb);
        }
        else if (type.equals(DpTypes.Enum.toString())) {
            int dp_id = c.getInt(4);
            DeviceDPEnum dpe = new DeviceDPEnum(c.getInt(4),c.getString(3),DpTypes.Enum,cd);
            dpe.enumKeyValue = new EnumKeyValue(getDpEnums(dp_id));
            res = cd.deviceDPS.add(dpe);
        }
        return res;
    }

    boolean getDPAndWriteItToDevice(Cursor c,List<CheckinDevice> devices) {
        boolean res = false;
        String deviceId = c.getString(1);
        CheckinDevice cd = CheckinDevice.getDeviceById(devices,deviceId);
        if (cd != null) {
            String type = c.getString(5);
            if(type.equals(DpTypes.bool.toString())) {
                DeviceDPBool dpb = new DeviceDPBool(c.getInt(4),c.getString(3),DpTypes.bool,cd);
                dpb.boolValues = new BoolKeyValue(c.getString(6),c.getString(7));
                res = cd.deviceDPS.add(dpb);
            }
            else if (type.equals(DpTypes.value.toString())) {
                DeviceDPValue dpb = new DeviceDPValue(c.getInt(4),c.getString(3),DpTypes.value,cd);
                dpb.valueKeyValue = new ValueKeyValue(c.getInt(11),c.getInt(10),c.getInt(13),c.getString(12),c.getInt(14));
                res = cd.deviceDPS.add(dpb);
            }
            else if (type.equals(DpTypes.Enum.toString())) {
                int dp_id = c.getInt(4);
                DeviceDPEnum dpe = new DeviceDPEnum(c.getInt(4),c.getString(3),DpTypes.Enum,cd);
                dpe.enumKeyValue = new EnumKeyValue(getDpEnums(dp_id));
                res = cd.deviceDPS.add(dpe);
            }
            return res;
        }
        else {
            return false;
        }
    }

    public void deleteAll() {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL("DROP TABLE IF EXISTS enums");
        onCreate(db);
    }

    boolean isDevicesDataSaved() {
        Cursor c = db.query(FeedEntry.TABLE_NAME,null,null, null,null,null,null);
        boolean res = c.getCount() > 0;
        c.close();
        return res;
    }

    boolean insertBooleanDp(DeviceDPBool dp) {
        ContentValues values = new ContentValues();
        values.put(FeedEntry.Device_ID,dp.device.device.devId); // 1
        values.put(FeedEntry.Device_Name,dp.device.device.name); // 2
        values.put(FeedEntry.DP_Name,dp.dpName); //3
        values.put(FeedEntry.DP_ID,dp.dpId); // 4
        values.put(FeedEntry.DP_Type,dp.dpType.toString()); // 5
        values.put(FeedEntry.TrueName,dp.boolValues.TrueName); // 6
        values.put(FeedEntry.FalseName,dp.boolValues.FalseName); // 7
        values.put(FeedEntry.Key,""); // 8
        values.put(FeedEntry.Value,""); // 9
        values.put(FeedEntry.Max,0); // 10
        values.put(FeedEntry.Min,0); // 11
        values.put(FeedEntry.Unit,""); // 12
        values.put(FeedEntry.Step,0); // 13
        values.put(FeedEntry.Scale,0); // 14
        long x = db.insert(FeedEntry.TABLE_NAME,null,values);
        Log.d("bootingOp", "saving "+dp.dpId+" "+x);
        if (x > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    boolean insertValueDp(DeviceDPValue dp) {
        ContentValues values = new ContentValues();
        values.put(FeedEntry.Device_ID,dp.device.device.devId);
        values.put(FeedEntry.Device_Name,dp.device.device.name);
        values.put(FeedEntry.DP_Name,dp.dpName);
        values.put(FeedEntry.DP_ID,dp.dpId);
        values.put(FeedEntry.DP_Type,dp.dpType.toString());
        values.put(FeedEntry.TrueName,"");
        values.put(FeedEntry.FalseName,"");
        values.put(FeedEntry.Key,"");
        values.put(FeedEntry.Value,"");
        values.put(FeedEntry.Max,dp.valueKeyValue.max);
        values.put(FeedEntry.Min,dp.valueKeyValue.min);
        values.put(FeedEntry.Unit,dp.valueKeyValue.unit);
        values.put(FeedEntry.Step,dp.valueKeyValue.step);
        values.put(FeedEntry.Scale,dp.valueKeyValue.scale);
        long x = db.insert(FeedEntry.TABLE_NAME,null,values);
        Log.d("bootingOp", "saving "+dp.dpId+" "+x);
        if (x > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    boolean insertEnumDp(DeviceDPEnum dp) {
            ContentValues values = new ContentValues();
            values.put(FeedEntry.Device_ID,dp.device.device.devId); // 1
            values.put(FeedEntry.Device_Name,dp.device.device.name); // 2
            values.put(FeedEntry.DP_Name,dp.dpName); // 3
            values.put(FeedEntry.DP_ID,dp.dpId); // 4
            values.put(FeedEntry.DP_Type,dp.dpType.toString()); // 5
            values.put(FeedEntry.TrueName,""); // 6
            values.put(FeedEntry.FalseName,""); // 7
            values.put(FeedEntry.Key,""); // 8
            values.put(FeedEntry.Value,""); // 9
            values.put(FeedEntry.Max,0); // 10
            values.put(FeedEntry.Min,0); // 11
            values.put(FeedEntry.Unit,""); // 12
            values.put(FeedEntry.Step,0); // 13
            values.put(FeedEntry.Scale,0); // 14
            long x = db.insert(FeedEntry.TABLE_NAME,null,values);
        Log.d("bootingOp", "saving "+dp.dpId+" "+x);
            if (x > 0) {
                if (insertEnum(dp,x)) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
    }

    boolean insertEnum(DeviceDPEnum dp,long dp_id) {
        for (EnumKeyValue.enumUnit e : dp.enumKeyValue.enums) {
            ContentValues values = new ContentValues();
            values.put("dp_id",dp_id);
            values.put("keye",e.key);
            values.put("value",e.value);
            long x = db.insert("enums",null,values);
            if (x < 0) {
                return false;
            }
        }
        return true;
    }

    List<EnumKeyValue.enumUnit> getDpEnums(int dp_id) {
        List<EnumKeyValue.enumUnit> enums = new ArrayList<>();
        Cursor cc = db.rawQuery("SELECT * FROM enums WHERE dp_id = ?",new String[]{String.valueOf(dp_id)});
        while (cc.moveToNext()) {
            enums.add(new EnumKeyValue.enumUnit(cc.getString(2),cc.getString(3)));
        }
        cc.close();
        return enums;
    }

    public void getAll() {
        Cursor c = db.rawQuery("SELECT * FROM "+ FeedEntry.TABLE_NAME,null);
        c.moveToFirst();
        c.close();
    }

    public void getAllDevicesData(List<CheckinDevice> devices,RequestCallback callback) {
        Cursor c = db.rawQuery("SELECT * FROM "+ FeedEntry.TABLE_NAME,null);
        Log.d("dbCount", c.getCount()+" ");
        c.moveToFirst();
        while(c.moveToNext()) {
            boolean res = getDPAndWriteItToDevice(c,devices);
            if (!res) {
                callback.onFail("row error:  "+c.getString(2));
                break;
            }
        }
        c.close();
        callback.onSuccess();
    }

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "deviceData";
        public static final String Device_ID = "device_id";
        public static final String Device_Name = "device_name";
        public static final String DP_ID = "dpId";
        public static final String DP_Name = "dpName";
        public static final String DP_Type = "dpType";
        public static final String TrueName = "trueName";
        public static final String FalseName = "falseName";
        public static final String Min = "min";
        public static final String Max = "max";
        public static final String Unit = "unit";
        public static final String Step = "step";
        public static final String Scale = "scale";
        public static final String Key = "keye";
        public static final String Value = "value";
    }

    public static void getDevicesData(List<CheckinDevice> devices, DevicesDataDB db, RequestCallback callback) {
        try {
            int total = devices.size();
            int part = total / 4;
            int lastPart = part;
            if (total % 4 != 0) {
                lastPart++;
            }
            Log.d("bootingOp", "part " + part + " last " + lastPart);
//        for (CheckinDevice cd :devices) {
//            db.getDeviceData(cd);
//        }
            final boolean[] finish1 = {false};
            final boolean[] finish2 = {false};
            final boolean[] finish3 = {false};
            final boolean[] finish4 = {false};
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < part; i++) {
                        if (!db.getDeviceData(devices.get(i))) {
                            Log.d("bootingOp", "index " + i+" get failed");
                        }
                    }
                    finish1[0] = true;
                    if (finish2[0] && finish3[0] && finish4[0]) {
                        callback.onSuccess();
                    }
                }
            };
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    for (int i = part; i < (part * 2); i++) {
                        if (!db.getDeviceData(devices.get(i))) {
                            Log.d("bootingOp", "index " + i+" get failed");
                        }
                    }
                    finish2[0] = true;
                    if (finish1[0] && finish3[0] && finish4[0]) {
                        callback.onSuccess();
                    }
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    for (int i = (part * 2); i < (part * 3); i++) {
                        if (!db.getDeviceData(devices.get(i))) {
                            Log.d("bootingOp", "index " + i+" get failed");
                        }
                    }
                    finish3[0] = true;
                    if (finish2[0] && finish1[0] && finish4[0]) {
                        callback.onSuccess();
                    }
                }
            };
            int finalLastPart = lastPart;
            Runnable r3 = new Runnable() {
                @Override
                public void run() {
                    for (int i = (part * 3); i < finalLastPart; i++) {
                        if (!db.getDeviceData(devices.get(i))) {
                            Log.d("bootingOp", "index " + i+" get failed");
                        }
                    }
                    finish4[0] = true;
                    if (finish2[0] && finish3[0] && finish1[0]) {
                        callback.onSuccess();
                    }
                }
            };
            Thread t = new Thread(r);
            Thread t1 = new Thread(r1);
            Thread t2 = new Thread(r2);
            Thread t3 = new Thread(r3);
            t.start();
            t1.start();
            t2.start();
            t3.start();
        }
        catch (Exception e) {
            callback.onFail(e.getMessage());
        }
    }

    public static void saveDevicesData(List<CheckinDevice> devices,DevicesDataDB db,RequestCallback callback) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                for (CheckinDevice cd:devices) {
                    if(!db.insertDeviceData(cd)) {
                        callback.onFail("error");
                    }
                }
                Log.d("bootingOp","saving devices data");
                callback.onSuccess();
            }
        };
        Thread t = new Thread(r);
        t.start();
//        for (CheckinDevice cd:devices) {
//            if(!db.insertDeviceData(cd)) {
//                callback.onFail("error");
//            }
//        }
//        Log.d("bootingOp","saving devices data");
//        callback.onSuccess();
    }

    public static void saveDevicesDataThreads(List<CheckinDevice> devices,DevicesDataDB db,RequestCallback callback) {
        int total = devices.size();
        int part = total / 4;
        int lastPart = part;
        if (total % 4 != 0) {
            lastPart++;
        }

        final boolean[] finish1 = {false};
        final boolean[] finish2 = {false};
        final boolean[] finish3 = {false};
        final boolean[] finish4 = {false};
        Runnable r = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < part; i++) {
                    if (!db.insertDeviceData(devices.get(i))) {
                        Log.d("bootingOp", "index " + i+" get failed");
                    }
                }
                finish1[0] = true;
                if (finish2[0] && finish3[0] && finish4[0]) {
                    callback.onSuccess();
                }
            }
        };
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                for (int i = part; i < (part * 2); i++) {
                    if (!db.insertDeviceData(devices.get(i))) {
                        Log.d("bootingOp", "index " + i+" get failed");
                    }
                }
                finish2[0] = true;
                if (finish1[0] && finish3[0] && finish4[0]) {
                    callback.onSuccess();
                }
            }
        };
        Runnable r2 = new Runnable() {
            @Override
            public void run() {
                for (int i = (part * 2); i < (part * 3); i++) {
                    if (!db.insertDeviceData(devices.get(i))) {
                        Log.d("bootingOp", "index " + i+" get failed");
                    }
                }
                finish3[0] = true;
                if (finish2[0] && finish1[0] && finish4[0]) {
                    callback.onSuccess();
                }
            }
        };
        int finalLastPart = lastPart;
        Runnable r3 = new Runnable() {
            @Override
            public void run() {
                for (int i = (part * 3); i < finalLastPart; i++) {
                    if (!db.insertDeviceData(devices.get(i))) {
                        Log.d("bootingOp", "index " + i+" get failed");
                    }
                }
                finish4[0] = true;
                if (finish2[0] && finish3[0] && finish1[0]) {
                    callback.onSuccess();
                }
            }
        };
        Thread t = new Thread(r);
        Thread t1 = new Thread(r1);
        Thread t2 = new Thread(r2);
        Thread t3 = new Thread(r3);
        t.start();
        t1.start();
        t2.start();
        t3.start();
    }
}
