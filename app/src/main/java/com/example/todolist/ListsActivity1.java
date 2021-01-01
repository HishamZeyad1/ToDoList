package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.todolist.ListsRecyclerAdapter;
//import com.lab.todolist.Adapters.TaskSearchAdapter;
import com.example.todolist.TODOList;
import com.example.todolist.TODOTask;
//import com.example.todolist.TODOTaskSearch;
import com.example.todolist.Helpers;
import com.example.todolist.ListPaddingDecoration;

import java.util.ArrayList;

public class ListsActivity1 extends AppCompatActivity {

    public ArrayList<TODOList> lists = new ArrayList<>();
    EditText et_list_create;
    RecyclerView listRecycler;
    ListsRecyclerAdapter listsRecyclerAdapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference listsRef;
    private String uid;
    private ProgressBar pc_loading;
    private EditText et_search;
    private TextView tv_listTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(ListsActivity1.this, Login.class);
            startActivity(intent);
            finish();
        }
        uid = currentUser.getUid();
        listsRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("lists");

        listRecycler = findViewById(R.id.lists_rv);
        ListPaddingDecoration dividerItemDecoration = new ListPaddingDecoration(this);
        listRecycler.addItemDecoration(dividerItemDecoration);
        listRecycler.setLayoutManager(new LinearLayoutManager(this));
        listsRecyclerAdapter = new ListsRecyclerAdapter(ListsActivity1.this, lists);
        listRecycler.setAdapter(listsRecyclerAdapter);

        et_list_create = findViewById(R.id.listCreate);

        et_list_create.setOnEditorActionListener((view, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_SEND) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Helpers.HideKeyboard(ListsActivity1.this);
                String titleText = et_list_create.getText().toString().trim();
                if (titleText.isEmpty()) {
                    et_list_create.setError("please enter title");
                    return false;
                } else {
                    AddTODOList(titleText);
                    et_list_create.getText().clear();
                }
            }
            return true;
        });

//        pc_loading = findViewById(R.id.pc_loading);
//        pc_loading.bringToFront();
//        pc_loading.setVisibility(View.VISIBLE);
        listsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pc_loading.setVisibility(View.VISIBLE);
                lists.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TODOList todoList = new TODOList();

                    todoList.setId((String) snapshot.child("id").getValue());
                    todoList.setTitle((String) snapshot.child("title").getValue());
                    if (snapshot.child("tasks").exists()) {
                        for (DataSnapshot tasksSnapshot : snapshot.child("tasks").getChildren()) {
                            todoList.getTasks().add(tasksSnapshot.getValue(TODOTask.class));
                        }
                    }

                    lists.add(todoList);
                }
                pc_loading.setVisibility(View.GONE);
                listsRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        tv_listTitle = findViewById(R.id.listTitle);
        et_search = findViewById(R.id.search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = et_search.getText().toString().trim();
                if (searchText.isEmpty()) {
                    et_list_create.setVisibility(View.VISIBLE);
                    tv_listTitle.setText("Lists:");
                    listRecycler.setAdapter(listsRecyclerAdapter);
                } else {
                    et_list_create.setVisibility(View.GONE);
                    tv_listTitle.setText("Results:");
//                    taskSearchAdapter = new TaskSearchAdapter(ListsActivity1.this, search(searchText), listsRef);
//                    listRecycler.setAdapter(taskSearchAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_SEARCH)) {
                    Helpers.HideKeyboard(ListsActivity1.this);
                }
                return true;
            }
        });

        ImageButton btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(view -> {
            onBackPressed();
        });

        TextView tv_Logout = findViewById(R.id.logout);
        tv_Logout.setOnClickListener(view -> {
            firebaseAuth.signOut();
            onBackPressed();
        });

    }

    public void AddTODOList(String titleText) {
        String listId = listsRef.push().getKey();
        TODOList newList = new TODOList(listId, titleText);
        listsRef.child(listId).setValue(newList);
        Toast.makeText(ListsActivity1.this, "to-do list has been added successfully", Toast.LENGTH_SHORT).show();
    }



}