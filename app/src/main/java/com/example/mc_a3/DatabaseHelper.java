package com.example.mc_a3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String dbname = "database_helper";
    private static final String tablename = "Sensors";
    private static final String COL1 = "ID";
    private static final String COL2 = "Xaxis";
    private static final String COL3 = "Yaxis";
    private static final String COL4 = "Zaxis";
    private static final String COL5 = "Latitude";
    private static final String COL6 = "Longitude";
    private static final String COL7 = "APName";
    private static final String COL8 = "APStrength";
    private static final String COL9 = "Audiopath";
    private static final String COL10 = "TimeStamp";

    DatabaseHelper(Context context) {
        super(context,dbname,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + tablename + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL2 + " VARCHAR(255) ,"+COL3 + " VARCHAR(225) ," + COL4 + " VARCHAR(225) ,"+COL5+" VARCHAR(225) ,"+COL6+" VARCHAR(225) ,"+COL7+" VARCHAR(225) ,"+COL8+" VARCHAR(225) ," +COL9+" VARCHAR(255) ," + COL10 +" VARCHAR(255));";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ tablename);
        onCreate(sqLiteDatabase);

    }

    public boolean addText (String X,String Y,String Z,String Lat,String Long,String APname,String APstrenght,String location, String time)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2,X);
        contentValues.put(COL3,Y);
        contentValues.put(COL4,Z);
        contentValues.put(COL5,Lat);
        contentValues.put(COL6,Long);
        contentValues.put(COL7,APname);
        contentValues.put(COL8,APstrenght);
        contentValues.put(COL9,location);
        contentValues.put(COL10,time);
        //contentValues.put(COL2,X);

        long result = sqLiteDatabase.insert(tablename,null,contentValues);

        if (result == -1)
            return false;
        else
            return true;
    }

    public List<DataModel> getAllText(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        List<DataModel> arrayList = new ArrayList<>();
        DataModel data = new DataModel();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from "+tablename,null);

        Log.d("SarthakAditya", "Data Recieved is = " + cursor);

        while (cursor.moveToNext())
        {
            data.setX(cursor.getString(cursor.getColumnIndex(COL2)));
            data.setY(cursor.getString(cursor.getColumnIndex(COL3)));
            data.setZ(cursor.getString(cursor.getColumnIndex(COL4)));
            data.setLat(cursor.getString(cursor.getColumnIndex(COL5)));
            data.setLong(cursor.getString(cursor.getColumnIndex(COL6)));
            data.setAPnam(cursor.getString(cursor.getColumnIndex(COL7)));
            data.setAPstrength(cursor.getString(cursor.getColumnIndex(COL8)));
            data.setLocation(cursor.getString(cursor.getColumnIndex(COL9)));
            data.setTime(cursor.getString(cursor.getColumnIndex(COL10)));
            arrayList.add(data);
        }
        return arrayList;
    }
}
