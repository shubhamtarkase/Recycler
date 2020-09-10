package com.shubham.recycler;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CancelOrder extends AppCompatActivity {

    TextView textView,shopPhone,shopName,shopAddress;
    ImageView imageView;

    //Firebase
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    DocumentReference reference,documentReference;

    String name,phone,address,link;
    String vendorId,customerId;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    String reason="";
    String paperKg,plasticKg,metalKg,total;

    LinearLayout linearLayout1,linearLayout2;
    TextView paperPrize,plasticPrize,metalPrize,totalValue;
    Button button;
    RadioGroup radioGroup;

    ProgressDialog progressDialog;

    String reason1="null";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancle_order);

        vendorId = getIntent().getExtras().getString("vendorId");

        fAuth = FirebaseAuth.getInstance();
        customerId = fAuth.getCurrentUser().getUid();
        fStore = FirebaseFirestore.getInstance();
        reference = fStore.collection("users").document(vendorId);
        documentReference = fStore.collection("customers").document(customerId);

        name = getIntent().getExtras().getString("name");
        phone = getIntent().getExtras().getString("phone");
        address = getIntent().getExtras().getString("address");
        link = getIntent().getExtras().getString("profile");

        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               reason1 = String.valueOf(documentSnapshot.get("reason1"));
               if (!reason1.equals("null")){
                   AlertDialog.Builder builder = new AlertDialog.Builder(CancelOrder.this);
                   builder.setTitle("Order canceled by vendor");
                   builder.setMessage(reason1);
                   builder.setCancelable(false);
                   builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {
                           progressDialog.setTitle("canceling order");
                           progressDialog.show();


                           Map<String,Object> users = new HashMap<>();
                           users.put("reason","null");
                           users.put("reason1","null");
                           users.put("order1","null");
                           reference.update(users);
                           Map<String,Object> customers = new HashMap<>();
                           customers.put("order","empty");
                           documentReference.update(customers);
                           progressDialog.dismiss();

                           startActivity(new Intent(CancelOrder.this,CustomerHome.class));
                           finish();
                       }
                   });
               }
            }
        });

        shopPhone = findViewById(R.id.shopPhone);
        shopName = findViewById(R.id.shopName);
        shopAddress = findViewById(R.id.shopAddress);
        imageView = findViewById(R.id.shopProfile);
        linearLayout1 = findViewById(R.id.linearLayout1);
        linearLayout2 = findViewById(R.id.linearLayout2);
        button = findViewById(R.id.cancel);
        radioGroup = findViewById(R.id.radioGroup);
        paperPrize = findViewById(R.id.paperPrize);
        plasticPrize = findViewById(R.id.plasticPrize);
        metalPrize = findViewById(R.id.metalPrize);
        totalValue = findViewById(R.id.totalValue);

        progressDialog = new ProgressDialog(this);


        shopName.setText(name);
        shopPhone.setText(phone);
        shopAddress.setText(address);

        Picasso.with(this).load(link).into(imageView);

        try {
            reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    paperKg = String.valueOf(documentSnapshot.get("order1.paperKg"));
                    plasticKg = String.valueOf(documentSnapshot.get("order1.plasticKg"));
                    metalKg = String.valueOf(documentSnapshot.get("order1.metalKg"));
                    total = String.valueOf(documentSnapshot.get("order1.total"));

                    paperPrize.append(paperKg);
                    plasticPrize.append(plasticKg);
                    metalPrize.append(metalKg);
                    totalValue.append(total);
                }
            });
        }catch (Exception e){
            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancel(View view) {
        linearLayout2.setVisibility(View.VISIBLE);
        linearLayout1.setVisibility(View.GONE);
        button.setVisibility(View.GONE);
    }

    public void onButtonClick(View view) {
        int id = radioGroup.getCheckedRadioButtonId();
        switch (id) {
            case R.id.radioButton1:
                reason = "Found better deal.";
                break;
            case R.id.radioButton2:
                reason = "Order placed by mistake.";
                break;
            case R.id.radioButton3:
                reason = "Customer changed his mind.";
                break;
        }
    }

    public void confirmCancel(View view) {
        if (reason.isEmpty()){
            Toast.makeText(this, "Select valid reason before canceling the order", Toast.LENGTH_SHORT).show();
        }else {
            progressDialog.setTitle("canceling order");
            progressDialog.show();


            Map<String,Object> users = new HashMap<>();
            users.put("reason",reason);
            reference.update(users);
            Map<String,Object> customers = new HashMap<>();
            customers.put("order","empty");
            documentReference.update(customers);
            progressDialog.dismiss();

            startActivity(new Intent(CancelOrder.this,CustomerHome.class));
            finish();
        }
    }

    public void back(View view) {
        linearLayout2.setVisibility(View.GONE);
        linearLayout1.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
    }
}
