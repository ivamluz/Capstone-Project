package ivamluz.marvelshelf.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.ui.fragments.CharacterDetailsFragment;

public class CharacterDetailsActivity extends AppCompatActivity {
    private static final String EXTRA_CHARACTER_ID = "ivamluz.marvelshelf.character_id";
    private static final int INVALID_CHARACTER_ID = -1;

    private long characterId;

    public static Intent newIntent(Context packageContext, long characterId) {
        Intent intent = new Intent(packageContext, CharacterDetailsActivity.class);
        intent.putExtra(EXTRA_CHARACTER_ID, characterId);

        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        setupCharacterDetailsFragment();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void setupCharacterDetailsFragment() {
        long characterId = getIntent().getLongExtra(EXTRA_CHARACTER_ID, INVALID_CHARACTER_ID);

        if (!isValidCharacterId(characterId)) {
            throw new IllegalArgumentException("Invalid characterId");
        }

        CharacterDetailsFragment fragment = CharacterDetailsFragment.newInstance(characterId);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.character_details_container, fragment)
                .commit();
    }

    private boolean isValidCharacterId(long characterId) {
        return INVALID_CHARACTER_ID != characterId;
    }
}
