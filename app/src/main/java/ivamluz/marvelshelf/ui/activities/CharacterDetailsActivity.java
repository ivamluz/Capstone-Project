package ivamluz.marvelshelf.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.karumi.marvelapiclient.model.ComicDto;
import com.squareup.picasso.Picasso;

import java.util.List;

import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.data.model.MarvelCharacter;
import ivamluz.marvelshelf.ui.fragments.CharacterDetailsFragment;
import ivamluz.marvelshelf.ui.fragments.workers.ComicsLoaderWorkerFragment;

public class CharacterDetailsActivity extends AppCompatActivity implements ComicsLoaderWorkerFragment.TaskCallbacks {
    private static final String EXTRA_CHARACTER = "ivamluz.marvelshelf.character";

    private MarvelCharacter mCharacter;

    private CharacterDetailsFragment mCharacterDetailsFragment;

    public static Intent newIntent(Context packageContext, MarvelCharacter character) {
        Intent intent = new Intent(packageContext, CharacterDetailsActivity.class);
        intent.putExtra(EXTRA_CHARACTER, character);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCharacter = getIntent().getParcelableExtra(EXTRA_CHARACTER);

        bindCharacterInfo();
        setupCharacterDetailsFragment();
    }

    private void bindCharacterInfo() {
        ImageView characterThumbnail = (ImageView) findViewById(R.id.image_character_thumb);

        Picasso picasso = MarvelShelfApplication.getInstance().getPicasso();

        picasso.load(mCharacter.getThumbnailUrl())
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
                .replace(R.id.character_details_container, mCharacterDetailsFragment)
                .commit();
    }

    @Override
    public void onPreExecute() {
        mCharacterDetailsFragment.onPreExecute();
    }

    @Override
    public void onCancelled() {
        mCharacterDetailsFragment.onCancelled();
    }

    @Override
    public void onComicsLoaded(List<ComicDto> comics) {
        mCharacterDetailsFragment.onComicsLoaded(comics);
    }
}
