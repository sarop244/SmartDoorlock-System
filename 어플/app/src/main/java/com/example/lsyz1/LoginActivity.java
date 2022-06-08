package com.example.lsyz1;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;      //파이어베이스 인증
    private DatabaseReference mDatabaseRef;  //실시간 데이터베이스
    private EditText mEtEmail, mEtName, mEtPwd ;        //로그인 입력필드
    private Button btn_login;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Doorlock");

        mEtEmail = findViewById(R.id.et_email);
        mEtName = findViewById(R.id.et_name);
        mEtPwd = findViewById(R.id.et_pwd);
        btn_login = findViewById(R.id.btn_login);

        mEtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {


                if (editable.length() >= 6) {
                    btn_login.setClickable(true);
                    btn_login.setBackgroundColor(Color.BLUE);
                } else {
                    btn_login.setClickable(false);
                    btn_login.setBackgroundColor(Color.GRAY);
                }
            }
        });



        btn_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                // 로그인 요청
                String strEmail = mEtEmail.getText().toString();
                String strName = mEtName.getText().toString();
                String strPwd = mEtPwd.getText().toString();



                mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            private Object Time;

                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    //로그인 성공 !!!
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("mEtName", strName);
                                    startActivity(intent);
                                    finish();
                                    ; // 현재 액티비티 파괴
                                    ; // 현재 액티비티 파괴

                                    FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

                                    UserAccount account = new UserAccount();
                                    account.setIdToken(firebaseUser.getUid());
                                    account.setEmailId(firebaseUser.getEmail());
                                    account.setUsername(strName);
                                    account.setPassword(strPwd);

                                    account.setTime = ServerValue.TIMESTAMP;
                                    //User Account의 setTime을 받고 서버시간이 들어감 이때 서버시간은 출력형식으로 변경되어 현재시간으로 출력됨


                                    // setValue : database insert (삽입) 행위
                                    mDatabaseRef.child("LoginAccount").child(firebaseUser.getUid()).setValue(account);
                                }

                                else {
                                    Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();


                                }


                            }



                        }

                );


            }

        });

        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                //회원가입 화면으로 이동
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });


    }

}
 
