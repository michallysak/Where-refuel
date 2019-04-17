package pl.michallysak.whererefuel.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Location;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import pl.michallysak.whererefuel.R;
import pl.michallysak.whererefuel.api.Api;
import pl.michallysak.whererefuel.api.GasStation;
import pl.michallysak.whererefuel.db.cities.Cities;
import pl.michallysak.whererefuel.db.cities.CitiesDatabase;
import pl.michallysak.whererefuel.db.companies.Companies;
import pl.michallysak.whererefuel.db.companies.CompaniesDatabase;
import pl.michallysak.whererefuel.other.CitySuggestion;
import pl.michallysak.whererefuel.other.PreferenceHelper;
import pl.michallysak.whererefuel.other.Tools;
import pl.michallysak.whererefuel.ui.fragments.AboutFragment;
import pl.michallysak.whererefuel.ui.fragments.AddNewGasStationFragment;
import pl.michallysak.whererefuel.ui.fragments.FavouritesFragment;
import pl.michallysak.whererefuel.ui.fragments.ListFragment;
import pl.michallysak.whererefuel.ui.fragments.MapFragment;
import pl.michallysak.whererefuel.ui.fragments.PermissionFragment;
import pl.michallysak.whererefuel.ui.fragments.SettingsFragment;
import pl.michallysak.whererefuel.ui.fragments.SortFragmentDialog;
import pl.michallysak.whererefuel.worker.Updater;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.android.material.snackbar.Snackbar.LENGTH_INDEFINITE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int SPEECH_REQUEST_CODE = 0;

    private String sort;
    private String fuel;
    private int radius;
    private String company;

    private FloatingSearchView searchView;
    private FloatingActionButton fab;
    private DrawerLayout drawer;

    private Fragment currentFragment;
    private LatLng currentLocation;

    private List<GasStation> gasStations;

    private Api api;
    private Activity activity = this;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    public String lastQuery = "";
    public Snackbar snackbar;

    private long backPressedTime;

    private PreferenceHelper sharedPreferences;


    public List<GasStation> getGasStations() {
        return gasStations;
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    public LatLng getCurrentLocation() {
        return currentLocation;
    }

    @Override
    protected void onResume() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */);
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Updater.startUpdater();

        setupPreferences();

        //setup api
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://michallysak.pl/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(Api.class);

        setupLocation();


        if (Tools.getTheme(this).equals("light")) {
            setTheme(R.style.AppThemeLigth);
        } else {
            setTheme(R.style.AppThemeDark);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentFragment = getDefualtFragment();

        //setupUI
        setupSearchView();
        setupFab();

        gasStations = new ArrayList<>();


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            showFragment(new PermissionFragment());

        } else if (sharedPreferences.getBoolean("current_location_when_started", true)) {
            showFragment(getDefualtFragment());
            getLastLocation();
        } else
            showFragment(getDefualtFragment());

    }

//    LOCATION

    private void getLastLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    sharedPreferences.putFloat("lat", (float) location.getLatitude());
                    sharedPreferences.putFloat("lng", (float) location.getLongitude());

                    searchNearbyGasStation("");
                    Tools.log("New location: " + currentLocation.latitude + " " + currentLocation.longitude);

                }
            }
        });

    }

    private void setupLocation() {


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Tools.log("UPDATE LOCATION " + location.getLatitude() + ", " + location.getLongitude());
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                }
            }
        };


    }


