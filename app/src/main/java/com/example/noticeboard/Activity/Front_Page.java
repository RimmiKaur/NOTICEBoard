package com.example.noticeboard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noticeboard.Fragments.All_notices_Fragment;
import com.example.noticeboard.Fragments.Edit_profile_Fragment;
import com.example.noticeboard.Fragments.List_of_groups_Fragment;
import com.example.noticeboard.Fragments.Login_Fragment;
import com.example.noticeboard.Fragments.Make_a_post_Fragment;
import com.example.noticeboard.R;
import com.example.noticeboard.Model.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Front_Page extends AppCompatActivity {

    private static final String TAG = "Front_Page";

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseUser user;


    private  String userID;

    boolean doubleBackToExitPressedOnce=true;

    private TextView name_textview,occupation_textview;
    private ImageView nav_profile_image;

    private DrawerLayout drawer;
    private NavigationView navigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front__page);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,new All_notices_Fragment()).commit();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
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

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
       // FirebaseUser user = mAuth.getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        // userID = user.getUid();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    userID = user.getUid();
                    toastMessage( ".................................onAuthStateChanged:signed_in:" + user.getUid());


                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.

                            for(DataSnapshot ds : dataSnapshot.getChildren()){
                                UserInformation uInfo = new UserInformation();
//
                                String occupation = null;


                                if(ds.getKey().contains("Users"))
                                {
                                    if(ds.child("Administrative").hasChild(userID)){
                                        occupation="Administrative";
                                    }
                                    else if(ds.child("Professor").hasChild(userID)){
                                        occupation="Professor";
                                    }
                                    else if(ds.child("Student").hasChild(userID)){
                                        occupation="Student";
                                    }
                                    uInfo=ds.child(occupation).child(userID).getValue(UserInformation.class);
                                    name_textview.setText(uInfo.getName());
                                    occupation_textview.setText(uInfo.getOccupation());
                                    Picasso.get().load(uInfo.getImageUrl()).into(nav_profile_image);
                                }


                            }

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            toastMessage(databaseError.getMessage());
                        }
                    });


                } else {
                    // User is signed out
                    Log.d(TAG, ".................................onAuthStateChanged:signed_out");
                    toastMessage("..........Successfully signed out.");
                }

            }
        };






    }


//    private void showData(@NotNull DataSnapshot dataSnapshot) {
//        for(DataSnapshot ds : dataSnapshot.getChildren()){
//            UserInformation uInfo = new UserInformation();
////
//             occupation = null;
//
//
//            if(ds.getKey().contains("Users"))
//            {
//                if(ds.child("Administrative").hasChild(userID)){
//                    occupation="Administrative";
//                }
//                else if(ds.child("Professor").hasChild(userID)){
//                    occupation="Professor";
//                }
//                else if(ds.child("Student").hasChild(userID)){
//                    occupation="Student";
//                }
////                switch(ds.child("Administrative").child(userID).getValue(UserInformation.class).getOccupation().toString())
////                {
////                case "Student":
////                    uInfo=ds.child("Student").child(userID).getValue(UserInformation.class);
////                    break;
////                case "Professor":
////                    uInfo=ds.child("Professor").child(userID).getValue(UserInformation.class);
////                    break;
////                case "Administrative":
////                    uInfo=ds.child("Administrative").child(userID).getValue(UserInformation.class);
////                    break;
////                }
//                uInfo=ds.child(occupation).child(userID).getValue(UserInformation.class);
//               // Log.d(TAG, "showData: name: " + ds.child("Administrative").child(userID).toString());
//                // Log.d(TAG, "showData: email: " + uInfo.getOccupation());
//
//
//                name_textview.setText(uInfo.getName());
//                occupation_textview.setText(uInfo.getOccupation());
//                Picasso.get().load(uInfo.getImageUrl()).into(nav_profile_image);
//            }
//        }
//    }


//
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

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else{

            if(doubleBackToExitPressedOnce){
                getSupportFragmentManager().popBackStack();
                doubleBackToExitPressedOnce=false;
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=true;
                    }
                }, 2000);

                toastMessage("ans"+doubleBackToExitPressedOnce);
            }
            else
            {
                toastMessage("ans"+doubleBackToExitPressedOnce);
                doubleBackToExitPressedOnce=true;
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("EXIT")
                        .setMessage("Are you sure you EXIT?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                moveTaskToBack(true);
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

              // doubleBackToExitPressedOnce=false;
            }

            toastMessage("ans"+doubleBackToExitPressedOnce);
        }

    }




}

