package com.sloydev.eathub.ui.fragments.tardis;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;


import com.sloydev.eathub.BuildConfig;
import com.sloydev.eathub.R;

import java.util.ArrayList;

/**
 * Created by rafa on 06/07/13.
 */
public abstract class MultiRowAdapter extends BaseAdapter implements AbsListView.RecyclerListener {

    private Context mContext;
    private ArrayList<Row> filas;
    private RecycleBin mRecycler;

    public MultiRowAdapter(Context context, ArrayList<Row> filas) {
        mContext = context;
        this.filas = filas;

        // Preparo la papelera de reciclaje
        mRecycler = new RecycleBin();
        mRecycler.setViewTypeCount(getInternalViewTypeCount());
    }

    @Override
    public int getCount() {
        return filas.size();
    }

    @Override
    public Row getItem(int position) {
        return filas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Row newRow = getItem(position);
        convertView = newRow.getView(position, convertView, parent, this);
        convertView.setTag(R.id.position, position);
        return convertView;
    }

    @Override
    public void onMovedToScrapHeap(View view) {
        // Cuando una fila se recicla, coger todas las Views de los items que hay dentro, y meterlas en el reciclador propio :S
        // Compruebo si la vista es directamente un item de los míos (SingleItemRow)
        Object tag = view.getTag(R.id.internal_type);
        if (tag != null) {
            //pozi, mándala a la papelera
            mRecycler.addScrapView(view);
        } else {
            //pono, comprueba sus hijos
            if (!(view instanceof ViewGroup)) {
                // Si la vista directamente no es de las nuestras, no hay más remedio que sea un ViewGroup conteniendo vistas nuestras. Si no es que alguien metió la pata.
                if (BuildConfig.DEBUG)
                    Log.w("MultiRow", "The view to be recycled does not correspond with a Item's view nor a Vie");
                return;
            }
            ViewGroup viewGroup = (ViewGroup) view;
            View childView;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                childView = viewGroup.getChildAt(i);
                Object internalTag = childView .getTag(R.id.internal_type);
                if (internalTag != null) {
                    //pozi, mándala a la papelera
                    viewGroup.removeView(childView);
                    mRecycler.addScrapView(childView );
                }
            }
        }
    }

    public abstract int getInternalViewType(Item item);

    public abstract int getInternalViewTypeCount();

    public RecycleBin getRecycleBin() {
        return mRecycler;
    }


    public class RecycleBin {

        private ArrayList<View>[] mScrapViews;
        private int mViewTypeCount;

        public View getInternalScrapView(Item item) {
            int whichScrap = getInternalViewType(item);
            if (whichScrap >= 0 && whichScrap < mScrapViews.length) {
                ArrayList<View> scrapView = mScrapViews[whichScrap];
                int size = scrapView.size();
                if (size > 0)
                    return scrapView.remove(size - 1);
            }
            return null;
        }

        public void setViewTypeCount(int viewTypeCount) {
            if (viewTypeCount < 1) {
                throw new IllegalArgumentException("Can't have a viewTypeCount < 1");
            }
            //noinspection unchecked
            ArrayList<View>[] scrapViews = new ArrayList[viewTypeCount];
            for (int i = 0; i < viewTypeCount; i++) {
                scrapViews[i] = new ArrayList<View>();
            }
            mViewTypeCount = viewTypeCount;
            mScrapViews = scrapViews;
        }

        public void addScrapView(View scrap) {
            int viewType = (Integer) scrap.getTag(R.id.internal_type);
            mScrapViews[viewType].add(scrap);
        }
    }


}
