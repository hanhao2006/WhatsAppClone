package com.example.whatappclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.whatappclone.model.MessageAdapter;
import com.example.whatappclone.model.Messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverId, messageReceiverName, messageReceiverimage, messageSendId;
    private TextView userName, userLastSeen;
    private CircleImageView userImage;
    private Toolbar chatToolBar;
    private ImageButton btnSendMessage;
    private EditText editTextMessageInput;
    private RecyclerView userMessageList;

    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        messageSendId = mAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference();

        messageReceiverId = getIntent().getExtras().get("visit_user_Id").toString();
        messageReceiverName = getIntent().getExtras().get("visit_user_Name").toString();
        messageReceiverimage = getIntent().getExtras().get("visit_user_image").toString();

       IntializeControllers();

       userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverimage).placeholder(R.drawable.profileimage).into(userImage);


        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });
    }

    private void IntializeControllers() {



        chatToolBar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custon_chat_bar,null);
        actionBar.setCustomView(actionBarView);

        userImage = findViewById(R.id.custom_profile_image);
        userName = findViewById(R.id.custom_profile_name);
        userLastSeen = findViewById(R.id.custom_profile_last_seen);
        btnSendMessage = findViewById(R.id.send_message_btn);
        editTextMessageInput = findViewById(R.id.input_message);

        messageAdapter = new MessageAdapter(messagesList);
        userMessageList = findViewById(R.id.message_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);

        userMessageList.setLayoutManager(linearLayoutManager);

        userMessageList.setAdapter(messageAdapter);
    }



    @Override
    protected void onStart() {
        super.onStart();
        reference.child("Messages").child(messageSendId).child(messageReceiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();

                        userMessageList.smoothScrollToPosition(userMessageList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void SendMessage(){
        String messageText = editTextMessageInput.getText().toString();
        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(this, "Please write your message", Toast.LENGTH_SHORT).show();
        }else{
            String messageSendRef = "Messages/" + messageSendId + "/" + messageReceiverId;
            String messageReceiverRef = "Messages/" + messageReceiverId + "/" + messageSendId;

            DatabaseReference userMessageKeyRef = reference.child("Messages").child(messageSendId)
                    .child(messageReceiverId).push();

            String messagePushId = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messageSendId);

            Map messageTextDetail = new HashMap();
            messageTextDetail.put(messageSendRef + "/" + messagePushId,messageTextBody);
            messageTextDetail.put(messageReceiverRef + "/" + messagePushId,messageTextBody);

            reference.updateChildren(messageTextDetail).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ChatActivity.this, "Message send", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ChatActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                    }

                  editTextMessageInput.setText("");
                }
            });


        }
    }
}
