package ivamluz.marvelshelf.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.karumi.marvelapiclient.model.ComicDto;
import com.karumi.marvelapiclient.model.SeriesDto;
import com.squareup.picasso.Picasso;

import java.util.List;

import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.data.model.MarvelCharacter;
import ivamluz.marvelshelf.ui.fragments.CharacterDetailsFragment;
import ivamluz.marvelshelf.ui.fragments.workers.ComicsLoaderWorkerFragment;
import ivamluz.marvelshelf.ui.fragments.workers.SeriesLoaderWorkerFragment;

public class CharacterDetailsActivity extends AppCompatActivity implements ComicsLoaderWorkerFragment.TaskCallbacks, SeriesLoaderWorkerFragment.TaskCallbacks {
    private static final String EXTRA_CHARACTER = "ivamluz.marvelshelf.character";

    private MarvelCharacter mCharacter;

    private CharacterDetailsFragment mCharacterDetailsFragment;
    private Picasso mPicasso;

    public static Intent newIntent(Context packageContext, MarvelCharacter character) {
        Intent intent = new Intent(packageContext, CharacterDetailsActivity.class);
        intent.putExtra(EXTRA_CHARACTER, character);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPicasso = MarvelShelfApplication.getInstance().getPicasso();

        mCharacter = getIntent().getParcelableExtra(EXTRA_CHARACTER);

        bindCharacterInfo();
        setupCharacterDetailsFragment();
    }

    private void bindCharacterInfo() {
        ImageView characterThumbnail = (ImageView) findViewById(R.id.image_details_thumb);

        mPicasso.load(mCharacter.getThumbnailUrl())
                .fit()
                .centerCrop()
                .error(R.drawable.character_placeholder)
                .into(characterThumbnail);

        setTitle(mCharacter.getName());
    }

    private void setupCharacterDetailsFragment() {
        mCharacterDetailsFragment = CharacterDetailsFragment.newInstance(mCharacter.getId());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.details_fragment_container, mCharacterDetailsFragment)
                .commit();
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
