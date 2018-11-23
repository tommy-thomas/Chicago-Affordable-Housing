package org.affordablehousing.chi.housingapp.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.data.PropertyDAO;
import org.affordablehousing.chi.housingapp.data.PropertyDatabase;
import org.affordablehousing.chi.housingapp.model.Property;
import org.affordablehousing.chi.housingapp.network.GetPropertyDataService;
import org.affordablehousing.chi.housingapp.network.RetrofitClientInstance;

import java.util.List;

import androidx.fragment.app.FragmentActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final String TAG = MapsActivity.class.getSimpleName() + " -- udacity";
    private List<Property> propertyList;
    private PropertyDatabase propertyDatabase;
    private UiSettings mUiSettings;
    private LatLng CHICAGO_CENTER = new LatLng(41.8087574,-87.677451);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        new FetchPropertiesTask(this).execute();

        if( propertyList == null ){
        /*Create handle for the RetrofitInstance interface*/
        GetPropertyDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetPropertyDataService.class);
        Call<List<Property>> call = service.getAllProperties();
        call.enqueue(new Callback<List<Property>>() {
            @Override
            public void onResponse(Call<List<Property>> call, Response<List<Property>> response) {
                propertyList = response.body();

                new SavePropertiesTask(propertyList).execute();

                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(MapsActivity.this);
                Log.v(TAG , propertyList.get(1).getAddress());
            }

            @Override
            public void onFailure(Call<List<Property>> call, Throwable t) {
                Log.d(TAG , t.getMessage());
            }
        });
        } else {
            Log.d(TAG , "From DB.");
        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();

        for (Property property : propertyList) {
            LatLng latLng  = new LatLng(property.getLatitude(),property.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(property.getAddress()));
        }
        mUiSettings.setZoomControlsEnabled(true);
       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CHICAGO.getCenter() , 13));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(CHICAGO_CENTER));

    }


    private class FetchPropertiesTask extends AsyncTask<Void,Void,Void>{


        private Context aynscContext;

        public FetchPropertiesTask(Context context) {
            aynscContext = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            PropertyDatabase propertyDatabase = PropertyDatabase.getPropertyDatababse(aynscContext);
            propertyList = propertyDatabase.propertyDAO().loadAllProperties();
            return null;
        }
    }


    private class SavePropertiesTask extends AsyncTask<Void,Void,Void>{

        private List<Property> propertyList;

        public SavePropertiesTask(List<Property> properties){
            propertyList = properties;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if( !propertyList.isEmpty() ){
                PropertyDAO propertyDAO = PropertyDatabase.getPropertyDatababse(MapsActivity.this).propertyDAO();
                for (Property property : propertyList) {
                    propertyDAO.save(property);
                }
                Log.v(TAG + " -- DATA LOADED " , propertyDAO.loadAllProperties().get(1).getAddress());
            }
            return null;
        }
    }


}
