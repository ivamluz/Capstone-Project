package ivamluz.marvelshelf.data;

/**
 * Created by iluz on 5/5/16.
 */

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import ivamluz.marvelshelf.BuildConfig;

public class MarvelShelfContract {
    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID;

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CHARACTER = "character";

    public static final class CharacterEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CHARACTER).build();

        @SuppressWarnings("UnusedDeclaration")
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHARACTER;

        @SuppressWarnings("UnusedDeclaration")
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHARACTER;

        public static final String TABLE_NAME = "Character";
        public static final String COLUMN_CHARACTER_KEY = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_MODIFIED = "modified";
        public static final String COLUMN_DETAILS_URL = "details_url";
        public static final String COLUMN_THUMBNAIL = "thumbnail";

        public static Uri buildCharacterUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
