package com.shubham.recycler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private static final String TAG = "EmailPassword";

    public int code;

    TextView textView;
    LinearLayout small;
    LinearLayout small1;
    EditText mail;
    EditText password;
    Button signup;
    Button signin;
    ProgressBar cprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // views
        mail = (EditText) findViewById(R.id.mail);
        password = (EditText) findViewById(R.id.password);
        textView = (TextView) findViewById(R.id.textView);
        small = (LinearLayout) findViewById(R.id.small);


        //buttons
        signin = (Button) findViewById(R.id.button);

        //progress bar
        cprogress=(ProgressBar)findViewById(R.id.cprogress);


        //firebase
        fAuth = FirebaseAuth.getInstance();

        SharedPreferences preferences = getSharedPreferences("setting", Context.MODE_PRIVATE);
        String uId = preferences.getString("vUid","");
        code = preferences.getInt("code",1);

        if (fAuth.getCurrentUser()!=null && fAuth.getCurrentUser().getUid().equals(uId) && code==1){
            startActivity(new Intent(MainActivity.this,dashboard.class));
            finish();
        }

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,register.class));
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=mail.getText().toString();
                String pass=password.getText().toString();

                if (pass.length()<6){
                    password.setError("Password must be at least 6 digits long");
                }
                if(email.isEmpty()){
                    mail.setError("please enter your email id");
                    mail.requestFocus();
                }
                else if(pass.isEmpty()){
                    password.setError("please enter your password");
                    password.requestFocus();
                }
                else{
                    cprogress.setVisibility(View.VISIBLE);
                    fAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                SharedPreferences preferences = getSharedPreferences("setting", Context.MODE_PRIVATE);

                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("vUid",fAuth.getCurrentUser().getUid());
                                editor.putInt("code",code);
                                editor.apply();
                                Toast.makeText(MainActivity.this,"saved.",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(MainActivity.this,dashboard.class));
                                finish();
                            }
                            else {
                                Toast.makeText(MainActivity.this,"Error: "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                cprogress.setVisibility(View.GONE);
                            }
                        }
                    });
                }

            }
        });
    }

    public void switchTo(View view) {
        startActivity(new Intent(MainActivity.this,Main2Activity.class));
        finish();
    }

}
