package com.rag.knowbase.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.rag.knowbase.R;
import com.rag.knowbase.data.api.UserApi;
import com.rag.knowbase.data.dto.UserRegisterRequest;
import com.rag.knowbase.data.dto.UserResponseDto;
import com.rag.knowbase.data.retrofit.RetrofitBackend;
import com.rag.knowbase.session.SessionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signup extends AppCompatActivity {

    private TextInputEditText firstName;
    private TextInputEditText lastName;
    private TextInputEditText email;
    private TextInputEditText password;
    private TextView errorMessage;
    private TextView imagelabel;
    private Button uploadImageButton;
    private Button signupButton;
    private CircularProgressIndicator signupAnimation;
    private Uri selectedImageUri;


    private ActivityResultLauncher<String> galleryLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupGalleryLauncher();
        uploadImageButton.setOnClickListener(v -> {
            galleryLauncher.launch("image/*");
        });
        signupButton.setOnClickListener(v -> {
            showLoading();
            signupUser();
        });
    }


    private void initViews() {

        firstName = findViewById(R.id.firstName);

        lastName = findViewById(R.id.lastName);

        email = findViewById(R.id.emailSignup);

        password = findViewById(R.id.passwordSignup);

        errorMessage = findViewById(R.id.errorMessageSignup);

        imagelabel = findViewById(R.id.profileImageLabel);

        uploadImageButton = findViewById(R.id.uploadProfile);

        signupButton = findViewById(R.id.SignupButton);

        signupAnimation = findViewById(R.id.SignupAnimation);
    }

    private void setupGalleryLauncher() {

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                selectedImageUri = uri;
                imagelabel.setText(uri.getLastPathSegment().toString());
                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signupUser() {

        String first = firstName.getText().toString().trim();
        String last = lastName.getText().toString().trim();
        String mail = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (first.isEmpty() || last.isEmpty() || mail.isEmpty() || pass.isEmpty()) {
            errorMessage.setText("All fields are required");
            return;
        }

        try {

            RequestBody firstNameBody = RequestBody.create(first, okhttp3.MultipartBody.FORM);

            RequestBody lastNameBody = RequestBody.create(last, okhttp3.MultipartBody.FORM);

            RequestBody emailBody = RequestBody.create(mail, okhttp3.MultipartBody.FORM);

            RequestBody passwordBody = RequestBody.create(pass, okhttp3.MultipartBody.FORM);

            MultipartBody.Part imagePart = null;

            if (selectedImageUri != null) {

                File file = createTempFileFromUri(selectedImageUri);

                RequestBody fileBody = RequestBody.create(file, MediaType.parse(getContentResolver().getType(selectedImageUri)));

                imagePart = MultipartBody.Part.createFormData("profileImage", file.getName(), fileBody);
            }

            UserApi userApi = RetrofitBackend.getUserApi();

            userApi.register(firstNameBody, lastNameBody, emailBody, passwordBody, imagePart).enqueue(new Callback<UserResponseDto>() {

                @Override
                public void onResponse(Call<UserResponseDto> call, Response<UserResponseDto> response) {

                    if(response.isSuccessful() && response.body() != null) {
                        if (!response.body().getAuthenticated()){
                            errorMessage.setText("This email is already in use!");

                        }else {

                            UserResponseDto user = response.body();
                            SessionManager session = new SessionManager(Signup.this);
                            session.saveUser(user);
                            handleSignupSuccessfull();

                        }
                        hideLoading();
                    }
                }

                @Override
                public void onFailure(Call<UserResponseDto> call, Throwable t) {
                    errorMessage.setText("Network problem!");
                    hideLoading();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            errorMessage.setText("Error uploading image");
            hideLoading();
        }
    }


    private File createTempFileFromUri(Uri uri) throws Exception {

        InputStream inputStream = getContentResolver().openInputStream(uri);

        File tempFile = File.createTempFile("upload", ".jpg", getCacheDir());

        FileOutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[4096];

        int read;

        while ((read = inputStream.read(buffer)) != -1) {

            outputStream.write(buffer, 0, read);
        }

        outputStream.flush();

        outputStream.close();

        inputStream.close();

        return tempFile;
    }


    private void showLoading() {
        signupButton.setEnabled(false);
        signupButton.setText("");
        signupAnimation.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        signupButton.setEnabled(true);
        signupButton.setText("Sign Up");
        signupAnimation.setVisibility(View.GONE);
    }

    public void handleSignupSuccessfull(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }


    //Handle having an account click
    public void startLoginActivity(View v){
        Intent intent = new Intent(v.getContext(), Login.class);
        startActivity(intent);
        finish();
    }
}