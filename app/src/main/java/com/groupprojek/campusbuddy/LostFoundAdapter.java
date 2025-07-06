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

public class LostFoundAdapter extends RecyclerView.Adapter<LostFoundAdapter.ViewHolder> {

    Context context;
    ArrayList<LostFoundModel> list;

    public LostFoundAdapter(Context context, ArrayList<LostFoundModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lostfound_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        LostFoundModel item = list.get(pos);
        holder.tvTitle.setText(item.getTitle());
        holder.tvDesc.setText(item.getDescription());
        if (!item.getImageUri().isEmpty()) {
            holder.imgItem.setImageURI(Uri.parse(item.getImageUri()));
        } else {
            holder.imgItem.setImageResource(R.drawable.ic_upload);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem;
        TextView tvTitle, tvDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.imgItem);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvDesc = itemView.findViewById(R.id.tvItemDesc);
        }
    }
}