package com.sloydev.eathub.ui.fragments.tardis;

import android.view.View;
import android.view.ViewGroup;

public abstract class Item implements View.OnClickListener {

    protected abstract View bindView(View view);

    public abstract View createView(ViewGroup parent);
}
