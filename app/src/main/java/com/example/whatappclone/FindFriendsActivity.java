package com.example.whatappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.whatappclone.model.Contacts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView findFriendsRecyclerView;

    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        findFriendsRecyclerView = findViewById(R.id.find_friends_recycler_list);
        findFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mToolbar = findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(databaseReference,Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts, FindFiredViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, FindFiredViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFiredViewHolder holder, final int position, @NonNull Contacts model) {
                holder.username.setText(model.getName());
                holder.userstatus.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profileimage).into(holder.profileImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id = getRef(position).getKey();

                        startActivity(new Intent(FindFriendsActivity.this,ProfileActivity.class)
                                .putExtra("visit_user_id",visit_user_id));

                    }
                });
            }

            @NonNull
            @Override
            public FindFiredViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                FindFiredViewHolder viewHolder = new FindFiredViewHolder(view);
                return viewHolder;
            }
        };

        findFriendsRecyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public  static  class FindFiredViewHolder extends  RecyclerView.ViewHolder{

        TextView username, userstatus;
        CircleImageView profileImage;

        public FindFiredViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_profile_name);
            userstatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}
