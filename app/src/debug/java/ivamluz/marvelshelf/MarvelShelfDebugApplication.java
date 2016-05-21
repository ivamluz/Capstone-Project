package ivamluz.marvelshelf;

import com.karumi.marvelapiclient.MarvelApiConfig;
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

    public MarvelApiConfig getMarvelApiConfig() {
        if (mMarvelApiConfig != null) {
            return mMarvelApiConfig;
        }

        mMarvelApiConfig = new MarvelApiConfig.Builder(BuildConfig.MARVEL_API_PUBLIC_KEY, BuildConfig.MARVEL_API_PRIVATE_KEY)
                .debug()
                .build();

        configureMarvelApiHttpClient();

        return mMarvelApiConfig;
    }

    private void setupButterKnife() {
        ButterKnife.setDebug(true);
    }
}
