package com.groupprojek.campusbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends BaseActivity {

    MaterialButton btnViewAssignments, btnOpenMap, btnProfile, btnLostFound;
    TextView tvNextDueAssignment;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    AssignmentDBHelper dbHelper;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        setToolbarTitle("CampusBuddy");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        if (mUser == null) {
            Toast.makeText(this, "Please login first.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        tvNextDueAssignment = findViewById(R.id.tvNextDueAssignment);
        btnViewAssignments = findViewById(R.id.btnViewAssignments);
        btnOpenMap = findViewById(R.id.btnOpenMap);
        btnProfile = findViewById(R.id.btnProfile);
        btnLostFound = findViewById(R.id.btnLostFound);

        dbHelper = new AssignmentDBHelper(this);

        btnViewAssignments.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AssignmentActivity.class)));

        btnOpenMap.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, MapActivity.class)));

        btnProfile.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ProfileActivity.class)));

        btnLostFound.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, LostFoundActivity.class)));

        loadNextDueAssignment();
    }

    private void loadNextDueAssignment() {
        ArrayList<AssignmentModel> list = dbHelper.getAssignmentsByUid(mUser.getUid());
        if (list.isEmpty()) {
            tvNextDueAssignment.setText("No upcoming assignments.");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date now = new Date();

        AssignmentModel nextAssignment = null;
        Date nextDate = null;

        for (AssignmentModel a : list) {
            try {
                Date dueDate = sdf.parse(a.getDueDate());
                if (dueDate != null && dueDate.after(now)) {
                    if (nextDate == null || dueDate.before(nextDate)) {
                        nextDate = dueDate;
                        nextAssignment = a;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (nextAssignment != null) {
            tvNextDueAssignment.setText(nextAssignment.getTitle() + " - Due: " + nextAssignment.getDueDate());
        } else {
            tvNextDueAssignment.setText("No upcoming assignments.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNextDueAssignment();
    }
}
