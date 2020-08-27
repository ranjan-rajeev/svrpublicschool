package com.svrpublicschool.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.svrpublicschool.models.FacultyDetailsEntity;
import com.svrpublicschool.models.NoticeEntity;

import java.util.List;

@Dao
public interface NoticeDao {

    @Query("SELECT * FROM NoticeEntity")
    List<NoticeEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertList(List<NoticeEntity> items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NoticeEntity item);

    @Delete
    void delete(NoticeEntity item);

    @Query("DELETE FROM  NoticeEntity")
    void deleteAll();

    @Update
    void update(NoticeEntity item);
}
