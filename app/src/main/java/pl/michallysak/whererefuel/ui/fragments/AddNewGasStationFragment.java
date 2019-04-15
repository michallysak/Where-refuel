package pl.michallysak.whererefuel.ui.fragments;


import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import pl.michallysak.whererefuel.R;
import pl.michallysak.whererefuel.api.Api;
import pl.michallysak.whererefuel.api.Result;
import pl.michallysak.whererefuel.db.cities.Cities;
import pl.michallysak.whererefuel.db.cities.CitiesDatabase;
import pl.michallysak.whererefuel.db.companies.Companies;
import pl.michallysak.whererefuel.db.companies.CompaniesDatabase;
import pl.michallysak.whererefuel.other.Tools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AddNewGasStationFragment extends Fragment {

    private EditText editName;
    private AutoCompleteTextView editCompany;
    private AutoCompleteTextView editCity;
    private EditText editAddress;
    private Button addButton;

    private String name;
    private String company;
    private String city;
    private String address;

    private String[] companies;
    private String[] cities;

    private LatLng currentLocation;

    private Api api;

    public AddNewGasStationFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_new_station, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_add_new_gas_station);
        Tools.prepareToolbar(getContext(), toolbar, false);

        editName = view.findViewById(R.id.add_gas_station_name);
        editCompany = view.findViewById(R.id.add_gas_station_company);
        editCity = view.findViewById(R.id.add_gas_station_city);
        editAddress = view.findViewById(R.id.add_gas_station_address);

        setupDb();

        ArrayAdapter<String> companiesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, companies);
        ArrayAdapter<String> citiesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, cities);

        editCity.setAdapter(citiesAdapter);
        editCompany.setAdapter(companiesAdapter);

        addButton = view.findViewById(R.id.add_station_btn);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddNewClicked();
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://michallysak.pl/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(Api.class);

        return view;
    }

    private void setupDb() {

        CitiesDatabase citiesDatabase = Room.databaseBuilder(getContext(), CitiesDatabase.class, "citiesDatabase")
                .allowMainThreadQueries()
                .build();

        List<Cities> citiesTemp = citiesDatabase.dao().getAll();
        cities = new String[citiesTemp.size()];

        for (int i = 0; i < citiesTemp.size(); i++) {
            cities[i] = citiesTemp.get(i).getName();
        }



        CompaniesDatabase companiesDatabase = Room.databaseBuilder(getContext(), CompaniesDatabase.class, "companiesDatabase")
                .allowMainThreadQueries()
                .build();

        List<Companies> companiesTemp = companiesDatabase.dao().getAll();
        companies = new String[companiesTemp.size()];

        for (int i = 0; i < companiesTemp.size(); i++) {
            companies[i] = companiesTemp.get(i).getName();
        }


    }



    private boolean isValid() {

        editName.setError(null);
        editCompany.setError(null);
        editCity.setError(null);
        editAddress.setError(null);

        boolean valid = true;

        name = editName.getText().toString();
        if (name.length() < 1) {
            editName.setError(getString(R.string.enter_valid) + " " + getString(R.string.name));
            valid = false;
        }

        company = editCompany.getText().toString();
        if (company.length() < 1) {
            editCompany.setError(getString(R.string.enter_valid) + " " + getString(R.string.company));
            valid = false;
        }

        city = editCity.getText().toString();
        if (city.length() < 1) {
            editCity.setError(getString(R.string.enter_valid) + " " + getString(R.string.city));
            valid = false;
        }

        address = editAddress.getText().toString();
        if (address.length() < 1) {
            editAddress.setError(getString(R.string.enter_valid) + " " + getString(R.string.address));
            valid = false;
        }

        return valid;
    }


    private void onAddNewClicked() {
        if (isValid()) {
            addButton.setClickable(false);

            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

            fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {


                        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                        enqueueAddNewStation();

                        Tools.toast(getContext(), "We find you");
                    }
                }
            });
            fusedLocationClient.getLastLocation().addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Tools.toast(getContext(), "We can't find you");
                    addButton.setClickable(true);
                }
            });

        }


    }


    private void enqueueAddNewStation(){

        Call<List<Result>> call = api.addStation(currentLocation.latitude, currentLocation.longitude, name, company, city, address);
        call.enqueue(new Callback<List<Result>>() {
            @Override
            public void onResponse(@NonNull Call<List<Result>> call, @NonNull Response<List<Result>> response) {
                Tools.toast(getContext(), getString(R.string.success_add_station));
                addButton.setClickable(true);
                editName.setText(null);
                editCompany.setText(null);
                editCity.setText(null);
                editAddress.setText(null);
            }

            @Override
            public void onFailure(@NonNull Call<List<Result>> call, @NonNull Throwable t) {
                Tools.toast(getContext(), getString(R.string.error_add_station));
                addButton.setClickable(true);
            }
        });



    }


}
