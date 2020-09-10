package com.shubham.recycler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class CustomerRegistration extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 111;
    EditText name,address,email;
    Button select,submit;
    ImageView profile;
    ProgressBar little;
    TextView error;

    public int code=0;

    private int flag=0;
    String userId,n,a,e;

    //firebase
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference folder;
    public Uri uri,uri1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK) {
            error.setText("");
            flag=1;
            uri = data.getData();
            profile.setImageURI(uri);
            profile.setVisibility(View.VISIBLE);
            uri1=data.getData();

          /*  StorageReference imageName = folder.child("profile"+uri.getLastPathSegment());
            imageName.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(CustomerRegistration.this,"uploaded",Toast.LENGTH_LONG).show();
                }
            });*/
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_registration);

        //firebase
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        folder = FirebaseStorage.getInstance().getReference().child("profiles");

        save();

        final DocumentReference reference = fStore.collection("customers").document(userId);

        //view
        name = findViewById(R.id.customerName);
        address = findViewById(R.id.customerAddress);
        email = findViewById(R.id.customerEmailId);
        select = findViewById(R.id.customerPic);
        submit = findViewById(R.id.customerSubmit);
        profile = findViewById(R.id.cProfile);
        little = findViewById(R.id.cProgressBar);
        error = findViewById(R.id.error);

        profile.setVisibility(View.GONE);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,RESULT_LOAD_IMAGE);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!name.getText().toString().isEmpty() && !address.getText().toString().isEmpty() && !email.getText().toString().isEmpty()/* && flag==1*/){
                    little.setVisibility(View.VISIBLE);

                    //upload();
                  /*  myThread myThread = new myThread();
                    myThread.start(); */

                    n=name.getText().toString();
                    a=address.getText().toString();
                    e=email.getText().toString();

                    Map<String,Object> customers = new HashMap<>();
                    customers.put("phone",fAuth.getCurrentUser().getPhoneNumber());
                    customers.put("name",n);
                    customers.put("address",a);
                    customers.put("email",e);
                    customers.put("order","temp");
                    customers.put("vendorId","temp");
                    customers.put("time","temp");

                    reference.set(customers).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(CustomerRegistration.this,"Data is inserted",Toast.LENGTH_LONG).show();
                                code=1;
                                //save();
                                startActivity(new Intent(CustomerRegistration.this,CDashboard.class));
                                finish();
                            }
                        }
                    });
                }else {
                    little.setVisibility(View.GONE);

                    if (name.getText().toString().isEmpty())
                        name.setError("please enter your full name");
                    else if (address.getText().toString().isEmpty())
                        address.setError("please valid address");
                    else if (email.getText().toString().isEmpty())
                        email.setError("please enter valid email id");
                    //else if (flag==0)
                    //    error.setText("select valid profile");
                }
            }
        });
    }

    private void upload() {
        StorageReference iReference = folder.child("profile"+uri.getLastPathSegment());

        iReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(CustomerRegistration.this,"profile uploaded",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void save() {
        SharedPreferences preferences = getSharedPreferences("setting", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("cUid",userId);
        //editor.putInt("code",code);
        editor.apply();
        Toast.makeText(CustomerRegistration.this,"saved.",Toast.LENGTH_LONG).show();
    }

    class myThread extends Thread{
        myThread(){

        }

        @Override
        public void run() {
            try {
                Thread.sleep(5000);

                StorageReference iReference = folder.child("profile"+uri.getLastPathSegment());

                iReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(CustomerRegistration.this,"profile uploaded",Toast.LENGTH_LONG).show();
                    }
                });
            }catch (Exception e){
                Toast.makeText(CustomerRegistration.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }
}
