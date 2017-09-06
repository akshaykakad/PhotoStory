package com.hfad.photostory;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;

public class PhotoStoryDatabaseHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "photoStory";
    private static final int DB_VERSION = 3;

    PhotoStoryDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE STORY_DETAILS("
        + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
        + "NAME TEXT);");
        insertRecords(db, "Trip to Lonavala");
        insertRecords(db, "Fun at NH-37");
        insertRecords(db, "Pizza party in Office");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        if(oldVersion == 1) {
            db.execSQL("ALTER TABLE STORY_DETAILS ADD COLUMN FAVORITE NUMERIC;");
            db.execSQL("ALTER TABLE STORY_DETAILS ADD COLUMN SYNOPSIS TEXT;");
        }
        else if(oldVersion == 2){
            db.execSQL("ALTER TABLE STORY_DETAILS ADD COLUMN SYNOPSIS TEXT;");
        }
    }

    private static void insertRecords(SQLiteDatabase db, String name){
        ContentValues stories = new ContentValues();
        stories.put("NAME", name);
        db.insert("STORY_DETAILS", null, stories);
    }
}
