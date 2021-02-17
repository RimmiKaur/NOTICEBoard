package com.example.noticeboard.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.noticeboard.R;
import com.example.noticeboard.utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class Registration_Fragment extends Fragment {

     FirebaseAuth firebaseAuth;
    TextInputLayout mailTextInputLayout, regpasswordTextInputLayout, confirmpasswordTextInputLayout;
    TextInputEditText mailTextInputEditText, regpasswordTextInputEditText, confirmpasswordTextInputEditText;
     Button registerButton;
    utils ut = new utils();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.registration_fragment, container, false);
        registerButton=view.findViewById(R.id.registration_outlinedButton);
        mailTextInputEditText = view.findViewById(R.id.mail_textinputedittext);
        regpasswordTextInputEditText = view.findViewById(R.id.reg_password_textinputedittext);
        confirmpasswordTextInputEditText = view.findViewById(R.id.confirmpassword_textinputedittext);
        mailTextInputLayout = view.findViewById(R.id.mail_textinputlayout);
        regpasswordTextInputLayout = view.findViewById(R.id.reg_password_textinputlayout);
        confirmpasswordTextInputLayout = view.findViewById(R.id.confirmpassword_textinputlayout);

        firebaseAuth=FirebaseAuth.getInstance();
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkmail() && checkpassword() && checkconfirmpassword())
                {
                    firebaseAuth.createUserWithEmailAndPassword(mailTextInputEditText.getText().toString(),regpasswordTextInputEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getContext(), "Registered Sucessfully", Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                            }
                            else
                            {
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                            }
                        }
                    });
                }
            }
        });

        regpasswordTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkpassword();

            }
        });

        mailTextInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkmail();
                }
            }
        });

        regpasswordTextInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkpassword();
                }
            }
        });

        confirmpasswordTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkpassword();

            }
        });

        confirmpasswordTextInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkpassword();
                }
            }
        });
        FirebaseAuth.getInstance().signOut();
        return view;
    }

    boolean checkmail() {
        String mail = mailTextInputEditText.getText().toString();

        Pattern pattern = Pattern.compile(ut.mailregex());
        if (pattern.matcher(mail).matches()) {
            mailTextInputLayout.setError(null);
            return true;
        } else {
            mailTextInputLayout.setError(getString(R.string.mail_validate));
            return false;
        }
    }


    boolean checkpassword() {
        String password = regpasswordTextInputEditText.getText().toString();

        Pattern pattern = Pattern.compile(ut.passwordregex());
        if (pattern.matcher(password).matches()) {
            regpasswordTextInputLayout.setError(null);
            return true;
        } else {
            regpasswordTextInputLayout.setError(getString(R.string.password_validate));
            return false;
        }
    }


    boolean checkconfirmpassword() {
        String conpassword = confirmpasswordTextInputEditText.getText().toString();
        String password = regpasswordTextInputEditText.getText().toString();

        Pattern pattern = Pattern.compile(ut.passwordregex());
        if (pattern.matcher(password).matches() && conpassword.equals(password)) {
            confirmpasswordTextInputLayout.setError(null);
            return true;
        } else {
            confirmpasswordTextInputLayout.setError(getString(R.string.conpassword_validate));
            return false;
        }
    }
}