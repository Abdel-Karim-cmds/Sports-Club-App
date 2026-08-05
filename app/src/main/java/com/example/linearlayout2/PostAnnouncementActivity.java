package com.example.linearlayout2;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.linearlayout2.firebase.FirebaseManager;
import com.example.linearlayout2.models.Announcement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostAnnouncementActivity extends AppCompatActivity {


    private EditText editAnnTitle, editAnnCategory, editAnnContent;
    private Button btnPostAnn;
    private ProgressBar postAnnProgress;
    private FirebaseManager firebaseManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_announcement);

        firebaseManager = FirebaseManager.getInstance();

        String uid = firebaseManager.getCurrentUserId();
        firebaseManager.getUserByUid(uid, new FirebaseManager.DataCallback<com.example.linearlayout2.models.User>() {
            @Override
            public void onSuccess(com.example.linearlayout2.models.User user) {
                if (user != null && "ATHLETE".equalsIgnoreCase(user.getUserType())) {
                    Toast.makeText(PostAnnouncementActivity.this, "Access Denied: Regular athletes do not have access to club management.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onError(String error) {
            }
        });

        editAnnTitle = findViewById(R.id.editAnnTitle);
        editAnnCategory = findViewById(R.id.editAnnCategory);
        editAnnContent = findViewById(R.id.editAnnContent);
        btnPostAnn = findViewById(R.id.btnPostAnn);
        postAnnProgress = findViewById(R.id.postAnnProgress);

        btnPostAnn.setOnClickListener(v -> performPost());
    }

    private void performPost() {
        String sport = firebaseManager.getManagerSport();
        String publishingClub = "All".equals(sport) ? "USIU Sports Board" : "USIU " + sport + " Club";
        String title = editAnnTitle.getText().toString().trim();
        String category = editAnnCategory.getText().toString().trim();
        String content = editAnnContent.getText().toString().trim();

        if (title.isEmpty()) {
            editAnnTitle.setError("Please enter a title");
            editAnnTitle.requestFocus();
            return;
        }

        if (content.isEmpty()) {
            editAnnContent.setError("Please enter details");
            editAnnContent.requestFocus();
            return;
        }

        if (category.isEmpty()) category = "Notice";

        String dateStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (postAnnProgress != null) postAnnProgress.setVisibility(View.VISIBLE);
        btnPostAnn.setEnabled(false);

        Announcement announcement = new Announcement(null, title, content, dateStr, category, publishingClub);

        firebaseManager.postAnnouncement(announcement, new FirebaseManager.DataCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if (postAnnProgress != null) postAnnProgress.setVisibility(View.GONE);
                btnPostAnn.setEnabled(true);
                Toast.makeText(PostAnnouncementActivity.this, "Announcement Published by " + publishingClub + "!", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(String error) {
                if (postAnnProgress != null) postAnnProgress.setVisibility(View.GONE);
                btnPostAnn.setEnabled(true);
                Toast.makeText(PostAnnouncementActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
