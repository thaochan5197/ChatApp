package com.hihi.mybad.chatapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

public class Profile extends AppCompatActivity {

    private TextView back, editImage, changeName, changePassword;
    private Toolbar toolbar;
    private ImageView profileImage;
    private EditText oldPassword, editTextName, newPassword,rePassword, email;
    private Button btnSave;
    private static final int SELECT_PICTURE = 100;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference storageReference = firebaseStorage.getReference();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/" + currentUser.getUid());
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbarProfile);
        setSupportActionBar(toolbar);
        back = (TextView) findViewById(R.id.back);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        oldPassword = (EditText) findViewById(R.id.oldPassword);
        newPassword = (EditText) findViewById(R.id.newPassword);
        editTextName = (EditText) findViewById(R.id.editTextName);
        rePassword = (EditText) findViewById(R.id.rePassword);
        email = (EditText) findViewById(R.id.email);
        changeName = (TextView) findViewById(R.id.changeName);
        changePassword = (TextView) findViewById(R.id.changePassword);
        editImage = (TextView) findViewById(R.id.editImage);
        btnSave = (Button) findViewById(R.id.btnSave);


        email.setText(currentUser.getEmail());
        editTextName.setText(currentUser.getDisplayName());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newPassword.getVisibility()==View.VISIBLE){
                    if(validate()){
                        showDialog("","Updating...");
                        User user = new User(currentUser.getUid(),currentUser.getPhotoUrl().toString(),currentUser.getEmail(),editTextName.getText().toString(),MD5(newPassword.getText().toString()));
                        databaseReference.setValue(user);
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(editTextName.getText().toString()).build();
                        currentUser.updateProfile(userProfileChangeRequest);
                        currentUser.updatePassword(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Profile.this, "Done!!!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                finish();
                                startActivity(getIntent());

                            }
                        });

                    }
                }else {
                    if(validateName()){
                        showDialog("","Updating...");
                        SharedPreferences prefs = getApplicationContext().getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
                        String currentPassơrd = prefs.getString("key",null);
                        User user = new User(currentUser.getUid(),currentUser.getPhotoUrl().toString(),currentUser.getEmail(),editTextName.getText().toString(),currentPassơrd);
                        databaseReference.setValue(user);
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(editTextName.getText().toString()).build();
                        currentUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                finish();
                                startActivity(getIntent());
                            }
                        });

                    }
                }
            }
        });
        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeName.setVisibility(View.INVISIBLE);
                editTextName.setEnabled(true);
                btnSave.setVisibility(View.VISIBLE);
            }
        });
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword.setVisibility(View.INVISIBLE);
                oldPassword.setEnabled(true);
                oldPassword.setText("");
                newPassword.setVisibility(View.VISIBLE);
                rePassword.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.VISIBLE);
            }
        });
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Picasso.with(getApplicationContext()).load(user.getImage()).into(profileImage);
                getPassword(user.getPassword());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    Bitmap bitmap = null;
                    try {
                        Uri uri = data.getData();
                        uploadImage(uri);
//                        databaseReference.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                User user = dataSnapshot.getValue(User.class);
//                                Picasso.with(getApplicationContext()).load(user.getImage()).into(profileImage);
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                   // profileImage.setImageURI(currentUser.getPhotoUrl());

                }
            }
        }
    }

    public void uploadImage(Uri uri) {
        final SharedPreferences prefs = getApplicationContext().getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        final StorageReference riversRef = storageReference.child(currentUser.getUid());
        riversRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String downloadURL = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                User user = new User(currentUser.getUid(), downloadURL, currentUser.getEmail(), currentUser.getDisplayName(), prefs.getString("key",null));
                databaseReference.setValue(user);
                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse(downloadURL.toString())).build();
                currentUser.updateProfile(userProfileChangeRequest);
            }
        });
    }
    public boolean validateName(){
        if (editTextName.getText().toString().trim().isEmpty()) {
            editTextName.setError("Please enter your name");
            return false;
        }
        return true;
    }
    public boolean validate() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        String currentPassơrd = prefs.getString("key",null);
        if (editTextName.getText().toString().trim().isEmpty()) {
            editTextName.setError("Please enter your name");
            return false;
        }
        if (oldPassword.getText().toString().trim().isEmpty()) {
            oldPassword.setError("Please enter your password");
            return false;
        }
       if(!MD5(oldPassword.getText().toString()).equals(currentPassơrd)){
           oldPassword.setError("Wrong password");
           return false;
       }
        if (newPassword.getText().toString().trim().length() < 6) {
            newPassword.setError("Your password must be at least 6 characters in length");
            return false;
        }
        if (!rePassword.getText().toString().equals(newPassword.getText().toString())) {
            rePassword.setError("Your password does not match");
            return false;
        }
        return true;
    }
    public void getPassword(String password){
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("key", password);
        editor.commit();
    }

    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
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
}
