package pl.michallysak.whererefuel.db.cities;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Cities.class}, version = 1)
public abstract class CitiesDatabase extends RoomDatabase {
    public abstract CitiesDao dao();
}
