package com.shubham.recycler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class dashboard extends AppCompatActivity {

    //required things
    FirebaseAuth fAuth=FirebaseAuth.getInstance();
    FirebaseUser fAuthCurrentUser=fAuth.getCurrentUser();
    FirebaseFirestore fStore=FirebaseFirestore.getInstance();
    StorageReference mStorageRef;
    DocumentReference reference;

    //used things
    Button verify;
    LinearLayout forVerify,main;
    TextView msg;

    //test things
    ImageView testImage;
    TextView testMsg;

    //to verify
    int flag=0;

    //main things
    ImageView profile;
    TextView name,address,canceledText;

    CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        String userId = fAuth.getCurrentUser().getUid();

        mStorageRef = FirebaseStorage.getInstance().getReference();
        reference = fStore.collection("users").document(userId);

        verify=(Button)findViewById(R.id.verify);
        forVerify=(LinearLayout)findViewById(R.id.forverify);
        main=(LinearLayout)findViewById(R.id.main);
        msg=(TextView)findViewById(R.id.msg);

        //main things
        profile = findViewById(R.id.profile);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        cardView = findViewById(R.id.cardView);
        canceledText = findViewById(R.id.canceledText);

        //test contents
        testImage = findViewById(R.id.testImage);

        if (fAuthCurrentUser.isEmailVerified()){
            flag=1;
        }
        if (flag==1){
            main.setVisibility(View.VISIBLE);
            forVerify.setVisibility(View.GONE);
        }

        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String cName,cAddress,url,reason;
                cName = String.valueOf(documentSnapshot.get("order1.customerName"));
                cAddress = String.valueOf(documentSnapshot.get("order1.customerAddress"));
                url = String.valueOf(documentSnapshot.get("order1.customerProfile"));
                reason = String.valueOf(documentSnapshot.get("reason"));

                if (!reason.isEmpty()){
                    name.setText(cName);
                    address.setText(cAddress);
                    Picasso.with(dashboard.this).load(url).into(profile);
                    canceledText.setVisibility(View.VISIBLE);
                    canceledText.append(reason);

                }else {
                    name.setText(cName);
                    address.setText(cAddress);
                    Picasso.with(dashboard.this).load(url).into(profile);
                }
                if (cName.equals("null")){
                    name.setText("");
                    address.setText("");
                    canceledText.setVisibility(View.GONE);
                    Picasso.with(dashboard.this).load(url).into(profile);
                }
            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(dashboard.this,CustomerDetails.class));
                finish();
            }
        });
    }
}
