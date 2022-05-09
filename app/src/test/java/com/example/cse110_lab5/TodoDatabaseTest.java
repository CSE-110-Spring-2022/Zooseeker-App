package com.example.cse110_lab5;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cse110_lab5.database.EdgeDao;
import com.example.cse110_lab5.database.GraphDatabase;
import com.example.cse110_lab5.database.NodeDao;
import com.example.cse110_lab5.database.ZooData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TodoDatabaseTest {
    private EdgeDao edgeDao;
    private NodeDao nodeDao;
    private GraphDatabase db;
    private ZooData.Node testNode;
    private ZooData.Node testNode2;
    private ZooData.Node testNode3;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, GraphDatabase.class)
                .allowMainThreadQueries()
                .build();
        edgeDao = db.edgeDao();
        nodeDao = db.nodeDao();
        testNode = new ZooData.Node("test-exhibit", "exhibit", "test", Arrays.asList(new String[]{"test", "tag1", "penguins"}));
        testNode2 = new ZooData.Node("test-exhibit2", "exhibit", "test2", Arrays.asList(new String[]{"tag1", "penguins"}));
        testNode3 = new ZooData.Node("test-exhibit3", "exhibit", "test3", Arrays.asList(new String[]{"penguins"}));
        long nodeID = nodeDao.insert(testNode);
        long nodeID2 = nodeDao.insert(testNode2);
        long nodeID3 = nodeDao.insert(testNode3);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void testGet() {
        ZooData.Edge testEdge = new ZooData.Edge("test-edge", "test street");
        long edgeID = edgeDao.insert(testEdge);

        ZooData.Edge edge = edgeDao.get("test-edge");
        ZooData.Node node = nodeDao.get("test-exhibit");
        assertEquals(testEdge.id, edge.id);
        assertEquals(testEdge.street, edge.street);

        assertNotNull(edgeDao.get("test-edge"));
        assertNotNull(nodeDao.get("test-exhibit"));

        assertEquals(testNode.id, node.id);
        assertEquals(testNode.kind, node.kind);
        assertEquals(testNode.name, node.name);
        assertEquals(testNode.tags, node.tags);
    }


    /**
     * Testing to see if exhibits can be added continuously without issues.
     */
    @Test
    public void testFiltered() {
        List<ZooData.Node> nodes = nodeDao.getFiltered("test");

        assertEquals(1, nodes.size());

        nodes = nodeDao.getFiltered("tag1");

        assertEquals(2, nodes.size());
    }

    /**
     * Testing to see if there are two matches when two exhibits both start with "t".
     */
    @Test
    public void testFiltered2() {
        List<ZooData.Node> nodes = nodeDao.getFiltered("t");
        assertEquals(2, nodes.size());
    }

    /**
     * Testing for no matches.
     */
    @Test
    public void testFiltered3() {

        List<ZooData.Node> nodes = nodeDao.getFiltered("m");
        assertEquals( 0, nodes.size());
    }

}
