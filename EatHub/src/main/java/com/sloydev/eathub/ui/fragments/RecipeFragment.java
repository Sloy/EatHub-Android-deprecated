package com.sloydev.eathub.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.manuelpeinado.fadingactionbar.FadingActionBarHelper;
import com.sloydev.eathub.ImageUrl;
import com.sloydev.eathub.R;
import com.sloydev.eathub.model.Recipe;
import com.sloydev.eathub.ui.activities.ProfileActivity;
import com.sloydev.eathub.ui.activities.RecipeActivity;
import com.sloydev.eathub.ui.animation.AnimatorPath;
import com.sloydev.eathub.ui.animation.PathEvaluator;
import com.sloydev.eathub.ui.animation.PathPoint;
import com.sloydev.eathub.ui.widgets.CommentsView;
import com.sloydev.eathub.ui.widgets.IngredientsView;
import com.sloydev.eathub.ui.widgets.StepsView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.w3c.dom.Text;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RecipeFragment extends BaseFragment {

    FadingActionBarHelper mFadingHelper;

    Recipe mRecipe;

    /* Views */
    @InjectView(R.id.recipe_header_title) TextView mTitle;
    @InjectView(R.id.recipe_header_subtitle) TextView mSubtitle;
    @InjectView(R.id.recipe_header_avatar) ImageView mAvatar;
    @InjectView(R.id.recipe_header_image) ImageView mMainImage;
    @InjectView(R.id.recipe_data_time) TextView mTime;
    @InjectView(R.id.recipe_data_difficulty) TextView mDifficulty;
    @InjectView(R.id.recipe_data_serves) TextView mServes;
    @InjectView(R.id.recipe_description) TextView mDescription;
    @InjectView(R.id.recipe_ingredients) IngredientsView mIngredients;
    @InjectView(R.id.recipe_notes) TextView mNotes;
    @InjectView(R.id.recipe_notes_title) TextView mNotesTitle;
    @InjectView(R.id.recipe_steps) StepsView mSteps;
    @InjectView(R.id.recipe_comments) CommentsView mComments;


    public static RecipeFragment newInstance(String id) {
        RecipeFragment f = new RecipeFragment();
        Bundle extras = new Bundle();
        extras.putString(RecipeActivity.EXTRA_ID, id);
        f.setArguments(extras);
        return f;
    }

    public static RecipeFragment newInstance(Recipe recipe) {
        RecipeFragment f = new RecipeFragment();
        Bundle extras = new Bundle();
        extras.putSerializable(RecipeActivity.EXTRA_SERIALIZED, recipe);
        f.setArguments(extras);
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFadingHelper = new FadingActionBarHelper()
                .actionBarBackground(R.drawable.ab_background)
                .headerLayout(R.layout.fragment_recipe_header_image)
                .headerOverlayLayout(R.layout.fragment_recipe_header_overlay)
                .contentLayout(R.layout.fragment_recipe_content)
                .lightActionBar(false);
        mFadingHelper.initActionBar(activity);
//        activity.getActionBar().setSubtitle("Piruletas caseras");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = mFadingHelper.createView(inflater);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Recipe recipe = (Recipe) (savedInstanceState != null ? savedInstanceState.getSerializable(RecipeActivity.EXTRA_SERIALIZED) : getArguments().getSerializable(RecipeActivity.EXTRA_SERIALIZED));
        if (recipe != null) {
            showRecipe(recipe);
        } else {
            final String id = savedInstanceState != null ? savedInstanceState.getString(RecipeActivity.EXTRA_ID) : getArguments().getString(RecipeActivity.EXTRA_ID);

            new AsyncTask<Void, Void, Recipe>() {
                @Override
                protected Recipe doInBackground(Void... params) {
                    return getEatHubService().getRecipe(id);
                }

                @Override
                protected void onPostExecute(Recipe recipe) {
                    if (getActivity()==null) return;
                    if (recipe != null) {
                        showRecipe(mRecipe);
                    } else {
                        //TODO mostrar info
                        Toast.makeText(getActivity(), "Error: recipe null", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        }
    }

    private void showRecipe(Recipe r) {
        Activity context = getActivity();
        if (context == null) {
            return;
        }
        mRecipe = r;

        // Header
        mTitle.setText(r.getTitle());
        mSubtitle.setText(getString(R.string.recipe_author_pattern, r.getAuthor().getDisplayName()));
        ImageUrl imageUrl = ImageUrl.getInstance(context);
        Picasso.with(context).load(imageUrl.getFotoUrl(r.getMainImage(), ImageUrl.TipoFoto.TIPO_GRANDE)).placeholder(R.drawable.placeholder).into(mMainImage);
        Picasso.with(context).load(imageUrl.getFotoUrl(r.getAuthor().getAvatar(), ImageUrl.TipoFoto.TIPO_AVATAR)).placeholder(R.drawable.placeholder).noFade().into(mAvatar, new Callback() {
            @Override
            public void onSuccess() {
                mAvatar.setVisibility(View.VISIBLE);
                // Vía https://www.youtube.com/watch?v=JVGg4zPRHNE
                /*float delta = mAvatar.getContext().getResources().getDimensionPixelSize(R.dimen.avatar_size);
                AnimatorPath path = new AnimatorPath();
                path.moveTo(0, 0);
                path.curveTo((delta / 2), delta, 0, delta / 2, 0, 0);
                final ObjectAnimator anim = ObjectAnimator.ofObject(RecipeFragment.this, "avatarTranslation",
                        new PathEvaluator(), path.getPoints().toArray());
                anim.setInterpolator(new DecelerateInterpolator());
                anim.setDuration(600);
                anim.setStartDelay(100);
                anim.start();*/

//                mAvatar.animate().translationX(0).translationY(0).setDuration(600).setInterpolator(new DecelerateInterpolator()).start();
            }

            @Override
            public void onError() {
            }
        });
        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle activityOptions = ActivityOptionsCompat.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight()).toBundle();
                Intent profileIntent = ProfileActivity.getIntent(getActivity(), mRecipe.getAuthor().getUsername()).putExtra(ProfileActivity.EXTRA_IMAGE, mRecipe.getMainImage());
                ActivityCompat.startActivity(getActivity(), profileIntent, activityOptions);
            }
        });

        // Data
        mTime.setText(getString(R.string.recipe_time_pattern, r.getTime().getTotal()));
        mDifficulty.setText(getResources().getStringArray(R.array.recipe_difficulty)[r.getDifficulty() - 1]);
        mServes.setText(r.getServes());

        mDescription.setText(r.getDescription());
        mIngredients.setIngredients(r.getIngredients());

        String notes = r.getNotes();
        if (notes != null && !TextUtils.isEmpty(notes)) {
            mNotes.setText(notes);
        } else {
            mNotes.setVisibility(View.GONE);
            mNotesTitle.setVisibility(View.GONE);
        }

        mSteps.setSteps(r.getSteps());
        mComments.setComments(r.getComments());
    }

    public void setAvatarTranslation(PathPoint newTrans) {
        mAvatar.setTranslationX(newTrans.mX);
        mAvatar.setTranslationY(newTrans.mY);
    }

}