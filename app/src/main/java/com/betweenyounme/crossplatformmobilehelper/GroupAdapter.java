package com.betweenyounme.crossplatformmobilehelper;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by johnpearson on 4/16/15.
 */
public class GroupAdapter extends CursorAdapter
{
    public GroupAdapter(Context context, Cursor c, int flags)
    {
        super(context, c, flags);
    }

    public static class ViewHolder
    {

        public final ImageView iconView;
        public final TextView descriptionView;


        public ViewHolder(View view)
        {

            iconView = (ImageView) view.findViewById(R.id.list_group_icon);
            descriptionView = (TextView) view.findViewById(R.id.list_item_group_textview);

        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        // Choose the layout type
//        int viewType = getItemViewType(cursor.getPosition());
//        int layout = -1;


        View view = LayoutInflater.from(context).inflate(R.layout.list_item_group, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.iconView.setImageResource(Utility.getIconResourceForGroup(
                cursor.getString(GroupFragment.COL_GROUP_IMAGEPATH)));

        viewHolder.iconView.setContentDescription(
                cursor.getString(GroupFragment.COL_GROUP_SUBTITLE));

        // Read weather forecast from cursor
        String description = cursor.getString(GroupFragment.COL_GROUP_TITLE);
        // Find TextView and set weather forecast on it
        viewHolder.descriptionView.setText(description);

    }
}
