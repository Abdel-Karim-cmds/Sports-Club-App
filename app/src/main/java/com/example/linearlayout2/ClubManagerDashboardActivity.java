package com.example.linearlayout2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.linearlayout2.firebase.FirebaseManager;

public class ClubManagerDashboardActivity extends AppCompatActivity {

    private CardView cardManageApps, cardScheduleFixture, cardPostAnnouncement, cardViewManagerRoster;
    private Button btnManagerSignOut;
    private TextView txtManagerEmail;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_manager_dashboard);

        firebaseManager = FirebaseManager.getInstance();

        txtManagerEmail = findViewById(R.id.txtManagerEmail);
        btnManagerSignOut = findViewById(R.id.btnManagerSignOut);
        cardManageApps = findViewById(R.id.cardManageApps);
        cardScheduleFixture = findViewById(R.id.cardScheduleFixture);
        cardPostAnnouncement = findViewById(R.id.cardPostAnnouncement);
        cardViewManagerRoster = findViewById(R.id.cardViewManagerRoster);

        if (txtManagerEmail != null) {
            txtManagerEmail.setText(firebaseManager.getCurrentUserEmail());
        }

        // Access control check for ATHLETE role
        String uid = firebaseManager.getCurrentUserId();
        String email = firebaseManager.getCurrentUserEmail();

        firebaseManager.getUserByUid(uid, new FirebaseManager.DataCallback<com.example.linearlayout2.models.User>() {
            @Override
            public void onSuccess(com.example.linearlayout2.models.User user) {
                if (user != null && "ATHLETE".equalsIgnoreCase(user.getUserType())) {
                    Toast.makeText(ClubManagerDashboardActivity.this, "Access Denied: Regular athletes do not have access to Club Management.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ClubManagerDashboardActivity.this, userDashboard.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onError(String error) {
                if (email != null && !email.toLowerCase().contains("manager") && !email.toLowerCase().contains("basketball@usiu.ac.ke") && !email.toLowerCase().contains("football@usiu.ac.ke") && !email.toLowerCase().contains("volleyball@usiu.ac.ke") && !email.toLowerCase().contains("hockey@usiu.ac.ke") && !email.toLowerCase().contains("tennis@usiu.ac.ke") && !email.toLowerCase().contains("swimming@usiu.ac.ke")) {
                    Toast.makeText(ClubManagerDashboardActivity.this, "Access Denied: Regular athletes do not have access to Club Management.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ClubManagerDashboardActivity.this, userDashboard.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        btnManagerSignOut.setOnClickListener(v -> {
            firebaseManager.signOut();
            Toast.makeText(this, "Manager Signed Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ClubManagerDashboardActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        cardManageApps.setOnClickListener(v -> {
            Intent intent = new Intent(ClubManagerDashboardActivity.this, ManageApplicationsActivity.class);
            startActivity(intent);
        });

        cardScheduleFixture.setOnClickListener(v -> {
            Intent intent = new Intent(ClubManagerDashboardActivity.this, ScheduleFixtureActivity.class);
            startActivity(intent);
        });

        cardPostAnnouncement.setOnClickListener(v -> {
            Intent intent = new Intent(ClubManagerDashboardActivity.this, PostAnnouncementActivity.class);
            startActivity(intent);
        });

        cardViewManagerRoster.setOnClickListener(v -> {
            Intent intent = new Intent(ClubManagerDashboardActivity.this, ViewPlayersActivity.class);
            intent.putExtra("EXTRA_SPORT_NAME", firebaseManager.getManagerSport());
            startActivity(intent);
        });
    }
}
