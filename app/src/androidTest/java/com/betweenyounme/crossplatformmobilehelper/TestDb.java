package com.betweenyounme.crossplatformmobilehelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by johnpearson on 4/13/15.
 */
public class TestDb extends AndroidTestCase
{
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteTheDatabase()
    {
        mContext.deleteDatabase(CrossPlatformDbHelper.DATABASE_NAME);
    }


    public void setUp()
    {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable
    {

        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(CrossPlatformMobileHelperContract.GroupEntry.TABLE_NAME);
        tableNameHashSet.add(CrossPlatformMobileHelperContract.ItemEntry.TABLE_NAME);

        mContext.deleteDatabase(CrossPlatformDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new CrossPlatformDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do
        {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + CrossPlatformMobileHelperContract.GroupEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> groupColumnHashSet = new HashSet<String>();
        groupColumnHashSet.add(CrossPlatformMobileHelperContract.GroupEntry._ID);
        groupColumnHashSet.add(CrossPlatformMobileHelperContract.GroupEntry.COLUMN_UNIQUEID);
        groupColumnHashSet.add(CrossPlatformMobileHelperContract.GroupEntry.COLUMN_TITLE);
        groupColumnHashSet.add(CrossPlatformMobileHelperContract.GroupEntry.COLUMN_SUBTITLE);
        groupColumnHashSet.add(CrossPlatformMobileHelperContract.GroupEntry.COLUMN_IMAGEPATH);
        groupColumnHashSet.add(CrossPlatformMobileHelperContract.GroupEntry.COLUMN_SUMMARY);

        int columnNameIndex = c.getColumnIndex("name");
        do
        {
            String columnName = c.getString(columnNameIndex);
            groupColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                groupColumnHashSet.isEmpty());
        db.close();

    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        location database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can uncomment out the "createNorthPoleLocationValues" function.  You can
        also make use of the ValidateCurrentRecord function from within TestUtilities.
    */
    public void testGroupTable() {
        insertGroup();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createWeatherValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */
    public void testItemTable() {
        // First insert the location, and then use the locationRowId to insert
        // the weather. Make sure to cover as many failure cases as you can.

        // Instead of rewriting all of the code we've already written in testLocationTable
        // we can move this code to insertLocation and then call insertLocation from both
        // tests. Why move it? We need the code to return the ID of the inserted location
        // and our testLocationTable can only return void because it's a test.

        long groupRowId = insertGroup();

        // Make sure we have a valid row ID.
        assertFalse("Error: Group Not Inserted Correctly", groupRowId == -1L);

        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        CrossPlatformDbHelper dbHelper = new CrossPlatformDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step (Weather): Create weather values
        ContentValues itemValues = TestUtilities.createItemValues(groupRowId);

        // Third Step (Weather): Insert ContentValues into database and get a row ID back
        long weatherRowId = db.insert(CrossPlatformMobileHelperContract.ItemEntry.TABLE_NAME, null, itemValues);
        assertTrue(weatherRowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor itemCursor = db.query(
                CrossPlatformMobileHelperContract.ItemEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue( "Error: No Records returned from location query", itemCursor.moveToFirst() );

        // Fifth Step: Validate the location Query
        TestUtilities.validateCurrentRecord("testInsertReadDb weatherEntry failed to validate",
                itemCursor, itemValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from weather query",
                itemCursor.moveToNext() );

        // Sixth Step: Close cursor and database
        itemCursor.close();
        dbHelper.close();
    }


    /*
        Students: This is a helper method for the testWeatherTable quiz. You can move your
        code from testLocationTable to here so that you can call this code from both
        testWeatherTable and testLocationTable.
     */
    public long insertGroup() {
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        CrossPlatformDbHelper dbHelper = new CrossPlatformDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues testValues = TestUtilities.createGroupValues();

        // Third Step: Insert ContentValues into database and get a row ID back
        long groupRowId;
        groupRowId = db.insert(CrossPlatformMobileHelperContract.GroupEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(groupRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                CrossPlatformMobileHelperContract.GroupEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from Group query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Group Query Validation Failed",
                cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from Group query",
                cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return groupRowId;
    }
}

