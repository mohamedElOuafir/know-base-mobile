package com.rag.knowbase.view.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.widget.EditText;
import android.widget.ImageButton;
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

    // UI
    private RecyclerView recyclerViewMessages;
    private EditText editTextMessage;
    private FloatingActionButton btnSend;
    private ImageButton btnUpload;
    private ImageButton btnMenu;
    private DrawerLayout drawerLayout;

    // Data
    private final List<Message> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private Chat chat;

    // State
    private boolean isWaitingForResponse = false;

    // File picker
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
        setupDrawer();
        setupFileLauncher();
        setupListeners();
        loadChatHistory();
    }

    private void initViews() {
        recyclerViewMessages = findViewById(R.id.recycler_view_messages);
        editTextMessage      = findViewById(R.id.edit_text_message);
        btnSend              = findViewById(R.id.btnsend);
        btnUpload            = findViewById(R.id.btnUpload);
        btnMenu              = findViewById(R.id.Menu);
        drawerLayout         = findViewById(R.id.drawer_layout);
    }

    private void setupRecyclerView() {
        messageAdapter = new MessageAdapter(messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewMessages.setLayoutManager(layoutManager);
        recyclerViewMessages.setAdapter(messageAdapter);
    }

    private void setupDrawer() {
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
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
        }
    }

    // Load existing messages from the chat object passed in intent
    private void loadChatHistory() {

        if (chat.getMessages() != null && !chat.getMessages().isEmpty()) {
            messageList.addAll(chat.getMessages());
            messageAdapter.notifyDataSetChanged();
            recyclerViewMessages.scrollToPosition(messageList.size() - 1);
        } else {
            SessionManager sessionManager = new SessionManager(this);
            UserResponseDto user = sessionManager.getUser();
            addMessageLocally("Hello how can i help you, " + user.getFirstName(), "bot");
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
                        //uploadFileToChat(uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    private void setupListeners() {
        btnSend.setOnClickListener(v -> sendUserMessage());
        btnUpload.setOnClickListener(v -> fileLauncher.launch(new String[]{"*/*"}));
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

        // Show user message immediately
        addMessageLocally(text, "user");
        editTextMessage.setText("");

        // Send to backend
        sendMessageToBackend(text);
    }

    private void sendMessageToBackend(String text) {
        SessionManager session = new SessionManager(this);
        String token = "Bearer " + session.getToken();

        MessageApi messageApi = RetrofitBackend.getMessageApi();

        MessageRequestDto request = new MessageRequestDto();
        request.setContent(text);
        request.setIdChat(chat.getIdChat());


        // Show typing indicator
        addMessageLocally("...", "bot");
        int typingIndex = messageList.size() - 1;

        messageApi.sendMessage(token, request).enqueue(new Callback<MessageDto>() {
            @Override
            public void onResponse(Call<MessageDto> call, Response<MessageDto> response) {
                // Remove typing indicator
                messageList.remove(typingIndex);
                messageAdapter.notifyItemRemoved(typingIndex);

                if (response.isSuccessful() && response.body() != null) {
                    Message botMessage = MessageMapper.convertDtoToMessageModel(response.body());
                    messageList.add(botMessage);
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    recyclerViewMessages.scrollToPosition(messageList.size() - 1);
                } else {
                    Toast.makeText(ChatbotActivity.this,
                            "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }

                isWaitingForResponse = false;
                btnSend.setEnabled(true);
            }

            @Override
            public void onFailure(Call<MessageDto> call, Throwable t) {
                // Remove typing indicator
                messageList.remove(typingIndex);
                messageAdapter.notifyItemRemoved(typingIndex);

                Toast.makeText(ChatbotActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                isWaitingForResponse = false;
                btnSend.setEnabled(true);
            }
        });
    }


    /*private void uploadFileToChat(Uri uri) {
        SessionManager session = new SessionManager(this);
        String token = session.getToken();

        String fileName = getFileName(uri);

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            byte[] bytes = inputStream.readAllBytes();
            RequestBody fileBody = RequestBody.create(bytes, MediaType.parse(getContentResolver().getType(uri)));
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", fileName, fileBody);
            RequestBody chatIdBody = RequestBody.create(String.valueOf(chat.getIdChat()), MediaType.parse("text/plain"));

            CollectionApi collectionApi = RetrofitBackend.getCollectionApi();
            collectionApi.uploadFile(token, chatIdBody, filePart).enqueue(new Callback<FileUploadedDto>() {
                        @Override
                        public void onResponse(Call<FileUploadedDto> call, Response<FileUploadedDto> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(ChatbotActivity.this, "File uploaded: " + fileName, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ChatbotActivity.this, "Upload failed: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FileUploadedDto> call, Throwable t) {
                            Toast.makeText(ChatbotActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to read file", Toast.LENGTH_SHORT).show();
        }
    }*/


    // Add a local-only message (not persisted, e.g. welcome or typing indicator)
    private void addMessageLocally(String content, String role) {
        Message message = new Message(null, content, new Date(), role, chat);
        messageList.add(message);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerViewMessages.scrollToPosition(messageList.size() - 1);
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (idx >= 0) result = cursor.getString(idx);
                }
            }
        }
        if (result == null) result = uri.getLastPathSegment();
        return result != null ? result : "Unknown file";
    }
}