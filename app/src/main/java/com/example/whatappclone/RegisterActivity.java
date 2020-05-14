package com.example.whatappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccountButton;
    private EditText editTextCreateEmail, editTextCreatePassword;
    private TextView textViewAlreadyHaveAccount;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        Initialize();

        textViewAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });


        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateNewAccount();
            }
        });
    }

    private void CreateNewAccount() {
        String email = editTextCreateEmail.getText().toString();
        String password = editTextCreatePassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
        }else{
            loadingBar.setTitle("Creating new account");
            loadingBar.setMessage("Please waiting");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                               Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                               mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                               startActivity(mainIntent);
                               finish();
                               //Firebase
                                String currentUserId = mAuth.getCurrentUser().getUid();
                                databaseReference.child("Users").child(currentUserId).setValue("");
                                Toast.makeText(RegisterActivity.this,"Account Created Successful",Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                            }else{
                                String errorMessage = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Error" + errorMessage,Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }

    private void Initialize() {
        createAccountButton = findViewById(R.id.register_button);
        editTextCreateEmail = findViewById(R.id.EditTextRegister_Email);
        editTextCreatePassword = findViewById(R.id.EditTextRegister_Password);
        textViewAlreadyHaveAccount = findViewById(R.id.TextViewAlready_have_account_link);
        loadingBar = new ProgressDialog(this);
    }
}
