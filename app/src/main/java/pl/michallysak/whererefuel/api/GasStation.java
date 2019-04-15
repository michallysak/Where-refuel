package pl.michallysak.whererefuel.api;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import pl.michallysak.whererefuel.R;

public class GasStation{


    private int id;
    private String name;
    private String company;
    private String city;
    private String address;

    @SerializedName("e95")
    private double e95;

    @SerializedName("e98")
    private double e98;

    @SerializedName("lpg")
    private double lpg;

    @SerializedName("on")
    private double on;

    @SerializedName("last_update")
    private String lastUpdate;

    private double lat;

    private double lng;

    private double distance;


    public void setId(int id) {
        this.id = id;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setE98(double e98) {
        this.e98 = e98;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setE95(double e95) {
        this.e95 = e95;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setLpg(double lpg) {
        this.lpg = lpg;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCompany() {
        return company;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public double getE95() {
        return e95;
    }

    public double getE98() {
        return e98;
    }

    public double getLpg() {
        return lpg;
    }

    public double getOn() {
        return on;
    }

    public void setOn(double on) {
        this.on = on;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public double getDistance() {
        return distance;
    }

    public String getShortUpdate() {
        return lastUpdate.substring(0, 10);
    }

    public String getReadyE95(Context context) {
        if (e95 == 0.0)
            return context.getString(R.string.no_data);
        else
            return e95 + " zł";
    }

    public String getReadyE98(Context context) {
        if (e98 == 0.0)
            return context.getString(R.string.no_data);
        else
            return e98 + " zł";
    }

    public String getReadyLpg(Context context) {
        if (lpg == 0.0)
            return context.getString(R.string.no_data);
        else
            return lpg + " zł";
    }

    public String getReadyOn(Context context) {
        if (on == 0.0)
            return context.getString(R.string.no_data);
        else
            return on + " zł";
    }

    public double getAvg(){
        return (Math.round((e95+e95+lpg+on)/4)*100)/100;
    }

    public String sharaData(Context context) {
        return name + "\n" +
                city + ", " + address + "\n" +
                "E95: " + e95 +
                " zł" + "   " + "E98: " + e98 + " zł" + "\n" +
                "ON: " + on + " zł" + "    " + "LPG: " + lpg + " zł" + "\n" +
                context.getString(R.string.last_update) + ": " + getShortUpdate();
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
