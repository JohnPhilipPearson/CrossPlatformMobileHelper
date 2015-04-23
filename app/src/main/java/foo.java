/**
 * Created by johnpearson on 4/17/15.
 */
public class foo {
}

//package com.betweenyounme.crossplatformmobilehelper;
//
//        import android.content.Intent;
//        import android.os.Bundle;
//        import android.support.v4.app.Fragment;
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.view.ViewGroup;
//        import android.widget.AdapterView;
//        import android.widget.ArrayAdapter;
//        import android.widget.ListView;
//
//        import java.util.ArrayList;
//        import java.util.Arrays;
//        import java.util.List;
//
///**
// * Created by johnpearson on 4/15/15.
// */
//public class GroupFragment extends Fragment {
//    private ArrayAdapter<String> mGroupAdapter;
//    private int mPosition = ListView.INVALID_POSITION;
//    private static final String SELECTED_KEY = "selected_position";
//
//    private static final String[] GROUP_COLUMNS = {
//            // In this case the id needs to be fully qualified with a table name, since
//            // the content provider joins the location & weather tables in the background
//            // (both have an _id column)
//            // On the one hand, that's annoying.  On the other, you can search the weather table
//            // using the location set by the user, which is only in the Location table.
//            // So the convenience is worth it.
//
//            CrossPlatformMobileHelperContract.GroupEntry.TABLE_NAME + "." + CrossPlatformMobileHelperContract.GroupEntry._ID,
//            CrossPlatformMobileHelperContract.GroupEntry.COLUMN_UNIQUEID,
//            CrossPlatformMobileHelperContract.GroupEntry.COLUMN_TITLE,
//            CrossPlatformMobileHelperContract.GroupEntry.COLUMN_SUBTITLE,
//            CrossPlatformMobileHelperContract.GroupEntry.COLUMN_IMAGEPATH,
//            CrossPlatformMobileHelperContract.GroupEntry.COLUMN_SUMMARY
//    };
//
//    static final int COL_GROUP_ID = 0;
//    static final int COL_GROUP_UNIQUEID = 1;
//    static final int COL_GROUP_TITLE = 2;
//    static final int COL_GROUP_SUBTITLE = 3;
//    static final int COL_GROUP_IMAGEPATH = 4;
//    static final int COL_GROUP_SUMMARY = 5;
//
//    public GroupFragment()
//    {
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState)
//    {
//        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//
//
////            Uri a = CrossPlatformMobileHelperContract.GroupEntry.buildGroupUri(0);
////            Uri b = CrossPlatformMobileHelperContract.GroupEntry.buildAllGroupsUri();
////            Uri c = CrossPlatformMobileHelperContract.ItemEntry.buildItemForGroupUri(0);
////            Uri d = CrossPlatformMobileHelperContract.ItemEntry.buildItemUri(0);
//        SampleData.loadSampleData(getActivity());
//
//        String[] groupArray =
//                {
//                        "AAA"
//                        , "Activity indicator"
//                        , "Ad banner view"
//                        , "Button"
//                        , "Date picker"
//                        , "Image view"
//                        , "Label"
//                        , "Map view"
//                        , "Navigation controller"
//                        , "Page control"
//                        , "Picker view"
//                        , "Table view controller"
//                };
//        List<String> groupList = new ArrayList<String>(Arrays.asList(groupArray));
//        mGroupAdapter = new ArrayAdapter<String>(
//                getActivity()
//                , R.layout.list_item_group
//                , R.id.list_item_group_textview
//                , groupList
//        );
//        ListView listView = (ListView) rootView.findViewById(R.id.listview_group);
//        listView.setAdapter(mGroupAdapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String group = mGroupAdapter.getItem(position);
//                //Toast.makeText(getActivity(), group, Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getActivity(), DetailActivity.class)
//                        .putExtra(Intent.EXTRA_TEXT, Long.toString(id));
//                startActivity(intent);
//            }
//        });
//
//        return rootView;
//    }
//}


