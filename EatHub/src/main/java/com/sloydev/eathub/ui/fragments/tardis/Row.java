package com.sloydev.eathub.ui.fragments.tardis;

import android.view.View;
import android.view.ViewGroup;

public abstract class Row {

    public abstract View getView(int position, View convertView, ViewGroup parent, MultiRowAdapter adapter);

}
