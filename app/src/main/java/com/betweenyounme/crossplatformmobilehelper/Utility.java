package com.betweenyounme.crossplatformmobilehelper;

/**
 * Created by johnpearson on 4/16/15.
 */
public class Utility
{

    public static int getIconResourceForGroup(String imagePath)
    {
        int returnValue = -1;
        boolean done = false;

        if (!done && imagePath.equals("all.png"))
        {
            returnValue = R.drawable.all;
            done = true;
        }
        if (!done && imagePath.equals("windowsonly.png"))
        {
            returnValue = R.drawable.windowsonly;
            done = true;
        }
        if (!done && imagePath.equals("iosonly.png"))
        {
            returnValue = R.drawable.iosonly;
            done = true;
        }
        if (!done && imagePath.equals("androidonly.png"))
        {
            returnValue = R.drawable.androidonly;
            done = true;
        }
        if (!done && imagePath.equals("iosandroid.png"))
        {
            returnValue = R.drawable.iosandroid;
            done = true;
        }
        if (!done && imagePath.equals("androidwindows.png"))
        {
            returnValue = R.drawable.androidwindows;
            done = true;
        }

        if (!done && imagePath.equals("ioswindows.png"))
        {
            returnValue = R.drawable.ioswindows;
            done = true;
        }
        if (!done && returnValue == -1)
        {
            int hh = 0;
            returnValue = R.drawable.all;
            done = true;
        }
        return returnValue;
    }
    public static int getIconResourceForItem(String imagePath)
    {
        int returnValue = -1;
        boolean done = false;

        if (!done && imagePath.equals("all.png"))
        {
            returnValue = R.drawable.all;
            done = true;
        }
        if (!done && imagePath.equals("windowsonly.png"))
        {
            returnValue = R.drawable.windowsonly;
            done = true;
        }
        if (!done && imagePath.equals("iosonly.png"))
        {
            returnValue = R.drawable.iosonly;
            done = true;
        }
        if (!done && imagePath.equals("androidonly.png"))
        {
            returnValue = R.drawable.androidonly;
            done = true;
        }
        if (!done && imagePath.equals("iosandroid.png"))
        {
            returnValue = R.drawable.iosandroid;
            done = true;
        }
        if (!done && imagePath.equals("androidwindows.png"))
        {
            returnValue = R.drawable.androidwindows;
            done = true;
        }

        if (!done && imagePath.equals("ioswindows.png"))
        {
            returnValue = R.drawable.ioswindows;
            done = true;
        }
        if (!done && returnValue == -1)
        {
            int hh = 0;
            returnValue = R.drawable.all;
            done = true;
        }
        return returnValue;
    }
}
