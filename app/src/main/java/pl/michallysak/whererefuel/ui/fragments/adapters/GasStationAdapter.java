package pl.michallysak.whererefuel.ui.fragments.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.michallysak.whererefuel.R;
import pl.michallysak.whererefuel.api.GasStation;
import pl.michallysak.whererefuel.other.PreferenceHelper;
import pl.michallysak.whererefuel.other.Tools;
import pl.michallysak.whererefuel.ui.MainActivity;
import pl.michallysak.whererefuel.ui.fragments.GasStationInfoDialogFragment;


public class GasStationAdapter extends RecyclerView.Adapter {

    private List<GasStation> gasStations;
    private RecyclerView recyclerView;
    private Context context;
    private boolean showAllFuels;

    private class ViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout container;

        TextView name;
        TextView company;
        TextView distance;
        TextView price;
        TextView lastUpdate;


        ViewHolder(View pItem) {
            super(pItem);
            name = pItem.findViewById(R.id.rv_gas_station_name);
            company = pItem.findViewById(R.id.rv_gas_station_company);
            distance = pItem.findViewById(R.id.rv_gas_station_distance);
            price = pItem.findViewById(R.id.rv_gas_station_price);
            lastUpdate = pItem.findViewById(R.id.rv_gas_station_lastUpdate);

            container = pItem.findViewById(R.id.rv_gas_station_container);
            if (Tools.getTheme(context).equals("dark"))
                container.setBackgroundColor(context.getResources().getColor(R.color.gray));
        }
    }

    public GasStationAdapter(final Context context, List<GasStation> gasStations, RecyclerView recyclerView, final boolean showAllFuels) {
        this.context = context;
        this.gasStations = gasStations;
        this.recyclerView = recyclerView;
        this.showAllFuels = showAllFuels;


        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0)
                    ((MainActivity) context).setFabVisibility(false);
                else if (!showAllFuels)
                    ((MainActivity) context).setFabVisibility(true);
            }
        };
        recyclerView.addOnScrollListener(onScrollListener);

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gas_station_item, viewGroup, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = recyclerView.getChildAdapterPosition(v);

                GasStationInfoDialogFragment gasStationInfoDialogFragment = new GasStationInfoDialogFragment(gasStations.get(position), context);
                FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
                gasStationInfoDialogFragment.show(fm, "gas_station_info_dialog");

            }
        });

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        GasStation station = gasStations.get(i);

        ((ViewHolder) viewHolder).name.setText(station.getName());
        ((ViewHolder) viewHolder).company.setText(station.getCompany());

        String distance = Tools.getFriendlyDistance(station.getDistance());

        ((ViewHolder) viewHolder).distance.setText(distance);

        String fuelText = null;
        String fuelType = new PreferenceHelper(context).getString("fuel", "E95");


        String priceE95 = "E95 " + station.getReadyE95(context);
        String priceE98 = "E98 " + station.getReadyE98(context);
        String priceON = "ON " + station.getReadyOn(context);
        String priceLPG = "LPG " + station.getReadyLpg(context);


//        fuelText = context.getString(R.string.average) + " " + station.getAvg() + " zł";

        if (showAllFuels) {

            fuelText = priceE95 + "\n" + priceE98 + "\n" + priceON + "\n" + priceLPG;

        } else {

            switch (fuelType) {
                case "E95":
                    fuelText = priceE95;
                    break;
                case "E98":
                    fuelText = priceE98;
                    break;
                case "LPG":
                    fuelText = priceLPG;
                    break;
                case "ON":
                    fuelText = priceON;
                    break;
            }

        }


        ((ViewHolder) viewHolder).price.setText(fuelText);
        ((ViewHolder) viewHolder).lastUpdate.setText(station.getShortUpdate());


    }


    @Override
    public int getItemCount() {
        return gasStations.size();
    }

}