package com.example.cse110_lab5;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {TodoListItem.class}, version = 1)
public abstract class TodoDatabase extends RoomDatabase {
    private static TodoDatabase singleton = null;

    public abstract TodoListItemDao todoListItemDao();

    public synchronized static TodoDatabase getSingleton(Context context) {
        if(singleton == null) {
            singleton = TodoDatabase.makeDatabase(context);
        }
        return singleton;
    }

    private static TodoDatabase makeDatabase(Context context) {
        return Room.databaseBuilder(context, TodoDatabase.class, "todo_app.db")
                .allowMainThreadQueries()
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(() -> {
                            List<TodoListItem> todos = TodoListItem
                                    .loadJSON(context, "demo_todos.json");
                            getSingleton(context).todoListItemDao().insertAll(todos);

                        });
                    }
                })
                .build();
    }

    @VisibleForTesting
    public static void injectTestDatabase(TodoDatabase testDatabase) {
        if(singleton != null ) {
            singleton.close();
        }

        singleton = testDatabase;
    }
}
