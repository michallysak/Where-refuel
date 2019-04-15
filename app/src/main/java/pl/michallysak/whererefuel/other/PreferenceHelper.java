package pl.michallysak.whererefuel.other;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHelper {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PreferenceHelper(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public String getString(String name, String defualtValue){
        return sharedPreferences.getString(name, defualtValue);
    }

    public boolean getBoolean(String name, boolean defualtValue){
        return sharedPreferences.getBoolean(name, defualtValue);
    }

    public int getInt(String name, int defualtValue){
        return sharedPreferences.getInt(name, defualtValue);
    }

    public float getFloat(String name, float defualtValue){
        return sharedPreferences.getFloat(name, defualtValue);
    }


    public void putString(String name, String value){
        editor.putString(name, value).apply();
    }

    public void putBoolean(String name, boolean value){
        editor.putBoolean(name, value).apply();
    }

    public void putInt(String name, int value){
        editor.putInt(name, value).apply();
    }

    public void putFloat(String name, float value){
        editor.putFloat(name, value).apply();
    }
}
