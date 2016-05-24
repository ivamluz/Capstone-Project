package ivamluz.marvelshelf.widget.lastseen;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squareup.picasso.Picasso;

import ivamluz.marvelshelf.MarvelShelfApplication;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.data.MarvelShelfContract;
import ivamluz.marvelshelf.infrastructure.MarvelShelfLogger;

/**
 * Created by iluz on 5/23/16.
 */
public class LastSeenWidgetRemoteViewsService extends RemoteViewsService {
    public final String LOG_TAG = LastSeenWidgetRemoteViewsService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor mCursor = null;

            @Override
            public void onCreate() {
                // empty method
            }

            @Override
            public void onDataSetChanged() {
                if (mCursor != null) {
                    MarvelShelfLogger.debug(LOG_TAG, "Dataset is null.");
                    mCursor.close();
                }

                String selection = String.format("%s IS NOT NULL", MarvelShelfContract.CharacterEntry.COLUMN_LAST_SEEN);
                String sortOrder = String.format("%s DESC LIMIT 20", MarvelShelfContract.CharacterEntry.COLUMN_LAST_SEEN);

                Uri uri = MarvelShelfContract.CharacterEntry.CONTENT_URI;
                mCursor = getContentResolver().query(uri, null, selection, null, sortOrder);
            }

            @Override
            public void onDestroy() {
                try {
                    mCursor.close();
                } finally {
                    mCursor = null;
                }
            }

            @Override
            public int getCount() {
                return mCursor == null ? 0 : mCursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (!isValidPosition(position)) {
                    MarvelShelfLogger.error(LOG_TAG, String.format("%s is not a valid position", position));
                    return null;
                }

                Context context = getApplicationContext();

                RemoteViews widgetRow = new RemoteViews(getPackageName(), R.layout.widget_last_seen_list_item);

                Intent fillInIntent = new Intent();
                fillInIntent.putExtra(LastSeenWidgetProvider.EXTRA_LIST_VIEW_ROW_NUMBER, position);
                widgetRow.setOnClickFillInIntent(R.id.widget_last_seen_list_item, fillInIntent);


//                int[] appWidgetIds = new int[]{intent.getIntExtra("appWidgetIds", 0)};
//
//                String thumbUrl = mCursor.getString(mCursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_THUMBNAIL));
//
//                MarvelShelfApplication application = (MarvelShelfApplication) getApplication();
//                Picasso picasso = application.getPicasso();
//
//                picasso.load(thumbUrl)
//                        .resizeDimen(R.dimen.widget_bookmarks_list_item_image_width, R.dimen.widget_bookmarks_list_item_height)
//                        .into(widgetRow, R.id.widget_last_seen_item_thumb, appWidgetIds);


                String title = mCursor.getString(mCursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_NAME));
                widgetRow.setTextViewText(R.id.widget_last_seen_item_title, title);
                widgetRow.setContentDescription(R.id.widget_last_seen_item_title, getString(R.string.content_description_character_name, title));

                String description = mCursor.getString(mCursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_DESCRIPTION));
                widgetRow.setTextViewText(R.id.widget_last_seen_item_description, description);
                widgetRow.setContentDescription(R.id.widget_last_seen_item_description, getString(R.string.content_description_character_description, description));

                return widgetRow;
            }

            private boolean isValidPosition(int position) {
                return (position != AdapterView.INVALID_POSITION && mCursor != null && mCursor.moveToPosition(position));
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_last_seen_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (mCursor.moveToPosition(position))
                    return mCursor.getLong(mCursor.getColumnIndex(MarvelShelfContract.CharacterEntry.COLUMN_CHARACTER_KEY));

                return AdapterView.INVALID_POSITION;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}