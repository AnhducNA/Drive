package com.kma.drive;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SanPhamAdapter extends RecyclerView.Adapter<SanPhamAdapter.ViewHolder> {
    private String TAG = "Duc";

    Context context;
    ArrayList<SanPham> listSanPham;

    public SanPhamAdapter(Context context, ArrayList<SanPham> listSanPham) {
        this.context = context;
        this.listSanPham = listSanPham;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // gán view
        Log.d(TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(context).inflate(R.layout.row_item_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Gán dữ liêu
        SanPham sanPham = listSanPham.get(position);
        Log.d("DUC", "onBindViewHolder: " + sanPham.getTenSanPham());
        holder.txtTenSanPham.setText(sanPham.getTenSanPham());
        holder.imgAvatar.setImageResource(sanPham.getHinhSanPham());
    }

    @Override
    public int getItemCount() {
        return listSanPham.size(); // trả item tại vị trí postion
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtTenSanPham;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ view
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtTenSanPham = itemView.findViewById(R.id.txtTenSanPham);
        }
    }
}
