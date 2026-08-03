package com.example.linearlayout2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.linearlayout2.firebase.FirebaseManager;
import com.example.linearlayout2.models.User;

public class userDashboard extends AppCompatActivity {

    private CardView cardBasketball, cardSoccer, cardVolleyball, cardHockey, cardTennis, cardSwimming;
    private ImageButton btnAnnouncements, btnProfile, btnManagerPortal;
    private TextView txtUserEmail;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseManager = FirebaseManager.getInstance();

        txtUserEmail = findViewById(R.id.txtUserEmail);
        btnAnnouncements = findViewById(R.id.btnAnnouncements);
        btnProfile = findViewById(R.id.btnProfile);
        btnManagerPortal = findViewById(R.id.btnManagerPortal);

        if (txtUserEmail != null) {
            txtUserEmail.setText(firebaseManager.getCurrentUserEmail());
        }

        // Hide Club Manager portal button by default for regular athlete users
        if (btnManagerPortal != null) {
            btnManagerPortal.setVisibility(View.GONE);

            String uid = firebaseManager.getCurrentUserId();
            firebaseManager.getUserByUid(uid, new FirebaseManager.DataCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    if (user != null && ("CLUB_MANAGER".equalsIgnoreCase(user.getUserType()) || "ADMIN".equalsIgnoreCase(user.getUserType()))) {
                        btnManagerPortal.setVisibility(View.VISIBLE);
                        btnManagerPortal.setOnClickListener(v -> {
                            Intent intent = new Intent(userDashboard.this, ClubManagerDashboardActivity.class);
                            startActivity(intent);
                        });
                    } else {
                        btnManagerPortal.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError(String error) {
                    btnManagerPortal.setVisibility(View.GONE);
                }
            });
        }

        cardBasketball = findViewById(R.id.cardBasketball);
        cardSoccer = findViewById(R.id.cardSoccer);
        cardVolleyball = findViewById(R.id.cardVolleyball);
        cardHockey = findViewById(R.id.cardHockey);
        cardTennis = findViewById(R.id.cardTennis);
        cardSwimming = findViewById(R.id.cardSwimming);

        if (cardBasketball != null) cardBasketball.setOnClickListener(v -> openSportDashboard("Basketball"));
        if (cardSoccer != null) cardSoccer.setOnClickListener(v -> openSportDashboard("Football"));
        if (cardVolleyball != null) cardVolleyball.setOnClickListener(v -> openSportDashboard("Volleyball"));
        if (cardHockey != null) cardHockey.setOnClickListener(v -> openSportDashboard("Field Hockey"));
        if (cardTennis != null) cardTennis.setOnClickListener(v -> openSportDashboard("Tennis"));
        if (cardSwimming != null) cardSwimming.setOnClickListener(v -> openSportDashboard("Swimming"));

        if (btnAnnouncements != null) {
            btnAnnouncements.setOnClickListener(v -> {
                Intent intent = new Intent(userDashboard.this, AnnouncementsActivity.class);
                startActivity(intent);
            });
        }

        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                Intent intent = new Intent(userDashboard.this, UserProfileActivity.class);
                startActivity(intent);
            });
        }
    }

    private void openSportDashboard(String sportName) {
        Intent intent = new Intent(userDashboard.this, basketballDashboard.class);
        intent.putExtra("EXTRA_SPORT_NAME", sportName);
        startActivity(intent);
    }
}