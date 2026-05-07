package com.rag.knowbase;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.rag.knowbase.adapter.CollectionAdapter;
import com.rag.knowbase.model.CollectionUser;


import java.sql.Date;
import java.util.List;

import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;



public class CollectionPage extends AppCompatActivity {

    final List<CollectionUser> collections = List.of(
            new CollectionUser(1L, "Sales reports", Date.valueOf("2026-04-18")),
            new CollectionUser(1L, "Sales reports", Date.valueOf("2026-04-18")),
            new CollectionUser(1L, "Sales reports", Date.valueOf("2026-04-18")),
            new CollectionUser(1L, "Sales reports", Date.valueOf("2026-04-18")),
            new CollectionUser(1L, "Sales reports", Date.valueOf("2026-04-18")),
            new CollectionUser(1L, "Sales reports", Date.valueOf("2026-04-18")),
            new CollectionUser(1L, "Sales reports", Date.valueOf("2026-04-18"))
    );
    private Button newCollection;

    private ActivityResultLauncher<String> galleryLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_collection_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.collection_page), (v, insets) -> {

            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            return insets;
        });
        RecyclerView recyclerViewCollection = findViewById(R.id.rvCollections);
        recyclerViewCollection.setLayoutManager(new LinearLayoutManager(this));
        CollectionAdapter collectionAdapter = new CollectionAdapter(collections);
        recyclerViewCollection.setAdapter(collectionAdapter);

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            // L'utilisateur a choisi un fichier
                            Toast.makeText(CollectionPage.this, "Fichier sélectionné ! ", Toast.LENGTH_SHORT).show();
                            // nous pouvons utiliser 'result' (qui est l'URI du fichier) ici plus tard
                        } else {
                            // L'utilisateur a annulé
                            Toast.makeText(CollectionPage.this, "Aucun fichier sélectionné", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Bouton Nouvelle Collection
        newCollection = findViewById(R.id.NewCollectionButton);
        newCollection.setOnClickListener(v -> {
            showDialogNewCollection();
        });

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
            }else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

    }

    public void showDialogNewCollection() {
        // Création du dialogue
        BottomSheetDialog dialog = new BottomSheetDialog(this);

        // AJOUTE CETTE LIGNE : Elle force le dialogue à se redimensionner au-dessus du clavier
        dialog.getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        View view = getLayoutInflater().inflate(R.layout.dialog_new_collection, null);

        // Récupération des éléments du formulaire
        EditText editName = view.findViewById(R.id.editCollectionName);
        //EditText editDescription = view.findViewById(R.id.editDescription);
        LinearLayout btnSelectFiles = view.findViewById(R.id.btnSelectFiles);
        Button btnCreateConfirm = view.findViewById(R.id.btnCreateConfirm);



        btnSelectFiles.setOnClickListener(v -> {
            galleryLauncher.launch("*/*");
            Toast.makeText(this, "File picker opened", Toast.LENGTH_SHORT).show();
        });
        // Gestion du bouton de création
        btnCreateConfirm.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();

            if (name.isEmpty()) {
                editName.setError("Name is required");
            } else {
                // Logique pour sauvegarder la collection
                Toast.makeText(this, "Collection '" + name + "' created!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), ChatbotActivity.class));
                overridePendingTransition(0, 0);
                finish();
                dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        dialog.show();
    }

}