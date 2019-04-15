package pl.michallysak.whererefuel.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import java.util.List;

import pl.michallysak.whererefuel.R;
import pl.michallysak.whererefuel.api.Api;
import pl.michallysak.whererefuel.api.GasStation;
import pl.michallysak.whererefuel.api.Result;
import pl.michallysak.whererefuel.other.Tools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReportStationFragment extends DialogFragment {

    private GasStation gasStation;

    private EditText reportEditText;
    private ImageView reportBtn;

    private Api api;


    ReportStationFragment(GasStation currentGasStation, Context context) {
        this.gasStation = currentGasStation;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_report_station, container, false);


        Toolbar toolbar = view.findViewById(R.id.toolbar_report);
        Tools.prepareToolbar(getContext(), toolbar, true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://michallysak.pl/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(Api.class);


        reportEditText = view.findViewById(R.id.report_station_message);

        reportBtn = view.findViewById(R.id.report_station_btn);

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enqueueReport();
            }
        });


        return view;
    }


    private void enqueueReport() {

        reportEditText.setError(null);

        Tools.hideKeyboard(getActivity());

        String message = reportEditText.getText().toString();

        if (message.length() > 0) {


            Call<List<Result>> call = api.reportStation(message + " " + gasStation.getId());

            Tools.log(call.request().toString());
            reportBtn.setVisibility(View.GONE);

            call.enqueue(new Callback<List<Result>>() {
                @Override
                public void onResponse(@NonNull Call<List<Result>> call, @NonNull Response<List<Result>> response) {
                    Tools.toast(getContext(), getString(R.string.success_report_station));
                }

                @Override
                public void onFailure(@NonNull Call<List<Result>> call, @NonNull Throwable t) {
                    Tools.toast(getContext(), getString(R.string.error_report_station));
                }
            });



        } else {
            reportEditText.setError(getString(R.string.enter_valid_message));

        }


    }

}
