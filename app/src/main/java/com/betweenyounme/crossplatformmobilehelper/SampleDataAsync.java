package com.betweenyounme.crossplatformmobilehelper;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;

/**
 * Created by johnpearson on 4/21/15.
 */
public class SampleDataAsync extends AsyncTask<Context, String, ArrayList>
{
    private static final int GROUP_NOTIFICATION_ID = 3004;
    protected ArrayList doInBackground(Context... contexts)
    {
        int returnGroupCounter = 0;

        returnGroupCounter = SampleData.loadSampleData(contexts[0]);
        ArrayList arrayList = new ArrayList(2);
        arrayList.add(0, contexts[0]);
        arrayList.add(1, new Integer(returnGroupCounter));

        return arrayList;
    }
    protected void onPostExecute(ArrayList arrayList)
    {
        Context context = (Context) arrayList.get(0);
        Integer groupCounter = (Integer) arrayList.get(1);
        if (groupCounter > 0)
        {
            NotificationCompat.Builder mbuilder =
                    new NotificationCompat.Builder(context)
                    .setSmallIcon(Utility.getIconResourceForGroup("all.png"))
                    .setContentTitle("Cross Platform Mobile Helper")
                    .setContentText("3 platforms & " + groupCounter.toString() + " concepts loaded");

            Intent resultIntent = new Intent(context, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mbuilder.setContentIntent(resultPendingIntent);
            NotificationManager mnotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mnotificationManager.notify(GROUP_NOTIFICATION_ID, mbuilder.build());

        }
    }
}
