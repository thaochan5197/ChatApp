package com.hihi.mybad.chatapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Signin extends Activity {

    private EditText editTextEmail, editTextPassword;
    private Button btnSignin;
    private TextView signUp;
    private ImageView logo;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signin);
        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                checkSignin(user);
            }
        };


        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        btnSignin = (Button) findViewById(R.id.btnSignin);
        signUp = (TextView) findViewById(R.id.signUp);
        logo = (ImageView) findViewById(R.id.logo);


        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(validate()){
                   showDialog("Login","Logging in....");
                   Signin();
               }
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Signin.this, Signup.class));
            }
        });

    }
    public void Signin(){
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Signin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            startActivity(new Intent(Signin.this,Detail.class));
                            finish();
                        }else{
                            Toast.makeText(Signin.this, "Login fail", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });

    }
    private void showDialog(String title, String mess) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(title);
        progressDialog.setMessage(mess);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(this, R.drawable.progress));
        progressDialog.show();
    }
    public void checkSignin(FirebaseUser user){
        if(user!=null){
            startActivity(new Intent(Signin.this, Detail.class));
            this.finish();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    public boolean validate(){
        if(editTextEmail.getText().toString().trim().isEmpty()){
            editTextEmail.setError("Please enter your email");
            return false;
        }
        if(editTextPassword.getText().toString().trim().isEmpty()){
            editTextPassword.setError("Please enter your password");
            return false;
        }
        return true;
    }
}
