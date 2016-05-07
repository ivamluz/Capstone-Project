package ivamluz.marvelshelf;

import android.app.Application;
import android.net.Uri;

import com.squareup.picasso.Picasso;

import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by iluz on 5/7/16.
 */
public class MarvelShelfApplication extends Application {
    private static final String LOG_TAG = MarvelShelfApplication.class.getSimpleName();
    private static MarvelShelfApplication sInstance;

    private Picasso mPicasso;

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

    private void setupCaligraphy() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