//    API

    public void searchNearbyGasStation(final String cityName) {


        double lat = currentLocation.latitude;
        double lng = currentLocation.longitude;

        sharedPreferences.putFloat("lat", (float) lat);
        sharedPreferences.putFloat("lng", (float) lng);

        Call<List<GasStation>> call;
        if (cityName.equals("")) {
            call = api.getNearbyGasStation(lat, lng, radius, sort, fuel, company);
            lastQuery = "";
        } else {
            call = api.getNearbyCityGasStation(lat, lng, 2000000000, sort, fuel, company, cityName);
            lastQuery = cityName;
        }
        enqueueGasStationRequest(call);

    }

    private void enqueueGasStationRequest(Call<List<GasStation>> call) {
        Tools.log(call.request().toString());
        call.enqueue(new Callback<List<GasStation>>() {
            @Override
            public void onResponse(@NonNull Call<List<GasStation>> call, @NonNull Response<List<GasStation>> response) {

                if (response.isSuccessful()) {

                    searchView.setSearchText(lastQuery);

                    gasStations = response.body();

                    if (gasStations != null) {
                        Tools.log("We download " + gasStations.size() + " station");
                        showRequestResult();
                        hideSnackBar();
                    }


                } else {
                    createSnackbar(
                            getString(R.string.error_download_problem) + " " + getString(R.string.gas_station_grammar) + " \n" + getString(R.string.check_internet_connections),
                            LENGTH_INDEFINITE);

                    snackbar.setAction(R.string.retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            searchNearbyGasStation(lastQuery);
                        }
                    });

                    snackbar.show();
                    Tools.log("We have problem with get new GasStation: " + response.code());
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<GasStation>> call, @NonNull Throwable t) {
                createSnackbar(
                        getString(R.string.error_download_problem) + " " + getString(R.string.gas_station_grammar) + " \n" + getString(R.string.check_internet_connections),
                        LENGTH_INDEFINITE);

                snackbar.setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchNearbyGasStation(lastQuery);
                    }
                });

                snackbar.show();
                Tools.log("We can't download new gasStation. Check internet connections: " + t.getMessage());
                if (t.getMessage().equals("Unable to resolve host \"michallysak.pl\": No address associated with hostname"))
                    Tools.toast(getApplicationContext(), "Check DNS configuration");
            }
        });
    }


    private void showRequestResult() {

        Toast.makeText(this, getString(R.string.we_find) + " " + gasStations.size() + " " + getString(R.string.gas_station_grammar), Toast.LENGTH_LONG).show();

        if (currentFragment.getClass().equals(MapFragment.class)) {
            ((MapFragment) currentFragment).showAll(gasStations, true);
        } else if (currentFragment.getClass().equals(ListFragment.class)) {
            ((ListFragment) currentFragment).showAll(gasStations, false);
        }
    }

    //    DB



    //    PREFERENCES
    public void setupPreferences() {
        sharedPreferences = new PreferenceHelper(this);
        sort = sharedPreferences.getString("sort", "price");
        fuel = sharedPreferences.getString("fuel", "E95");
        radius = sharedPreferences.getInt("radius", 15);
        company = sharedPreferences.getString("company", "all");

        double lat = sharedPreferences.getFloat("lat", 52.0881023f);
        double lng = sharedPreferences.getFloat("lng", 19.4048991f);
        currentLocation = new LatLng(lat, lng);

    }


    //    VIEW

    private void createSnackbar(String message, int duration) {
        snackbar = Snackbar.make(fab, message, duration);
        if (Tools.getTheme(this).equals("light")) {
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.white));
            TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(getResources().getColor(R.color.black));
        } else {
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.gray_light));
            TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void hideSnackBar() {
        try {
            snackbar.dismiss();
        } catch (Exception e) {
            Tools.log(e.getMessage());
        }
    }

    private void setSearchText(String query) {
        if (query.equals("")) {
            searchView.setSearchText(getString(R.string.current_location));
        } else {
            searchView.setSearchText(query);
        }
    }

    private void setupSearchView() {

        searchView = findViewById(R.id.floating_search_view);

        searchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                searchView.hideProgress();
            }

            @Override
            public void onFocusCleared() {
                searchView.hideProgress();
            }
        });


        CitiesDatabase citiesDatabase = Room.databaseBuilder(this, CitiesDatabase.class, "citiesDatabase")
                .allowMainThreadQueries()
                .build();

        final List<String> cities = new ArrayList<>();

        for(Cities c: citiesDatabase.dao().getAll()){
            cities.add(c.getName());
        }

        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    searchView.clearSuggestions();
                } else {

                    searchView.showProgress();

                    List<CitySuggestion> suggestions = CitySuggestion.getSuggestion(newQuery, cities, 5);

                    if (suggestions.size() > 0) {
                        searchView.swapSuggestions(suggestions);
                    } else {
                        searchView.clearSuggestions();
                    }

                    searchView.hideProgress();

                }
            }
        });

        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

                lastQuery = searchSuggestion.getBody();
                searchNearbyGasStation(lastQuery);

                setSearchText(lastQuery);

                Tools.hideKeyboard(activity);
            }

            @Override
            public void onSearchAction(String currentQuery) {

                if (!lastQuery.equals(currentQuery)) {
                    lastQuery = currentQuery;
                    Tools.toast(getApplicationContext(), currentQuery);
                    searchView.hideProgress();
                    if (currentQuery.equals(getString(R.string.current_location))) {
                        lastQuery = "";
                        searchNearbyGasStation("");
                    } else {
                        searchNearbyGasStation(currentQuery);
                        setSearchText(lastQuery);
                    }
                    Tools.hideKeyboard(activity);

                }

            }
        });

        searchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, SearchSuggestion item, int itemPosition) {
                leftIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_city_blue));
            }

        });


        drawer = findViewById(R.id.drawer_layout);
        searchView.attachNavigationDrawerToMenuButton(drawer);
        searchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                int id = item.getItemId();

                switch (id) {
                    case R.id.action_speak:
                        displaySpeechRecognizer();
                        break;
                    case R.id.action_current_location:
                        searchNearbyGasStation("");
                        break;
                    case R.id.action_sort:
                        hideSnackBar();
                        SortFragmentDialog gasStationInfoDialogFragment = new SortFragmentDialog();
                        FragmentManager fm = getSupportFragmentManager();
                        gasStationInfoDialogFragment.show(fm, "gas_station_info_dialog");
                        break;

                }


            }

        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ColorStateList color;

        if (Tools.getTheme(this).equals("dark"))
            color = ColorStateList.valueOf(getResources().getColor(R.color.white));
        else
            color = ColorStateList.valueOf(getResources().getColor(R.color.gray));

        navigationView.setItemTextColor(color);
        navigationView.setItemIconTintList(color);
    }

    private void setupFab() {
        fab = findViewById(R.id.fab);
        changeFabIcon(currentFragment);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentFragment.getClass().equals(MapFragment.class)) {
                    currentFragment = new ListFragment(gasStations);
                } else {
                    currentFragment = new MapFragment(currentLocation, gasStations);
                }

                changeFabIcon(currentFragment);

                showFragment(currentFragment);
                searchView.bringToFront();
            }
        });
    }

    public void setFabVisibility(boolean visibility) {
        if (visibility)
            fab.show();
        else
            fab.hide();
    }

    private void changeFabIcon(Fragment requestFragment) {

//        because of problems with 28.0.0 library
        fab.hide();
//

        if (requestFragment.getClass().equals(MapFragment.class)) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
        } else {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_map));
        }

