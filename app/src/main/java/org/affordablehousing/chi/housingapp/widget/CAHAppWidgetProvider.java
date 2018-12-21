package org.affordablehousing.chi.housingapp.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.service.LocationWidgetService;
import org.affordablehousing.chi.housingapp.ui.MapsActivity;

/**
 * Implementation of App Widget functionality.
 */
public class CAHAppWidgetProvider extends AppWidgetProvider {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.location_favorites_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName() , R.layout.cahapp_widget_provider);
        views.setTextViewText(R.id.tv_widget_title, widgetText);

        Intent mainIntent = new Intent(context, MapsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);
        views.setOnClickPendingIntent(R.id.tv_widget_title, pendingIntent);


        Intent intent = new Intent(context, CAHRemoteViewsService.class);
        views.setRemoteAdapter(R.id.lv_location_list, intent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /**
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    public static void updateLocationWidget(Context context, AppWidgetManager appWidgetManager,
                                          int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId );
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        LocationWidgetService.startActionUpdateLocationList(context);
        LocationWidgetService.startActionGetLocationList(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        LocationWidgetService.startActionUpdateLocationList(context);
        LocationWidgetService.startActionGetLocationList(context);
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        LocationWidgetService.startActionUpdateLocationList(context);
        //LocationWidgetService.startActionGetLocationList(context);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

