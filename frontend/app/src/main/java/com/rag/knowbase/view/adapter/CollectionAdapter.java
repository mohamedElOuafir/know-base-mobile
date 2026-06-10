package com.rag.knowbase.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rag.knowbase.R;
import com.rag.knowbase.data.dto.CollectionDetailsDto;
import com.rag.knowbase.model.User;
import com.rag.knowbase.model.UserCollection;

import java.text.SimpleDateFormat;
import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {

    private List<UserCollection> collections;
    private static OnButtonClickListener listener;

    public interface OnButtonClickListener {
        public void onButtonClick(UserCollection collectionUser);
    }

    public CollectionAdapter(List<UserCollection> collections, OnButtonClickListener listener){

        this.collections = collections;
        this.listener = listener;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView date;
        TextView chatsCount;
        TextView filesCount;
        Button button;

        public ViewHolder(View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.collectionTitle);
            date = itemView.findViewById(R.id.collectionDate);
            button = itemView.findViewById(R.id.openCollectionButton);
            chatsCount = itemView.findViewById(R.id.chatsCount);
            filesCount = itemView.findViewById(R.id.filesCount);
        }

        public void bind(UserCollection collection){
            title.setText(collection.getNameCollection());

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
            date.setText("Created at, " + sdf.format(collection.getCreatedAt()));

            chatsCount.setText(String.valueOf(collection.getChats().size()));
            filesCount.setText(String.valueOf(collection.getFileUploadeds().size()));
            button.setOnClickListener(v -> {
                if(listener != null)
                    listener.onButtonClick(collection);
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(collections.get(position));
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }



}
