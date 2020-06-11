package com.example.whatappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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


public class ResetPasswordActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private Button btnResetPassword;
    private Toolbar toolbar;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        editTextEmail = findViewById(R.id.EditTextPassword_email);
        btnResetPassword = findViewById(R.id.button_sentEmail);

        toolbar = findViewById(R.id.password_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Reset password");

        mAuth = FirebaseAuth.getInstance();

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(ResetPasswordActivity.this, "Please input your email", Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ResetPasswordActivity.this, "Please check your Email to get the link to reset your new password", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPasswordActivity.this,LoginActivity.class));
                            }else{
                                String error = task.getException().toString();
                                Toast.makeText(ResetPasswordActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}
