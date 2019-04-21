package pl.michallysak.whererefuel.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.michallysak.whererefuel.R;
import pl.michallysak.whererefuel.api.Api;
import pl.michallysak.whererefuel.api.Result;
import pl.michallysak.whererefuel.api.GasStation;
import pl.michallysak.whererefuel.other.Tools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdateFuelPriceFragment extends Fragment {

    private EditText editE95;
    private EditText editE98;
    private EditText editOn;
    private EditText editLpg;

    private Button updateButton;

    private GasStation gasStation;

    private Api api;

    private double e95;
    private double e98;
    private double on;
    private double lpg;

    UpdateFuelPriceFragment(GasStation currentGasStation) {
        gasStation = currentGasStation;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_update_fuel_price, container, false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://michallysak.pl/")

                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(Api.class);


        editE95 = view.findViewById(R.id.edit_gas_station_e95);
        editE98 = view.findViewById(R.id.edit_gas_station_e98);
        editOn = view.findViewById(R.id.edit_gas_station_on);
        editLpg = view.findViewById(R.id.edit_gas_station_lpg);

        String noData = getString(R.string.no_data);
        String tempHint;
        double tempPrice;

        tempPrice = gasStation.getE95();

        if (tempPrice == 0)
            tempHint = noData;
        else
            tempHint = tempPrice + "";

        editE95.setHint(tempHint);


        tempPrice = gasStation.getE98();

        if (tempPrice == 0)
            tempHint = noData;
        else
            tempHint = tempPrice + "";


        editE98.setHint(tempHint);


        tempPrice = gasStation.getOn();

        if (tempPrice == 0)
            tempHint = noData;
        else
            tempHint = tempPrice + "";

        editOn.setHint(tempHint);


        tempPrice = gasStation.getLpg();

        if (tempPrice == 0)
            tempHint = noData;
        else
            tempHint = tempPrice + "";

        editLpg.setHint(tempHint);

        Toolbar toolbar = view.findViewById(R.id.toolbar_edit);
        Tools.prepareToolbar(getContext(), toolbar, true);


        updateButton = view.findViewById(R.id.update_station_btn);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enqueueUpdatePrice();
            }
        });


        return view;
    }



    private String getFuel(EditText editText) {

        editText.setError(null);

        String text = editText.getText().toString();
        String hint = editText.getHint().toString();

        if (text.length() == 4){
            final String regex = "\\d\\.\\d{2}";


            final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            final Matcher matcher = pattern.matcher(text);


            while (matcher.find()) {
                if (matcher.group(0).equals(text))
                    return text;

            }

            editText.setError(getString(R.string.enter_valid_fuel_price));
            return null;



        }else if (text.length() == 0){
            return hint;
        }else {
            editText.setError(getString(R.string.enter_valid_fuel_price));
            return null;
        }

    }

    private boolean isValid(){

        boolean valid = true;
        String fuelTemp;

        fuelTemp = getFuel(editE95);

        if (fuelTemp == null)
            valid = false;
        else if (fuelTemp.equals(editE95.getText().toString()))
            e95 = Double.parseDouble(fuelTemp);
        else if (fuelTemp.equals(editE95.getHint().toString()))
            e95 = Double.parseDouble(fuelTemp);

        fuelTemp = getFuel(editE98);


        if (fuelTemp == null)
            valid = false;
        else if (fuelTemp.equals(editE98.getText().toString()))
            e98 = Double.parseDouble(fuelTemp);
        else if (fuelTemp.equals(editE98.getHint().toString()))
            e98 = Double.parseDouble(fuelTemp);


        fuelTemp = getFuel(editOn);

        if (fuelTemp == null)
            valid = false;
        else if (fuelTemp.equals(editOn.getText().toString()))
            on = Double.parseDouble(fuelTemp);
        else if (fuelTemp.equals(editOn.getHint().toString()))
            on = Double.parseDouble(fuelTemp);


        fuelTemp = getFuel(editLpg);


        if (fuelTemp == null)
            valid = false;
        else if (fuelTemp.equals(editLpg.getText().toString()))
            lpg = Double.parseDouble(fuelTemp);
        else if (fuelTemp.equals(editLpg.getHint().toString()))
            lpg = Double.parseDouble(fuelTemp);

        if (editE95.getText().toString().equals("") && editE98.getText().toString().equals("")
                && editLpg.getText().toString().equals("") && editOn.getText().toString().equals("")) {
            Tools.toast(getContext(), getString(R.string.enter_at_least_one_change));
            valid = false;
        }

        return valid;
    }


    private void enqueueUpdatePrice() {

        Tools.hideKeyboard(getActivity());

        if (isValid()){
            updateButton.setClickable(false);

            Call<List<Result>> call = api.updatePrice(gasStation.getId(), e95, e98, on, lpg);

            Tools.log(call.request().toString());

            call.enqueue(new Callback<List<Result>>() {
                @Override
                public void onResponse(@NonNull Call<List<Result>> call, @NonNull Response<List<Result>> response) {
                    Tools.toast(getContext(), getString(R.string.success_update_fuel_price));
                    updateButton.setClickable(true);
                    editE95.setText(null);
                    editE98.setText(null);
                    editOn.setText(null);
                    editLpg.setText(null);
                    editE95.setHint(String.valueOf(e95));
                    editE98.setHint(String.valueOf(e98));
                    editOn.setHint(String.valueOf(on));
                    editLpg.setHint(String.valueOf(lpg));
                }

                @Override
                public void onFailure(@NonNull Call<List<Result>> call, @NonNull Throwable t) {
                    Tools.toast(getContext(), getString(R.string.error_update_fuel_price));
                    updateButton.setClickable(true);
                }
            });


        }



    }

}
