package com.example.noticeboard.Fragments;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.noticeboard.Activity.MainActivity;
import com.example.noticeboard.Model.Post_Information;
import com.example.noticeboard.R;
import com.example.noticeboard.Model.UserInformation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


public class Make_a_post_Fragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnLongClickListener, View.OnDragListener {

    private static final String LOG_TAG = "Make_a_post_Fragment";
    private Boolean pic_selected=false;
    private ImageView  add_image;
    private EditText make_a_post_edittext;
    private Uri profile_pic_uri,uri_audio_file;
    private static final int PICK_IMAGE_REQUEST = 1000;
    private BottomNavigationView navigation;
    private int OPEN_GALLERY=0;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mediaPlayer;
    private String AudioSavePathInDevice = null;
    private MediaRecorder mediaRecorder ;
    public static final int RequestPermissionCode = 1;
    private boolean showingFirst = true;
    private String mFileName = null;
    private MenuItem menuItem;
    private String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    private Random random;
    private ToggleButton play_pause;
    private ViewStub stub;
    //Firebase
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;

    //int
    private int post_no;

    //String
    private String userID, current_Date, current_Time,user_name,photoStringLink,audiostringLink,profile_pic;

    // Boolean
    private Boolean audio_recorded_flag=false;


    private ImageView drag_drop;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_make_a_post, container, false);
        make_a_post_edittext=view.findViewById(R.id.make_post_edittext);
        stub = view.findViewById(R.id.layout_stub);
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-d");
        current_Date = date.format(new Date());

        drag_drop=view.findViewById(R.id.delete_drag_and_drop);
        random = new Random();
        navigation= view.findViewById(R.id.bottom_navigation);
        add_image=view.findViewById(R.id.add_image);
        navigation.setOnNavigationItemSelectedListener(this);


        stub.setOnLongClickListener(this);
        add_image.setOnLongClickListener(this);
        drag_drop.setOnDragListener(this);

        stub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                View.DragShadowBuilder mShadow = new View.DragShadowBuilder(v);
//                ClipData data = ClipData.newPlainText("","");
//                drag_drop.setVisibility(View.VISIBLE);
//                toastMessage("yes"+v.getId());
//                v.startDrag(data, mShadow, v, 0);
                toastMessage("audio");
            }
        });

        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mAuth.getCurrentUser();

        mStorageRef = FirebaseStorage.getInstance().getReference();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (user != null) {
                    // User is signed in
                    userID = user.getUid();
                    Log.d(TAG, "Hello there onAuthStateChanged:signed_in:" + user.getUid());
                    myRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            showData(snapshot);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    // User is signed out
                    Log.d(TAG, "Hello there onAuthStateChanged:signed_out");
                }
            }
        };

