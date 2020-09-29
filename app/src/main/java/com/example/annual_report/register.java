package com.example.annual_report;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class register extends AppCompatActivity {

    EditText email,password,password2;
    Button button;
    private FirebaseDatabase rootNode;
    FirebaseAuth mAuth;
    int maxid = 0;
    mem_add madd;
    private DatabaseReference reference ;
    Button button2;
    ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    //private static int register_screen = 4500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity2();
            }
        });
        //getSupportActionBar().setTitle("Register Form");


        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        password2 = (EditText) findViewById(R.id.password2);
        button = (Button) findViewById(R.id.button);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        madd = new mem_add();
        firebaseAuth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    maxid = (int) dataSnapshot.getChildrenCount();
                } else {
                    //
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txtemail = email.getText().toString().trim();
                String txtpassword = password.getText().toString().trim();
                String txtpassword2 = password2.getText().toString().trim();


                if (TextUtils.isEmpty(txtemail)) {
                    Toast.makeText(register.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(txtpassword)) {
                    Toast.makeText(register.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(txtpassword2)) {
                    Toast.makeText(register.this, "Please Enter Confirm Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (txtpassword.length() < 6) {
                    Toast.makeText(register.this, "Password too short", Toast.LENGTH_SHORT).show();
                }

              //  progressBar.setVisibility(View.VISIBLE);
                else{
                    firebaseAuth.createUserWithEmailAndPassword(txtemail, txtpassword)
                            .addOnCompleteListener(register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    progressBar.setVisibility(View.GONE);

                                    if (task.isSuccessful()) {
                                        Intent i = new Intent(register.this, login.class);
                                        startActivity(i);


                                    } else {

                                        Toast.makeText(register.this, "Authentication Failed or User already exists", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                }


            }

        });
        //    public void openActivity2() {
//        Intent i = new Intent(register.this, login.class);
//        startActivity(i);

    }
    private void openActivity2() {
        Intent i = new Intent(register.this, login.class);
        startActivity(i);

    }
}

