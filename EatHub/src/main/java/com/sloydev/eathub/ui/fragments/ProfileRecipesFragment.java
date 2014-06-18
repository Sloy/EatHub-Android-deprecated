package com.sloydev.eathub.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sloydev.eathub.ImageUrl;
import com.sloydev.eathub.R;
import com.sloydev.eathub.model.Recipe;
import com.sloydev.eathub.ui.adapter.RecipeListAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProfileRecipesFragment extends BaseProfileFragment {

    @InjectView(R.id.profile_recipes_list) LinearLayout mLista;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_recipes, container, false);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mLista.removeAllViews();
        Picasso picasso = Picasso.with(getActivity());
        ImageUrl imageUrl = ImageUrl.getInstance(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        List<Recipe.EmbeddedRecipe> recipes = getUser().getProfile().getRecipes();
        for (Recipe.EmbeddedRecipe r : recipes) {
            View row = inflater.inflate(R.layout.item_list_recipe, mLista, false);
            TextView title = (TextView) row.findViewById(R.id.item_recipe_title);
            title.setText(r.getTitle());
            TextView subtitle = (TextView) row.findViewById(R.id.item_recipe_subtitle);
            subtitle.setText(String.format("Por %s", r.getAuthor().getDisplayName()));
            ImageView image = (ImageView) row.findViewById(R.id.item_recipe_image);
            picasso.load(imageUrl.getFotoUrl(r.getMainImage(), ImageUrl.TipoFoto.TIPO_MINIATURA)).placeholder(R.drawable.placeholder).into(image);
            mLista.addView(row);
        }

    }


}
