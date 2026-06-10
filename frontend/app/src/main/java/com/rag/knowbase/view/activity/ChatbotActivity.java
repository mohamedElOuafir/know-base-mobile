package com.rag.knowbase.view.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rag.knowbase.R;
import com.rag.knowbase.data.api.CollectionApi;
import com.rag.knowbase.data.api.MessageApi;
import com.rag.knowbase.data.dto.FileUploadedDto;
import com.rag.knowbase.data.dto.MessageDto;
import com.rag.knowbase.data.dto.MessageRequestDto;
import com.rag.knowbase.data.dto.UserResponseDto;
import com.rag.knowbase.data.retrofit.RetrofitBackend;
import com.rag.knowbase.mapper.MessageMapper;
import com.rag.knowbase.model.User;
import com.rag.knowbase.session.SessionManager;
import com.rag.knowbase.view.adapter.MessageAdapter;
import com.rag.knowbase.model.Chat;
import com.rag.knowbase.model.Message;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private EditText editTextMessage;
    private FloatingActionButton btnSend;
    private TextView tvChatName;
    private ImageButton btnBack;

    private final List<Message> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private Chat chat;

    private boolean isWaitingForResponse = false;

    private ActivityResultLauncher<String[]> fileLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        initViews();
        setupInsets();
        loadIntentData();
        setupRecyclerView();
        setupFileLauncher();
        setupListeners();
        loadChatHistory();
        showProcessingBanner();
    }

    private void initViews() {
        recyclerViewMessages = findViewById(R.id.recycler_view_messages);
        editTextMessage = findViewById(R.id.edit_text_message);
        btnSend = findViewById(R.id.btnsend);
        tvChatName = findViewById(R.id.tvChatName);
        btnBack = findViewById(R.id.btnBackChat);
    }

    private void setupRecyclerView() {
        messageAdapter = new MessageAdapter(messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewMessages.setLayoutManager(layoutManager);
        recyclerViewMessages.setAdapter(messageAdapter);
    }

    private void setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chat_page_layout), (v, insets) -> {
            Insets systemInsets = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime()
            );
            v.setPadding(systemInsets.left, systemInsets.top, systemInsets.right, systemInsets.bottom);

            boolean keyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime());
            if (keyboardVisible && !messageList.isEmpty()) {
                recyclerViewMessages.scrollToPosition(messageList.size() - 1);
            }
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void loadIntentData() {
        chat = (Chat) getIntent().getSerializableExtra("chat");
        if (chat == null) {
            Toast.makeText(this, "Chat introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        tvChatName.setText(chat.getName());
    }

    private void loadChatHistory() {
        if (chat.getMessages() != null && !chat.getMessages().isEmpty()) {
            messageList.addAll(chat.getMessages());
            messageAdapter.notifyDataSetChanged();
            recyclerViewMessages.scrollToPosition(messageList.size() - 1);
        } else {
            SessionManager sessionManager = new SessionManager(this);
            UserResponseDto user = sessionManager.getUser();
            addMessageLocally("Hello, how can I help you " + user.getFirstName() + "?", "bot");
        }
    }

    private void setupFileLauncher() {
        fileLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri == null) return;
                    try {
                        getContentResolver().takePersistableUriPermission(
                                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    private void setupListeners() {
        btnSend.setOnClickListener(v -> sendUserMessage());
        btnBack.setOnClickListener(v -> finish());
    }

    private void sendUserMessage() {
        String text = editTextMessage.getText().toString().trim();

        if (isWaitingForResponse) {
            Toast.makeText(this, "Waiting...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (text.isEmpty()) return;

        isWaitingForResponse = true;
        btnSend.setEnabled(false);

        addMessageLocally(text, "user");
        editTextMessage.setText("");

        sendMessageToBackend(text);
    }

    private void sendMessageToBackend(String text) {
        SessionManager session = new SessionManager(this);
        String token = "Bearer " + session.getToken();

        MessageApi messageApi = RetrofitBackend.getMessageApi();

        MessageRequestDto request = new MessageRequestDto();
        request.setContent(text);
        request.setIdChat(chat.getIdChat());

        addMessageLocally("...", "bot");
        int typingIndex = messageList.size() - 1;

        messageApi.sendMessage(token, request).enqueue(new Callback<MessageDto>() {
            @Override
            public void onResponse(Call<MessageDto> call, Response<MessageDto> response) {
                messageList.remove(typingIndex);
                messageAdapter.notifyItemRemoved(typingIndex);

                if (response.isSuccessful() && response.body() != null) {
                    Message botMessage = MessageMapper.convertDtoToMessageModel(response.body());
                    messageList.add(botMessage);
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    recyclerViewMessages.scrollToPosition(messageList.size() - 1);
                } else {
                    Toast.makeText(ChatbotActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }

                isWaitingForResponse = false;
                btnSend.setEnabled(true);
            }

            @Override
            public void onFailure(Call<MessageDto> call, Throwable t) {
                messageList.remove(typingIndex);
                messageAdapter.notifyItemRemoved(typingIndex);

                Toast.makeText(ChatbotActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                isWaitingForResponse = false;
                btnSend.setEnabled(true);
            }
        });
    }

    private void showProcessingBanner() {
        if (chat == null) return;

        boolean hasFiles = getIntent().getBooleanExtra("hasFiles", false);
        if (!hasFiles) return;

        View banner = findViewById(R.id.processingBanner);
        if (banner != null) {
            banner.setAlpha(1f);
            banner.setVisibility(View.VISIBLE);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (!isFinishing()) {
                    banner.animate().alpha(0f).setDuration(500).withEndAction(() ->
                            banner.setVisibility(View.GONE)
                    ).start();
                }
            }, 6000);
        }
    }

    private void addMessageLocally(String content, String role) {
        Message message = new Message(null, content, new Date(), role, chat);
        messageList.add(message);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerViewMessages.scrollToPosition(messageList.size() - 1);
    }
}