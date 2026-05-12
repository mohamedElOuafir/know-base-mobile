package com.rag.knowbase.view.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
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
import com.rag.knowbase.data.api.CollectionApi;
import com.rag.knowbase.data.api.DashboardApi;
import com.rag.knowbase.data.dto.ChatDto;
import com.rag.knowbase.data.dto.CollectionDetailsDto;
import com.rag.knowbase.data.dto.DashboardStatsDto;
import com.rag.knowbase.data.dto.FileUploadedDto;
import com.rag.knowbase.data.dto.MessageDto;
import com.rag.knowbase.data.dto.UserResponseDto;
import com.rag.knowbase.data.retrofit.RetrofitBackend;
import com.rag.knowbase.mapper.ChatMapper;
import com.rag.knowbase.mapper.CollectionMapper;
import com.rag.knowbase.mapper.FileUploadedMapper;
import com.rag.knowbase.model.Chat;
import com.rag.knowbase.model.FileUploaded;
import com.rag.knowbase.model.Message;
import com.rag.knowbase.session.SessionManager;
import com.rag.knowbase.view.adapter.CollectionAdapter;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rag.knowbase.model.UserCollection;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CollectionPage extends AppCompatActivity {

    private List<UserCollection> collections = new ArrayList<>();
    private List<Uri> selectedFileUris = new ArrayList<>();

    private RecyclerView recyclerViewCollection;
    private CollectionAdapter collectionAdapter;
    private LinearLayout emptyStateLayout;

    private LinearLayout selectedFilesContainer;
    private ActivityResultLauncher<String[]> multiFileLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_collection_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.collection_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        recyclerViewCollection = findViewById(R.id.rvCollections);
        recyclerViewCollection.setLayoutManager(new LinearLayoutManager(this));

        collectionAdapter = new CollectionAdapter(collections, collection -> {
            consultCollection(collection);
        });
        recyclerViewCollection.setAdapter(collectionAdapter);

        SessionManager session = new SessionManager(this);
        UserResponseDto user = session.getUser();
        loadCollections("Bearer " + user.getToken());

        //pour sélection multiple
        multiFileLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenMultipleDocuments(),
                uris -> {
                    if (uris != null && !uris.isEmpty()) {
                        for (Uri uri : uris) {
                            // Persistance de la permission de lecture
                            getContentResolver().takePersistableUriPermission(
                                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );
                            if (!selectedFileUris.contains(uri)) {
                                selectedFileUris.add(uri);
                            }
                        }
                        refreshFileList(); // Rafraîchir la liste dans le dialog
                    }
                }
        );

        Button newCollection = findViewById(R.id.NewCollectionButton);
        newCollection.setOnClickListener(v -> showDialogNewCollection());

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bar);
        bottomNavigationView.setSelectedItemId(R.id.nav_collections);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_collections) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void updateEmptyState() {
        if (collections.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerViewCollection.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            recyclerViewCollection.setVisibility(View.VISIBLE);
        }
    }

    //Rafraîchit la liste des fichiers affichée dans le dialog
    private void refreshFileList() {
        if (selectedFilesContainer == null) return;

        selectedFilesContainer.removeAllViews();

        if (selectedFileUris.isEmpty()) {
            selectedFilesContainer.setVisibility(View.GONE);
            return;
        }

        selectedFilesContainer.setVisibility(View.VISIBLE);

        for (int i = 0; i < selectedFileUris.size(); i++) {
            Uri uri = selectedFileUris.get(i);
            String fileName = getFileName(uri);
            int index = i;

            // Ligne fichier
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            rowParams.setMargins(0, 8, 0, 8);
            row.setLayoutParams(rowParams);

            // Nom du fichier
            TextView tvName = new TextView(this);
            tvName.setText(fileName);
            tvName.setTextColor(0xFF4B5563);
            tvName.setTextSize(13);
            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            );
            tvName.setLayoutParams(tvParams);

            // Bouton supprimer
            TextView btnRemove = new TextView(this);
            btnRemove.setText("✕");
            btnRemove.setTextColor(0xFFEF4444);
            btnRemove.setTextSize(16);
            btnRemove.setPadding(16, 0, 0, 0);
            btnRemove.setOnClickListener(v -> {
                selectedFileUris.remove(index);
                refreshFileList();
            });

            row.addView(tvName);
            row.addView(btnRemove);
            selectedFilesContainer.addView(row);
        }
    }

    // Récupère le nom lisible d'un fichier depuis son URI
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(
                    uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (idx >= 0) result = cursor.getString(idx);
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result != null ? result : "Unknown file";
    }

    public void showDialogNewCollection() {
        selectedFileUris.clear();

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        View view = getLayoutInflater().inflate(R.layout.dialog_new_collection, null);

        EditText editName = view.findViewById(R.id.editCollectionName);
        EditText editDescription = view.findViewById(R.id.editDescription);
        EditText editFirstChatName = view.findViewById(R.id.editFirstChatName);
        LinearLayout btnSelectFiles = view.findViewById(R.id.btnSelectFiles);
        Button btnCreateConfirm = view.findViewById(R.id.btnCreateConfirm);
        selectedFilesContainer = view.findViewById(R.id.selectedFilesContainer);

        btnSelectFiles.setOnClickListener(v ->
                multiFileLauncher.launch(new String[]{"*/*"})
        );

        btnCreateConfirm.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String description = editDescription.getText().toString().trim();
            String chatName = editFirstChatName.getText().toString().trim();

            if (name.isEmpty()) {
                editName.setError("Name is required");
                return;
            }

            SessionManager session = new SessionManager(this);
            String token = "Bearer " + session.getToken();

            //Construire les parts multipart
            RequestBody nameBody = RequestBody.create(name, MediaType.parse("text/plain"));
            RequestBody descBody = RequestBody.create(description, MediaType.parse("text/plain"));
            RequestBody chatNameBody = RequestBody.create(chatName, MediaType.parse("text/plain"));

            List<MultipartBody.Part> fileParts = new ArrayList<>();
            for (Uri uri : selectedFileUris) {
                String fileName = getFileName(uri);
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    byte[] bytes = inputStream.readAllBytes();
                    RequestBody fileBody = RequestBody.create(bytes, MediaType.parse(
                            getContentResolver().getType(uri)
                    ));
                    fileParts.add(MultipartBody.Part.createFormData("files", fileName, fileBody));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            CollectionApi collectionApi = RetrofitBackend.getCollectionApi();
            collectionApi.addNewCollection(token, nameBody, descBody, chatNameBody, fileParts)
                    .enqueue(new Callback<CollectionDetailsDto>() {
                        @Override
                        public void onResponse(Call<CollectionDetailsDto> call, Response<CollectionDetailsDto> response) {

                            if (response.isSuccessful() && response.body() != null) {

                                collections.add(CollectionMapper.convertDtoToCollectionModel(response.body()));
                                collectionAdapter.notifyItemInserted(collections.size() - 1);
                                updateEmptyState();
                                Toast.makeText(CollectionPage.this, "Collection '" + name + "' created!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(CollectionPage.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<CollectionDetailsDto> call, Throwable t) {
                            Toast.makeText(CollectionPage.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        dialog.setContentView(view);
        dialog.show();
    }

    public void loadCollections(String token) {
        CollectionApi collectionApi = RetrofitBackend.getCollectionApi();
        collectionApi.getUserCollection(token).enqueue(new Callback<List<CollectionDetailsDto>>() {
            @Override
            public void onResponse(Call<List<CollectionDetailsDto>> call, Response<List<CollectionDetailsDto>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    collections.clear();
                    collections.addAll(CollectionMapper.convertDtoToCollectionListModel(response.body()));
                    collectionAdapter.notifyDataSetChanged();
                }
                updateEmptyState();
            }

            @Override
            public void onFailure(Call<List<CollectionDetailsDto>> call, Throwable throwable) {
                startActivity(new Intent(CollectionPage.this, HomeActivity.class));
                finish();
            }
        });
    }





    public void consultCollection(UserCollection collection) {
        Intent intent = new Intent(this, ConsultCollection.class);
        intent.putExtra("collection", collection);
        startActivity(intent);
    }
}