package com.example.cse110_lab5.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EdgeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ZooData.Edge edge);

    @Insert
    List<Long> insertAll(List<ZooData.Edge> edge);

    @Query("SELECT * FROM `edges` WHERE `id`=:id")
    ZooData.Edge get(String id);

    @Query("SELECT * FROM `edges`")
    List<ZooData.Edge> getAll();

    @Query("SELECT * FROM `edges`")
    LiveData<List<ZooData.Edge>> getAllLive();

    @Update
    int update(ZooData.Edge edge);

    @Delete
    int delete(ZooData.Edge edge);
}
