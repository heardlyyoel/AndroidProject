package com.example.testchatfragment;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.Manifest;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    ImageButton btnProfile,btnBluetooth, lightSensorImage;
    ImageView homeImage, chatImage, storiesImage, callImage;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });


        // Inisialisasi views
        homeImage = findViewById(R.id.homeImage);
        chatImage = findViewById(R.id.chatImage);
        storiesImage = findViewById(R.id.storiesImage);
        callImage = findViewById(R.id.callImage);
        btnProfile = findViewById(R.id.btnProfile);
        btnBluetooth = findViewById(R.id.btnBluetooth);
        lightSensorImage = findViewById(R.id.lightSensorImage); // Inisialisasi light sensor


        // Set click listener untuk light sensor
        lightSensorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateButton(lightSensorImage); // Tambahkan animasi
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.fragmentContainerView, LightSensorFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("light_sensor")
                        .commit();
            }
        });

        // Set click listener dengan animasi
        homeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateButton(homeImage); // Tambahkan animasi
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.fragmentContainerView, homePage.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("home")
                        .commit();
            }
        });

        chatImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateButton(chatImage); // Tambahkan animasi
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.fragmentContainerView, chatPage.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("chat")
                        .commit();
            }
        });

        storiesImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateButton(storiesImage); // Tambahkan animasi
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.fragmentContainerView, StoriesFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("stories")
                        .commit();
            }
        });

        callImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateButton(callImage); // Tambahkan animasi
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.fragmentContainerView, callPage.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("call")
                        .commit();
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateButton(btnProfile); // Tambahkan animasi
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.fragmentContainerView, profilePage.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("profile")
                        .commit();
            }
        });

        btnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateButton(btnBluetooth); // Tambahkan animasi
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.fragmentContainerView, bluetooth.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("bluetooth")
                        .commit();
            }
        });

    }


    // Method untuk menjalankan animasi pada tombol dengan animasi skala, rotasi, dan alpha
    // Method untuk menjalankan animasi pada tombol dengan efek membesar lalu mengecil
    // Method untuk menjalankan animasi pada tombol dengan efek membesar lalu mengecil
    private void animateButton(View view) {
        // Animasi Skala (Membesar lebih besar dan lebih lambat)
        ObjectAnimator scaleXUp = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.5f); // Membesar lebih besar
        ObjectAnimator scaleYUp = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.5f); // Membesar lebih besar

        ObjectAnimator scaleXDown = ObjectAnimator.ofFloat(view, "scaleX", 1.5f, 1.0f); // Mengecil kembali
        ObjectAnimator scaleYDown = ObjectAnimator.ofFloat(view, "scaleY", 1.5f, 1.0f); // Mengecil kembali

        // Gabungkan animasi dalam urutan: membesar dulu, lalu mengecil
        AnimatorSet scaleUpSet = new AnimatorSet();
        scaleUpSet.playTogether(scaleXUp, scaleYUp);
        scaleUpSet.setDuration(300); // Durasi membesar lebih lambat

        AnimatorSet scaleDownSet = new AnimatorSet();
        scaleDownSet.playTogether(scaleXDown, scaleYDown);
        scaleDownSet.setDuration(300); // Durasi mengecil kembali juga lebih lambat

        // Gabungkan keduanya menjadi satu urutan
        AnimatorSet finalSet = new AnimatorSet();
        finalSet.playSequentially(scaleUpSet, scaleDownSet);
        finalSet.setInterpolator(new AccelerateDecelerateInterpolator()); // Efek interpolasi halus
        finalSet.start(); // Mulai animasi
    }


}