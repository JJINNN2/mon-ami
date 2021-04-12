package com.dlab.monami;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    ArrayList<String> data;
    ArrayAdapter<String> arrayAdapter;

    TextView signupbtn;

    String TAG="signupActivity";

    //Firebase ------------------------------
    private DatabaseReference mPostReference;

    // google -------------------------------------------------------
    public static final String GOOGLE_ACCOUNT = "google_account";
    private TextView nameET, emailET, passwordET, passwordconfirmET;
    private GoogleSignInAccount mGoogleSignInAccount;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth mAuth;
    private String name="", email="";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mPostReference= FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        if(intent.getExtras()!=null) {
            name = intent.getExtras().getString("name", "");
            email = intent.getExtras().getString("email", "");
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //파이어베이스 접근 설정
        // user = firebaseAuth.getCurrentUser();
        firebaseAuth =  FirebaseAuth.getInstance();
        //firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();

        nameET = findViewById(R.id.username);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        passwordconfirmET = findViewById(R.id.passwordETconfirm);

        mGoogleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

        if(email!=null) emailET.setText(email);
        if(name!=null) nameET.setText(name);

        //setDataOnView();

        signupbtn = findViewById(R.id.singupBtn);
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = nameET.getText().toString();
                email = emailET.getText().toString();
                signUp();
            }
        });
    }

    private void signUp(){
        final String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        String passwordCheck = passwordconfirmET.getText().toString().trim();

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("account_list");
        Query query=ref.orderByChild("name").equalTo("david"); // ========================= TEMP NAME : david

        if (email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0) {
            if (password.equals(passwordCheck)) {
//                        final RelativeLayout loaderLayout = findViewById(R.id.loaderLyaout);
//                        loaderLayout.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                        loaderLayout.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.getChildrenCount()>0){ // Username found
                                                Toast.makeText(SignupActivity.this,"already exists.", Toast.LENGTH_SHORT).show();
                                            }
                                            else{ //Username not found
                                                Toast.makeText(SignupActivity.this,"add success", Toast.LENGTH_SHORT).show();
                                                postFirebaseAccountDatabase(true, email, name);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(SignupActivity.this,"Error", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    Toast.makeText(SignupActivity.this, "회원가입에 성공하였습니다. 로그인해주세요", Toast.LENGTH_SHORT).show();
                                    Intent gotomain = new Intent(SignupActivity.this, LoginActivity.class);
                                    startActivity(gotomain);
                                } else {
                                    if (task.getException() != null) {
                                        Toast.makeText(SignupActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            } else {
                Toast.makeText(SignupActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(SignupActivity.this, "이메일 또는 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    public void postFirebaseAccountDatabase(boolean add, String email, String name){
        Map<String,Object> childUpdates=new HashMap<>();
        Map<String,Object> postValues=null;
        if(add) {
            FirebaseAccountPost post = new FirebaseAccountPost(email, name);
            postValues = post.toMap();
        }
        Log.d("FirebasePost","added to account list : "+email+name);
        childUpdates.put("/account_list/"+name,postValues);
        mPostReference.updateChildren(childUpdates);
        //clearET();
    }

    /*
    private void setDataOnView() {
        // implementation 'com.squareup.picasso:picasso:2.71828'
        profileName.setText(mGoogleSignInAccount.getDisplayName());
        profileEmail.setText(mGoogleSignInAccount.getEmail());
    }*/
}
