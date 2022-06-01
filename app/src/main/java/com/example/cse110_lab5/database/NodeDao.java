package com.example.cse110_lab5.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Interface methods and definitions for interacting with the database storing all the Nodes
 */
@Dao
public interface NodeDao {
    /**
     * Insert a Node into the database
     * @param node      the Node to insert
     * @return          the rowId of the inserted Node
     */
    @Insert
    long insert(ZooData.Node node);

    /**
     * Insert a list of Nodes into the database, one at a time
     * @param nodes     the list containing each of the Nodes to insert
     * @return          a list of rowIds of the inserted Nodes
     */
    @Insert
    List<Long> insertAll(List<ZooData.Node> nodes);

    /**
     * Clear all the Nodes that the user has selected to visit
     */
    @Query("UPDATE `nodes` SET `selected`=0")
    int clearAll();

    /**
     * Get the Node with the indicated id
     * @param id        the id
     * @return          the Node with that id
     */
    @Query("SELECT * FROM `nodes` WHERE `id`=:id")
    ZooData.Node get(String id);

    /**
     * Get a list of all the Nodes in the database
     * @return      the list of all Nodes
     */
    @Query("SELECT * FROM `nodes`")
    List<ZooData.Node> getAll();

    /**
     * Get a list of all the Nodes that are exhibits in the database
     * @return      the list of all exhibit Nodes
     */
    @Query("SELECT * FROM `nodes` WHERE `kind`='exhibit'")
    List<ZooData.Node> getExhibits();

    /**
     * Get a list of all the Nodes with valid coordinates in the database
     * @return      the list of all exhibits with coordinates
     */
    @Query("SELECT * FROM `nodes` WHERE `lat` IS NOT NULL AND `lng` IS NOT NULL")
    List<ZooData.Node> getExhibitsWithLocations();

    /**
     * Get an observable representation of the list of all Nodes
     * @return      a LiveData of the list of all Nodes
     */
    @Query("SELECT * FROM `nodes`")
    LiveData<List<ZooData.Node>> getAllLive();

    /**
     * Get an observable representation of the list of all Nodes that are exhibits
     * @return      a LiveData of the list of all exhibit Nodes
     */
    @Query("SELECT * FROM `nodes` WHERE `kind`='exhibit' ORDER BY `name`")
    LiveData<List<ZooData.Node>> getAllExhibitLive();

    /**
     * Get a list of all Nodes based on their tags
     * @param tag       the tag to search by
     * @return          a list of all the Nodes with tags that are similar to the tag
     */
    @Query("SELECT * from `nodes` WHERE `kind`='exhibit' AND tags LIKE '%' || :tag || '%' ORDER BY `name`")
    List<ZooData.Node> getFiltered(String tag);

    /**
     * Get a list of all the Nodes the user has selected
     * @return      a list of all the selected Nodes
     */
    @Query("SELECT id FROM `nodes` WHERE `kind`='exhibit' AND `selected`=1")
    List<String> getSelected();

    /**
     * Get an observable list of all the Nodes the user has selected
     * @return      a LiveData of the list of all the selected Nodes
     */
    @Query("SELECT name FROM `nodes` WHERE `kind`='exhibit' AND `selected`=1 ORDER BY `name`")
    LiveData<List<String>> getSelectedLive();

    /**
     * Get the gate Node
     * @return      the Node that corresponds to the entrance and exit gate
     */
    @Query("SELECT * FROM `nodes` WHERE `kind`='gate'")
    ZooData.Node getGate();

    /**
     * Update a Node in the database
     * @param node      the Node to update
     * @return          the number of Nodes updated successfully
     */
    @Update
    int update(ZooData.Node node);

    /**
     * Remove a Node from the database
     * @param node      the Node to remove
     * @return          the number of Nodes removed successfully
     */
    @Delete
    int delete(ZooData.Node node);
}
