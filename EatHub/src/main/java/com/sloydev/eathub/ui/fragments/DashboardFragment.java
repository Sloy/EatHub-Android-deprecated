package com.sloydev.eathub.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sloydev.eathub.BuildConfig;
import com.sloydev.eathub.ImageUrl;
import com.sloydev.eathub.R;
import com.sloydev.eathub.model.Recipe;
import com.sloydev.eathub.ui.activities.RecipeActivity;
import com.sloydev.eathub.ui.fragments.tardis.Item;
import com.sloydev.eathub.ui.fragments.tardis.MultiItemRow;
import com.sloydev.eathub.ui.fragments.tardis.MultiRowAdapter;
import com.sloydev.eathub.ui.fragments.tardis.Row;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

public class DashboardFragment extends BaseFragment {

    @InjectView(android.R.id.list) ListView mListView;
    @InjectView(R.id.dashboard_loading) View mLoadingGroup;
    @InjectView(R.id.dashboard_loading_smoke) View mLoadingSmoke;
    @InjectView(R.id.dashboard_loading_base) View mLoadingBase;
    @InjectView(R.id.dashboard_loading_text) TextView mLoadingText;

    private List<Recipe> mRecipes;
    private int mBaseMovementOffset;
    private int mSmokeMovementOffset;
    private ObjectAnimator mAnimSmoke;
    private ObjectAnimator mAnimBase;
    private boolean mLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.inject(this, v);
        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSmokeMovementOffset = getResources().getDimensionPixelOffset(R.dimen.dashboard_loading_smoke_offset);
        mBaseMovementOffset = getResources().getDimensionPixelOffset(R.dimen.dashboard_loading_base_offset);

