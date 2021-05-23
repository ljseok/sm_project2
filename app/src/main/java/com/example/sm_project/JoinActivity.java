package com.example.sm_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Patterns;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;

public class JoinActivity extends AppCompatActivity {
    private AdView mAdview;
    private static final String TAG = "JoinActivity";
    // 이메일과 비밀번호
    EditText mEmailText, mPasswordText, mPasswordcheckText, mNameText;
    Button  mregisterBtn;
    RadioGroup mUserGr;
    //파이어베이스 인증 객체 생성
    private FirebaseAuth firebaseAuth;

    //비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN= Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{6,16}$");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });

        mAdview = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdview.loadAd(adRequest);
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(" \n"+"ca-app-pub-3940256099942544/6300978111");

        //파이어베이스 접근 설정
        // user = firebaseAuth.getCurrentUser();
        firebaseAuth =  FirebaseAuth.getInstance();
        firebaseAuth.useAppLanguage();
        //firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        mEmailText = findViewById(R.id.input_email);
        mPasswordText = findViewById(R.id.input_pass);
        mPasswordcheckText = findViewById(R.id.input_pass_confirm);
        mNameText= findViewById(R.id.input_name);
        mregisterBtn = findViewById(R.id.join_submit_btn);
        mUserGr = findViewById(R.id.userGr);

        //파이어베이스 user 로 접글

        //가입버튼 클릭리스너 --> firebase에 데이터를 저장한다.
        mregisterBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //가입 정보 가져오기
                final String email;
                String pwd, pwdcheck, name;
                int user;
                boolean checkedUser;

                if (!mEmailText.getText().toString().equals("") && !mPasswordText.getText().toString().equals("") && !mPasswordcheckText.getText().toString().equals("") && !mNameText.getText().toString().equals("") && mUserGr != null) {
                    //항목들이 공백이 아닌 경우
                    email = mEmailText.getText().toString().trim();
                    pwd = mPasswordText.getText().toString().trim();
                    pwdcheck = mPasswordcheckText.getText().toString().trim();
                    name = mNameText.getText().toString().trim();
                    user = mUserGr.getCheckedRadioButtonId();

                    if (isValidEmail(email) && isValidPasswd(pwd, pwdcheck)) {

                        if (pwd.equals(pwdcheck)) {
                            checkedUser = CheckedUser(user);
                            createUser(email, pwd, name, checkedUser);

                            //비밀번호가 일치하지 않을 시
                        } else {
                            Toast.makeText(JoinActivity.this, "비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } else {
                        Toast.makeText(JoinActivity.this, "이메일이나 비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
                    else {
                        //항목들이 공백인 경우
                        Toast.makeText(JoinActivity.this, "빈칸없이 입력해주세요.", Toast.LENGTH_LONG).show();
                    }
                }
        });
        
    }

    private boolean CheckedUser(int user) {
        if(user == R.id.normal){
            return true;
        } else {
            return false;
        }
    }

    //이메일 유효성 검사
    private boolean isValidEmail(String email) {
        email = mEmailText.getText().toString().trim();
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //이메일 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    //비밀번호 유효성 검사
    private boolean isValidPasswd(String password, String passwordCheck) {
        password = mPasswordText.getText().toString().trim();
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            //비밀번호 형식 불일치
            return false;
        }
        else {
            return true;
        }
    }

    //회원가입
    private void createUser (String email, String pwd, String Name, boolean checkedUser) {
        //파이어베이스에 신규계정 등록하기
        firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(JoinActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //가입 성공시
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    emailVerification(user);

                    String email = user.getEmail();
                    String password = pwd;
                    String uid = user.getUid();
                    String name = Name;
                    String userType = String.format("%b", checkedUser);

                    //해쉬맵 테이블을 파이어베이스 데이터베이스에 저장
                    HashMap<Object, String> userInfo = new HashMap<>();

                    userInfo.put("uid", uid);
                    userInfo.put("email", email);
                    userInfo.put("password", password);
                    userInfo.put("name", name);
                    userInfo.put("userType", userType);

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference userRef = database.getReference("Users");
                    userRef.child(uid).setValue(userInfo);

                    Boolean type = Boolean.parseBoolean(userType);
                    if(!type) {
                        HashMap<Object, String> storeInfo = new HashMap<>();

                        storeInfo.put("uid", uid);

                        DatabaseReference storeRef = database.getReference("StoreInfo");
                        storeRef.child(uid).setValue(storeInfo);
                    }

                    //가입이 이루어졌을 시 가입 화면을 빠져나감
                    firebaseAuth.signOut();
                    Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(JoinActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(JoinActivity.this, "이미 존재하는 이메일 입니다.", Toast.LENGTH_SHORT).show();
                    return;  //헤당 메소드 진행을 멈추고 빠져나감.

                }
            }
        });
    }


    public void emailVerification(FirebaseUser user) {
        user = firebaseAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(JoinActivity.this, "확인 이메일을 보냈습니다.", Toast.LENGTH_LONG).show();
                } else{
                    Toast.makeText(JoinActivity.this, "메일 발송에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}