package com.example.sm_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {
    Handler handler;

    private FirebaseAuth firebaseAuth;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler = new Handler();

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            handler.postDelayed(new mainSplash(),2000);
        }else{
            handler.postDelayed(new splashhandler(),2000);
        }
    }

    private  class mainSplash implements Runnable {
        @Override
        public void run() {
            FirebaseUser user = firebaseAuth.getCurrentUser(); //로그인한 유저의 정보 가져오기
            String uid = user != null ? user.getUid() : null;

            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Users").child(uid).child("userType").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean userType = Boolean.parseBoolean(snapshot.getValue(String.class));
                    if (userType) {
                        Toast.makeText(SplashActivity.this, "일반인으로 로그인했습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SplashActivity.this, "업주로 로그인했습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        //firebaseAuth.signOut();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        }
    }

    private class splashhandler implements Runnable {
        @Override
        public void run() {
            startActivity(new Intent(getApplication(), LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {

    }
}
