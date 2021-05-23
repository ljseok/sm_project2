package com.example.sm_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class RequestActivity extends AppCompatActivity {
    SellerIntroFragment sellerIntroFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        sellerIntroFragment = new SellerIntroFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.activity_request,sellerIntroFragment).commit();
    }
}