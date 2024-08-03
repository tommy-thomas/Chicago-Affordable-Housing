package org.affordablehousing.chi.housing.service;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.affordablehousing.chi.housing.AppExecutors;
import org.affordablehousing.chi.housing.R;
import org.affordablehousing.chi.housing.data.LocationDatabase;
import org.affordablehousing.chi.housing.model.LocationEntity;
import org.affordablehousing.chi.housing.widget.CAHAppWidgetProvider;

import java.lang.reflect.Type;
import java.util.List;

import androidx.annotation.Nullable;

public class LocationWidgetService extends IntentService {

    public static final String ACTION_UPDATE_LOCATION_LIST = "org.affordablehousing.chi.housing.service.action.update_location_list";
    public static final String ACTION_GET_LOCATION_LIST = "org.affordablehousing.chi.housing.service.action.get_location_list";
    private List <LocationEntity> mLocationList;
    final static String KEY_LOCATION_LIST_DATA = "location-list-data";
    private Context mContext;
    private final String TAG = LocationWidgetService.class.getSimpleName();


    public LocationWidgetService() {
        super(LocationWidgetService.class.getName());

    }

    public static void startActionUpdateLocationList(Context context) {
        Intent intent = new Intent(context, LocationWidgetService.class);
        intent.setAction(ACTION_UPDATE_LOCATION_LIST);
        context.startService(intent);
    }

    public static void startActionGetLocationList(Context context) {
        Intent intent = new Intent(context, LocationWidgetService.class);
        intent.setAction(ACTION_GET_LOCATION_LIST);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_LOCATION_LIST.equals(action)) {
                sendLocationListBackToClient();
            } else if (ACTION_UPDATE_LOCATION_LIST.equals(action)) {
                handleActionUpdateLocationList();
            }
        }

    }

    private void handleActionUpdateLocationList() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, CAHAppWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_location_list);
        CAHAppWidgetProvider.updateLocationWidget(this, appWidgetManager, appWidgetIds);
        Log.d(TAG , "HANDLE ACTION " + LocationWidgetService.class.getSimpleName());
    }

    public static void notifyServiceUpdateLocationWidget(Context context){

        startActionUpdateLocationList(context);

        Intent intent = new Intent(context, CAHAppWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, CAHAppWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    private void sendLocationListBackToClient() {
        AppExecutors appExecutors = new AppExecutors();
        LocationDatabase locationDatabase = LocationDatabase.getInstance(this , appExecutors);
        mLocationList = locationDatabase.locationDAO().loadFavoritesforWidget();
        Gson gson = new Gson();
        Type type = new TypeToken<List <LocationEntity>>() {
        }.getType();
        String json = gson.toJson(mLocationList, type);
        Intent intent = new Intent();
        intent.setAction(ACTION_GET_LOCATION_LIST);
        intent.putExtra(KEY_LOCATION_LIST_DATA, json);
        sendBroadcast(intent);
    }
}
