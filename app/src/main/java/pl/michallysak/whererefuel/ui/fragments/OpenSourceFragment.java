package pl.michallysak.whererefuel.ui.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;

import pl.michallysak.whererefuel.R;
import pl.michallysak.whererefuel.other.License;
import pl.michallysak.whererefuel.other.Tools;


public class OpenSourceFragment extends Fragment {


    public OpenSourceFragment() {}

    private ArrayList<License> licenses;

    class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return licenses.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint({"ViewHolder", "InflateParams"})
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.license_item, null);


            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Tools.openUrl(getContext(), licenses.get(position).getUrl());
                    return false;
                }
            });

            TextView licenseName = convertView.findViewById(R.id.license_name);
            TextView licenseMessage = convertView.findViewById(R.id.license_message);

            licenseName.setText(licenses.get(position).getName());
            licenseMessage.setText(licenses.get(position).getMessage());

            return convertView;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_open_source, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_open_source_licences);
        Tools.prepareToolbar(getContext(), toolbar, true);

        ImageView ossAll = view.findViewById(R.id.open_source_licences_all);

        if (Tools.getTheme(getContext()).equals("light")) {
            Drawable fabDraw = getResources().getDrawable(R.drawable.ic_oss);
            fabDraw.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
            ossAll.setImageDrawable(fabDraw);
        }

        ossAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), OssLicensesMenuActivity.class));
            }
        });




        licenses = getLicenses();

        ListView listView = view.findViewById(R.id.oss_list_view);
        listView.setAdapter(new Adapter());


        return view;
    }


    private ArrayList<License> getLicenses() {
        XmlPullParserFactory parserFactory;
        try {
            parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();
            InputStream is = getResources().openRawResource(R.raw.license);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);

            ArrayList<License> licenses = new ArrayList<>();
            int eventType = parser.getEventType();
            String name = "NAME";
            String message = "MESSAGE";
            String url = "URL";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String eltName;

                if (eventType == XmlPullParser.START_TAG) {
                    eltName = parser.getName();

                    if ("license".equals(eltName)) {
                        if (!name.equals("NAME")) {
                            licenses.add(new License(name, message, url));
                        }

                    } else {
                        if ("name".equals(eltName)) {
                            name = parser.nextText();
                        } else if ("message".equals(eltName)) {
                            message = parser.nextText();
                        } else if ("url".equals(eltName)) {
                            url = parser.nextText();
                        }
                    }
                }

                eventType = parser.next();
            }

            licenses.add(new License(name, message, url));

            return licenses;

        } catch (Exception e) {
            return null;
        }
    }


}
