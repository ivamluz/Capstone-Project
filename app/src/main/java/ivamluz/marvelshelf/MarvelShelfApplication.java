package ivamluz.marvelshelf;

import android.app.Application;
import android.net.Uri;
import android.support.v4.util.Pair;

import com.karumi.marvelapiclient.MarvelApiConfig;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by iluz on 5/7/16.
 */
public class MarvelShelfApplication extends Application {
    private static final String LOG_TAG = MarvelShelfApplication.class.getSimpleName();
    private static MarvelShelfApplication sInstance;

    private Picasso mPicasso;
    protected MarvelApiConfig mMarvelApiConfig;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        setupCaligraphy();
    }


    public static MarvelShelfApplication getInstance() {
        MarvelShelfLogger.debug(LOG_TAG, "getInstance() called. Returning " + sInstance);
        return sInstance;
    }

    public Picasso getPicasso() {
        if (mPicasso != null) {
            return mPicasso;
        }

        Picasso.Builder builder = new Picasso.Builder(this);
        // http://stackoverflow.com/a/30628461
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                MarvelShelfLogger.error(LOG_TAG, exception);
            }
        });

        mPicasso = builder.build();

        return mPicasso;
    }

    public MarvelApiConfig getMarvelApiConfig() {
        if (mMarvelApiConfig != null) {
            return mMarvelApiConfig;
        }

        mMarvelApiConfig = new MarvelApiConfig.Builder(BuildConfig.MARVEL_API_PUBLIC_KEY, BuildConfig.MARVEL_API_PRIVATE_KEY).build();
        mMarvelApiConfig.getRetrofit().client().setConnectTimeout(BuildConfig.HTTP_CONNECTION_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
        mMarvelApiConfig.getRetrofit().client().setReadTimeout(BuildConfig.HTTP_READ_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);

        return mMarvelApiConfig;
    }

    private void setupCaligraphy() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
