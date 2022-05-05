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

@RunWith(AndroidJUnit4.class)
public class TodoDatabaseTest {
    private EdgeDao edgeDao;
    private NodeDao nodeDao;
    private GraphDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, GraphDatabase.class)
                .allowMainThreadQueries()
                .build();
        edgeDao = db.edgeDao();
        nodeDao = db.nodeDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    /*
    @Test
    public void testInsert() {
        TodoListItem item1 = new TodoListItem("Pizza time", false, 0);
        TodoListItem item2 = new TodoListItem("Photos of Spider-Man", false, 1);

        long id1 = dao.insert(item1);
        long id2 = dao.insert(item2);

        assertNotEquals(id1, id2);
    }
*/
    @Test
    public void testGet() {
        ZooData.Edge testEdge = new ZooData.Edge("test-edge", "test street");
        ZooData.Node testNode = new ZooData.Node("test-exhibit", "exhibit", "test", Arrays.asList(new String[]{"test", "tag1", "penguins"}));
        long edgeID = edgeDao.insert(testEdge);
        long nodeID = nodeDao.insert(testNode);

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

   /* @Test
    public void testUpdate() {
        TodoListItem item = new TodoListItem("Pizza time", false, 0);
        long id = dao.insert(item);

        item = dao.get(id);
        item.text = "Photos of Spider-Man";
        int itemsUpdated = dao.update(item);
        assertEquals(1, itemsUpdated);

        item = dao.get(id);
        assertNotNull(item);
        assertEquals("Photos of Spider-Man", item.text);
    }*/

    /*
    @Test
    public void testDelete() {
        TodoListItem item = new TodoListItem("Pizza time", false, 0);
        long id = dao.insert(item);

        item = dao.get(id);
        int itemsDeleted = dao.delete(item);
        assertEquals(1, itemsDeleted);
        assertNull(dao.get(id));
    }*/
}
