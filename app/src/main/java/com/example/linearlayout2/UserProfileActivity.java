package com.example.linearlayout2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linearlayout2.firebase.FirebaseManager;
import com.example.linearlayout2.models.ApplicationRecord;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private TextView txtProfileName, txtProfileEmail, txtEmptyUserApps;
    private Button btnSignOut;
    private RecyclerView recyclerUserApplications;
    private ProgressBar profileProgress;
    private ApplicationAdapter adapter;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        firebaseManager = FirebaseManager.getInstance();

        txtProfileName = findViewById(R.id.txtProfileName);
        txtProfileEmail = findViewById(R.id.txtProfileEmail);
        txtEmptyUserApps = findViewById(R.id.txtEmptyUserApps);
        btnSignOut = findViewById(R.id.btnSignOut);
        recyclerUserApplications = findViewById(R.id.recyclerUserApplications);
        profileProgress = findViewById(R.id.profileProgress);

        if (txtProfileEmail != null) {
            txtProfileEmail.setText(firebaseManager.getCurrentUserEmail());
        }

        if (txtProfileName != null && firebaseManager.getCurrentUser() != null) {
            String name = firebaseManager.getCurrentUser().getDisplayName();
            txtProfileName.setText(name != null && !name.isEmpty() ? name : "USIU Sports Club Member");
        }

        Button btnOpenManagerDashboard = findViewById(R.id.btnOpenManagerDashboard);
        if (btnOpenManagerDashboard != null) {
            btnOpenManagerDashboard.setVisibility(View.GONE);

            String uid = firebaseManager.getCurrentUserId();
            firebaseManager.getUserByUid(uid, new FirebaseManager.DataCallback<com.example.linearlayout2.models.User>() {
                @Override
                public void onSuccess(com.example.linearlayout2.models.User user) {
                    if (user != null && ("CLUB_MANAGER".equalsIgnoreCase(user.getUserType()) || "ADMIN".equalsIgnoreCase(user.getUserType()))) {
                        btnOpenManagerDashboard.setVisibility(View.VISIBLE);
                        btnOpenManagerDashboard.setOnClickListener(v -> {
                            Intent intent = new Intent(UserProfileActivity.this, ClubManagerDashboardActivity.class);
                            startActivity(intent);
                        });
                    } else {
                        btnOpenManagerDashboard.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError(String error) {
                    btnOpenManagerDashboard.setVisibility(View.GONE);
                }
            });
        }

        btnSignOut.setOnClickListener(v -> {
            firebaseManager.signOut();
            Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        recyclerUserApplications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ApplicationAdapter(new ArrayList<>());
        recyclerUserApplications.setAdapter(adapter);

        loadApplications();
    }

    private void loadApplications() {
        profileProgress.setVisibility(View.VISIBLE);
        firebaseManager.getUserApplications(new FirebaseManager.DataCallback<List<ApplicationRecord>>() {
            @Override
            public void onSuccess(List<ApplicationRecord> data) {
                profileProgress.setVisibility(View.GONE);
                adapter.updateList(data);

                if (data.isEmpty()) {
                    txtEmptyUserApps.setVisibility(View.VISIBLE);
                } else {
                    txtEmptyUserApps.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String error) {
                profileProgress.setVisibility(View.GONE);
                txtEmptyUserApps.setVisibility(View.VISIBLE);
            }
        });
    }

    private static class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.AppViewHolder> {
        private final List<ApplicationRecord> list;

        public ApplicationAdapter(List<ApplicationRecord> list) {
            this.list = list;
        }

        public void updateList(List<ApplicationRecord> newList) {
            this.list.clear();
            this.list.addAll(newList);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_application, parent, false);
            return new AppViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
            ApplicationRecord rec = list.get(position);
            holder.txtSport.setText(rec.getSport());
            holder.txtDetails.setText("Position: " + rec.getPosition() + " • " + rec.getGender() + " • " + rec.getSchool());
            holder.txtDate.setText("Submitted: " + (rec.getSubmittedAt() != null ? rec.getSubmittedAt() : "Recently"));

            String status = rec.getStatus() != null ? rec.getStatus() : "Pending";
            holder.txtStatus.setText(status);

            if ("Approved".equalsIgnoreCase(status)) {
                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_approved);
                holder.txtStatus.setTextColor(0xFF10B981);
            } else {
                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_pending);
                holder.txtStatus.setTextColor(0xFFF59E0B);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class AppViewHolder extends RecyclerView.ViewHolder {
            TextView txtSport, txtStatus, txtDetails, txtDate;

            public AppViewHolder(@NonNull View itemView) {
                super(itemView);
                txtSport = itemView.findViewById(R.id.txtAppSport);
                txtStatus = itemView.findViewById(R.id.txtAppStatus);
                txtDetails = itemView.findViewById(R.id.txtAppDetails);
                txtDate = itemView.findViewById(R.id.txtAppDate);
            }
        }
    }
}
