package pl.michallysak.whererefuel.api;

import com.google.gson.annotations.SerializedName;

public class City {

    @SerializedName("city")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
