package com.sloydev.eathub.ui.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.sloydev.eathub.BlurTransformation;
import com.sloydev.eathub.R;
import com.sloydev.eathub.model.Recipe;
import com.sloydev.eathub.model.User;
import com.sloydev.eathub.ui.fragments.BaseProfileFragment;
import com.sloydev.eathub.ui.fragments.DummyFragment;
import com.sloydev.eathub.ui.fragments.ProfileInfoFragment;
import com.sloydev.eathub.ui.fragments.ProfileRecipesFragment;
import com.sloydev.eathub.ui.widgets.ObservableScrollView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProfileActivity extends BaseActivity implements ObservableScrollView.Callbacks, PagerSlidingTabStrip.OnTabSelectedListener {

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_SERIALIZED = "user";
    public static final String EXTRA_IMAGE = "image";

    @InjectView(R.id.profile_tabs) PagerSlidingTabStrip mTabs;
    @InjectView(R.id.profile_header) View mProfileHeader;
    @InjectView(R.id.profile_content) View mProfileContent;
    @InjectView(R.id.profile_scroll) ObservableScrollView mScrollView;
    @InjectView(R.id.profile_name) TextView mProfileName;
    @InjectView(R.id.profile_avatar) ImageView mProfileAvatar;
    @InjectView(R.id.profile_header_background) FrameLayout mProfileHeaderBackground;
    @InjectView(R.id.profile_header_background_image) ImageView mProfileBackgroundImage;

    private int mHiddenHeight;
    private int mNameTotalOffset;
    private User mUser;
    private String mImageExtra;
    private int mHomeIconTop;
    private int mHomeIconLeft;
    private int mAvatarLeft;
    private int mAvatarTop;
    private RectF mRectAvatar;
    private RectF mRectHome;

    private Bundle mProfileFragmentExtras;

    public static Intent getIntent(Context context, String username) {
        Intent i = new Intent(context, ProfileActivity.class);
        i.putExtra(EXTRA_ID, username);
        return i;
    }

    public static Intent getIntent(Context context, User user) {
        Intent i = new Intent(context, ProfileActivity.class);
        i.putExtra(EXTRA_SERIALIZED, user);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.inject(this);
        mProfileFragmentExtras = new Bundle();

        mImageExtra = getIntent().getStringExtra(EXTRA_IMAGE);
        if (savedInstanceState == null) {
            User user = (User) getIntent().getSerializableExtra(EXTRA_SERIALIZED);
            if (user != null) {
                setProfileInfo(user);
            } else {
                final String username = getIntent().getStringExtra(EXTRA_ID);

                new AsyncTask<Void, Void, User>() {
                    @Override
                    protected User doInBackground(Void... params) {
                        return getEatHubService().getUser(username);
                        //TODO capturar error
                    }

                    @Override
                    protected void onPostExecute(User user) {
                        if (user != null) {
                            setProfileInfo(user);
                        } else {
                            Toast.makeText(ProfileActivity.this, "Error: user null", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
            }
        }

        // Coloca las pestañas, pero aún no el contenido.
        mTabs.setTextTabs(new String[]{"Profile", "Recipes", "Likes", "Comments"});


        mScrollView.setCallbacks(this);

        assert mProfileHeader.getViewTreeObserver() != null;
        ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Extrae valores para las animaciones y el scroll
                int tabsHeight = mTabs.getHeight();
                int headerHeight = mProfileHeader.getHeight();
                int actionBarHeight = mProfileName.getHeight();
                mHiddenHeight = headerHeight - tabsHeight - actionBarHeight;

                int homeId = Resources.getSystem().getIdentifier("home", "id", "android");
                View homeIcon = findViewById(homeId);
                mHomeIconLeft = homeIcon.getLeft();
                mHomeIconTop = homeIcon.getTop();
                int homeIconRight = homeIcon.getRight();
                mRectHome = getOnScreenRect(new RectF(), homeIcon);
                mRectAvatar = new RectF();
                mHomeIconLeft = homeIcon.getLeft();
                int nameLeft = mProfileName.getLeft();
                mAvatarLeft = mProfileAvatar.getLeft();
                mAvatarTop = mProfileAvatar.getTop();
                mNameTotalOffset = nameLeft - homeIconRight - getResources().getDimensionPixelSize(R.dimen.eight_dip);

                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mProfileContent.getLayoutParams();
                lp.setMargins(0, headerHeight, 0, 0);
                mProfileContent.setLayoutParams(lp);

                // Establece el alto de la imagen de fondo igual al tamaño de la cabecera
                //TODO puede hacerse con el layout?
                ViewGroup.LayoutParams lpImage = mProfileBackgroundImage.getLayoutParams();
                lpImage.height = mProfileHeader.getHeight();
                mProfileBackgroundImage.setLayoutParams(lpImage);
            }
        };
        ViewTreeObserver.OnGlobalLayoutListener listener = onGlobalLayoutListener;
        mProfileHeader.getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }

    private void setProfileInfo(User user) {
        mUser = user;
        User.Profile profile = user.getProfile();
        // Prepara el extra para los fragments de info del profile
        mProfileFragmentExtras.putSerializable(BaseProfileFragment.USER_EXTRA, mUser);
        // Coloca el listener de las pestañas, lo que hará que carguen el fragment correspondiente.
        mTabs.setOnTabSelectedListener(this, true);


        Picasso.with(this).load(profile.getAvatar()).noFade().into(mProfileAvatar, new Callback() {
            @Override
            public void onSuccess() {
                mProfileAvatar.setVisibility(View.VISIBLE);
                mProfileAvatar.setAlpha(0f);
                mProfileAvatar.animate().alpha(1f).setDuration(500).start();
            }

            @Override
            public void onError() {

            }
        });
        mProfileName.setText(profile.getDisplayName());

        // Imagen de fondo:
        /*new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                // Decide la imagen de fondo
//                    return mUser.getProfile().getAvatar();
                if (mImageExtra == null) {
                    //TODO usar una del usuario y no una aleatoria
                    List<Recipe> recipes = getEatHubService().listRecipes();
                    return recipes.get(new Random().nextInt(recipes.size())).getMainImage();
                } else {
                    return mImageExtra;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                BlurTransformation transformation = new BlurTransformation(ProfileActivity.this); //Strong reference
                Picasso.with(ProfileActivity.this).load(s).transform(transformation).into(mProfileBackgroundImage, new Callback() {
                    @Override
                    public void onSuccess() {
//                        mProfileHeaderBackground.setForeground(new ColorDrawable(0x66000000));
                    }

                    @Override
                    public void onError() {

                    }
                });
//                Picasso.with(ProfileActivity.this).load(s).into(mProfileBackgroundImage);
            }
        }.execute();*/
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(EXTRA_SERIALIZED, mUser);
    }

    /* --- Animation related --- */

    @Override
    public void onScrollChanged(int scrollY) {
        if (scrollY > mHiddenHeight) {
            mProfileHeader.setTranslationY(scrollY - mHiddenHeight);
            mProfileName.setTranslationX(-mNameTotalOffset);
        } else {
            mProfileHeader.setTranslationY(0);
            mProfileHeaderBackground.setTranslationY(scrollY / 2);
            float factor = (float) scrollY / (float) mHiddenHeight;
            float nameOffset = mNameTotalOffset * factor;
            mProfileName.setTranslationX(-nameOffset);
            // Move avatar to home icon position
            /*getOnScreenRect(mRectAvatar, mProfileAvatar);
            float avatarTotalOffsetX = mAvatarLeft - mHomeIconLeft;
            float avatarTotalOffsetY = mRectAvatar.top - mRectHome.top;
            mProfileAvatar.setTranslationY(avatarTotalOffsetY * factor);*/
            mProfileAvatar.setTranslationX(nameOffset);
        }

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent() {

    }

    private RectF getOnScreenRect(RectF rect, View view) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        return rect;
    }


    @Override
    public void onTabSelected(int position) {
        // Change fragment
        String fname;
        switch (position) {
            case 0:
                fname = ProfileInfoFragment.class.getName();
                break;
            case 1:
                fname = ProfileRecipesFragment.class.getName();
                break;
            default:
                fname = DummyFragment.class.getName();

        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        // Quita el actual
        Fragment current = getFragmentManager().findFragmentById(R.id.profile_content);
        if (current != null) {
            transaction.detach(current);
        }
        // Pone el nuevo. Attach si existe, add si no.
        Fragment f = getFragmentManager().findFragmentByTag(fname);
        if (f == null) {
            Log.d("android", "Instanciando fragment " + fname);
            f = Fragment.instantiate(this, fname, mProfileFragmentExtras);
            transaction.add(R.id.profile_content, f, fname);
        } else {
            transaction.attach(f);
        }
        transaction.commit();

        int scrollY = mScrollView.getScrollY();
        mScrollView.setScrollY(Math.min(scrollY, mHiddenHeight));
    }
}
