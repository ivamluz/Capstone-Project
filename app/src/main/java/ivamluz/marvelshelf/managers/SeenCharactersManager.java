package ivamluz.marvelshelf.managers;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import java.text.SimpleDateFormat;
import java.util.Date;

import ivamluz.marvelshelf.data.MarvelShelfContract;
import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;

/**
 * Created by iluz on 5/19/16.
 */
public class SeenCharactersManager {
    private static final String LOG_TAG = SeenCharactersManager.class.getSimpleName();

    private static final SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Context mContext;

    public SeenCharactersManager(Context context) {
        mContext = context;
    }

    public void registrySeenCharacter(final long characterId, final OnRegisterCharacterAsSeen callback) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                ContentValues updateValues = new ContentValues();
                updateValues.put(MarvelShelfContract.CharacterEntry.COLUMN_LAST_SEEN, iso8601Format.format(new Date()));

                String selectionClause = String.format("%s = ?", MarvelShelfContract.CharacterEntry.COLUMN_CHARACTER_ID);
                String[] selectionArgs = {String.valueOf(characterId)};

                int rowsUpdated = mContext.getContentResolver().update(
                        MarvelShelfContract.CharacterEntry.CONTENT_URI,
                        updateValues,
                        selectionClause,
                        selectionArgs
                );

                MarvelShelfLogger.debug(LOG_TAG, String.format("registrySeenCharacter - rowsUpdated: ", rowsUpdated));

                return (rowsUpdated > 0);
            }

            @Override
            protected void onPostExecute(Boolean wasRegistered) {
                super.onPostExecute(wasRegistered);

                if (callback != null) {
                    callback.onRegisterAsSeen(wasRegistered);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public interface OnRegisterCharacterAsSeen {
        void onRegisterAsSeen(boolean wasRegistered);
    }
}
