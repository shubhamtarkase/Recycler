package com.shubham.recycler;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RejectByVendor extends AppCompatActivity {

    RadioGroup radioGroup;
    String reason="";
    ProgressDialog progressDialog;

    //Firebase
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore;
    DocumentReference reference,documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reject_by_vendor);


        String userId = fAuth.getCurrentUser().getUid();
        reference = fStore.collection("users").document(userId);
        String customerId = getIntent().getExtras().getString("cId");
        documentReference = fStore.collection("customers").document(customerId);

        radioGroup = findViewById(R.id.radioGroup);
        progressDialog = new ProgressDialog(this);
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
            users.put("reason1",reason);
            reference.update(users);
            Map<String,Object> customers = new HashMap<>();
            customers.put("order","empty");
            documentReference.update(customers);
            progressDialog.dismiss();

            startActivity(new Intent(RejectByVendor.this,dashboard.class));
            finish();
        }
    }

    public void back(View view) {
        startActivity(new Intent(RejectByVendor.this,CustomerDetails.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RejectByVendor.this,CustomerDetails.class));
        finish();
    }
}
