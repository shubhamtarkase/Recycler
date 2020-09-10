package com.shubham.recycler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CustomerHome extends AppCompatActivity {

    //Firebase
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private CollectionReference vendorReference = fStore.collection("users");
    private VendorAdapter adapter;

    private ViewFlipper viewFlipper;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        viewFlipper = findViewById(R.id.viewFlipper);
        viewFlipper.setFlipInterval(8000);
        viewFlipper.setInAnimation(this,R.anim.slide_in_right);
        viewFlipper.startFlipping();

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = vendorReference.orderBy("name",Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<VendorLIst> options = new FirestoreRecyclerOptions.Builder<VendorLIst>()
                .setQuery(query,VendorLIst.class)
                .build();

        adapter = new VendorAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.firestoreList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void go(View view) {
        startActivity(new Intent(CustomerHome.this,PlasticFreeCampaign.class));
        finish();
    }

    public void go1(View view) {
        startActivity(new Intent(CustomerHome.this,MedicalWasteCampaign.class));
        finish();
    }

    public void go2(View view) {
        startActivity(new Intent(CustomerHome.this,EWasteDisposalCampagin.class));
        finish();
    }

    public void go3(View view) {
        startActivity(new Intent(CustomerHome.this,MeetNClean.class));
        finish();
    }
}