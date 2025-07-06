package com.groupprojek.campusbuddy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LostFoundActivity extends BaseActivity {

    ImageView imgLostItem;
    Button btnChangePhoto, btnSubmit;
    EditText etItemTitle, etItemDescription;
    RecyclerView recyclerView;

    Uri photoUri;
    File photoFile;

    LostFoundDBHelper dbHelper;
    LostFoundAdapter adapter;
    ArrayList<LostFoundModel> list;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_lostfound;
    }

    @Override
    protected void initViews() {
        setToolbarTitle("Lost & Found");

        imgLostItem = findViewById(R.id.imgLostItem);
        btnChangePhoto = findViewById(R.id.btnChangePhotoLost);
        btnSubmit = findViewById(R.id.btnSubmitLostItem);
        etItemTitle = findViewById(R.id.etItemTitle);
        etItemDescription = findViewById(R.id.etItemDescription);
        recyclerView = findViewById(R.id.recyclerLostFound);

        dbHelper = new LostFoundDBHelper(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadData();

        btnChangePhoto.setOnClickListener(v -> showImagePickerOptions());
        btnSubmit.setOnClickListener(v -> submitLostItem());
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
        try {
            photoFile = createImageFile();
            photoUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    photoFile
            );

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            cameraLauncher.launch(intent);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "LOST_" + timeStamp;
        File storageDir = getExternalFilesDir(null);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            imgLostItem.setImageURI(photoUri);
                        }
                    });

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            photoUri = result.getData().getData();
                            imgLostItem.setImageURI(photoUri);
                        }
                    });

    private void submitLostItem() {
        String title = etItemTitle.getText().toString().trim();
        String description = etItemDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String imagePath = (photoUri != null) ? photoUri.toString() : "";

        dbHelper.addItem(title, description, imagePath);
        Toast.makeText(this, "Item saved!", Toast.LENGTH_SHORT).show();

        etItemTitle.setText("");
        etItemDescription.setText("");
        imgLostItem.setImageResource(R.drawable.ic_upload);
        photoUri = null;

        loadData();
    }

    private void loadData() {
        list = dbHelper.getAllItems();
        adapter = new LostFoundAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }
}
