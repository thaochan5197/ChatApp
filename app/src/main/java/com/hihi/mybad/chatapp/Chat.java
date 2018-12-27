package com.hihi.mybad.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Chat extends AppCompatActivity {

    private EditText newMessage;
    private TextView send,titleName,back;
    private ListView listViewMessage;
    private Toolbar toolbar;
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private Date date = new Date();
    private MessagesAdapter messagesAdapter;
    private ArrayList<Message> listMessage;
    private ValueEventListener valueEventListener;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("chat");
    DatabaseReference databaseReferenceChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = (Toolbar) findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        final User user = (User) intent.getSerializableExtra("user");

        firebaseAuth = FirebaseAuth.getInstance();

        newMessage = (EditText) findViewById(R.id.newMessage);
        send = (TextView) findViewById(R.id.send);
        titleName = (TextView) findViewById(R.id.titleName);
        back = (TextView) findViewById(R.id.back);

        listViewMessage = (ListView) findViewById(R.id.listViewMessage);
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        titleName.setText(user.getName());

        listMessage = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(listMessage);
        listViewMessage.setAdapter(messagesAdapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message();
                date = new Date();
                String time = simpleDateFormat.format(date);
                message.setMess(newMessage.getText().toString());
                message.setDate(time);
                message.setSender(currentUser.getUid());
                writeChat(currentUser,user,message);
                newMessage.setText("");
            }
        });
        if(currentUser.getUid().toString().compareTo(user.getId())<0){
            databaseReferenceChat = FirebaseDatabase.getInstance().getReference("chat/"+currentUser.getUid().toString()+"-"+user.getId());
        }else{
            databaseReferenceChat = FirebaseDatabase.getInstance().getReference("chat/"+user.getId()+"-"+currentUser.getUid().toString());
        }


        databaseReferenceChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                listMessage.add(message);
                messagesAdapter.notifyDataSetChanged();
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


    public void writeChat(FirebaseUser firebaseUseruser,User receiver, Message message) {
        if(firebaseUseruser.getUid().toString().compareTo(receiver.getId())<0){
            databaseReference.child(firebaseUseruser.getUid()+"-"+receiver.getId()).push().setValue(message);
        }else{
            databaseReference.child(receiver.getId()+"-"+firebaseUseruser.getUid()).push().setValue(message);
        }

    }
    private class MessagesAdapter extends ArrayAdapter<Message> {
        MessagesAdapter(ArrayList<Message> messages) {
            super(Chat.this, R.layout.message, R.id.message, messages);
    }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = super.getView(position, convertView, parent);
            Message message = getItem(position);

            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.layout);
            TextView nameView = (TextView) convertView.findViewById(R.id.message);
            final TextView time = (TextView) convertView.findViewById(R.id.time);
            nameView.setText(message.getMess());
            time.setText(message.getDate());

            nameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(time.getVisibility() == View.VISIBLE){
                        time.setVisibility(View.INVISIBLE);
                    }else{
                        time.setVisibility(View.VISIBLE);
                    }
                }
            });

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if(user.getUid().toString().equals(message.getSender())){
                nameView.setBackgroundResource(R.drawable.chat_mess_blue);
                nameView.setTextColor(Color.parseColor("#FFFFFF"));
                layoutParams.gravity = Gravity.RIGHT;
            }else {
                nameView.setBackgroundResource(R.drawable.chat_gray);
                nameView.setTextColor(Color.parseColor("#222222"));
                layoutParams.gravity = Gravity.LEFT;

            }
            nameView.setLayoutParams(layoutParams);
            return convertView;
        }
    }
}
