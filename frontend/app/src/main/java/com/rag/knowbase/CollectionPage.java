package com.rag.knowbase;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rag.knowbase.adapter.CollectionAdapter;
import com.rag.knowbase.model.CollectionUser;

import java.lang.reflect.Array;
import java.sql.Date;
import java.util.List;

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
    }

}