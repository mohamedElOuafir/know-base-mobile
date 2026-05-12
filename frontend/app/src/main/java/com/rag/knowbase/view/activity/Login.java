package com.rag.knowbase.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.rag.knowbase.R;
import com.rag.knowbase.data.api.UserApi;
import com.rag.knowbase.data.dto.UserLoginRequest;
import com.rag.knowbase.data.dto.UserResponseDto;
import com.rag.knowbase.data.retrofit.RetrofitBackend;
import com.rag.knowbase.session.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Login extends AppCompatActivity {

    private TextInputEditText email;
    private TextInputEditText password;
    private TextView errorMessage;
    private Button loginButton;
    private CircularProgressIndicator loginAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_page), (v, insets) -> {
            //modification ici pour prendre en compte le clavier
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = findViewById(R.id.emailLogin);
        password = findViewById(R.id.passwordLogin);
        errorMessage = findViewById(R.id.errorMessageLogin);

        loginButton = findViewById(R.id.loginButton);
        loginAnimation = findViewById(R.id.loginAnimation);

        loginButton.setOnClickListener(v -> {
            if(!email.getText().toString().trim().isEmpty() && !password.getText().toString().trim().isEmpty()) {
                showLoading();
                loginUser(email.getText().toString(), password.getText().toString());
            }
        });

    }


    public void loginUser(String email, String password){
        UserLoginRequest req = new UserLoginRequest(email, password);
        UserApi userApi = RetrofitBackend.getUserApi();
        userApi.login(req).enqueue(new Callback<UserResponseDto>() {
            @Override
            public void onResponse(Call<UserResponseDto> call, Response<UserResponseDto> response) {
                if(response.isSuccessful() && response.body() != null) {
                    if (!response.body().getAuthenticated()){
                        errorMessage.setText("Invalid email or password!");
                    }else {

                        UserResponseDto user = response.body();
                        SessionManager session = new SessionManager(Login.this);
                        session.saveUser(user);
                        handleLoginSuccessfull();
                    }
                    hideLoading();
                }
            }

            @Override
            public void onFailure(Call<UserResponseDto> call, Throwable throwable) {
                errorMessage.setText("Network problem!");
                hideLoading();
            }
        });

    }


    private void showLoading() {
        loginButton.setEnabled(false);
        loginButton.setText("");
        loginAnimation.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        loginButton.setEnabled(true);
        loginButton.setText("Login");
        loginAnimation.setVisibility(View.GONE);
    }

    public void handleLoginSuccessfull(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }



    //Handle creation account click
    public void startSignupActivity(View v){
        Intent intent = new Intent(v.getContext(), Signup.class);
        startActivity(intent);
        finish();
    }
}