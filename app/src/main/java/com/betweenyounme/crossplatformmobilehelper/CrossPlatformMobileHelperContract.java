package com.betweenyounme.crossplatformmobilehelper;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by johnpearson on 4/10/15.
 */
public class CrossPlatformMobileHelperContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.betweenyounme.crossplatformmobilehelper";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.betweenyounme.crossplatformmobilehelper/group/ is a valid path for
    // looking at weather data. content://com.betweenyounme.crossplatformmobilehelper/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_GROUP = "group";
    public static final String PATH_ITEM = "item";


    /* Inner class that defines the table contents of the group table */
    public static final class GroupEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GROUP).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GROUP;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GROUP;

        // Table name
        public static final String TABLE_NAME = "grouptable";


        public static final String COLUMN_UNIQUEID = "UniqueId";
        public static final String COLUMN_TITLE = "Title";
        public static final String COLUMN_SUBTITLE = "Subtitle";
        public static final String COLUMN_IMAGEPATH = "ImagePath";
        public static final String COLUMN_SUMMARY = "Summary";


        public static Uri buildGroupUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildAllGroupsUri() {
            return CONTENT_URI.buildUpon().appendPath("*").build();
        }



    }

    /* Inner class that defines the table contents of the item table */
    public static final class ItemEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEM).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;

        public static final String TABLE_NAME = "itemtable";

        // Column with the foreign key into the group table.
        public static final String COLUMN_LOC_KEY = "group_id";

        public static final String COLUMN_UNIQUEID = "UniqueId";
        public static final String COLUMN_TITLE = "Title";
        public static final String COLUMN_SUBTITLE = "Subtitle";
        public static final String COLUMN_IMAGEPATH = "ImagePath";
        public static final String COLUMN_SUMMARY = "Summary";
        public static final String COLUMN_CONTENT = "Content";

        public static Uri buildItemUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildItemForGroupUri(long id) {
            String groupid = Long.toString(id);

            return CONTENT_URI.buildUpon().appendPath(PATH_GROUP).appendPath(groupid).build();
            //return CONTENT_URI.buildUpon().appendQueryParameter(PATH_GROUP, groupid).build();
        }

        public static long getGroupFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

    }
}
