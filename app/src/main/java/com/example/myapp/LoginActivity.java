package com.example.myapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etId, etPassword;
    private CheckBox chkAutoLogin;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etId = findViewById(R.id.etId);
        etPassword = findViewById(R.id.etPassword);
        chkAutoLogin = findViewById(R.id.chkAutoLogin);

        db = AppDatabase.getDatabase(this);

        loadLoginDetails();

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        findViewById(R.id.tvForgotPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    private void saveLoginDetails(String id, String password) {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_id", id);
        editor.putString("user_password", password);
        editor.apply();
    }

    private void loadLoginDetails() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String id = preferences.getString("user_id", null);
        String password = preferences.getString("user_password", null);

        if (id != null && password != null) {
            etId.setText(id);
            etPassword.setText(password);
            chkAutoLogin.setChecked(true);
        }
    }

    private void login() {
        String username = etId.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = db.userDao().getUser(username, password);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (user != null) {
                            if (chkAutoLogin.isChecked()) {
                                saveLoginDetails(username, password);
                            }
                            Intent intent = new Intent(LoginActivity.this, MainScreenActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호가 틀렸습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
}
