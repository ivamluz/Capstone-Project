package ivamluz.marvelshelf.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.karumi.marvelapiclient.model.ComicDto;
import com.karumi.marvelapiclient.model.SeriesDto;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ivamluz.marvelshelf.BuildConfig;
import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.data.model.MarvelCharacter;
import ivamluz.marvelshelf.managers.BookmarksManager;
import ivamluz.marvelshelf.ui.fragments.CharacterDetailsFragment;
import ivamluz.marvelshelf.ui.fragments.workers.ComicsLoaderWorkerFragment;
import ivamluz.marvelshelf.ui.fragments.workers.SeriesLoaderWorkerFragment;

public class CharacterDetailsActivity extends BaseActivity implements ComicsLoaderWorkerFragment.TaskCallbacks, SeriesLoaderWorkerFragment.TaskCallbacks {
    private static final String EXTRA_CHARACTER = String.format("%s.character", BuildConfig.APPLICATION_ID);

    private MarvelCharacter mCharacter;

    private CharacterDetailsFragment mCharacterDetailsFragment;
    private Picasso mPicasso;

    @BindView(R.id.image_details_thumb)
    protected ImageView mCharacterThumbnail;

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    private BookmarksManager mBookmarksManager;

    public static Intent newIntent(Context packageContext, MarvelCharacter character) {
        Intent intent = new Intent(packageContext, CharacterDetailsActivity.class);
        intent.putExtra(EXTRA_CHARACTER, character);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        setupBookmarksManager();

        configToolbar(mToolbar);

        mPicasso = MarvelShelfApplication.getInstance().getPicasso();

        mCharacter = getIntent().getParcelableExtra(EXTRA_CHARACTER);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCharacterThumbnail.setTransitionName(getString(R.string.shared_transition_character_image));
        }

        bindCharacterInfo();
        setupCharacterDetailsFragment(savedInstanceState);

        if (savedInstanceState == null) {
            String title = String.format("%s: %s", CharacterDetailsActivity.class.getSimpleName(), mCharacter.getName());
            trackScreenView(title);
        }
    }

    private void setupBookmarksManager() {
        mBookmarksManager = new BookmarksManager(this, new BookmarksManager.BookmarkCallbacks() {
            @Override
            public void onBookmarkToogled(boolean isBookmarked) {
                mCharacter.setBookmarked(isBookmarked);

                invalidateOptionsMenu();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, CharacterDetailsFragment.TAG, mCharacterDetailsFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_character, menu);

        setBookmarkMenuItemIcon(menu);

        return true;
    }

    private void setBookmarkMenuItemIcon(Menu menu) {
        int bookmarkIcon = R.drawable.ic_bookmark_border;
        if (mCharacter.isBookmarked()) {
            bookmarkIcon = R.drawable.ic_bookmark_filled;
        }

        MenuItem bookmarkMenuItem = menu.findItem(R.id.action_toggle_bookmark);
        bookmarkMenuItem.setIcon(ResourcesCompat.getDrawable(getResources(), bookmarkIcon, getTheme()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_toggle_bookmark:
                mBookmarksManager.toggleBookmark(mCharacter.getId());
                break;

            case R.id.action_share:
                share(mCharacter.getName(), mCharacter.getDetailsUrl());
                break;

            case R.id.action_see_details:
                openUrlExternally(mCharacter.getDetailsUrl());
                break;

            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void bindCharacterInfo() {
        mPicasso.load(mCharacter.getThumbnailUrl())
                .fit()
                .centerCrop()
                .error(R.drawable.character_placeholder_landscape)
                .into(mCharacterThumbnail);

        getSupportActionBar().setTitle(mCharacter.getName());
    }

    private void setupCharacterDetailsFragment(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mCharacterDetailsFragment = (CharacterDetailsFragment) getSupportFragmentManager().getFragment(savedInstanceState, CharacterDetailsFragment.TAG);
        } else {
            mCharacterDetailsFragment = CharacterDetailsFragment.newInstance(mCharacter.getId());

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.details_fragment_container, mCharacterDetailsFragment)
                    .commit();
        }
    }

    @Override
    public void onComicsLoadingPreExecute() {
        mCharacterDetailsFragment.onComicsLoadingPreExecute();
    }

    @Override
    public void onComicsLoadingCancelled() {
        mCharacterDetailsFragment.onComicsLoadingCancelled();
    }

    @Override
    public void onComicsLoaded(List<ComicDto> comics) {
        mCharacterDetailsFragment.onComicsLoaded(comics);
    }

    @Override
    public void onSeriesLoadingPreExecute() {
        mCharacterDetailsFragment.onSeriesLoadingPreExecute();
    }

    @Override
    public void onSeriesLoadingCancelled() {
        mCharacterDetailsFragment.onSeriesLoadingCancelled();
    }

    @Override
    public void onSeriesLoaded(List<SeriesDto> series) {
        mCharacterDetailsFragment.onSeriesLoaded(series);
    }
}
