package com.example.linearlayout2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;


public class signupActivity extends AppCompatActivity {

   EditText email, password;
   Button btnsignup;
   FirebaseAuth firebaseAuth;

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

        email = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);
        btnsignup = findViewById(R.id.signUpButton);

        firebaseAuth = FirebaseAuth.getInstance();

        btnsignup.setOnClickListener( view -> {

            String emailPicked = email.getText().toString().trim();
            String passwordPicked = password.getText().toString().trim();

            if(emailPicked.isEmpty() || passwordPicked.isEmpty()){

                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();

            }

            firebaseAuth.createUserWithEmailAndPassword(emailPicked, passwordPicked).addOnCompleteListener(

                    task -> {

                        if(task.isSuccessful()){

                            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
                            Intent openlogin = new Intent(signupActivity.this, MainActivity.class);
                            startActivity(openlogin);
                        } else{

                            Log.e("Firebase Signup", "Error: ", task.getException());

                            Toast.makeText(this, "Sign Up failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }


            );




                }
        );


    }



}