package pl.michallysak.whererefuel.ui.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import pl.michallysak.whererefuel.api.GasStation;
import pl.michallysak.whererefuel.R;
import pl.michallysak.whererefuel.other.PreferenceHelper;
import pl.michallysak.whererefuel.other.Tools;
import pl.michallysak.whererefuel.ui.MainActivity;
import pl.michallysak.whererefuel.ui.fragments.display.DisplayGasStation;


public class MapFragment extends Fragment implements OnMapReadyCallback, DisplayGasStation, GoogleMap.OnMarkerClickListener {

    private List<GasStation> gasStationList;
    private GoogleMap map;
    private boolean forceDark;
    private LatLng currentLocation;

    public MapFragment(LatLng currentLocation, List<GasStation> gasStationList) {
        this.currentLocation = currentLocation;
        this.gasStationList = gasStationList;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        forceDark = Tools.getTheme(getContext()).equals("dark") && !new PreferenceHelper(getContext()).getBoolean("force_daily_map", false);

        View v = inflater.inflate(R.layout.fragment_map, container, false);

        try {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            SupportMapFragment sMap = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, sMap).commit();
            sMap.getMapAsync(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        if (forceDark) {
            try {
                map.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.night_map));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setMyLocationEnabled(true);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f));
        map.setOnMarkerClickListener(this);

        if (gasStationList != null && gasStationList.size() > 0) {
            showAll(gasStationList, false);
        }

    }



    @Override
    public boolean show(GasStation gasStation) {

        try {

            map.setMyLocationEnabled(true);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f));

            LatLng latLng = new LatLng(gasStation.getLat(), gasStation.getLng());
            Tools.log("Show " + "on map (" + gasStation.getName()+ ")");

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean showAll(List<GasStation> gasStations, boolean focusLast) {
        gasStationList = gasStations;
        Tools.log( "Show marker on map " + gasStations.size());

        try {
            clear();
        }catch (Exception e){
            Tools.log(e.getMessage());
        }


        for (GasStation g : gasStations) {
            try {

                Marker marker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(g.getLat(), g.getLng())));
                marker.setTag(g.getId());
                marker.setTitle(g.getName());

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }


        if (gasStations.size() > 0 && focusLast) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gasStations.get(0).getLat(), gasStations.get(0).getLng()), 12f));
        }

        return true;
    }

    @Override
    public boolean clear() {
        Tools.log("clear map");
        map.clear();
        map.setMyLocationEnabled(true);
        return true;
    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        for (GasStation gasStation : gasStationList) {
            if ((int) marker.getTag() == gasStation.getId()) {
                GasStationInfoDialogFragment gasStationInfoDialogFragment = new GasStationInfoDialogFragment(gasStation, getContext());
                FragmentManager fm = ((MainActivity) getContext()).getSupportFragmentManager();
                gasStationInfoDialogFragment.show(fm, "gas_station_info_dialog");
                return true;
            }
        }

        return false;
    }
}
