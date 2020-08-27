package com.svrpublicschool.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.svrpublicschool.models.FacultyDetailsEntity;
import com.svrpublicschool.models.UserEntity;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM UserEntity")
    List<UserEntity> getAll();

    @Query("SELECT * FROM UserEntity where fId=:fid")
    UserEntity getUser(String fid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertList(List<UserEntity> items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserEntity item);

    @Delete
    void delete(UserEntity item);

    @Query("DELETE FROM  UserEntity")
    void deleteAll();

    @Update
    void update(UserEntity item);
}
