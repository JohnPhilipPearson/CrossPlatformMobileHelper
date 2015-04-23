package com.betweenyounme.crossplatformmobilehelper;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by johnpearson on 4/14/15.
 */
public class TestUriMatcher extends AndroidTestCase
{
    private static final String ALL_GROUPS_QUERY = "*";
    private static final long GROUP_QUERY = 0L;
    private static final long ITEM_QUERY = 0L;

    // content://com.example.android.sunshine.app/weather"
    private static final Uri TEST_ITEM_DIR = CrossPlatformMobileHelperContract.ItemEntry.CONTENT_URI;
    private static final Uri TEST_ITEM_WITH_GROUP_DIR = CrossPlatformMobileHelperContract.ItemEntry.buildItemForGroupUri(GROUP_QUERY);
    private static final Uri TEST_ITEM = CrossPlatformMobileHelperContract.ItemEntry.buildItemUri(ITEM_QUERY);


    private static final Uri TEST_GROUP_DIR = CrossPlatformMobileHelperContract.GroupEntry.CONTENT_URI;
    private static final Uri TEST_GROUP = CrossPlatformMobileHelperContract.GroupEntry.buildGroupUri(GROUP_QUERY);
    private static final Uri TEST_GROUPS = CrossPlatformMobileHelperContract.GroupEntry.buildAllGroupsUri();

    /*
        Students: This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.  Uncomment this when you are
        ready to test your UriMatcher.
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = CrossPlatformContentProvider.buildUriMatcher();

        assertEquals("Error: The ITEM URI was matched incorrectly.",
                testMatcher.match(TEST_ITEM_DIR), CrossPlatformContentProvider.ITEMDIR);

        assertEquals("Error: The ITEM WITH GROUP URI was matched incorrectly.",
                testMatcher.match(TEST_ITEM_WITH_GROUP_DIR), CrossPlatformContentProvider.ITEM_WITH_GROUP);
        assertEquals("Error: The ITEM QUERY URI was matched incorrectly.",
                testMatcher.match(TEST_ITEM), CrossPlatformContentProvider.ITEM);

        assertEquals("Error: The TEST_GROUP_DIR URI was matched incorrectly.",
                testMatcher.match(TEST_GROUP_DIR), CrossPlatformContentProvider.GROUPDIR);


        assertEquals("Error: The TEST_GROUP URI was matched incorrectly.",
                testMatcher.match(TEST_GROUP), CrossPlatformContentProvider.GROUP);
        assertEquals("Error: The TEST_GROUPS URI was matched incorrectly.",
                testMatcher.match(TEST_GROUPS), CrossPlatformContentProvider.GROUPS);
    }
}