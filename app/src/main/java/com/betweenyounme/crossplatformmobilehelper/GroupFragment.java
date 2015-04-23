package com.betweenyounme.crossplatformmobilehelper;

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
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by johnpearson on 4/15/15.
 */
public class GroupFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = GroupFragment.class.getSimpleName();
    private GroupAdapter mGroupAdapter;
    private ListView mListView;
    private static int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private static final int GROUP_LOADER = 0;

    private static final String[] GROUP_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.

            CrossPlatformMobileHelperContract.GroupEntry.TABLE_NAME + "." + CrossPlatformMobileHelperContract.GroupEntry._ID,
    CrossPlatformMobileHelperContract.GroupEntry.COLUMN_UNIQUEID,
    CrossPlatformMobileHelperContract.GroupEntry.COLUMN_TITLE,
    CrossPlatformMobileHelperContract.GroupEntry.COLUMN_SUBTITLE,
    CrossPlatformMobileHelperContract.GroupEntry.COLUMN_IMAGEPATH,
    CrossPlatformMobileHelperContract.GroupEntry.COLUMN_SUMMARY
    };

    static final int COL_GROUP_ID = 0;
    static final int COL_GROUP_UNIQUEID = 1;
    static final int COL_GROUP_TITLE = 2;
    static final int COL_GROUP_SUBTITLE = 3;
    static final int COL_GROUP_IMAGEPATH = 4;
    static final int COL_GROUP_SUMMARY = 5;

    public GroupFragment()
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
        public void onItemSelected(String jsonString);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        new SampleDataAsync().execute(getActivity());
        //SampleData.loadSampleData(getActivity());

        // The ForecastAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mGroupAdapter = new GroupAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_group);
        mListView.setAdapter(mGroupAdapter);
        // We'll call our MainActivity
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null)
                {


                    String title = cursor.getString(COL_GROUP_TITLE);
                    String jsonString = null;

                    try
                    {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("title", title);
                        jsonObject.put("groupid", id);
                        jsonString = jsonObject.toString();
                    }
                    catch (JSONException ex)
                    {
                        // code handler
                    }

                    ((Callback) getActivity())
                            .onItemSelected(jsonString);

                }
                mPosition = position;
            }
        });

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }



        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(GROUP_LOADER, null, this);
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


        Uri groupAllUri = CrossPlatformMobileHelperContract.GroupEntry.buildAllGroupsUri();



        return new CursorLoader(getActivity(),
                groupAllUri,
                GROUP_COLUMNS,
                null,
                null,
                null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mGroupAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mListView.smoothScrollToPosition(mPosition);
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        mGroupAdapter.swapCursor(null);
    }
}


