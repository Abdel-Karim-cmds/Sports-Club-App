package com.example.linearlayout2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

public class MainActivity extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    Button loginButton;

    FirebaseAuth firebaseAuth;

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

        TextView linkSignup = findViewById(R.id.linkToSignUpPage);

        linkSignup.setOnClickListener(view -> {

            Intent openSignUp = new Intent(MainActivity.this, signupActivity.class);
            startActivity( openSignUp );

        });

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);

        firebaseAuth = FirebaseAuth.getInstance();


        loginButton.setOnClickListener( view -> {

            String emailuserpicked = loginEmail.getText().toString().trim();
            String passworduserpicked = loginPassword.getText().toString().trim();

            if(emailuserpicked.isEmpty() || passworduserpicked.isEmpty()){

                Toast.makeText(this, "Please fill in the email and password", Toast.LENGTH_SHORT).show();
            }

            firebaseAuth.signInWithEmailAndPassword(emailuserpicked, passworduserpicked).addOnCompleteListener(

                    task -> {
                        if(task.isSuccessful()){
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent openuserdashboard = new Intent(MainActivity.this, userDashboard.class);
                            startActivity(openuserdashboard);
                        } else {

                            Toast.makeText(this, "Login Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }


                    }

            );

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.optionsbeforelogin, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if(item.getItemId() == R.id.aboutApp){

            Toast.makeText(this, "About App Item clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (item.getItemId() == R.id.SignUp){




            
        }

        if(item.getItemId() == R.id.exitapp){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}