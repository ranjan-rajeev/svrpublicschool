package com.svrpublicschool.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.svrpublicschool.database.dao.BooksDao;
import com.svrpublicschool.database.dao.ChatEntityDao;
import com.svrpublicschool.database.dao.DashBoardDao;
import com.svrpublicschool.database.dao.FacultyDao;
import com.svrpublicschool.database.dao.NoticeDao;
import com.svrpublicschool.database.dao.UserDao;
import com.svrpublicschool.models.BooksEntity;
import com.svrpublicschool.models.ChatEntity;
import com.svrpublicschool.models.DashBoardEntity;
import com.svrpublicschool.models.FacultyDetailsEntity;
import com.svrpublicschool.models.NoticeEntity;
import com.svrpublicschool.models.UserEntity;

@Database(entities = {ChatEntity.class, BooksEntity.class, DashBoardEntity.class, FacultyDetailsEntity.class
        , UserEntity.class, NoticeEntity.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ChatEntityDao chatEntityDao();

    public abstract BooksDao booksDao();

    public abstract DashBoardDao dashBoardDao();

    public abstract FacultyDao facultyDao();

    public abstract UserDao userDao();

    public abstract NoticeDao noticeDao();
}