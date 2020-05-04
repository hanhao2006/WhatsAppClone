package com.example.whatappclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton sendMessageButton;
    private EditText editTextInputMessage;
    private ScrollView mScrollView;
    private TextView textViewDisplayMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        Initialize();
    }

    private void Initialize() {

        toolbar = findViewById(R.id.Group_chat_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Group Name");

        sendMessageButton = findViewById(R.id.btnSendMessage);
        editTextInputMessage = findViewById(R.id.input_group_message);
        textViewDisplayMessage = findViewById(R.id.textViewGroup_chat_display);
        mScrollView = findViewById(R.id.my_scroll_view);

    }
}
