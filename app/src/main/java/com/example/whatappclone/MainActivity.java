package com.example.whatappclone;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.whatappclone.corona.CoronaMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    // main page title
    private Toolbar mainToolbar;

    // Bottom navigation view
    private BottomNavigationView bottomNavigationView;

    // Fragment
    private ChatsFragment chatsFragment;
    private ContactsFragment contactsFragment;
    private GroupsFragment groupsFragment;
    private RequestsFragment requestsFragment;

    // Firebase
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private String currentUserId;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
//        id = mAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        // set toolbar
        mainToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Online Chat");





        // Fragment
        contactsFragment = new ContactsFragment();
        chatsFragment = new ChatsFragment();
        groupsFragment = new GroupsFragment();
        requestsFragment = new RequestsFragment();


        getFramgent(chatsFragment);

        if(currentUser == null){
            SendUserToLoginActivity();
        }
        else{
           //updateUserStatus("Online");
            VerifyUserExistance();
        }

        // bottom navigation
        bottomNavigationView = findViewById(R.id.main_btnNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mainChat:
                        getFramgent(chatsFragment);
                        return true;
                    case R.id.mainGroup:
                        getFramgent(groupsFragment);
                        return true;
                    case R.id.mainContact:
                        getFramgent(contactsFragment);
                        return true;
                    case R.id.mainRequest:
                        getFramgent(requestsFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();



    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        if(currentUser !=null){
//            updateUserStatus("OffLine");
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        if(currentUser !=null){
//            updateUserStatus("OffLine");
//        }
//    }

    private void VerifyUserExistance() {
        final String currentId = mAuth.getCurrentUser().getUid();
        databaseReference.child("Users").child(currentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("name").exists()){
                    Toast.makeText(MainActivity.this,"Welcome",Toast.LENGTH_LONG).show();
                }
                else{
                    SendUserSettingActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getFramgent(Fragment f){
        FragmentTransaction  fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frameLayout,f);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.options_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_find_friends:
                SendUserFindFrenidsActivity();
                return true;
            case R.id.menu_create_group:
                RequestNewGroup();
                return true;
            case R.id.menu_corona:
                SendUserToGetChart();
                return true;
            case R.id.menu_settings:
                SendUserSettingActivity();
                return true;
            case R.id.menu_Logout:
                mAuth.signOut();
                SendUserToLoginActivity();
                return true;

            default: return false;
        }
    }

    private void RequestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter Group Name");
        final EditText groupName = new EditText(MainActivity.this);
        groupName.setHint("e.g Cafe");
        builder.setView(groupName);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupNameGet = groupName.getText().toString();
                if(TextUtils.isEmpty(groupNameGet)){
                    Toast.makeText(MainActivity.this,"Please write Group name",Toast.LENGTH_SHORT).show();
                }else{
                    CreateNewGroup(groupNameGet);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void CreateNewGroup(final String groupNameGet ) {

        databaseReference.child("Groups").child(groupNameGet).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this,groupNameGet + " is created successfully",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }


    private void SendUserSettingActivity() {
        Intent settingIntent = new Intent(MainActivity.this,SettingActivity.class);
        startActivity(settingIntent);

    }
    private void SendUserFindFrenidsActivity() {
        Intent findFrenidsIntent = new Intent(MainActivity.this,FindFriendsActivity.class);
        startActivity(findFrenidsIntent);

    }
    private void SendUserToGetChart(){
        startActivity(new Intent(MainActivity.this, CoronaMainActivity.class));
    }

//    private  void updateUserStatus(String state){
//        String saveCurrentTime, saveCurrentDate;
//
//        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
//        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
//        saveCurrentDate = currentDate.format(calendar.getTime());
//        saveCurrentTime = currentTime.format(calendar.getTime());
//
//        HashMap<String, Object> onlineStateMap = new HashMap<>();
//        onlineStateMap.put("time",saveCurrentTime);
//        onlineStateMap.put("date",saveCurrentDate);
//        onlineStateMap.put("state",state);
//
//        currentUserId = mAuth.getCurrentUser().getUid();
//
//        databaseReference.child("Users").child(currentUserId).child("userState")
//                .updateChildren(onlineStateMap);
//
//    }
}
