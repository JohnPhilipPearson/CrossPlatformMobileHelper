package com.betweenyounme.crossplatformmobilehelper;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/**
 * Created by johnpearson on 4/9/15.
 */
public class SampleData
{
    public final String LOG_TAG = SampleData.class.getSimpleName();
    public static final String JSON_FILENAME = "SampleData.json";
    public static final String JSON_GROUPARRAY = "Groups";
    public static final String JSON_ITEMARRAY = "Items";
    public static final String JSON_UNIQUEID = "UniqueId";
    public static final String JSON_TITLE = "Title";
    public static final String JSON_SUBTITLE = "Subtitle";
    public static final String JSON_IMAGEPATH = "ImagePath";
    public static final String JSON_SUMMARY = "Summary";
    public static final String JSON_CONTENT = "Content";

    public static String AssetJSONFile (String filename, Context context) throws IOException
    {
        AssetManager manager = context.getAssets();
        InputStream file = manager.open(filename);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }

    public static int loadSampleData(Context context)
    {
        int returnGroupCounter = 0;
        int returnItemCounter = 0;
        try {


            boolean loadDatabase = false;
            Cursor groupCursor = context.getContentResolver().query(
                    CrossPlatformMobileHelperContract.GroupEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
//            Cursor itemCursor = context.getContentResolver().query(
//                    CrossPlatformMobileHelperContract.ItemEntry.CONTENT_URI,
//                    null,
//                    null,
//                    null,
//                    null
//            );
            int groupCounter = groupCursor.getCount();
//            int itemCounter = itemCursor.getCount();
            if (groupCounter == 0)// && itemCounter == 0)
            {
                loadDatabase = true;
            }
            if (loadDatabase) {

                //ContentValues weatherValues = new ContentValues();
                String jsonLocation = AssetJSONFile(JSON_FILENAME, context);
                JSONArray groupArray = (new JSONObject(jsonLocation)).getJSONArray(JSON_GROUPARRAY);
                returnGroupCounter = groupArray.length();
                for (int groupCount = 0; groupCount < groupArray.length(); groupCount++) {
                    JSONObject group_inside = groupArray.getJSONObject(groupCount);
                    String groupuniqueId = group_inside.getString(JSON_UNIQUEID);
                    String grouptitle = group_inside.getString(JSON_TITLE);
                    String groupsubtitle = group_inside.getString(JSON_SUBTITLE);
                    String groupimagepath = group_inside.getString(JSON_IMAGEPATH);
                    String groupsummary = group_inside.getString(JSON_SUMMARY);
                    JSONArray itemArray = group_inside.getJSONArray(JSON_ITEMARRAY);


                    long groupid = addLocation(groupuniqueId
                            , grouptitle
                            , groupimagepath
                            , groupimagepath
                            , groupsummary
                            , context);

                    returnItemCounter += itemArray.length();

                    // Insert the new weather information into the database
                    Vector<ContentValues> cVVector = new Vector<ContentValues>(itemArray.length());
                    for (int itemCount = 0; itemCount < itemArray.length(); itemCount++) {
                        JSONObject item_inside = itemArray.getJSONObject(itemCount);
                        String itemuniqueId = item_inside.getString(JSON_UNIQUEID);
                        String itemtitle = item_inside.getString(JSON_TITLE);
                        String itemsubtitle = item_inside.getString(JSON_SUBTITLE);
                        String itemimagepath = item_inside.getString(JSON_IMAGEPATH);
                        String itemsummary = item_inside.getString(JSON_SUMMARY);
                        String itemcontent = item_inside.getString(JSON_CONTENT);

                        ContentValues itemValues = new ContentValues();

                        itemValues.put(CrossPlatformMobileHelperContract.ItemEntry.COLUMN_LOC_KEY, groupid);
                        itemValues.put(CrossPlatformMobileHelperContract.ItemEntry.COLUMN_UNIQUEID, itemuniqueId);
                        itemValues.put(CrossPlatformMobileHelperContract.ItemEntry.COLUMN_TITLE, itemtitle);
                        itemValues.put(CrossPlatformMobileHelperContract.ItemEntry.COLUMN_SUBTITLE, itemsubtitle);
                        itemValues.put(CrossPlatformMobileHelperContract.ItemEntry.COLUMN_IMAGEPATH, itemimagepath);
                        itemValues.put(CrossPlatformMobileHelperContract.ItemEntry.COLUMN_SUMMARY, itemsummary);
                        itemValues.put(CrossPlatformMobileHelperContract.ItemEntry.COLUMN_CONTENT, itemcontent);

                        cVVector.add(itemValues);
                    }

                    //bulk to database
                    int inserted = 0;
                    // add to database
                    if (cVVector.size() > 0) {
                        ContentValues[] cvArray = new ContentValues[cVVector.size()];
                        cVVector.toArray(cvArray);
                        context.getContentResolver().bulkInsert(CrossPlatformMobileHelperContract.ItemEntry.CONTENT_URI, cvArray);
/*
longer term web service
                    // delete old data so we don't build up an endless history
                    context.getContentResolver().delete(CrossPlatformMobileHelperContract.WeatherEntry.CONTENT_URI,
                            WeatherContract.WeatherEntry.COLUMN_DATE + " <= ?",
                            new String[] {Long.toString(dayTime.setJulianDay(julianStartDay-1))});
*/
                        //notifyWeather();
                    }

                    //Log.d(new SampleData()LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");

                }
                //Log.d(new SampleData().LOG_TAG, "Group count=> " + totalGroups);
                //Log.d(new SampleData().LOG_TAG, "Item count=> " + totalItems);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnGroupCounter;
    }



    /**
     * Helper method to handle insertion of a new location in the weather database.
     *
     * @param uniqueid The location string used unique from the server.
     * @param title A human-readable Title, e.g "Activity indicator"
     * @param subtitle A human-readable Subtitle, e.g "iOS and Windows 8  and Android"
     * @param imagepath A human-readable ImagePath, e.g "Windows300x225All.png""
     * @param summary A human-readable Summary, e.g "None"
     * @param context Context, e.g "None"
     * @return the row ID of the added location.
     */
    public static long addLocation(String uniqueid
            , String title
            , String subtitle
            , String imagepath
            , String summary
            , Context context)
    {
        long groupid;

        // Now that the content provider is set up, inserting rows of data is pretty simple.
        // First create a ContentValues object to hold the data you want to insert.
        ContentValues groupValues = new ContentValues();

        // Then add the data, along with the corresponding name of the data type,
        // so the content provider knows what kind of value is being inserted.
        groupValues.put(CrossPlatformMobileHelperContract.GroupEntry.COLUMN_UNIQUEID, uniqueid);
        groupValues.put(CrossPlatformMobileHelperContract.GroupEntry.COLUMN_TITLE, title);
        groupValues.put(CrossPlatformMobileHelperContract.GroupEntry.COLUMN_SUBTITLE, subtitle);
        groupValues.put(CrossPlatformMobileHelperContract.GroupEntry.COLUMN_IMAGEPATH, imagepath);
        groupValues.put(CrossPlatformMobileHelperContract.GroupEntry.COLUMN_SUMMARY, summary);
        // Finally, insert location data into the database.
        Uri insertedUri = context.getContentResolver().insert(
                CrossPlatformMobileHelperContract.GroupEntry.CONTENT_URI,
                groupValues
        );

        // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
        groupid = ContentUris.parseId(insertedUri);
        return groupid;
    }
    
    /*
    "Groups": [
    {
        "UniqueId": "Activity indicator",
            "Title": "Activity indicator",
            "Subtitle": "iOS and Windows 8  and Android",
            "ImagePath": "Windows300x225All.png",
            "Summary": "None",
            "Items": [
        {
            "UniqueId": "ProgressRing",
                "Title": "Windows 8.1",
                "Subtitle": "ProgressRing",
                "ImagePath": "WindowsOnly300x225.png",
                "Summary": "ProgressRing",
                "Content": "ProgressRing"
        },
        */
}
