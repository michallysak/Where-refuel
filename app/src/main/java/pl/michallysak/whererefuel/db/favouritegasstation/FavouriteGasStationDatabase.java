package pl.michallysak.whererefuel.db.favouritegasstation;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import pl.michallysak.whererefuel.db.cities.Cities;
import pl.michallysak.whererefuel.db.cities.CitiesDao;

@Database(entities = {FavouriteGasStation.class}, version = 1)
public abstract class FavouriteGasStationDatabase extends RoomDatabase {
    public abstract FavouriteGasStationDao dao();
}
