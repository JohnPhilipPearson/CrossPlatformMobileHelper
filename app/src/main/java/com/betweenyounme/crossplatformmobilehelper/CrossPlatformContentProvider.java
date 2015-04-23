package com.betweenyounme.crossplatformmobilehelper;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by johnpearson on 4/13/15.
 */
public class CrossPlatformContentProvider extends ContentProvider
{
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private CrossPlatformDbHelper mOpenHelper;

    static final int ITEM = 100;
    static final int ITEM_WITH_GROUP = 101;
    static final int ITEMDIR = 102;
    static final int GROUP = 300;
    static final int GROUPS = 301;
    static final int GROUPDIR = 302;



    private static final SQLiteQueryBuilder sItemByGroupJoinGroupidQueryBuilder;

    static{
        sItemByGroupJoinGroupidQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sItemByGroupJoinGroupidQueryBuilder.setTables(
                CrossPlatformMobileHelperContract.ItemEntry.TABLE_NAME + " INNER JOIN " +
                        CrossPlatformMobileHelperContract.GroupEntry.TABLE_NAME +
                        " ON " + CrossPlatformMobileHelperContract.ItemEntry.TABLE_NAME +
                        "." + CrossPlatformMobileHelperContract.ItemEntry.COLUMN_LOC_KEY +
                        " = " + CrossPlatformMobileHelperContract.GroupEntry.TABLE_NAME +
                        "." + CrossPlatformMobileHelperContract.GroupEntry._ID);
    }

    //location.location_setting = ?
    private static final String sGroupIDSelection =
            CrossPlatformMobileHelperContract.GroupEntry.TABLE_NAME+
                    "." + CrossPlatformMobileHelperContract.GroupEntry._ID + " = ? ";

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ITEMDIR:
                return CrossPlatformMobileHelperContract.ItemEntry.CONTENT_TYPE;
            case ITEM_WITH_GROUP:
                return CrossPlatformMobileHelperContract.ItemEntry.CONTENT_TYPE;
            case ITEM:
                return CrossPlatformMobileHelperContract.ItemEntry.CONTENT_ITEM_TYPE;
            case GROUP:
                return CrossPlatformMobileHelperContract.GroupEntry.CONTENT_ITEM_TYPE;
            case GROUPS:
                return CrossPlatformMobileHelperContract.GroupEntry.CONTENT_TYPE;
            case GROUPDIR:
                return CrossPlatformMobileHelperContract.GroupEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the WEATHER, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_DATE,
        and LOCATION integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CrossPlatformMobileHelperContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, CrossPlatformMobileHelperContract.PATH_ITEM, ITEMDIR);

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, CrossPlatformMobileHelperContract.PATH_ITEM + "/#", ITEM);
        matcher.addURI(authority, CrossPlatformMobileHelperContract.PATH_ITEM + "/*/#", ITEM_WITH_GROUP);

        matcher.addURI(authority, CrossPlatformMobileHelperContract.PATH_GROUP, GROUPDIR);
        matcher.addURI(authority, CrossPlatformMobileHelperContract.PATH_GROUP + "/#", GROUP);
        matcher.addURI(authority, CrossPlatformMobileHelperContract.PATH_GROUP + "/*", GROUPS);
        return matcher;
    }
    @Override
    public boolean onCreate() {
        mOpenHelper = new CrossPlatformDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor = null;

//        static final int ITEM = 100;
//        static final int ITEM_WITH_GROUP = 101;
//        static final int ITEMDIR = 102;
//        static final int GROUP = 300;
//        static final int GROUPS = 301;
//        static final int GROUPDIR = 302;
         final int match = sUriMatcher.match(uri);

        switch (match) {
            // "Item/*"
            case ITEM_WITH_GROUP: {
                retCursor = getItemByGroup(uri, projection, sortOrder);
                break;
            }
            // "Item"
            case ITEMDIR:
            case ITEM: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        CrossPlatformMobileHelperContract.ItemEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "Group"
            case GROUPS:
            case GROUPDIR:
            case GROUP: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        CrossPlatformMobileHelperContract.GroupEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    private Cursor getItemByGroup(Uri uri, String[] projection, String sortOrder) {
        long groupid = CrossPlatformMobileHelperContract.ItemEntry.getGroupFromUri(uri);


        String[] selectionArgs;
        String selection;

            selectionArgs = new String[]{Long.toString(groupid)};
            selection = sGroupIDSelection;

//        return null;
        return sItemByGroupJoinGroupidQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
//        static final int ITEM = 100;
//        static final int ITEM_WITH_GROUP = 101;
//        static final int ITEMDIR = 102;
//        static final int GROUP = 300;
//        static final int GROUPS = 301;
//        static final int GROUPDIR = 302;
        switch (match) {
            case ITEM_WITH_GROUP:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            case ITEMDIR:
            case ITEM: {
                long _id = db.insert(CrossPlatformMobileHelperContract.ItemEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = CrossPlatformMobileHelperContract.ItemEntry.buildItemUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case GROUPS:
            case GROUPDIR:
            case GROUP: {
                long _id = db.insert(CrossPlatformMobileHelperContract.GroupEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = CrossPlatformMobileHelperContract.GroupEntry.buildGroupUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";

//        static final int ITEM = 100;
//        static final int ITEM_WITH_GROUP = 101;
//        static final int ITEMDIR = 102;
//        static final int GROUP = 300;
//        static final int GROUPS = 301;
//        static final int GROUPDIR = 302;
        switch (match) {
            case ITEMDIR:
            case ITEM_WITH_GROUP:
            case ITEM:
                rowsDeleted = db.delete(
                        CrossPlatformMobileHelperContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case GROUPS:
            case GROUPDIR:
            case GROUP:
                rowsDeleted = db.delete(
                        CrossPlatformMobileHelperContract.GroupEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
//        static final int ITEM = 100;
//        static final int ITEM_WITH_GROUP = 101;
//        static final int ITEMDIR = 102;
//        static final int GROUP = 300;
//        static final int GROUPS = 301;
//        static final int GROUPDIR = 302;
        switch (match) {
            case ITEM_WITH_GROUP:
            case ITEMDIR:
            case ITEM:
                rowsUpdated = db.update(CrossPlatformMobileHelperContract.ItemEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case GROUPDIR:
            case GROUPS:
            case GROUP:
                rowsUpdated = db.update(CrossPlatformMobileHelperContract.GroupEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
//        static final int ITEM = 100;
//        static final int ITEM_WITH_GROUP = 101;
//        static final int ITEMDIR = 102;
//        static final int GROUP = 300;
//        static final int GROUPS = 301;
//        static final int GROUPDIR = 302;
        switch (match) {
            case ITEM_WITH_GROUP:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            case ITEMDIR:
            case ITEM:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(CrossPlatformMobileHelperContract.ItemEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
