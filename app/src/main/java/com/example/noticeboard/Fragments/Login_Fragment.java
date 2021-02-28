package com.example.noticeboard.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noticeboard.Activity.Front_Page;
import com.example.noticeboard.Model.UserInformation;
import com.example.noticeboard.R;
import com.example.noticeboard.utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.regex.Pattern;


public class Login_Fragment extends Fragment implements View.OnClickListener {

    TextView registrationTextView;
    utils ut = new utils();
    String userID;
    private static final String TAG = "MainActivity";
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private Boolean username_check=false,pass_check=false;
    private TextView forgot_password;
    public TextInputLayout usernameTextInputLayout, passwordTextInputLayout;
    public TextInputEditText usernameTextInputEditText, passwordTextInputEditText;
    AppCompatButton loginButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
            View view= inflater.inflate(R.layout.fragment_login, container, false);
        registrationTextView = view.findViewById(R.id.registration_textview);
        usernameTextInputEditText = view.findViewById(R.id.username_textinputedittext);
        passwordTextInputEditText = view.findViewById(R.id.password_textinputedittext);
        loginButton = view.findViewById(R.id.login_outlinedButton);
        usernameTextInputLayout = view.findViewById(R.id.username_textinputlayout);
        passwordTextInputLayout = view.findViewById(R.id.password_textinputlayout);
        forgot_password=view.findViewById(R.id.forgotpassword_textview);
        registrationTextView.setOnClickListener(this);



        usernameTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkusername();
                username_check=true;

            }
        });

        usernameTextInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkusername();
                }
            }
        });

        passwordTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkpassword();
                pass_check=true;

            }
        });

        passwordTextInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkpassword();
                }
            }
        });


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    userID=user.getUid();
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Log.d(TAG,"Successfully signed in with: " + user.getEmail());


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                    toastMessage("Successfully signed out.");
                }
                // ...
            }
        };


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username_check && pass_check){
                    String email = usernameTextInputEditText.getText().toString();
                    String pass = passwordTextInputEditText.getText().toString();
                    mAuth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(
                                    new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(
                                                @NonNull Task<AuthResult> task)
                                        {
                                            if (task.isSuccessful()) {
                                                toastMessage("Login successful!!");
                                                // if sign-in is successful
                                                // intent to home activity
                                                mAuth = FirebaseAuth.getInstance();
                                                mFirebaseDatabase = FirebaseDatabase.getInstance();
                                                myRef = mFirebaseDatabase.getReference();
                                                myRef.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for(DataSnapshot ds : snapshot.getChildren()){
                                                            if(ds.getKey().contains("Users")){
                                                                if(ds.child("Administrative").hasChild(userID)){
                                                                    Intent intent = new Intent(getActivity(), Front_Page.class);
                                                                    startActivity(intent);
                                                                }
                                                                else if(ds.child("Professor").hasChild(userID)){
                                                                    Intent intent = new Intent(getActivity(), Front_Page.class);
                                                                    startActivity(intent);
                                                                }
                                                                else if(ds.child("Student").hasChild(userID)){
                                                                    Intent intent = new Intent(getActivity(), Front_Page.class);
                                                                    startActivity(intent);
                                                                }
                                                                else
                                                                {
                                                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame,new Register_info_Fragment()).commit();
                                                                }
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                            }

                                            else {
                                                toastMessage( "First register");
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            toastMessage("Login FAIL!!"+e.getMessage());
                        }
                    });
                    toastMessage(email+"  fcr   "+pass);
                }
                else
                {
                    toastMessage("u suck");
                }


            }
        });

        forgot_password.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame,new Forgot_password_Fragment()).commit();
            }
        }));


            return view;
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
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registration_textview:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, new Registration_Fragment()).addToBackStack(null).commit();
                break;


        }

    }


    public boolean checkusername() {
        String username = usernameTextInputEditText.getText().toString().trim();

        Pattern pattern = Pattern.compile(ut.mailregex());
        if (pattern.matcher(username).matches()) {
            usernameTextInputLayout.setError(null);
            return true;
        } else {
            usernameTextInputLayout.setError("Check Username");
            return false;
        }

    }

    public boolean checkpassword() {
        String password = passwordTextInputEditText.getText().toString().trim();

        Pattern pattern = Pattern.compile(ut.passwordregex());
        if (pattern.matcher(password).matches()) {
            usernameTextInputLayout.setError(null);
            return true;
        } else {
            passwordTextInputLayout.setError("Check Password");
            return false;
        }
    }


}