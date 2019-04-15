package pl.michallysak.whererefuel.other;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.drawerlayout.widget.DrawerLayout;

import pl.michallysak.whererefuel.BuildConfig;
import pl.michallysak.whererefuel.R;
import pl.michallysak.whererefuel.ui.MainActivity;

public class Tools {

    public static void log(String message) {
        if (BuildConfig.DEBUG) {
            Log.d("Where_refuel_log", message);
        }
    }

    public static String getFriendlyDistance(double distance) {
        if (distance >= 1) {
            return Math.floor(distance * 10) / 10 + " km";
        } else {
            return Math.floor(distance * 1000) / 10 + "  m";
        }
    }

    public static void openUrl(Context context, String url) {
        int color = context.getResources().getColor(R.color.colorAccent);
        if (Tools.getTheme(context).equals("dark")) {
            color = context.getResources().getColor(R.color.gray);
            url += "?d";
        }

        new CustomTabsIntent.Builder()
                .setToolbarColor(color)
                .setStartAnimations(context, android.R.anim.fade_in, android.R.anim.fade_out)
                .setExitAnimations(context, android.R.anim.fade_in, android.R.anim.fade_out)
                .setShowTitle(true)
                .enableUrlBarHiding()
                .build()
                .launchUrl(context, Uri.parse(url));
    }

    public static String getTheme(Context context) {

        if (PreferenceManager.getDefaultSharedPreferences(context).getString("theme", "light").equals("dark"))
            return "dark";
        else
            return "light";

    }

    public static double getDistance(double lat, double lng, double latNow, double lngNow) {
        return Math.sqrt(Math.pow((lat - latNow) * 110.574, 2) + (Math.pow((lng - lngNow) * 111.320 * Math.cos(Math.toRadians(lat)), 2)) );
    }

    public static void prepareToolbar(Context context, Toolbar toolbar, boolean backArrow) {

        try {
            ((MainActivity) context).setSupportActionBar(toolbar);

            ((MainActivity) context).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((MainActivity) context).getSupportActionBar().setDisplayShowHomeEnabled(true);

            if (backArrow) {
                final Drawable arrow = context.getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
                if (Tools.getTheme(context).equals("dark")) {
                    arrow.setColorFilter(context.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                } else
                    arrow.setColorFilter(context.getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
                ((MainActivity) context).getSupportActionBar().setHomeAsUpIndicator(arrow);
            } else {
                DrawerLayout drawer = ((MainActivity) context).findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(((MainActivity) context), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.addDrawerListener(toggle);
                toggle.setDrawerIndicatorEnabled(true);
                toggle.syncState();
            }
        }catch (Exception e){
            log(e.getMessage());
        }



    }

    public static void toast(Context context, String message) {
        try {
            log("TOAST: " + message);
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }catch (Exception e){
            log(e.getMessage());
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}
