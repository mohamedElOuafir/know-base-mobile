package com.rag.knowbase.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rag.knowbase.R;
import com.rag.knowbase.model.CollectionUser;

import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {

    private List<CollectionUser> collections;

    public CollectionAdapter(List<CollectionUser> collections){
        this.collections = collections;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView date;

        public ViewHolder(View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.collectionTitle);
            date = itemView.findViewById(R.id.collectionDate);
        }

        public void bind(CollectionUser collection){
            title.setText(collection.getName());
            date.setText(collection.getCreatedAt().toString());
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
