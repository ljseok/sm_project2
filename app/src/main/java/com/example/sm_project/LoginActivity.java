package com.example.sm_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    Button mLoginBtn, mJoinBtn;
    EditText mEmailText, mPasswordText;
    TextView mFindpwText;
    //Firebase에서 계정 정보를 가져오는 객체
    private FirebaseAuth firebaseAuth;

    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    //구글 로그인을 위한 객체

    DatabaseReference mDatabase;

    //onActivityResultCode 를 위한것
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        firebaseAuth = FirebaseAuth.getInstance();

        //버튼 등록하기
        mJoinBtn = findViewById(R.id.join_btn);
        mLoginBtn = findViewById(R.id.login_btn);
        mEmailText = findViewById(R.id.login_email);
        mPasswordText = findViewById(R.id.login_passward);
        mFindpwText = findViewById(R.id.find_pw);

        //가입 버튼이 눌리면
        mJoinBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //intent함수를 통해 register액티비티 함수를 호출한다.
                startActivity(new Intent(getApplication(), JoinActivity.class));

            }
        });

        //로그인 버튼이 눌리면
        mLoginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String id, pwd;

                if (!mEmailText.getText().toString().equals("") && !mPasswordText.getText().toString().equals("")) {
                    id = mEmailText.getText().toString().trim();
                    pwd = mPasswordText.getText().toString().trim();

                    loginUser(id, pwd);
                } else {
                    Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                }
            }
        });

        mFindpwText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //intent함수를 통해 register액티비티 함수를 호출한다.
                startActivity(new Intent(getApplication(), FindActivity.class));

            }
        });

        /*firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser(); //로그인한 유저의 정보 가져오기
                String uid = user != null ? user.getUid() : null; //로그인한 유저의 고유 uid 가져오기

                mDatabase = FirebaseDatabase.getInstance().getReference(); //파이어베이스 realtime database에서 정보 가져오기
                DatabaseReference userType = mDatabase.child(uid).child("userType");
                if (user != null) {
                            //사용자에 따른 엑티비티 전환
                            if (snapshot.getValue() == "false") {
                                finish();
                                Toast.makeText(LoginActivity.this, "업주로 로그인했습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
            }
        };*/

    }

    private void readUser( ) {
        FirebaseUser user = firebaseAuth.getCurrentUser(); //로그인한 유저의 정보 가져오기
        String uid = user != null ? user.getUid() : null;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(uid).child("userType").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean userType = Boolean.parseBoolean(snapshot.getValue(String.class));
                    if (userType) {
                        Toast.makeText(LoginActivity.this, "일반인으로 로그인했습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "업주로 로그인했습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        //firebaseAuth.signOut();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

    public void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if(user.isEmailVerified()) {
                                    Toast.makeText(LoginActivity.this, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();
                                    readUser();
                            } else {
                                Toast.makeText(LoginActivity.this, "이메일 인증이 필요합니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "이메일 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }

                    }

                });
    }


        @Override
        protected void onStart () {
            super.onStart();
        }

        @Override
        protected void onStop () {
            super.onStop();
        }

}
