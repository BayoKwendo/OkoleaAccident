package com.okolea;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
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

import com.bumptech.glide.Glide;
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
import com.okolea.Authentication.LoginActivity;
import com.okolea.hospital.activities.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class casualty extends AppCompatActivity {
    private EditText Fullname,mphonenumber, mdescription;
    private Button mRegistration, choosebtn, mconfirm;
    private RadioGroup mRadioGroup;
    private ProgressDialog mProgressDialog;
    TextView mlogin;
    private Uri filePath;
    FirebaseStorage storage;

    private Boolean isLoggingOut = false;
    StorageReference storageReference;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private String mRole;
    private Intent intent;
    private final int PICK_IMAGE_REQUEST = 71;
    private ImageView img, imageview ;
    DatabaseReference casulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_casualty);
        img = findViewById(R.id.imgView);
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        choosebtn = findViewById(R.id.btnChoose);
        mRadioGroup.check(R.id.hospital);

        imageview = findViewById(R.id.imView);
        Fullname = findViewById(R.id.title);
        mdescription = findViewById(R.id.description1);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mconfirm = (Button) findViewById(R.id.post);
        mphonenumber = findViewById(R.id.login_edittext_phone);
        casulty = FirebaseDatabase.getInstance().getReference().child("Casualty_Requests");
        Display();


        choosebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();

            }
        });

        mAuth = FirebaseAuth.getInstance();
//
//        mAuth = FirebaseAuth.getInstance();

//
//        mlogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
//
//            }
//        });
//        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                if(user!=null){
//                    if (mRole.equals("Author"))
//                        intent = new Intent(RegisterActivity.this, MainActivity.class);
//                    else if(mRole.equals("Publisher"))
//                        intent = new Intent(RegisterActivity.this, Publisher.class);
//
//                    startActivity(intent);
//                    finish();
//                    return;
//                }
//            }
//        };
//
//        mEmail = (EditText) findViewById(R.id.email);
//
//        mPassword = (EditText) findViewById(R.id.password);
//
//        mEditTextPhone = (EditText) findViewById(R.id.login_edittext_phone);
//
//
//
        mProgressDialog = new ProgressDialog(this);
//
//        Firstname = (EditText) findViewById(R.id.firstname);
//
//        SecondName = (EditText) findViewById(R.id.lastname);
//
//        Username = (EditText) findViewById(R.id.username);
//
//        PasswordAgain = (EditText) findViewById(R.id.conf_password);


        mconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String fullnME = Fullname.getText().toString();
                final String mPhone = mphonenumber.getText().toString();
                final String description = mdescription.getText().toString();

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                int selectId = mRadioGroup.getCheckedRadioButtonId();
                final RadioButton radioButton = (RadioButton) findViewById(selectId);

                if(TextUtils.isEmpty(fullnME)){
                    Fullname.setError("Name required");
                    Fullname.requestFocus();
                    Toast.makeText(casualty.this, "Name required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(mPhone)){
                    mphonenumber.setError("Phone number required");
                    mphonenumber.requestFocus();
                    Toast.makeText(casualty.this, "Phone required", Toast.LENGTH_SHORT).show();

                    return;
                }

                if(TextUtils.isEmpty(description)){
                    mdescription.setError("Description required");
                    mdescription.requestFocus();
                    Toast.makeText(casualty.this, "Description required", Toast.LENGTH_SHORT).show();
                    return;
                }

               if (radioButton.getText() == null){
                    return;
                }
                mProgressDialog.setMessage("Saving... Kindly Wait");
                mProgressDialog.show();
//
                mRole = radioButton.getText().toString();



                Map topicMap = new HashMap();
                topicMap.put("Name", fullnME);
                topicMap.put("Phone Number", mPhone);
                topicMap.put("Description", description);
                topicMap.put("Deliverable", mRole);

                uploadImage();
                casulty.push().setValue(topicMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            mProgressDialog.dismiss();
                            Query query = casulty.limitToLast(1);
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                        String deliverable = (String) childSnapshot.child("Deliverable").getValue();

                                        Toast.makeText(casualty.this, "" + deliverable, Toast.LENGTH_SHORT).show();
                                        if (deliverable.equals("Hospital")){
                                            startActivity(new Intent(casualty.this, MainActivity.class));
                                        }
                                        if (deliverable.equals("Insurance")){
                                            startActivity(new Intent(casualty.this, com.okolea.MInsurance.MainActivity.class));

                                        }
                                        if (deliverable.equals("Police")){
                                            startActivity(new Intent(casualty.this, com.okolea.police.activities.MainActivity.class));


                                        }


//                                        if (username!= null) {
//                                            mRole = map.get("role").toString();
//                                            if (mRole.equals("Hospital"))
//                                                Toast.makeText(casualty.this, "Hospital", Toast.LENGTH_SHORT).show();
//                                            if (mRole.equals("Insurance"))
//                                                Toast.makeText(casualty.this, "Insurance", Toast.LENGTH_SHORT).show();
//                                            else if (mRole.equals("Police"))
//                                                Toast.makeText(casualty.this, "Police", Toast.LENGTH_SHORT).show();
//                                        }

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


        }});
    }

    void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        imageview.setVisibility(View.VISIBLE);
    }


    void uploadImage(){

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Saving...Please Wait");
            progressDialog.show();

            StorageReference ref = storageReference.child("UploadedFiles/"+ UUID.randomUUID().toString());

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
                    progressDialog.dismiss();



                    Query query = FirebaseDatabase.getInstance().getReference().child("Casualty_Requests").limitToFirst(1);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                childSnapshot.getRef().child("URL").setValue(sdownload_url);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

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

            }
            );
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
                imageview.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    void Display() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("User_ID").equalTo(user_id);
            query.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                        String URLs = (String) childSnapshot.child("URL").getValue();
                        String BookTitle = (String) childSnapshot.child("Fullname").getValue();
                        String phones = (String) childSnapshot.child("phone").getValue();
                        // Do something


                        Fullname.setText(BookTitle);
                        mphonenumber.setText(String.valueOf(phones));

                        Glide.with(getApplication()).load(URLs).into(img);


                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException(); // never ignore errors
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mAuth.addAuthStateListener(firebaseAuthListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
//        mAuth.removeAuthStateListener(firebaseAuthListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        FirebaseAuth.getInstance().signOut();
//        Intent intent = new Intent(casualty.this, LoginActivity.class);
//        startActivity(intent);
//        finish();
//        Toast.makeText(this, "Sign Out Successful", Toast.LENGTH_SHORT).show();
////        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
////        startActivity(intent);
//        finish();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Return true to display menu
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as Forums specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            isLoggingOut = true;

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(casualty.this, HomePage.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Sign Out Successful", Toast.LENGTH_SHORT).show();
        }


        return super.onOptionsItemSelected(item);
    }
}
