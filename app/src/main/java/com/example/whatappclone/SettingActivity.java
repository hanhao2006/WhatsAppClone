package com.example.whatappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private Button btnUpdate;
    private EditText editTextUsername, editTextUserStatus;
    private CircleImageView imageView;

    private String cuurentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();
        cuurentUserId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        Initialize();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
    }

    private void Initialize() {
        btnUpdate = findViewById(R.id.setting_update_button);

        editTextUsername = findViewById(R.id.setting_user_name);
        editTextUserStatus = findViewById(R.id.setting_profile_status);
        imageView = findViewById(R.id.set_profile_image);

    }
    private void update() {
        String name = editTextUsername.getText().toString();
        String status = editTextUserStatus.getText().toString();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please Write your user name",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(status)){
            Toast.makeText(this,"Please Write your status",Toast.LENGTH_SHORT).show();
        }else{
            HashMap<String,String> profileMap = new HashMap<>();
            profileMap.put("uid",cuurentUserId);
            profileMap.put("name",name);
            profileMap.put("status",status);
            databaseReference.child("Users").child(cuurentUserId).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                SendUserToMainActivity();
                               Toast.makeText(SettingActivity.this,"Profile updated Successfully",Toast.LENGTH_SHORT).show();
                            }else{
                                String errorMessage = task.getException().toString();
                                Toast.makeText(SettingActivity.this,"Error" + errorMessage,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
