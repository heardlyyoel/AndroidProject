package com.example.testchatfragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;


public class profilePage extends Fragment {
//permmit to pick or tack image
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private EditText txtName, txtEmail, txtPassword, txtNumber, txtMajor;
    private Button btnEditProfile, btnUploadPhoto;
    private FirebaseFirestore db; //object to acces database firestore
    private String userId;
    private ImageView profileImageView;
    private Uri photoUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_page, container, false);

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", requireActivity().MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", ""); //from sherepreferences we alredy save before

        profileImageView = view.findViewById(R.id.profileImageView);
        txtName = view.findViewById(R.id.txtName);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtPassword = view.findViewById(R.id.txtPassword);
        txtNumber = view.findViewById(R.id.txtNumber);
        txtMajor = view.findViewById(R.id.txtMajor);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnUploadPhoto = view.findViewById(R.id.btnUploadPhoto);

        loadUserProfile();

        return view;

    }

    private void loadUserProfile() {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        String phone = documentSnapshot.getString("numberPhone");
                        String password = documentSnapshot.getString("password");
                        String major = documentSnapshot.getString("major");
                        String photoUrl = documentSnapshot.getString("photoUrl");

                        txtName.setText(name);
                        txtEmail.setText(email);
                        txtNumber.setText(phone);
                        txtMajor.setText(major);
                        txtPassword.setText(password);

                        if (photoUrl != null) {
                            Glide.with(this).load(photoUrl).into(profileImageView);
                        }
                    } else {
                        Toast.makeText(getActivity(), "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting user profile", e);
                    Toast.makeText(getActivity(), "Error loading profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateProfile() {
        String name = txtName.getText().toString();
        String email = txtEmail.getText().toString();
        String phone = txtNumber.getText().toString();
        String major = txtMajor.getText().toString();

        UserProfile userProfile = new UserProfile(name, email, phone, major);

        db.collection("users").document(userId)
                .set(userProfile, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(error -> {
                    Toast.makeText(getActivity(), "Error updating profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void openImagePicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Upload Foto")
                .setItems(new String[]{"Pilih dari Galeri", "Ambil Foto"}, (dialog, which) -> {
                    if (which == 0) {
                        // Pilih dari galeri
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, REQUEST_IMAGE_PICK);
                    } else {
                        // Ambil foto
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        try {
                            // Buat file untuk menyimpan foto
                            File photoFile = createImageFile();
                            photoUri = FileProvider.getUriForFile(requireContext(), requireActivity().getPackageName() + ".fileprovider", photoFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                        } catch (IOException e) {
                            Log.e("ProfilePage", "Error creating image file", e);
                            Toast.makeText(getActivity(), "Gagal membuat file gambar", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }

    private File createImageFile() throws IOException {
        // Buat file gambar sementara when we get acoording user id
        String imageFileName = "profile_" + userId + "_";
        File storageDir = requireContext().getExternalFilesDir(null);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { //handel chek result activitas
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = null;

            if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                // Mendapatkan URI gambar dari galeri
                imageUri = data.getData();
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Menggunakan URI foto yang telah dibuat
                imageUri = photoUri;
            }

            // Memastikan imageUri tidak null sebelum melanjutkan
            if (imageUri != null) {
                Glide.with(this).load(imageUri).into(profileImageView);
                uploadProfilePhoto(imageUri);
            } else {
                Toast.makeText(getActivity(), "Gagal mendapatkan gambar", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Operasi dibatalkan", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadProfilePhoto(Uri photoUri) {
        StorageReference photoRef = FirebaseStorage.getInstance().getReference().child("profile_photos/" + userId + ".jpg"); //make referensi to fire base storage for upload

        photoRef.putFile(photoUri)
                .addOnSuccessListener(taskSnapshot -> photoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    savePhotoUrlToFirestore(uri.toString()); //upload foto user url we get
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Gagal mengunggah foto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void savePhotoUrlToFirestore(String photoUrl) { //update (change) url photo with new photo
        db.collection("users").document(userId)
                .update("photoUrl", photoUrl)
                .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Foto profil diperbarui!", Toast.LENGTH_SHORT).show()) //if succes
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Gagal memperbarui foto profil: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public static class UserProfile { //save user data
        String name;
        String email;
        String numberPhone;
        String major;

        public UserProfile() {}

        public UserProfile(String name, String email, String numberPhone, String major) {
            this.name = name;
            this.email = email;
            this.numberPhone = numberPhone;
            this.major = major;
        }
    }
}
