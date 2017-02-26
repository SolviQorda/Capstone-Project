package qorda_projects.tracktive.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import qorda_projects.tracktive.MainActivity;
import qorda_projects.tracktive.R;
import qorda_projects.tracktive.StoryDetailActivity;

/**
 * Created by sorengoard on 23/02/2017.
 */

public class CardWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            Log.i("TAG", "update");
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.wiget_list);

            setRemoteAdapter(context, views);

            Intent widgetClickIntent = new Intent(context, StoryDetailActivity.class);
            PendingIntent widgetClickPendingIntent = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(widgetClickIntent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.card_widget_list, widgetClickPendingIntent);
            Log.v("LOGTAG", "LOG:clcikintent" + widgetClickIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, getClass())
        );
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.card_widget_list);

    }

   private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
       views.setRemoteAdapter(R.id.card_widget_list, new Intent(context, CardWidgetRemoteViewsService.class));
   }
}
