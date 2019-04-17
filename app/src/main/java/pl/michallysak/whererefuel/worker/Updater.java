package pl.michallysak.whererefuel.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import pl.michallysak.whererefuel.api.Api;
import pl.michallysak.whererefuel.api.City;
import pl.michallysak.whererefuel.api.Company;
import pl.michallysak.whererefuel.api.GasStation;
import pl.michallysak.whererefuel.db.cities.Cities;
import pl.michallysak.whererefuel.db.cities.CitiesDatabase;
import pl.michallysak.whererefuel.db.companies.Companies;
import pl.michallysak.whererefuel.db.companies.CompaniesDatabase;
import pl.michallysak.whererefuel.db.favouritegasstation.FavouriteGasStation;
import pl.michallysak.whererefuel.db.favouritegasstation.FavouriteGasStationDatabase;
import pl.michallysak.whererefuel.other.Tools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Updater extends Worker {


    private CompaniesDatabase companiesDatabase;
    private CitiesDatabase citiesDatabase;

    private static final long TIME_REPEAT = 12;
    private static final String FAVOURITES_WORK_ID = "FAVOURITES_WORK_ID";

    private FavouriteGasStationDatabase favouritesDatabase;
    private Api api;

    public Updater(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://michallysak.pl/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(Api.class);

        setupDb();

        if (favouritesDatabase.dao().getSize() > 0){

            StringBuilder sb = new StringBuilder();
            for (FavouriteGasStation favouriteGasStation :favouritesDatabase.dao().getAll()) {
                sb.append(favouriteGasStation.getId()).append(",");
            }
            sb.deleteCharAt(sb.length()-1);
            enqueueGasStationRequest(sb.toString());
        }

        enqueueCitiesRequest();
        enqueueCompaniesRequest();


        return Result.success();

    }

    private void setupDb() {

        favouritesDatabase = Room.databaseBuilder(getApplicationContext(), FavouriteGasStationDatabase.class, "favouritesGasStationDatabase")
                .allowMainThreadQueries()
                .build();

        companiesDatabase = Room.databaseBuilder(getApplicationContext(), CompaniesDatabase.class, "companiesDatabase")
                .allowMainThreadQueries()
                .build();

        citiesDatabase = Room.databaseBuilder(getApplicationContext(), CitiesDatabase.class, "citiesDatabase")
                .allowMainThreadQueries()
                .build();

    }


    private void enqueueGasStationRequest(String ids) {

        Call<List<GasStation>> call = api.getGasStationByIds(ids);
        Tools.log(call.request().toString());

        call.enqueue(new Callback<List<GasStation>>() {
            @Override
            public void onResponse(@NonNull Call<List<GasStation>> call, @NonNull Response<List<GasStation>> response) {

                if (response.isSuccessful()) {

                    List<GasStation> gasStations = response.body();

                    if (gasStations != null) {

                        List<FavouriteGasStation> favouriteGasStations = favouritesDatabase.dao().getAll();
                        List<Integer> ids = new ArrayList<>();

                        for(GasStation gasStation: gasStations){
                            try {
                                ids.add(gasStation.getId());
                                favouritesDatabase.dao().update(FavouriteGasStation.generateFavouritesGasStation(gasStation));
                            }catch (Exception e){
                                Tools.log(e.getMessage());
                            }

                        }


                        for(FavouriteGasStation gasStation: favouriteGasStations){
                            if (!ids.contains(gasStation.getId())){
                                try {
                                    favouritesDatabase.dao().delete(gasStation);
                                }catch (Exception e){
                                    Tools.log(e.getMessage());
                                }
                            }
                        }


                        Tools.log("We updated favouriteStation");
                    }


                } else {
                    Tools.log("We have problem with updated FavouriteGasStation: " + response.code());
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<GasStation>> call, @NonNull Throwable t) {
                Tools.log( "We can't updated new FavouriteGasStation\nCheck internet connections");
            }
        });
    }


    private void enqueueCitiesRequest() {

        Call<List<City>> call = api.getCities();

        call.enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(@NonNull Call<List<City>> call, @NonNull Response<List<City>> response) {
                if (response.isSuccessful()) {
                    try {
                        if (response.body() != null) {

                            List<Cities> responseCities = new ArrayList<>();
                            for(City city: response.body()){
                                responseCities.add(new Cities(city.getName()));
                            }

                            List<Cities> databaseCities = citiesDatabase.dao().getAll();

                            if (!databaseCities.equals(responseCities)){
                                citiesDatabase.dao().deleteAll(databaseCities);
                                citiesDatabase.dao().insertAll(responseCities);
                            }

                            Tools.log("We updated CitiesDatabase");


                        }


                    } catch (Exception e) {
                        Tools.log("We have problem with get new Cities " + e.getMessage());
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<City>> call, @NonNull Throwable t) {
                Tools.log("We can't get new Cities" + t.getMessage());
            }

        });
    }

    private void enqueueCompaniesRequest() {

        Call<List<Company>> call = api.getCompanies();

        call.enqueue(new Callback<List<Company>>() {
            @Override
            public void onResponse(@NonNull Call<List<Company>> call, @NonNull Response<List<Company>> response) {
                if (response.isSuccessful()) {
                    try {
                        if (response.body() != null) {

                            List<Companies> responseCompanies = new ArrayList<>();
                            for (Company company : response.body()) {
                                responseCompanies.add(new Companies(company.getName()));
                            }

                            List<Companies> databaseCompanies = companiesDatabase.dao().getAll();

                            if (!databaseCompanies.equals(responseCompanies)) {
                                companiesDatabase.dao().deleteAll(databaseCompanies);
                                companiesDatabase.dao().insertAll(responseCompanies);
                            }

                            Tools.log("We updated CompaniesDatabase");
                        }

                    } catch (Exception e) {
                        Tools.log("We have problem with get new Companies " + e.getMessage());
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Company>> call, @NonNull Throwable t) {
                Tools.log("We can't get new Companies" + t.getMessage());
            }
        });
    }

    static public void startUpdater(){

        Constraints newsConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(Updater.class, TIME_REPEAT, TimeUnit.HOURS)
                .setConstraints(newsConstraints)
                .build();

        WorkManager.getInstance().enqueueUniquePeriodicWork(FAVOURITES_WORK_ID, ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);

    }

}
