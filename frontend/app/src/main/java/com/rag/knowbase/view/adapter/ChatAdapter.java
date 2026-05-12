package com.rag.knowbase.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rag.knowbase.R;
import com.rag.knowbase.model.Chat;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<Chat> chatList;
    private OnItemClickListener listener;

    // INTERFACE
    public interface OnItemClickListener {
        void onItemClick(Chat chat);
    }

    // CONSTRUCTOR
    public ChatAdapter(List<Chat> chatList, OnItemClickListener listener) {
        this.chatList = chatList;
        this.listener = listener;
    }

    // VIEWHOLDER
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView metaData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tvChatTitle);
            metaData = itemView.findViewById(R.id.tvChatMeta);
        }

        public void bind(Chat chat) {

            title.setText(chat.getName());

            SimpleDateFormat sdf =
                    new SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault());

            metaData.setText(sdf.format(chat.getCreateAt()));


            itemView.setOnClickListener(v -> {

                if (listener != null) {
                    listener.onItemClick(chat);
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_card, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.bind(chatList.get(position));
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}