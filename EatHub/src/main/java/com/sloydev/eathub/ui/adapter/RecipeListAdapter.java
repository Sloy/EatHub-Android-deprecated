package com.sloydev.eathub.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sloydev.eathub.ImageUrl;
import com.sloydev.eathub.R;
import com.sloydev.eathub.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;

public class RecipeListAdapter extends BaseAdapter {

    public LayoutInflater mInflater;
    public List<Recipe.EmbeddedRecipe> mRecipes;
    public Picasso mPicasso;
    public ImageUrl mImageUrl;

    public RecipeListAdapter(Activity context, List<Recipe.EmbeddedRecipe> mRecipes) {
        this.mRecipes = mRecipes;
        this.mInflater = LayoutInflater.from(context);
        this.mPicasso = Picasso.with(context);
        this.mImageUrl = ImageUrl.getInstance(context);
    }

    @Override
    public int getCount() {
        return mRecipes.size();
    }

    @Override
    public Recipe.EmbeddedRecipe getItem(int position) {
        return mRecipes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_list_recipe, parent, false);
        }
        Recipe.EmbeddedRecipe recipe = getItem(position);
        TextView title = (TextView) convertView.findViewById(R.id.item_recipe_title);
        title.setText(recipe.getTitle());

        TextView subtitle = (TextView) convertView.findViewById(R.id.item_recipe_subtitle);
        subtitle.setText(String.format("Por %s", recipe.getAuthor().getDisplayName()));

        ImageView image = (ImageView) convertView.findViewById(R.id.item_recipe_image);
        mPicasso.load(mImageUrl.getFotoUrl(recipe.getMainImage(), ImageUrl.TipoFoto.TIPO_MINIATURA)).placeholder(R.drawable.placeholder).into(image);

        return convertView;
    }
}
