package org.affordablehousing.chi.housingapp.ui;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.model.MarkerTag;
import org.affordablehousing.chi.housingapp.model.PropertyEntity;
import org.affordablehousing.chi.housingapp.viewmodel.PropertyListViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final String TAG = MapsActivity.class.getSimpleName() + " -- map acctivity";
    private PropertyListViewModel mPropertyListViewModel;
    private UiSettings mUiSettings;
    private ArrayList <Marker> mMapMarkers;
    private LatLng CURRENT_COMMUNITY = new LatLng(41.8087574, -87.677451);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mMapMarkers = new ArrayList <>();

        mPropertyListViewModel =
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
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
                String selectedCommunityText = (String) parent.getItemAtPosition(position);
                // Notify the selected item text
                if (position != 0) {
                    // Move camera to new selected community
                    MoveCameraToCommuty(selectedCommunityText);
                }
            }

            @Override
            public void onNothingSelected(AdapterView <?> parent) {

            }
        });

        MenuItem item1 = menu.findItem(R.id.property_type_spinner);
        Spinner spinner1 = (Spinner) item1.getActionView();
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
                String selectedPropertyTypeText = (String) parent.getItemAtPosition(position);
                showAllMarkers();
                // Notify the selected item text
                if (position != 0) {
                    for (Marker m : mMapMarkers) {
                        MarkerTag markerTag = (MarkerTag) m.getTag();
                        if ( !markerTag.getPropertyType().equals(selectedPropertyTypeText) ) {
                            Log.d(TAG, m.toString());
                            m.setVisible(false);
                        } else {
                            m.setVisible(true);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView <?> parent) {

            }
        });


        mPropertyListViewModel.getComminites().observe(this, communites -> {
            if (communites != null) {
                communites.add(0, "Community");
                ArrayAdapter <String> adapter = new ArrayAdapter <String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        communites
                );

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }
        });


        mPropertyListViewModel.getPropertyTypes().observe(this, property_types -> {
            if (property_types != null) {
                property_types.add(0, "Property Type");
                ArrayAdapter <String> adapter = new ArrayAdapter <String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        property_types
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setPrompt("Residence Type");
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

        mPropertyListViewModel.getProperties().observe(this, propertyEntities -> {
            if (propertyEntities != null) {
                for (PropertyEntity property : propertyEntities) {
                    LatLng latLng = new LatLng(property.getLatitude(), property.getLongitude());
                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(property.getAddress()));
                    marker.setTag(new MarkerTag(property.getProperty_type()));
                    mMapMarkers.add(marker);
                }
            }
        });

        mUiSettings = mMap.getUiSettings();

        mUiSettings.setCompassEnabled(false);
        mUiSettings.setAllGesturesEnabled(true);
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(CURRENT_COMMUNITY)
                .bearing(112)
                .tilt(45)
                .zoom(13)
                .build();

        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CHICAGO.getCenter() , 13));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(CURRENT_COMMUNITY));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    private void showAllMarkers() {
        if (mMapMarkers.size() > 0) {
            for (Marker m : mMapMarkers) {
                m.setVisible(true);
            }
        }

    }

    private void MoveCameraToCommuty(String community) {
        if (Geocoder.isPresent()) {
            try {
                String communityName = community + "Chicago, IL";
                // String location = "theNameOfTheLocation";
                Geocoder gc = new Geocoder(getApplicationContext());
                List <Address> addresses = gc.getFromLocationName(communityName, 5); // get the found Address Objects

                List <LatLng> ll = new ArrayList <LatLng>(addresses.size()); // A list to save the coordinates if they are available
                for (Address a : addresses) {
                    if (a.hasLatitude() && a.hasLongitude()) {
                        ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                    }
                }
                if (ll.get(0) != null) {
                    CURRENT_COMMUNITY = ll.get(0);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(CURRENT_COMMUNITY)
                            .bearing(112)
                            .tilt(45)
                            .zoom(13)
                            .build();

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else {

                }

            } catch (IOException e) {
                // handle the exception
            }
        }

    }
}


