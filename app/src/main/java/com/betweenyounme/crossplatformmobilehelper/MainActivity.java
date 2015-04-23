package com.betweenyounme.crossplatformmobilehelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;


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

                //getApplicationContext();
            }

        }
        else
        {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new GroupFragment())
//                    .commit();
//
//            //getApplicationContext();
//        }

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
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String jsonString = "";
        try
        {
            jsonString = SampleData.AssetJSONFile(SampleData.JSON_FILENAME, this);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
//        File file = null;
//        try
//        {
//            file = new File(this.getCacheDir(), SampleData.JSON_FILENAME);
//            FileOutputStream outputStream = new FileOutputStream(file);
//            byte [] bytes = jsonString.getBytes();
//            outputStream.write(bytes);
//            outputStream.close();
//        }
//        catch (Exception ex)
//        {
//            ex.printStackTrace();
//        }
//
//        Uri uri = Uri.fromFile(file);
        //shareIntent.putExtra(Intent.EXTRA_STREAM, uri.toString());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "share json file");
        shareIntent.putExtra(Intent.EXTRA_TEXT, jsonString);
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

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
