package com.rag.knowbase.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.rag.knowbase.R;
import com.rag.knowbase.data.api.UserApi;
import com.rag.knowbase.data.dto.UserResponseDto;
import com.rag.knowbase.data.retrofit.RetrofitBackend;
import com.rag.knowbase.session.SessionManager;

import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imageProfile;
    private TextView tvUserName, tvUserEmail;
    private EditText editFirstName, editLastName;
    private TextInputEditText editNewPassword, editConfirmNewPassword;
    private MaterialButton btnSaveProfile, btnLogout;

    private SessionManager session;
    private UserResponseDto currentUser;

    private ActivityResultLauncher<String> galleryLauncher;
    private Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_profile), (v, insets) -> {
            Insets insetsToApply = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime()
            );
            v.setPadding(insetsToApply.left, insetsToApply.top, insetsToApply.right, 0);
            return insets;
        });

        initViews();
        setupSession();
        fillUserData();
        setupGalleryLauncher();
        setupListeners();

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bar);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_collections) {
                startActivity(new Intent(getApplicationContext(), CollectionPage.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else
                return itemId == R.id.nav_profile;
        });
    }

    private void initViews() {
        imageProfile = findViewById(R.id.imageProfile);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editNewPassword = findViewById(R.id.editNewPassword);
        editConfirmNewPassword = findViewById(R.id.editConfirmNewPassword);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupSession() {
        session = new SessionManager(this);
        currentUser = session.getUser();
        if (currentUser == null) {
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }

    private void fillUserData() {
        tvUserName.setText(currentUser.getFirstName() + " " + currentUser.getLasName());
        tvUserEmail.setText(currentUser.getEmail());
        editFirstName.setText(currentUser.getFirstName());
        editLastName.setText(currentUser.getLasName());

        // Load profile image if available
        if (currentUser.getProfileImage() != null && !currentUser.getProfileImage().isEmpty()) {
            imageProfile.setImageTintList(null);
            Glide.with(this)
                    .load(currentUser.getProfileImage())
                    .circleCrop()
                    .placeholder(R.drawable.ic_profile)
                    .into(imageProfile);
        }
    }

    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri == null) return;
                    selectedImageUri = uri;
                    Glide.with(this)
                            .load(uri)
                            .circleCrop()
                            .into(imageProfile);
                }
        );
    }

    private void setupListeners() {

        findViewById(R.id.fab_edit_photo).setOnClickListener(v ->
                galleryLauncher.launch("image/*")
        );

        btnSaveProfile.setOnClickListener(v -> updateProfile());

        btnLogout.setOnClickListener(v -> logout());
    }

    private void updateProfile() {
        String firstName = editFirstName.getText().toString().trim();
        String lastName  = editLastName.getText().toString().trim();
        String newPass   = editNewPassword.getText().toString().trim();
        String confirmPass = editConfirmNewPassword.getText().toString().trim();

        if (firstName.isEmpty()) {
            editFirstName.setError("First name is required");
            return;
        }
        if (lastName.isEmpty()) {
            editLastName.setError("Last name is required");
            return;
        }
        if (!newPass.isEmpty()) {
            if (!newPass.equals(confirmPass)) {
                editConfirmNewPassword.setError("Passwords do not match");
                return;
            }
            if (newPass.length() < 6) {
                editNewPassword.setError("Password must be at least 6 characters");
                return;
            }
        }

        String token = "Bearer " + session.getToken();

        // Build multipart request
        RequestBody firstNameBody = RequestBody.create(firstName, MediaType.parse("text/plain"));
        RequestBody lastNameBody  = RequestBody.create(lastName, MediaType.parse("text/plain"));
        RequestBody passwordBody  = RequestBody.create(newPass, MediaType.parse("text/plain"));

        MultipartBody.Part imagePart = null;
        if (selectedImageUri != null) {
            try {
                InputStream is = getContentResolver().openInputStream(selectedImageUri);
                byte[] bytes = is.readAllBytes();
                RequestBody imageBody = RequestBody.create(bytes, MediaType.parse(
                        getContentResolver().getType(selectedImageUri)
                ));
                imagePart = MultipartBody.Part.createFormData("profileImage",
                        "profile_image.jpg", imageBody);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        UserApi userApi = RetrofitBackend.getUserApi();
        userApi.updateProfile(token, firstNameBody, lastNameBody, passwordBody, imagePart)
                .enqueue(new Callback<UserResponseDto>() {
                    @Override
                    public void onResponse(Call<UserResponseDto> call, Response<UserResponseDto> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Update session with new user data
                            session.saveUser(response.body());
                            currentUser = response.body();
                            fillUserData();
                            Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Update failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponseDto> call, Throwable t) {
                        Toast.makeText(ProfileActivity.this,
                                "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> {
                    session.logout();
                    Intent intent = new Intent(ProfileActivity.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}