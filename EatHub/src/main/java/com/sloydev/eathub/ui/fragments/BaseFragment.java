package com.sloydev.eathub.ui.fragments;

import android.app.Fragment;
import android.util.Log;

import com.sloydev.eathub.EatHubService;
import com.sloydev.eathub.R;
import com.sloydev.eathub.ui.activities.BaseActivity;

import retrofit.RestAdapter;

public abstract class BaseFragment extends Fragment {

    private EatHubService service;

    protected EatHubService getEatHubService() {
        Log.d("Endpoint", getString(R.string.api_endpoint));
        if (service == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(getString(R.string.api_endpoint))
                    .build();
            service = restAdapter.create(EatHubService.class);
        }
        return service;
    }
}
