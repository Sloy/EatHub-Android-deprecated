package com.sloydev.eathub.ui.activities;


import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sloydev.eathub.BuildConfig;
import com.sloydev.eathub.R;
import com.sloydev.eathub.model.User;
import com.sloydev.eathub.ui.UserSingleton;
import com.sloydev.eathub.ui.fragments.DashboardFragment;
import com.sloydev.eathub.ui.fragments.DummyFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RetrofitError;

public class MainActivity extends BaseActivity {

    private static final String EXTRA_DRAWER_POSITION = "drawer_position";
    private static final String EXTRA_CURRENT_TITLE = "mCurrentTitle";
    private static final int LOGIN_CODE = 1;

    @InjectView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @InjectView(R.id.left_drawer_list) ListView mDrawerList;
    @InjectView(R.id.left_drawer_profile) View mDrawerProfile;

    private ActionBarDrawerToggle mDrawerToggle;
    private String mCurrentTitle;
    private int mCurrentDrawerPosition = -1;
    private MenuAdapter mAdapter;

    /* --- Life cycle methods --- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        // Comienza
        startNormal(savedInstanceState);

        ((TextView) findViewById(R.id.left_drawer_version)).setText("Version " + BuildConfig.VERSION_NAME);
    }

    @Override
    /**
     * Guarda datos para restaurar tras un cambio de configuración
     */
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_CURRENT_TITLE, mCurrentTitle);
        outState.putInt(EXTRA_DRAWER_POSITION, mCurrentDrawerPosition);
    }

    @Override
    public void onBackPressed() {
        if (mCurrentDrawerPosition == 0) {
            super.onBackPressed();
        } else {
            doSelectDrawerItem(0);
        }
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOGIN_CODE:
                doUpdateLoginInfo();
                break;
        }

    }

    /* ---- Starting methods --- */

    private void startNormal(Bundle savedInstanceState) {
        setupActionBar();

        setupNavigationDrawer();

        // Coloca el Fragment que toque, por defecto el Dashboard
        /* Si se está re-creando la Activity por un cambio de configuración, comprueba el savedInstanceState.
         * Si es null se está creando por primera vez, y tengo que añadir manualmente los fragments.
         * Si no es null, el sistema se encargará de añadirlos automáticamente por mi.
         * Vía https://plus.google.com/+AndroidDevelopers/posts/3exHM3ZuCYM (Protip de Bruno Olivieira
         */
        if (savedInstanceState == null) {
            doSelectDrawerItem(0);
        } else {
            // Por lo menos restaura el título que tenía puesto, ya que los fragments los pone el sistema solo.
            doUpdateTitle(savedInstanceState.getString(EXTRA_CURRENT_TITLE));
            // Y el item seleccionado del Drawer
            doHighlightDrawerPosition(savedInstanceState.getInt(EXTRA_DRAWER_POSITION, -1));
        }

        doAutoLogin();
    }

    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            //invalidateOptionsMenu(); // Only needed if there is a previous screen to "startNormal"
        }
    }

    private void setupNavigationDrawer() {
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.drawer_icon,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                setTitle(mCurrentTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                setTitle(R.string.app_name);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        // Crea la lista de entradas del Drawer
        List<Object> items = new ArrayList<>();
        items.add(new ItemFragment(getString(R.string.title_dashboard), R.drawable.ic_action_recetas, DashboardFragment.class.getName()));
        items.add(new ItemFragment("Explora", R.drawable.ic_action_nevera, DummyFragment.class.getName()));
        items.add(new ItemFragment("Recomendaciones", R.drawable.ic_action_nevera, DummyFragment.class.getName()));
        items.add(new ItemFragment("Favoritas", R.drawable.ic_action_nevera, DummyFragment.class.getName()));

        mAdapter = new MenuAdapter(items);
        // Set the adapter for the list view
        mDrawerList.setAdapter(mAdapter);
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                doSelectDrawerItem(position);
            }
        });

        doUpdateLoginInfo();
    }

    private void doAutoLogin() {
        final UserSingleton userSingleton = UserSingleton.getUserSingleton();
        final String authorization = userSingleton.getStoredAuthorization(this);
        if (authorization != null) {
            new AsyncTask<Void, Void, User>() {
                @Override
                protected User doInBackground(Void... params) {
                    try {
                        return getEatHubService().login(authorization);

                    } catch (RetrofitError e) {
                        Log.e("Eathub", "Error en el login automático: " + e.getResponse().getReason(), e);
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(User user) {
                    if (user != null) {
                        userSingleton.setCurrentUser(user);
                        doUpdateLoginInfo();
                    }
                }
            }.execute();
        }

    }

    /* ---- UI actions methods ---- */

    /**
     * Swaps fragments in the main content view
     */
    private void doSelectDrawerItem(int position) {
        if (mCurrentDrawerPosition != position) {
            Object item = mAdapter.getItem(position);

            if (item instanceof ItemFragment) {
                ItemFragment itemFragment = (ItemFragment) item;
                // Carga el fragment que representa
                doReplaceFragment(itemFragment.mFragmentClassName, itemFragment.mExtras);
                doUpdateTitle(itemFragment.mTitle);
            }
            // Highlight the selected item, update the title, and close the drawer
            doHighlightDrawerPosition(position);
        }
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    private void doHighlightDrawerPosition(int position) {
        mCurrentDrawerPosition = position;
        mDrawerList.setItemChecked(position, true);
        mAdapter.setSelectedPosition(position);
    }

    private void doUpdateTitle(String title) {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            mCurrentTitle = title;
        }
    }

    /**
     * Muestra el Fragment cuyo nombre se pasa por parámetro, reemplazando el que esté actualmente.
     * NO se añade al Back Stack.
     * NO actualiza el Navigation Drawer.
     *
     * @param fname  Nombre del fragment que se va a mostrar. Se obtiene con NombreClase.class.getName()
     * @param extras Bundle opcional con datos extra para el fragment. Puede ser nulo.
     */
    private void doReplaceFragment(String fname, Bundle extras) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Quita el actual
        Fragment current = getFragmentManager().findFragmentById(R.id.main_container);
        if (current != null) {
            transaction.detach(current);
        }

        // Pone el nuevo. Attach si existe, add si no.
        Fragment f = getFragmentManager().findFragmentByTag(fname);
        if (f == null) {
            Log.d("android", "Instanciando fragment " + fname);
            f = Fragment.instantiate(this, fname, extras);
            transaction.add(R.id.main_container, f, fname);
        } else {
            transaction.attach(f);
        }
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.commit();
    }

    private void doUpdateLoginInfo() {
        // Login account button stuff
        TextView name = (TextView) mDrawerProfile.findViewById(R.id.left_drawer_profile_name);
        TextView email = (TextView) mDrawerProfile.findViewById(R.id.left_drawer_profile_email);
        ImageView avatar = (ImageView) mDrawerProfile.findViewById(R.id.left_drawer_profile_avatar);
        ImageView profileBackground = (ImageView) mDrawerProfile.findViewById(R.id.left_drawer_profile_background);

        final User currentUser = UserSingleton.getUserSingleton().getCurrentUser();
        if (currentUser != null) {
            name.setText(currentUser.getProfile().getDisplayName());
            email.setText(currentUser.getEmail());
            String avatarUrl = currentUser.getProfile().getAvatar();
            Picasso.with(this).load(avatarUrl).into(avatar);
            profileBackground.setImageResource(R.drawable.profile_background);
            //TODO fondo de una receta suya
            mDrawerProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle extras = ActivityOptionsCompat.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight()).toBundle();
                    ActivityCompat.startActivity(MainActivity.this, ProfileActivity.getIntent(MainActivity.this, currentUser), extras);
                }
            });
        } else {
            //TODO Logged out? Volver a poner los datos de inicio de sesión y tal
            mDrawerProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle extras = ActivityOptionsCompat.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight()).toBundle();
                    ActivityCompat.startActivityForResult(MainActivity.this, new Intent(MainActivity.this, LoginActivity.class), LOGIN_CODE, extras);
                }
            });

        }
    }

    /* ---- Utility classes ---- */

    private class MenuAdapter extends BaseAdapter {

        private int mSelectedPosition = -1;
        private List<Object> mItems;

        MenuAdapter(List<Object> items) {
            mItems = items;
        }

        public void setSelectedPosition(int position) {
            mSelectedPosition = position;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            if (position == getCount()) {
                return null;
            }
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            Object item = getItem(position);
            if (item instanceof Category) {
                return 0;
            } else if (item instanceof Item) {
                return 1;
            } else {
                return -1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public boolean isEnabled(int position) {
            return getItemViewType(position) > 0;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            Object item = getItem(position);

            switch (getItemViewType(position)) {
                case 0: // category
                    if (v == null) {
                        v = MainActivity.this.getLayoutInflater().inflate(R.layout.drawer_category, parent, false);
                    }

                    ((TextView) v).setText(((Category) item).mTitle);
                    break;

                case 1: // Item
                    Item itemItem = (Item) item;
                    if (v == null) {
                        v = MainActivity.this.getLayoutInflater().inflate(R.layout.drawer_item, parent, false);
                    }
                    TextView tv3 = (TextView) v.findViewById(android.R.id.text1);
                    tv3.setText(itemItem.mTitle);
                    ImageView iv = (ImageView) v.findViewById(android.R.id.icon2);
                    if (itemItem.mIconRes > 0) {
                        iv.setImageResource(itemItem.mIconRes);
                    } else {
                        iv.setVisibility(View.GONE);
                    }

                    if (position == mSelectedPosition) {
                        tv3.setTextAppearance(MainActivity.this, R.style.MenuDrawer_Widget_TitleText_Selected);
                        v.findViewById(android.R.id.icon1).setVisibility(View.VISIBLE);
                    } else {
                        tv3.setTextAppearance(MainActivity.this, R.style.MenuDrawer_Widget_TitleText);
                        v.findViewById(android.R.id.icon1).setVisibility(View.INVISIBLE);
                    }
                    break;
                default:
                    break;
            }

            return v;
        }
    }


    private static class Category {
        String mTitle;

        Category(String title) {
            mTitle = title;
        }
    }


    public static class Item {
        public String mTitle;
        int mIconRes;

        Item(String title, int iconRes) {
            mTitle = title;
            mIconRes = iconRes;
        }
    }

    public static class ItemFragment extends Item {

        public String mFragmentClassName;
        public Bundle mExtras;

        ItemFragment(String title, int iconRes, String fragmentClass) {
            super(title, iconRes);
            mFragmentClassName = fragmentClass;
        }

        ItemFragment(String title, int iconRes, String fragmentClass, Bundle extras) {
            this(title, iconRes, fragmentClass);
            mExtras = extras;
        }
    }

}
