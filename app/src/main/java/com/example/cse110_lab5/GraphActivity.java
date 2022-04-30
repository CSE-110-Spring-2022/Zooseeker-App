package com.example.cse110_lab5;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GraphActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    private GraphViewModel viewModel;
    private EditText newTodoText;
    private Button addTodoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        List<Node> nodes = Node.loadJSON(this, "sample_node_info.json");
        List<Edge> edges = Edge.loadJSON(this, "sample_edge_info.json");

        Log.d("Nodes", nodes.toString());
        Log.d("Edges", edges.toString());

        /**
        viewModel = new ViewModelProvider(this)
                .get(GraphViewModel.class);


        TodoListAdapter adapter = new TodoListAdapter();
        adapter.setHasStableIds(true);
        adapter.setOnCheckBoxClickedHandler(viewModel::toggleCompleted);
        adapter.setOnDeleteClickedHandler(viewModel::deleteTodo);
        adapter.setOnTextEditedHandler(viewModel::updateText);
        viewModel.getTodoListItems().observe(this, adapter::setTodoListItems);

        recyclerView = findViewById(R.id.todo_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        this.newTodoText = this.findViewById(R.id.new_todo_text);
        this.addTodoButton = this.findViewById(R.id.add_todo_btn);

        addTodoButton.setOnClickListener(this::onAddTodoClicked);

        //adapter.setTodoListItems(TodoListItem.loadJSON(this, "demo_todos.json"));
        */
    }
    /**
    void onAddTodoClicked(View view) {
        String text = newTodoText.getText().toString();
        newTodoText.setText("");
        viewModel.createTodo(text);
    }
     */
}