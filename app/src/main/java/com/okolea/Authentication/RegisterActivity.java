package com.okolea.Authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;


import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;


import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.okolea.R;
import com.okolea.casualty;
import com.okolea.institution;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    private EditText mEmail, mPassword,mEditTextPhone, Firstname, SecondName,Username,PasswordAgain;
    private Button mRegistration, choosebtn;
    private ProgressDialog mProgressDialog;
    TextView mlogin;

    private FirebaseAuth mAuth;
    private String mRole;

    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath;
    private RadioGroup mRadioGroup;
    private  StorageReference refstorage;

    private final int PICK_IMAGE_REQUEST = 71;

    private ImageView image;

    private DatabaseReference mUserDatabase;

    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_register);
        mlogin = findViewById(R.id.login);

        mAuth = FirebaseAuth.getInstance();

        image = findViewById(R.id.imgView);

        storage = FirebaseStorage.getInstance();

        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);

        mRadioGroup.check(R.id.casualty);

        storageReference = storage.getReference();

        choosebtn = findViewById(R.id.btnChoose);


        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

            }
        });

        choosebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();

            }
        });




        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                    mUserDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                if(map.get("role")!=null){
                                    mRole = map.get("role").toString();

                                    if (mRole.equals("Casualty"))
                                        intent = new Intent(RegisterActivity.this, casualty.class);
                                    else if(mRole.equals("Institution"))
                                        intent = new Intent(RegisterActivity.this, institution.class);

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
//        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                if(user!=null){
////                    if (mRole.equals("Courier"))
////                        intent = new Intent(RegisterActivity.this, CourierMapActivity.class);
////                    else if(mRole.equals("Customer"))
////                        intent = new Intent(RegisterActivity.this, CustomerMapActivity.class);
////
////                    startActivity(intent);
////                    finish();
////                    return;
//                }
//            }
//        };

        mEmail = (EditText) findViewById(R.id.email);

        mPassword = (EditText) findViewById(R.id.password);

        mEditTextPhone = (EditText) findViewById(R.id.login_edittext_phone);



        mRegistration = (Button) findViewById(R.id.register);

        mProgressDialog = new ProgressDialog(this);

        Firstname = (EditText) findViewById(R.id.firstname);

//        SecondName = (EditText) findViewById(R.id.lastname);

        Username = (EditText) findViewById(R.id.username);

        PasswordAgain = (EditText) findViewById(R.id.conf_password);


        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String mPhone = mEditTextPhone.getText().toString().trim();

                final String firstname = Firstname.getText().toString();
                final String username = Username.getText().toString();
                final String PassAgain = PasswordAgain.getText().toString();

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                int selectId = mRadioGroup.getCheckedRadioButtonId();
                final RadioButton radioButton = (RadioButton) findViewById(selectId);

                if(TextUtils.isEmpty(firstname)){
                    Firstname.setError("Name required");
                    Firstname.requestFocus();
                    Toast.makeText(RegisterActivity.this, "Name required", Toast.LENGTH_SHORT).show();
                    return;
                }
//                if(TextUtils.isEmpty(secondname)){
//                    SecondName.setError("LastNmae required");
//                    SecondName.requestFocus();
//                    Toast.makeText(RegisterActivity.this, "Lastname required", Toast.LENGTH_SHORT).show();
//
//                    return;
//                }

                if(TextUtils.isEmpty(username)){
                    Username.setError("Username required");
                    Username.requestFocus();
                    Toast.makeText(RegisterActivity.this, "Username required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email Required");
                    mEmail.requestFocus();
                    Toast.makeText(RegisterActivity.this, "Email required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!email.matches(emailPattern)){
                    mEmail.setError("Enter valid Email ");
                    mEmail.requestFocus();
                    Toast.makeText(RegisterActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(mPhone)){
                    mEditTextPhone.setError("EnterPhone");
                    mEditTextPhone.requestFocus();
                    Toast.makeText(RegisterActivity.this, "Phone Number required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mPhone.length() <9||mPhone.length() ==9  ){
                    mEditTextPhone.setError("Phone Number Must be 10 digits");
                    mEditTextPhone.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password required");
                    mPassword.requestFocus();
                    Toast.makeText(RegisterActivity.this, "Paasword required", Toast.LENGTH_SHORT).show();

                    return;
                }
                 if (password.length() < 6){
                    mPassword.setError("Password Require minimum of 6 characters");
                     mPassword.requestFocus();
                     Toast.makeText(RegisterActivity.this, "Password Require minimum of 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }


               if(password.length() != PassAgain.length()){
                    PasswordAgain.setError("Password Not Match");
                   PasswordAgain.requestFocus();
                   Toast.makeText(RegisterActivity.this, "Password Not Match", Toast.LENGTH_SHORT).show();
                    return;
                }


               if (radioButton.getText() == null){
                    return;
                }

                mProgressDialog.setMessage("Registering");
                mProgressDialog.show();
                uploadImage();

//
                mRole = radioButton.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(!task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "sign up error", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }else{

                            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference user_reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("User_ID");
                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("name");
                            DatabaseReference current_user_role = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("role");
                            DatabaseReference current_user_email = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("email");
                            DatabaseReference current_user_phone = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("phone");
                            DatabaseReference current_user_first = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("Fullname");


                            current_user_email.setValue(email);
                            current_user_phone.setValue(mPhone);
                            current_user_first.setValue(firstname);
//                            current_user_second.setValue(secondname);
                            current_user_db.setValue(username);
                            current_user_role.setValue(mRole);

                            user_reference.setValue(user_id);

                            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                            mUserDatabase.addValueEventListener(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                                        Toast.makeText(RegisterActivity.this, "SUCCESSFUL", Toast.LENGTH_SHORT).show();
                                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                        if(map.get("role")!=null){
                                            mRole = map.get("role").toString();

                                            if (mRole.equals("Casualty"))
                                                intent = new Intent(RegisterActivity.this, casualty.class);
                                            else if(mRole.equals("Institution"))
                                                intent = new Intent(RegisterActivity.this, institution.class);

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
                            mProgressDialog.dismiss();
                        }
                    }
                });
            }
        });
    }



    void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        image.setVisibility(View.VISIBLE);
    }


    void uploadImage(){

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Saving...Please Wait");
            progressDialog.show();

            StorageReference ref = storageReference.child("CoverPage/"+ UUID.randomUUID().toString());

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = ref.putBytes(data);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    //and you can convert it to string like this:
                    final String sdownload_url = downloadUrl.toString();


                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user!=null) {
                        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("User_ID").equalTo(user_id);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    progressDialog.dismiss();

                                    childSnapshot.getRef().child("URL").setValue(sdownload_url);

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//                            throw databaseError.toException(); // never ignore errors//                    FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
////                        @Override
////                        public void onDataChange(DataSnapshot dataSnapshot) {
////                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
////
////
////                                Log.i("db", "onDataChange: Key : " + childSnapshot.getKey());
////
////                                String myKey = childSnapshot.getKey();
////
////                                childSnapshot.getRef().child("URL").setValue(sdownload_url);
////
////                            }
////                        }
//                        }
//                    });
                    return;
                }

            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
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
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}