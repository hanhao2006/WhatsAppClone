package com.example.whatappclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    // main page title
    private Toolbar mainToolbar;

    // Bottom navigation view
    private BottomNavigationView bottomNavigationView;

    // Fragment
    private ChatsFragment chatsFragment;
    private ContactsFragment contactsFragment;
    private GroupsFragment groupsFragment;

    // Firebase
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        System.out.println(currentUser);

        // set toolbar
        mainToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("WhatsAppClone");

        // Fragment
        contactsFragment = new ContactsFragment();
        chatsFragment = new ChatsFragment();
        groupsFragment = new GroupsFragment();


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
                    default:
                        return false;
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(currentUser == null){
            SendUserToLoginActivity();
        }
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginIntent);
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
                return true;

            case R.id.menu_settings:
                return true;

            case R.id.menu_Logout:
                mAuth.signOut();
                SendUserToLoginActivity();
                return true;

            default: return false;
        }
    }
}
