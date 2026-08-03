package com.example.linearlayout2;

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

public class ManageApplicationsActivity extends AppCompatActivity {

    private RecyclerView recyclerManageApps;
    private ProgressBar manageAppsProgress;
    private TextView txtEmptyManageApps;
    private ManageAdapter adapter;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_applications);

        firebaseManager = FirebaseManager.getInstance();

        // Access control check for ATHLETE role
        String uid = firebaseManager.getCurrentUserId();
        firebaseManager.getUserByUid(uid, new FirebaseManager.DataCallback<com.example.linearlayout2.models.User>() {
            @Override
            public void onSuccess(com.example.linearlayout2.models.User user) {
                if (user != null && "ATHLETE".equalsIgnoreCase(user.getUserType())) {
                    Toast.makeText(ManageApplicationsActivity.this, "Access Denied: Regular athletes do not have access to club management.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onError(String error) {
            }
        });

        recyclerManageApps = findViewById(R.id.recyclerManageApps);
        manageAppsProgress = findViewById(R.id.manageAppsProgress);
        txtEmptyManageApps = findViewById(R.id.txtEmptyManageApps);

        recyclerManageApps.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ManageAdapter(new ArrayList<>());
        recyclerManageApps.setAdapter(adapter);

        loadPendingApplications();
    }

    private void loadPendingApplications() {
        manageAppsProgress.setVisibility(View.VISIBLE);
        firebaseManager.getAllApplications(new FirebaseManager.DataCallback<List<ApplicationRecord>>() {
            @Override
            public void onSuccess(List<ApplicationRecord> data) {
                manageAppsProgress.setVisibility(View.GONE);

                String managerSport = firebaseManager.getManagerSport();
                
                // Filter only Pending or Under Review applications AND matching manager's sport
                List<ApplicationRecord> pendingList = new ArrayList<>();
                for (ApplicationRecord rec : data) {
                    boolean isPending = rec.getStatus() == null || "Pending".equalsIgnoreCase(rec.getStatus()) || "Under Review".equalsIgnoreCase(rec.getStatus());
                    boolean matchesSport = "All".equals(managerSport) || (rec.getSport() != null && rec.getSport().equalsIgnoreCase(managerSport));
                    if (isPending && matchesSport) {
                        pendingList.add(rec);
                    }
                }

                adapter.updateList(pendingList);
                if (pendingList.isEmpty()) {
                    txtEmptyManageApps.setVisibility(View.VISIBLE);
                } else {
                    txtEmptyManageApps.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String error) {
                manageAppsProgress.setVisibility(View.GONE);
                txtEmptyManageApps.setVisibility(View.VISIBLE);
            }
        });
    }

    private class ManageAdapter extends RecyclerView.Adapter<ManageAdapter.ManageViewHolder> {
        private final List<ApplicationRecord> list;

        public ManageAdapter(List<ApplicationRecord> list) {
            this.list = list;
        }

        public void updateList(List<ApplicationRecord> newList) {
            this.list.clear();
            this.list.addAll(newList);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ManageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_application, parent, false);
            return new ManageViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ManageViewHolder holder, int position) {
            ApplicationRecord rec = list.get(position);
            holder.txtName.setText(rec.getPlayerName());
            holder.txtSport.setText(rec.getSport());
            String studentIdStr = rec.getStudentId() != null && !rec.getStudentId().isEmpty() ? "ID: " + rec.getStudentId() + " • " : "";
            holder.txtDetails.setText(studentIdStr + "Position: " + rec.getPosition() + " • " + rec.getGender() + (rec.getSchool() != null ? " • " + rec.getSchool() : ""));
            holder.txtDate.setText("Submitted: " + (rec.getSubmittedAt() != null ? rec.getSubmittedAt() : "Recently"));

            holder.btnApprove.setOnClickListener(v -> {
                firebaseManager.updateApplicationStatus(rec.getId(), "Approved", rec, new FirebaseManager.DataCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        Toast.makeText(ManageApplicationsActivity.this, rec.getPlayerName() + " Approved & Added to Team Roster!", Toast.LENGTH_SHORT).show();
                        loadPendingApplications();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(ManageApplicationsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            });

            holder.btnReject.setOnClickListener(v -> {
                firebaseManager.updateApplicationStatus(rec.getId(), "Rejected", rec, new FirebaseManager.DataCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        Toast.makeText(ManageApplicationsActivity.this, rec.getPlayerName() + " Application Rejected", Toast.LENGTH_SHORT).show();
                        loadPendingApplications();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(ManageApplicationsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ManageViewHolder extends RecyclerView.ViewHolder {
            TextView txtName, txtSport, txtDetails, txtDate;
            Button btnApprove, btnReject;

            public ManageViewHolder(@NonNull View itemView) {
                super(itemView);
                txtName = itemView.findViewById(R.id.txtApplicantName);
                txtSport = itemView.findViewById(R.id.txtApplicantSport);
                txtDetails = itemView.findViewById(R.id.txtApplicantDetails);
                txtDate = itemView.findViewById(R.id.txtApplicantDate);
                btnApprove = itemView.findViewById(R.id.btnApproveApplicant);
                btnReject = itemView.findViewById(R.id.btnRejectApplicant);
            }
        }
    }
}
