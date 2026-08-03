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

public class signupActivity extends AppCompatActivity {

    private EditText nameInput, studentIdInput, emailInput, passwordInput;
    private Button btnSignUp;
    private ProgressBar signupProgress;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseManager = FirebaseManager.getInstance();

        nameInput = findViewById(R.id.nameInput);
        studentIdInput = findViewById(R.id.studentIdInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        btnSignUp = findViewById(R.id.signUpButton);
        signupProgress = findViewById(R.id.signupProgress);
        TextView linkToLogin = findViewById(R.id.linkToLogin);

        if (linkToLogin != null) {
            linkToLogin.setOnClickListener(v -> finish());
        }

        btnSignUp.setOnClickListener(view -> performSignUp());
    }

    private void performSignUp() {
        String name = nameInput.getText().toString().trim();
        String studentId = studentIdInput != null ? studentIdInput.getText().toString().trim() : "";
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameInput.setError("Please enter your full name");
            nameInput.requestFocus();
            return;
        }

        if (studentId.isEmpty()) {
            if (studentIdInput != null) {
                studentIdInput.setError("Please enter your Student ID");
                studentIdInput.requestFocus();
            }
            return;
        }

        if (email.isEmpty()) {
            emailInput.setError("Please enter your email");
            emailInput.requestFocus();
            return;
        }

        if (password.isEmpty() || password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            passwordInput.requestFocus();
            return;
        }

        signupProgress.setVisibility(View.VISIBLE);
        btnSignUp.setEnabled(false);

        firebaseManager.getAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    signupProgress.setVisibility(View.GONE);
                    btnSignUp.setEnabled(true);

                    if (task.isSuccessful()) {
                        String uid = firebaseManager.getCurrentUserId();
                        User newUser = new User(uid, name, email, studentId, "ATHLETE", System.currentTimeMillis());

                        firebaseManager.saveUser(newUser, new FirebaseManager.DataCallback<Void>() {
                            @Override
                            public void onSuccess(Void data) {
                                Toast.makeText(signupActivity.this, "Welcome Athlete! Account registered.", Toast.LENGTH_SHORT).show();
                                Intent openDashboard = new Intent(signupActivity.this, userDashboard.class);
                                startActivity(openDashboard);
                                finishAffinity();
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(signupActivity.this, "Account created with error: " + error, Toast.LENGTH_SHORT).show();
                                Intent openDashboard = new Intent(signupActivity.this, userDashboard.class);
                                startActivity(openDashboard);
                                finishAffinity();
                            }
                        });
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Sign up failed";
                        Log.e("Firebase Signup", "Error: " + error);
                        Toast.makeText(signupActivity.this, "Sign Up Failed: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }
}