package com.rag.knowbase.view.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
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
import com.rag.knowbase.viewmodel.CollectionViewModel;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CollectionPage extends AppCompatActivity {

    private CollectionViewModel viewModel;
    private RecyclerView recyclerViewCollection;
    private CollectionAdapter collectionAdapter;
    private LinearLayout emptyStateLayout;
    private LinearLayout selectedFilesContainer;
    private TextInputEditText searchCollectionInput;
    private Button newCollection;
    private Button addNewCollectionEmptyState;
    private Button btnCreateConfirm;
    private CircularProgressIndicator createCollectionAnimation;
    private BottomSheetDialog dialog;
    private ActivityResultLauncher<String[]> multiFileLauncher;

    // Ajoutez ce flag
    private boolean isRecreatingDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_collection_page);

        viewModel = new ViewModelProvider(this).get(CollectionViewModel.class);

        // RESTAURER L'ÉTAT APRÈS ROTATION ÉCRAN
        if (savedInstanceState != null) {
            viewModel.isFilePicking = savedInstanceState.getBoolean("isFilePicking", false);
            if (viewModel.isFilePicking) {
                // Programme la réouverture après la création complète
                new Handler().postDelayed(() -> showDialogNewCollection(), 500);
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.collection_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        recyclerViewCollection = findViewById(R.id.rvCollections);
        recyclerViewCollection.setLayoutManager(new LinearLayoutManager(this));

        collectionAdapter = new CollectionAdapter(viewModel.collections, this::consultCollection);
        recyclerViewCollection.setAdapter(collectionAdapter);

        searchCollectionInput = findViewById(R.id.searchCollectionInput);
        searchCollectionInput.addTextChangedListener(new TextWatcher() {
            @Override public void afterTextChanged(Editable s) {}
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCollections(s.toString().trim());
            }
        });

        SessionManager session = new SessionManager(this);

        if (viewModel.collections.isEmpty()) {
            loadCollections("Bearer " + session.getUser().getToken());
        } else {
            updateEmptyState();
        }

        // CONTRAT POUR SÉLECTIONNER PLUSIEURS FICHIERS
        multiFileLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenMultipleDocuments(),
                uris -> {
                    if (uris != null && !uris.isEmpty()) {
                        for (Uri uri : uris) {
                            try {
                                getContentResolver().takePersistableUriPermission(
                                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                                );
                                if (!viewModel.selectedFileUris.contains(uri)) {
                                    viewModel.selectedFileUris.add(uri);
                                }
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    // Ne pas réouvrir ici, onResume le fera
                }
        );

        newCollection = findViewById(R.id.NewCollectionButton);
        addNewCollectionEmptyState = findViewById(R.id.emptyStateNewButton);
        newCollection.setOnClickListener(v -> showDialogNewCollection());
        addNewCollectionEmptyState.setOnClickListener(v -> showDialogNewCollection());

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isFilePicking", viewModel.isFilePicking);
        outState.putString("dialogName", viewModel.dialogName);
        outState.putString("dialogDescription", viewModel.dialogDescription);
        outState.putString("dialogChatName", viewModel.dialogChatName);

        if (!viewModel.selectedFileUris.isEmpty()) {
            ArrayList<String> uris = new ArrayList<>();
            for (Uri uri : viewModel.selectedFileUris) {
                uris.add(uri.toString());
            }
            outState.putStringArrayList("selectedFileUris", uris);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewModel.dialogName = savedInstanceState.getString("dialogName", "");
        viewModel.dialogDescription = savedInstanceState.getString("dialogDescription", "");
        viewModel.dialogChatName = savedInstanceState.getString("dialogChatName", "");

        ArrayList<String> uris = savedInstanceState.getStringArrayList("selectedFileUris");
        if (uris != null) {
            viewModel.selectedFileUris.clear();
            for (String uri : uris) {
                viewModel.selectedFileUris.add(Uri.parse(uri));
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Réouvrir la dialog si on revient du file picker
        if (viewModel.isFilePicking) {
            viewModel.isFilePicking = false;
            // Utiliser un délai plus long pour éviter les conflits
            new Handler().postDelayed(() -> {
                if (!isFinishing() && !isDestroyed()) {
                    showDialogNewCollection();
                }
            }, 300);
        }
    }

    public void showDialogNewCollection() {
        // Éviter les appels multiples
        if (isRecreatingDialog) return;
        isRecreatingDialog = true;

        // Nettoyer l'ancienne dialog si elle existe
        if (dialog != null && dialog.isShowing()) {
            dialog.setOnDismissListener(null);
            dialog.dismiss();
        }

        dialog = new BottomSheetDialog(this);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // Empêcher la fermeture au clic extérieur pendant le chargement
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        View view = getLayoutInflater().inflate(R.layout.dialog_new_collection, null);

        EditText editName = view.findViewById(R.id.editCollectionName);
        EditText editDescription = view.findViewById(R.id.editDescription);
        EditText editFirstChatName = view.findViewById(R.id.editFirstChatName);
        LinearLayout btnSelectFiles = view.findViewById(R.id.btnSelectFiles);
        btnCreateConfirm = view.findViewById(R.id.btnCreateConfirm);
        selectedFilesContainer = view.findViewById(R.id.selectedFilesContainer);
        createCollectionAnimation = view.findViewById(R.id.createCollectionAnimation);

        // Restaurer les valeurs depuis le ViewModel
        editName.setText(viewModel.dialogName);
        editDescription.setText(viewModel.dialogDescription);
        editFirstChatName.setText(viewModel.dialogChatName);

        if (!viewModel.dialogName.isEmpty()) {
            editName.setSelection(editName.getText().length());
        }

        refreshFileList();

        // Sauvegarder les changements en temps réel
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.dialogName = editName.getText().toString().trim();
                viewModel.dialogDescription = editDescription.getText().toString().trim();
                viewModel.dialogChatName = editFirstChatName.getText().toString().trim();
            }
        };

        editName.addTextChangedListener(watcher);
        editDescription.addTextChangedListener(watcher);
        editFirstChatName.addTextChangedListener(watcher);

        btnSelectFiles.setOnClickListener(v -> {
            // Sauvegarder immédiatement
            viewModel.dialogName = editName.getText().toString().trim();
            viewModel.dialogDescription = editDescription.getText().toString().trim();
            viewModel.dialogChatName = editFirstChatName.getText().toString().trim();

            viewModel.isFilePicking = true;
            dialog.dismiss(); // Fermer proprement la dialog avant d'ouvrir le picker
            multiFileLauncher.launch(new String[]{"*/*"});
        });

        // Cleanup quand l'utilisateur ferme la dialog
        dialog.setOnDismissListener(d -> {
            if (!viewModel.isFilePicking) {
                viewModel.dialogName = "";
                viewModel.dialogDescription = "";
                viewModel.dialogChatName = "";
                viewModel.selectedFileUris.clear();
            }
            isRecreatingDialog = false;
        });

        btnCreateConfirm.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String description = editDescription.getText().toString().trim();
            String chatName = editFirstChatName.getText().toString().trim();

            if (name.isEmpty()) {
                editName.setError("Name is required");
                return;
            }

            showLoading();

            SessionManager session = new SessionManager(this);
            String token = "Bearer " + session.getToken();

            RequestBody nameBody = RequestBody.create(name, MediaType.parse("text/plain"));
            RequestBody descBody = RequestBody.create(description, MediaType.parse("text/plain"));
            RequestBody chatNameBody = RequestBody.create(chatName, MediaType.parse("text/plain"));

            List<MultipartBody.Part> fileParts = new ArrayList<>();
            for (Uri uri : viewModel.selectedFileUris) {
                String fileName = getFileName(uri);
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    byte[] bytes = inputStream.readAllBytes();
                    RequestBody fileBody = RequestBody.create(
                            bytes, MediaType.parse(getContentResolver().getType(uri))
                    );
                    fileParts.add(MultipartBody.Part.createFormData("files", fileName, fileBody));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            CollectionApi collectionApi = RetrofitBackend.getCollectionApi();
            collectionApi.addNewCollection(token, nameBody, descBody, chatNameBody, fileParts)
                    .enqueue(new Callback<CollectionDetailsDto>() {
                        @Override
                        public void onResponse(Call<CollectionDetailsDto> call,
                                               Response<CollectionDetailsDto> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                UserCollection userCollection =
                                        CollectionMapper.convertDtoToCollectionModel(response.body());

                                viewModel.collections.add(userCollection);
                                collectionAdapter.notifyItemInserted(viewModel.collections.size() - 1);
                                viewModel.allCollections.add(userCollection);
                                updateEmptyState();

                                boolean hadFiles = !userCollection.getFileUploadeds().isEmpty();

                                // Cleanup
                                viewModel.dialogName = "";
                                viewModel.dialogDescription = "";
                                viewModel.dialogChatName = "";
                                viewModel.selectedFileUris.clear();
                                viewModel.isFilePicking = false;

                                if (dialog != null) {
                                    dialog.setOnDismissListener(null);
                                    dialog.dismiss();
                                }

                                if (!userCollection.getChats().isEmpty()) {
                                    Chat firstChat = userCollection.getChats().get(0);
                                    Intent intent = new Intent(CollectionPage.this, ChatbotActivity.class);
                                    intent.putExtra("chat", firstChat);
                                    intent.putExtra("hasFiles", hadFiles);
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(CollectionPage.this,
                                        "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                            hideLoading();
                        }

                        @Override
                        public void onFailure(Call<CollectionDetailsDto> call, Throwable t) {
                            Toast.makeText(CollectionPage.this,
                                    "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            hideLoading();
                        }
                    });
        });

        dialog.setOnShowListener(d -> isRecreatingDialog = false);
        dialog.setContentView(view);
        dialog.show();
    }


    private void refreshFileList() {
        if (selectedFilesContainer == null) return;
        selectedFilesContainer.removeAllViews();

        if (viewModel.selectedFileUris.isEmpty()) {
            selectedFilesContainer.setVisibility(View.GONE);
            return;
        }

        selectedFilesContainer.setVisibility(View.VISIBLE);

        for (int i = 0; i < viewModel.selectedFileUris.size(); i++) {
            Uri uri = viewModel.selectedFileUris.get(i);
            String fileName = getFileName(uri);
            final int index = i;

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            rowParams.setMargins(0, 8, 0, 8);
            row.setLayoutParams(rowParams);

            TextView tvName = new TextView(this);
            tvName.setText(fileName);
            tvName.setTextColor(0xFF4B5563);
            tvName.setTextSize(13);
            tvName.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            ));

            TextView btnRemove = new TextView(this);
            btnRemove.setText("✕");
            btnRemove.setTextColor(0xFFEF4444);
            btnRemove.setTextSize(16);
            btnRemove.setPadding(16, 0, 0, 0);
            btnRemove.setOnClickListener(v -> {
                viewModel.selectedFileUris.remove(index);
                refreshFileList();
            });

            row.addView(tvName);
            row.addView(btnRemove);
            selectedFilesContainer.addView(row);
        }
    }

    private void updateEmptyState() {
        if (viewModel.collections.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            recyclerViewCollection.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            recyclerViewCollection.setVisibility(View.VISIBLE);
        }
    }

    private void filterCollections(String query) {
        viewModel.collections.clear();
        if (query.isEmpty()) {
            viewModel.collections.addAll(viewModel.allCollections);
        } else {
            String lower = query.toLowerCase();
            for (UserCollection c : viewModel.allCollections) {
                if (c.getNameCollection().toLowerCase().contains(lower)) {
                    viewModel.collections.add(c);
                }
            }
        }
        collectionAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (android.database.Cursor cursor = getContentResolver().query(
                    uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(
                            android.provider.OpenableColumns.DISPLAY_NAME);
                    if (idx >= 0) result = cursor.getString(idx);
                }
            }
        }
        if (result == null) result = uri.getLastPathSegment();
        return result != null ? result : "Unknown file";
    }

    public void loadCollections(String token) {
        CollectionApi collectionApi = RetrofitBackend.getCollectionApi();
        collectionApi.getUserCollection(token).enqueue(new Callback<List<CollectionDetailsDto>>() {
            @Override
            public void onResponse(Call<List<CollectionDetailsDto>> call,
                                   Response<List<CollectionDetailsDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    viewModel.collections.clear();
                    viewModel.collections.addAll(
                            CollectionMapper.convertDtoToCollectionListModel(response.body()));
                    viewModel.allCollections.clear();
                    viewModel.allCollections.addAll(viewModel.collections);
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

    private void showLoading() {
        btnCreateConfirm.setEnabled(false);
        btnCreateConfirm.setText("");
        createCollectionAnimation.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        btnCreateConfirm.setEnabled(true);
        btnCreateConfirm.setText("Create Collection");
        createCollectionAnimation.setVisibility(View.GONE);
    }

    public void consultCollection(UserCollection collection) {
        Intent intent = new Intent(this, ConsultCollection.class);
        intent.putExtra("collection", collection);
        startActivity(intent);
    }
}