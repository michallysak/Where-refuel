package pl.michallysak.whererefuel.db.favouritegasstation;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import pl.michallysak.whererefuel.api.GasStation;

@Entity(tableName = "favourites_gas_station_table")
public class FavouriteGasStation {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "company")
    private String company;

    @ColumnInfo(name = "city")
    private String city;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "e95")
    private double e95;

    @ColumnInfo(name = "e98")
    private double e98;

    @ColumnInfo(name = "lpg")
    private double lpg;

    @ColumnInfo(name = "on")
    private double on;

    @ColumnInfo(name = "lastUpdate")
    private String lastUpdate;

    @ColumnInfo(name = "lat")
    private double lat;

    @ColumnInfo(name = "lng")
    private double lng;

    @ColumnInfo(name = "distance")
    private double distance;

    public FavouriteGasStation() {

    }

    public static FavouriteGasStation generateFavouritesGasStation(GasStation gasStation){
        FavouriteGasStation favouriteGasStation = new FavouriteGasStation();
        favouriteGasStation.setId(gasStation.getId());
        favouriteGasStation.setName(gasStation.getName());
        favouriteGasStation.setCompany(gasStation.getCompany());
        favouriteGasStation.setCity(gasStation.getCity());
        favouriteGasStation.setAddress(gasStation.getAddress());
        favouriteGasStation.setE95(gasStation.getE95());
        favouriteGasStation.setE98(gasStation.getE98());
        favouriteGasStation.setOn(gasStation.getOn());
        favouriteGasStation.setLpg(gasStation.getLpg());
        favouriteGasStation.setLastUpdate(gasStation.getLastUpdate());
        favouriteGasStation.setLat(gasStation.getLat());
        favouriteGasStation.setLng(gasStation.getLng());
        favouriteGasStation.setDistance(gasStation.getDistance());

        return favouriteGasStation;
    }

    public GasStation generateGasStation(){
        GasStation gasStation = new GasStation();
        gasStation.setId(id);
        gasStation.setName(name);
        gasStation.setCompany(company);
        gasStation.setCity(city);
        gasStation.setAddress(address);
        gasStation.setE95(e95);
        gasStation.setE98(e98);
        gasStation.setOn(on);
        gasStation.setLpg(lpg);
        gasStation.setLastUpdate(lastUpdate);
        gasStation.setLat(lat);
        gasStation.setLng(lng);
        gasStation.setDistance(distance);

        return gasStation;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getE95() {
        return e95;
    }

    public double getE98() {
        return e98;
    }

    public double getDistance() {
        return distance;
    }

    public double getLat() {
        return lat;
    }

    public double getLpg() {
        return lpg;
    }

    public double getLng() {
        return lng;
    }

    public double getOn() {
        return on;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCompany() {
        return company;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLpg(double lpg) {
        this.lpg = lpg;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setE95(double e95) {
        this.e95 = e95;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setE98(double e98) {
        this.e98 = e98;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setOn(double on) {
        this.on = on;
    }

    public void setId(int id) {
        this.id = id;
    }

}
