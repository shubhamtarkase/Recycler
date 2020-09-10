package com.shubham.recycler;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScrapImage extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_WRITE_PERMISSION_CODE = 6646;
    private ImageView imageView,testImage;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    LinearLayout linearLayout;
    Button capture,confirm;
    TextView paperPrize,plasticPrize,metalPrize,totalValue;
    String customerId,vendorId;
    String paperKg,plasticKg,metalKg,total;
    String customerName,customerAddress,customerPhone,customerProfile;
    String vendorPhone,vendorName,vendorProfile,vendorAddress;
    Bitmap photo;
    String imageFilePath = "";
    Uri uri;
    boolean temp,temp1;
    String imageUrl;
    int ans=1;
    //Firebase

    FirebaseFirestore fStore;
    StorageReference storageReference;
    DocumentReference documentReference,documentReferenceForCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrap_image);

        imageView = findViewById(R.id.imageView);
        linearLayout = findViewById(R.id.linearLayout);
        capture = findViewById(R.id.capture);
        confirm = findViewById(R.id.confirm);
        paperPrize = findViewById(R.id.paperPrize);
        plasticPrize = findViewById(R.id.plasticPrize);
        metalPrize = findViewById(R.id.metalPrize);
        totalValue = findViewById(R.id.totalValue);
        testImage = findViewById(R.id.testImage);

        // All customer details to send in vendors account
        paperKg = getIntent().getExtras().getString("paperKg");
        plasticKg = getIntent().getExtras().getString("plasticKg");
        metalKg = getIntent().getExtras().getString("metalKg");
        total = getIntent().getExtras().getString("total");
        customerId = getIntent().getExtras().getString("customerId");
        vendorId = getIntent().getExtras().getString("vendorId");
        customerName = getIntent().getExtras().getString("customerName");
        customerAddress = getIntent().getExtras().getString("customerAddress");
        customerPhone = getIntent().getExtras().getString("customerPhone");
        customerProfile = getIntent().getExtras().getString("customerProfile");

        //All vendor details
        vendorAddress = getIntent().getExtras().getString("address");
        vendorName = getIntent().getExtras().getString("name");
        vendorPhone = getIntent().getExtras().getString("phone");
        vendorProfile = getIntent().getExtras().getString("profile");

        paperPrize.append(paperKg);
        plasticPrize.append(plasticKg);
        metalPrize.append(metalKg);
        totalValue.append(total);

        fStore = FirebaseFirestore.getInstance();
        documentReference = fStore.collection("users").document(vendorId);
        documentReferenceForCustomer = fStore.collection("customers").document(customerId);
        storageReference = FirebaseStorage.getInstance().getReference("trashImages");

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_WRITE_PERMISSION_CODE);
        }
    }

    public void captureImage(View view) {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        }
        else
        {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    public void confirmOrder(View view) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Confirming Order....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final StorageReference reference = storageReference.child("/" + UUID.randomUUID().toString());
        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri iUri) {
                        imageUrl = String.valueOf(iUri);
                        final Map<String,Object> users = new HashMap<>();
                        users.put("trashImage",imageUrl);
                        users.put("paperKg",paperKg);
                        users.put("plasticKg",plasticKg);
                        users.put("metalKg",metalKg);
                        users.put("total",total);
                        users.put("customerId",customerId);
                        users.put("vendorId",vendorId);
                        users.put("customerName",customerName);
                        users.put("customerPhone",customerPhone);
                        users.put("customerAddress",customerAddress);
                        users.put("customerProfile",customerProfile);


                        final Map<String,Object> orderList = new HashMap<>();
                        orderList.put("order1",users);
                        documentReference.update(orderList);


                        Map<String,Object> forGlobal = new HashMap<>();
                        forGlobal.put("reason","");
                        forGlobal.put("reason1","null");
                        documentReference.update(forGlobal);

                   /*     List<Map<String, Object>> orders = new ArrayList<>();
                        int i;
                        i = orders.indexOf(users);
                        int j = i+1;
                        Log.d("testing i", String.valueOf(j));
                        orders.add(users);

                        Map<String,Object> orderList = new HashMap<>();
                        orderList.put("order list",orders);

                        documentReference.update(orderList); */
                        save();
                        Intent intent = new Intent(ScrapImage.this,CancelOrder.class);
                        intent.putExtra("vendorId",vendorId);
                        intent.putExtra("profile",vendorProfile);
                        intent.putExtra("name",vendorName);
                        intent.putExtra("phone",vendorPhone);
                        intent.putExtra("address",vendorAddress);
                        progressDialog.dismiss();
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    private void save() {
        Map<String,Object> customers = new HashMap<>();
        customers.put("order","booked");
        customers.put("vendorId",vendorId);
        documentReferenceForCustomer.update(customers);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == MY_WRITE_PERMISSION_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "write permission granted", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "write permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

        //    Uri imageUri = (Uri)data.getData();
        //    uri = data.getData();
        //    Log.d("image", String.valueOf(imageUri));
            photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);

            FileOutputStream outputStream = null;

            File sdCard = Environment.getExternalStorageDirectory();
            File directory = new File(sdCard.getAbsolutePath() + "/Recycler App");
            directory.mkdir();
            String fileName = String.format("%d.jpg",System.currentTimeMillis());
            File outFile = new File(directory,fileName);

            try {
                outputStream = new FileOutputStream(outFile);
                photo.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
             //   Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show();
                Log.d("imagePath", String.valueOf(outputStream));
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            uri = Uri.fromFile(outFile.getAbsoluteFile());
            Log.d("testingFilePath", String.valueOf(uri));
            linearLayout.setVisibility(View.VISIBLE);
            confirm.setVisibility(View.VISIBLE);
            //testImage.setImageURI(uri);
        }
    }
}
