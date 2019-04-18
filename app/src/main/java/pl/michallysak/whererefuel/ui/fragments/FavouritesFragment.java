package pl.michallysak.whererefuel.ui.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import pl.michallysak.whererefuel.R;
import pl.michallysak.whererefuel.api.GasStation;
import pl.michallysak.whererefuel.db.favouritegasstation.FavouriteGasStation;
import pl.michallysak.whererefuel.db.favouritegasstation.FavouriteGasStationDatabase;
import pl.michallysak.whererefuel.other.Tools;
import pl.michallysak.whererefuel.ui.MainActivity;
import pl.michallysak.whererefuel.ui.fragments.display.DisplayGasStation;
import pl.michallysak.whererefuel.ui.fragments.adapters.GasStationAdapter;


public class FavouritesFragment extends Fragment implements DisplayGasStation {

    private List<GasStation> gasStationList;

    public FavouritesFragment() {
    }

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);


        Toolbar toolbar = view.findViewById(R.id.toolbar_favourite);
        Tools.prepareToolbar(getContext(), toolbar, false);

        recyclerView = view.findViewById(R.id.recyclerView_favourite);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        gasStationList = new ArrayList<>();
        setupDb();
        showAll(gasStationList, false);

        return view;
    }

    private void setupDb(){
        FavouriteGasStationDatabase favouritesDatabase = Room.databaseBuilder(getContext(), FavouriteGasStationDatabase.class, "favouritesGasStationDatabase")
                .allowMainThreadQueries()
                .build();

        LatLng latLng = ((MainActivity)getContext()).getCurrentLocation();

        if (favouritesDatabase.dao().getSize() > 0){
            for (FavouriteGasStation favouriteGasStation: favouritesDatabase.dao().getAll()){
                GasStation gasStation = favouriteGasStation.generateGasStation();
                gasStation.setDistance(Tools.getDistance(gasStation.getLat(), gasStation.getLng(), latLng.latitude, latLng.longitude));

                gasStationList.add(gasStation);
            }
        }else {
            new AlertDialog.Builder(getContext())
                    .setCancelable(false)
                    .setTitle(getString(R.string.oops))
                    .setMessage(getString(R.string.empty_favourites))
                    .setPositiveButton(getString(R.string.go_add_to_favourites), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try{
                                ((MainActivity)getContext()).onBackPressed();
//                                Fragment fragment = ((MainActivity)getContext()).getDefualtFragment();
//                                ((MainActivity)getContext()).showFragment(fragment);
                            }catch (Exception e){
                                Tools.log(e.getMessage());
                            }

                        }
                    }).create().show();
        }
    }

    @Override
    public boolean show(GasStation gasStation) {
        return false;
    }

    @Override
    public boolean showAll(List<GasStation> gasStations, boolean focusLast) {

        Tools.log("Showing favourites size: " + gasStations.size());
        recyclerView.setAdapter(new GasStationAdapter(getContext(), gasStations, recyclerView, true));
        recyclerView.invalidate();

        return true;
    }

    @Override
    public boolean clear() {
        return false;
    }
}
