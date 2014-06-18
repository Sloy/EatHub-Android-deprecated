package com.sloydev.eathub.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sloydev.eathub.R;
import com.sloydev.eathub.model.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProfileInfoFragment extends BaseProfileFragment {

    @InjectView(R.id.profile_info_name) TextView mName;
    @InjectView(R.id.profile_info_location) TextView mLocation;
    @InjectView(R.id.profile_info_languages) TextView mLanguages;
    @InjectView(R.id.profile_info_gender) TextView mGender;
    @InjectView(R.id.profile_info_web) TextView mWeb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_info, container, false);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        User user = getUser();
        final User.Profile profile = user.getProfile();

        mName.setText(String.format("%s (%s)", profile.getDisplayName(), user.getUsername()));
        mLocation.setText(profile.getLocation());
        List<String> additionalLanguages = profile.getAdditionalLanguages();

        // Apaño
        List<String> languagesApañaos = new ArrayList<>();
        for (String l : additionalLanguages) {
            if (l.equals("es")) {
                languagesApañaos.add("Español");
            } else if (l.equals("en")) {
                languagesApañaos.add("Inglés");
            }
        }

        if (languagesApañaos != null && languagesApañaos.size() > 0) {
            String text = "Español" + ", ";
            mLanguages.setText(text + TextUtils.join(", ", languagesApañaos));
        } else {
            mLanguages.setText("Español");
        }
        mGender.setText(profile.getGender().equals("m") ? "Hombre" : "Mujer");
        mWeb.setText(profile.getWebsite());
        mWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(profile.getWebsite())));
            }
        });
    }
}
