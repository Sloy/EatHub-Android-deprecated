package com.sloydev.eathub.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.sloydev.eathub.BuildConfig;
import com.sloydev.eathub.EatHubService;
import com.sloydev.eathub.R;

import retrofit.RestAdapter;

public class BaseActivity extends Activity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!BuildConfig.DEBUG) {
            Crashlytics.start(this);
        }
    }
}
