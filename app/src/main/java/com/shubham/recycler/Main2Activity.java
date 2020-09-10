package com.shubham.recycler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class Main2Activity extends AppCompatActivity {

    public static final String TAG = "TAG";
    //views
    CountryCodePicker ccp;
    LinearLayout forCode,forOtp;
    EditText phone,otp;
    Button next;
    ProgressBar big;
    TextView resend,checking;

    public int code;

    //flag
    Boolean verificationInProgress=false;

    //Firebase
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference fCloud;

    //for verify otp
    String verificationId,phoneNum;
    PhoneAuthProvider.ForceResendingToken fToken;
    private int ans=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //firebase
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fCloud = FirebaseStorage.getInstance().getReference().child("profiles/");

        //views instantiation
        ccp = (CountryCodePicker)findViewById(R.id.ccp);
        forCode = (LinearLayout)findViewById(R.id.forCode);
        forOtp = (LinearLayout)findViewById(R.id.forOtp);
        phone = (EditText)findViewById(R.id.phoneNo);
        otp = (EditText)findViewById(R.id.otp);
        next = (Button)findViewById(R.id.next);
        resend = (TextView)findViewById(R.id.resend);
        big = (ProgressBar)findViewById(R.id.cprogress);
        checking = (TextView)findViewById(R.id.checking);


        SharedPreferences preferences = getSharedPreferences("setting", Context.MODE_PRIVATE);
        String uId = preferences.getString("cUid","");
        String vUid = preferences.getString("vUid","");
        code = preferences.getInt("code",1);

        try {
            if (fAuth.getCurrentUser().getUid().equals(vUid)){
                startActivity(new Intent(Main2Activity.this,MainActivity.class));
                finish();
            }else if (fAuth.getCurrentUser() != null){
                big.setVisibility(View.VISIBLE);
                checking.setVisibility(View.VISIBLE);
                final DocumentReference reference = fStore.collection("customers").document(fAuth.getCurrentUser().getUid());
                reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            ans=1;
                            startActivity(new Intent(Main2Activity.this,CDashboard.class));
                            finish();
                        }else {
                            startActivity(new Intent(Main2Activity.this,CustomerRegistration.class));
                            finish();
                        }
                    }
                });
            }
        }catch (Exception e){

        }

        if (fAuth.getCurrentUser() != null && fAuth.getCurrentUser().getUid().equals(uId)){
            big.setVisibility(View.VISIBLE);
            checking.setVisibility(View.VISIBLE);
            DocumentReference reference = fStore.collection("customers").document(fAuth.getCurrentUser().getUid());
            reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()){
                        ans=1;
                        startActivity(new Intent(Main2Activity.this,CDashboard.class));
                        finish();
                    }else {
                        startActivity(new Intent(Main2Activity.this,CustomerRegistration.class));
                        finish();
                    }
                }
            });
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next.setText("verify");
                if (verificationInProgress==false){
                    if(!phone.getText().toString().isEmpty() && phone.getText().toString().length()==10){
                        phoneNum = ccp.getSelectedCountryCodeWithPlus()+phone.getText().toString();
                        Log.d(TAG,phoneNum);
                        forOtp.setVisibility(View.VISIBLE);
                        forCode.setVisibility(View.GONE);
                        resend.setEnabled(false);
                        resend.setText("sending OTP...");
                        requestOTP(phoneNum);
                    }else {
                        phone.setError("please enter valid phone no");
                    }
                }else {
                    String otpno = otp.getText().toString();
                    if (!otpno.isEmpty()){
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,otpno);
                        verifyAuth(credential);
                    }else {
                        otp.setError("Enter OTP");
                    }
                }
            }
        });
    }


    private void checkUserProfile() {
        DocumentReference reference = fStore.collection("customers").document(fAuth.getCurrentUser().getUid());
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    ans=1;
                    startActivity(new Intent(Main2Activity.this,CDashboard.class));
                    finish();
                }else {
                    startActivity(new Intent(Main2Activity.this,CustomerRegistration.class));
                    finish();
                }
            }
        });
    }

    private void verifyAuth(PhoneAuthCredential credential) {
        fAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Main2Activity.this,"Authentication is successful.",Toast.LENGTH_LONG).show();
                    checkUserProfile();
                }else {
                    Toast.makeText(Main2Activity.this,"Authentication failed.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void requestOTP(String phoneNum) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNum, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId=s;
                fToken=forceResendingToken;
                resend.setVisibility(View.GONE);
                verificationInProgress=true;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                resend.setText("resend");
                resend.setVisibility(View.VISIBLE);
                resend.setEnabled(true);
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                verifyAuth(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(Main2Activity.this,"Can not create user"+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void switchTo(View view) {
        startActivity(new Intent(Main2Activity.this,MainActivity.class));
        finish();
    }

    public void customerRegistration(View view) {
        startActivity(new Intent(Main2Activity.this,CustomerRegistration.class));
        finish();
    }

    public void resendOTP(View view) {
        requestOTP(phoneNum);
    }
}
