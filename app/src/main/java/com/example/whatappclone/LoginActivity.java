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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    // Firebase
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    private Button loginButton, phoneLoginButton;
    private EditText editTextEmail, editTextPassword;
    private TextView textViewForgetPassword, textViewNeedNewAccount;
    private DatabaseReference userRef;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        Initialize();
        textViewNeedNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendUserToRegisterActivity();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        phoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,PhoneLoginActivity.class));
            }
        });

        textViewForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPassword();
            }
        });
    }



    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }

    private void ForgotPassword(){
        startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));
    }

    private void login() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        SendUserToMainActivity();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
        }else{
            loadingBar.setTitle("Sign In");
            loadingBar.setMessage("Please waiting");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                String currentUserId = mAuth.getCurrentUser().getUid();
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                userRef.child(currentUserId).child("device_token")
                                        .setValue(deviceToken)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    SendUserToMainActivity();
                                                    Toast.makeText(LoginActivity.this,"Logged in Successful...",Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                            }
                                        });

                            }else{
                                String errorMessage = task.getException().toString();
                                Toast.makeText(LoginActivity.this, "Error" + errorMessage,Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void Initialize() {
        loginButton = findViewById(R.id.login_button);
        phoneLoginButton = findViewById(R.id.phoneLogin_button);

        editTextEmail = findViewById(R.id.EditTextLogin_Email);
        editTextPassword = findViewById(R.id.EditTextLogin_Password);

        textViewForgetPassword = findViewById(R.id.TextViewForget_password_link);
        textViewNeedNewAccount = findViewById(R.id.TextViewNeed_account_link);
        loadingBar = new ProgressDialog(this);

    }


    // if the use is already logged then it will sent the user to main activity
    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser != null){
            SendUserToMainActivity();
        }

    }

}
