package org.affordablehousing.chi.housing.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class CAHRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new CAHRemoteViewsFactory( this.getApplicationContext(), intent);
    }
}
