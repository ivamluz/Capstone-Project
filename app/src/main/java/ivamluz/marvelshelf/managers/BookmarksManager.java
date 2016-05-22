package ivamluz.marvelshelf.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import ivamluz.marvelshelf.data.MarvelShelfContract;
import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;

/**
 * Created by iluz on 5/19/16.
 */
public class BookmarksManager {
    private static final String LOG_TAG = BookmarksManager.class.getSimpleName();

    public static long INVALID_BOOKMARK_ID = -1;

    private Context mContext;
    private BookmarkCallbacks mBookmarkCallbacks;

    public BookmarksManager(Context context, BookmarkCallbacks bookmarkCallbacks) {
        mContext = context;
        mBookmarkCallbacks = bookmarkCallbacks;
    }

    public void loadBookmark(long characterId) {
        Uri uri = MarvelShelfContract.BookmarkEntry.buildBookmarkUri(characterId);

        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        if (cursor.getCount() == 0) {
            mBookmarkCallbacks.onBookmarkLoaded(null);
        } else {
            mBookmarkCallbacks.onBookmarkLoaded(uri);
        }
    }

    public void toggleBookmark(long characterId) {
        Uri uri = MarvelShelfContract.BookmarkEntry.buildBookmarkUri(characterId);
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);

        boolean isBookmarked = (cursor.getCount() != 0);
        if (isBookmarked) { // delete
            cursor.moveToFirst();

            int _id = cursor.getInt(cursor.getColumnIndex(MarvelShelfContract.BookmarkEntry.COLUMN_BOOKMARK_KEY));

            String where = "_id = ?";
            String[] args = new String[]{String.valueOf(_id)};

            int rowsDeleted = mContext.getContentResolver().delete(MarvelShelfContract.BookmarkEntry.CONTENT_URI, where, args);

            if (rowsDeleted > 0) {
                uri = null;
            }
        } else { // insert
            uri = insertBookmark(characterId);
        }

        if (mBookmarkCallbacks != null) {
            mBookmarkCallbacks.onBookmarkAdded(uri);
        }
    }

    private Uri insertBookmark(long characterId) {
        ContentValues values = new ContentValues();

        values.put(MarvelShelfContract.BookmarkEntry.COLUMN_CHARACTER_ID, characterId);
        Uri uri = mContext.getContentResolver().insert(
                MarvelShelfContract.BookmarkEntry.CONTENT_URI,
                values
        );

        MarvelShelfLogger.debug(LOG_TAG, "BookmarkEntry URI: " + uri);
        return uri;
    }

    public interface BookmarkCallbacks {
        void onBookmarkLoaded(Uri uri);

        void onBookmarkAdded(Uri uri);

        void onBookmarkRemoved();
    }
}
