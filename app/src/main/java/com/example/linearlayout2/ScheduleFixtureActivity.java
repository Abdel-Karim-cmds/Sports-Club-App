package com.example.linearlayout2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.linearlayout2.firebase.FirebaseManager;
import com.example.linearlayout2.models.Fixture;

public class ScheduleFixtureActivity extends AppCompatActivity {


    private EditText editHomeTeam, editAwayTeam, editHomeScore, editAwayScore, editFixtureDate, editFixtureVenue;
    private RadioGroup radioGroupStatus;
    private RadioButton radioUpcoming, radioFinished;
    private Button btnSaveFixture;
    private ProgressBar fixtureProgress;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_fixture);

        firebaseManager = FirebaseManager.getInstance();

        // Access control check for ATHLETE role
        String uid = firebaseManager.getCurrentUserId();
        firebaseManager.getUserByUid(uid, new FirebaseManager.DataCallback<com.example.linearlayout2.models.User>() {
            @Override
            public void onSuccess(com.example.linearlayout2.models.User user) {
                if (user != null && "ATHLETE".equalsIgnoreCase(user.getUserType())) {
                    Toast.makeText(ScheduleFixtureActivity.this, "Access Denied: Regular athletes do not have access to club management.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onError(String error) {
            }
        });


        editHomeTeam = findViewById(R.id.editHomeTeam);
        editAwayTeam = findViewById(R.id.editAwayTeam);
        editHomeScore = findViewById(R.id.editHomeScore);
        editAwayScore = findViewById(R.id.editAwayScore);
        editFixtureDate = findViewById(R.id.editFixtureDate);
        editFixtureVenue = findViewById(R.id.editFixtureVenue);
        radioGroupStatus = findViewById(R.id.radioGroupFixtureStatus);
        radioUpcoming = findViewById(R.id.radioUpcoming);
        radioFinished = findViewById(R.id.radioFinished);
        btnSaveFixture = findViewById(R.id.btnSaveFixture);
        fixtureProgress = findViewById(R.id.fixtureProgress);

        // Auto-detect manager's sport & pre-fill home team name
        String email = firebaseManager.getCurrentUserEmail();
        if (email != null) {
            String lower = email.toLowerCase();
            if (lower.contains("basketball")) {
                editHomeTeam.setText("USIU Tigers Basketball");
            } else if (lower.contains("football")) {
                editHomeTeam.setText("USIU Strikers Football");
            } else if (lower.contains("volleyball")) {
                editHomeTeam.setText("USIU Spikers Volleyball");
            } else if (lower.contains("hockey")) {
                editHomeTeam.setText("USIU Field Hockey Squad");
            } else if (lower.contains("tennis")) {
                editHomeTeam.setText("USIU Tennis Academy");
            } else if (lower.contains("swimming") || lower.contains("swim")) {
                editHomeTeam.setText("USIU Aquatics Swimming");
            }
        }

        radioGroupStatus.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioUpcoming) {
                editHomeScore.setText("0");
                editAwayScore.setText("0");
                editHomeScore.setEnabled(false);
                editAwayScore.setEnabled(false);
            } else {
                if (editHomeScore.getText().toString().equals("0")) editHomeScore.setText("");
                if (editAwayScore.getText().toString().equals("0")) editAwayScore.setText("");
                editHomeScore.setEnabled(true);
                editAwayScore.setEnabled(true);
            }
        });

        // Initialize state
        if (radioUpcoming.isChecked()) {
            editHomeScore.setText("0");
            editAwayScore.setText("0");
            editHomeScore.setEnabled(false);
            editAwayScore.setEnabled(false);
        }

        btnSaveFixture.setOnClickListener(v -> performSaveFixture());
    }

    private void performSaveFixture() {
        String sport = firebaseManager.getManagerSport();
        String homeTeam = editHomeTeam.getText().toString().trim();
        String awayTeam = editAwayTeam.getText().toString().trim();
        String homeScore = editHomeScore.getText().toString().trim();
        String awayScore = editAwayScore.getText().toString().trim();
        String date = editFixtureDate.getText().toString().trim();
        String venue = editFixtureVenue.getText().toString().trim();

        if (homeTeam.isEmpty() || awayTeam.isEmpty()) {
            Toast.makeText(this, "Please enter home and away team names", Toast.LENGTH_SHORT).show();
            return;
        }

        if (date.isEmpty()) {
            editFixtureDate.setError("Please enter date and time");
            editFixtureDate.requestFocus();
            return;
        }

        String status = "Upcoming";
        if (radioFinished != null && radioFinished.isChecked()) {
            status = "Finished";
        }

        if ("Upcoming".equals(status)) {
            homeScore = "0";
            awayScore = "0";
        } else {
            if (homeScore.isEmpty()) homeScore = "0";
            if (awayScore.isEmpty()) awayScore = "0";
        }

        if (fixtureProgress != null) fixtureProgress.setVisibility(View.VISIBLE);
        btnSaveFixture.setEnabled(false);

        Fixture fixture = new Fixture(null, sport, homeTeam, awayTeam, homeScore, awayScore, date, venue, status);

        firebaseManager.createFixture(fixture, new FirebaseManager.DataCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if (fixtureProgress != null) fixtureProgress.setVisibility(View.GONE);
                btnSaveFixture.setEnabled(true);
                Toast.makeText(ScheduleFixtureActivity.this, "Match Fixture Posted for " + homeTeam + "!", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(String error) {
                if (fixtureProgress != null) fixtureProgress.setVisibility(View.GONE);
                btnSaveFixture.setEnabled(true);
                Toast.makeText(ScheduleFixtureActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
