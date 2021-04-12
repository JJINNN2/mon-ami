package com.dlab.monami;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {


    EditText usernameET,passwordET;

    ArrayList<String> data;
    private FirebaseAuth mAuth;

    private DatabaseReference mPostReference;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        data=new ArrayList<String>();
        usernameET=(EditText) findViewById(R.id.userid);
        passwordET=(EditText) findViewById(R.id.password);




        if(getIntent().getExtras() != null){
            EditText username = (EditText)findViewById(R.id.userid);
            Intent signupIntent = getIntent();
            username.setText(signupIntent.getStringExtra("Username"));
        }

        TextView login = findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    login();
            }
        });

        TextView signup = (TextView)findViewById(R.id.findidpw);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "서비스 준비중입니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login() {
        final String checkEmail;

        String email = usernameET.getText().toString();
        String password = passwordET.getText().toString();
        Log.d("LoginActivity", "id:" + email + " pw:" + password);
        mAuth = FirebaseAuth.getInstance();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing in...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);

        mPostReference = FirebaseDatabase.getInstance().getReference();
        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference().child("account_list"); // DB 테이블 연결



        if (email.length() > 0 && password.length() > 0) {
//            final RelativeLayout loaderLayout = findViewById(R.id.loaderLyaout);
//            loaderLayout.setVisibility(View.VISIBLE);
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //loaderLayout.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // Get Post object and use the values to update the UI
                                        if(dataSnapshot.getValue(AccountPost.class) != null){

                                            final ValueEventListener postListener=new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    Log.d("onDataChange","Data is Updated");
                                                    for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                                                        String key=postSnapshot.getKey();
                                                        AccountPost get = postSnapshot.getValue(AccountPost.class);
                                                        String info[] = {get.name, get.email};

                                                        if(email.equals(get.email)==true){
                                                            Intent gotomain = new Intent(LoginActivity.this, MainActivity.class);
                                                            gotomain.putExtra("name", get.name);
                                                            gotomain.putExtra("email", email);
                                                            progressDialog.dismiss();
                                                            startActivity(gotomain);
                                                            finish();
                                                        }else{
//                                                            Toast.makeText(LoginActivity.this,"", Toast.LENGTH_SHORT).show();
                                                        }

                                                        Log.d("login_check","info: "+ info[0]+" "+ info[1]);
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            };
                                            mPostReference.child("account_list").addValueEventListener(postListener);

                                        } else {
                                            Toast.makeText(LoginActivity.this, "데이터 없음...", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Getting Post failed, log a message
                                        Log.w("login_check", "loadPost:onCancelled", databaseError.toException());
                                    }
                                });


                            } else {
                                if (task.getException() != null) {
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        } else {
            Toast.makeText(LoginActivity.this, "이메일 또는 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
        }

    }


    public void getFirebaseDatabase(){
        final ValueEventListener postListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange","Data is Updated");
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String key=postSnapshot.getKey();
                    AccountPost get = postSnapshot.getValue(AccountPost.class);
                    String[] info={get.name, get.email};


                    Log.d("login_check","info: "+info[0]+" "+info[1]);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mPostReference.child("account_list").addValueEventListener(postListener);
    }
}