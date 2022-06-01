package com.example.cse110_lab5.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Interface methods and definitions for interacting with the database storing all the Edges
 */
@Dao
public interface EdgeDao {
    /**
     * Insert an Edge into the database, replacing when the Edge already exists in the database
     * @param edge      the Edge to insert
     * @return          the number of Edges inserted successfully
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ZooData.Edge edge);

    /**
     * Insert a list of Edges into the database, one at a time
     * @param edge      a list of the Edges to insert
     * @return          the number of Edges inserted successfully
     */
    @Insert
    List<Long> insertAll(List<ZooData.Edge> edge);

    /**
     * Get the Edge in the database with the specified id
     * @param id        the id of the Edge to obtain
     * @return          the Edge with the specified id
     */
    @Query("SELECT * FROM `edges` WHERE `id`=:id")
    ZooData.Edge get(String id);

    /**
     * Get a list of all the Edges in the database
     * @return          a list of all the Edges in the database
     */
    @Query("SELECT * FROM `edges`")
    List<ZooData.Edge> getAll();

    /**
     * Get an observable representation of all the Edges in the database
     * @return          a LiveData of the list of all Edges in the database
     */
    @Query("SELECT * FROM `edges`")
    LiveData<List<ZooData.Edge>> getAllLive();

    /**
     * Update an Edge in the database
     * @param edge      the Edge to update
     * @return          the number of Edges updated successfully
     */
    @Update
    int update(ZooData.Edge edge);

    /**
     * Delete an Edge in the database
     * @param edge      the Edge to delete
     * @return          the number of Edges deleted successfully
     */
    @Delete
    int delete(ZooData.Edge edge);
}
