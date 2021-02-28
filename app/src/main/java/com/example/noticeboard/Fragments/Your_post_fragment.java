package com.example.noticeboard.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.noticeboard.Adapter.NatureCreativePagerAdapter;
import com.example.noticeboard.Model.Post_Information;
import com.example.noticeboard.Model.UserInformation;
import com.example.noticeboard.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tbuonomo.creativeviewpager.CreativeViewPager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class Your_post_fragment extends Fragment {
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef,user_ref;
    private FirebaseUser user;
    String image = "null";
    ArrayList<Post_Information> all_post=new ArrayList<Post_Information>();;
    CreativeViewPager creativeViewPager;
    NatureCreativePagerAdapter adapter;

    //String
    private  String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_your_post_fragment, container, false);
        ProgressDialog progressdialog = new ProgressDialog(getContext());
        progressdialog.setMessage("Loading....");

        progressdialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressdialog.dismiss();

            }
        }, 6000);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        user_ref = mFirebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    userID = user.getUid();
                    Log.d(TAG, ".................................onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("..............Successfully signed in with: " + user.getEmail());
                    toastMessage("Entered auth");
                    myRef.child("Users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds : snapshot.getChildren()){



                                if(ds.getKey().contains("Administrative")){
                                    image= ds.child(userID).getValue(UserInformation.class).getImageUrl();
                                }
                                else if(ds.getKey().contains("Professor")){
                                    image= ds.child(userID).getValue(UserInformation.class).getImageUrl();
                                }
                                else if(ds.getKey().contains("Student")){
                                    image= ds.child(userID).getValue(UserInformation.class).getImageUrl();
                                }
                            }

                            //  toastMessage("Entered user");
//                                        toastMessage("image00000000"+image);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            myRef.child("Post").child("14_Feb_2021").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // This method is called once with the initial value and again
                                    // whenever data at this location is updated.
                                    // toastMessage("entered post");
                                    all_post.clear();
                                    showData(dataSnapshot);
                                    //toastMessage("exit post");


                                    toastMessage("size"+all_post.size());
                                    NatureCreativePagerAdapter natureCreativePagerAdapter=new NatureCreativePagerAdapter(getContext(),all_post);
                                    creativeViewPager=view.findViewById(R.id.creativeViewPagerView);
                                    adapter=natureCreativePagerAdapter;
                                    creativeViewPager.setCreativeViewPagerAdapter(adapter);
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    toastMessage(databaseError.getMessage());
                                }
                            });
                        }
                    }, 5000);




                } else {
                    // User is signed out
                    Log.d(TAG, ".................................onAuthStateChanged:signed_out");
                    toastMessage("..........Successfully signed out.");
                }

            }

        };

        return view;
    }

    private void showData(@NotNull DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){

            Post_Information post= new Post_Information();
            post=ds.getValue(Post_Information.class);
            if(post.getPost_userId()==userID){
                all_post.add(post);
            }
        }
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



    private void toastMessage(String message){
        Toast.makeText(getContext(),"ALL_NOT "+message,Toast.LENGTH_SHORT).show();
    }

}