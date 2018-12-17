package org.affordablehousing.chi.housingapp.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.model.LocationEntity;
import org.affordablehousing.chi.housingapp.service.LocationWidgetService;

import java.lang.reflect.Type;
import java.util.List;

import static android.widget.RemoteViewsService.RemoteViewsFactory;

public class CAHRemoteViewsFactory implements RemoteViewsFactory {

    private Context mContext;
    private List <LocationEntity> mLocationEntityList;
    private final String KEY_LOCATION_LIST_DATA = "location-list-data";
    private final String TAG = CAHRemoteViewsFactory.class.getSimpleName();
    private LocationWidgetReceiver mLocationWidgetReceiver;


    public CAHRemoteViewsFactory(Context applicationContext, Intent intent) {

        this.mContext = applicationContext;

        registerLocationListReceiver();

      LocationWidgetService.startActionGetLocationList(mContext);
    }

    @Override
    public void onCreate() {
        LocationWidgetService.startActionGetLocationList(mContext);
    }

    @Override
    public void onDataSetChanged() {
        LocationWidgetService.startActionGetLocationList(mContext);
    }

    @Override
    public void onDestroy() {
        mContext.unregisterReceiver(mLocationWidgetReceiver);
    }

    @Override
    public int getCount() {
        return (mLocationEntityList != null) ? mLocationEntityList.size() : 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        if (getCount() > 0) {
            remoteViews.setTextViewText(R.id.tv_widget_property_name, mLocationEntityList.get(position).getProperty_name());
        }

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void registerLocationListReceiver() {
        mLocationWidgetReceiver = new LocationWidgetReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LocationWidgetService.ACTION_GET_LOCATION_LIST);
        mContext.registerReceiver(mLocationWidgetReceiver, intentFilter);
        Log.d(TAG, "FACTORY REGISTERED");
    }

    private class LocationWidgetReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String stringLocationList = intent.getStringExtra(KEY_LOCATION_LIST_DATA);

                Gson gson = new Gson();
                Type type = new TypeToken <List <LocationEntity>>() {
                }.getType();
                mLocationEntityList = gson.fromJson(stringLocationList, type);
                Log.d(TAG, "Data received.");


        }
    }
}
