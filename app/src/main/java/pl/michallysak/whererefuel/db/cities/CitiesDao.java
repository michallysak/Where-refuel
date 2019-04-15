package pl.michallysak.whererefuel.db.cities;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CitiesDao {

    @Query("SELECT * FROM cities_table")
    List<Cities> getAll();

    @Query("SELECT COUNT(*) FROM cities_table")
    int getSize();

    @Insert
    void insertAll(List<Cities> citiesList);

    @Insert
    void insert(Cities... companies);

    @Update
    void update(Cities cities);

    @Delete
    void delete(Cities cities);

    @Delete
    void deleteAll(List<Cities> citiesList);

}
