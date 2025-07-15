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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LostFoundActivity extends BaseActivity {

    private EditText etTitle, etDesc;
    private Button   btnSubmit;
    private LostFoundAdapter adapter;

    private FirebaseFirestore db;
    private CollectionReference lostFoundCol;
    private FirebaseAuth auth;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_lostfound;
    }

    @Override
    protected void initViews() {
        setToolbarTitle("Lost & Found");

        etTitle   = findViewById(R.id.etItemTitle);
        etDesc    = findViewById(R.id.etItemDescription);
        btnSubmit = findViewById(R.id.btnSubmitLostItem);
        RecyclerView rv = findViewById(R.id.recyclerLostFound);

        db           = FirebaseFirestore.getInstance();
        lostFoundCol = db.collection("lostFound");
        auth         = FirebaseAuth.getInstance();

        adapter = new LostFoundAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        lostFoundCol.orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(this, (snap, err) -> {
                    if (err != null || snap == null) return;

                    List<LostFoundModel> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        LostFoundModel m = doc.toObject(LostFoundModel.class);
                        if (m != null) {
                            list.add(new LostFoundModel(
                                    doc.getId(),
                                    m.getItemName(),
                                    m.getDescription(),
                                    m.getStatus(),
                                    m.getTimestamp()));
                        }
                    }
                    adapter.submit(list);
                });

        btnSubmit.setOnClickListener(v -> saveLostItem());
    }

    private void saveLostItem() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Sila log masuk dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = etTitle.getText().toString().trim();
        String desc  = etDesc .getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Required");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("itemName",   title);
        data.put("description",desc);
        data.put("status",     "lost");
        data.put("timestamp",  System.currentTimeMillis());

        lostFoundCol.add(data)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show();
                    etTitle.setText("");
                    etDesc .setText("");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
