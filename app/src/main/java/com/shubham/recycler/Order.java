package com.shubham.recycler;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

public class Order extends AppCompatActivity {

    String name,email,phone,link,address,vendorId;
    String customerId,customerPhone,customerName,customerAddress,customerProfile;
    TextView shopName,shopAddress,shopPhone,shopEmail;
    EditText paper,plastic,metal;
    ImageView profile;
    CardView rates;
    ImageView downArrow;
    TextView total;
    Button book;
    int test=0;

    //firebase
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    DocumentReference documentReference,reference;
    String flag="",flagValue="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        //firebase
        fAuth = FirebaseAuth.getInstance();
        String currentUser = fAuth.getUid();
        fStore = FirebaseFirestore.getInstance();

        name  = getIntent().getExtras().getString("name");
        email = getIntent().getExtras().getString("email");
        phone = getIntent().getExtras().getString("phone");
        link = getIntent().getExtras().getString("link");
        address = getIntent().getExtras().getString("address");
        vendorId = getIntent().getExtras().getString("vendorId");
        Log.d("testVendorId",vendorId);


        //customer
        customerId = fAuth.getCurrentUser().getUid();
        customerPhone = fAuth.getCurrentUser().getPhoneNumber();
        Log.d("customerId",customerId+" "+customerName+" "+customerPhone);


        documentReference = fStore.collection("users").document(vendorId);
        reference = fStore.collection("customers").document(customerId);

