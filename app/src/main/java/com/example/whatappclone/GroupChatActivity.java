package com.example.whatappclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton sendMessageButton;
    private EditText editTextInputMessage;
    private ScrollView mScrollView;
    private TextView textViewDisplayMessage;

    // get intent from group Fragment
    private String currentGroupName, currentUserId, currentUserName, currentDate,currentTime;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference, databaseReferenceGroupName, groupMessageKeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        Initialize();
        // get intent from group Fragment
        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(this,currentGroupName,Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReferenceGroupName = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);
        GetUserInfo();
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveMessageInfoToDatabase();
                editTextInputMessage.setText("");
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });


    }



    // get intent from group Fragment
    private void GetUserInfo() {

        databaseReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private void Initialize() {

        toolbar = findViewById(R.id.Group_chat_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentGroupName);

        sendMessageButton = findViewById(R.id.btnSendMessage);
        editTextInputMessage = findViewById(R.id.input_group_message);
        textViewDisplayMessage = findViewById(R.id.textViewGroup_chat_display);
        mScrollView = findViewById(R.id.my_scroll_view);

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseReferenceGroupName.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    displayMessage(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists()){
                    displayMessage(dataSnapshot);
                }
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

    private void displayMessage(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while(iterator.hasNext()){
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();

            textViewDisplayMessage.append(chatName + " :\n" + chatMessage + "\n" + chatTime + "   " + chatDate + "\n\n\n");

            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }

    }

    private void SaveMessageInfoToDatabase() {

        String message = editTextInputMessage.getText().toString();
        String messageId = databaseReferenceGroupName.push().getKey();

        if(TextUtils.isEmpty(message)){
            Toast.makeText(this, "Please Write message", Toast.LENGTH_SHORT).show();
        }else{
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd,yyyy");
            currentDate = currentDateFormat.format(calendar.getTime());

            Calendar calendarTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calendarTime.getTime());

            HashMap<String,Object> groupMessageKey = new HashMap<>();
            databaseReferenceGroupName.updateChildren(groupMessageKey);
            groupMessageKeyRef = databaseReferenceGroupName.child(messageId);

            HashMap<String,Object> messageInfo = new HashMap<>();
            messageInfo.put("name",currentUserName);
            messageInfo.put("message",message);
            messageInfo.put("date",currentDate);
            messageInfo.put("time",currentTime);
            groupMessageKeyRef.updateChildren(messageInfo);
           // textViewDisplayMessage.setText(message);


        }
    }
}


