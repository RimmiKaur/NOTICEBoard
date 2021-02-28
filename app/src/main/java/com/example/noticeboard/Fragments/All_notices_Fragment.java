package com.example.noticeboard.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.noticeboard.Activity.MainActivity;
import com.example.noticeboard.Activity.SplashScreen;
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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static android.content.ContentValues.TAG;


public class All_notices_Fragment extends Fragment {
    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef,user_ref;
    private FirebaseUser user;
    String image = "null";
    ArrayList<Post_Information> all_post=new ArrayList<Post_Information>();
    ArrayList<String> dates_in_spinner=new ArrayList<>();
    CreativeViewPager creativeViewPager;
    NatureCreativePagerAdapter adapter;
    Spinner spinner;
    //String
    private  String userID;
    Date startDate;
    String selection_date;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_all_notices, container, false);
        Spinner spinner =view.findViewById(R.id.date_spinner);
        creativeViewPager=view.findViewById(R.id.creativeViewPagerView);
        ProgressDialog progressdialog = new ProgressDialog(getContext());
        progressdialog.setMessage("Loading....");

        startDate=new Date();


        LocalDate startDate = LocalDate.of(2021,2,2);
        LocalDate endDate = LocalDate.now().plusDays(1);

        long numOfDays = ChronoUnit.DAYS.between(startDate, endDate);
        List<LocalDate> listOfDates1 = Stream.iterate(startDate, date -> date.plusDays(1)).limit(numOfDays).collect(Collectors.toList());
        for (LocalDate date1 : listOfDates1) {
            dates_in_spinner.add(date1.toString());
        }



        ArrayAdapter dataAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_expandable_list_item_1,listOfDates1 );
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selection_date=dates_in_spinner.get(position);
                myRef.child("Post").child(selection_date).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        all_post.clear();
                        for(DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            Post_Information post= new Post_Information();
                            post=ds.getValue(Post_Information.class);
                            System.out.println("############################"+post.getPost_edit_text());
                            System.out.println("############################"+post.getPost_userId());
                            System.out.println("############################"+post.getPost_username());
                            all_post.add(post);
                        }
                        adapter=new NatureCreativePagerAdapter(getContext(),all_post);
                        toastMessage("count"+String.valueOf(adapter.getCount()));
                        try {
                            creativeViewPager.setCreativeViewPagerAdapter(adapter);
                        }catch (Exception e){
                            System.out.println("=============================="+e.getCause()+"+++++"+e.getMessage());
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        toastMessage(databaseError.getMessage());
                    }
                });




            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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
                    myRef.child("Post").child(endDate.toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            // toastMessage("entered post");
                            all_post.clear();
                            // showData(dataSnapshot);

                            for(DataSnapshot ds : dataSnapshot.getChildren())
                            {
                                Post_Information post= new Post_Information();
                                post=ds.getValue(Post_Information.class);
                                System.out.println("############################"+post.getPost_userId());
                                System.out.println("############################"+post.getPost_username());
                                all_post.add(post);
                            }
                            adapter=new NatureCreativePagerAdapter(getContext(),all_post);;
                            creativeViewPager.setCreativeViewPagerAdapter(adapter);
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

        return view;
    }

    private void showData(@NotNull DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){

            Post_Information post= new Post_Information();
            post=ds.getValue(Post_Information.class);
          //  toastMessage(post.getPost_edit_text());


            System.out.println("############################"+post.getPost_edit_text());
            System.out.println("############################"+post.getPost_userId());
            System.out.println("############################"+post.getPost_username());
            all_post.add(post);


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