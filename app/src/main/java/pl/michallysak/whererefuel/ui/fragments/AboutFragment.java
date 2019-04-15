package pl.michallysak.whererefuel.ui.fragments;


import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import pl.michallysak.whererefuel.R;
import pl.michallysak.whererefuel.other.Tools;
import pl.michallysak.whererefuel.ui.MainActivity;


public class AboutFragment extends Fragment {


    public AboutFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_about);

        Tools.prepareToolbar(getContext(), toolbar, false);

        String version = "";

        try {
            PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            version = pInfo.versionName;
        }catch(Exception e) {
            e.printStackTrace();
        }

        TextView textView = view.findViewById(R.id.version);
        version = getResources().getString(R.string.version)  + " " + version;
        textView.setText(version);


        view.findViewById(R.id.privacy_policy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Tools.openUrl(getContext(), "https://michallysak.pl/whererefuel/privacy_policy.html");
                }catch (Exception e){
                    Tools.log(e.getMessage());
                }
            }
        });

        view.findViewById(R.id.open_source_licences).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((MainActivity)getContext()).showFragment(new OpenSourceFragment());
                }catch (Exception e){
                    Tools.log(e.getMessage());
                }
            }
        });

        return view;
    }




}
