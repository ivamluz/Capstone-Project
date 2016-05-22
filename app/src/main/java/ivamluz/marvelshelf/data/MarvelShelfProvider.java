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

import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;

/**
 * Created by iluz on 5/5/16.
 */
public class MarvelShelfProvider extends ContentProvider {
    private static final String LOG_TAG = MarvelShelfProvider.class.getSimpleName();

    private static final int CHARACTER = 100;
    private static final int CHARACTER_ID = 110;
    private static final int BOOKMARK = 200;
    private static final int BOOKMARK_CHARACTER_ID = 210;
    private static final int SEEN_CHARACTER = 300;
    private static final int SEEN_CHARACTER_ID = 310;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MarvelShelfDbHelper mDbHelper;

    private static final HashMap<String, String> mAllCharactersColumnMap = buildAllCharactersColumnMap();
    private static final HashMap<String, String> mBookmarksColumnMap = buildBookmarksColumnMap();
    private static final HashMap<String, String> mSeenCharactersColumnMap = buildSeenCharactersColumnMap();

    private static HashMap<String, String> buildAllCharactersColumnMap() {
        HashMap<String, String> map = new HashMap<String, String>();

        String characterProjection[] = MarvelShelfContract.CharacterEntry.TABLE_COLUMNS;
        for (String column : characterProjection) {
            String qualifiedColumn = MarvelShelfContract.CharacterEntry.TABLE_NAME + "." + column;
            map.put(qualifiedColumn, qualifiedColumn + " AS " + column);
        }

        String bookmarkProjection[] = MarvelShelfContract.BookmarkEntry.TABLE_COLUMNS;
        for (String column : bookmarkProjection) {
            String qualifiedColumn = MarvelShelfContract.BookmarkEntry.TABLE_NAME + "." + column;

            String alias = column;
            if (MarvelShelfContract.BookmarkEntry.COLUMN_BOOKMARK_KEY.equals(column)) {
                alias = qualifiedColumn.replace(".", "_");
            }

            map.put(qualifiedColumn, qualifiedColumn + " as " + alias);
        }

        return map;
    }

    private static HashMap<String, String> buildBookmarksColumnMap() {
        HashMap<String, String> map = new HashMap<String, String>();

        String bookmarkProjection[] = MarvelShelfContract.BookmarkEntry.TABLE_COLUMNS;
        for (String column : bookmarkProjection) {
            String qualifiedColumn = MarvelShelfContract.BookmarkEntry.TABLE_NAME + "." + column;

            if (MarvelShelfContract.BookmarkEntry.COLUMN_BOOKMARK_KEY.equals(column)) {
                String alias = qualifiedColumn.replace(".", "_");
                map.put(qualifiedColumn, qualifiedColumn + " as " + alias);
            }

            map.put(qualifiedColumn, qualifiedColumn + " as " + column);
        }

        HashMap<String, String> mapCharacter = buildCharactersColumnMap();
        map.putAll(mapCharacter);

        return map;
    }

    private static HashMap<String, String> buildSeenCharactersColumnMap() {
        HashMap<String, String> map = new HashMap<String, String>();

        String seenCharactersProjection[] = MarvelShelfContract.SeenCharacterEntry.TABLE_COLUMNS;
        for (String column : seenCharactersProjection) {
            String qualifiedColumn = MarvelShelfContract.SeenCharacterEntry.TABLE_NAME + "." + column;
            map.put(qualifiedColumn, qualifiedColumn + " as " + column);
        }

        HashMap<String, String> mapCharacter = buildCharactersColumnMap();
        map.putAll(mapCharacter);

        return map;
    }

