package com.example.lsyz1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity implements OnClickListener{
    final static int TAKE_PICTURE = 1;
    final static int GET_GALLERY_IMAGE = 2;
    private Button Button;
    FirebaseDatabase database;
    DatabaseReference myRef;

    Button takePicture;
    Button save;
    Button gallery;
    ImageView imageview;
    Uri selectedImageUri;

    //ON OFF 버튼 클릭 이벤트



    public void addListenerOnButtonClick() {
        //Getting the ToggleButton and Button instance from the layout xml file
        Button = (Button) findViewById(R.id.toggleButton);
        //Performing action on button click
        Button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                StringBuilder result = new StringBuilder();


                result.append(Button.getText());
                if(result.toString().equals("열기")){
                    Toast.makeText(getApplicationContext(), "열기", Toast.LENGTH_LONG).show();
                    //Database의 DoorRock 값을 ON로 갱신한다.
                    myRef.setValue("ON");
                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "열기", Toast.LENGTH_LONG).show();

                            //Database이 DoorRock 값을 OFF로 갱신한다.
                            myRef.setValue("OFF");
                        }
                    },2000);

                }
                //Displaying the message in toast
                //Toast.makeText(getApplicationContext(), result.toString(),Toast.LENGTH_LONG).show();
            }

        });
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("문 열기");
        //앱이 시작되면서 Database의 DoorRock값을 읽어 와서 토글버튼의 초기 값을 변경


        //ON OFF 버튼 클릭시 실행
        addListenerOnButtonClick();

        takePicture = findViewById(R.id.takePicture);
        save = findViewById(R.id.save);
        gallery = findViewById(R.id.gallery);
        imageview = findViewById(R.id.imageView);

        takePicture.setOnClickListener(this);
        gallery.setOnClickListener(this);
        save.setOnClickListener(this);


        // 마시멜로우 이상일 경우에는 권한 체크 후 권한 요청
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    ==checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){}
            else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},1 );
            }
        }
        Button btn_log = findViewById(R.id.btn_log);
        btn_log.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                //회원가입 화면으로 이동
                Intent intent = new Intent(MainActivity.this, logActivity.class);
                startActivity(intent);

            }
        });
    }

    // 권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED){
            Log.d("로그", "Permission: " + permissions[0] + " was " + grantResults[0]);
        }
    }

    // 버튼 이벤트 리스너
    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()) {
            case R.id.takePicture: // 사진 찍기
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, TAKE_PICTURE);
                break;
            case R.id.gallery: // 갤러리 들어가기
                intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
                break;
            case R.id.save: // 파이어베이스 이미지 업로드
                clickUpload();
                selectedImageUri = null;
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 사진 촬영 완료 후 응답
        if(requestCode == TAKE_PICTURE) {
            if(resultCode == RESULT_OK && data.hasExtra("data")) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                if(bitmap != null) imageview.setImageBitmap(bitmap);

                String imageSaveUri = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "사진 저장", "찍은 사진이 저장되었습니다.");
                selectedImageUri = Uri.parse(imageSaveUri);
                Log.d("Firebase Storage", "MainActivity - onActivityResult() called" + selectedImageUri);
            }
        }

        // 갤러리에서 이미지 가져온 후의 응답
        else if(requestCode == GET_GALLERY_IMAGE){
            if(resultCode == RESULT_OK && data.getData() != null) {
                selectedImageUri = data.getData();
                Log.d("Firebase Storage", "MainActivity - onActivityResult() called" + selectedImageUri);
                Log.d("Firebase Storage", "MainActivity - onActivityResult() called" + getRealPathFromURI(selectedImageUri));


            }
        }
    }

    // 절대 경로로 변경
    public String getRealPathFromURI(Uri contentUri) {

        String[] proj = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToNext();
        @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));

        cursor.close();
        return path;
    }

    // 파이어베이스 업로드 함수
    public void clickUpload() {

        // 1. FirebaseStorage을 관리하는 객체 얻어오기
        FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();

        // 로그인 이름으로 파일명 지정
        // 원래 확장자는 파일의 실제 확장자를 얻어와서 사용해야함. 그러려면 이미지의 절대 주소를 구해야함.
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String filename = bundle.getString("mEtName")+".jpg";

        StorageReference imgRef= firebaseStorage.getReference("uploads/"+filename);
        // uploads라는 폴더가 없으면 자동 생성

        // 참조 객체를 통해 이미지 파일 업로드
        // imgRef.putFile(imgUri);
        // 업로드 결과를 받고 싶다면 아래와 같이 UploadTask를 사용하면 된다.
        UploadTask uploadTask =imgRef.putFile(selectedImageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(MainActivity.this, "success upload", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firebase Storage", "MainActivity - onFailure() called");
                    }
                });


    }

}

 
