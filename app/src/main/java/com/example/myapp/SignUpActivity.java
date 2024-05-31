package com.example.myapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText etId, etPassword, etConfirmPassword;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etId = findViewById(R.id.etId);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        db = AppDatabase.getDatabase(this);

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btnSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {
        String username = etId.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (password.equals(confirmPassword)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    User existingUser = db.userDao().getUserByUsername(username);
                    if (existingUser != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SignUpActivity.this, "이미 존재하는 아이디입니다", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        User user = new User();
                        user.username = username;
                        user.password = password;
                        db.userDao().insert(user);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SignUpActivity.this, "회원가입 완료", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                }
            }).start();
        } else {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
        }
    }
}
