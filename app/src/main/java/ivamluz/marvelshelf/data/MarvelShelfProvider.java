package ivamluz.marvelshelf.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by iluz on 5/5/16.
 */
public class MarvelShelfProvider extends ContentProvider {
    private static final int CHARACTER = 100;
    private static final int CHARACTER_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MarvelShelfDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new MarvelShelfDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case CHARACTER:
                cursor = db.query(
                        MarvelShelfContract.CharacterEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CHARACTER_ID:
                long _id = ContentUris.parseId(uri);
                cursor = db.query(
                        MarvelShelfContract.CharacterEntry.TABLE_NAME,
                        projection,
                        MarvelShelfContract.CharacterEntry._ID + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throwErrorForUnknowUri(uri);
                return null;
        }

        // Set the notification URI for the cursor to the one passed into the function. This
        // causes the cursor to register a content observer to watch for changes that happen to
        // this URI and any of it's descendants. By descendants, we mean any URI that begins
        // with this path.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case CHARACTER:
                return MarvelShelfContract.CharacterEntry.CONTENT_TYPE;
            case CHARACTER_ID:
                return MarvelShelfContract.CharacterEntry.CONTENT_ITEM_TYPE;
            default:
                throwErrorForUnknowUri(uri);
                return null;
        }
    }

    private void throwErrorForUnknowUri(Uri uri) {
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    /**
     * Builds a UriMatcher that is used to determine witch database request is being made.
     */
    private static UriMatcher buildUriMatcher() {
        String content = MarvelShelfContract.CONTENT_AUTHORITY;

        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(content, MarvelShelfContract.PATH_CHARACTER, CHARACTER);
        matcher.addURI(content, MarvelShelfContract.PATH_CHARACTER + "/#", CHARACTER_ID);

        return matcher;
    }
}
