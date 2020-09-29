package com.example.annual_report;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;

public class login extends AppCompatActivity {

    EditText email,password;
    Button button2;
    Button button;
    ProgressBar progressBar;
    TextView mCreateBtn, forgotTextLink;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG = "login";
    private FirebaseAuth firebaseAuth;
    private static final String KEY_EMAIL="email";
    //private DatabaseReference databaseReference;
    //private FirebaseUser user;
    private Button button3;
    private int RC_SIGN_IN = 1;
    String userEmailToken;

    //String userId = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        signInButton =  findViewById(R.id.button3);
        firebaseAuth =  FirebaseAuth.getInstance();

        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);


        //databaseReference.child(userId).push().setValue(databaseReference);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity2();
            }
        });

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        button2 = (Button) findViewById((R.id.button2));
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        firebaseAuth = FirebaseAuth.getInstance();
        forgotTextLink = findViewById(R.id.forgotpassword);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtemail = email.getText().toString().trim();
                String txtpassword = password.getText().toString().trim();

                String username = email.getText().toString();

                if(TextUtils.isEmpty(txtemail)){
                    Toast.makeText(login.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(txtpassword)){
                    Toast.makeText(login.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(txtemail, txtpassword)
                        .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                    int at_index = firebaseUser.getEmail().indexOf('@');
                                    userEmailToken = firebaseUser.getEmail().substring(0,at_index);
                                    SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putBoolean("isUserLogin", true);
                                    editor.putString("ShrEmail",firebaseUser.getEmail());
                                    editor.putString("userEmailKey",userEmailToken);
                                    editor.commit();
                                    startActivity(new Intent(getApplicationContext(),navigation.class).putExtra("doremonuser",firebaseUser.getEmail()));
                                    Toast.makeText(login.this,"Signed in successfully",Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(login.this,"Login Failed or User not Available", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                progressBar.setVisibility(View.VISIBLE);
            }
        });

        forgotTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter Your Email To Received Reset Link.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String mail = resetMail.getText().toString();
                        firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(login.this, "Reset Link Sent To Your Email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(login.this, "Error ! Reset Link is Not Sent. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                    }
                });

                passwordResetDialog.create().show();
            }
        });

    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
    if(requestCode == RC_SIGN_IN){
        handleSignInResult(task);
    }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try{
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            //Toast.makeText(login.this,"Signed in successfully",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }

        catch (ApiException e){
            Toast.makeText(login.this,"Signed in Failed",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(),null);
            firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Toast.makeText(login.this,"Signed in successfully",Toast.LENGTH_SHORT).show();
                        updateUI(user);
                    }
                    else {
                        Toast.makeText(login.this,"Failed",Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }
            });
    }

    private void updateUI(FirebaseUser fUser){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(fUser != null){
            int at_index = fUser.getEmail().indexOf('@');
            userEmailToken = fUser.getEmail().substring(0,at_index);
            System.out.println(userEmailToken);
            Toast.makeText(this, userEmailToken, Toast.LENGTH_SHORT).show();
            SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isUserLogin", true);
            editor.putString("ShrEmail",fUser.getEmail());
            editor.putString("userEmailKey",userEmailToken);
            editor.commit();
            startActivity(new Intent(getApplicationContext(),navigation.class).putExtra("doremonuser",fUser.getEmail()));


        }
    }

    public void openActivity2(){
        Intent intent = new Intent(this, register.class);
        startActivity(intent);
    }
}