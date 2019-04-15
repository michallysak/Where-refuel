package pl.michallysak.whererefuel.other;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import java.util.ArrayList;
import java.util.List;

public class CitySuggestion implements SearchSuggestion {


    private String cityName;

    private CitySuggestion(String suggestion) {
        this.cityName = suggestion;
    }

    private CitySuggestion(Parcel source) {
        this.cityName = source.readString();
    }



    @Override
    public String getBody() {
        return cityName;
    }

    public static final Creator<CitySuggestion> CREATOR = new Creator<CitySuggestion>() {
        @Override
        public CitySuggestion createFromParcel(Parcel in) {
            return new CitySuggestion(in);
        }

        @Override
        public CitySuggestion[] newArray(int size) {
            return new CitySuggestion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cityName);
    }


    public static List<CitySuggestion> getSuggestion(String query, List<String> dbList, int limit) {
        List<CitySuggestion> result = new ArrayList<>();

        for (String city : dbList) {

            if (query.length() <= city.length()) {
                String readyCity = city.toUpperCase().substring(0, query.length());
                if (readyCity.equals(query.toUpperCase())) {
                    result.add(new CitySuggestion(city));

                }
            }
            if (result.size() > limit)
                break;
        }

        return result;
    }

}
