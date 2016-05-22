package ivamluz.marvelshelf.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by iluz on 5/22/16.
 */
public class BaseDetailsActivity extends AppCompatActivity {
    protected void configToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
}
