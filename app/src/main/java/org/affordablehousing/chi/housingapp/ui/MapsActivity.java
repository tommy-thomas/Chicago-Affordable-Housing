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
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.model.MarkerTag;
import org.affordablehousing.chi.housingapp.model.PropertyEntity;
import org.affordablehousing.chi.housingapp.viewmodel.PropertyListViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener,
        PropertyTypeListFragment.PropertyTypeClickListener {

    private GoogleMap mMap;
    private final String TAG = MapsActivity.class.getSimpleName() + " -- map acctivity";
    private PropertyListViewModel mPropertyListViewModel;
    private UiSettings mUiSettings;
    private ArrayList <Marker> mMapMarkers;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private LatLng CURRENT_LOCATION = new LatLng(41.8087574, -87.677451);
    private ArrayList <String> mPropertyTypeListFilter;
    private ArrayList <String> mPropertyTypeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mMapMarkers = new ArrayList <>();

        mPropertyTypeListFilter = new ArrayList <>();

        mPropertyListViewModel =
                ViewModelProviders.of(this).get(PropertyListViewModel.class);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.map_fragment_container, mapFragment).commit();
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }


    @Override
    protected void onStart() {

        Toast toast = Toast.makeText(getApplicationContext(),
                "Activity Started.",
                Toast.LENGTH_SHORT);
        toast.show();

        if( mPropertyTypeListFilter.size() > 0 && mMapMarkers.size() > 0 ){

            toast = Toast.makeText(getApplicationContext(),
                    "This is a message displayed in a Toast",
                    Toast.LENGTH_SHORT);
            toast.show();

            filterMarkers();
        }
        super.onStart();
    }

    private void  filterMarkers(){
        for (int i=0; i< mMapMarkers.size(); i++ ) {
            Marker marker = mMapMarkers.get(i);
            MarkerTag markerTag = (MarkerTag) marker.getTag();
            if( !mPropertyTypeListFilter.contains(markerTag) ){
                mMapMarkers.get(i).setVisible(false);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_map, menu);

        inflater.inflate(R.menu.community_list, menu);

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
        mPropertyListViewModel.getCommunities().observe(this, communites -> {
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


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_property_type_filter:

                PropertyTypeListFragment propertyTypeListFragment = new PropertyTypeListFragment();

                Bundle bundle = new Bundle();
                bundle.putStringArrayList( "LIST_FILTER", mPropertyTypeListFilter );
                propertyTypeListFragment.setArguments( bundle );
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.map_fragment_container, propertyTypeListFragment);
                ft.commit();
                ft.addToBackStack(null);

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPropertypeSelected(String propertyType) {

        if( !propertyType.isEmpty() &&  !mPropertyTypeListFilter.contains(propertyType)){
            mPropertyTypeListFilter.add( propertyType );
        } else {
            mPropertyTypeListFilter.remove( propertyType );
        }

    }
}


