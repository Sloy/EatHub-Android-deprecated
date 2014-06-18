package com.sloydev.eathub.ui.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sloydev.eathub.R;
import com.sloydev.eathub.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

import static butterknife.ButterKnife.findById;

public class CommentsView extends LinearLayout {

    private LayoutInflater layoutInflater;

    public CommentsView(Context context) {
        this(context, null);
    }

    public CommentsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setOrientation(VERTICAL);
        layoutInflater = LayoutInflater.from(context);

        if (isInEditMode()) {
            initTestData();
        }
    }

    void initTestData() {
    }

    public void setComments(List<Recipe.Comment> comments) {
        if (getContext() == null) return;
        this.removeAllViews();

        for (int i = 0; i < comments.size(); i++) {
            // Generate and add the step view
            Recipe.Comment c = comments.get(i);
            View commentView = layoutInflater.inflate(R.layout.item_recipe_comment, this, false);
            this.addView(commentView);

            TextView name = findById(commentView, R.id.item_recipe_comment_name);
            name.setText(c.getUser().getDisplayName());

            TextView date = findById(commentView, R.id.item_recipe_comment_date);
            date.setText(c.getCreateDate());

            TextView text = findById(commentView, R.id.item_recipe_comment_text);
            text.setText(c.getText());

           /* ImageView avatar = findById(commentView, R.id.item_recipe_comment_avatar);
            String imageUrl = c.getUser().getAvatar();
            if (imageUrl != null && !TextUtils.isEmpty(imageUrl)) {
                Picasso.with(getContext()).load(imageUrl).placeholder(R.drawable.placeholder).into(avatar);
            } else {
            }*/
        }
    }


}