//        System.out.println("=================================================="+post_information.getPost_edititext());
//        System.out.println("=================================================="+userID);
//        System.out.println("=================================================="+current_Date_Time);
        return view;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_photos:
                toastMessage("open gallery");
                //OPEN_GALLERY=1;
                openGallery();
                break;

            case R.id.mic:
                if(showingFirst == true){
                    startRecording();;
                    showingFirst = false;
                }else{
                    showingFirst = true;
                    toastMessage("Recording Ended");
                    System.out.println("..............................Recording ended");
                    stopRecording();
                    stub.setLayoutResource(R.layout.music_player_view);
                    View inflatedView = stub.inflate();
                    play_pause= (ToggleButton) inflatedView.findViewById(R.id.toggleButton);
                    play_pause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked)
                            {
                                play_audio();
                            }
                            else
                            {
                                try {
                                    mediaPlayer.reset();
                                    mediaPlayer.prepare();
                                    mediaPlayer.stop();
                                    mediaPlayer.release();
                                    mediaPlayer=null;
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    }
                break;

            case R.id.gif:

            case R.id.nav_send:
                //myRef.child("No_post").child(current_Date).setValue(String.valueOf(post_no));
                String key=myRef.child("Post").child(current_Date).push().getKey();
                System.out.println(key);
                final boolean[] audio_success = {false};
                final boolean[] image_success = { false };

                if(pic_selected=true){
                    StorageReference storageReference= mStorageRef.child("Posts/"+current_Date+"/" + key + "/Post_image.jpg");

                    storageReference.putFile(profile_pic_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content0
                            toastMessage("Upload Success");
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {

                                @Override
                                public void onSuccess(Uri uri) {
                                    photoStringLink = uri.toString();

                                    image_success[0] =true;
                                    toastMessage("image_sucess"+image_success[0]);
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            toastMessage("Upload Failed");
                        }
                    });
                }
                else
                {
                    photoStringLink="none";
                }

                if(audio_recorded_flag==true){
                    StorageReference storageReference= mStorageRef.child("Posts/"+current_Date+"/" + key + "/Post_audio.3pg");

                    storageReference.putFile(uri_audio_file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            toastMessage("audio uploaded");

                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {

                                @Override
                                public void onSuccess(Uri uri) {
                                    audiostringLink = uri.toString();
                                    audio_success[0] =true;
                                   toastMessage("audio_sucess"+audio_success[0]);

                                }
                            });
                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {

                        }
                    });
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("==========================edit"+make_a_post_edittext.getText().toString());
                        System.out.println("==========================pic_select"+pic_selected);
                        System.out.println("==========================image"+image_success[0]);
                        System.out.println("==========================audio"+audio_success[0]);
                        System.out.println("==========================audio"+profile_pic);
                        if(!audio_success[0])
                            audiostringLink="null";
                        if((make_a_post_edittext.getText().toString()!=null || pic_selected==true)&& (image_success[0]==true || audio_success[0]==true)){
                            Post_Information post_information=new Post_Information(make_a_post_edittext.getText().toString(),photoStringLink,user_name,userID,audiostringLink,profile_pic);
                            myRef.child("Post").child(current_Date).child(key).setValue(post_information);
                            System.out.println("==========================post sucessful");
                            toastMessage("post successful");
                        }
                        else
                        {
                            toastMessage("Nothing to post");
                        }
                    }
                }, 10000);




                break;
        }
        return false;
    }


    private void showData(@NotNull DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            System.out.println("================showdata");
            System.out.println("================Users"+ds.getKey());
         //   System.out.println("============================child"+ds.child("Users").hasChild("Administrative"));
            if(ds.getKey().contains("Administrative")){

                if(ds.hasChild(userID)){

                    user_name=ds.child(userID).getValue(UserInformation.class).getName().toString();
                    profile_pic=ds.child(userID).getValue(UserInformation.class).getImageUrl().toString();
                }
            }
            else if(ds.getKey().contains("Professor")){
                if(ds.hasChild(userID))
                {
                    user_name=ds.child(userID).getValue(UserInformation.class).getName().toString();
                    profile_pic=ds.child(userID).getValue(UserInformation.class).getImageUrl().toString();
                }
            }
            else if(ds.getKey().contains("Student")){
                System.out.println("================Student");
                if(ds.hasChild(userID)){
                    System.out.println("================userID"+userID);
                    user_name=ds.child(userID).getValue(UserInformation.class).getName().toString();
                    profile_pic=ds.child(userID).getValue(UserInformation.class).getImageUrl().toString();
                }
            }
        }
    }
    private void play_audio(){
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(AudioSavePathInDevice);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startRecording() {

        if(checkPermission()) {
            SimpleDateFormat format=new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.ENGLISH);
            Date now= new Date();
            AudioSavePathInDevice = getActivity().getExternalFilesDir("/").getAbsolutePath()+"/"+"AudioRecording"+format.format(now)+".3gp";
            mediaRecorder=new MediaRecorder();
            mediaRecorder.reset();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(AudioSavePathInDevice);
            mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);


            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Toast.makeText(getContext(), "Recording started",
                    Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }

    }
    private void stopRecording(){
        try{
            //mediaPlayer.stop();

            mediaRecorder.stop();
            mediaRecorder.reset();
            //  mediaPlayer.release();
//                mediaRecorder=null;
            audio_recorded_flag=true;
            uri_audio_file = Uri.fromFile(new File(AudioSavePathInDevice).getAbsoluteFile());
            toastMessage("======="+uri_audio_file.toString());
            Toast.makeText(getContext(), "Recording Completed",
                    Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            System.out.println("Not going good............................."+e.getMessage());
            System.out.println("Not going goo.................................d"+e.getCause());
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
            profile_pic_uri=data.getData();
            add_image.setImageURI(profile_pic_uri);
            pic_selected=true;

        }
    }


    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(getContext(), "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(),"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getContext(),
                RECORD_AUDIO);
        if( result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED)
        {
            toastMessage("Permission given");
            return true;
        }
        else {
            return false;
        }

    }

    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(RandomAudioFileName.charAt(random.nextInt(RandomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();
    }



    private void toastMessage(String message){
        Toast.makeText(getContext(),"MAKE A POST"+message,Toast.LENGTH_SHORT).show();
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


    @Override
    public boolean onLongClick(View v) {
        View.DragShadowBuilder mShadow = new View.DragShadowBuilder(v);
        ClipData data = ClipData.newPlainText("","");
        drag_drop.setVisibility(View.VISIBLE);
        toastMessage("yes"+v.getId());
        v.startDrag(data, mShadow, v, 0);

        return true;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        final View view=(View) event.getLocalState();

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:

                ((ImageView) v).setColorFilter(R.color.colorLightGreyTransparent);
                return true;

            case DragEvent.ACTION_DRAG_ENTERED:
                ((ImageView) v).setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
               try{
                   switch (view.getId()) {
                           case R.id.layout_stub:
                               toastMessage("stub");
                               break;

//                    case R.id.add_image:
//                        toastMessage("image");
//                        break;
                   }
               }catch(Exception e){
                   System.out.println("==========================="+e.getMessage()+"   "+e.getCause());
               }
                v.invalidate();
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                return true;

            case DragEvent.ACTION_DRAG_EXITED:

////                ((ImageView) v).clearColorFilter();
//                ((ImageView) v).setColorFilter(Color.YELLOW);
//
//                v.invalidate();
                return true;

            case DragEvent.ACTION_DROP:

                drag_drop.setVisibility(View.INVISIBLE);
//                clipData = event.getClipDescription().getLabel().toString();
//                Toast.makeText(getActivity().getApplicationContext(),clipData, Toast.LENGTH_SHORT).show();
                switch (view.getId()) {
                    case R.id.layout_stub:
                        toastMessage("stub");
                        break;
                    case R.id.add_image:
                        add_image.setImageResource(0);
                        break;
                }
                v.invalidate();

//                v.invalidate();
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                ((ImageView) v).setColorFilter(R.color.colorPrimaryDark);
                drag_drop.setVisibility(View.INVISIBLE);
//                ((ImageView) v).clearColorFilter();
//                if (event.getResult()) {
//                    Toast.makeText(getContext(), "Awesome!", Toast.LENGTH_SHORT).show();
//
//                } else {
//                    Toast.makeText(getContext(), "Aw Snap! Try dropping it again", Toast.LENGTH_SHORT).show();
//                }
                return true;

            default:
                return false;
        }
    }

}