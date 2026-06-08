package com.rag.knowbase.view.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rag.knowbase.R;
import com.rag.knowbase.model.FileUploaded;


import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class UploadedFileAdapter extends RecyclerView.Adapter<UploadedFileAdapter.ViewHolder> {

    private List<FileUploaded> uploadedFiles;

    public UploadedFileAdapter(List<FileUploaded> uploadedFiles){
        this.uploadedFiles = uploadedFiles;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView metaData;

        public ViewHolder(View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.tvFileName);
            metaData = itemView.findViewById(R.id.tvFileMeta);
        }

        public void bind(FileUploaded uploadedFile){
            name.setText(uploadedFile.getFileName());
            Float sizeMegaBit = (float) uploadedFile.getSize() / (1000 * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault());
            String fileSize = String.format("%.2f", sizeMegaBit);
            String date = sdf.format(uploadedFile.getUploadedAt());
            metaData.setText(String.valueOf( fileSize + " MB · " + date));
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadedFileAdapter.ViewHolder holder, int position) {
        holder.bind(uploadedFiles.get(position));
    }

    @Override
    public int getItemCount() {
        return uploadedFiles.size();
    }
}
