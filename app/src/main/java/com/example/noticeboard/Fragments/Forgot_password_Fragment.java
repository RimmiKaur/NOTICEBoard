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
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class Forgot_password_Fragment extends Fragment {


    private TextInputEditText emailTextInputEditText;
    public TextInputLayout emailTextInputLayout;
    utils ut = new utils();
    Button reset_button;
    private String email;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_forgot_password_, container, false);
        emailTextInputEditText = view.findViewById(R.id.forgot_email_textinputedittext);
        emailTextInputLayout = view.findViewById(R.id.forgot_email_textinputlayout);
        reset_button=view.findViewById(R.id.reset_button);
        FirebaseAuth auth = FirebaseAuth.getInstance();

         emailTextInputEditText.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {


           }

           @Override
           public void afterTextChanged(Editable s) {
               email=emailTextInputEditText.getText().toString();

           }
       });

         reset_button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(checkusername()){
                     auth.sendPasswordResetEmail(email)
                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if (task.isSuccessful()) {
                                         Toast.makeText(getContext(), "Mail sent", Toast.LENGTH_SHORT).show();
                                         getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame,new Login_Fragment()).commit();
                                     } else {
                                       //  Toast.makeText(getContext(), "Mail Invalid", Toast.LENGTH_SHORT).show();
                                         emailTextInputLayout.setError("Mail ID doesn't exist");
                                     }
                                 }
                             });
                 }
             }
         });


        return view;
    }
    public boolean checkusername() {
        String username = emailTextInputEditText.getText().toString().trim();

        Pattern pattern = Pattern.compile(ut.mailregex());
        if (pattern.matcher(username).matches()) {
            emailTextInputLayout.setError(null);
            return true;
        } else {
            emailTextInputLayout.setError("Check Username");
            return false;
        }

    }
}