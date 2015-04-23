package com.betweenyounme.crossplatformmobilehelper;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.betweenyounme.crossplatformmobilehelper.CrossPlatformMobileHelperContract.GroupEntry;
import com.betweenyounme.crossplatformmobilehelper.CrossPlatformMobileHelperContract.ItemEntry;

/**
 * Created by johnpearson on 4/14/15.
 */
public class TestProvider extends AndroidTestCase
{
    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.

       Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written
       the delete functionality in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                ItemEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                GroupEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                ItemEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Weather table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                GroupEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Location table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /*
        This test checks to make sure that the content provider is registered correctly.
        Students: Uncomment this test to make sure you've correctly registered the CrossPlatformContentProvider.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // CrossPlatformContentProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                CrossPlatformContentProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: CrossPlatformContentProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + CrossPlatformMobileHelperContract.CONTENT_AUTHORITY,
                    providerInfo.authority, CrossPlatformMobileHelperContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: CrossPlatformContentProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
            This test doesn't touch the database.  It verifies that the ContentProvider returns
            the correct type for each type of URI that it can handle.
            Students: Uncomment this test to verify that your implementation of GetType is
            functioning correctly.
         */
    public void testGetType() {
        // content://com.example.android.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(ItemEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the ItemEntry CONTENT_URI should return ItemEntry.CONTENT_TYPE",
                ItemEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(
                ItemEntry.buildItemUri(TestUtilities.TEST_RECORD));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the ItemEntry CONTENT_URI with group should return ItemEntry.CONTENT_ITEM_TYPE",
                ItemEntry.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(
                ItemEntry.buildItemForGroupUri(TestUtilities.TEST_RECORD));
        assertEquals("Error: the ItemEntry CONTENT_URI with GroupId should return ItemEntry.CONTENT_ITEM",
                ItemEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(GroupEntry.CONTENT_URI);
        assertEquals("Error: the GroupEntry CONTENT_URI should return GroupEntry.CONTENT_TYPE",
                GroupEntry.CONTENT_TYPE, type);

        Object foo = GroupEntry.buildGroupUri(TestUtilities.TEST_RECORD);

        type = mContext.getContentResolver().getType(
                GroupEntry.buildGroupUri(TestUtilities.TEST_RECORD));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the GroupEntry CONTENT_URI should return GroupEntry.CONTENT_ITEM_TYPE",
                GroupEntry.CONTENT_ITEM_TYPE, type);


    }


    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if the basic weather query functionality
        given in the ContentProvider is working correctly.
     */
    public void testBasicItemQuery() {
        // insert our test records into the database
        CrossPlatformDbHelper dbHelper = new CrossPlatformDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createGroupValues();
        long locationRowId = TestUtilities.insertGroupValues(mContext);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues itemValues = TestUtilities.createItemValues(locationRowId);

        long itemRowId = db.insert(ItemEntry.TABLE_NAME, null, itemValues);
        assertTrue("Unable to Insert ItemEntry into the Database", itemRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor itemCursor = mContext.getContentResolver().query(
                ItemEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicItemQuery", itemCursor, itemValues);
    }

    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if your location queries are
        performing correctly.
     */
    public void testBasicGroupQueries() {
        // insert our test records into the database
        CrossPlatformDbHelper dbHelper = new CrossPlatformDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createGroupValues();
        long groupRowId = TestUtilities.insertGroupValues(mContext);

        // Test the basic content provider query
        Cursor groupCursor = mContext.getContentResolver().query(
                GroupEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicGroupQueries, group query", groupCursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Location Query did not properly set NotificationUri",
                    groupCursor.getNotificationUri(), GroupEntry.CONTENT_URI);
        }
    }

    /*
        This test uses the provider to insert and then update the data. Uncomment this test to
        see if your update location is functioning correctly.
     */
    public void testUpdateGroup() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createGroupValues();

        Uri groupUri = mContext.getContentResolver().
                insert(GroupEntry.CONTENT_URI, values);
        long groupRowId = ContentUris.parseId(groupUri);

        // Verify we got a row back.
        assertTrue(groupRowId != -1);
        Log.d(LOG_TAG, "New row id: " + groupRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(GroupEntry._ID, groupRowId);
        updatedValues.put(GroupEntry.COLUMN_SUBTITLE, "Santa's Village");

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor groupCursor = mContext.getContentResolver().query(GroupEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        groupCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                GroupEntry.CONTENT_URI, updatedValues, GroupEntry._ID + "= ?",
                new String[] { Long.toString(groupRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        groupCursor.unregisterContentObserver(tco);
        groupCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                GroupEntry.CONTENT_URI,
                null,   // projection
                GroupEntry._ID + " = " + groupRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateGroup.  Error validating group entry update.",
                cursor, updatedValues);

        cursor.close();
    }


    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the insert functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createGroupValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
//        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
//        mContext.getContentResolver().registerContentObserver(GroupEntry.CONTENT_URI, true, tco);
        Uri groupUri = mContext.getContentResolver().insert(GroupEntry.CONTENT_URI, testValues);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
//        tco.waitForNotificationOrFail();
//        mContext.getContentResolver().unregisterContentObserver(tco);

        long groupRowId = ContentUris.parseId(groupUri);

        // Verify we got a row back.
        assertTrue(groupRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                GroupEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating GroupEntry.",
                cursor, testValues);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues itemValues = TestUtilities.createItemValues(groupRowId);
        // The TestContentObserver is a one-shot class
//        tco = TestUtilities.getTestContentObserver();

        //mContext.getContentResolver().registerContentObserver(ItemEntry.CONTENT_URI, true, tco);

        Uri itemInsertUri = mContext.getContentResolver()
                .insert(ItemEntry.CONTENT_URI, itemValues);
        assertTrue(itemInsertUri != null);

        // Did our content observer get called?  Students:  If this fails, your insert weather
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
//        tco.waitForNotificationOrFail();
//        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor itemCursor = mContext.getContentResolver().query(
                ItemEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating ItemEntry insert.",
                itemCursor, itemValues);

        // Add the location values in with the weather data so that we can make
        // sure that the join worked and we actually get all the values back
        itemValues.putAll(testValues);

        // Get the joined Weather and Location data
        itemCursor = mContext.getContentResolver().query(
                ItemEntry.buildItemForGroupUri(groupRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Item and Group Data.",
                itemCursor, itemValues);

    }

    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the delete functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testDeleteRecords() {
        testInsertReadProvider();



        // Register a content observer for our location delete.
//        TestUtilities.TestContentObserver groupObserver = TestUtilities.getTestContentObserver();
//        mContext.getContentResolver().registerContentObserver(GroupEntry.CONTENT_URI, true, groupObserver);
//
//        // Register a content observer for our weather delete.
//        TestUtilities.TestContentObserver itemObserver = TestUtilities.getTestContentObserver();
//        mContext.getContentResolver().registerContentObserver(ItemEntry.CONTENT_URI, true, itemObserver);

        deleteAllRecordsFromProvider();

        // Students: If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
//        groupObserver.waitForNotificationOrFail();
//        itemObserver.waitForNotificationOrFail();
//
//        mContext.getContentResolver().unregisterContentObserver(groupObserver);
//        mContext.getContentResolver().unregisterContentObserver(itemObserver);

    }


    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertItemValues(long groupRowId) {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        String uniqueid = "foo";
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues itemValues = new ContentValues();
            itemValues.put(CrossPlatformMobileHelperContract.ItemEntry.COLUMN_LOC_KEY, groupRowId);
            itemValues.put(ItemEntry.COLUMN_UNIQUEID, uniqueid + new Integer(i).toString());
            itemValues.put(ItemEntry.COLUMN_TITLE, uniqueid + new Integer(i).toString());
            itemValues.put(ItemEntry.COLUMN_SUBTITLE, uniqueid + new Integer(i).toString());
            itemValues.put(ItemEntry.COLUMN_IMAGEPATH, uniqueid + new Integer(i).toString());
            itemValues.put(ItemEntry.COLUMN_SUMMARY, uniqueid + new Integer(i).toString());
            itemValues.put(ItemEntry.COLUMN_CONTENT, uniqueid + new Integer(i).toString());
            returnContentValues[i] = itemValues;
        }
        return returnContentValues;
    }

    // Student: Uncomment this test after you have completed writing the BulkInsert functionality
    // in your provider.  Note that this test will work with the built-in (default) provider
    // implementation, which just inserts records one-at-a-time, so really do implement the
    // BulkInsert ContentProvider function.
    public void testBulkInsert() {
        // first, let's create a location value
        ContentValues testValues = TestUtilities.createGroupValues();
        Uri groupUri = mContext.getContentResolver().insert(GroupEntry.CONTENT_URI, testValues);
        long groupRowId = ContentUris.parseId(groupUri);

        // Verify we got a row back.
        assertTrue(groupRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                GroupEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating GroupEntry.",
                cursor, testValues);

        // Now we can bulkInsert some weather.  In fact, we only implement BulkInsert for weather
        // entries.  With ContentProviders, you really only have to implement the features you
        // use, after all.
        ContentValues[] bulkInsertContentValues = createBulkInsertItemValues(groupRowId);

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver crossPlatformObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ItemEntry.CONTENT_URI, true, crossPlatformObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(ItemEntry.CONTENT_URI, bulkInsertContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        crossPlatformObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(crossPlatformObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        cursor = mContext.getContentResolver().query(
                ItemEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating ItemEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }
}
