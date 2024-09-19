package com.example.hotelservicesstandalone.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.hotelservicesstandalone.Classes.Devices.BoolKeyValue;
import com.example.hotelservicesstandalone.Classes.Devices.CheckinDevice;
import com.example.hotelservicesstandalone.Classes.Devices.DeviceDP;
import com.example.hotelservicesstandalone.Classes.Devices.DeviceDPBool;
import com.example.hotelservicesstandalone.Classes.Devices.DeviceDPEnum;
import com.example.hotelservicesstandalone.Classes.Devices.DeviceDPValue;
import com.example.hotelservicesstandalone.Classes.Devices.EnumKeyValue;
import com.example.hotelservicesstandalone.Classes.Devices.ValueKeyValue;
import com.example.hotelservicesstandalone.Classes.Enumes.DpTypes;

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

    public void insertDeviceData(CheckinDevice cd) {
        for (DeviceDP dp : cd.deviceDPS) {
            if (dp.dpType == DpTypes.bool) {
                insertBooleanDp((DeviceDPBool) dp);
            }
            else if (dp.dpType == DpTypes.value) {
                insertValueDp((DeviceDPValue) dp);
            }
            else if (dp.dpType == DpTypes.Enum) {
                insertEnumDp((DeviceDPEnum) dp);
            }
        }
        Log.d("bootingOp","saving "+cd.device.name);
    }

    public void getDeviceData(CheckinDevice cd) {
        Cursor c = db.rawQuery("SELECT * FROM "+FeedEntry.TABLE_NAME+" WHERE "+FeedEntry.Device_ID+" LIKE ?",new String[]{cd.device.devId});
        Log.d("gettingDeviceData", cd.device.name+" "+c.getCount());
        while(c.moveToNext()) {
            getDeviceDP(c,cd);
        }
        c.close();
        Log.d("gettingDeviceData", cd.device.name+" "+cd.deviceDPS.size());
    }

    void getDeviceDP(Cursor c,CheckinDevice cd) {
        String type = c.getString(5);
        if(type.equals(DpTypes.bool.toString())) {
            DeviceDPBool dpb = new DeviceDPBool(c.getInt(4),c.getString(3),DpTypes.bool,cd);
            dpb.boolValues = new BoolKeyValue(c.getString(6),c.getString(7));
            cd.deviceDPS.add(dpb);
        }
        else if (type.equals(DpTypes.value.toString())) {
            DeviceDPValue dpb = new DeviceDPValue(c.getInt(4),c.getString(3),DpTypes.value,cd);
            dpb.valueKeyValue = new ValueKeyValue(c.getInt(11),c.getInt(10),c.getInt(13),c.getString(12),c.getInt(14));
            cd.deviceDPS.add(dpb);
        }
        else if (type.equals(DpTypes.Enum.toString())) {
            int dp_id = c.getInt(4);
            DeviceDPEnum dpe = new DeviceDPEnum(c.getInt(4),c.getString(3),DpTypes.Enum,cd);
            dpe.enumKeyValue = new EnumKeyValue(getDpEnums(dp_id));
            cd.deviceDPS.add(dpe);
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

    void insertBooleanDp(DeviceDPBool dp) {
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
    }

    void insertValueDp(DeviceDPValue dp) {
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
    }

    void insertEnumDp(DeviceDPEnum dp) {
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
            insertEnum(dp,x);
            Log.d("bootingOp", "saving "+dp.dpId+" "+x);
    }

    void insertEnum(DeviceDPEnum dp,long dp_id) {
        for (EnumKeyValue.enumUnit e : dp.enumKeyValue.enums) {
            ContentValues values = new ContentValues();
            values.put("dp_id",dp_id);
            values.put("keye",e.key);
            values.put("value",e.value);
            db.insert("enums",null,values);
        }
    }

    List<EnumKeyValue.enumUnit> getDpEnums(int dp_id) {
        List<EnumKeyValue.enumUnit> enums = new ArrayList<>();
        Cursor cc = db.rawQuery("SELECT * FROM enums WHERE dp_id = ?",new String[]{String.valueOf(dp_id)});
        while (cc.moveToNext()) {
            enums.add(new EnumKeyValue.enumUnit(cc.getString(2),cc.getString(3)));
        }
        return enums;
    }

    public void getAll() {
        Cursor c = db.rawQuery("SELECT * FROM "+FeedEntry.TABLE_NAME,null);
        Log.d("dbCount", c.getCount()+" ");
        c.moveToFirst();
        while(c.moveToNext()) {
            Log.d("dbCount", c.getPosition()+" "+c.getString(0)+" "+c.getString(1)+" "+c.getString(2)+" "+c.getString(3)+" "+c.getInt(4)+" "+c.getString(5)+" "+c.getString(6)+" "+c.getString(7)+" "+c.getString(8)+" "+c.getString(9)+" "+c.getString(10)+" "+c.getString(11)+" "+c.getString(12)+" "+c.getString(13)+" "+c.getString(14));
        }
        c.close();
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

    public static void getDevicesData(List<CheckinDevice> devices,DevicesDataDB db) {
        for (CheckinDevice cd :devices) {
            db.getDeviceData(cd);
        }
    }

    public static void saveDevicesData(List<CheckinDevice> devices,DevicesDataDB db) {
        for (CheckinDevice cd:devices) {
            db.insertDeviceData(cd);
        }
        Log.d("bootingOp","saving devices data");
    }
}
