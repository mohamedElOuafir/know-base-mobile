package com.rag.knowbase;


import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
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
import com.rag.knowbase.adapter.ChatAdapter;
import com.rag.knowbase.model.Chat;

import java.util.ArrayList;
import java.util.List;

public class ChatbotActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList = new ArrayList<>();
    private EditText editTextMessage;
    private FloatingActionButton btnSend;
    private ImageButton btnUpload;
    private ActivityResultLauncher<String> galleryLauncher;
    private boolean isWaitingForResponse = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        // Gestion des insets (Edge to Edge + Clavier)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_chat), (v, insets) -> {
            // On combine les barres système ET le clavier (ime)
            Insets insetsToApply = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());

            v.setPadding(insetsToApply.left, insetsToApply.top, insetsToApply.right, insetsToApply.bottom);

            // Si le clavier s'ouvre, on scrolle le RecyclerView vers le bas
            boolean isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime());
            if (isKeyboardVisible && chatList.size() > 0) {
                recyclerViewMessages.scrollToPosition(chatList.size() - 1);
            }
            return WindowInsetsCompat.CONSUMED;
        });

        ImageButton menu = findViewById(R.id.Menu);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        menu.setOnClickListener(v -> {
            // ce ligne Ouvre le menu latéral
            drawerLayout.openDrawer(GravityCompat.START);
        });



        recyclerViewMessages = findViewById(R.id.recycler_view_messages);
        editTextMessage = findViewById(R.id.edit_text_message);
        btnSend = findViewById(R.id.btnsend);
        btnUpload = findViewById(R.id.btnUpload);


        chatAdapter = new ChatAdapter(chatList);
        // affichage des items verticalement
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewMessages.setLayoutManager(layoutManager);
        recyclerViewMessages.setAdapter(chatAdapter);

        addMessage("Bonjour, comment puis-je vous aider ?", false);

        btnSend.setOnClickListener(v -> {
            String text = editTextMessage.getText().toString().trim();

            if(isWaitingForResponse){
                Toast.makeText(this, "Veuillez patienter...", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!text.isEmpty()) {
                isWaitingForResponse = true;
                btnSend.setEnabled(false);
                addMessage(text, true);
                sendMessage(text);
                editTextMessage.setText("");

                new Handler().postDelayed(() ->{
                    addMessage("Voiçi ma réponse automatique " , false);
                    isWaitingForResponse = false;
                    btnSend.setEnabled(true);
                }, 1000);
            }
        });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            Toast.makeText(ChatbotActivity.this, "Image sélectionnée : " + result.toString(), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(ChatbotActivity.this, "Aucune image sélectionnée", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        );

        btnUpload.setOnClickListener(v -> {
            Toast.makeText(this, "Upload clicked", Toast.LENGTH_SHORT).show();
            galleryLauncher.launch("image/*");
        });
    }

    private void sendMessage(String text) {
        Toast.makeText(this, " message ajouté : "+ text, Toast.LENGTH_SHORT).show();
    }

    private void addMessage(String message, boolean isUser) {
        chatList.add(new Chat(message, isUser));
        chatAdapter.notifyItemInserted(chatList.size() - 1);
        recyclerViewMessages.scrollToPosition(chatList.size() - 1);
    }
}