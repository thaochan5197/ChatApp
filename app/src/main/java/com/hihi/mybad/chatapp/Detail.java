package com.hihi.mybad.chatapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Detail extends AppCompatActivity {

    private TextView contact, signOut,editProfile;
    private EditText search;
    private ListView listViewName;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private ValueEventListener valueEventListener;
    private ArrayList<User> listUser = new ArrayList<>();
    private ListUserAdapter listUserAdapter;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        showDialog("","Loading...");

        signOut = (TextView) findViewById(R.id.signOut);
        contact = (TextView) findViewById(R.id.contact);
        editProfile = (TextView) findViewById(R.id.editProfile);
        listViewName = (ListView) findViewById(R.id.listViewName);
        search = (EditText) findViewById(R.id.search);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        firebaseAuth = FirebaseAuth.getInstance();
        listUserAdapter = new ListUserAdapter(Detail.this,R.layout.layout_row_detail, listUser);
        listViewName.setAdapter(listUserAdapter);

        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        loadData(currentUser);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listUser.clear();
               loadData(currentUser);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Detail.this,Profile.class));
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(Detail.this,Signin.class));

            }
        });

        listViewName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = listUser.get(position);
                Intent intent = new Intent(Detail.this, Chat.class);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });

    }

    public void loadData(final FirebaseUser firebaseUser){
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                if(!user.getId().equals(firebaseUser.getUid())){
                    listUser.add(user);
                    if(!user.getName().contains(search.getText().toString()) && !user.getEmail().contains(search.getText().toString())){
                        listUser.remove(user);
                    }
                    listUserAdapter.notifyDataSetChanged();
                }
                progressDialog.dismiss();
                contact.setText("  CONTACTS ("+listUser.size()+")");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
}
