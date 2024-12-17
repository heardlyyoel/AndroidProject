package com.example.testchatfragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;//representasi object pictrue, save and manage picture in app
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostStoryActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 99;
    private Button btnSnap;
    private ImageView imageView;
    private ImageView btnPost;
    private EditText editDescription;
    private String savedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_story);
//Delarion and initialitation
        btnSnap = findViewById(R.id.btncamera);
        imageView = findViewById(R.id.imageview1);
        btnPost = findViewById(R.id.btnPost);
        editDescription = findViewById(R.id.editDescription);

        btnSnap.setOnClickListener(new View.OnClickListener() { //open cam
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CODE);
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() { //post picture, and description
            @Override
            public void onClick(View view) {
                String description = editDescription.getText().toString();
                Toast.makeText(PostStoryActivity.this, "Posted: " + description, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) { //chek resulut from cam and status succes
            Bitmap picTaken = (Bitmap) data.getExtras().get("data");//get picture as Bitmap show in imagevew
            imageView.setImageBitmap(picTaken);

            // Simpan gambar ke penyimpanan lokal
            savedImagePath = saveImageToStorage(picTaken);

            if (savedImagePath != null) {
                // Simpan path ke SharedPreferences so can acces and then time
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("last_saved_image_path", savedImagePath);
                editor.apply();
            }
        } else {
            Toast.makeText(this, "Camera Canceled", Toast.LENGTH_SHORT).show();
        }
    }

    private String saveImageToStorage(Bitmap bitmap) { //save bitmap as picture in external storage
        String imageFileName = "JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "TestChatFragment");//directory storage

        if (!storageDir.exists()) {
            storageDir.mkdirs(); //make folder if storage not yet
        }

        File imageFile = new File(storageDir, imageFileName); //crate obejct for picture
        try (FileOutputStream fos = new FileOutputStream(imageFile)) { //open flowout to write Bitmap to file picture
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); //compress bitmap to jpeg
            fos.flush();
            Toast.makeText(this, "Image saved: " + imageFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            return imageFile.getAbsolutePath(); //retutn path file if succes
        } catch (IOException e) { //handle eror
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
