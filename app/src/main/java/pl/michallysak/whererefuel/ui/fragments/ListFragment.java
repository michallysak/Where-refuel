package pl.michallysak.whererefuel.ui.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import pl.michallysak.whererefuel.api.GasStation;
import pl.michallysak.whererefuel.R;
import pl.michallysak.whererefuel.other.Tools;
import pl.michallysak.whererefuel.ui.fragments.display.DisplayGasStation;
import pl.michallysak.whererefuel.ui.fragments.adapters.GasStationAdapter;


public class ListFragment extends Fragment implements DisplayGasStation {

    private List<GasStation> gasStationList;

    public ListFragment(List<GasStation> gasStationList) {
        this.gasStationList = gasStationList;
    }

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (gasStationList != null && gasStationList.size() > 0){
            showAll(gasStationList, false);
        }

        return view;
    }


    @Override
    public boolean show(GasStation gasStation) {
        return false;
    }

    @Override
    public boolean showAll(List<GasStation> gasStations, boolean focusLast) {

        Tools.log("Showing list size: " + gasStations.size());
        recyclerView.setAdapter(new GasStationAdapter(getContext(), gasStations, recyclerView, false));
        recyclerView.invalidate();

        return true;
    }

    @Override
    public boolean clear() {
        gasStationList.clear();
        showAll(gasStationList, false);
        return false;
    }


}
