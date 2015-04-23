package com.betweenyounme.crossplatformmobilehelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by johnpearson on 4/13/15.
 */
public class TestUtilities extends AndroidTestCase
{

    static final long TEST_RECORD = 1;

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /* "UniqueId": "ProgressRing",
                    "Title": "Windows 8.1",
                    "Subtitle": "ProgressRing",
                    "ImagePath": "WindowsOnly300x225.png",
                    "Summary": "ProgressRing",
                    "Content": "ProgressRing"
     */
    static ContentValues createItemValues(long groupRowId) {
        ContentValues itemValues = new ContentValues();
        itemValues.put(CrossPlatformMobileHelperContract.ItemEntry.COLUMN_LOC_KEY, groupRowId);
        itemValues.put(CrossPlatformMobileHelperContract.ItemEntry.COLUMN_UNIQUEID, "ProgressRing");
        itemValues.put(CrossPlatformMobileHelperContract.ItemEntry.COLUMN_TITLE, "Windows 8.1");
        itemValues.put(CrossPlatformMobileHelperContract.ItemEntry.COLUMN_SUBTITLE, "ProgressRing");
        itemValues.put(CrossPlatformMobileHelperContract.ItemEntry.COLUMN_IMAGEPATH, "WindowsOnly300x225.png");
        itemValues.put(CrossPlatformMobileHelperContract.ItemEntry.COLUMN_SUMMARY, "ProgressRing");
        itemValues.put(CrossPlatformMobileHelperContract.ItemEntry.COLUMN_CONTENT, "ProgressRing");

        return itemValues;
    }

    /*
        "UniqueId": "Activity indicator",
                "Title": "Activity indicator",
                "Subtitle": "iOS and Windows 8  and Android",
                "ImagePath": "Windows300x225All.png",
                "Summary": "None",
     */
    static ContentValues createGroupValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(CrossPlatformMobileHelperContract.GroupEntry.COLUMN_UNIQUEID, "Activity indicator");
        testValues.put(CrossPlatformMobileHelperContract.GroupEntry.COLUMN_TITLE, "Activity indicator");
        testValues.put(CrossPlatformMobileHelperContract.GroupEntry.COLUMN_SUBTITLE, "iOS and Windows 8  and Android");
        testValues.put(CrossPlatformMobileHelperContract.GroupEntry.COLUMN_IMAGEPATH, "Windows300x225All.png");
        testValues.put(CrossPlatformMobileHelperContract.GroupEntry.COLUMN_SUMMARY, "Windows300x225All.png");
        return testValues;
    }

    /*
        Students: You can uncomment this function once you have finished creating the
        LocationEntry part of the WeatherContract as well as the WeatherDbHelper.
     */
    static long insertGroupValues(Context context) {
        // insert our test records into the database
        CrossPlatformDbHelper dbHelper = new CrossPlatformDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createGroupValues();

        long groupRowId;
        groupRowId = db.insert(CrossPlatformMobileHelperContract.GroupEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Group Values", groupRowId != -1);

        return groupRowId;
    }

    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
