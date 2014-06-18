package com.sloydev.eathub.ui.fragments.tardis;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.sloydev.eathub.BuildConfig;
import com.sloydev.eathub.R;

public class MultiItemRow extends Row {

    private Context mContext;
    private int mHeightDimensionReference;
    private int[] mColumnSizes;
    private int mSeparatorWidth;
    private Item[] mItems;

    public MultiItemRow(Context context, int heightDimensionReference, int separatorWidthReference, Item... items) {
        mContext = context;
        mHeightDimensionReference = heightDimensionReference;
        mSeparatorWidth = context.getResources().getDimensionPixelOffset(separatorWidthReference);
        mItems = items;
        mColumnSizes = new int[items.length];
        for (int i = 0; i < mColumnSizes.length; i++) {
            mColumnSizes[i] = 1;
        }

    }

    public MultiItemRow(Context context, int heightDimensionReference, int separatorWidthReference, int[] columnSizes, Item... items) {
        mContext = context;
        mHeightDimensionReference = heightDimensionReference;
        mSeparatorWidth = context.getResources().getDimensionPixelOffset(separatorWidthReference);
        mItems = items;
        mColumnSizes = columnSizes; //TODO validar y lanzar excepción
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent, MultiRowAdapter adapter) {
        if (convertView == null) {
            convertView = new LinearLayout(mContext);
        }

        convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, mContext.getResources().getDimensionPixelOffset(mHeightDimensionReference)));
        //TODO que use un linearlayout si hay varios items, que use la vista directamente si hay sólo uno
        ViewGroup container = (ViewGroup) convertView;
        container.removeAllViews();

        Item item;
        for (int i = 0; i < mItems.length; i++) {
            item = mItems[i];
            View itemView = adapter.getRecycleBin().getInternalScrapView(item);
            if (itemView == null) {
                if (BuildConfig.DEBUG)
                    Log.d("MultiRow", "Creating new view for " + ((Object)item).getClass().getName());
                itemView = item.createView(parent);
            } else {
                if (BuildConfig.DEBUG)
                    Log.d("MultiRow", "Recycling view for " + ((Object)item).getClass().getName());
            }
            itemView = item.bindView(itemView);
            itemView.setClickable(true); //TODO clickable sólo si el item quiere
            itemView.setOnClickListener(item);
            itemView.setTag(R.id.internal_type, adapter.getInternalViewType(item));
            container.addView(itemView, new LayoutParams(0, LayoutParams.MATCH_PARENT, mColumnSizes[i]));

            if (i < mItems.length - 1) {
                container.addView(getSeparator());
            }
        }

        return convertView;
    }

    public View getSeparator() {
        View separator = new View(mContext);
        separator.setLayoutParams(new LayoutParams(mSeparatorWidth, LayoutParams.MATCH_PARENT));
        return separator;
    }
}
