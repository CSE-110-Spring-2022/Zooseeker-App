package com.example.cse110_lab5.database;

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
    long insert(ZooData.Node node);

    @Insert
    List<Long> insertAll(List<ZooData.Node> nodes);

    @Query("SELECT * FROM `nodes` WHERE `id`=:id")
    ZooData.Node get(String id);

    @Query("SELECT * FROM `nodes`")
    List<ZooData.Node> getAll();

    @Query("SELECT * FROM `nodes` WHERE `kind`='exhibit'")
    List<ZooData.Node> getExhibits();

    @Query("SELECT * FROM `nodes` WHERE `lat` IS NOT NULL AND `lng` IS NOT NULL")
    List<ZooData.Node> getExhibitsWithLocations();

    @Query("SELECT * FROM `nodes`")
    LiveData<List<ZooData.Node>> getAllLive();

    @Query("SELECT * FROM `nodes` WHERE `kind`='exhibit'")
    LiveData<List<ZooData.Node>> getAllExhibitLive();

    @Query("SELECT * from `nodes` WHERE `kind`='exhibit' AND tags LIKE '%' || :tag || '%'")
    List<ZooData.Node> getFiltered(String tag);

    @Query("SELECT id FROM `nodes` WHERE `kind`='exhibit' AND `selected`=1")
    List<String> getSelected();

    @Update
    int update(ZooData.Node node);

    @Delete
    int delete(ZooData.Node node);
}
