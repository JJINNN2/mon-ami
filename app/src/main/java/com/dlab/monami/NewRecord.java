package com.dlab.monami;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class NewRecord extends AppCompatActivity {
    //Firebase ------------------------------
    private DatabaseReference mPostReference;
    static String patient_name="";
    static String writer_name="";
    static String title="", symptom="", img="", comment="", recordtime="";


    //xml layout ----------------------------
    ImageButton backButton, datePickButton;
    ImageButton imgUploadButton;
    EditText titleET, commentET;
    AutoCompleteTextView symptomET;

    //image ---------------------------------
    private static final int PICK_IMAGE=777;
    private StorageReference mStorageRef;
    Uri currentImageUri;
    boolean imagecheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newrecordscreen);

        Intent intent = getIntent();
        writer_name = intent.getExtras().getString("writer");
        patient_name = intent.getExtras().getString("writer");

        // 환자 이름
        // 기록자 이름
        // intent 가져오기

        backButton = (ImageButton) findViewById(R.id.backbtn);
        datePickButton = (ImageButton) findViewById(R.id.downbtn);
        imgUploadButton = (ImageButton) findViewById(R.id.imguploadbtn);
        titleET = findViewById(R.id.input_title);
        symptomET = findViewById(R.id.input_symptom);
        commentET = findViewById(R.id.input_comment);

        mPostReference= FirebaseDatabase.getInstance().getReference();
        mStorageRef= FirebaseStorage.getInstance().getReference("Images");

//        Spinner spinner = (Spinner) findViewById(R.id.symptoms_spinner); // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.my_array, android.R.layout.simple_spinner_item); // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Apply the adapter to the spinner
//        spinner.setAdapter(adapter);

        // BackButton -------------------------------------------
        backButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        // Text change listeners -------------------------------------------
        /*
        titleET.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if(titleET.getText().toString().isEmpty()){
                    titleET.setBackgroundResource(R.drawable.titleborderbox);
                    titleET.setPadding(18,0,0,0);
                }else {
                    titleET.setBackgroundResource(R.drawable.titleborderbox_selected);
                    titleET.setPadding(18,0,0,0);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        symptomET.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if(symptomET.getText().toString().isEmpty()){
                    symptomET.setBackgroundResource(R.drawable.titleborderbox);
                    symptomET.setPadding(18,0,0,0);
                }else {
                    symptomET.setBackgroundResource(R.drawable.titleborderbox_selected);
                    symptomET.setPadding(18,0,0,0);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

*/
        // Save Button Click ------------------------------------
        TextView saveBtn = (TextView)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("saveButton","pressed");

                title=titleET.getText().toString();
                symptom=symptomET.getText().toString();
                comment=commentET.getText().toString();

                Date currentTime = Calendar.getInstance().getTime();
                final String date_text = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(currentTime);
                Log.d("Saved time", date_text);

                if(title.length()==0){
                    Toast.makeText(NewRecord.this,"Please fill in required values.", Toast.LENGTH_SHORT).show();
                }
                else {
                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("patient_list");
                    Query query=ref.orderByChild("name").equalTo("david"); // ========================= TEMP NAME : david

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getChildrenCount()>0){ // Username found
                                Toast.makeText(NewRecord.this,"already exists.", Toast.LENGTH_SHORT).show();
                            }
                            else{ //Username not found
                                Toast.makeText(NewRecord.this,"add success", Toast.LENGTH_SHORT).show();

                                //postFirebaseDatabase(true);

                                //Image upload
                                if(imagecheck){
                                    Log.d("img","check=true");
                                    Log.d("imguri", date_text+"_"+currentImageUri.getLastPathSegment());
                                    final StorageReference riverseRef=mStorageRef.child(date_text+"_"+currentImageUri.getLastPathSegment());
                                    final UploadTask uploadTask=riverseRef.putFile(currentImageUri);
                                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                        @Override
                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                            if(!task.isSuccessful()){
                                                //throw.task.getException();
                                            }
                                            return riverseRef.getDownloadUrl();
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if(task.isSuccessful()){
                                                Uri downloadUri = task.getResult();
                                                Log.d("imageLog", String.valueOf(downloadUri));
                                                img = String.valueOf(downloadUri);
                                                setImgUrl(String.valueOf(downloadUri));
                                                postFirebaseDatabase(true, img, date_text);
                                            }else{

                                            }
                                        }
                                    });
                                }
                                else{ // upload with no image
                                    Log.d("img","check=false");
                                    postFirebaseDatabase(true, date_text);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(NewRecord.this,"Error", Toast.LENGTH_SHORT).show();
                        }
                    });

                    finish();
                }
            }
            public void setImgUrl(String url){
                img = url;
                //this.img = url;
                Log.d("imageLog","this img"+img);
            }
        });


        // Image button click ---------------------------------------------------------------------------
        ImageButton image=(ImageButton)findViewById(R.id.imguploadbtn);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });

        // Auto complete box -------------------------------------------------------------------------
        ArrayAdapter symptom_adapter = ArrayAdapter.createFromResource(this, R.array.my_array, android.R.layout.simple_spinner_item);
        final AutoCompleteTextView autoTextView = (AutoCompleteTextView) findViewById(R.id.input_symptom);
        autoTextView.setAdapter(symptom_adapter);
        autoTextView.setThreshold(1);
        autoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoTextView.showDropDown();
            }
        });
        ImageButton downbtn=(ImageButton)findViewById(R.id.search_btn);
        downbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoTextView.showDropDown();
            }
        });

    }

    public void postFirebaseDatabase(boolean add, String timestamp){
        Map<String,Object> childUpdates=new HashMap<>();
        Map<String,Object> postValues=null;
        if(add){
            FirebasePost post=new FirebasePost(0, title, symptom, "none", comment, timestamp, writer_name, false);
            postValues=post.toMap();
        }

        childUpdates.put("/patient_list/"+patient_name+"/"+timestamp,postValues);
        mPostReference.updateChildren(childUpdates);
        //clearET();
    }

    public void postFirebaseDatabase(boolean add, String img, String timestamp){
        Map<String,Object> childUpdates=new HashMap<>();
        Map<String,Object> postValues=null;
        if(add){
            FirebasePost post=new FirebasePost(0, title, symptom, img, comment, timestamp, writer_name, false);
            postValues=post.toMap();
        }
        childUpdates.put("/patient_list/"+patient_name+"/"+timestamp,postValues);
        mPostReference.updateChildren(childUpdates);
        clearET();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if(requestCode==PICK_IMAGE){
                ImageView img = (ImageView) findViewById(R.id.imguploadbtn);
                currentImageUri = data.getData();
                imagecheck =true;
                img.setImageURI(currentImageUri);
                img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        } else {
            Log.d("ActivityResult","Nothing selected");
            Toast.makeText(NewRecord.this,"No photo selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void clearET(){
        titleET.setText("");
        symptomET.setText("");
        commentET.setText("");
        img="";
    }

    @Override
    public void finish() {
        clearET();
        super.finish();
        //overridePendingTransition(R.anim.slide_down,R.anim.slide_down);
    }
}
