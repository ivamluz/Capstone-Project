package ivamluz.marvelshelf.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;

/**
 * Created by iluz on 5/22/16.
 */
public class BaseActivity extends AppCompatActivity {
    protected Tracker mTracker;

    private final String TRACKING_LOG_TAG = "TRACKING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MarvelShelfApplication application = (MarvelShelfApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    protected void configToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    protected void openUrlExternally(String url) {
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(this, getString(R.string.invalid_url), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    protected void share(String title, String url) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("text/plain");

        startActivity(intent);
    }

    protected void trackScreenView(String title) {
        MarvelShelfLogger.debug(TRACKING_LOG_TAG, String.format("Tracking screen view: %s", title));

        mTracker.setScreenName(title);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
