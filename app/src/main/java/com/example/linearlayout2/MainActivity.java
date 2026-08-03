package com.example.linearlayout2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.linearlayout2.firebase.FirebaseManager;
import com.example.linearlayout2.models.User;

public class MainActivity extends AppCompatActivity {

    private EditText loginEmail, loginPassword;
    private Button loginButton;
    private ProgressBar loginProgress;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseManager = FirebaseManager.getInstance();
        firebaseManager.seedInitialDataIfEmpty();

        // Check if user is already logged in
        if (firebaseManager.getCurrentUser() != null) {
            routeUserToRoleDashboard();
            return;
        }

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        loginProgress = findViewById(R.id.loginProgress);
        TextView linkSignup = findViewById(R.id.linkToSignUpPage);

        if (linkSignup != null) {
            linkSignup.setOnClickListener(view -> {
                Intent openSignUp = new Intent(MainActivity.this, signupActivity.class);
                startActivity(openSignUp);
            });
        }

        if (loginButton != null) {
            loginButton.setOnClickListener(view -> performLogin());
        }
    }

    private void performLogin() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (email.isEmpty()) {
            loginEmail.setError("Please enter your email");
            loginEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            loginPassword.setError("Please enter your password");
            loginPassword.requestFocus();
            return;
        }

        loginProgress.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        firebaseManager.getAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    loginProgress.setVisibility(View.GONE);
                    loginButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        routeUserToRoleDashboard();
                    } else {
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Authentication failed";
                        Toast.makeText(MainActivity.this, "Login Failed: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void routeUserToRoleDashboard() {
        String uid = firebaseManager.getCurrentUserId();
        String email = firebaseManager.getCurrentUserEmail();

        firebaseManager.getUserByUid(uid, new FirebaseManager.DataCallback<User>() {
            @Override
            public void onSuccess(User user) {
                Intent intent;
                String userType = user != null ? user.getUserType() : "ATHLETE";

                if ("ADMIN".equalsIgnoreCase(userType)) {
                    intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
                } else if ("CLUB_MANAGER".equalsIgnoreCase(userType)) {
                    intent = new Intent(MainActivity.this, ClubManagerDashboardActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, userDashboard.class);
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String error) {
                Log.e("MainActivity", "Error fetching user record from users table: " + error);
                Intent intent;
                if ("admin@usiu.ac.ke".equalsIgnoreCase(email)) {
                    intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
                } else if (email != null && (email.equalsIgnoreCase("manager@usiu.ac.ke") || email.equalsIgnoreCase("basketball@usiu.ac.ke") || email.equalsIgnoreCase("football@usiu.ac.ke") || email.equalsIgnoreCase("volleyball@usiu.ac.ke") || email.equalsIgnoreCase("hockey@usiu.ac.ke") || email.equalsIgnoreCase("tennis@usiu.ac.ke") || email.equalsIgnoreCase("swimming@usiu.ac.ke"))) {
                    intent = new Intent(MainActivity.this, ClubManagerDashboardActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, userDashboard.class);
                }
                startActivity(intent);
                finish();
            }
        });
    }
}