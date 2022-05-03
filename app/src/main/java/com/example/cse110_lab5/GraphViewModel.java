package com.example.cse110_lab5;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.cse110_lab5.database.EdgeDao;
import com.example.cse110_lab5.database.GraphDatabase;
import com.example.cse110_lab5.database.NodeDao;
import com.example.cse110_lab5.template.TodoListItem;

import java.util.List;

public class GraphViewModel extends AndroidViewModel {
    private LiveData<List<TodoListItem>> todoListItems;
    private final EdgeDao edgeDao;
    private final NodeDao nodeDao;

    public GraphViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        GraphDatabase db = GraphDatabase.getSingleton(context);
        edgeDao = db.edgeDao();
        nodeDao = db.nodeDao();
    }

    //ADAPT THIS FOR OUR NEW DATABASE
    /**
    public LiveData<List<TodoListItem>> getTodoListItems() {
        if (todoListItems == null) {
            loadUsers();
        }
        return todoListItems;
    }


    public void toggleCompleted (Node node, Edge edge) {
        node.completed = !todoListItem.completed;
        todoListItemDao.update(todoListItem);

        todoListItem.completed = !todoListItem.completed;
        todoListItemDao.update(todoListItem);
    }


    private void loadUsers() {
        todoListItems = todoListItemDao.getAllLive();
    }

    public void updateText(TodoListItem todoListItem, String newText) {
        todoListItem.text = newText;
        todoListItemDao.update(todoListItem);
    }
    public void createTodo(String text) {
        int endOfListOrder = todoListItemDao.getOrderForAppend();
        TodoListItem newItem = new TodoListItem(text, false, endOfListOrder);
        todoListItemDao.insert(newItem);
    }
    public void deleteTodo(TodoListItem todoListItem){
        todoListItemDao.delete(todoListItem);
    }

     */
}
