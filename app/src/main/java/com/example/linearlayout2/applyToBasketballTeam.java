package com.example.linearlayout2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.linearlayout2.firebase.FirebaseManager;
import com.example.linearlayout2.models.ApplicationRecord;

public class applyToBasketballTeam extends AppCompatActivity {

    private EditText playerName, editStudentId, playerPosition;
    private RadioGroup rgGender;
    private Spinner selectSchools;
    private Button applyBB;
    private ProgressBar applyProgress;
    private TextView txtApplyHeader;
    private ImageView imgApplySportIcon;
    private String selectedSport = "Basketball";
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_apply_to_basketball_team);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseManager = FirebaseManager.getInstance();

        if (getIntent() != null && getIntent().hasExtra("EXTRA_SPORT_NAME")) {
            selectedSport = getIntent().getStringExtra("EXTRA_SPORT_NAME");
        }

        txtApplyHeader = findViewById(R.id.txtApplyHeader);
        imgApplySportIcon = findViewById(R.id.imgApplySportIcon);

        if (txtApplyHeader != null) {
            txtApplyHeader.setText(selectedSport + " Registration");
        }

        if (imgApplySportIcon != null) {
            switch (selectedSport) {
                case "Football":
                    imgApplySportIcon.setImageResource(R.drawable.ic_soccer);
                    break;
                case "Volleyball":
                    imgApplySportIcon.setImageResource(R.drawable.ic_volleyball);
                    break;
                case "Field Hockey":
                    imgApplySportIcon.setImageResource(R.drawable.ic_hockey);
                    break;
                case "Tennis":
                    imgApplySportIcon.setImageResource(R.drawable.ic_tennis);
                    break;
                case "Swimming":
                    imgApplySportIcon.setImageResource(R.drawable.ic_swimming);
                    break;
                default:
                    imgApplySportIcon.setImageResource(R.drawable.ic_basketball);
                    break;
            }
        }

        playerName = findViewById(R.id.bbplayername);
        editStudentId = findViewById(R.id.editStudentId);
        playerPosition = findViewById(R.id.bbposition);
        rgGender = findViewById(R.id.radioGender);
        selectSchools = findViewById(R.id.selectSchool);
        applyBB = findViewById(R.id.btnApplyBB);
        applyProgress = findViewById(R.id.applyProgress);

        autoFetchUserDetails();

        applyBB.setOnClickListener(view -> savePlayer());
    }

    private void autoFetchUserDetails() {
        String uid = firebaseManager.getCurrentUserId();
        if (uid != null && !uid.isEmpty()) {
            firebaseManager.getUserByUid(uid, new FirebaseManager.DataCallback<com.example.linearlayout2.models.User>() {
                @Override
                public void onSuccess(com.example.linearlayout2.models.User user) {
                    if (user != null) {
                        if (user.getName() != null && !user.getName().isEmpty()) {
                            playerName.setText(user.getName());
                        }
                        if (user.getStudentId() != null && !user.getStudentId().isEmpty()) {
                            editStudentId.setText(user.getStudentId());
                        }
                        Toast.makeText(applyToBasketballTeam.this, "Profile details auto-filled!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String error) {
                    if (firebaseManager.getCurrentUser() != null && firebaseManager.getCurrentUser().getDisplayName() != null) {
                        playerName.setText(firebaseManager.getCurrentUser().getDisplayName());
                    }
                }
            });
        }
    }

    private void savePlayer() {
        String name = playerName.getText().toString().trim();
        String studentId = editStudentId != null ? editStudentId.getText().toString().trim() : "";
        String position = playerPosition.getText().toString().trim();

        if (name.isEmpty()) {
            playerName.setError("Enter player name");
            playerName.requestFocus();
            return;
        }

        if (studentId.isEmpty()) {
            if (editStudentId != null) {
                editStudentId.setError("Enter Student ID");
                editStudentId.requestFocus();
            }
            return;
        }

        if (position.isEmpty()) {
            playerPosition.setError("Enter playing position");
            playerPosition.requestFocus();
            return;
        }

        int selectgenderID = rgGender.getCheckedRadioButtonId();
        if (selectgenderID == -1) {
            Toast.makeText(this, "Please select a gender squad", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedGender = findViewById(selectgenderID);
        String gender = selectedGender.getText().toString();

        if (selectSchools.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a school / department", Toast.LENGTH_SHORT).show();
            return;
        }
        String school = selectSchools.getSelectedItem().toString();

        if (applyProgress != null) applyProgress.setVisibility(View.VISIBLE);
        applyBB.setEnabled(false);

        ApplicationRecord record = new ApplicationRecord(
                null,
                firebaseManager.getCurrentUserId(),
                name,
                studentId,
                selectedSport,
                position,
                gender,
                school,
                "Pending",
                null
        );

        firebaseManager.submitApplication(record, new FirebaseManager.DataCallback<String>() {
            @Override
            public void onSuccess(String docId) {
                if (applyProgress != null) applyProgress.setVisibility(View.GONE);
                applyBB.setEnabled(true);
                Toast.makeText(applyToBasketballTeam.this, "Application Submitted Successfully for " + selectedSport + "!", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(String error) {
                if (applyProgress != null) applyProgress.setVisibility(View.GONE);
                applyBB.setEnabled(true);
                Toast.makeText(applyToBasketballTeam.this, "Submission Failed: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}