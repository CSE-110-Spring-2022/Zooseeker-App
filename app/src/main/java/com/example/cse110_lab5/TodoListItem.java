package com.example.cse110_lab5;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

//EDIT THIS TO MATCH ZOOSEEKER
@Entity(tableName = "todo_list_items")
public class TodoListItem {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String text;
    public boolean completed;
    public int order;

    @Override
    public String toString() {
        return "TodoListItem{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", completed=" + completed +
                ", order=" + order +
                '}';
    }

    TodoListItem(String text, boolean completed, int order){
        this.text = text;
        this.completed = completed;
        this.order = order;
    }
    // 3. Factory method for loading our JSON.
    public static List<TodoListItem> loadJSON(Context context, String path) {
        try {
            InputStream input = context.getAssets().open(path);
            Reader reader = new InputStreamReader(input);
            Gson gson = new Gson();
            Type type = new TypeToken<List<TodoListItem>>(){}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections. emptyList();
        }
    }
}
