package com.betweenyounme.crossplatformmobilehelper;

/**
 * Created by johnpearson on 4/17/15.
 */
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
public class ItemAdapter extends CursorAdapter
{
    public ItemAdapter(Context context, Cursor c, int flags)
    {
        super(context, c, flags);
    }

    public static class ViewHolder
    {

        public final ImageView iconView;
        public final TextView titleView;
        public final TextView subtitleView;


        public ViewHolder(View view)
        {

            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            titleView = (TextView) view.findViewById(R.id.list_item_title_textview);
            subtitleView = (TextView) view.findViewById(R.id.list_item_subtitle_textview);

        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        // Choose the layout type
//        int viewType = getItemViewType(cursor.getPosition());
//        int layout = -1;


        View view = LayoutInflater.from(context).inflate(R.layout.list_item_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.iconView.setImageResource(Utility.getIconResourceForItem(
                cursor.getString(ItemFragment.COL_ITEM_IMAGEPATH)));

        viewHolder.iconView.setContentDescription(
                cursor.getString(ItemFragment.COL_ITEM_TITLE)
                + " "
                + cursor.getString(ItemFragment.COL_ITEM_SUBTITLE));

        // Read weather forecast from cursor
        String title = cursor.getString(ItemFragment.COL_ITEM_TITLE);
        // Find TextView and set weather forecast on it
        viewHolder.titleView.setText(title);

        String subtitle = cursor.getString(ItemFragment.COL_ITEM_SUBTITLE);
        // Find TextView and set weather forecast on it
        viewHolder.subtitleView.setText(subtitle);

    }
}

