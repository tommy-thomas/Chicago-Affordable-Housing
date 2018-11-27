package org.affordablehousing.chi.housingapp.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.model.PropertyEntity;
import org.affordablehousing.chi.housingapp.viewmodel.PropertyListViewModel;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final String TAG = MapsActivity.class.getSimpleName() + " -- map acctivity";
    private LiveData <List <PropertyEntity>> propertyList;
    private PropertyListViewModel propertyListViewModel;
    private UiSettings mUiSettings;
    private LatLng CHICAGO_CENTER = new LatLng(41.8087574, -87.677451);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        propertyListViewModel =
                ViewModelProviders.of(this).get(PropertyListViewModel.class);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.map_fragment_container, mapFragment).commit();
        mapFragment.getMapAsync(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.type_options, menu);

        inflater.inflate(R.menu.community_options, menu);
        inflater.inflate(R.menu.property_type_options, menu);

        MenuItem item = menu.findItem(R.id.community_spinner);
        Spinner spinner = (Spinner) item.getActionView();

        MenuItem item1 = menu.findItem(R.id.property_type_spinner);
        Spinner spinner1 = (Spinner) item1.getActionView();


        propertyListViewModel.getComminites().observe(this, communites -> {
            if (communites != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        communites
                );

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }
        });


        propertyListViewModel.getPropertyTypes().observe(this, property_types -> {
            if (property_types != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        property_types
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner1.setAdapter(adapter);
            }
        });

        return true;
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
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        propertyListViewModel.getProperties().observe(this, propertyEntities -> {
            if (propertyEntities != null) {
                for (PropertyEntity property : propertyEntities) {
                    LatLng latLng = new LatLng(property.getLatitude(), property.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(property.getAddress()));
                }
            }
        });

        mUiSettings = mMap.getUiSettings();

        mUiSettings.setCompassEnabled(false);
        mUiSettings.setAllGesturesEnabled(true);
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(CHICAGO_CENTER)
                .bearing(112)
                .tilt(45)
                .zoom(13)
                .build();

        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CHICAGO.getCenter() , 13));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(CHICAGO_CENTER));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }


}


