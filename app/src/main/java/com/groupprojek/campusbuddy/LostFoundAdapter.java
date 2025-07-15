package com.groupprojek.campusbuddy;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import java.util.List;

public class LostFoundAdapter extends RecyclerView.Adapter<LostFoundAdapter.ViewHolder> {

    private final List<LostFoundModel> items = new ArrayList<>();

    // ---------- ViewHolder ----------
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemTitle, tvItemDesc;

        ViewHolder(View v) {
            super(v);
            tvItemTitle = v.findViewById(R.id.tvItemTitle);
            tvItemDesc  = v.findViewById(R.id.tvItemDesc);
        }
    }

    // ---------- Public helper ----------
    public void submit(List<LostFoundModel> list) {
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    // ---------- Adapter overrides ----------
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lostfound_item, parent, false);   // ← nama layout awak
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        LostFoundModel m = items.get(position);

        h.tvItemTitle.setText(m.getItemName());
        h.tvItemDesc .setText(m.getDescription());
    }

    @Override
    public int getItemCount() { return items.size(); }
}