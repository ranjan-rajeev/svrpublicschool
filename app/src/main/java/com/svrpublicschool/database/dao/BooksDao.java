package com.svrpublicschool.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.svrpublicschool.models.BooksEntity;
import com.svrpublicschool.models.ChatEntity;

import java.util.List;

@Dao
public interface BooksDao {

    @Query("SELECT * FROM BooksEntity")
    List<BooksEntity> getAll();

    @Query("SELECT * FROM BooksEntity where isCategory=:isCategory")
    List<BooksEntity> getUniqueBooks(boolean isCategory);

    @Query("SELECT * FROM BooksEntity where classX=:classname AND subject=:subject AND isCategory=:isCategogy AND hasSubCategory =:hasSubcategory ")
    List<BooksEntity> getBookDetails(int classname, String subject, boolean isCategogy, boolean hasSubcategory);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertList(List<BooksEntity> booksEntities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BooksEntity bookEntity);

    @Delete
    void delete(BooksEntity bookEntity);

    @Query("DELETE FROM  BooksEntity")
    void deleteAll();

    @Update
    void update(BooksEntity bookEntity);
}
