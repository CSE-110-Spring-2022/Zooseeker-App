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

    @Query("SELECT * FROM `nodes`")
    LiveData<List<ZooData.Node>> getAllLive();

    @Query("SELECT * from `nodes` WHERE `kind`='exhibit' AND tags LIKE '%' || :tag || '%'")
    List<ZooData.Node> getFiltered(String tag);

    @Update
    int update(ZooData.Node node);

    @Delete
    int delete(ZooData.Node node);
}
