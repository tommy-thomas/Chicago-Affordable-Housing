package org.affordablehousing.chi.housingapp.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
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

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.model.Location;
import org.affordablehousing.chi.housingapp.model.LocationEntity;
import org.affordablehousing.chi.housingapp.model.MarkerTag;
import org.affordablehousing.chi.housingapp.service.LocationSyncService;
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
import androidx.recyclerview.widget.RecyclerView;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static org.affordablehousing.chi.housingapp.ui.PropertyTypeListFragment.PropertyTypeClickListener;

public class MapsActivity extends AppCompatActivity implements
        OnNavigationItemSelectedListener,
        PropertyTypeClickListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int JOB_START_TIME = 2592000;
    private static final int JOB_END_TIME = 2678400;
    private static final String TAG = MapsActivity.class.getSimpleName() + " -- MAP ACTIVITY -- ";
    private static final String KEY_CURRENT_COMMUNITY = "current-community";
    private static final String KEY_CURRENT_LOCATION = "current-location";
    private static final String KEY_SELECTED_COMMUNITY_INDEX = "selected-community-index";
    private static final String KEY_PROPERTY_LIST_FILTER = "property-filter-list";
    private static final String KEY_SHOW_LIST = "show-list";
    private static final String KEY_SHOW_LOCATION = "show-location";
    private static final String KEY_LOCATION_OBJECT = "location-object";
    private static final String KEY_SHOW_MAP = "show-map";
    private static final String KEY_SHOW_PROPERTY_TYPE_LIST = "show-type-list";
    private static final String KEY_SHOW_FAVORITES = "show-favorites";
    private static final String URL_CHICAGO_DATA = "https://data.cityofchicago.org/Community-Economic-Development/Affordable-Rental-Housing-Developments/s6ha-ppgi";
    private GoogleMap mMap;
    private LocationListViewModel mLocationListViewModel;
    private UiSettings mUiSettings;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationClient;
    private SupportMapFragment mSupportMapFragment;
    private CameraPosition mCameraPosition;
    private boolean mLocationPermissionGranted;
    private ArrayList <Marker> mMapMarkers;
    private Spinner mSpinner;
    private MenuItem mReset;
    private boolean mTwoPane;
    private int mContentFrameLayoutId;
    private int DEFAULT_ZOOM = 13;
    private int DEFAULT_BEARING = 90;
    private int DEFAULT_TILT = 30;
    private LatLng CURRENT_LOCATION;
    private LatLng DEFAULT_LOCATION = new LatLng(41.8087574, -87.677451);
    private String CURRENT_COMMUNITY = "Community";
    private int SELECTED_COMMUNITY_INDEX = 0;
    private ArrayList <String> mPropertyTypeListFilter;
    private boolean mIsListDisplay = false;
    private boolean mIsShowLocation = false;
    private Location mLocationObject = null;
    private boolean mIsShowFavorites = false;
    private boolean mIsShowMap = true;
    private boolean mIsShowPropertyTypeList = false;
    private FirebaseJobDispatcher mJobDispatcher;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    filterMarkers();
                    if (!isTwoPane()) {
                        FragmentManager fm = getSupportFragmentManager();
                        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                            fm.popBackStack();
                        }
                        if (mSpinner != null && mReset != null) {
                            mReset.setVisible(true);
                            mSpinner.setVisibility(View.VISIBLE);
                        }
                        setIsShowMap(true);
                    }
                    return true;
                case R.id.navigation_list:
                    showLocationList();
                    setIsListDisplay(true);
                    return true;
                case R.id.navigation_filter:
                    showPropertyTypeFilterList();
                    setIsShowPropertyTypeList(true);
                    return true;
            }
            return false;
        }

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        restoreState(savedInstanceState);

        /* job dispatcher */
        mJobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        /* map view */
        setContentView(R.layout.activity_maps);

        /* Two panes */
        setTwoPane();

        mMapMarkers = new ArrayList <>();

        mPropertyTypeListFilter = new ArrayList <>();

        mLocationListViewModel =
                ViewModelProviders.of(this).get(LocationListViewModel.class);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        setMapLocation();
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

        if (isTwoPane()) {
            showLocationList();
        }

        scheduleSyncJob();

    }

    private void setNavItemChecked(){
        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        if( isShowMap() ){
            navigationView.setSelectedItemId(R.id.navigation_home);
        } else if( isShowLocation() || isListDisplay() || isShowFavorites() ){
            navigationView.setSelectedItemId(R.id.navigation_list);
        } else if( isShowPropertyTypeList() ){
            navigationView.setSelectedItemId(R.id.navigation_filter);
        }
    }

    private void scheduleSyncJob() {

        Job syncJob = mJobDispatcher.newJobBuilder()
                .setService(LocationSyncService.class)
                .setTag(TAG)
                .setRecurring(true)
                //.setTrigger(Trigger.executionWindow(JOB_START_TIME,JOB_END_TIME))  FOR RELEASE //
                .setTrigger(Trigger.executionWindow(0, 10))
                .setLifetime(Lifetime.FOREVER)
                .setReplaceCurrent(false)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();
        mJobDispatcher.mustSchedule(syncJob);
        //Toast.makeText(this, "Data sync has been scheduled.", Toast.LENGTH_LONG).show();
    }

    private void restoreState(Bundle savedInstanceState) {

        if (savedInstanceState != null) {

            setCurrentCommunity(savedInstanceState.getString(KEY_CURRENT_COMMUNITY, "Community"));
            setCurrentLocation(savedInstanceState.getString(KEY_CURRENT_LOCATION, ""));
            setSelectedCommunity(savedInstanceState.getInt(KEY_SELECTED_COMMUNITY_INDEX));
            setPropertyTypeListFilter(savedInstanceState.getString(KEY_PROPERTY_LIST_FILTER));
            setLocationObject(savedInstanceState.getString(KEY_LOCATION_OBJECT));

            setIsShowMap(savedInstanceState.getBoolean(KEY_SHOW_MAP));
            setIsShowPropertyTypeList(savedInstanceState.getBoolean(KEY_SHOW_PROPERTY_TYPE_LIST));
            setIsShowFavorites(savedInstanceState.getBoolean(KEY_SHOW_FAVORITES));
            setIsListDisplay(savedInstanceState.getBoolean(KEY_SHOW_LIST));
            setIsShowLocation(savedInstanceState.getBoolean(KEY_SHOW_LOCATION));

            if (isShowMap()) {
                setMapLocation();
                Toast.makeText( this , "show map", Toast.LENGTH_LONG).show();
            }

            if (isShowLocation()) {
                show(mLocationObject);
            }

            if (isListDisplay()) {
                showLocationList();
            }

            if (isShowFavorites()) {
                showFavorites();
                hiudeMenus();
            }

            if (isShowPropertyTypeList()) {
                showPropertyTypeFilterList();
                hiudeMenus();
            }

            setNavItemChecked();

        }
        // super.onRestoreInstanceState(savedInstanceState);

    }

    private void setTwoPane() {
        if (findViewById(R.id.fr_content_fragment_container) != null) {
            mTwoPane = true;
            mContentFrameLayoutId = R.id.fr_content_fragment_container;
        } else {
            mTwoPane = false;
            mContentFrameLayoutId = R.id.map_fragment_container;
        }
    }

    private boolean isTwoPane() {
        return mTwoPane;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && isTwoPane()) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        setTwoPane();
        super.onResume();
    }

    /**
     * Shows the product detail fragment
     */
    public void show(Location location) {

        if (mSpinner != null && mReset != null) {
            mReset.setVisible(false);
            mSpinner.setVisibility(View.INVISIBLE);
        }

        mLocationObject = location;

        LocationDetailFragment locationDetailFragment = LocationDetailFragment.forLocation(location.getLocationId());

        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack("location")
                .replace(mContentFrameLayoutId,
                        locationDetailFragment, null).commit();

    }

    public void favorite(Location location, boolean isFavorite) {
        mLocationListViewModel.setFavorite(location.getLocationId(), isFavorite);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        /* current community (String) */
        outState.putString(KEY_CURRENT_COMMUNITY, getCurrentCommunity());
        /* current location (LatLng) */
        Gson gson = new Gson();
        Type latlng = new TypeToken <LatLng>() {
        }.getType();
        String currentLocation = gson.toJson(CURRENT_LOCATION, latlng);
        /* current LatLng gson object */
        outState.putString(KEY_CURRENT_LOCATION, currentLocation);
        Type list = new TypeToken <ArrayList <String>>() {
        }.getType();
        String listFilter = gson.toJson(mPropertyTypeListFilter, list);
        /* property types gson object */
        outState.putString(KEY_PROPERTY_LIST_FILTER, listFilter);
        /* selected community index int */
        outState.putInt(KEY_SELECTED_COMMUNITY_INDEX, SELECTED_COMMUNITY_INDEX);
        /* screen display flags */
        outState.putBoolean(KEY_SHOW_LIST, isListDisplay());
        outState.putBoolean(KEY_SHOW_MAP, isShowMap());
        outState.putBoolean(KEY_SHOW_FAVORITES, isShowFavorites());
        outState.putBoolean(KEY_SHOW_PROPERTY_TYPE_LIST, isShowPropertyTypeList());
        outState.putBoolean(KEY_SHOW_LOCATION, isShowLocation());
        Type location = new TypeToken <Location>() {
        }.getType();
        String locationObject = gson.toJson(mLocationObject, location);
        outState.putString(KEY_LOCATION_OBJECT, locationObject);
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        }
       // super.onRestoreInstanceState(savedInstanceState);

    }

    private void hiudeMenus(){
        if (mSpinner != null && mReset != null) {
            mReset.setVisible(false);
            mSpinner.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {

        if (mPropertyTypeListFilter.size() > 0 && mMapMarkers.size() > 0) {

            filterMarkers();
        }
        super.onStart();
    }

    private void filterMarkers() {

        for (int i = 0; i < mMapMarkers.size(); i++) {
            mMapMarkers.get(i).setVisible(true);
        }

        if (mPropertyTypeListFilter.size() == 0 && CURRENT_COMMUNITY.equals("Community")) {
            resetMarkers();
            return;
        }

        if (mPropertyTypeListFilter.size() == 0 && !CURRENT_COMMUNITY.equals("Comunity")) {
            for (int i = 0; i < mMapMarkers.size(); i++) {
                Marker marker = mMapMarkers.get(i);
                MarkerTag markerTag = (MarkerTag) marker.getTag();
                if (!mPropertyTypeListFilter.contains(markerTag.getPropertyType())) {
                    mMapMarkers.get(i).setVisible(true);
                }
            }
            return;
        }

        if (mPropertyTypeListFilter.size() > 0) {
            for (int i = 0; i < mMapMarkers.size(); i++) {
                Marker marker = mMapMarkers.get(i);
                MarkerTag markerTag = (MarkerTag) marker.getTag();
                if (!mPropertyTypeListFilter.contains(markerTag.getPropertyType())) {
                    mMapMarkers.get(i).setVisible(false);
                }
            }
            return;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        /* top nav */
        inflater.inflate(R.menu.actions_map, menu);
        mReset = menu.findItem(R.id.action_reset);

        /* community list spinner */
        inflater.inflate(R.menu.community_list, menu);
        MenuItem community = menu.findItem(R.id.action_community);
        mSpinner = (Spinner) community.getActionView();

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
                String selectedCommunityText = (String) parent.getItemAtPosition(position);
                // Notify the selected item text
                setCurrentCommunity(selectedCommunityText);

                view.setBackground(getDrawable(R.drawable.rounded_red_bg));
                view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                view.setPadding(4, 2, 2, 4);
                view.setMinimumWidth(420);

                setSelectedCommunity(position);

                moveCameraToCommunity(selectedCommunityText);

                if (position != 0) {
                    // Move camera to new selected community
                    if (isListDisplay()) {
                        showLocationList();
                    }
                    if (isShowFavorites()) {
                        showFavorites();
                    }
                    if( isShowFavorites() || isShowPropertyTypeList() ){
                        hiudeMenus();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView <?> parent) {
                mSpinner.setSelection(SELECTED_COMMUNITY_INDEX);
            }
        });


        mLocationListViewModel.getCommunities().observe(this, communities -> {
            if (communities != null && communities.size() > 0) {
                if (!communities.get(0).equals("Community")) {
                    communities.add(0, "Community");
                }
                ArrayAdapter <String> adapter = new ArrayAdapter <String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        communities
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(adapter);
                mSpinner.setSelection(SELECTED_COMMUNITY_INDEX);
            }
        });

        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //on Handle item selection
        switch (item.getItemId()) {
            case R.id.action_reset:
                resetMarkers();
                if( isListDisplay() ){
                    setCurrentCommunity("Community");
                    showLocationList();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_favorite:
                showFavorites();
                break;
            case R.id.nav_info:
                Intent infoIntent = new Intent( this, InfoActivity.class);
                startActivity( infoIntent );
                break;
            case R.id.nav_chicago_data:
                Intent urlIntent = new Intent(Intent.ACTION_VIEW);
                urlIntent.setData(Uri.parse(URL_CHICAGO_DATA));
                startActivity(urlIntent);
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
        if (isTwoPane()) {
            filterMarkers();
        }

    }

    private void setIsListDisplay(boolean isListDisplay) {
        mIsListDisplay = isListDisplay;
        mIsShowFavorites = false;
        mIsShowMap = false;
        mIsShowPropertyTypeList = false;
        mIsShowLocation = false;

    }

    private void setIsShowLocation(boolean isShowLocation) {
        mIsShowLocation = isShowLocation;
        mIsListDisplay = false;
        mIsShowFavorites = false;
        mIsShowMap = false;
        mIsShowPropertyTypeList = false;

    }

    private void setIsShowFavorites(boolean isShowFavorites) {
        mIsShowFavorites = isShowFavorites;
        mIsListDisplay = false;
        mIsShowMap = false;
        mIsShowPropertyTypeList = false;
        mIsShowLocation = false;
    }

    private void setIsShowMap(boolean isShowMap) {
        mIsShowMap = isShowMap;
        mIsShowFavorites = false;
        mIsListDisplay = false;
        mIsShowPropertyTypeList = false;
        mIsShowLocation = false;

    }

    private void setIsShowPropertyTypeList(boolean isShowPropertyTypeList) {
        mIsShowPropertyTypeList = isShowPropertyTypeList;
        mIsShowMap = false;
        mIsShowFavorites = false;
        mIsListDisplay = false;
        mIsShowLocation = false;

    }

    private boolean isListDisplay() {

        RecyclerView rv = findViewById(R.id.rv_location_list);
        return ( rv != null && rv.getContentDescription().equals("Location List"));

    }

    private boolean isShowLocation() {
        return mIsShowLocation;
    }

    private boolean isShowFavorites() {

        RecyclerView rv = findViewById(R.id.rv_location_list);
        return ( rv != null && rv.getContentDescription().equals("Favorites List"));
    }

    private boolean isShowMap() {
        return mIsShowMap;
    }

    private boolean isShowPropertyTypeList() {
     return findViewById(R.id.rv_property_type) != null ;

    }

    private void showLocationList() {

        if (mSpinner != null && mReset != null) {
            mReset.setVisible(true);
            mSpinner.setVisibility(View.VISIBLE);
        }

        LocationListFragment locationListFragment = new LocationListFragment();

        Bundle bundle = new Bundle();
        bundle.putStringArrayList(KEY_PROPERTY_LIST_FILTER, mPropertyTypeListFilter);
        bundle.putString(KEY_CURRENT_COMMUNITY, getCurrentCommunity());
        locationListFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(mContentFrameLayoutId, locationListFragment)
                .addToBackStack(null)
                .commit();

        setIsListDisplay(true);
    }

    private void showFavorites() {

        if (mSpinner != null && mReset != null) {
            mReset.setVisible(false);
            mSpinner.setVisibility(View.INVISIBLE);
        }

        LocationListFragment locationListFragment = new LocationListFragment();

        Bundle bundle = new Bundle();
        bundle.putStringArrayList(KEY_PROPERTY_LIST_FILTER, new ArrayList <>());
        bundle.putString(KEY_CURRENT_COMMUNITY, "showFavorites");
        locationListFragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(mContentFrameLayoutId, locationListFragment)
                .addToBackStack(null)
                .commit();

        setIsShowFavorites(true);
    }

    private void showPropertyTypeFilterList() {

        if (mSpinner != null && mReset != null) {
            mReset.setVisible(false);
            mSpinner.setVisibility(View.INVISIBLE);
        }

        PropertyTypeListFragment propertyTypeListFragment = new PropertyTypeListFragment();

        Bundle bundle = new Bundle();
        bundle.putStringArrayList(KEY_PROPERTY_LIST_FILTER, mPropertyTypeListFilter);
        propertyTypeListFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(mContentFrameLayoutId, propertyTypeListFragment)
                .addToBackStack(null)
                .commit();

        setIsShowPropertyTypeList(true);
    }

    public void setMapLocation() {

        if (mSpinner != null && mReset != null) {
            mReset.setVisible(true);
            mSpinner.setVisibility(View.VISIBLE);
        }

        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mSupportMapFragment == null) {
            mSupportMapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.map_fragment_container, mSupportMapFragment).commit();
        }
        mSupportMapFragment.setRetainInstance(true);
        mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    loadMap(googleMap, DEFAULT_LOCATION);
                } else {
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                    mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(new OnSuccessListener <android.location.Location>() {
                                @Override
                                public void onSuccess(android.location.Location location) {
                                    //Toast.makeText(getApplicationContext(), "CURRENT LOCATION: " + String.valueOf(location.getLatitude()) + " , " + String.valueOf(location.getLongitude()), Toast.LENGTH_LONG).show();
                                    // Log.d(TAG, "CURRENT LOCATION: " + String.valueOf(location.getLatitude()) + " , " + String.valueOf(location.getLongitude()));
                                    CURRENT_LOCATION =
                                            (location != null && CURRENT_LOCATION == null) ? new LatLng(location.getLatitude(), location.getLongitude()) : CURRENT_LOCATION;
                                    //Toast.makeText(getApplicationContext(), "HAVE PERMISSION: " + String.valueOf(CURRENT_LOCATION), Toast.LENGTH_SHORT).show();
                                    loadMap(googleMap, CURRENT_LOCATION);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadMap(googleMap, DEFAULT_LOCATION);
                                }
                            });
                }
            }
        });

        setIsShowMap(true);
    }

    @SuppressLint("MissingPermission")
    public void loadMap(GoogleMap googleMap, LatLng currentLocation) {
        getLocationPermission();

        if( mMap == null ){
            mMap = googleMap;
        }

        if( currentLocation == null ){
            currentLocation = DEFAULT_LOCATION;
        }

        mMap.setMapType(MAP_TYPE_NORMAL);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (marker != null) {
                    MarkerTag tag = (MarkerTag) marker.getTag();
                    LocationEntity locationEntity = tag.getLocationEntity();
                    show(locationEntity);
                }
            }
        });

        mMap.setInfoWindowAdapter(new LocationInfoWindowAdapter(this));

        mLocationListViewModel.getLocations().observe(this, locationEntities -> {
            if (locationEntities != null) {
                for (LocationEntity locationEntity : locationEntities) {
                    LatLng latLng = new LatLng(locationEntity.getLatitude(), locationEntity.getLongitude());
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(latLng).title(locationEntity.getProperty_name())
                            .snippet(locationEntity.getAddress() + " Type: " + locationEntity.getProperty_type())
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(206)));
                    marker.setTag(new MarkerTag(locationEntity));
                    mMapMarkers.add(marker);
                }
            }
        });

        mUiSettings = mMap.getUiSettings();

        mUiSettings.setCompassEnabled(false);
        mUiSettings.setAllGesturesEnabled(true);
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);

        // Log.d(TAG, "CURRENT LOCATION FOR MAP: " + String.valueOf(currentLocation.latitude) + " , " + String.valueOf(currentLocation.longitude));


        mCameraPosition = new CameraPosition.Builder()
                .target(currentLocation)
                .zoom(DEFAULT_ZOOM)
                .bearing(DEFAULT_BEARING)
                .tilt(DEFAULT_TILT)
                .build();

        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CHICAGO.getCenter() , 13));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CURRENT_LOCATION, 13));

        if( CURRENT_COMMUNITY == "Community" ){
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        }

    }

    private String getCurrentCommunity() {
        return CURRENT_COMMUNITY;
    }

    private void setCurrentCommunity(String currentCommunity) {
        CURRENT_COMMUNITY = currentCommunity;
    }

    private void setSelectedCommunity(int selectedCommunity) {
        SELECTED_COMMUNITY_INDEX = selectedCommunity;
    }

    private void setLocationObject(String locationObject) {
        if (mLocationObject != null && locationObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken <Location>() {
            }.getType();
            mLocationObject = gson.fromJson(locationObject, type);
        }
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

        mSpinner.setSelection(0);
    }


    private void moveCameraToDefaultLocation() {
        if (Geocoder.isPresent()) {
            try {
                mCameraPosition = new CameraPosition.Builder()
                        .target(DEFAULT_LOCATION)
                        .bearing(DEFAULT_BEARING)
                        .tilt(DEFAULT_TILT)
                        .zoom(DEFAULT_ZOOM)
                        .build();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
            } catch (Exception e) {
                // handle the exception
            }
        }

    }


    private void moveCameraToCommunity(String community) {
        setCurrentCommunity(community);
        if (Geocoder.isPresent() && !community.equals("Community")) {
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
                    mCameraPosition = new CameraPosition.Builder()
                            .target(CURRENT_LOCATION)
                            .bearing(DEFAULT_BEARING)
                            .tilt(DEFAULT_TILT)
                            .zoom(DEFAULT_ZOOM)
                            .build();

                    if (mMap != null) {
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
                    } else {
                        Toast.makeText(this, "no map", Toast.LENGTH_LONG).show();
                    }

                    if (isTwoPane()) {
                        filterMarkers();
                    }

                } else {
                }

            } catch (IOException e) {
                // handle the exception
                Log.d(TAG , "GEOCODE: " + e.toString());
            }
        } else {

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;

                }
            }
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