package com.rag.knowbase.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rag.knowbase.R;
import com.rag.knowbase.model.Chat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Chat> chatList;

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;

    public ChatAdapter(List<Chat> chatList) {
        this.chatList = chatList;
    }

    @Override
    public int getItemViewType(int position) {
        if (chatList.get(position).getIsUser()) {
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_BOT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //ici j'ai crèe la vue pour chaque message en fonction du type de message
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_bot, parent, false);
            return new BotViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chat chat = chatList.get(position);

        // ici je remplis la vue avec les données du message
        if (holder.getItemViewType() == VIEW_TYPE_USER) {
            UserViewHolder userHolder = (UserViewHolder) holder;
            userHolder.textMessageUser.setText(chat.getMessage());
        } else {
            BotViewHolder botHolder = (BotViewHolder) holder;
            botHolder.textMessageBot.setText(chat.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    //pour éviter de chercher les vues à chaque fois
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textMessageUser;
        public UserViewHolder(View itemView) {
            super(itemView);
            textMessageUser = itemView.findViewById(R.id.text_message_user);
        }
    }

    public static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView textMessageBot;

        public BotViewHolder(View itemView) {
            super(itemView);
            textMessageBot = itemView.findViewById(R.id.text_message_bot);
        }
    }
}
