package com.example.sm_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SellerSettingActivity extends AppCompatActivity {
    Spinner spinner_store, spinner_city, spinner_town;
    ArrayAdapter<CharSequence> stspin, adspin1, adspin2;

    EditText txt_shop_name, txt_shop_addr, txt_shop_time, txt_intro;
    Button button_first;
    String uid;
    int num;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    DatabaseReference mDatabase;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_setting);

        //객체 초기화
        spinner_city = findViewById(R.id.spinner_city);
        spinner_town =  findViewById(R.id.spinner_town);
        spinner_store = findViewById(R.id.spinner_store_type);

        txt_shop_name = findViewById(R.id.txt_shop_name);
        txt_shop_addr = findViewById(R.id.txt_shop_addr);
        txt_shop_time = findViewById(R.id.txt_shop_time);
        txt_intro = findViewById(R.id.txt_intro);
        button_first = findViewById(R.id.button_first);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser(); //로그인한 유저의 정보 가져오기
        uid = user != null ? user.getUid() : null;

        stspin = ArrayAdapter.createFromResource(this, R.array.store_type, android.R.layout.simple_spinner_dropdown_item);
        stspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adspin1 = ArrayAdapter.createFromResource(this, R.array.city, android.R.layout.simple_spinner_dropdown_item);
        adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_store.setAdapter(stspin);
        spinner_city.setAdapter(adspin1);

        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selItem = (String) spinner_city.getSelectedItem();

                switch (selItem) {
                    case "서울특별시":
                        adspin2 = ArrayAdapter.createFromResource(SellerSettingActivity.this, R.array.Seoul, android.R.layout.simple_spinner_dropdown_item);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_town.setAdapter(adspin2);
                        break;

                    case "부산광역시":
                        adspin2 = ArrayAdapter.createFromResource(SellerSettingActivity.this, R.array.Busan, android.R.layout.simple_spinner_dropdown_item);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_town.setAdapter(adspin2);
                        break;

                    case "대구광역시":
                        adspin2 = ArrayAdapter.createFromResource(SellerSettingActivity.this, R.array.Daegu, android.R.layout.simple_spinner_dropdown_item);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_town.setAdapter(adspin2);
                        break;
                    case "인천광역시":
                        adspin2 = ArrayAdapter.createFromResource(SellerSettingActivity.this, R.array.Incheon, android.R.layout.simple_spinner_dropdown_item);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_town.setAdapter(adspin2);
                        break;
                    case "대전광역시":
                        adspin2 = ArrayAdapter.createFromResource(SellerSettingActivity.this, R.array.Daejeon, android.R.layout.simple_spinner_dropdown_item);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_town.setAdapter(adspin2);
                        break;
                    case "울산광역시":
                        adspin2 = ArrayAdapter.createFromResource(SellerSettingActivity.this, R.array.Ulsan, android.R.layout.simple_spinner_dropdown_item);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_town.setAdapter(adspin2);
                        break;
                    case "세종특별자치시":
                        adspin2 = ArrayAdapter.createFromResource(SellerSettingActivity.this, R.array.Sejong, android.R.layout.simple_spinner_dropdown_item);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_town.setAdapter(adspin2);
                        break;
                    case "경기도":
                        adspin2 = ArrayAdapter.createFromResource(SellerSettingActivity.this, R.array.Gyeonggi, android.R.layout.simple_spinner_dropdown_item);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_town.setAdapter(adspin2);
                        break;
                    case "강원도":
                        adspin2 = ArrayAdapter.createFromResource(SellerSettingActivity.this, R.array.Gangwon, android.R.layout.simple_spinner_dropdown_item);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_town.setAdapter(adspin2);
                        break;
                    case "충청북도":
                        adspin2 = ArrayAdapter.createFromResource(SellerSettingActivity.this, R.array.Chungbuk, android.R.layout.simple_spinner_dropdown_item);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_town.setAdapter(adspin2);
                        break;
                    case "충청남도":
                        adspin2 = ArrayAdapter.createFromResource(SellerSettingActivity.this, R.array.Chungnam, android.R.layout.simple_spinner_dropdown_item);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_town.setAdapter(adspin2);
                        break;
                    case "전라북도":
                        adspin2 = ArrayAdapter.createFromResource(SellerSettingActivity.this, R.array.Jeonbuk, android.R.layout.simple_spinner_dropdown_item);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_town.setAdapter(adspin2);
                        break;
                    case "전라남도":
                        adspin2 = ArrayAdapter.createFromResource(SellerSettingActivity.this, R.array.Jeonnam, android.R.layout.simple_spinner_dropdown_item);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_town.setAdapter(adspin2);
                        break;
                    case "경상북도":
                        adspin2 = ArrayAdapter.createFromResource(SellerSettingActivity.this, R.array.Gyeongbuk, android.R.layout.simple_spinner_dropdown_item);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_town.setAdapter(adspin2);
                        break;
                    case "경상남도":
                        adspin2 = ArrayAdapter.createFromResource(SellerSettingActivity.this, R.array.Gyeongnam, android.R.layout.simple_spinner_dropdown_item);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_town.setAdapter(adspin2);
                        break;
                    case "제주특별자치도":
                        adspin2 = ArrayAdapter.createFromResource(SellerSettingActivity.this, R.array.Jeju, android.R.layout.simple_spinner_dropdown_item);
                        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_town.setAdapter(adspin2);
                        break;
                    default:
                        break;

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("StoreInfo").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(uid != null){
                    txt_shop_name.setText(snapshot.child("name").getValue(String.class));
                    spinner_store.setSelection(snapshot.child("type_int").getValue(Integer.class));
                    spinner_city.setSelection(snapshot.child("city_int").getValue(Integer.class));
                    spinner_town.setSelection(snapshot.child("town_int").getValue(Integer.class));
                    txt_shop_addr.setText(snapshot.child("addr").getValue(String.class));
                    txt_shop_time.setText(snapshot.child("time").getValue(String.class));
                    txt_intro.setText(snapshot.child("intro").getValue(String.class));
                }else {
                    Toast.makeText(SellerSettingActivity.this, "회원정보를 불러올수가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        button_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txt_shop_name.getText().toString().equals("") && !txt_shop_addr.getText().toString().equals("") && !spinner_store.getSelectedItem().toString().equals("") && !spinner_city.getSelectedItem().toString().equals("") && !spinner_town.getSelectedItem().toString().equals("")) {

                    String name = txt_shop_name.getText().toString().trim();
                    String type_string = spinner_store.getSelectedItem().toString();
                    int type_int = spinner_store.getSelectedItemPosition();
                    String city_string = spinner_city.getSelectedItem().toString();
                    int city_int = spinner_city.getSelectedItemPosition();
                    String town_string = spinner_town.getSelectedItem().toString();
                    int town_int = spinner_town.getSelectedItemPosition();
                    String addr = txt_shop_addr.getText().toString().trim();
                    String time = "";
                    String intro = "";

                    if(!txt_shop_time.getText().toString().equals("")) {
                        time = txt_shop_time.getText().toString().trim();
                    }

                    if(!txt_intro.getText().toString().equals("")) {
                        intro = txt_intro.getText().toString().trim();
                    }

                    HashMap<String, Object> storeUpdates = new HashMap<>();

                    StoreInfo storeInfo = new StoreInfo(uid, name, type_string, type_int, city_string, city_int, town_string, town_int, addr, time, intro);
                    Map<String, Object> storeValue = storeInfo.toMap();

                    storeUpdates.put("/StoreInfo/" + uid, storeValue);
                    mDatabase.updateChildren(storeUpdates);

                    Toast.makeText(SellerSettingActivity.this, "수정되었습니다.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SellerSettingActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(SellerSettingActivity.this, "작성하지 않은 항목이 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public class StoreInfo {
        private String uid;
        private String name;
        private String type_string;
        private int type_int;
        private String city_string;
        private int city_int;
        private String town_string;
        private int town_int;
        private String addr;
        private String time;
        private String intro;

        public StoreInfo(){ }

        public StoreInfo(String uid ,String name, String type_string, int type_int, String city_string, int city_int, String town_string, int town_int,
                String addr, String time, String intro) {
            this.uid = uid;
            this.name = name;
            this.type_string = type_string;
            this.type_int = type_int;
            this.city_string = city_string;
            this.city_int = city_int;
            this.town_string = town_string;
            this.town_int = town_int;
            this.addr = addr;
            this.time = time;
            this.intro = intro;
        }

        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("uid", uid);
            result.put("name", name);
            result.put("type_string", type_string);
            result.put("type_int", type_int);
            result.put("city_string", city_string);
            result.put("city_int", city_int);
            result.put("town_string", town_string);
            result.put("town_int", town_int);
            result.put("addr", addr);
            result.put("time", time);
            result.put("intro", intro);

            return result;
        }
    }
}