package com.shubham.recycler;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CDashboard extends AppCompatActivity {

    private static final int RESULT_IMAGE = 999;

    ImageView fPhoto;
    public Uri filePath;
    Button fupload;

    //firebase
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    DocumentReference documentReference;
    String userId;

    //image download url
    String imageUrl;

    String ans="no";

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cdashboard);

        fPhoto = findViewById(R.id.fPhoto);
        fupload = findViewById(R.id.fUpload);

        //firebase
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("pImages");
        documentReference = fStore.collection("customers").document(userId);

        SharedPreferences preferences = getSharedPreferences("setting1", Context.MODE_PRIVATE);
        String flag = preferences.getString("pUploaded","");

        if(flag.equals("yes")){
            startActivity(new Intent(CDashboard.this,CustomerHome.class));
            finish();
        }
    }

    public void select(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,RESULT_IMAGE);
        fupload.setEnabled(true);
    }

    public void storeImg(View view) {

        if (filePath != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading....");
            progressDialog.setCancelable(false);
            progressDialog.show();

            final StorageReference reference = storageReference.child("/" + UUID.randomUUID().toString());

            reference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri iUri) {
                            Toast.makeText(CDashboard.this,"Image uploaded",Toast.LENGTH_LONG).show();
                            imageUrl = String.valueOf(iUri); /* Fetch url image */
                            Log.d("customerImageUrl: ",imageUrl);
                            Map<String,Object> customers = new HashMap<>();
                            customers.put("profile",imageUrl);
                            customers.put("userId",userId);
                            documentReference.update(customers);
                            Toast.makeText(CDashboard.this,"image url downloaded and inserted",Toast.LENGTH_LONG).show();
                            save();
                            startActivity(new Intent(CDashboard.this,CustomerHome.class));
                            finish();
                        }
                    });

                            //int img =  Picasso.with(getBaseContext()).load(imageUrl).into(imgFirebase);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CDashboard.this,"Failed to upload profile",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void save() {
        ans="yes";
        SharedPreferences preferences = getSharedPreferences("setting1", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("pUploaded",ans);
        //editor.putInt("code",code);
        editor.apply();
        Toast.makeText(CDashboard.this,"saved.",Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK) {
            try {
                filePath = data.getData();
                fPhoto.setImageURI(filePath);
            }catch (Exception e){
                Toast.makeText(CDashboard.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }
}
