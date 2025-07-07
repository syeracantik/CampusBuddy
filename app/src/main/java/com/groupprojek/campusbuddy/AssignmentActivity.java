package com.groupprojek.campusbuddy;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;

public class AssignmentActivity extends BaseActivity {

    EditText etTitle;
    TextView tvSelectedDate;
    Button btnPickDateTime, btnAdd;
    RecyclerView recyclerView;

    AssignmentDBHelper dbHelper;
    ArrayList<AssignmentModel> assignmentList;
    AssignmentAdapter adapter;

    String selectedDate = "";
    String currentUid = "";

    private FirebaseAuth mAuth;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_assignment;
    }

    @Override
    protected void initViews() {
        setToolbarTitle("My Assignments");

        etTitle = findViewById(R.id.etAssignmentTitle);
        tvSelectedDate = findViewById(R.id.tvSelectedDateTime);
        btnPickDateTime = findViewById(R.id.btnPickDateTime);
        btnAdd = findViewById(R.id.btnAddAssignment);
        recyclerView = findViewById(R.id.recyclerAssignments);

        dbHelper = new AssignmentDBHelper(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login first.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        currentUid = currentUser.getUid();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        btnPickDateTime.setOnClickListener(v -> pickDateTime());
        btnAdd.setOnClickListener(v -> addAssignment());

        loadData();
    }

    private void pickDateTime() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (timeView, hourOfDay, minute) -> {
                                selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year
                                        + " " + hourOfDay + ":" + String.format("%02d", minute);
                                tvSelectedDate.setText(selectedDate);
                            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                    timePickerDialog.show();
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void addAssignment() {
        String title = etTitle.getText().toString().trim();
        if (title.isEmpty() || selectedDate.isEmpty()) {
            Toast.makeText(this, "Please enter title and date", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.addAssignment(title, selectedDate, currentUid);
        showNotification(title, selectedDate);
        setReminder(title, selectedDate);

        etTitle.setText("");
        tvSelectedDate.setText("No date selected");
        selectedDate = "";

        loadData();
    }

    private void loadData() {
        assignmentList = dbHelper.getAssignmentsByUid(currentUid);
        adapter = new AssignmentAdapter(this, assignmentList, dbHelper);
        recyclerView.setAdapter(adapter);
    }

    private void showNotification(String title, String dueDate) {
        String channelId = "assignment_channel";
        String channelName = "Assignment Notifications";

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, AssignmentActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        androidx.core.app.NotificationCompat.Builder builder = new androidx.core.app.NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("New Assignment Added")
                .setContentText(title + " - Due: " + dueDate)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void setReminder(String title, String dueDate) {
        Calendar reminderTime = Calendar.getInstance();
        String[] dateParts = dueDate.split("[ /:]");
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1;
        int year = Integer.parseInt(dateParts[2]);
        int hour = Integer.parseInt(dateParts[3]);
        int minute = Integer.parseInt(dateParts[4]);

        reminderTime.set(year, month, day, hour, minute);
        reminderTime.add(Calendar.MINUTE, -10);

        Intent reminderIntent = new Intent(this, AssignmentReminderReceiver.class);
        reminderIntent.putExtra("title", title);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) System.currentTimeMillis(),
                reminderIntent,
                PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTime.getTimeInMillis(), pendingIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
