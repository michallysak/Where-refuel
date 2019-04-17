package pl.michallysak.whererefuel.ui.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

import pl.michallysak.whererefuel.R;
import pl.michallysak.whererefuel.db.companies.Companies;
import pl.michallysak.whererefuel.db.companies.CompaniesDatabase;
import pl.michallysak.whererefuel.other.PreferenceHelper;
import pl.michallysak.whererefuel.other.Tools;
import pl.michallysak.whererefuel.ui.MainActivity;


public class SortFragmentDialog  extends DialogFragment {

    private PreferenceHelper sharedPreferences;

    private Button buttonSort;
    private Button buttonFuel;
    private Button buttonCompany;

    private List<String> companies;

    private String sort;
    private String fuel;
    private int radius;
    private String company;

    public SortFragmentDialog() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sort_dialog, container, false);

        if (Tools.getTheme(getContext()).equals("dark"))
            view.setBackgroundColor(getContext().getResources().getColor(R.color.gray));

        sharedPreferences = new PreferenceHelper(getContext());

        CompaniesDatabase companiesDatabase = Room.databaseBuilder(getContext(), CompaniesDatabase.class, "companiesDatabase")
                .allowMainThreadQueries()
                .build();

        companies = new ArrayList<>();

        for(Companies c: companiesDatabase.dao().getAll()){
            companies.add(c.getName());
        }

        Button filterButton = view.findViewById(R.id.filter_station_btn);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortPress();
            }
        });

        sort = sharedPreferences.getString("sort", "price");
        fuel = sharedPreferences.getString("fuel", "E95");
        radius = sharedPreferences.getInt("radius", 15);
        company = sharedPreferences.getString("company", "all");


//        RADIUS
        final TextView seekTextView = view.findViewById(R.id.textView_search_radius);
        SeekBar seekBar = view.findViewById(R.id.seekBar_search_radius);
        seekBar.setProgress(radius - 5);
        String s = radius + " km ";
        seekTextView.setText(s);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String text = (progress + 5) + " km ";
                seekTextView.setText(text);
                radius = progress + 5;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



//        SORT BY
        buttonSort = view.findViewById(R.id.button_sort);
        switch (sort){
            case "price":
                buttonSort.setText(R.string.price);
                break;
            case "distance":
                buttonSort.setText(R.string.distance);
                break;
            case "update":
                buttonSort.setText(R.string.last_update);
                break;
        }

        buttonSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (Tools.getTheme(getContext()).equals("dark"))
                    builder = new AlertDialog.Builder(getContext(), R.style.DarkAlertDialog);
                else
                    builder = new AlertDialog.Builder(getContext());

                final String[] sortTab = {
                        getString(R.string.distance),
                        getString(R.string.price),
                        getString(R.string.last_update),
                };

                builder.setItems(sortTab, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selected = sortTab[which];

                        switch (which){
                            case 0:
                                buttonSort.setText(R.string.distance);
                                sort = "distance";
                                break;
                            case 1:
                                buttonSort.setText(R.string.price);
                                sort =  "price";
                                break;
                            case 2:
                                buttonSort.setText(R.string.last_update);
                                sort = "update";
                                break;
                        }

                        buttonSort.setText(selected);
                    }
                }).create().show();
            }
        });


//          FUEL
        buttonFuel = view.findViewById(R.id.button_fuel);

        buttonFuel.setText(fuel);

        buttonFuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (Tools.getTheme(getContext()).equals("dark"))
                    builder = new AlertDialog.Builder(getContext(), R.style.DarkAlertDialog);
                else
                    builder = new AlertDialog.Builder(getContext());

                final String[] fuelTab = {
                        "E95",
                        "E98",
                        "ON",
                        "LPG"
                };

                builder.setItems(fuelTab, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selected = fuelTab[which];

                        fuel = selected;
                        buttonFuel.setText(selected);


                    }
                }).create().show();
            }
        });

//        COMPANY

        if (companies.size() == 0 || !companies.get(0).equals(getString(R.string.all)))
            companies.add(0, getString(R.string.all));

        final String[] tab;
        tab = companies.toArray(new String[0]);

        buttonCompany = view.findViewById(R.id.button_company);
        if (company.equals("all"))
            buttonCompany.setText(R.string.all);
        else
            buttonCompany.setText(company);



        buttonCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (Tools.getTheme(getContext()).equals("dark"))
                    builder = new AlertDialog.Builder(getContext(), R.style.DarkAlertDialog);
                else
                    builder = new AlertDialog.Builder(getContext());

                builder.setItems(tab, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selected = tab[which];

                        if (selected.equals(getString(R.string.all)))
                            company = "all";
                        else
                            company = selected;


                        buttonCompany.setText(selected);
                    }
                }).create().show();

            }
        });



        return view;

    }

    private  void sortPress(){
        sharedPreferences.putString("sort", sort);
        sharedPreferences.putString("fuel", fuel);
        sharedPreferences.putInt("radius", radius);
        sharedPreferences.putString("company", company);
        dismiss();
        ((MainActivity)getContext()).setupPreferences();
        ((MainActivity)getContext()).searchNearbyGasStation(((MainActivity) getContext()).lastQuery);
    }








}
