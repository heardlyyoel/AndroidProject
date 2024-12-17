package com.example.testchatfragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.view.Gravity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {

    EditText edtUsername, edtPassword;
    Button btnLogin;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance(); // Inisialisasi Firebase Authentication for login with user alredy have
        edtUsername = findViewById(R.id.usernameET);
        edtPassword = findViewById(R.id.passwordET);
        btnLogin = findViewById(R.id.signInBtn);

        btnLogin.setOnClickListener(view -> {
            String email = edtUsername.getText().toString();
            String password = edtPassword.getText().toString();
            loginUser(email, password);
        });
    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)  //try login with user firebase with email & password
                .addOnCompleteListener(this, task -> { //memantau result
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser(); //get information user enter
                        if (user != null) {
                            String uid = user.getUid(); //get id
                            saveUserId(uid); //save id in sherePreferences
                            navigateToProfilePage(); //direct to profile
                        }
                    } else { //handle eror
                        Toast.makeText(login.this, "Login gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserId(String uid) { //save userid succes to enter this app into shereprefences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE); //get object
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", uid); // Simpan userId untuk digunakan di profilePage
        editor.apply(); //save change with asinkron
    }

    private void navigateToProfilePage() {
        Intent intent = new Intent(login.this, MainActivity.class); // Sesuaikan jika MainActivity yang membuka profilePage
        intent.putExtra("openProfile", true); // Indikasi untuk membuka profilePage
        startActivity(intent);
    }
}
