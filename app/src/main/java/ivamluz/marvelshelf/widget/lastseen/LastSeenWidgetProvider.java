package ivamluz.marvelshelf.widget.lastseen;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import ivamluz.marvelshelf.BuildConfig;
import ivamluz.marvelshelf.R;
import ivamluz.marvelshelf.ui.activities.MainActivity;

/**
 * Created by iluz on 5/23/16.
 */
public class LastSeenWidgetProvider extends AppWidgetProvider {
    public static String EXTRA_LIST_VIEW_ROW_NUMBER = String.format("%s.widget.last_seen.list.item_position", BuildConfig.APPLICATION_ID);

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_last_seen);

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_last_seen, pendingIntent);

            Intent serviceIntent = new Intent(context, LastSeenWidgetRemoteViewsService.class);
            serviceIntent.putExtra("appWidgetId", appWidgetId);

            views.setRemoteAdapter(R.id.widget_last_seen_list, serviceIntent
                    );

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
