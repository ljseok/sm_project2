package com.example.sm_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProductAddActivity extends AppCompatActivity {

    private static final String TAG = "ImageUploadActivity";

    private Button btChoose, btUpload, btCancel;
    private ImageView ivPreview;
    EditText txt_product_name, txt_product_price;
    String uid, filename, name, price, image;

    HashMap<String, Object> productUpdates;
    ProductInfo productInfo;
    Map<String, Object> productValue;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    DatabaseReference mDatabase;
    FirebaseUser user;

    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_add);

        btChoose = findViewById(R.id.bt_choose);
        btUpload = findViewById(R.id.bt_upload);
        btCancel = findViewById(R.id.bt_cancel);
        txt_product_name = findViewById(R.id.txt_product_name);
        txt_product_price = findViewById(R.id.txt_product_price);
        ivPreview = findViewById(R.id.iv_preview);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser(); //로그인한 유저의 정보 가져오기
        uid = user != null ? user.getUid() : null;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //버튼 클릭 이벤트
        btChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //이미지를 선택
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplication(), MainActivity.class));
                finish();
            }
        });

        btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txt_product_name.getText().toString().equals("") && !txt_product_price.getText().toString().equals("")) {
                    name = txt_product_name.getText().toString().trim();
                    price = txt_product_price.getText().toString().trim();

                    if(filePath != null) {
                        productUpdates = new HashMap<>();

                        //업로드할 파일이 있으면 이미지업로드
                        uploadFile();

                        image = filename;
                        productInfo = new ProductInfo(name, price, image);
                        productValue = productInfo.toMap();

                        productUpdates.put("/ProductInfo/" + uid + "/" + name, productValue); //가게마다 메뉴 이름별로
                        mDatabase.updateChildren(productUpdates);

                        Toast.makeText(ProductAddActivity.this, "상품이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplication(), MainActivity.class));
                        finish();
                    } else {
                        ImageDialog(view);
                    }
                } else {
                    Toast.makeText(ProductAddActivity.this, "작성하지 않은 항목이 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            Log.d(TAG, "uri:" + String.valueOf(filePath));
            try {
                //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ivPreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //upload the file
    private void uploadFile() {
            //storage
            FirebaseStorage storage = FirebaseStorage.getInstance();

            //Unique한 파일명을 만들자.
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_HHmm");
            Date now = new Date();
            filename = uid + "_" + formatter.format(now) + ".png";
            //storage 주소와 폴더 파일명을 지정해 준다.
            StorageReference storageRef = storage.getReferenceFromUrl("gs://sm-project-400f7.appspot.com").child("Products/" + filename);
            //올라가거라...
            storageRef.putFile(filePath)
                    //성공시
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "이미지 업로드 완료!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //실패시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "이미지 업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //진행중
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests")
                            double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                        }
                    });
    }

    public void ImageDialog (View view) {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(view.getContext());
        alt_bld.setMessage("이미지 없이 상품을 등록합니다.").setCancelable(false)
                .setPositiveButton("네",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                productUpdates = new HashMap<>();

                                image = "";

                                productInfo = new ProductInfo(name, price, image);
                                productValue = productInfo.toMap();

                                productUpdates.put("/ProductInfo/" + uid + "/" + name, productValue); //가게마다 메뉴 이름별로
                                mDatabase.updateChildren(productUpdates);

                                Toast.makeText(ProductAddActivity.this, "상품이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplication(), MainActivity.class));
                                finish();
                            }
                        }).setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();

        //대화창 클릭 시 뒷 배경 어두워지는 것 막기
        alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        //대화창 제목 설정
        alert.setTitle("이미지 확인");

        //대화창 아이콘 설정
        alert.setIcon(R.drawable.exclamation);

        //대화창 배경 색 설정
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.rgb(180, 180, 180)));
        alert.show();

    }

    public class ProductInfo {
        private String name;
        private String price;
        private String image;

        public ProductInfo(){ }

        public ProductInfo(String name ,String price, String image) {
            this.name = name;
            this.price = price;
            this.image = image;
        }

        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("name", name);
            result.put("price", price);
            result.put("image", image);

            return result;
        }
    }
}