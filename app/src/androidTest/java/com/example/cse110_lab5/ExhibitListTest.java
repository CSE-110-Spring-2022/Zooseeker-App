package com.example.cse110_lab5;

        import static org.junit.Assert.assertEquals;
        import static org.junit.Assert.assertFalse;
        import static org.junit.Assert.assertNotNull;
        import static org.junit.Assert.assertNull;
        import static org.junit.Assert.assertTrue;

        import android.content.Context;
        import android.view.View;
        import android.widget.Button;
        import android.widget.CheckBox;
        import android.widget.EditText;

        import androidx.lifecycle.Lifecycle;
        import androidx.recyclerview.widget.RecyclerView;
        import androidx.room.Room;
        import androidx.test.core.app.ActivityScenario;
        import androidx.test.core.app.ApplicationProvider;

        import com.example.cse110_lab5.database.EdgeDao;
        import com.example.cse110_lab5.database.GraphDatabase;
        import com.example.cse110_lab5.database.NodeDao;
        import com.example.cse110_lab5.database.ZooData;

        import org.junit.Before;
        import org.junit.Test;

        import java.util.List;

public class ExhibitListTest {
    GraphDatabase testDb;

    NodeDao nodeDao;
    EdgeDao edgeDao;


    private static void forceLayout(RecyclerView recyclerView) {
        recyclerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        recyclerView.layout(0, 0, 1080, 2280);
    }

    @Before
    public void resetDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, GraphDatabase.class)
                .allowMainThreadQueries()
                .build();
        GraphDatabase.injectTestDatabase(testDb);

        List<ZooData.Node> nodes = ZooData.Node.loadJSON(context, "sample_node_info.json");
        nodeDao = testDb.nodeDao();
        nodeDao.insertAll(nodes);

        List<ZooData.Edge> edges = ZooData.Edge.loadJSON(context, "sample_edge_info.json");
        edgeDao = testDb.edgeDao();
        edgeDao.insertAll(edges);
    }

    @Test
    public void testCheckoffTodo() {
        ActivityScenario<MainActivity> scenario
                = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.recyclerView;
            RecyclerView.ViewHolder firstVH = recyclerView.findViewHolderForAdapterPosition(0);
            assertNotNull(firstVH);
            long id = firstVH.getItemId();

            CheckBox todoText = firstVH.itemView.findViewById(R.id.selected);

            if(todoText.isChecked()) {
                todoText.performClick();
                ZooData.Node editedItem = nodeDao.get("gorillas");
                assertFalse(editedItem.selected);
            } else {
                todoText.performClick();
                ZooData.Node editedItem = nodeDao.get("gorillas");
                assertTrue(editedItem.selected);
            }
        });
    }
}
