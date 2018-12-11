package org.affordablehousing.chi.housingapp.ui;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.model.Location;
import org.affordablehousing.chi.housingapp.model.LocationEntity;
import org.affordablehousing.chi.housingapp.model.MarkerTag;
import org.affordablehousing.chi.housingapp.viewmodel.LocationListViewModel;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener,
        PropertyTypeListFragment.PropertyTypeClickListener {

    private GoogleMap mMap;
    private final String TAG = MapsActivity.class.getSimpleName() + " -- map acctivity";
    private LocationListViewModel mLocationListViewModel;
    private UiSettings mUiSettings;
    private ArrayList <Marker> mMapMarkers;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private LatLng CURRENT_LOCATION = new LatLng(41.8087574, -87.677451);
    private LatLng DEFAULT_LOCATION = new LatLng(41.8087574, -87.677451);
    private String CURRENT_COMMUNITY = "Community";
    private int SELECTED_COMMUNITY_INDEX = 0;
    private ArrayList <String> mPropertyTypeListFilter;
    private boolean mIsListDisplay = false;
    private final String KEY_LIST_FILTER = "list-filter";
    private final String KEY_CURRENT_COMMUNITY = "current-community";
    private final String KEY_CURRENT_LOCATION = "current-location";
    private final String KEY_SELECTED_COMMUNITY = "selected-community";
    private final String KEY_PROPERTY_FILTER = "property-filter-list";
    private final String KEY_SHOW_LOCATION = "show-location";
    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            setCurrentCommunity(savedInstanceState.getString(KEY_CURRENT_COMMUNITY, "Community"));
            setCurrentLocation(savedInstanceState.getString(KEY_CURRENT_LOCATION, ""));
            setPropertyTypeListFilter(savedInstanceState.getString(KEY_LIST_FILTER, ""));

        }

        /* map */
        setContentView(R.layout.activity_maps);

        mMapMarkers = new ArrayList <>();

        mPropertyTypeListFilter = new ArrayList <>();

        mLocationListViewModel =
                ViewModelProviders.of(this).get(LocationListViewModel.class);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment_container);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.map_fragment_container, mapFragment).commit();
        }
        mapFragment.setRetainInstance(true);
        mapFragment.getMapAsync(this);
        /* end map */

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        updateLocationUI();

        Toast toast = Toast.makeText(getApplicationContext(),
                String.valueOf("Create : " + getCurrentCommunity()),
                Toast.LENGTH_SHORT);
        toast.show();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Shows the product detail fragment
     */
    public void show(Location location) {

        LocationDetailFragment locationDetailFragment = LocationDetailFragment.forLocation(location.getLocationId());

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("location")
                .replace(R.id.map_fragment_container,
                        locationDetailFragment, null).commit();
    }

    public void favorite(Location location, boolean isFavorite) {

        mLocationListViewModel.setFavorite(location.getLocationId(), isFavorite);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        /* current community (String) */
        outState.putString(KEY_CURRENT_COMMUNITY, mSpinner.getSelectedItem().toString());
        /* current community (int) */
        outState.putInt(KEY_SELECTED_COMMUNITY, mSpinner.getSelectedItemPosition());
        /* current location (LatLng) */
        Gson gson = new Gson();
        Type latlng = new TypeToken <LatLng>() {
        }.getType();
        String currentLocation = gson.toJson(CURRENT_LOCATION, latlng);
        outState.putString(KEY_CURRENT_LOCATION, currentLocation);
        Type list = new TypeToken <ArrayList <String>>() {
        }.getType();
        String listFilter = gson.toJson(mPropertyTypeListFilter, list);
        outState.putString(KEY_PROPERTY_FILTER, listFilter);
        outState.putInt(KEY_SELECTED_COMMUNITY, SELECTED_COMMUNITY_INDEX);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            setCurrentCommunity(savedInstanceState.getString(KEY_CURRENT_COMMUNITY, "Community"));
            setCurrentLocation(savedInstanceState.getString(KEY_CURRENT_LOCATION, ""));
            setSelectedCommunity(savedInstanceState.getInt(KEY_SELECTED_COMMUNITY));
            setPropertyTypeListFilter(savedInstanceState.getString(KEY_LIST_FILTER));

            Toast toast = Toast.makeText(getApplicationContext(),
                    "Restore Instance: " + savedInstanceState.getString(KEY_CURRENT_LOCATION),
                    Toast.LENGTH_SHORT);
            toast.show();
        }
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    protected void onStart() {

        if (mPropertyTypeListFilter.size() > 0 && mMapMarkers.size() > 0) {

            filterMarkers();
        }
        super.onStart();
    }


    private void filterMarkers() {

        if (mPropertyTypeListFilter.size() == 0 && CURRENT_COMMUNITY.equals( "Community")) {
            resetMarkers();
        }

        if (mPropertyTypeListFilter.size() == 0 && !CURRENT_COMMUNITY.equals("Comunity")) {
            for (int i = 0; i < mMapMarkers.size(); i++) {
                Marker marker = mMapMarkers.get(i);
                MarkerTag markerTag = (MarkerTag) marker.getTag();
                if (!mPropertyTypeListFilter.contains(markerTag.getPropertyType())) {
                    mMapMarkers.get(i).setVisible(true);
                }
            }
        }

        if (mPropertyTypeListFilter.size() > 0) {
            for (int i = 0; i < mMapMarkers.size(); i++) {
                Marker marker = mMapMarkers.get(i);
                MarkerTag markerTag = (MarkerTag) marker.getTag();
                if (!mPropertyTypeListFilter.contains(markerTag.getPropertyType())) {
                    mMapMarkers.get(i).setVisible(false);
                }
            }
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    filterMarkers();
                    FragmentManager fm = getSupportFragmentManager();
                    for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                        fm.popBackStack();
                    }
                    setIsListDisplay(false);
                    return true;
                case R.id.navigation_list:
                    showLocationList();
                    return true;
                case R.id.navigation_filter:
                    showPropertyTypeFilterList();
                    return true;
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        /* top nav */
        inflater.inflate(R.menu.actions_map, menu);

        /* community list spinner */
        inflater.inflate(R.menu.community_list, menu);

        /* handle community list */
        MenuItem community = menu.findItem(R.id.action_community);
        mSpinner = (Spinner) community.getActionView();

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
                String selectedCommunityText = (String) parent.getItemAtPosition(position);
                // Notify the selected item text
                setCurrentCommunity(selectedCommunityText);
                setSelectedCommunity(position);
                if (position != 0) {
                    // Move camera to new selected community
                    if (isListDisplay()) {
                        showLocationList();
                    }
                    moveCameraToCommunity(selectedCommunityText);
                } else {
                    moveCameraToDefaultLocation();
                }
            }

            @Override
            public void onNothingSelected(AdapterView <?> parent) {

            }
        });

        mLocationListViewModel.getCommunities().observe(this, communites -> {
            if (communites != null && communites.size() > 0) {
                if (!communites.get(0).equals("Community")) {
                    communites.add(0, "Community");
                }
                ArrayAdapter <String> adapter = new ArrayAdapter <String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        communites
                );

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(adapter);
            }
        });
        mSpinner.setSelection(SELECTED_COMMUNITY_INDEX);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_reset:
                resetMarkers();
            case R.id.action_note_edit:
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Edit Node",
                        Toast.LENGTH_SHORT);
                toast.show();
                super.onResume();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.navigation_home:
                return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPropertyTypeSelected(String propertyType) {

        if (!propertyType.isEmpty() && !mPropertyTypeListFilter.contains(propertyType)) {
            mPropertyTypeListFilter.add(propertyType);
        } else {
            mPropertyTypeListFilter.remove(propertyType);
        }

    }

    private void setIsListDisplay(boolean isListDisplay) {
        mIsListDisplay = isListDisplay;
    }

    private boolean isListDisplay() {
        return mIsListDisplay;
    }

    private void showLocationList() {

        setIsListDisplay(true);
        LocationListFragment locationListFragment = new LocationListFragment();

        Bundle bundle = new Bundle();
        bundle.putStringArrayList(KEY_LIST_FILTER, mPropertyTypeListFilter);
        bundle.putString(KEY_CURRENT_COMMUNITY, getCurrentCommunity());
        locationListFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.map_fragment_container, locationListFragment);
        ft.commit();
        ft.addToBackStack(null);
    }

    private void showPropertyTypeFilterList() {
        setIsListDisplay(false);
        PropertyTypeListFragment propertyTypeListFragment = new PropertyTypeListFragment();

        Bundle bundle = new Bundle();
        bundle.putStringArrayList(KEY_LIST_FILTER, mPropertyTypeListFilter);
        propertyTypeListFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.map_fragment_container, propertyTypeListFragment);
        ft.commit();
        ft.addToBackStack(null);
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

        mLocationListViewModel.getLocations().observe(this, propertyEntities -> {
            if (propertyEntities != null) {
                for (LocationEntity property : propertyEntities) {
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

    private void setCurrentCommunity(String currentCommunity) {
        CURRENT_COMMUNITY = currentCommunity;
    }

    private String getCurrentCommunity() {
        return CURRENT_COMMUNITY;
    }

    private void setSelectedCommunity(int selectedCommunity) {
        SELECTED_COMMUNITY_INDEX = selectedCommunity;
    }

    private void setCurrentLocation(String currentLocation) {
        Gson gson = new Gson();
        Type type = new TypeToken <LatLng>() {
        }.getType();
        CURRENT_LOCATION = gson.fromJson(currentLocation, type);
    }

    private void setPropertyTypeListFilter(String listFilter) {
        if (mPropertyTypeListFilter != null && listFilter != null) {
            Gson gson = new Gson();
            Type type = new TypeToken <ArrayList <String>>() {
            }.getType();
            mPropertyTypeListFilter = gson.fromJson(listFilter, type);

        }
    }

    private void resetMarkers() {
        for (int i = 0; i < mMapMarkers.size(); i++) {
            mMapMarkers.get(i).setVisible(true);
        }
        mPropertyTypeListFilter = new ArrayList <>();
        moveCameraToDefaultLocation();
    }

    private void moveCameraToDefaultLocation() {
        if (Geocoder.isPresent()) {
            try {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(DEFAULT_LOCATION)
                        .bearing(112)
                        .tilt(45)
                        .zoom(13)
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } catch (Exception e) {
                // handle the exception
            }
        }

    }

    private void moveCameraToCommunity(String community) {
        setCurrentCommunity(community);
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