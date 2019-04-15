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

import java.util.List;
import java.util.concurrent.TimeUnit;

import pl.michallysak.whererefuel.api.Api;
import pl.michallysak.whererefuel.api.GasStation;
import pl.michallysak.whererefuel.db.favouritegasstation.FavouriteGasStation;
import pl.michallysak.whererefuel.db.favouritegasstation.FavouriteGasStationDatabase;
import pl.michallysak.whererefuel.other.Tools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FavouritesWorker extends Worker {


    private static final long TIME_REPEAT = 12;
    private static final String FAVOURITES_WORK_ID = "FAVOURITES_WORK_ID";

    private FavouriteGasStationDatabase favouritesDatabase;
    private Api api;

    public FavouritesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        favouritesDatabase = Room.databaseBuilder(getApplicationContext(), FavouriteGasStationDatabase.class, "favouritesGasStationDatabase")
                .allowMainThreadQueries()
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://michallysak.pl/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(Api.class);

        if (favouritesDatabase.dao().getSize() > 0){

            StringBuilder sb = new StringBuilder();
            for (FavouriteGasStation favouriteGasStation :favouritesDatabase.dao().getAll()) {
                sb.append(favouriteGasStation.getId());
            }
            enqueueGasStationRequest(sb.toString());
        }


        return Result.success();

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
                        Tools.log("We updated favourite " + gasStations.size() + " station");
                        for(GasStation gasStation: gasStations){
                            favouritesDatabase.dao().update(FavouriteGasStation.generateFavouritesGasStation(gasStation));
                        }
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

    static public void startFavouritesWork(){

        Constraints newsConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(FavouritesWorker.class, TIME_REPEAT, TimeUnit.HOURS)
                .setConstraints(newsConstraints)
                .build();

        WorkManager.getInstance().enqueueUniquePeriodicWork(FAVOURITES_WORK_ID, ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);

    }

}
