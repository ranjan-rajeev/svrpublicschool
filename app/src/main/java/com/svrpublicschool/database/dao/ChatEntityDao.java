package com.svrpublicschool.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.svrpublicschool.models.ChatEntity;

import java.lang.annotation.RetentionPolicy;
import java.util.List;

@Dao
public interface ChatEntityDao {

    @Query("SELECT * FROM ChatEntity")
    List<ChatEntity> getAllChat();

    @Query("SELECT * FROM ChatEntity where pkId=:pkId")
    ChatEntity getChat(long pkId);

    @Query(" select * from  (SELECT * FROM ChatEntity where  sender =:subRef ORDER BY pkId DESC LIMIT :pageSize OFFSET :startIndex )  ORDER BY pkId ASC  ")
    List<ChatEntity> getAllChat(String subRef, int startIndex, int pageSize);

    /*@Query("SELECT * FROM ChatEntity where subRef =:subRef AND chatType =:type ORDER BY pkId DESC LIMIT 2")
    List<ChatEntity> getAllChatType(String subRef, int type);*/

    @Query(" select * from  (SELECT * FROM ChatEntity where  sender =:sender AND receiver=:receiver ORDER BY pkId DESC LIMIT :pageSize OFFSET :startIndex )  ORDER BY pkId ASC  ")
    List<ChatEntity> getSingleChatWithPage(String sender, String receiver, int startIndex, int pageSize);


    @Query("select * from  (  SELECT * FROM ChatEntity where  (sender =:sender AND receiver=:receiver) OR (sender=:receiver AND receiver=:sender)  ORDER BY pkId DESC LIMIT :pageSize OFFSET :startIndex ) ORDER BY pkId ASC   ")
    List<ChatEntity> getSingleChatFirstPage(String sender, String receiver, int startIndex, int pageSize);


    @Query("select * from  (  SELECT * FROM ChatEntity where  receiver=:groupId  ORDER BY pkId DESC LIMIT :pageSize OFFSET :startIndex ) ORDER BY pkId ASC ")
    List<ChatEntity> getGroupChatWithPage(String groupId, int startIndex, int pageSize);

    @Query("SELECT * FROM ChatEntity where  receiver=:groupId ")
    List<ChatEntity> getGroupChatWithPage(String groupId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChat(List<ChatEntity> chatEntities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChat(ChatEntity chatEntity);

    @Delete
    void deleteChat(ChatEntity chatEntity);

    @Query("DELETE FROM  ChatEntity")
    void deleteAll();

    @Update
    void updateChat(ChatEntity chatEntity);
}
