package com.example.cubler.foodtracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cubler on 11/26/17.
 */

public class DateDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DateEntries.db";

    private static final String DATEENTIRES_CREATE_ENTRIES =
            "CREATE TABLE " + DateEntryContract.DateEntry.TABLE_NAME + " ("
                    + DateEntryContract.DateEntry.DATE + " TEXT PRIMARY KEY,"
                    + DateEntryContract.DateEntry.FOODENTRIESJSON + " TEXT)";

    private static final String DATEENTIRES_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
            + DateEntryContract.DateEntry.TABLE_NAME;

    public DateDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATEENTIRES_CREATE_ENTRIES);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DATEENTIRES_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       onUpgrade(db, oldVersion, newVersion);
    }
}
