package com.example.noticeboard.Fragments;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.noticeboard.Activity.Front_Page;
import com.example.noticeboard.R;
import com.example.noticeboard.Model.UserInformation;
import com.example.noticeboard.utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

public class Register_info_Fragment extends Fragment implements View.OnClickListener {
    private static final int PICK_IMAGE_REQUEST = 1000;
    private static final String TAG = "AddToDatabase";
    //add Firebase Database stuff
    private String userID;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String GENDER;
    private Boolean pic_selected=false;
    private StorageReference mStorageRef;
    private Uri profile_pic;
    private Button registrationButton;
    private CircularImageView reg_profile_picture;
    private TextInputLayout country_spinner_layout, nameTextInputLayout,countryTextInputLayout, mobileTextInputLayout, dateTextInputLayout,occupation_spinner_layout;
    private TextInputEditText nameTextInputEditText, mailTextInputEditText, mobileTextInputEditText, dateTextInputEditText;
    private AutoCompleteTextView country_spinner_textview, occupation_spinner_textview;
    private utils ut = new utils();
    private Boolean gender_selector=false;
    private Boolean occupation_selector=false, country_selector=false, image_selector=false;
    private RadioGroup radioGroup;
    private String[] countries = new String[]{"Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegowina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory", "Brunei Darussalam", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo", "Congo, the Democratic Republic of the", "Cook Islands", "Costa Rica", "Cote d'Ivoire", "Croatia (Hrvatska)", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Falkland Islands (Malvinas)", "Faroe Islands", "Fiji", "Finland", "France", "France Metropolitan", "French Guiana", "French Polynesia", "French Southern Territories", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Heard and Mc Donald Islands", "Holy See (Vatican City State)", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran (Islamic Republic of)", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, Democratic People's Republic of", "Korea, Republic of", "Kuwait", "Kyrgyzstan", "Lao, People's Democratic Republic", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libyan Arab Jamahiriya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia, The Former Yugoslav Republic of", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia, Federated States of", "Moldova, Republic of", "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russian Federation", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Seychelles", "Sierra Leone", "Singapore", "Slovakia (Slovak Republic)", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Georgia and the South Sandwich Islands", "Spain", "Sri Lanka", "St. Helena", "St. Pierre and Miquelon", "Sudan", "Suriname", "Svalbard and Jan Mayen Islands", "Swaziland", "Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan, Province of China", "Tajikistan", "Tanzania, United Republic of", "Thailand", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "United States Minor Outlying Islands", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam", "Virgin Islands (British)", "Virgin Islands (U.S.)", "Wallis and Futuna Islands", "Western Sahara", "Yemen", "Yugoslavia", "Zambia", "Zimbabwe"};
    private String[] occupation=new String[]{"Student","Professor","Administrative"};
    private final Calendar myCalendar = Calendar.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.registration_info_fragment, container, false);

        country_spinner_layout=view.findViewById(R.id.country_spinner);
        country_spinner_textview=view.findViewById(R.id.country_spinner_selector);
        occupation_spinner_layout=view.findViewById(R.id.occupation_textinputlayout);
        occupation_spinner_textview=view.findViewById(R.id.occupation_selector);
        nameTextInputEditText = view.findViewById(R.id.name_textinputedittext);
        mobileTextInputEditText = view.findViewById(R.id.mobile_textinputedittext);
        dateTextInputEditText = view.findViewById(R.id.dob_textinputedittext);
        nameTextInputLayout = view.findViewById(R.id.name_textinputlayout);
        mobileTextInputLayout = view.findViewById(R.id.mobile_textinputlayout);
        dateTextInputLayout = view.findViewById(R.id.dob_textinputlayout);
        radioGroup=view.findViewById(R.id.gender_radiogroup);
        reg_profile_picture = view.findViewById(R.id.reg_profile_pic_imageview);
        registrationButton = view.findViewById(R.id.registration_outlinedButton);

        registrationButton.setOnClickListener(this);
        reg_profile_picture.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                gender_selector=true;
                if(i==0)
                    GENDER="Male";
                else if(i==1)
                    GENDER="Female";
                else if(i==2)
                    GENDER="Others";
                toastMessage("radiogroup");
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

        nameTextInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkname();
                }
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

        mobileTextInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkmobile();
                }
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


        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        checkFilePermissions();
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


        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "onDataChange: Added information to database: \n" +
                        dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Submit pressed.");
                String name = nameTextInputEditText.getText().toString();
                String phoneNum = mobileTextInputEditText.getText().toString();
                String occupation=occupation_spinner_textview.getText().toString();
                String country=country_spinner_textview.getText().toString();
                String DOB=dateTextInputEditText.getText().toString();
                final String[] Image_uri = new String[1];
                StorageReference storageReference= mStorageRef.child("Users/Student/" + userID + "/" + name + ".jpg");

                //handle the exception if the EditText fields are null
                if(checkname()  && checkmobile() && checkdateofbirth() && image_selector  ){

                    switch(occupation){
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
                                    String photoStringLink = uri.toString();
                                    UserInformation userInformation = new UserInformation(name,GENDER,phoneNum,occupation,country, photoStringLink,DOB);

                                    toastMessage(photoStringLink);
                                    switch(occupation){
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
                        }
                    });






                    toastMessage("New Information has been saved.");

                }else{
                    toastMessage("Fill out all the fields");
                }

            }

        });
        ArrayAdapter adapter1 = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, occupation);
        occupation_spinner_textview.setAdapter(adapter1);

        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, countries);
        country_spinner_textview.setAdapter(adapter);

        occupation_spinner_textview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                occupation_selector=true;
                toastMessage("occupation");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        country_spinner_textview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                country_selector=true;
                toastMessage("country");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return view;
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateTextInputEditText.setText(sdf.format(myCalendar.getTime()));
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



    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.reg_profile_pic_imageview:
                openGallery();
                break;


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
            reg_profile_picture.setImageURI(profile_pic);
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
        Toast.makeText(getContext(),"REG_INFO "+message,Toast.LENGTH_SHORT).show();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver() ;
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
