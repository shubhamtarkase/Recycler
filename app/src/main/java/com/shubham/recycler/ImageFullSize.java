package com.shubham.recycler;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ImageFullSize extends AppCompatActivity {

    String link;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_size);

        imageView = findViewById(R.id.imageView);

        link = getIntent().getExtras().getString("link");

        Picasso.with(this).load(link).into(imageView);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ImageFullSize.this,CustomerDetails.class));
        finish();
    }
}
