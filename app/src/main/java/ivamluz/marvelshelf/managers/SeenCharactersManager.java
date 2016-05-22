package ivamluz.marvelshelf.managers;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import ivamluz.marvelshelf.data.MarvelShelfContract;
import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;

/**
 * Created by iluz on 5/19/16.
 */
public class SeenCharactersManager {
    private static final String LOG_TAG = SeenCharactersManager.class.getSimpleName();

    private Context mContext;

    public SeenCharactersManager(Context context) {
        mContext = context;
    }

    public void registrySeenCharacter(long characterId, OnCharacterRegisteredAsSeen callback) {
        ContentValues values = new ContentValues();

        values.put(MarvelShelfContract.SeenCharacterEntry.COLUMN_CHARACTER_ID, characterId);

        Uri uri = mContext.getContentResolver().insert(
                MarvelShelfContract.SeenCharacterEntry.CONTENT_URI,
                values
        );

        MarvelShelfLogger.debug(LOG_TAG, "SeenCharacter URI: " + uri);

        if (callback != null) {
            callback.onRegisteredAsSeen(uri);
        }
    }

    public interface OnCharacterRegisteredAsSeen {
        void onRegisteredAsSeen(Uri uri);
    }
}
