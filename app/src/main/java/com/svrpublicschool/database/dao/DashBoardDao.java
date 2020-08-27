package com.svrpublicschool.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.svrpublicschool.models.ChatEntity;
import com.svrpublicschool.models.DashBoardEntity;

import java.util.List;

@Dao
public interface DashBoardDao {

    @Query("SELECT * FROM DashBoardEntity")
    List<DashBoardEntity> getAll();

    @Query("SELECT DISTINCT (type) FROM DashBoardEntity ")
    List<String> getAllTypes();

    @Query("SELECT * FROM DashBoardEntity  where type =:type")
    List<DashBoardEntity> getListTypes(String type);

    @Query("SELECT url FROM DashBoardEntity  where type =:type")
    List<String> getBannerList(String type);

    @Query("SELECT title FROM DashBoardEntity  where type =:type")
    String getMarquee(String type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertList(List<DashBoardEntity> dashBoardEntities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DashBoardEntity dashBoardEntity);

    @Delete
    void delete(DashBoardEntity dashBoardEntity);

    @Update
    void update(DashBoardEntity dashBoardEntity);

    @Query("DELETE FROM  DashBoardEntity")
    void deleteAll();
}
