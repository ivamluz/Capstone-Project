package ivamluz.marvelshelf.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import ivamluz.marvelshelf.BuildConfig;
import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.adapter.ImagesAdapter;
import ivamluz.marvelshelf.data.model.MarvelComic;
import ivamluz.marvelshelf.ui.fragments.ComicDetailsFragment;

public class ComicDetailsActivity extends AppCompatActivity {
    private static final String EXTRA_COMIC = String.format("%s.comic", BuildConfig.APPLICATION_ID);

    private MarvelComic mComic;

    private ComicDetailsFragment mComicDetailsFragment;
    private Picasso mPicasso;

    @BindView(R.id.image_details_thumb)
    protected ImageView mComicThumbnail;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPicasso = MarvelShelfApplication.getInstance().getPicasso();

        mComic = getIntent().getParcelableExtra(EXTRA_COMIC);

        mComicThumbnail = (ImageView) findViewById(R.id.image_details_thumb);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mComicThumbnail.setTransitionName(getString(R.string.shared_transition_comic_thumb));
        }

        bindCharacterInfo();
        setupComicDetailsFragment();
    }

    private void bindCharacterInfo() {
        mPicasso.load(mComic.getThumbnailUrl())
                .fit()
                .centerCrop()
                .error(R.drawable.character_placeholder)
                .into(mComicThumbnail);

        setTitle(mComic.getTitle());
    }

    private void setupComicDetailsFragment() {
        mComicDetailsFragment = ComicDetailsFragment.newInstance(mComic);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.details_fragment_container, mComicDetailsFragment)
                .commit();
    }
}
