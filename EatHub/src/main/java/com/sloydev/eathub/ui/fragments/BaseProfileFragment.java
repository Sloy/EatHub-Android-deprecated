package com.sloydev.eathub.ui.fragments;

import android.os.Bundle;

import com.sloydev.eathub.model.User;

public abstract class BaseProfileFragment extends BaseFragment {

    public static final String USER_EXTRA = "user";

    private User mUser;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mUser = (User) savedInstanceState.getSerializable(USER_EXTRA);
        } else {
            mUser = (User) getArguments().getSerializable(USER_EXTRA);
        }

        if (mUser == null) {
            throw new RuntimeException("onActivityCreated called without User in arguments nor savedInstanceState");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(USER_EXTRA, mUser);
    }

    protected User getUser() {
        return mUser;
    }

}
