package com.groupprojek.campusbuddy;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {

    Context context;
    ArrayList<AssignmentModel> assignmentList;
    AssignmentDBHelper dbHelper;

    public AssignmentAdapter(Context context, ArrayList<AssignmentModel> assignmentList, AssignmentDBHelper dbHelper) {
        this.context = context;
        this.assignmentList = assignmentList;
        this.dbHelper = dbHelper;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.assignment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AssignmentModel assignment = assignmentList.get(position);
        holder.tvTitle.setText(assignment.getTitle());
        holder.tvDueDate.setText(assignment.getDueDate());

        holder.btnDelete.setOnClickListener(v -> {
            dbHelper.deleteAssignment(assignment.getId());
            assignmentList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, assignmentList.size());
            Toast.makeText(context, "Assignment deleted", Toast.LENGTH_SHORT).show();
        });

        holder.btnEdit.setOnClickListener(v -> {
            showEditDialog(assignment, position);
        });
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDueDate;
        ImageButton btnDelete, btnEdit;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvAssignmentTitle);
            tvDueDate = itemView.findViewById(R.id.tvAssignmentDueDate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);  // pastikan ada di layout assignment_item.xml
        }
    }

    private void showEditDialog(AssignmentModel assignment, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Assignment");

        View viewInflated = LayoutInflater.from(context).inflate(R.layout.dialog_edit_assignment, null);
        final TextView inputTitle = viewInflated.findViewById(R.id.etEditTitle);
        final TextView tvEditDate = viewInflated.findViewById(R.id.tvEditDate);

        inputTitle.setText(assignment.getTitle());
        tvEditDate.setText(assignment.getDueDate());

        final Calendar calendar = Calendar.getInstance();

        tvEditDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                    (view, year, month, dayOfMonth) -> {
                        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                                (timeView, hourOfDay, minute) -> {
                                    String formattedDate = dayOfMonth + "/" + (month + 1) + "/" + year
                                            + " " + String.format("%02d:%02d", hourOfDay, minute);
                                    tvEditDate.setText(formattedDate);
                                },
                                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                        timePickerDialog.show();
                    },
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        builder.setView(viewInflated);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newTitle = inputTitle.getText().toString().trim();
            String newDate = tvEditDate.getText().toString().trim();

            if (!newTitle.isEmpty() && !newDate.isEmpty()) {
                dbHelper.updateAssignment(assignment.getId(), newTitle, newDate);

                assignment.setTitle(newTitle);
                assignment.setDueDate(newDate);
                notifyItemChanged(position);

                Toast.makeText(context, "Assignment updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            }

            dialog.dismiss();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}