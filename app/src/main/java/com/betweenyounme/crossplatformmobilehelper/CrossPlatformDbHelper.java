package com.betweenyounme.crossplatformmobilehelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.betweenyounme.crossplatformmobilehelper.CrossPlatformMobileHelperContract.GroupEntry;
import com.betweenyounme.crossplatformmobilehelper.CrossPlatformMobileHelperContract.ItemEntry;

/**
 * Created by johnpearson on 4/13/15.
 */
public class CrossPlatformDbHelper extends SQLiteOpenHelper
{

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "crossplatform.db";

    public CrossPlatformDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {

        final String SQL_CREATE_GROUP_TABLE = "CREATE TABLE " + GroupEntry.TABLE_NAME + " (" +
                GroupEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                GroupEntry.COLUMN_UNIQUEID + " TEXT UNIQUE NOT NULL, " +
                GroupEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                GroupEntry.COLUMN_SUBTITLE + " TEXT NOT NULL, " +
                GroupEntry.COLUMN_IMAGEPATH + " TEXT NOT NULL, " +
                GroupEntry.COLUMN_SUMMARY + " TEXT NOT NULL, " +
                "UNIQUE (" + GroupEntry.COLUMN_UNIQUEID + ") ON CONFLICT IGNORE"+
                " );";

        final String SQL_CREATE_ITEM_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + " (" +

                ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ItemEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                ItemEntry.COLUMN_UNIQUEID + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_SUBTITLE + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_IMAGEPATH + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_SUMMARY + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +

                " FOREIGN KEY (" + ItemEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                GroupEntry.TABLE_NAME + " (" + GroupEntry._ID + ") " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_GROUP_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GroupEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
