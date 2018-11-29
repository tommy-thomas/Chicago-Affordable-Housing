package org.affordablehousing.chi.housingapp.ui;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.model.MarkerTag;
import org.affordablehousing.chi.housingapp.model.PropertyEntity;
import org.affordablehousing.chi.housingapp.viewmodel.PropertyListViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final String TAG = MapsActivity.class.getSimpleName() + " -- map acctivity";
    private PropertyListViewModel mPropertyListViewModel;
    private UiSettings mUiSettings;
    private ArrayList <Marker> mMapMarkers;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private LatLng CURRENT_LOCATION = new LatLng(41.8087574, -87.677451);
    private ArrayList <String> mCommunityList;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_community:
                    BottomSheetFragment bottomSheetFragment =
                            BottomSheetFragment.newInstance();
                    bottomSheetFragment.setMenuVisibility(true);
                    bottomSheetFragment.show(getSupportFragmentManager(),
                            "fragnment_bottomsheet");
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mMapMarkers = new ArrayList <>();

        mCommunityList = new ArrayList <>();

        mPropertyListViewModel =
                ViewModelProviders.of(this).get(PropertyListViewModel.class);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.map_fragment_container, mapFragment).commit();
        mapFragment.getMapAsync(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        getSupportActionBar().setDisplayShowTitleEnabled(false);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_map, menu);

        inflater.inflate(R.menu.actions_bottom_nav, menu);

        MenuItem community = menu.findItem(R.id.action_community);
        Spinner spinner = (Spinner) community.getActionView();



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
                String selectedCommunityText = (String) parent.getItemAtPosition(position);
                // Notify the selected item text
                if (position != 0) {
                    // Move camera to new selected community
                    moveCameraToCommunity(selectedCommunityText);
                }
            }

            @Override
            public void onNothingSelected(AdapterView <?> parent) {

            }


        });

//        MenuItem property_type = menu.findItem(R.id.action_property_type);
//        Spinner spinner1 = (Spinner) property_type.getActionView();
//        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
//                String selectedPropertyTypeText = (String) parent.getItemAtPosition(position);
//                //showAllMarkers();
//                // Notify the selected item text
//                if (position != 0) {
//                    for (Marker m : mMapMarkers) {
//                        MarkerTag markerTag = (MarkerTag) m.getTag();
//                        if ( !markerTag.getPropertyType().equals(selectedPropertyTypeText) ) {
//                            Log.d(TAG, m.toString());
//                            m.setVisible(false);
//                        } else {
//                            m.setVisible(true);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView <?> parent) {
//
//            }
//        });

//        MenuItem reset =  menu.findItem(R.id.action_reset);
//        Button resetButton = (Button) reset.getActionView();


//        mPropertyListViewModel.getCommunities().observe(this, communites -> {
//            if (communites != null) {
//                communites.add(0, "Community");
//                ArrayAdapter <String> adapter = new ArrayAdapter <String>(
//                        this,
//                        android.R.layout.simple_spinner_item,
//                        communites
//                );
//
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                spinner.setAdapter(adapter);
//            }
//        });


//        mPropertyListViewModel.getPropertyTypes().observe(this, property_types -> {
//            if (property_types != null) {
//                property_types.add(0, "Property Type");
//                ArrayAdapter <String> adapter = new ArrayAdapter <String>(
//                        this,
//                        android.R.layout.simple_spinner_item,
//                        property_types
//                );
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                spinner.setPrompt("Residence Type");
//                spinner1.setAdapter(adapter);
//            }
//        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_community:

            default:
                return super.onOptionsItemSelected(item);
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
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        getLocationPermission();

        mPropertyListViewModel.getProperties().observe(this, propertyEntities -> {
            if (propertyEntities != null) {
                for (PropertyEntity property : propertyEntities) {
                    LatLng latLng = new LatLng(property.getLatitude(), property.getLongitude());
                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(property.getProperty_name()));
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
                .target(CURRENT_LOCATION)
                .bearing(112)
                .tilt(45)
                .zoom(13)
                .build();

        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CHICAGO.getCenter() , 13));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(CURRENT_LOCATION));

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    private void showAllMarkers() {
        if (mMapMarkers.size() > 0) {
            for (Marker m : mMapMarkers) {
                m.setVisible(true);
            }
        }

    }

    private void moveCameraToCommunity(String community) {
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
                    CURRENT_LOCATION = ll.get(0);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(CURRENT_LOCATION)
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

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                //mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}


