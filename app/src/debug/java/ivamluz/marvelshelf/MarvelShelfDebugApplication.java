package ivamluz.marvelshelf;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;

/**
 * Created by iluz on 5/7/16.
 * <p/>
 * Credits: http://littlerobots.nl/blog/stetho-for-android-debug-builds-only/
 */
public class MarvelShelfDebugApplication extends MarvelShelfApplication {
    private static final String LOG_TAG = MarvelShelfDebugApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        setupButterKnife();
    }

    @Override
    public Picasso getPicasso() {
        Picasso picasso = super.getPicasso();
        picasso.setLoggingEnabled(true);

        return picasso;
    }

    private void setupButterKnife() {
        ButterKnife.setDebug(true);
    }
}
