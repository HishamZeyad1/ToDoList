package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity {
    RecyclerView tasks_rv;
//    TaskAdapterEx taskAdapter;
    static List<TaskItem> tasksList = new ArrayList<>();
    EditText listCreate;
    TextView listTitle;
    RecyclerView taskRecycler;
    TasksRecyclerAdapter tasksRecyclerAdapter;
    TaskSearchAdapter taskSearchAdapter;
    TODOList todoList = new TODOList();
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    DatabaseReference listRef;
    String uid;
    ProgressBar pc_loading;
    TextView tv_delete_list,TaskCreate;
    String titleText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(TaskActivity.this, Login.class);
            startActivity(intent);
            finish();
        }
        uid = currentUser.getUid();

        String listId = getIntent().getStringExtra("listId");
        listRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("lists").child(listId);

        taskRecycler = findViewById(R.id.tasks_rv);
        ListPaddingDecoration dividerItemDecoration = new ListPaddingDecoration(this);
        taskRecycler.addItemDecoration(dividerItemDecoration);
        taskRecycler.setLayoutManager(new LinearLayoutManager(this));
        tasksRecyclerAdapter = new TasksRecyclerAdapter(TaskActivity.this, todoList, listRef);
        taskRecycler.setAdapter(tasksRecyclerAdapter);

        TaskCreate = findViewById(R.id.TaskCreate);
        TaskCreate.setOnEditorActionListener((view, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_SEND) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Helpers.HideKeyboard(TaskActivity.this);

                titleText = TaskCreate.getText().toString().trim();
                if (titleText.isEmpty()) {
                    TaskCreate.setError("please enter title");

                    return false;
                } else {
                    AddTODOList(titleText);
//                    TaskCreate.getText().clear();
                }
            }
            return true;
        });


        listTitle = findViewById(R.id.listTitle);
        pc_loading = findViewById(R.id.pc_loading);
        pc_loading.bringToFront();
        pc_loading.setVisibility(View.VISIBLE);
        listRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pc_loading.setVisibility(View.VISIBLE);
                if (!dataSnapshot.exists()) return;
                todoList.setId((String) dataSnapshot.child("id").getValue());
                todoList.setTitle((String) dataSnapshot.child("title").getValue());
                todoList.getTasks().clear();
                if (dataSnapshot.child("tasks").exists()) {
                    for (DataSnapshot tasksSnapshot : dataSnapshot.child("tasks").getChildren()) {
                        todoList.getTasks().add(tasksSnapshot.getValue(TODOTask.class));
                    }
                }
                listTitle.setText(todoList.getTitle() + " List");
                pc_loading.setVisibility(View.GONE);
                tasksRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        tv_delete_list = findViewById(R.id.delete_list);
        tv_delete_list.setOnClickListener(view -> {
            listRef.removeValue();
            onBackPressed();
        });
        ImageButton btn_back = findViewById(R.id.btn_back);
        ImageButton btn_search = findViewById(R.id.btn_search);
        btn_back.setOnClickListener(view -> onBackPressed());
        btn_search.setOnClickListener(view -> onBackPressed());


// add task items to the list
//        tasksList.add(new TaskItem("Study Programming",false));
//        tasksList.add(new TaskItem("Study",true));
//        tasksList.add(new TaskItem("Programming",true));
//        tasks_rv = findViewById(R.id.tasks_rv);
//        tasks_rv.setLayoutManager(new LinearLayoutManager(this));
//        taskAdapter = new TaskAdapterEx(this ,tasksList );
//        tasks_rv.setAdapter(taskAdapter);



    }

    private void AddTODOList(String titleText) {
        String taskId = listRef.child("tasks").push().getKey();
        TODOTask newTask = new TODOTask(taskId, titleText, "2020-20-20 : 20:20", "desc", false);
        listRef.child("tasks").child(taskId).setValue(newTask);
        Toast.makeText(TaskActivity.this, "to-do task has been added successfully", Toast.LENGTH_SHORT).show();
    }


}