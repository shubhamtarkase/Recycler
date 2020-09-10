package com.shubham.recycler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EWasteDisposalCampagin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_waste_disposal_campagin);
    }

    public void donate(View view) {
        Toast.makeText(this, "Under Development.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(EWasteDisposalCampagin.this,CustomerHome.class));
        finish();
    }
}