    @NonNull
    private static HashMap<String, String> buildCharactersColumnMap() {
        HashMap<String, String> mapCharacter = new HashMap<String, String>();
        String characterProjection[] = MarvelShelfContract.CharacterEntry.TABLE_COLUMNS;
        for (String column : characterProjection) {
            String qualifiedColumn = MarvelShelfContract.CharacterEntry.TABLE_NAME + "." + column;
            String alias = column;
            if (MarvelShelfContract.CharacterEntry.COLUMN_CHARACTER_KEY.equals(column)) {
                alias = qualifiedColumn.replace(".", "_");
            }
            mapCharacter.put(qualifiedColumn, qualifiedColumn + " AS " + alias);
        }
        return mapCharacter;
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
                long characterId = ContentUris.parseId(uri);
                cursor = createBookmarkByCharacterIdQuery(projection, sortOrder, db, characterId);
                break;
            case SEEN_CHARACTER:
                cursor = createSeenCharactersQueryCursor(projection, selection, selectionArgs, sortOrder, db);
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
        SQLiteQueryBuilder queryBuilder = createAllCharactersQueryBuilder();

        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    private SQLiteQueryBuilder createAllCharactersQueryBuilder() {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String leftColumn = MarvelShelfContract.CharacterEntry.TABLE_NAME + "." + MarvelShelfContract.CharacterEntry.COLUMN_CHARACTER_ID;
        String rightColumn = MarvelShelfContract.BookmarkEntry.TABLE_NAME + "." + MarvelShelfContract.BookmarkEntry.COLUMN_CHARACTER_ID;

        StringBuilder sb = new StringBuilder();
        sb.append(MarvelShelfContract.CharacterEntry.TABLE_NAME);
        sb.append(" LEFT OUTER JOIN ");
        sb.append(MarvelShelfContract.BookmarkEntry.TABLE_NAME);
        sb.append(" ON (");
        sb.append(leftColumn);
        sb.append(" = ");
        sb.append(rightColumn);
        sb.append(")");
        String table = sb.toString();

        queryBuilder.setTables(table);
        queryBuilder.setProjectionMap(mAllCharactersColumnMap);

        return queryBuilder;
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
    }

    @NonNull
    // Reference: http://www.copperykeenclaws.com/setting-up-an-android-contentprovider-with-a-join/
    private SQLiteQueryBuilder createBookmarkedCharactersQueryBuilder() {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String leftColumn = MarvelShelfContract.BookmarkEntry.TABLE_NAME + "." + MarvelShelfContract.BookmarkEntry.COLUMN_CHARACTER_ID;
        String rightColumn = MarvelShelfContract.CharacterEntry.TABLE_NAME + "." + MarvelShelfContract.CharacterEntry.COLUMN_CHARACTER_ID;

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

    private Cursor createBookmarkByCharacterIdQuery(String[] projection, String sortOrder, SQLiteDatabase db, long characterId) {
        return db.query(
                MarvelShelfContract.BookmarkEntry.TABLE_NAME,
                projection,
                MarvelShelfContract.BookmarkEntry.COLUMN_CHARACTER_ID + " = ?",
                new String[]{String.valueOf(characterId)},
                null,
                null,
                sortOrder
        );
    }

    private Cursor createSeenCharactersQueryCursor(String[] projection, String selection, String[] selectionArgs, String sortOrder, SQLiteDatabase db) {
        SQLiteQueryBuilder queryBuilder = createSeenCharactersQueryBuilder();

        return queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @NonNull
    // Reference: http://www.copperykeenclaws.com/setting-up-an-android-contentprovider-with-a-join/
    private SQLiteQueryBuilder createSeenCharactersQueryBuilder() {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String leftColumn = MarvelShelfContract.SeenCharacterEntry.TABLE_NAME + "." + MarvelShelfContract.SeenCharacterEntry.COLUMN_CHARACTER_ID;
        String rightColumn = MarvelShelfContract.CharacterEntry.TABLE_NAME + "." + MarvelShelfContract.CharacterEntry.COLUMN_CHARACTER_ID;

        StringBuilder sb = new StringBuilder();
        sb.append(MarvelShelfContract.SeenCharacterEntry.TABLE_NAME);
        sb.append(" LEFT OUTER JOIN ");
        sb.append(MarvelShelfContract.CharacterEntry.TABLE_NAME);
        sb.append(" ON (");
        sb.append(leftColumn);
        sb.append(" = ");
        sb.append(rightColumn);
        sb.append(")");
        String table = sb.toString();

        queryBuilder.setTables(table);
        queryBuilder.setProjectionMap(mSeenCharactersColumnMap);

        return queryBuilder;
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
//            case BOOKMARK_CHARACTER_ID:
//                return MarvelShelfContract.BookmarkEntry.CONTENT_ITEM_TYPE;
            case SEEN_CHARACTER:
                return MarvelShelfContract.SeenCharacterEntry.CONTENT_TYPE;
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
        Uri returnUri = null;

        switch (sUriMatcher.match(uri)) {
            case SEEN_CHARACTER:
                returnUri = insertSeenCharacter(contentValues);
                if (returnUri == null) {
                    throwSqlExceptionForFailedInsertion(uri);
                }

                break;
            case BOOKMARK:
                returnUri = insertBookmarkedCharacter(contentValues);
                if (returnUri == null) {
                    throwSqlExceptionForFailedInsertion(uri);
                }

                getContext().getContentResolver().notifyChange(MarvelShelfContract.SeenCharacterEntry.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(MarvelShelfContract.BookmarkEntry.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(MarvelShelfContract.CharacterEntry.CONTENT_URI, null);

                break;
            default:
                throwErrorForUnknowUri(uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    private Uri insertSeenCharacter(ContentValues contentValues) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Uri returnUri = null;

        long _id = db.insertWithOnConflict(MarvelShelfContract.SeenCharacterEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        if (_id > 0) {
            returnUri = MarvelShelfContract.SeenCharacterEntry.buildSeenCharacterUri(_id);

            String message = String.format("%s new URI: %s", MarvelShelfContract.SeenCharacterEntry.TABLE_NAME, returnUri);
            MarvelShelfLogger.debug(LOG_TAG, message);
        }

        return returnUri;
    }

    private Uri insertBookmarkedCharacter(ContentValues contentValues) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Uri returnUri = null;

        long _id = db.insertWithOnConflict(MarvelShelfContract.BookmarkEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        if (_id > 0) {
            returnUri = MarvelShelfContract.BookmarkEntry.buildBookmarkUri(_id);

            String message = String.format("%s new URI: %s", MarvelShelfContract.BookmarkEntry.TABLE_NAME, returnUri);
            MarvelShelfLogger.debug(LOG_TAG, message);
        }

        return returnUri;
    }

    private Uri throwSqlExceptionForFailedInsertion(Uri uri) {
        throw new android.database.SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String whereClause, String[] whereArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsDeleted = 0;

        switch (sUriMatcher.match(uri)) {
            case BOOKMARK:
                rowsDeleted = db.delete(MarvelShelfContract.BookmarkEntry.TABLE_NAME, whereClause, whereArgs);

                break;
            default:
                throwErrorForUnknowUri(uri);
        }

        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(MarvelShelfContract.CharacterEntry.CONTENT_URI, null);
            getContext().getContentResolver().notifyChange(MarvelShelfContract.BookmarkEntry.CONTENT_URI, null);
            getContext().getContentResolver().notifyChange(MarvelShelfContract.SeenCharacterEntry.CONTENT_URI, null);
        }

        return rowsDeleted;
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
        matcher.addURI(content, MarvelShelfContract.PATH_SEEN_CHARACTER, SEEN_CHARACTER);

        return matcher;
    }
}
