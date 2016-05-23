package ivamluz.marvelshelf.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.ui.fragments.CharactersListFragment;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    @BindView(R.id.search_field)
    protected TextView mTextSearchField;

    private FragmentManager mFragmentManager;
    private CharactersListFragment mSearchResultsFragment;

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, SearchActivity.class);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);

        mFragmentManager = getSupportFragmentManager();

        setupSearchResultsFragment(savedInstanceState);

        configToolbar();
    }

    private void setupSearchResultsFragment(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mSearchResultsFragment = (CharactersListFragment) getSupportFragmentManager().getFragment(savedInstanceState, CharactersListFragment.TAG);
        } else {
            mSearchResultsFragment = CharactersListFragment.newInstance(CharactersListFragment.LIST_TYPE_SEARCH);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.search_results_fragment_container, mSearchResultsFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Toast.makeText(this, "search clicked", Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(mTextSearchField.getText())) {
                    mSearchResultsFragment.search(mTextSearchField.getText().toString());
                }
                break;

            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void configToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark, getTheme()));
        }
    }
}
