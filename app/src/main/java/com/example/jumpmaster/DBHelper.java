package com.example.jumpmaster;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "JumpMaster.db";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE games " + "(id INTEGER PRIMARY KEY, playerX REAL, playerY REAL, selectedLevel INTEGER, topLevel INTEGER, numOfJumps INTEGER, numOfFalls INTEGER, elapsedTimeSec INTEGER, name TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS games");
        onCreate(db);
    }

    public boolean createGame(String name, float playerX, float playerY, int selectedLevel, int numOfJumps, int numOfFalls, int elapsedTimeSec, int topLevel)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("playerX", playerX);
        contentValues.put("playerY", playerY);
        contentValues.put("selectedLevel", selectedLevel);
        contentValues.put("numOfJumps", numOfJumps);
        contentValues.put("numOfFalls", numOfFalls);
        contentValues.put("topLevel", topLevel);
        contentValues.put("elapsedTimeSec", elapsedTimeSec);
        long insertedId = db.insert("games", null, contentValues);
        if (insertedId == -1) return false;
        return true;
    }

    public boolean deleteGame (int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM games WHERE id =" + id);
        return true;
    }

    public boolean updateGame (float playerX, float playerY, int selectedLevel, int numOfJumps, int numOfFalls, int elapsedTimeSec, int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("UPDATE items SET playerX ='" + playerX + "', playerY ='" + playerY + "', selectedLevel ='" + selectedLevel + "', numOfJumps ='" + numOfJumps + "', numOfFalls ='" + numOfFalls + "', elapsedTimeSec ='" + elapsedTimeSec + "' WHERE id =" + id);
        return true;
    }

    public Cursor loadGame (int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from games where id=" + id + "", null);
        return res;
    }

    public Cursor getTotalStats (){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select sum(numOfJumps), sum(numOfFalls), sum(elapsedTimeSec) from games", null);
        return res;
    }

    public ArrayList<String> getGameList()
    {
        ArrayList<String> arrayList = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from games", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            String name = res.getString(res.getColumnIndex("name"));
            int id = res.getInt(0);
            arrayList.add("#" + id + " " + name);
            res.moveToNext();
        }

        return arrayList;
    }
}
