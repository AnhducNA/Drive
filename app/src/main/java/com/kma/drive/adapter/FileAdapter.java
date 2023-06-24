package com.kma.drive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.drive.R;
import com.kma.drive.callback.FragmentCallback;
import com.kma.drive.callback.ItemFileClickListener;
import com.kma.drive.dto.FileDto;
import com.kma.drive.util.Util;

import java.lang.ref.WeakReference;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    private String TAG = "Duc";

    private WeakReference<Context> mContext;
    private List<FileDto> mFiles;
    private FragmentCallback mCallback;
    private ItemFileClickListener mClickListener;

    public FileAdapter(Context context, List<FileDto> files, FragmentCallback callback, ItemFileClickListener clickListener) {
        this.mContext = new WeakReference<>(context);
        this.mFiles = files;
        mCallback = callback;
        mClickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext.get()).inflate(R.layout.row_item_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Gán dữ liêu
        FileDto file = mFiles.get(position);
        int icon = Util.getIconFileFromType(file.getType());
        String modifyText = mContext.get().getString(R.string.text_item_last_modify_time) + " " + file.getDate().toString();

        holder.txtTenSanPham.setText(file.getFileName());
        holder.imgIconFile.setImageResource(icon);
        holder.recentlyTimeTextView.setText(modifyText);
    }

    @Override
    public int getItemCount() {
        return mFiles.size(); // trả item tại vị trí postion
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIconFile;
        TextView txtTenSanPham;
        TextView recentlyTimeTextView;
        ImageButton functionButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ view
            imgIconFile = itemView.findViewById(R.id.imgAvatar);
            txtTenSanPham = itemView.findViewById(R.id.txtTenSanPham);
            recentlyTimeTextView = itemView.findViewById(R.id.open_recent_main);
            functionButton = itemView.findViewById(R.id.ibt_menu_function);

            itemView.setOnClickListener(view -> mClickListener.open(mFiles.get(getAdapterPosition()).getId()));
            functionButton.setOnClickListener(view -> mCallback.doAnOrder(0));
        }
    }
}
