package pl.michallysak.whererefuel.ui.fragments.display;

import java.util.List;

import pl.michallysak.whererefuel.api.GasStation;

public interface DisplayGasStation {

    boolean show(GasStation gasStation);

    boolean showAll(List<GasStation> gasStations, boolean focusLast);

    boolean clear();

}