//        because of problems with 28.0.0 library
        fab.show();
//
    }


    public void showFragment(Fragment fragment) {

        currentFragment = fragment;

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                .addToBackStack(null)
                .replace(R.id.container, fragment)
                .commit();


        Tools.hideKeyboard(this);

        showHomeElements(isHomeDisplayed());
    }


    private void showHomeElements(boolean show) {
        if (show) {
            fab.show();
            searchView.bringToFront();
            searchView.setVisibility(View.VISIBLE);
        } else {
            hideSnackBar();
            fab.hide();
            searchView.setVisibility(View.GONE);
        }
    }

    public Fragment getDefualtFragment() {
        if (sharedPreferences.getString("home", "map").equals("map")) {
            return new MapFragment(currentLocation, gasStations);
        } else {
            return new ListFragment(gasStations);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Fragment requestFragment = null;

        switch (menuItem.getItemId()) {
            case R.id.action_home:
                requestFragment = getDefualtFragment();
                break;
            case R.id.action_favourites:
                requestFragment = new FavouritesFragment();
                break;
            case R.id.action_add_new_gas_station:
                requestFragment = new AddNewGasStationFragment();
                break;
            case R.id.action_settings:
                requestFragment = new SettingsFragment();
                break;
            case R.id.action_about:
                requestFragment = new AboutFragment();
                break;
        }

        if (requestFragment != null && !requestFragment.getClass().equals(currentFragment.getClass()))
            showFragment(requestFragment);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public boolean isHomeDisplayed() {
        return currentFragment.getClass().equals(MapFragment.class)
                || currentFragment.getClass().equals(ListFragment.class)
                || currentFragment.getClass().equals(SupportMapFragment.class);
    }

    @Override
    public void onBackPressed() {
        hideSnackBar();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {


            if (isHomeDisplayed()) {


                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    finish();
                    return;
                } else {
                    Toast.makeText(this, R.string.click_again_to_exit, Toast.LENGTH_SHORT).show();
                }
                backPressedTime = System.currentTimeMillis();


            } else {


//                super.onBackPressed();

                FragmentManager fragmentManager = getSupportFragmentManager();
                List<Fragment> temp = fragmentManager.getFragments();

                currentFragment = temp.get(temp.size() - 2);

                if (isHomeDisplayed()) {
                    showFragment(getDefualtFragment());
                    showHomeElements(true);
                }else
                    super.onBackPressed();

            }


        }
    }


    public void resetActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
        currentFragment = getDefualtFragment();
    }

    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            java.util.List results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = String.valueOf(results.get(0));
            searchView.setSearchText(spokenText);
            searchNearbyGasStation(spokenText);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
