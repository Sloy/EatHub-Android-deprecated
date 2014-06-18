package com.sloydev.eathub.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sloydev.eathub.R;

import java.util.ArrayList;
import java.util.List;

public class IngredientsView extends LinearLayout {

    private final int separatorMarginVertical;
    private final int separatorMarginHorizontal;
    private int separatorHeight;

    public IngredientsView(Context context) {
        this(context, null);
    }

    public IngredientsView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.ingredientsViewStyle);
    }

    public IngredientsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        separatorHeight = getResources().getDimensionPixelSize(R.dimen.separator_size);
        separatorMarginVertical = getResources().getDimensionPixelSize(R.dimen.separator_ingredient_vertical);
        separatorMarginHorizontal = getResources().getDimensionPixelSize(R.dimen.separator_ingredient_horizontal);

        if (isInEditMode()) {
            initTestData();
        }
    }

    void initTestData() {
        ArrayList<String> testList = new ArrayList<>();
        testList.add("Nanananananan");
        testList.add("Nenenene");
        testList.add("Nononononono");
        setIngredients(testList);
    }

    public void setIngredients(List<String> ingredients) {
        if (getContext() == null) return;
        this.removeAllViews();

        for (int i = 0; i < ingredients.size(); i++) {
            String ing = ingredients.get(i);
            TextView tv = new TextView(getContext(), null, R.attr.ingredientsViewItemStyle);
            tv.setText(ing);
            this.addView(tv);
            if (i < ingredients.size() - 1) {
                View separator = new View(getContext(), null, R.attr.ingredientsViewSeparatorStyle);
                separator.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        separatorHeight));
                this.addView(separator);
                LayoutParams lpm = (LayoutParams) separator.getLayoutParams();
                if (lpm != null) {
                    lpm.setMargins(separatorMarginHorizontal, separatorMarginVertical, separatorMarginHorizontal, separatorMarginVertical);
                }
            }
        }
    }


}
