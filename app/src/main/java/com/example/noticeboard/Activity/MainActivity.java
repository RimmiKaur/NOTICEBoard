package com.example.noticeboard.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.noticeboard.Fragments.Login_Fragment;

import com.example.noticeboard.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";


    Login_Fragment login_fragment=new Login_Fragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File directoryToStore;
        directoryToStore = getBaseContext().getExternalFilesDir("TestFolder");
        if (!directoryToStore.exists()) {
            if (directoryToStore.mkdir()) ; //directory is created;
        }
//        CreativeViewPager creativeViewPagerView = findViewById(R.id.creativeContentViewPager);
//        creativeViewPagerView.setCreativeViewPagerAdapter((CreativePagerAdapter)(new NatureCreativePagerAdapter((this))));


        getSupportFragmentManager().beginTransaction().replace(R.id.frame,login_fragment).commit();



    }



}