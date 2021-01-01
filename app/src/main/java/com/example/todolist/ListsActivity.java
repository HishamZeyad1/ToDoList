package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListsActivity extends AppCompatActivity {
    TextView logout;
    //    Button addNewList;
    EditText listCreate;
    ArrayList<TODOList> Lists = new ArrayList<TODOList>();
    FirebaseAuth mAuth;
    TaskSearchAdapter taskSearchAdapter;
    ListsRecyclerAdapter listsRecyclerAdapter;
    RecyclerView lists_rv;
    private ProgressBar pc_loading;

    private DatabaseReference listsRef;


    private FirebaseUser currentUser;
    private String uid;
    private EditText search;
    private TextView tv_listTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();
        listsRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("lists");

        lists_rv = findViewById(R.id.lists_rv);
        ListPaddingDecoration dividerItemDecoration = new ListPaddingDecoration(this);
        lists_rv.addItemDecoration(dividerItemDecoration);
        lists_rv = findViewById(R.id.lists_rv);
        lists_rv.setLayoutManager(new LinearLayoutManager(this));
        listsRecyclerAdapter = new ListsRecyclerAdapter((Activity) ListsActivity.this, Lists);
        lists_rv.setAdapter(listsRecyclerAdapter);

        listCreate = findViewById(R.id.listCreate);
//        listCreate.setOnEditorActionListener((view, actionId, event) -> {
//            mAuth = FirebaseAuth.getInstance();
//            FirebaseUser user = mAuth.getCurrentUser();
//            String uid = user.getUid();
//
//            TODOList newList= new TODOList();
//            newList.setTitle(listCreate.getText().toString());
//
//            String listId= FirebaseDatabase.getInstance().getReference("Users").child(uid).child("lists").push().getKey();
//            newList.setId(listId);
//            FirebaseDatabase.getInstance().getReference("Users").child(uid).child("lists").child(listId).setValue(newList);
//            Toast.makeText(ListsActivity.this,"task has been added successfully", Toast.LENGTH_SHORT).show();
//            listCreate.setText(" ");
//            return false;
//        });
        listCreate.setOnEditorActionListener((view, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_SEND) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Helpers.HideKeyboard(ListsActivity.this);
                String titleText = listCreate.getText().toString().trim();
                if (titleText.isEmpty()) {
                    listCreate.setError("please enter title");
                    return false;
                } else {
                    AddTODOList(titleText);
                    listCreate.getText().clear();
                }
            }
            return true;
        });


        pc_loading = findViewById(R.id.pc_loading);
        pc_loading.bringToFront();
        pc_loading.setVisibility(View.VISIBLE);
        listsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pc_loading.setVisibility(View.VISIBLE);
                Lists.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TODOList todoList = new TODOList();

                    todoList.setId((String) snapshot.child("id").getValue());
                    todoList.setTitle((String) snapshot.child("title").getValue());
                    if (snapshot.child("tasks").exists()) {
                        for (DataSnapshot tasksSnapshot : snapshot.child("tasks").getChildren()) {
                            todoList.getTasks().add(tasksSnapshot.getValue(TODOTask.class));
                        } }
                    Lists.add(todoList);
                }
                pc_loading.setVisibility(View.GONE);
                listsRecyclerAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {} });


        mAuth= FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        listsRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("lists");
//        listCreate = findViewById(R.id.listCreate);




        listsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pc_loading.setVisibility(View.VISIBLE);
                Lists.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TODOList todoList = new TODOList();

                    todoList.setId((String) snapshot.child("id").getValue());
                    todoList.setTitle((String) snapshot.child("title").getValue());
                    if (snapshot.child("tasks").exists()) {
                        for (DataSnapshot tasksSnapshot : snapshot.child("tasks").getChildren()) {
                            todoList.getTasks().add(tasksSnapshot.getValue(TODOTask.class));
                        }
                    }

                    Lists.add(todoList);
                }
                pc_loading.setVisibility(View.GONE);
                listsRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        tv_listTitle = findViewById(R.id.listTitle);
        search = findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = search.getText().toString().trim();
                if (searchText.isEmpty()) {
                    listCreate.setVisibility(View.VISIBLE);
                    tv_listTitle.setText("Lists:");
                    lists_rv.setAdapter(listsRecyclerAdapter);
                } else {
                    listCreate.setVisibility(View.GONE);
                    tv_listTitle.setText("Results:");
                    taskSearchAdapter = new TaskSearchAdapter(ListsActivity.this, search(searchText), listsRef);
                    lists_rv.setAdapter(taskSearchAdapter);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_SEARCH)) {
                    Helpers.HideKeyboard(ListsActivity.this);
                }
                return true;
            }
        });


        ImageButton btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(view -> {
            onBackPressed();
        });



        logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                Toast.makeText(ListsActivity.this,"signed out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ListsActivity.this, Login.class);
                startActivity(intent);
            }
        });
    }

    public void AddTODOList(String titleText) {
        String listId = listsRef.push().getKey();
        TODOList newList = new TODOList(listId, titleText);
        listsRef.child(listId).setValue(newList);
        Toast.makeText(ListsActivity.this, "to-do list has been added successfully", Toast.LENGTH_SHORT).show();
    }



    public ArrayList<TODOTaskSearch> search(String text) {
        ArrayList<TODOTaskSearch> result = new ArrayList<TODOTaskSearch>();
        int listCount = Lists.size();
        for (int i = 0; i < listCount; i++) {
            TODOList list = Lists.get(i);
            int tasksCount = list.getTasks().size();
            for (int j = 0; j < tasksCount; j++) {
                TODOTask task = list.getTasks().get(j);
                if (task.getTitle().toLowerCase().contains(text.toLowerCase())) {
                    result.add(new TODOTaskSearch(list.getId(), task.getId(), list.getTitle(), task.getTitle(), task.isChecked()));
                }
            }
        }
        return result;
    }
}
