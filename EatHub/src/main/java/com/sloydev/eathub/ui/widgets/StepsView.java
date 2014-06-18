package com.sloydev.eathub.ui.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sloydev.eathub.R;
import com.sloydev.eathub.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

import static butterknife.ButterKnife.findById;

public class StepsView extends LinearLayout {

    private LayoutInflater layoutInflater;

    public StepsView(Context context) {
        this(context, null);
    }

    public StepsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StepsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setOrientation(VERTICAL);
        layoutInflater = LayoutInflater.from(context);

        if (isInEditMode()) {
            initTestData();
        }
    }

    void initTestData() {
    }

    public void setSteps(List<Recipe.Step> steps) {
        if (getContext() == null) return;
        this.removeAllViews();

        for (int i = 0; i < steps.size(); i++) {
            // Generate and add the step view
            Recipe.Step s = steps.get(i);
            View stepView = layoutInflater.inflate(R.layout.item_recipe_step, this, false);
            this.addView(stepView);

            TextView number = findById(stepView, R.id.item_recipe_step_number);
            number.setText(String.valueOf(i + 1));

            TextView text = findById(stepView, R.id.item_recipe_step_text);
            text.setText(s.getText());

            ImageView image = findById(stepView, R.id.item_recipe_step_image);
            String imageUrl = s.getImage();
            if (imageUrl != null && !TextUtils.isEmpty(imageUrl)) {
                Picasso.with(getContext()).load(imageUrl).placeholder(R.drawable.placeholder).into(image);
            } else {
                image.setVisibility(GONE);
            }

            // Add separator if neccesary
            if (i < steps.size() - 1) {
                View separator = layoutInflater.inflate(R.layout.separator_recipe_steps, this, false);
                this.addView(separator);
            }
        }
    }


}
