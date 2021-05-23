package com.example.sm_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MypageActivity extends AppCompatActivity {

    Button go_to_setting, go_to_likepage, go_to_cost_setting;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        Toolbar tb = findViewById(R.id.mypage_toolbar);
        setSupportActionBar(tb);

        go_to_setting = findViewById(R.id.go_to_setting);
        go_to_likepage = findViewById(R.id.go_to_likepage2);
        go_to_cost_setting = findViewById(R.id.go_to_cost_setting);

        go_to_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication(), SettingActivity.class));
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplication(), MainActivity.class));
        finish();
    }

}