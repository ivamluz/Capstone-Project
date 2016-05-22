package ivamluz.marvelshelf.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import ivamluz.marvelshelf.data.MarvelShelfContract;
import ivamluz.marvelshelf.data.model.MarvelCharacter;
import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;

/**
 * Created by iluz on 5/19/16.
 */
public class BookmarksManager {
    private static final String LOG_TAG = BookmarksManager.class.getSimpleName();

    private Context mContext;
    private BookmarkCallbacks mBookmarkCallbacks;

    public BookmarksManager(Context context, BookmarkCallbacks bookmarkCallbacks) {
        mContext = context;
        mBookmarkCallbacks = bookmarkCallbacks;
    }

    public void toggleBookmark(final long characterId) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                Uri uri = MarvelShelfContract.CharacterEntry.buildCharacterUri(characterId);

                Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();

                MarvelCharacter marvelCharacter = MarvelCharacter.fromCursor(cursor);
                int newIsBookmarked = marvelCharacter.isBookmarked() ? 0 : 1;

                ContentValues updateValues = new ContentValues();
                updateValues.put(MarvelShelfContract.CharacterEntry.COLUMN_IS_BOOKMARK, newIsBookmarked);

                String selectionClause = String.format("%s = ?", MarvelShelfContract.CharacterEntry.COLUMN_CHARACTER_ID);
                String[] selectionArgs = {String.valueOf(characterId)};

                int rowsUpdated = mContext.getContentResolver().update(
                        MarvelShelfContract.CharacterEntry.CONTENT_URI,
                        updateValues,
                        selectionClause,
                        selectionArgs
                );

                if (rowsUpdated > 0) {
                    MarvelShelfLogger.debug(LOG_TAG, String.format("Character %s updated with is_bookmarked = %s", marvelCharacter.getId(), newIsBookmarked));

                    return (newIsBookmarked == 1);
                } else {
                    MarvelShelfLogger.debug(LOG_TAG, String.format("Character %s was not updated. is_bookmarked is still %s", marvelCharacter.getId(), marvelCharacter.isBookmarked()));

                    return marvelCharacter.isBookmarked();
                }
            }

            @Override
            protected void onPostExecute(Boolean isBookmarked) {
                super.onPostExecute(isBookmarked);

                if (mBookmarkCallbacks != null) {
                    mBookmarkCallbacks.onBookmarkToogled(isBookmarked);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public interface BookmarkCallbacks {
        void onBookmarkToogled(boolean isBookmarked);
    }
}