        try {
            reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    customerName = documentSnapshot.get("name").toString();
                    customerAddress = documentSnapshot.get("address").toString();
                    customerProfile = documentSnapshot.get("profile").toString();
                    flag = documentSnapshot.get("order").toString();
                    flagValue = documentSnapshot.get("vendorId").toString();
                    Log.d("customerName",customerName+" "+customerAddress+" "+customerProfile);

                    if (flag.equals("booked") && flagValue.equals(vendorId)) {
                        Log.d("testingFlags",flag+" "+flagValue);
                        Intent intent = new Intent(Order.this, CancelOrder.class);
                        intent.putExtra("vendorId",vendorId);
                        intent.putExtra("profile",link);
                        intent.putExtra("name",name);
                        intent.putExtra("phone",phone);
                        intent.putExtra("address",address);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(Order.this,"Failed to get and send customer details",Toast.LENGTH_LONG).show();
        }


        //views
        shopName = findViewById(R.id.shopName);
        shopEmail = findViewById(R.id.shopEmail);
        shopAddress = findViewById(R.id.shopAddress);
        shopPhone = findViewById(R.id.shopPhone);
        profile = findViewById(R.id.shopProfile);
        rates = findViewById(R.id.rates);
        downArrow = findViewById(R.id.downArrow);
        paper = findViewById(R.id.paperEdit);
        plastic = findViewById(R.id.plasticEdit);
        metal = findViewById(R.id.metalEdit);
        total = findViewById(R.id.prize);
        book = findViewById(R.id.book);

        shopName.setText(name);
        shopPhone.setText(phone);
        shopAddress.setText(address);
        shopEmail.setText(email);

        Picasso.with(this).load(link).fit().into(profile);

        book.setEnabled(false);

        try {
            paper.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (paper.getText().length()==0) {
                        paper.setText("0");
                    }else if (paper.getText().length()>=10){
                        paper.setText("0");
                    }
                    else {

                        int paperPrize=0,plasticPrize=0,metalPrize=0,totalPrize=0;

                        paperPrize = Integer.parseInt(String.valueOf(paper.getText()));
                        plasticPrize = Integer.parseInt(String.valueOf(plastic.getText()));
                        metalPrize = Integer.parseInt(String.valueOf(metal.getText()));


                        if (paperPrize==0)
                            totalPrize=(plasticPrize*18)+(metalPrize*20);
                        else if (plasticPrize==0)
                            totalPrize=(paperPrize*12)+(metalPrize*20);
                        else if (metalPrize==0)
                            totalPrize=(paperPrize*12)+(plasticPrize*18);
                        else {
                            totalPrize=(paperPrize*12)+(plasticPrize*18)+(metalPrize*20);
                        }

                        if (totalPrize<=0){
                            total.setText("0");
                            book.setEnabled(false);
                        }
                        else{
                            total.setText(String.valueOf(totalPrize));
                            book.setEnabled(true);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            plastic.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (plastic.getText().length()==0){
                        plastic.setText("0");
                    }else if (plastic.getText().length()>=10){
                        plastic.setText("0");
                    }else {
                        int paperPrize=0,plasticPrize=0,metalPrize=0,totalPrize=0;

                        paperPrize = Integer.parseInt(String.valueOf(paper.getText()));
                        plasticPrize = Integer.parseInt(String.valueOf(plastic.getText()));
                        metalPrize = Integer.parseInt(String.valueOf(metal.getText()));


                        if (paperPrize==0)
                            totalPrize=(plasticPrize*18)+(metalPrize*20);
                        else if (plasticPrize==0)
                            totalPrize=(paperPrize*12)+(metalPrize*20);
                        else if (metalPrize==0)
                            totalPrize=(paperPrize*12)+(plasticPrize*18);
                        else {
                            totalPrize=(paperPrize*12)+(plasticPrize*18)+(metalPrize*20);
                        }

                        if (totalPrize<=0){
                            total.setText("0");
                            book.setEnabled(false);
                        }
                        else{
                            total.setText(String.valueOf(totalPrize));
                            book.setEnabled(true);
                        }

                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            metal.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (metal.getText().length()==0){
                        metal.setText("0");
                    }else if (metal.getText().length()>=10){
                        metal.setText("0");
                    }else {
                        int paperPrize=0,plasticPrize=0,metalPrize=0,totalPrize=0;

                        paperPrize = Integer.parseInt(String.valueOf(paper.getText()));
                        plasticPrize = Integer.parseInt(String.valueOf(plastic.getText()));
                        metalPrize = Integer.parseInt(String.valueOf(metal.getText()));


                        if (paperPrize==0)
                            totalPrize=(plasticPrize*18)+(metalPrize*20);
                        else if (plasticPrize==0)
                            totalPrize=(paperPrize*12)+(metalPrize*20);
                        else if (metalPrize==0)
                            totalPrize=(paperPrize*12)+(plasticPrize*18);
                        else {
                            totalPrize=(paperPrize*12)+(plasticPrize*18)+(metalPrize*20);
                        }

                        if (totalPrize<=0){
                            total.setText("0");
                            book.setEnabled(false);
                        }
                        else{
                            total.setText(String.valueOf(totalPrize));
                            book.setEnabled(true);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }catch (Exception e){
            Toast.makeText(Order.this,"Enter valid value"+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void openRates(View view) {
        int a=0;
        if (rates.getVisibility() == View.GONE && a==0){
            TransitionManager.beginDelayedTransition(rates,new AutoTransition());
            rates.setVisibility(View.VISIBLE);
            downArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            a=1;
        }else {
            TransitionManager.beginDelayedTransition(rates,new AutoTransition());
            rates.setVisibility(View.GONE);
            downArrow.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
            a=0;
        }
        if (a==0)
            downArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
    }

    public void sub(View view) {
        int a=0,sub=0;
        if (paper.getText().length()==0){
            paper.setText("0");
            a=0;
            sub=0;
        }else {
            a = Integer.parseInt(String.valueOf(paper.getText()));
            sub = a-1;
            paper.setText(String.valueOf(sub));
        }
    }

    public void sub1(View view) {
        int a=0,sub=0;
        if (plastic.getText().length()==0){
            plastic.setText("0");
            a=0;
            sub=0;
        }else {
            a = Integer.parseInt(String.valueOf(plastic.getText()));
            sub = a-1;
            plastic.setText(String.valueOf(sub));
        }
    }

    public void sub2(View view) {
        int a=0,sub=0;
        if (metal.getText().length()==0){
            metal.setText("0");
            a=0;
            sub=0;
        }else {
            a = Integer.parseInt(String.valueOf(metal.getText()));
            sub = a-1;
            metal.setText(String.valueOf(sub));
        }
    }

    public void add(View view) {
        int a=0,sub=0;
        if (paper.getText().length()==0){
            paper.setText("0");
            a=0;
            sub=0;
        }else {
            a = Integer.parseInt(String.valueOf(paper.getText()));
            sub = a+1;
            paper.setText(String.valueOf(sub));
        }
    }

    public void add1(View view) {
        int a=0,sub=0;
        if (plastic.getText().length()==0){
            plastic.setText("0");
            a=0;
            sub=0;
        }else {
            a = Integer.parseInt(String.valueOf(plastic.getText()));
            sub = a+1;
            plastic.setText(String.valueOf(sub));
        }
    }

    public void add2(View view) {
        int a=0,sub=0;
        if (metal.getText().length()==0){
            metal.setText("0");
            a=0;
            sub=0;
        }else {
            a = Integer.parseInt(String.valueOf(metal.getText()));
            sub = a+1;
            metal.setText(String.valueOf(sub));
        }
    }


    public void getPrize(View view) {
        int paperPrize=0,plasticPrize=0,metalPrize=0,totalPrize=0;

        paperPrize = Integer.parseInt(String.valueOf(paper.getText()));
        plasticPrize = Integer.parseInt(String.valueOf(plastic.getText()));
        metalPrize = Integer.parseInt(String.valueOf(metal.getText()));


        if (paperPrize==0)
            totalPrize=(plasticPrize*18)+(metalPrize*20);
        else if (plasticPrize==0)
            totalPrize=(paperPrize*12)+(metalPrize*20);
        else if (metalPrize==0)
            totalPrize=(paperPrize*12)+(plasticPrize*18);
        else {
                totalPrize=(paperPrize*12)+(plasticPrize*18)+(metalPrize*20);
        }

        if (totalPrize==0){
            total.setText("0");
            book.setEnabled(false);
        }
        else{
            total.setText(String.valueOf(totalPrize));
            book.setEnabled(true);
        }
    }

    public void book(View view) {
        Intent intent = new Intent(Order.this,ScrapImage.class);
        intent.putExtra("customerId",customerId);
        intent.putExtra("customerName",customerName);
        intent.putExtra("customerAddress",customerAddress);
        intent.putExtra("customerPhone",customerPhone);
        intent.putExtra("vendorId",vendorId);
        intent.putExtra("customerProfile",customerProfile);
        String p,p1,m,totalRate;
        p=paper.getText().toString();
        p1=plastic.getText().toString();
        m=metal.getText().toString();
        totalRate=total.getText().toString();
        intent.putExtra("paperKg",p);
        intent.putExtra("plasticKg",p1);
        intent.putExtra("metalKg",m);
        intent.putExtra("total",totalRate);
        Log.d("sendDetails",p+" "+p1+" "+m+" "+" "+totalRate+" "+" "+customerName+" "+customerAddress+" "+customerPhone+" "+customerId+" "+vendorId+" "+customerProfile);
        startActivity(intent);
        finish();
    }
}
