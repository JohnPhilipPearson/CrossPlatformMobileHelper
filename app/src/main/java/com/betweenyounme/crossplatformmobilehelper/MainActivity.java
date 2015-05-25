package com.betweenyounme.crossplatformmobilehelper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements GroupFragment.Callback
{
    private ShareActionProvider mShareActionProvider;
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String ITEMFRAGMENT_TAG = "IFTAG";
    private boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.item_detail_container) != null)
        {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_detail_container, new ItemFragment(), ITEMFRAGMENT_TAG)
                        .commit();
            }

        }
        else
        {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        MenuItem item = menu.findItem(R.id.menu_item_share);
        try {
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        }
        catch (Exception ex)
        {
            int i = 0;
        }
        setShareIntent(createShareIntent());

        return true;
    }

    private void setShareIntent(Intent shareIntent)
    {
        if (mShareActionProvider != null)
        {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
    private Intent createShareIntent()
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

        ArrayList<String> filenames = new ArrayList<String>();
        filenames.add(SampleData.JSON_FILENAME);
        filenames.add(SampleData.BETWEENYONME_FILENAME);
        ArrayList<Uri> urisToShare = new ArrayList<Uri>();
        try {
            urisToShare = SampleData.AssetsImageAndJSONFile(filenames, this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }



        shareIntent.setType("*/*");
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, urisToShare);

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share files");
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onItemSelected(String jsonString)
    {
        if (mTwoPane)
        {
            Bundle args = new Bundle();

            args.putString(ItemFragment.ITEM_JSON_STRING, jsonString);
            ItemFragment detailedFragment = new ItemFragment();
            detailedFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, detailedFragment, ITEMFRAGMENT_TAG)
                    .commit();
        }
        else
        {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(Intent.EXTRA_TEXT, jsonString);
            startActivity(intent);
        }


    }


}
