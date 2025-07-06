package com.groupprojek.campusbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProfileActivity extends BaseActivity {

    ImageView imgProfile;
    Button btnChangePhoto, btnLogout, btnStudentPortal;
    TextView tvUserName, tvUserEmail, tvUserMatric;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserProfile";
    private static final String KEY_IMAGE_PATH = "profile_image_path";

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_profile;
    }

    @Override
    protected void initViews() {
        setToolbarTitle("My Profile");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        imgProfile = findViewById(R.id.imgProfile);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        btnLogout = findViewById(R.id.btnLogout);
        btnStudentPortal = findViewById(R.id.btnStudentPortal); // ← New line
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserMatric = findViewById(R.id.tvUserMatric);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        loadSavedProfileImage();

        String uid = currentUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("fullName").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String matric = snapshot.child("matricNo").getValue(String.class);

                tvUserName.setText(name);
                tvUserEmail.setText(email);
                tvUserMatric.setText(matric);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });

        btnChangePhoto.setOnClickListener(v -> showImagePickerOptions());

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });

        // 🔗 Open Student Portal Button
        btnStudentPortal.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://istudent.uitm.edu.my/index_isp.htm"));
            startActivity(browserIntent);
        });
    }

    private void showImagePickerOptions() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Select Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) openCamera();
                    else openGallery();
                })
                .show();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                            imgProfile.setImageBitmap(photo);
                            saveImageToInternalStorage(photo);
                        }
                    });

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri selectedImage = result.getData().getData();
                            try {
                                InputStream inputStream = getContentResolver().openInputStream(selectedImage);
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                                imgProfile.setImageBitmap(bitmap);
                                saveImageToInternalStorage(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

    private void saveImageToInternalStorage(Bitmap bitmap) {
        File file = new File(getFilesDir(), "profile.jpg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_IMAGE_PATH, file.getAbsolutePath());
            editor.apply();
            Toast.makeText(this, "Photo Saved!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSavedProfileImage() {
        String imagePath = sharedPreferences.getString(KEY_IMAGE_PATH, null);
        if (imagePath != null) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                imgProfile.setImageURI(Uri.fromFile(imgFile));
            }
        }
    }
}
