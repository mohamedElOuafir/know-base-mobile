package com.rag.knowbase.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rag.knowbase.R;
import com.rag.knowbase.model.FileUploaded;


import java.util.List;

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
            String meta = uploadedFile.getSize().toString() + " · " + uploadedFile.getUploadedAt().toString();
            metaData.setText(meta);
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