        if (mRecipes != null && mRecipes.size() > 0) {
            setRecipes(mRecipes);
        } else {
            loadLatestRecipes();
        }
    }


    /* ---- Logic methods --- */

    private void loadLatestRecipes() {

        new AsyncTask<Void, Void, List<Recipe>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                setLoading(true);
                setListVisibility(false);
            }

            @Override
            protected List<Recipe> doInBackground(Void... params) {
                if (BuildConfig.BUILD_TYPE.equals("demo")) {
                    try {
                        Log.d("Eathub", "Sleeping ^^'");
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    return getEatHubService().listRecipes();
                } catch (RetrofitError error) {
                    Log.e("Eathub", error.getResponse().getReason(), error);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Recipe> recipes) {
                Activity context = getActivity();
                if (context != null) {
                    setLoading(false);
                    if (recipes == null || recipes.size() <= 0) {
                        Toast.makeText(context, "No hay recetas?", Toast.LENGTH_SHORT).show();
                    } else {
                        setRecipes(recipes);
                        setListVisibility(true);
                    }
                }
            }
        }.execute();
    }


    /* ---- UI methods --- */

    private void setRecipes(List<Recipe> recipes) {
        mRecipes = recipes;
        int maxColumnas = getResources().getInteger(R.integer.dashboard_max_columnas);
        ArrayList<Row> filas = new ArrayList<>();

        // -- La primera fila
        int columnasPrimeraFila = getResources().getInteger(R.integer.dashboard_columnas_primera_fila);
        RecetaItem[] primeraFila = new RecetaItem[columnasPrimeraFila];
        for (int i = columnasPrimeraFila - 1; i >= 0; i--) {
            Recipe r = recipes.remove(i);
            primeraFila[i] = new RecetaItem(getActivity(), r, RecetaItem.Altura.GRANDE);
        }
        filas.add(new MultiItemRow(getActivity(), R.dimen.dashboard_item_height_big, R.dimen.dashboard_item_margin, new int[]{3, 3, 2}, primeraFila));

        // -- "El montón"
        RecetaItem[] arrayFila = new RecetaItem[maxColumnas];
        int columna = 0;
        for (int i = 0; i < recipes.size(); i++) {
            Recipe r = recipes.get(i);
            RecetaItem item = new RecetaItem(getActivity(), r, RecetaItem.Altura.NORMAL);
            arrayFila[columna] = item;
            columna++;

            // Está completa la fila?
            if (columna >= maxColumnas) {
                columna = 0;
                filas.add(new MultiItemRow(getActivity(), R.dimen.dashboard_item_height, R.dimen.dashboard_item_margin, arrayFila));
                arrayFila = new RecetaItem[maxColumnas];
            }
            //FIXME la última fila no se muestra si no está completa :(
        }
        if (columna != 0 && columna < maxColumnas) { // Fila incompleta
            RecetaItem[] filaSobras = new RecetaItem[columna];
            for (int i = 0; i < columna; i++) {
                filaSobras[i] = arrayFila[i];
            }
            filas.add(new MultiItemRow(getActivity(), R.dimen.dashboard_item_height, R.dimen.dashboard_item_margin, filaSobras));
        }

        MultiRowAdapter adapter = new MultiRowAdapter(getActivity(), filas) {
            @Override
            public int getInternalViewType(Item item) {
                RecetaItem itemReceta = (RecetaItem) item;
                if (itemReceta.altura == RecetaItem.Altura.NORMAL) {
                    return 0;
                } else {
                    return 1;
                }
            }

            @Override
            public int getInternalViewTypeCount() {
                return 2;
            }
        };
        mListView.setAdapter(adapter);
        mListView.setRecyclerListener(adapter);
    }

    private void setLoading(boolean loading) {
        mLoading = loading;
        if (loading) {
            // Show the group
            mLoadingGroup.setVisibility(View.VISIBLE);
            mLoadingGroup.setAlpha(0f);
            mLoadingGroup.animate().alpha(1f).setDuration(500).setListener(null).start();

            // Animate the smoke
            if (mAnimSmoke == null) {
                mAnimSmoke = ObjectAnimator.ofFloat(mLoadingSmoke, "translationX", 0, -mSmokeMovementOffset, 0, mSmokeMovementOffset, 0);
                mAnimSmoke.setInterpolator(new LinearInterpolator());
//                mAnimSmoke.setInterpolator(new AccelerateDecelerateInterpolator());
                mAnimSmoke.setDuration(1000);
            }
            mAnimSmoke.setRepeatCount(ValueAnimator.INFINITE);
            mAnimSmoke.start();

            // Animate the base
            if (mAnimBase == null) {
                mAnimBase = ObjectAnimator.ofFloat(mLoadingBase, "translationX", 0, -mBaseMovementOffset, 0, mBaseMovementOffset, 0, -mBaseMovementOffset, 0, mBaseMovementOffset, 0);
                mAnimBase.setDuration(500);
                mAnimBase.setInterpolator(new AccelerateDecelerateInterpolator());
                mAnimBase.setStartDelay(2000);
                // Animación que no hace nada, sólo está para crear un delay entre las repeticiones
                /*ObjectAnimator mAnimBaseFake = ObjectAnimator.ofFloat(mLoadingBase, "translationX", 0, 0);
                mAnimBaseFake.setDuration(5000);
                mAnimBase = new AnimatorSet();
                mAnimBase.playSequentially(mAnimBaseFake, mAnimBaseReal);*/
                mAnimBase.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (mLoading) {
                            mAnimBase.start();
                        }
                    }
                });
            }
            mAnimBase.start();

        } else {
            // Hide the group
            mLoadingGroup.animate().alpha(0f).setDuration(500).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoadingGroup.setVisibility(View.VISIBLE);
                }
            }).start();

            // Stop smoke animation
            if (mAnimSmoke != null) {
                mAnimSmoke.setRepeatCount(0);
            }

            // Base animation stops by itself with mLoading==false
        }
    }

    private void setListVisibility(boolean show) {
        if (show) {
            mListView.setVisibility(View.VISIBLE);
            mListView.setAlpha(0f);
            mListView.animate().alpha(1f).setDuration(500).setListener(null).start();
        } else {
            mListView.animate().alpha(0f).setDuration(500).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mListView.setVisibility(View.GONE);
                }
            }).start();
        }
    }

    /* ---- Utility classes--- */

    private static class RecetaItem extends Item {

        public enum Altura {
            NORMAL,
            GRANDE,
        }

        private Activity context;

        private String id;
        private String titulo;
        private String subtitulo;
        private String urlFoto;
        private Altura altura;

        private Recipe recipe;

        private static class ViewHolder {

            TextView titulo;
            TextView subtitulo;
            ImageView foto;
        }

        public RecetaItem(Activity context, String id, String titulo, String subtitulo, String urlFoto, Altura altura) {
            this.context = context;
            this.id = id;
            this.titulo = titulo;
            this.subtitulo = subtitulo;
            this.urlFoto = urlFoto;
            this.altura = altura;
        }

        public RecetaItem(Activity context, Recipe receta, Altura altura) {
            this(context, receta.getId(), receta.getTitle(), receta.getAuthor().getDisplayName(), receta.getMainImage(), altura);
            recipe = receta;
        }

        @Override
        public View createView(ViewGroup parent) {
            int layout;
            switch (altura) {
                case GRANDE:
                    layout = R.layout.item_dashboard_big;
                    break;
                case NORMAL:
                default:
                    layout = R.layout.item_dashboard;
                    break;
            }
            return LayoutInflater.from(context).inflate(layout, parent, false);
        }

        @Override
        protected View bindView(View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null) {
                holder = new ViewHolder();
                holder.titulo = (TextView) view.findViewById(R.id.item_dashboard_titulo);
                holder.subtitulo = (TextView) view.findViewById(R.id.item_dashboard_subtitulo);
                holder.foto = (ImageView) view.findViewById(R.id.item_dashboard_foto);
                view.setTag(holder);
            }

            holder.titulo.setText(titulo);
            holder.subtitulo.setText(String.format("Por %s", subtitulo));

            if (BuildConfig.DEBUG) Picasso.with(context).setDebugging(true);
            Picasso.with(context).load(ImageUrl.getSoftInstance(context).getFotoUrl(urlFoto, ImageUrl.TipoFoto.TIPO_MINIATURA)).placeholder(R.drawable.placeholder).into(holder.foto);

            return view;
        }

        @Override
        public void onClick(View v) {
            Bundle extras = ActivityOptionsCompat.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight()).toBundle();
            Intent intent;
            if (recipe != null) {
                intent = RecipeActivity.getIntent(context, recipe);
            } else {
                intent = RecipeActivity.getIntent(context, id);
            }
            ActivityCompat.startActivity(context, intent, extras);
        }
    }

}
