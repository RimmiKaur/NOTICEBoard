package com.example.noticeboard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noticeboard.Fragments.All_notices_Fragment;
import com.example.noticeboard.Fragments.Edit_profile_Fragment;
import com.example.noticeboard.Fragments.List_of_groups_Fragment;
import com.example.noticeboard.Fragments.Make_a_post_Fragment;
import com.example.noticeboard.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Administrative extends AppCompatActivity {
    private  String userID;

    String TAG="Administrative";
    boolean doubleBackToExitPressedOnce=true;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;
    private TextView name_textview,occupation_textview;
    private ImageView nav_profile_image;
    Button addButton;
    EditText GetValue;
    final ArrayList<String> listElementsArrayList = new ArrayList<>();
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ImageView drag_drop;
    RecyclerviewAdapter adapter;
    int item_removed=1;
    int position_for_array;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrative);
        addButton = findViewById(R.id.button1);
        GetValue = findViewById(R.id.editText1);
        RecyclerView recyclerView =  findViewById(R.id.recyclerview_admin);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerviewAdapter( listElementsArrayList,this);
        recyclerView.setAdapter(adapter);


        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    userID = user.getUid();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                }
                // ...
            }
        };

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    String userID = user.getUid();
                    myRef.child("Groups").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            listElementsArrayList.clear();
                            for(DataSnapshot ds : snapshot.getChildren()){

                                listElementsArrayList.add(ds.getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        };
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listElementsArrayList.add(GetValue.getText().toString());

                myRef.child("Groups").child(String.valueOf(listElementsArrayList.size()+item_removed)).setValue(GetValue.getText().toString());
                adapter.notifyDataSetChanged();

            }
        });

        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);





        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);


        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.addDrawerListener(toggle);
                toggle.syncState();
            }
        });
        navigationView=findViewById(R.id.nav_view);

        View hView =  navigationView.getHeaderView(0);
        nav_profile_image = (ImageView) hView.findViewById(R.id.front_page_profile_pic);
        name_textview=(TextView) hView.findViewById(R.id.front_page_name);
        occupation_textview=(TextView) hView.findViewById(R.id.front_page_occupation);
        //nav_profile_image.setImageResource(R.drawable.selsel);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.nav_create_post:
                        getSupportFragmentManager().beginTransaction().add(R.id.drawer_layout, new Make_a_post_Fragment()).addToBackStack("tag").commit();
                        break;
                    case R.id.nav_edit_profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Edit_profile_Fragment()).addToBackStack(null).commit();
                        break;
                    case R.id.nav_notice_group:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new All_notices_Fragment() ).addToBackStack(null).commit();
                        break;
                    case R.id.nav_sign_out:
                        FirebaseAuth.getInstance().signOut();;
                        break;

                }
                return false;
            }
        });
    }

    private void toastMessage(String message){
        Toast.makeText(getApplicationContext(),"MAKE A POST"+message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    ItemTouchHelper.SimpleCallback simpleCallback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {


        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position=viewHolder.getAdapterPosition();
            listElementsArrayList.remove(position);
            item_removed++;
            adapter.notifyItemRemoved(position);
            toastMessage(position+"pos");
            FirebaseDatabase.getInstance().getReference().child("Groups").child(String.valueOf(position+1+item_removed)).removeValue();

        }
    };
    public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.ViewHolder> {
        ArrayList<String> group_list;
        Context context;

        public RecyclerviewAdapter(ArrayList<String> group_list, Context context) {
            this.group_list = group_list;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_recycler_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String animal = group_list.get(position);
            holder.list_name.setText(animal);
        }

        @Override
        public int getItemCount() {
            return group_list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            TextView list_name;

            public ViewHolder(View itemView) {
                super(itemView);
                list_name=itemView.findViewById(R.id.textview_recycler_group_name);

            }
        }



    }






}