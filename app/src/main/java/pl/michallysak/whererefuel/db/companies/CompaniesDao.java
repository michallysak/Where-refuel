package pl.michallysak.whererefuel.db.companies;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CompaniesDao {

    @Query("SELECT * FROM companies_table")
    List<Companies> getAll();

    @Query("SELECT COUNT(*) FROM companies_table")
    int getSize();

    @Insert
    void insertAll(List<Companies> companiesList);

    @Insert
    void insert(Companies... companies);

    @Update
    void update(Companies companies);

    @Delete
    void delete(Companies companies);

    @Delete
    void deleteAll(List<Companies> companiesList);

}
