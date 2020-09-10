package com.shubham.recycler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 100;

    //Firebase
    FirebaseAuth fAuth;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private StorageReference mStorageRef;
    String userId;

    public int code=0;

    //views
    EditText name,phone,address,emailid,password;
    Button pick,submit;
    ImageView profile;
    TextView error;
    ProgressBar progressBar;


    public Uri uri,uri1;
    int flag=0;


    //to upload image
    public String imgName,imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //views
        name= findViewById(R.id.name);
        phone= findViewById(R.id.cell);
        address= findViewById(R.id.address);
        profile= findViewById(R.id.profile);
        emailid= findViewById(R.id.emailid);
        error= findViewById(R.id.error);
        password= findViewById(R.id.password);

        //buttons
        pick= findViewById(R.id.pic);
        submit= findViewById(R.id.submit);

        //progress bar
        progressBar= findViewById(R.id.progressBar);

        fAuth=FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        profile.setVisibility(View.GONE);

    }

    public void select(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK) {
            error.setText("");
            flag=1;
            uri = data.getData();
            profile.setImageURI(uri);
            profile.setVisibility(View.VISIBLE);
            uri1=data.getData();
        }
    }

    public void sumbit(View view) {
        submit.setEnabled(false);
        final String n,p,a,e,pass;
        n=name.getText().toString();
        p=phone.getText().toString();
        a=address.getText().toString();
        e=emailid.getText().toString();
        pass=password.getText().toString();


        if (pass.length()<6)
            password.setError("password at least 6 digit long");

        if (n.isEmpty())
            name.setError("Enter valid name");
        else if (a.isEmpty())
            address.setError("Enter valid address");
        else if(p.isEmpty())
            phone.setError("Enter valid phone no");
        else if (e.isEmpty())
            emailid.setError("Enter valid email id");
        else if (pass.isEmpty())
            password.setError("Choose valid password");
        else if (p.length()!=10)
            phone.setError("Enter valid phone no");
        //else if (flag==0)
            //error.setText("select profile image");
        else {
            progressBar.setVisibility(View.VISIBLE);


            fAuth.createUserWithEmailAndPassword(e,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        //Toast.makeText(register.this,"successfully registered",Toast.LENGTH_LONG).show();
                        userId=fAuth.getCurrentUser().getUid();
                        //sending verification mail
                        FirebaseUser firebaseUser=fAuth.getCurrentUser();
                        firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(register.this,"error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });

                        //sending details to the database
                        userId=fAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = fStore.collection("users").document(userId);
                        Map<String, Object> user = new HashMap<>();
                        user.put("name",n);
                        user.put("address",a);
                        user.put("phone",p);
                        user.put("email",e);


                        documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("success","user profile created sucessfully "+userId);
                                code=1;
                                save();
                              //  fileUploader();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("failed","user profile created failed "+e.getMessage());
                            }
                        });
                        startActivity(new Intent(register.this,ProfileUploadVendor.class));
                        finish();
                    }
                    else {
                        Toast.makeText(register.this,"Error while registering: "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        submit.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void save() {
        SharedPreferences preferences = getSharedPreferences("setting", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("vUid",userId);
        editor.putInt("code",code);
        editor.apply();
        Toast.makeText(register.this,"saved.",Toast.LENGTH_LONG).show();
    }




  /*  private void uploadImgToFirebase(String imgName, Uri uri) {
        final StorageReference image = mStorageRef.child("profiles/"+imgName);
        image.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri sUri) {
                        Log.d("success","image uploaded successfully to cloud");
                    }
                });
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(register.this,"image not saved in database",Toast.LENGTH_LONG).show();
            }
        });
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
