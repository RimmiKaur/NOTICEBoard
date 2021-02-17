package com.example.noticeboard.Fragments;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noticeboard.Activity.Front_Page;
import com.example.noticeboard.Model.UserInformation;
import com.example.noticeboard.R;
import com.example.noticeboard.utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class Edit_profile_Fragment extends Fragment {

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private final Calendar myCalendar = Calendar.getInstance();
    private  String userID;
    private Boolean gender_selector=false;
    private Boolean firebase_image_selector=false,image_selector=false,name_selector=false,mobile_selector=false,date_selector=false;
    private utils ut=new utils();
    private TextInputLayout  nameTextInputLayout, mobileTextInputLayout, dateTextInputLayout;
    private TextInputEditText nameTextInputEditText, mobileTextInputEditText, dateTextInputEditText;
    private AutoCompleteTextView country_spinner_textview, occupation_spinner_textview;
    private static final int PICK_IMAGE_REQUEST = 1000;
    private String GENDER,occupation1,country;
    private Boolean pic_selected=false;
    private StorageReference mStorageRef;
    private Uri profile_pic=null;
    private Button changeButton;
    private ImageView nav_profile_image;
    private String photoStringLink;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_edit_profile_, container, false);

        changeButton=view.findViewById(R.id.changes_outlinedButton);
        nav_profile_image=view.findViewById(R.id.reg_profile_pic_imageview);
        country_spinner_textview=view.findViewById(R.id.country_spinner_selector);
        occupation_spinner_textview=view.findViewById(R.id.occupation_selector);
        nameTextInputEditText = view.findViewById(R.id.name_textinputedittext);
        mobileTextInputEditText = view.findViewById(R.id.mobile_textinputedittext);
        dateTextInputEditText = view.findViewById(R.id.dob_textinputedittext);
        nameTextInputLayout = view.findViewById(R.id.name_textinputlayout);
        mobileTextInputLayout = view.findViewById(R.id.mobile_textinputlayout);
        dateTextInputLayout = view.findViewById(R.id.dob_textinputlayout);

        nav_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                image_selector=true;
            }
        });
        nameTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkname();

            }
        });



        mobileTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkmobile();

            }
        });




        dateTextInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkdateofbirth();

            }
        });

        nameTextInputEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameTextInputEditText.setText("");
                name_selector=true;
            }
        });


        nameTextInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkname();
                }
            }
        });



        mobileTextInputEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileTextInputEditText.setText("");
                mobile_selector=true;
            }
        });




        mobileTextInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkmobile();
                }
            }
        });



        dateTextInputEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTextInputEditText.setText("");
                date_selector=true;
            }
        });


        dateTextInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkdateofbirth();
                }
            }
        });



        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        dateTextInputEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        // FirebaseUser user = mAuth.getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        // userID = user.getUid();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    userID = user.getUid();
                    Log.d(TAG, ".................................onAuthStateChanged:signed_in:" + user.getUid());
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

                                    if(name_selector==false)
                                        nameTextInputEditText.setText(uInfo.getName());

                                    if(mobile_selector==false)
                                        mobileTextInputEditText.setText(uInfo.getPhone_num());

                                    if(date_selector==false)
                                        dateTextInputEditText.setText(uInfo.getDOB());

                                    if(image_selector==false)
                                    {
                                   //     toastMessage("firebase");
                                        Picasso.get().load(uInfo.getImageUrl()).into(nav_profile_image);
                                        firebase_image_selector=true;
                                        photoStringLink=Uri.parse(uInfo.getImageUrl()).toString();
                                    }
                                    occupation1=uInfo.getOccupation();
                                    country=uInfo.getCountry();
                                    dateTextInputEditText.setText(uInfo.getDOB());
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


        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Submit pressed.");
                String name = nameTextInputEditText.getText().toString();
                String phoneNum = mobileTextInputEditText.getText().toString();
                String DOB=dateTextInputEditText.getText().toString();

                toastMessage(userID);
                final String[] Image_uri = new String[1];
                StorageReference storageReference= mStorageRef.child("Users/Administrative/" + userID);
                //handle the exception if the EditText fields are null
                if(checkname()  && checkmobile() && checkdateofbirth() && (image_selector || firebase_image_selector )){

                  //  toastMessage("entered");
                    switch(occupation1){
                        case "Student":
                            storageReference= mStorageRef.child("Users/Student/" + userID + "/profile_pic.jpg");
                            break;
                        case "Professor":
                            storageReference= mStorageRef.child("Users/Professor/" + userID + "/profile_pic.jpg");
                            break;
                        case "Administrative":
                            storageReference= mStorageRef.child("Users/Administrative/" + userID + "/profile_pic.jpg");
                            break;
                    }

                    if(image_selector==true){
                        storageReference.putFile(profile_pic).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                        {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                toastMessage("Upload Success");



                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        photoStringLink = uri.toString();
                                        UserInformation userInformation = new UserInformation(name,GENDER,phoneNum,occupation1,country, photoStringLink,DOB);

                                        toastMessage(photoStringLink);
                                        switch(occupation1){
                                            case "Student":
                                                myRef.child("Users").child("Student").child(userID).setValue(userInformation);
                                                break;
                                            case "Professor":
                                                myRef.child("Users").child("Professor").child(userID).setValue(userInformation);
                                                break;
                                            case "Administrative":
                                                myRef.child("Users").child("Administrative").child(userID).setValue(userInformation);
                                                break;
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                toastMessage("Upload Failed");
                             //   Toast.makeText(getActivity(), e.getMessage()+"   v   "+e.getCause(), Toast.LENGTH_LONG).show();
                                System.out.println(e.getMessage()+"   v   "+e.getCause());
                            }
                        });
                    }
                    else
                    {
                        if(firebase_image_selector==true)
                        {
                            UserInformation userInformation = new UserInformation(name,GENDER,phoneNum,occupation1,country, photoStringLink,DOB);
                            switch(occupation1){
                                case "Student":
                                    myRef.child("Users").child("Student").child(userID).setValue(userInformation);
                                    break;
                                case "Professor":
                                    myRef.child("Users").child("Professor").child(userID).setValue(userInformation);
                                    break;
                                case "Administrative":
                                    myRef.child("Users").child("Administrative").child(userID).setValue(userInformation);
                                    break;
                            }
                        }
                    }


                    toastMessage("New Information has been saved.");
                }else{
                    toastMessage("checkname"+checkname());
                    toastMessage("checkmobile"+checkmobile());
                    toastMessage("checkdob"+checkdateofbirth());
                    toastMessage("image_selector"+image_selector);
                }

            }

        });

        return view;
    }

    boolean checkname() {
        String name = nameTextInputEditText.getText().toString();

        Pattern pattern = Pattern.compile(ut.nameregex());
        if (pattern.matcher(name).matches()) {
            nameTextInputLayout.setError(null);
            return true;
        } else {
            nameTextInputLayout.setError(getString(R.string.name_validate));
            return false;
        }
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateTextInputEditText.setText(sdf.format(myCalendar.getTime()));
    }


    boolean checkmobile() {
        String mobile = mobileTextInputEditText.getText().toString();

        Pattern pattern = Pattern.compile(ut.mobileregex());
        if (pattern.matcher(mobile).matches()) {
            mobileTextInputLayout.setError(null);
            return true;
        } else {
            mobileTextInputLayout.setError(getString(R.string.mobile_validate));
            return false;
        }
    }

    boolean checkdateofbirth() {
        String date = dateTextInputEditText.getText().toString();
        Pattern pattern = Pattern.compile(ut.dateregex());
        if (pattern.matcher(date).matches()) {
            dateTextInputLayout.setError(null);
            return true;
        } else {
            dateTextInputLayout.setError(getString(R.string.date_validate));
            return false;
        }
    }


    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== PICK_IMAGE_REQUEST  && resultCode == RESULT_OK && data!=null && data.getData()!=null) {
            profile_pic = data.getData();
            image_selector=true;
            toastMessage(image_selector.toString());
            nav_profile_image.setImageURI(profile_pic);
            pic_selected=true;
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkFilePermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck =getContext().checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck += getContext().checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    private void toastMessage(String message){
        Toast.makeText(getActivity(),"EDIT_PRO "+message,Toast.LENGTH_SHORT).show();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver() ;
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}