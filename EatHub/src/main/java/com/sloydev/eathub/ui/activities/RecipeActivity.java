package com.sloydev.eathub.ui.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.manuelpeinado.fadingactionbar.FadingActionBarHelper;
import com.sloydev.eathub.R;
import com.sloydev.eathub.model.Recipe;
import com.sloydev.eathub.ui.fragments.RecipeFragment;

public class RecipeActivity extends Activity {

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_SERIALIZED = "recipe";

    public static Intent getIntent(Context context, String id) {
        Intent i = new Intent(context, RecipeActivity.class);
        i.putExtra(EXTRA_ID, id);
        return i;
    }

    public static Intent getIntent(Context context, Recipe recipe) {
        Intent i = new Intent(context, RecipeActivity.class);
        i.putExtra(EXTRA_SERIALIZED, recipe);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // Si savedInstanceState NO es nulo, el sistema se encarga de recuperar el fragment
        if (savedInstanceState == null) {
            RecipeFragment f;
            Object serial = getIntent().getSerializableExtra(EXTRA_SERIALIZED);
            if (serial != null) {
                Recipe r = (Recipe) serial;
                f = RecipeFragment.newInstance(r);
            } else {
                String id = getIntent().getStringExtra(EXTRA_ID);
                if (id == null) {
                    throw new IllegalStateException("No se recibió receta ni ID pra mostrar");
                }
                f = RecipeFragment.newInstance(id);
            }

            getFragmentManager().beginTransaction()
                    .add(R.id.container, f)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


}
