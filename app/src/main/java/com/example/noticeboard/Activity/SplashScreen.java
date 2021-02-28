package com.example.noticeboard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.noticeboard.Fragments.Login_Fragment;
import com.example.noticeboard.Fragments.Register_info_Fragment;
import com.example.noticeboard.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "SplashScreen";
    private DatabaseReference myRef;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);




        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Log.d(TAG,"Successfully signed in with: " + user.getEmail());

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            mAuth = FirebaseAuth.getInstance();
                            mFirebaseDatabase = FirebaseDatabase.getInstance();
                            myRef = mFirebaseDatabase.getReference();
                            myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String userID=FirebaseAuth.getInstance().getUid();
                                    for(DataSnapshot ds : snapshot.getChildren()){
                                        if(ds.getKey().contains("Users")){
                                            if(ds.child("Administrative").hasChild(userID)){
                                                Intent intent = new Intent(getApplicationContext(), Administrative.class);
                                                startActivity(intent);

                                            }
                                            else if(ds.child("Professor").hasChild(userID)){
                                                Intent intent = new Intent(getApplicationContext(), Front_Page.class);
                                                startActivity(intent);
                                            }
                                            else if(ds.child("Student").hasChild(userID)){
                                                Intent intent = new Intent(getApplicationContext(), Front_Page.class);
                                                startActivity(intent);
                                            }
                                            else
                                            {
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                    }, 2000);

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                    toastMessage("Successfully signed out.");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(SplashScreen.this, MainActivity.class);
                            startActivity(i);
                        }
                    }, 2000);
                }
            }
        };
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
}