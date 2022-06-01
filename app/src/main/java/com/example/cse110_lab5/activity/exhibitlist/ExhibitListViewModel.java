package com.example.cse110_lab5.activity.exhibitlist;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.cse110_lab5.database.GraphDatabase;
import com.example.cse110_lab5.database.NodeDao;
import com.example.cse110_lab5.database.ZooData;

import java.util.List;

public class ExhibitListViewModel extends AndroidViewModel {
    private LiveData<List<ZooData.Node>> todoListItems;
    private final NodeDao todoListItemDao;

    public ExhibitListViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        GraphDatabase db = GraphDatabase.getSingleton(context);
        todoListItemDao = db.nodeDao();
    }

    public LiveData<List<ZooData.Node>> getTodoListItems() {
        if (todoListItems == null) {
            loadUsers();
        }

        return todoListItems;
    }

    private void loadUsers() {
        todoListItems = todoListItemDao.getAllExhibitLive();
    }

    public LiveData<List<String>> getSelectedItems() {
        return todoListItemDao.getSelectedLive();
    }

    public void toggleSelected(ZooData.Node zooDataNode) {
        zooDataNode.selected = !zooDataNode.selected;
        todoListItemDao.update(zooDataNode);
    }

}
