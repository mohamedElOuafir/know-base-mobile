package com.rag.knowbase.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.rag.knowbase.R;
import com.rag.knowbase.model.Message;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messageList;

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getSenderRole().equals("user")) {
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_BOT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

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
        Message message = messageList.get(position);


        if (holder.getItemViewType() == VIEW_TYPE_USER) {
            UserViewHolder userHolder = (UserViewHolder) holder;
            userHolder.textMessageUser.setText(message.getContent());
        } else {
            BotViewHolder botHolder = (BotViewHolder) holder;
            botHolder.textMessageBot.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }



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