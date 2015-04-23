package com.betweenyounme.crossplatformmobilehelper;

/**
 * Created by johnpearson on 4/17/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by johnpearson on 4/15/15.
 */
public class ItemFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = ItemFragment.class.getSimpleName();
    static final String ITEM_JSON_STRING = "JSON_STRING";
    private ItemAdapter mItemAdapter;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private static final int ITEM_LOADER = 0;
    private static long groupid = 0;

    private static final String[] ITEM_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.

            CrossPlatformMobileHelperContract.ItemEntry.TABLE_NAME + "." + CrossPlatformMobileHelperContract.ItemEntry._ID,
            CrossPlatformMobileHelperContract.ItemEntry.COLUMN_LOC_KEY,
            CrossPlatformMobileHelperContract.ItemEntry.COLUMN_UNIQUEID,
            CrossPlatformMobileHelperContract.ItemEntry.COLUMN_TITLE,
            CrossPlatformMobileHelperContract.ItemEntry.COLUMN_SUBTITLE,
            CrossPlatformMobileHelperContract.ItemEntry.COLUMN_IMAGEPATH,
            CrossPlatformMobileHelperContract.ItemEntry.COLUMN_SUMMARY,
            CrossPlatformMobileHelperContract.ItemEntry.COLUMN_CONTENT
    };




    static final int COL_ITEM_ID = 0;
    static final int COL_ITEM_LOC_KEY = 1;
    static final int COL_ITEM_UNIQUEID = 2;
    static final int COL_ITEM_TITLE = 3;
    static final int COL_ITEM_SUBTITLE = 4;
    static final int COL_ITEM_IMAGEPATH = 5;
    static final int COL_ITEM_SUMMARY = 6;
    static final int COL_ITEM_CONTENT = 7;

    public ItemFragment()
    {
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // The ForecastAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mItemAdapter = new ItemAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);




        String jsonString = null;
        String title = "EXTRA_TEXT error";

        Intent intent = getActivity().getIntent();
        Bundle args = getArguments();
        if (args != null)
        {
            jsonString = args.getString(ItemFragment.ITEM_JSON_STRING);
        }

        if (jsonString == null && intent != null && intent.hasExtra(Intent.EXTRA_TEXT))
        {
            jsonString = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        if (jsonString != null)
        {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                groupid = jsonObject.getLong("groupid");
                title = jsonObject.getString("title");
            } catch (JSONException ex) {
                //handle error
            }
        }

//            String groupidString = intent.getStringExtra(Intent.EXTRA_TEXT);
//            groupid = Long.valueOf(groupidString);
        Activity activity = getActivity();
        if (activity.getClass() == DetailActivity.class)
        {
            activity.setTitle(title);
        }


        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_item);
        mListView.setAdapter(mItemAdapter);



        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY))
        {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }



        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ITEM_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, filter the query to return weather only for
        // dates after or including today.

        // Sort order:  Ascending, by date.
        //String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";

        Uri itemForGroupUri = CrossPlatformMobileHelperContract.ItemEntry.buildItemForGroupUri(groupid);



        return new CursorLoader(getActivity(),
                itemForGroupUri,
                null,
                null,
                null,
                null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mItemAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mListView.smoothScrollToPosition(mPosition);
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mItemAdapter.swapCursor(null);
    }
}


