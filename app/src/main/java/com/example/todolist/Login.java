package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private TextView toSignUp;
    private TextView create_account ;

    TextView signuptv;
    EditText email,password;
    TextView login;
    FirebaseAuth mAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password= findViewById(R.id.password);
        login = findViewById(R.id.login);

        create_account = findViewById(R.id.create_account);

        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();}});
//




        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login.this.Login(email.getText().toString(), password.getText().toString());
            }
        });}
    private void Login(String email, String password) {
    mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) {


                if (task.isSuccessful()) {
                    Intent intent = new Intent(Login.this, ListsActivity.class);
                    startActivity(intent);
    //
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(Login.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    }