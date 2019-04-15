package pl.michallysak.whererefuel.db.companies;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Companies.class}, version = 1)
public abstract class CompaniesDatabase extends RoomDatabase {
    public abstract CompaniesDao dao();
}
