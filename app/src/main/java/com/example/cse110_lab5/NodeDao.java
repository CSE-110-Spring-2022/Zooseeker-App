package com.example.cse110_lab5;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NodeDao {
    @Insert
    long insert(Node node);

    @Insert
    List<Long> insertAll(List<Node> nodes);

    @Query("SELECT * FROM `nodes` WHERE `id`=:id")
    Node get(String id);

    @Query("SELECT * FROM `nodes`")
    List<Node> getAll();

    @Query("SELECT * FROM `nodes`")
    LiveData<List<Node>> getAllLive();

    @Update
    int update(Node node);

    @Delete
    int delete(Node node);
}
