package com.thahaseen.timeoff;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thahaseen on 7/10/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper{

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "timeoff";

    // table name
    private static final String TABLE_TIME_OFF = "timedata";

    private static final String ID  = "id";
    private static final String USERNAME  = "username";
    private static final String FULL_NAME  = "name";
    private static final String DEPARTMENT  = "dept";
    private static final String LOCATION  = "location";
    private static final String REASON  = "reason";
    private static final String REMARKS  = "remarks";
    private static final String TIME_LATE  = "timelate";
    private static final String DATA_SYNC  = "datasync  ";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_TIME_OFF + "("
                + ID + " INTEGER PRIMARY KEY,"
                + USERNAME + " TEXT,"
                + FULL_NAME + " TEXT,"
                + DEPARTMENT + " TEXT,"
                + LOCATION + " TEXT,"
                + REASON + " TEXT,"
                + REMARKS + " TEXT,"
                + TIME_LATE + " INTEGER,"
                + DATA_SYNC + " TEXT)";

        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addTimeOff(TimeOffItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USERNAME, item.getUsername());
        values.put(FULL_NAME, item.getName());
        values.put(DEPARTMENT, item.getDept());
        values.put(LOCATION, item.getLocation());
        values.put(REASON, item.getReason());
        values.put(REMARKS, item.getRemarks());
        values.put(TIME_LATE, item.getTimelate());
        values.put(DATA_SYNC, item.getDatasync());
        // Inserting Row
        db.insert(TABLE_TIME_OFF, null, values);
        db.close(); // Closing database connection
    }

    public List<TimeOffItem> getUnSyncedData()
    {
        List<TimeOffItem> items = new ArrayList<TimeOffItem>();
        String selectQuery = "SELECT * FROM " + TABLE_TIME_OFF + " where " + DATA_SYNC + " ='N'";
        Log.i("getUnSyncedData", "getUnSyncedData:: " + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do{
                TimeOffItem item = new TimeOffItem();
                item.setId((c.getInt(c.getColumnIndex(ID))));
                item.setUsername((c.getString(c.getColumnIndex(USERNAME))));
                item.setName((c.getString(c.getColumnIndex(FULL_NAME))));
                item.setDept((c.getString(c.getColumnIndex(DEPARTMENT))));
                item.setLocation((c.getString(c.getColumnIndex(LOCATION))));
                item.setReason((c.getString(c.getColumnIndex(REASON))));
                item.setRemarks((c.getString(c.getColumnIndex(REMARKS))));
                item.setTimelate((c.getInt(c.getColumnIndex(TIME_LATE))));
                items.add(item);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return items;
    }
    public void updateDataSyncDone(int id)
    {
        String Query = "update " + TABLE_TIME_OFF +" set " + DATA_SYNC + "='Y' where " + ID + "="+id;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(Query);
        db.close();
    }
}
