package org.affordablehousing.chi.chicagoaffordablehousingapp.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.affordablehousing.chi.chicagoaffordablehousingapp.R;
import org.affordablehousing.chi.chicagoaffordablehousingapp.model.Property;
import org.affordablehousing.chi.chicagoaffordablehousingapp.network.GetPropertyDataService;
import org.affordablehousing.chi.chicagoaffordablehousingapp.network.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final String BASE_URL = "https://data.cityofchicago.org";
    private final String TAG = MapsActivity.class.getSimpleName() + " -- udacity";
    private List<Property> propertList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        /*Create handle for the RetrofitInstance interface*/
        GetPropertyDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetPropertyDataService.class);
        Call<List<Property>> call = service.getAllProperties();
        call.enqueue(new Callback<List<Property>>() {
            @Override
            public void onResponse(Call<List<Property>> call, Response<List<Property>> response) {
                propertList = response.body();
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(MapsActivity.this);
                Log.v(TAG , propertList.get(1).getAddress());
            }

            @Override
            public void onFailure(Call<List<Property>> call, Throwable t) {
                Log.d(TAG , t.getMessage());
            }
        });




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

        for (Property property : propertList) {
            LatLng latLng  = new LatLng(property.getLatitude(),property.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(property.getAddress()));
        }

        // Add a marker in Sydney and move the camera
        LatLng chicago_home  = new LatLng(41.7970789,-87.5923087);
        LatLng chicago_store  = new LatLng(41.8019229,-87.5902032);
        mMap.addMarker(new MarkerOptions().position(chicago_home).title("5429 S. Blackstone"));
        mMap.addMarker(new MarkerOptions().position(chicago_store).title("5118 S Lake Park Ave"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(chicago_home));
    }




}
