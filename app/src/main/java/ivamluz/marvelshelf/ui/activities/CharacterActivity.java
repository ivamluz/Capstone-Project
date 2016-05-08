package ivamluz.marvelshelf.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ivamluz.marvelshelf.R;

public class CharacterActivity extends AppCompatActivity {
    private static final String EXTRA_CHARACTER_ID = "ivamluz.marvelshelf.character_id";

    public static Intent newIntent(Context packageContext, long characterId) {
        Intent intent = new Intent(packageContext, CharacterActivity.class);
        intent.putExtra(EXTRA_CHARACTER_ID, characterId);

        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
