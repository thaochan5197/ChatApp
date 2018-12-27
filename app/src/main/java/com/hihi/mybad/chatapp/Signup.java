package com.hihi.mybad.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;


public class Signup extends Activity{

    private EditText editTextEmail, editTextPassword, editTextName, editTextRePassword;
    private Button btnSignup;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    DatabaseReference databaseReference = firebaseDatabase.getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();


        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextRePassword = (EditText) findViewById(R.id.editTextRePassword);
        editTextName = (EditText) findViewById(R.id.editTextName);
        btnSignup = (Button) findViewById(R.id.btnSignup);


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(validate()){
                showDialog("Register", "Register a new account...");
                Signup();
            }
            }

        });



    }

    public void Signup() {
        final String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();
        final String name = editTextName.getText().toString();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                            uploadImage(currentUser,name,MD5(editTextPassword.getText().toString()));
                            Toast.makeText(Signup.this, "Sign up Successful", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(Signup.this, "Sign up fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showDialog(String title, String mess) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(title);
        progressDialog.setMessage(mess);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(this, R.drawable.progress));
        progressDialog.show();
    }

    private void writeNewUser(String id, String image, String name, String email, String password) {
        User user = new User(id,image, email, name, password);
        databaseReference.child(id).setValue(user);
    }


    public boolean validate() {
        if (editTextEmail.getText().toString().trim().isEmpty()) {
            editTextEmail.setError("Please enter your email");
            return false;
        }
        if (editTextName.getText().toString().trim().isEmpty()) {
            editTextName.setError("Please enter your name");
            return false;
        }
        if (editTextPassword.getText().toString().trim().isEmpty()) {
            editTextPassword.setError("Please enter your password");
            return false;
        }
        if (editTextPassword.getText().toString().trim().length() < 6) {
            editTextPassword.setError("Your password must be at least 6 characters in length");
            return false;
        }
        if (!editTextPassword.getText().toString().equals(editTextRePassword.getText().toString())) {
            editTextRePassword.setError("Your password does not match");
            return false;
        }
        return true;
    }
    public void uploadImage(final FirebaseUser user, final String name, final String password){
        Uri file = Uri.parse("android.resource://com.hihi.mybad.chatapp/drawable/profileimage");
        final StorageReference riversRef = storageReference.child(user.getUid());

        riversRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String downloadURL = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                writeNewUser(user.getUid(),downloadURL, name, user.getEmail(), password);
                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(Uri.parse(downloadURL)).build();
                user.updateProfile(userProfileChangeRequest);
            }
        });
    }
    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }
}
