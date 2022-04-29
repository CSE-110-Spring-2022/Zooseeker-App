package com.example.cse110_lab5;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EdgeDao {
    @Insert
    long insert(Edge edge);

    @Insert
    List<Long> insertAll(List<Edge> edge);

    @Query("SELECT * FROM `edges` WHERE `id`=:id")
    Edge get(String id);

    @Query("SELECT * FROM `edges`")
    List<Edge> getAll();

    @Query("SELECT * FROM `edges`")
    LiveData<List<Edge>> getAllLive();

    @Update
    int update(Edge edge);

    @Delete
    int delete(Edge edge);
}
