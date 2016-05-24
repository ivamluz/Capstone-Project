package ivamluz.marvelshelf.ui.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import butterknife.BindView;
import butterknife.ButterKnife;
import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;
import ivamluz.marvelshelf.ui.fragments.CharactersListFragment;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String KEY_FRAGMENT = "fragment";
    private static final String KEY_TITLE = "title";

    private FragmentManager mFragmentManager;

    @BindView(R.id.drawer_layout)
    protected DrawerLayout mDrawer;

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    @BindView(R.id.nav_view)
    protected NavigationView mNavigationView;

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        mFragmentManager = getSupportFragmentManager();

        if (savedInstanceState != null) {
            Fragment fragment = getSupportFragmentManager().getFragment(savedInstanceState, KEY_FRAGMENT);
            String title = savedInstanceState.getString(KEY_TITLE, "");

            if (fragment != null) {
                setTitle(title);
                mFragmentManager.beginTransaction().replace(R.id.content_holder, fragment).commit();
            }
        } else {
            Fragment fragment = CharactersListFragment.newInstance(CharactersListFragment.LIST_TYPE_ALL);
            setTitle(getString(R.string.characters_all));
            mFragmentManager.beginTransaction().replace(R.id.content_holder, fragment).commit();

            trackScreenView();
        }

        MarvelShelfApplication application = (MarvelShelfApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    private void trackScreenView() {
        trackScreenView(String.valueOf(getTitle()));
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.nav_characters_all:
                fragment = CharactersListFragment.newInstance(CharactersListFragment.LIST_TYPE_ALL);
                break;
            case R.id.nav_characters_seen:
                fragment = CharactersListFragment.newInstance(CharactersListFragment.LIST_TYPE_SEEN);
                break;
            case R.id.nav_bookmarks:
            default:
                fragment = CharactersListFragment.newInstance(CharactersListFragment.LIST_TYPE_BOOKMARKS);
        }

        mFragmentManager.beginTransaction().replace(R.id.content_holder, fragment).commit();


        item.setChecked(true);
        setTitle(item.getTitle());

        trackScreenView();

        mDrawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
