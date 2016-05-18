package ivamluz.marvelshelf.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

/**
 * Created by iluz on 5/5/16.
 */
public class MarvelShelfProvider extends ContentProvider {
    private static final int CHARACTER = 100;
    private static final int CHARACTER_ID = 110;
    private static final int BOOKMARK = 200;
    private static final int BOOKMARK_CHARACTER_ID = 210;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MarvelShelfDbHelper mDbHelper;

    private static final HashMap<String, String> mBookmarksColumnMap = buildBookmarksColumnMap();

    private static HashMap<String, String> buildBookmarksColumnMap() {
        HashMap<String, String> map = new HashMap<String, String>();

        String bookmarkProjection[] = MarvelShelfContract.BookmarkEntry.TABLE_COLUMNS;

        for (String column : bookmarkProjection) {
            String qualifiedCol = MarvelShelfContract.BookmarkEntry.TABLE_NAME + "." + column;
            map.put(qualifiedCol, qualifiedCol + " as " + column);
        }

        String characterProjection[] = MarvelShelfContract.CharacterEntry.TABLE_COLUMNS;
        for (String column : characterProjection) {
            String qualifiedColumn = MarvelShelfContract.CharacterEntry.TABLE_NAME + "." + column;
            String alias = column;
            if (MarvelShelfContract.CharacterEntry.COLUMN_CHARACTER_KEY.equals(column)) {
                alias = qualifiedColumn.replace(".", "_");
            }
            map.put(qualifiedColumn, qualifiedColumn + " AS " + alias);
        }

        return map;
    }

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
                cursor = createAllCharactersQueryCursor(projection, selection, selectionArgs, sortOrder, db);
                break;
            case CHARACTER_ID:
                long _id = ContentUris.parseId(uri);
                cursor = createCharacterByIdQueryCursor(projection, sortOrder, db, _id);
                break;
            case BOOKMARK:
                cursor = createBookmarkedCharactersQueryCursor(projection, selection, selectionArgs, sortOrder, db);
                break;
            case BOOKMARK_CHARACTER_ID:
                long _characterId = ContentUris.parseId(uri);
                cursor = createBookmarkByCharacterIdQuery(projection, sortOrder, db, _characterId);
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

    private Cursor createAllCharactersQueryCursor(String[] projection, String selection, String[] selectionArgs, String sortOrder, SQLiteDatabase db) {
        return db.query(
                MarvelShelfContract.CharacterEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor createCharacterByIdQueryCursor(String[] projection, String sortOrder, SQLiteDatabase db, long _id) {
        return db.query(
                MarvelShelfContract.CharacterEntry.TABLE_NAME,
                projection,
                MarvelShelfContract.CharacterEntry._ID + " = ?",
                new String[]{String.valueOf(_id)},
                null,
                null,
                sortOrder
        );
    }

    private Cursor createBookmarkedCharactersQueryCursor(String[] projection, String selection, String[] selectionArgs, String sortOrder, SQLiteDatabase db) {

        SQLiteQueryBuilder queryBuilder = createBookmarkedCharactersQueryBuilder();

        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        // projectionIn, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder

//        return db.query(
//                MarvelShelfContract.CharacterEntry.TABLE_NAME,
//                projection,
//                selection,
//                selectionArgs,
//                null,
//                null,
//                sortOrder
//        );
    }

    @NonNull
    private SQLiteQueryBuilder createBookmarkedCharactersQueryBuilder() {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String leftColumn = MarvelShelfContract.BookmarkEntry.TABLE_NAME + "." + MarvelShelfContract.BookmarkEntry.COLUMN_CHARACTER_KEY;
        String rightColumn = MarvelShelfContract.CharacterEntry.TABLE_NAME + "." + MarvelShelfContract.CharacterEntry.COLUMN_CHARACTER_KEY;

        StringBuilder sb = new StringBuilder();
        sb.append(MarvelShelfContract.BookmarkEntry.TABLE_NAME);
        sb.append(" LEFT OUTER JOIN ");
        sb.append(MarvelShelfContract.CharacterEntry.TABLE_NAME);
        sb.append(" ON (");
        sb.append(leftColumn);
        sb.append(" = ");
        sb.append(rightColumn);
        sb.append(")");
        String table = sb.toString();

        queryBuilder.setTables(table);
        queryBuilder.setProjectionMap(mBookmarksColumnMap);

        return queryBuilder;
    }

    private Cursor createBookmarkByCharacterIdQuery(String[] projection, String sortOrder, SQLiteDatabase db, long _id) {
        return db.query(
                MarvelShelfContract.CharacterEntry.TABLE_NAME,
                projection,
                MarvelShelfContract.CharacterEntry._ID + " = ?",
                new String[]{String.valueOf(_id)},
                null,
                null,
                sortOrder
        );
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case CHARACTER:
                return MarvelShelfContract.CharacterEntry.CONTENT_TYPE;
            case CHARACTER_ID:
                return MarvelShelfContract.CharacterEntry.CONTENT_ITEM_TYPE;
            case BOOKMARK:
                return MarvelShelfContract.BookmarkEntry.CONTENT_TYPE;
            case BOOKMARK_CHARACTER_ID:
                return MarvelShelfContract.BookmarkEntry.CONTENT_ITEM_TYPE;
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
        matcher.addURI(content, MarvelShelfContract.PATH_BOOKMARK, BOOKMARK);
        matcher.addURI(content, MarvelShelfContract.PATH_BOOKMARK + "/#", BOOKMARK_CHARACTER_ID);

        return matcher;
    }
}
