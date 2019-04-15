package pl.michallysak.whererefuel.api;

import com.google.gson.annotations.SerializedName;

public class Result {
    @SerializedName("result")
    private String result;

    public String getResult() {
        return result;
    }
}
