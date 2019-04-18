package pl.michallysak.whererefuel.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import pl.michallysak.whererefuel.R;
import pl.michallysak.whererefuel.other.PreferenceHelper;
import pl.michallysak.whererefuel.other.Tools;
import pl.michallysak.whererefuel.ui.MainActivity;

public class SettingsFragment extends Fragment {

    private PreferenceHelper sharedPreferences;

    private boolean force;
    private Button forceButton;
    private Button buttonThemeChanger;

    public SettingsFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPreferences = new PreferenceHelper(getContext());

        Toolbar toolbar = view.findViewById(R.id.toolbar_settings);
        Tools.prepareToolbar(getContext(), toolbar, false);



        Button buttonHome = view.findViewById(R.id.button_home);
        String home = sharedPreferences.getString("home", "map");

        if (home.equals("map"))
            buttonHome.setText(R.string.map);
        else
            buttonHome.setText(R.string.list);

        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                if (button.getText().toString().equalsIgnoreCase(getString(R.string.map))){
                    sharedPreferences.putString("home", "list");
                    button.setText(R.string.list);
                }else{
                    sharedPreferences.putString("home", "map");
                    button.setText(R.string.map);
                }
            }
        });


        Button buttonCurrentLocationWhenStarted = view.findViewById(R.id.button_current_location_when_started);
        boolean currentLocationWhenStarted = sharedPreferences.getBoolean("current_location_when_started", true);

        if (currentLocationWhenStarted)
            buttonCurrentLocationWhenStarted.setText(R.string.yes);
        else
            buttonCurrentLocationWhenStarted.setText(R.string.no);

        buttonCurrentLocationWhenStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                if (button.getText().toString().equalsIgnoreCase(getString(R.string.yes))){
                    sharedPreferences.putBoolean("current_location_when_started", false);
                    button.setText(R.string.no);
                }else{
                    sharedPreferences.putBoolean("current_location_when_started", true);
                    button.setText(R.string.yes);
                }
            }
        });


        buttonThemeChanger = view.findViewById(R.id.button_theme_changer);
        if (Tools.getTheme(getContext()).equals("light")) {
            buttonThemeChanger.setText(R.string.theme_light);
        } else {
            buttonThemeChanger.setText(R.string.theme_dark);

            view.findViewById(R.id.layout_force_daily_map).setVisibility(View.VISIBLE);

            forceButton = view.findViewById(R.id.button_force_daily_map);

            force = sharedPreferences.getBoolean("force_daily_map", false);

            if (force){
                forceButton.setText(R.string.yes);
            }else {
                forceButton.setText(R.string.no);
            }

            forceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sharedPreferences.getBoolean("force_daily_map", false)){
                        forceButton.setText(R.string.no);
                        sharedPreferences.putBoolean("force_daily_map", false);
                    }else {
                        forceButton.setText(R.string.yes);
                        sharedPreferences.putBoolean("force_daily_map", true);
                    }
                }
            });

        }

        buttonThemeChanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Tools.getTheme(getContext()).equals("light")) {
                    sharedPreferences.putString("theme", "dark");
                    buttonThemeChanger.setText(R.string.theme_dark);
                } else {
                    sharedPreferences.putString("theme", "light");
                    buttonThemeChanger.setText(R.string.theme_light);
                }
                ((MainActivity)getContext()).resetActivity(new Intent(getContext(), MainActivity.class));
            }
        });

        return view;
    }






}
