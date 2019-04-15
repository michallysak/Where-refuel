package pl.michallysak.whererefuel.db.favouritegasstation;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavouriteGasStationDao {

    @Query("SELECT * FROM favourites_gas_station_table")
    List<FavouriteGasStation> getAll();

    @Query("SELECT COUNT(*) FROM favourites_gas_station_table")
    int getSize();

    @Query("SELECT * FROM favourites_gas_station_table WHERE id = :id")
    FavouriteGasStation get(int id);

    @Insert
    void insertAll(List<FavouriteGasStation> favouriteGasStationList);

    @Insert
    void insert(FavouriteGasStation... favouriteGasStations);

    @Update
    void update(FavouriteGasStation favouriteGasStation);

    @Delete
    void delete(FavouriteGasStation favouriteGasStation);

    @Delete
    void deleteAll(List<FavouriteGasStation> favouriteGasStationList);

}
