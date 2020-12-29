package com.example.todolist;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.TaskAdapterEx;
import com.example.todolist.TaskItem;

import java.util.ArrayList;
import java.util.List;

public class Main1Activity extends AppCompatActivity {
    TextView back,yourEmail;
    String emailFromIntent;
    RecyclerView tasks_rv;
    TaskAdapterEx taskAdapter;
    static List<TaskItem> tasksList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        back = findViewById(R.id.back);
        yourEmail = findViewById(R.id.yourEmail);

        emailFromIntent = getIntent().getStringExtra("email");
        yourEmail.setText(emailFromIntent);

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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