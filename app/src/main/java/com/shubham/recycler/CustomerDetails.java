package com.shubham.recycler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class CustomerDetails extends AppCompatActivity {

    ImageView customerProfile,trashImage;
    TextView customerName,customerPhone,customerAddress,paperKg,plasticKg,metalKg,totalValue;
    String name,address,phone,trashLink,profileLink,paper,plastic,metal,total;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    DocumentReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

        customerProfile = findViewById(R.id.cProfile);
        trashImage = findViewById(R.id.trashImage);
        customerName = findViewById(R.id.customerName);
        customerPhone = findViewById(R.id.customerPhone);
        customerAddress = findViewById(R.id.customerAddress);
        paperKg = findViewById(R.id.paperKg);
        plasticKg = findViewById(R.id.plasticKg);
        metalKg = findViewById(R.id.metalKg);
        totalValue = findViewById(R.id.totalValue);

        String userId = fAuth.getCurrentUser().getUid();
        reference = fStore.collection("users").document(userId);

        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                profileLink = String.valueOf(documentSnapshot.get("order1.customerProfile"));
                trashLink = String.valueOf(documentSnapshot.get("order1.trashImage"));
                name = String.valueOf(documentSnapshot.get("order1.customerName"));
                address = String.valueOf(documentSnapshot.get("order1.customerAddress"));
                phone = String.valueOf(documentSnapshot.get("order1.customerPhone"));
                paper = String.valueOf(documentSnapshot.get("order1.paperKg"));
                plastic = String.valueOf(documentSnapshot.get("order1.plasticKg"));
                metal = String.valueOf(documentSnapshot.get("order1.metalKg"));
                total = String.valueOf(documentSnapshot.get("order1.total"));

                customerName.setText(name);
                customerAddress.setText(address);
                customerPhone.setText(phone);
                paperKg.append(paper);
                plasticKg.append(plastic);
                metalKg.append(metal);
                totalValue.setText(total);

                Picasso.with(CustomerDetails.this).load(profileLink).into(customerProfile);
                Picasso.with(CustomerDetails.this).load(trashLink).into(trashImage);
            }
        });

        customerProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerDetails.this,ImageFullSize.class);
                intent.putExtra("link",profileLink);
                startActivity(intent);
                finish();
            }
        });
        trashImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerDetails.this,ImageFullSize.class);
                intent.putExtra("link",trashLink);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CustomerDetails.this,dashboard.class));
        finish();
    }

    public void reject(View view) {
        Toast.makeText(this, "Under Development.", Toast.LENGTH_SHORT).show();
       /* reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String customerId = String.valueOf(documentSnapshot.get("order1.customerId"));
                Intent intent = new Intent(CustomerDetails.this,RejectByVendor.class);
                intent.putExtra("cId",customerId);
                finish();
            }
        }); */
    }
}
