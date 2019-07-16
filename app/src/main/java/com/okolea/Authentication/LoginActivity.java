package com.okolea.Authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.okolea.R;
import com.okolea.casualty;
import com.okolea.institution;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText mEmail, mPassword;
    private Button mLogin, btn2;
    private TextView register;
    private ProgressDialog mProgressDialog;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private DatabaseReference mUserDatabase;
    private String mRole;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        mProgressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
//        btn2 = findViewById(R.id.anon);
        register = findViewById(R.id.register);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

            }
        });
//       btn2.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View v) {
//               startActivity(new Intent(LoginActivity.this, MainActivity.class));
//
//           }
//       });


        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){

                    String user_id = mAuth.getCurrentUser().getUid();
                    mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                    mUserDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                if(map.get("role")!=null){
                                    mRole = map.get("role").toString();

                                    if (mRole.equals("Casualty"))
                                        intent = new Intent(LoginActivity.this, casualty.class);
                                    else if(mRole.equals("Institution"))
                                        intent = new Intent(LoginActivity.this, institution.class);

                                    startActivity(intent);
                                    finish();
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
        };

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);

        mLogin = (Button) findViewById(R.id.login);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email Required");
                    mEmail.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password required");
                    mPassword.requestFocus();
                    return;
                }
                mProgressDialog.setMessage("Processing...Please Wait");
                mProgressDialog.show();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressDialog.show();
                        if(!task.isSuccessful()){
                            mProgressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "sign in error", Toast.LENGTH_SHORT).show();
                        }
                        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                        mUserDatabase.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                    Toast.makeText(LoginActivity.this, "SUCCESSFUL", Toast.LENGTH_SHORT).show();
                                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                    if(map.get("role")!=null){
                                        mRole = map.get("role").toString();

                                        if (mRole.equals("Casualty"))
                                            intent = new Intent(LoginActivity.this, casualty.class);
                                        else if(mRole.equals("Institution"))
                                            intent = new Intent(LoginActivity.this, institution.class);

                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

//                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        mProgressDialog.dismiss();
                    }
                });

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
         finishAffinity();
    }
}
