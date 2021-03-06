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
        user = firebaseAuth.getCurrentUser(); //???????????? ????????? ?????? ????????????
        uid = user != null ? user.getUid() : null;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //?????? ?????? ?????????
        btChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //???????????? ??????
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "???????????? ???????????????."), 0);
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

                        //???????????? ????????? ????????? ??????????????????
                        uploadFile();

                        image = filename;
                        productInfo = new ProductInfo(name, price, image);
                        productValue = productInfo.toMap();

                        productUpdates.put("/ProductInfo/" + uid + "/" + name, productValue); //???????????? ?????? ????????????
                        mDatabase.updateChildren(productUpdates);

                        Toast.makeText(ProductAddActivity.this, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplication(), MainActivity.class));
                        finish();
                    } else {
                        ImageDialog(view);
                    }
                } else {
                    Toast.makeText(ProductAddActivity.this, "???????????? ?????? ????????? ????????????.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //?????? ??????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //request????????? 0?????? OK??? ???????????? data??? ????????? ?????? ?????????
        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
            Log.d(TAG, "uri:" + String.valueOf(filePath));
            try {
                //Uri ????????? Bitmap?????? ???????????? ImageView??? ?????? ?????????.
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

            //Unique??? ???????????? ?????????.
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_HHmm");
            Date now = new Date();
            filename = uid + "_" + formatter.format(now) + ".png";
            //storage ????????? ?????? ???????????? ????????? ??????.
            StorageReference storageRef = storage.getReferenceFromUrl("gs://sm-project-400f7.appspot.com").child("Products/" + filename);
            //???????????????...
            storageRef.putFile(filePath)
                    //?????????
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "????????? ????????? ??????!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //?????????
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "????????? ????????? ??????!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //?????????
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
        alt_bld.setMessage("????????? ?????? ????????? ???????????????.").setCancelable(false)
                .setPositiveButton("???",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                productUpdates = new HashMap<>();

                                image = "";

                                productInfo = new ProductInfo(name, price, image);
                                productValue = productInfo.toMap();

                                productUpdates.put("/ProductInfo/" + uid + "/" + name, productValue); //???????????? ?????? ????????????
                                mDatabase.updateChildren(productUpdates);

                                Toast.makeText(ProductAddActivity.this, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplication(), MainActivity.class));
                                finish();
                            }
                        }).setNegativeButton("?????????",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();

        //????????? ?????? ??? ??? ?????? ??????????????? ??? ??????
        alert.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        //????????? ?????? ??????
        alert.setTitle("????????? ??????");

        //????????? ????????? ??????
        alert.setIcon(R.drawable.exclamation);

        //????????? ?????? ??? ??????
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