package com.rag.knowbase.view.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.rag.knowbase.R;
import com.rag.knowbase.data.api.ChatApi;
import com.rag.knowbase.data.api.CollectionApi;
import com.rag.knowbase.data.api.FileUploadedApi;
import com.rag.knowbase.data.dto.ChatDto;
import com.rag.knowbase.data.dto.CollectionDetailsDto;
import com.rag.knowbase.data.dto.FileUploadedDto;
import com.rag.knowbase.data.retrofit.RetrofitBackend;
import com.rag.knowbase.mapper.ChatMapper;
import com.rag.knowbase.mapper.FileUploadedMapper;
import com.rag.knowbase.session.SessionManager;
import com.rag.knowbase.view.adapter.ChatAdapter;
import com.rag.knowbase.view.adapter.UploadedFileAdapter;
import com.rag.knowbase.model.Chat;
import com.rag.knowbase.model.FileUploaded;
import com.rag.knowbase.model.Message;
import com.rag.knowbase.model.UserCollection;


import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsultCollection extends AppCompatActivity {

    private TextView title;
    private TextView chatStats;
    private TextView fileStats;
    private TextView date;
    private UserCollection collection;

    private List<Chat> chatList = new ArrayList<>();
    private List<FileUploaded> uploadedFilesList = new ArrayList<>();

    private ChatAdapter chatAdapter;
    private UploadedFileAdapter uploadedFileAdapter;

    private ActivityResultLauncher<String[]> fileLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_consult_collection);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.consult_collection), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        collection = (UserCollection) getIntent().getSerializableExtra("collection");
        if (collection == null) {
            finish();
            return;
        }

        initViews();
        fillHeader();
        setupRecyclerViews();
        setupButtons();
        setupFileLauncher();
    }

    private void initViews() {
        title    = findViewById(R.id.tvCollectionName);
        chatStats = findViewById(R.id.tvStatChats);
        fileStats = findViewById(R.id.tvStatFiles);
        date     = findViewById(R.id.tvStatDate);
    }

    private void fillHeader() {
        title.setText(collection.getNameCollection());
        chatStats.setText(String.valueOf(collection.getChats().size()));
        fileStats.setText(String.valueOf(collection.getFileUploadeds().size()));

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
        date.setText(sdf.format(collection.getCreatedAt()));
    }

    private void setupRecyclerViews() {

        chatList.addAll(collection.getChats());
        uploadedFilesList.addAll(collection.getFileUploadeds());


        RecyclerView recyclerViewChat = findViewById(R.id.rvChats);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.setNestedScrollingEnabled(false);
        chatAdapter = new ChatAdapter(chatList, chat -> {
            openChat(chat);
        });
        recyclerViewChat.setAdapter(chatAdapter);


        RecyclerView recyclerViewFile = findViewById(R.id.rvFiles);
        recyclerViewFile.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFile.setNestedScrollingEnabled(false);
        uploadedFileAdapter = new UploadedFileAdapter(uploadedFilesList);
        recyclerViewFile.setAdapter(uploadedFileAdapter);
    }

    private void setupButtons() {

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());


        findViewById(R.id.btnNewChat).setOnClickListener(v -> createNewChat());


        findViewById(R.id.btnUploadFile).setOnClickListener(v ->
                fileLauncher.launch(new String[]{"*/*"})
        );
    }

    private void setupFileLauncher() {
        fileLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(), uri -> {
                    if (uri == null) return;

                    try {
                        getContentResolver().takePersistableUriPermission(
                                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );

                        SessionManager session = new SessionManager(this);
                        String token = "Bearer " + session.getToken();

                        String fileName = getFileName(uri);
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        byte[] bytes = inputStream.readAllBytes();
                        RequestBody fileBody = RequestBody.create(bytes, MediaType.parse(getContentResolver().getType(uri)));
                        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", fileName, fileBody);

                        uploadFileToCollection(token, filePart);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    private void createNewChat() {

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_new_chat, null);

        EditText editChatName = view.findViewById(R.id.editChatName);
        Button btnConfirm = view.findViewById(R.id.btnConfirmChat);
        Button btnCancel = view.findViewById(R.id.btnCancelChat);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String chatName = editChatName.getText().toString().trim();
            if (chatName.isEmpty()) {
                editChatName.setError("Please enter a chat name");
                return;
            }

            dialog.dismiss();

            SessionManager session = new SessionManager(this);
            String token = "Bearer " + session.getToken();

            ChatApi chatApi = RetrofitBackend.getChatApi();
            chatApi.createChat(token, collection.getIdCollection(), chatName).enqueue(new Callback<ChatDto>() {
                        @Override
                        public void onResponse(Call<ChatDto> call, Response<ChatDto> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Chat newChat = ChatMapper.convertDtoToChatModel(response.body());
                                chatList.add(newChat);
                                chatAdapter.notifyItemInserted(chatList.size() - 1);
                                chatStats.setText(String.valueOf(chatList.size()));
                                openChat(newChat);
                            } else {
                                Toast.makeText(ConsultCollection.this, "Failed to create chat: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ChatDto> call, Throwable t) {
                            Toast.makeText(ConsultCollection.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        dialog.setContentView(view);
        dialog.show();
    }

    private void uploadFileToCollection(String token, MultipartBody.Part file) {


        FileUploadedApi fileUploadedApi = RetrofitBackend.getFileUploadedApi();
        fileUploadedApi.uploadFile(token, collection.getIdCollection(), file).enqueue(new Callback<FileUploadedDto>() {
                    @Override
                    public void onResponse(Call<FileUploadedDto> call, Response<FileUploadedDto> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            FileUploaded newFile = FileUploadedMapper.convertDtoToFileUploadedModel(response.body());
                            int insertStart = uploadedFilesList.size();
                            uploadedFilesList.add(newFile);
                            uploadedFileAdapter.notifyItemInserted(uploadedFilesList.size() - 1);

                            // Mettre à jour le stat header
                            fileStats.setText(String.valueOf(uploadedFilesList.size()));

                            Toast.makeText(ConsultCollection.this, 1 + " file(s) uploaded!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ConsultCollection.this, "Upload failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<FileUploadedDto> call, Throwable t) {
                        Toast.makeText(ConsultCollection.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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

    public void openChat(Chat chat) {
        Intent intent = new Intent(this, ChatbotActivity.class);
        intent.putExtra("chat", chat);
        startActivity(intent);
    }
}