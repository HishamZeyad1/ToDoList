package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity {
    RecyclerView tasks_rv;
    TaskAdapterEx taskAdapter;
    static List<TaskItem> tasksList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);


// add task items to the list
        tasksList.add(new TaskItem("Study Programming",false));
        tasksList.add(new TaskItem("Study",true));
        tasksList.add(new TaskItem("Programming",true));


        tasks_rv = findViewById(R.id.tasks_rv);
        tasks_rv.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapterEx(this ,tasksList );
        tasks_rv.setAdapter(taskAdapter);




    }




}