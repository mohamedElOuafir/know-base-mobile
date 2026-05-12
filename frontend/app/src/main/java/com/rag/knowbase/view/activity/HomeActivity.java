package com.rag.knowbase.view.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rag.knowbase.R;
import com.rag.knowbase.data.api.DashboardApi;
import com.rag.knowbase.data.api.UserApi;
import com.rag.knowbase.data.dto.DashboardStatsDto;
import com.rag.knowbase.data.dto.UserLoginRequest;
import com.rag.knowbase.data.dto.UserResponseDto;
import com.rag.knowbase.data.retrofit.RetrofitBackend;
import com.rag.knowbase.session.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private TextView userName;
    private ImageView profileImage;
    private TextView chatsCount;
    private TextView filesCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        SessionManager session = new SessionManager(this);
        UserResponseDto user = session.getUser();

        if(user.getProfileImage() != null) {
            profileImage = findViewById(R.id.profileImage);
            profileImage.clearColorFilter();
            Glide.with(this).load(user.getProfileImage()).into(profileImage);
        }
        userName = findViewById(R.id.tvUserName);
        chatsCount = findViewById(R.id.totalChatsDashboard);
        filesCount = findViewById(R.id.totalFilesDashboard);

        userName.setText(String.valueOf(user.getFirstName() + " " + user.getLasName()));

        loadDashboard("Bearer " + user.getToken());


        //gestion de nav_bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bar);

        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {

                //rester dans la page d'acceuil
                return true;
            } else if (itemId == R.id.nav_collections) {
                // Naviguer vers la page Collections
                startActivity(new Intent(getApplicationContext(), CollectionPage.class));
                overridePendingTransition(0, 0); // Retire l'animation par défaut pour un effet "changement d'onglet"
                finish();
                return true;
            }else if (itemId == R.id.nav_profile) {
                // Naviguer vers la page Profil
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

    }


    public void loadDashboard(String token){

        DashboardApi dashboardApi = RetrofitBackend.getDashboardApi();
        dashboardApi.getStats(token).enqueue(new Callback<DashboardStatsDto>() {
            @Override
            public void onResponse(Call<DashboardStatsDto> call, Response<DashboardStatsDto> response) {
                if(response.isSuccessful() && response.body() != null) {
                    DashboardStatsDto stats = response.body();
                    chatsCount.setText(String.valueOf(stats.getTotalChats()));
                    filesCount.setText(String.valueOf(stats.getTotalFiles()));
                }
            }

            @Override
            public void onFailure(Call<DashboardStatsDto> call, Throwable throwable) {
                Intent intent = new Intent(HomeActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

    }
}