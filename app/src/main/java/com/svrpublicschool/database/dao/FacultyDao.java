package com.svrpublicschool.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.svrpublicschool.models.BooksEntity;
import com.svrpublicschool.models.FacultyDetailsEntity;

import java.util.List;

@Dao
public interface FacultyDao {

    @Query("SELECT * FROM FacultyDetailsEntity")
    List<FacultyDetailsEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertList(List<FacultyDetailsEntity> items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FacultyDetailsEntity item);

    @Delete
    void delete(FacultyDetailsEntity item);

    @Query("DELETE FROM  FacultyDetailsEntity")
    void deleteAll();

    @Update
    void update(FacultyDetailsEntity item);
}
