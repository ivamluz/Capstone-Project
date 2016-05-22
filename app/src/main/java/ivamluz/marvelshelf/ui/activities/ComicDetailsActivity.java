package ivamluz.marvelshelf.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import ivamluz.marvelshelf.BuildConfig;
import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.data.model.MarvelComic;
import ivamluz.marvelshelf.ui.fragments.ComicDetailsFragment;

public class ComicDetailsActivity extends BaseDetailsActivity {
    private static final String EXTRA_COMIC = String.format("%s.comic", BuildConfig.APPLICATION_ID);

    private MarvelComic mComic;

    private ComicDetailsFragment mComicDetailsFragment;
    private Picasso mPicasso;

    @BindView(R.id.image_details_thumb)
    protected ImageView mComicThumbnail;

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    public static Intent newIntent(Context packageContext, MarvelComic comic) {
        Intent intent = new Intent(packageContext, ComicDetailsActivity.class);
        intent.putExtra(EXTRA_COMIC, comic);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        configToolbar(mToolbar);

        mPicasso = MarvelShelfApplication.getInstance().getPicasso();

        mComic = getIntent().getParcelableExtra(EXTRA_COMIC);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mComicThumbnail.setTransitionName(getString(R.string.shared_transition_comic_thumb));
        }

        bindComicInfo();
        setupComicDetailsFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_comic, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_toggle_bookmark:
                Toast.makeText(this, "NOT IMPLEMEMENTED YET.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_share:
                share(mComic.getTitle(), mComic.getDetailsUrl());
                break;

            case R.id.action_see_details:
                openUrlExternally(mComic.getDetailsUrl());
                break;

            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void bindComicInfo() {
        mPicasso.load(mComic.getThumbnailUrl())
                .fit()
                .centerCrop()
                .error(R.drawable.character_placeholder_landscape)
                .into(mComicThumbnail);

        getSupportActionBar().setTitle(mComic.getTitle());
    }

    private void setupComicDetailsFragment() {
        mComicDetailsFragment = ComicDetailsFragment.newInstance(mComic);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.details_fragment_container, mComicDetailsFragment)
                .commit();
    }
}
