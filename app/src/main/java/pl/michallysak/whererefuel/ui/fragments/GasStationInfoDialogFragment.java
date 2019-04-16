package pl.michallysak.whererefuel.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.room.Room;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import pl.michallysak.whererefuel.R;
import pl.michallysak.whererefuel.api.GasStation;
import pl.michallysak.whererefuel.db.favouritegasstation.FavouriteGasStation;
import pl.michallysak.whererefuel.db.favouritegasstation.FavouriteGasStationDatabase;
import pl.michallysak.whererefuel.other.Tools;
import pl.michallysak.whererefuel.ui.MainActivity;

public class GasStationInfoDialogFragment extends DialogFragment {

    private GasStation gasStation;
    private Context context;
    private boolean favourite;

    private FavouriteGasStationDatabase favouritesDatabase;

    public GasStationInfoDialogFragment(GasStation gasStation, Context context) {
        this.gasStation = gasStation;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gas_station_info_dialog, container, false);

        favouritesDatabase = Room.databaseBuilder(getContext(), FavouriteGasStationDatabase.class, "favouritesGasStationDatabase")
                .allowMainThreadQueries()
                .build();
        FavouriteGasStation favouriteGasStation = favouritesDatabase.dao().get(gasStation.getId());
        favourite = favouriteGasStation != null;

        if (Tools.getTheme(context).equals("dark"))
            view.setBackgroundColor(context.getResources().getColor(R.color.gray));

        TextView name = view.findViewById(R.id.gas_station_name);
        name.setText(gasStation.getName());

        final TextView company = view.findViewById(R.id.gas_station_company);
        company.setText(gasStation.getCompany());

        TextView address = view.findViewById(R.id.gas_station_adress);
        address.setText(String.format("%s, %s", gasStation.getCity(), gasStation.getAddress()));


        TextView distance = view.findViewById(R.id.gas_station_distance);
        distance.setText(Tools.getFriendlyDistance(gasStation.getDistance()));


        TextView e95 = view.findViewById(R.id.gas_station_e95);
        e95.setText(gasStation.getReadyE95(getContext()));

        TextView e98 = view.findViewById(R.id.gas_station_e98);
        e98.setText(gasStation.getReadyE98(getContext()));

        TextView on = view.findViewById(R.id.gas_station_on);
        on.setText(gasStation.getReadyOn(getContext()));

        TextView lpg = view.findViewById(R.id.gas_station_lpg);
        lpg.setText(gasStation.getReadyLpg(getContext()));


        TextView lastUpdate = view.findViewById(R.id.gas_station_lastUpdate);
        lastUpdate.setText(gasStation.getLastUpdate());


        ImageView reportBtn = view.findViewById(R.id.report_btn);
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                ((MainActivity) context).showFragment(new ReportStationFragment(gasStation));
            }
        });

        reportBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Tools.toast(context, context.getString(R.string.report_gas_station));
                return false;
            }
        });


        ImageView editBtn = view.findViewById(R.id.gas_station_edit_btn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                ((MainActivity) context).showFragment(new UpdateFuelPriceFragment(gasStation));
            }
        });

        editBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Tools.toast(context, context.getString(R.string.edit_gas_station));
                return false;
            }
        });


        ImageView showOnMapBtn = view.findViewById(R.id.gas_station_show_on_map_btn);
        showOnMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (((MainActivity) context).getCurrentFragment().getClass().equals(ListFragment.class)
                        || ((MainActivity) context).getCurrentFragment().getClass().equals(FavouritesFragment.class)) {

                    LatLng latLng = ((MainActivity) context).getCurrentLocation();
                    List<GasStation> gasStations = ((MainActivity) context).getGasStations();

                    if (((MainActivity) context).getCurrentFragment().getClass().equals(FavouritesFragment.class)){
                       gasStations.add(gasStation);
                    }

                    ((MainActivity) context).showFragment(new MapFragment(latLng, gasStations));


                    final Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (!((MapFragment) ((MainActivity) context).getCurrentFragment()).show(gasStation))
                                handler.postDelayed(this, 100);
                        }
                    };
                    handler.postDelayed(runnable, 100);


                }

            }
        });

        showOnMapBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Tools.toast(context, context.getString(R.string.show_on_map));
                return false;
            }
        });


        ImageView navigateBtn = view.findViewById(R.id.gas_station_navigate_btn);
        navigateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tools.toast(getContext(), getString(R.string.open_google_maps_to_navigate));
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + gasStation.getLat() + "," + gasStation.getLng());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        navigateBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Tools.toast(context, context.getString(R.string.navigate));
                return false;
            }
        });


        final ImageView favouriteStar = view.findViewById(R.id.gas_station_favourite_btn);
        if (favourite) {
            favouriteStar.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_blue));
        } else {
            favouriteStar.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_border_blue));
        }

        favouriteStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (favourite) {
                    removeFromFavourite();
                    Toast.makeText(getContext(), R.string.remove_from_favourites, Toast.LENGTH_LONG).show();
                    favouriteStar.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_border_blue));
                    favourite = false;
                } else {
                    addToFavourite();
                    Toast.makeText(getContext(), R.string.add_to_favourites, Toast.LENGTH_LONG).show();
                    favouriteStar.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_blue));
                    favourite = true;
                }

            }
        });
        favouriteStar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (favourite) {
                    Tools.toast(context, context.getString(R.string.click_to) + " " + context.getString(R.string.remove_to_favourites));
                } else {
                    Tools.toast(context, context.getString(R.string.click_to) + " " + context.getString(R.string.add_to_favourites));
                }
                return false;
            }
        });


        ImageView shareBtn = view.findViewById(R.id.gas_station_share);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, gasStation.sharaData(context));
                intent.setType("text/plain");
                startActivity(intent);
                dismiss();
            }
        });

        shareBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Tools.toast(context, context.getString(R.string.share));
                return false;
            }
        });


        return view;
    }

    private void addToFavourite() {
        try {
            favouritesDatabase.dao().insert(FavouriteGasStation.generateFavouritesGasStation(gasStation));
        } catch (Exception e) {
            Tools.log("Add " + e.getMessage());
        }
    }

    private void removeFromFavourite() {
        try {
            favouritesDatabase.dao().delete(FavouriteGasStation.generateFavouritesGasStation(gasStation));
        } catch (Exception e) {
            Tools.log("Add " + e.getMessage());
        }
    }


}
