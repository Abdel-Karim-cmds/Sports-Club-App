package com.example.linearlayout2;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linearlayout2.firebase.FirebaseManager;
import com.example.linearlayout2.models.Club;
import com.example.linearlayout2.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboardActivity extends AppCompatActivity {

    private CardView cardRegisterClub, cardCreateUserAccount;
    private Button btnAdminSignOut;
    private RecyclerView recyclerClubs;
    private ProgressBar adminProgress;
    private TextView txtEmptyClubs;
    private ClubAdapter adapter;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        firebaseManager = FirebaseManager.getInstance();

        String uid = firebaseManager.getCurrentUserId();
        firebaseManager.getUserByUid(uid, new FirebaseManager.DataCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (user != null && !"ADMIN".equalsIgnoreCase(user.getUserType())) {
                    Toast.makeText(AdminDashboardActivity.this, "Access Denied: Admin privileges required.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AdminDashboardActivity.this, userDashboard.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onError(String error) {
                if (!"admin@usiu.ac.ke".equalsIgnoreCase(firebaseManager.getCurrentUserEmail())) {
                    Toast.makeText(AdminDashboardActivity.this, "Access Denied: Admin privileges required.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AdminDashboardActivity.this, userDashboard.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        cardRegisterClub = findViewById(R.id.cardRegisterClub);
        cardCreateUserAccount = findViewById(R.id.cardCreateUserAccount);
        btnAdminSignOut = findViewById(R.id.btnAdminSignOut);
        recyclerClubs = findViewById(R.id.recyclerClubs);
        adminProgress = findViewById(R.id.adminProgress);
        txtEmptyClubs = findViewById(R.id.txtEmptyClubs);

        btnAdminSignOut.setOnClickListener(v -> {
            firebaseManager.signOut();
            Toast.makeText(this, "Admin Signed Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AdminDashboardActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        cardRegisterClub.setOnClickListener(v -> showRegisterClubDialog());

        if (cardCreateUserAccount != null) {
            cardCreateUserAccount.setOnClickListener(v -> showCreateUserAccountDialog());
        }

        recyclerClubs.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClubAdapter(new ArrayList<>());
        recyclerClubs.setAdapter(adapter);

        loadClubs();
    }

    private void loadClubs() {
        adminProgress.setVisibility(View.VISIBLE);
        firebaseManager.getAllClubs(new FirebaseManager.DataCallback<List<Club>>() {
            @Override
            public void onSuccess(List<Club> data) {
                adminProgress.setVisibility(View.GONE);
                adapter.updateList(data);

                if (data.isEmpty()) {
                    txtEmptyClubs.setVisibility(View.VISIBLE);
                } else {
                    txtEmptyClubs.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String error) {
                adminProgress.setVisibility(View.GONE);
                txtEmptyClubs.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showRegisterClubDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Register New Sports Club");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_register_club, null);
        builder.setView(dialogView);

        EditText editClubName = dialogView.findViewById(R.id.editDialogClubName);
        EditText editSportCategory = dialogView.findViewById(R.id.editDialogSportCategory);
        Spinner spinnerManagerEmail = dialogView.findViewById(R.id.spinnerDialogManagerEmail);
        EditText editDesc = dialogView.findViewById(R.id.editDialogClubDesc);

        List<String> emailList = new ArrayList<>();
        firebaseManager.getAllUsers(new FirebaseManager.DataCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                for (User u : users) {
                    if (u.getEmail() != null && !u.getEmail().isEmpty() && !emailList.contains(u.getEmail())) {
                        emailList.add(u.getEmail());
                    }
                }
                if (emailList.isEmpty()) {
                    emailList.add("manager@usiu.ac.ke");
                    emailList.add("basketball@usiu.ac.ke");
                    emailList.add("football@usiu.ac.ke");
                }
                ArrayAdapter<String> emailAdapter = new ArrayAdapter<>(AdminDashboardActivity.this, android.R.layout.simple_spinner_dropdown_item, emailList);
                spinnerManagerEmail.setAdapter(emailAdapter);
            }

            @Override
            public void onError(String error) {
                emailList.add("manager@usiu.ac.ke");
                emailList.add("basketball@usiu.ac.ke");
                emailList.add("football@usiu.ac.ke");
                ArrayAdapter<String> emailAdapter = new ArrayAdapter<>(AdminDashboardActivity.this, android.R.layout.simple_spinner_dropdown_item, emailList);
                spinnerManagerEmail.setAdapter(emailAdapter);
            }
        });

        builder.setPositiveButton("Register Club", (dialog, which) -> {
            String name = editClubName.getText().toString().trim();
            String sport = editSportCategory.getText().toString().trim();
            String desc = editDesc.getText().toString().trim();
            String managerEmail = spinnerManagerEmail.getSelectedItem() != null ? spinnerManagerEmail.getSelectedItem().toString() : "";

            if (name.isEmpty() || sport.isEmpty() || managerEmail.isEmpty()) {
                Toast.makeText(AdminDashboardActivity.this, "Please fill in club name, sport category, and manager email", Toast.LENGTH_SHORT).show();
                return;
            }

            Club club = new Club(null, name, sport, desc, managerEmail, "Active", System.currentTimeMillis());
            firebaseManager.registerClub(club, new FirebaseManager.DataCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    Toast.makeText(AdminDashboardActivity.this, "Club Registered Successfully!", Toast.LENGTH_SHORT).show();
                    loadClubs();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(AdminDashboardActivity.this, "Failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void showCreateUserAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Provision User / Manager Account");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_create_user, null);
        builder.setView(dialogView);

        EditText editFullName = dialogView.findViewById(R.id.editUserFullName);
        Spinner spinnerType = dialogView.findViewById(R.id.spinnerUserType);
        TextView txtAssignClubLabel = dialogView.findViewById(R.id.txtAssignClubLabel);
        Spinner spinnerAssignClub = dialogView.findViewById(R.id.spinnerAssignClub);
        EditText editEmail = dialogView.findViewById(R.id.editUserEmail);
        EditText editPassword = dialogView.findViewById(R.id.editUserPassword);

        String[] roles = new String[]{"CLUB_MANAGER", "ADMIN"};
        ArrayAdapter<String> adapterRoles = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, roles);
        spinnerType.setAdapter(adapterRoles);

        List<String> clubList = new ArrayList<>();
        firebaseManager.getAllClubs(new FirebaseManager.DataCallback<List<Club>>() {
            @Override
            public void onSuccess(List<Club> clubs) {
                for (Club c : clubs) {
                    if (c.getName() != null && !c.getName().isEmpty() && !clubList.contains(c.getName())) {
                        clubList.add(c.getName());
                    }
                }
                if (clubList.isEmpty()) {
                    clubList.add("USIU Basketball Club");
                    clubList.add("USIU Football Club");
                    clubList.add("USIU Spikers Volleyball");
                }
                ArrayAdapter<String> clubAdapter = new ArrayAdapter<>(AdminDashboardActivity.this, android.R.layout.simple_spinner_dropdown_item, clubList);
                spinnerAssignClub.setAdapter(clubAdapter);
            }

            @Override
            public void onError(String error) {
                clubList.add("USIU Basketball Club");
                clubList.add("USIU Football Club");
                clubList.add("USIU Spikers Volleyball");
                ArrayAdapter<String> clubAdapter = new ArrayAdapter<>(AdminDashboardActivity.this, android.R.layout.simple_spinner_dropdown_item, clubList);
                spinnerAssignClub.setAdapter(clubAdapter);
            }
        });

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString();
                if ("CLUB_MANAGER".equalsIgnoreCase(selectedType)) {
                    txtAssignClubLabel.setVisibility(View.VISIBLE);
                    spinnerAssignClub.setVisibility(View.VISIBLE);
                } else {
                    txtAssignClubLabel.setVisibility(View.GONE);
                    spinnerAssignClub.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        builder.setPositiveButton("Create User", (dialog, which) -> {
            String name = editFullName.getText().toString().trim();
            String userType = spinnerType.getSelectedItem().toString();
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.length() < 6) {
                Toast.makeText(AdminDashboardActivity.this, "Please fill all fields (password min 6 chars)", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> userData = new HashMap<>();
            userData.put("name", name);
            userData.put("email", email);
            userData.put("userType", userType);
            userData.put("createdAt", System.currentTimeMillis());

            if ("CLUB_MANAGER".equalsIgnoreCase(userType) && spinnerAssignClub.getSelectedItem() != null) {
                String assignedClub = spinnerAssignClub.getSelectedItem().toString();
                userData.put("assignedClub", assignedClub);
            }

            // Write user profile to Firestore
            String dummyUid = "managed_" + System.currentTimeMillis();
            userData.put("uid", dummyUid);
            firebaseManager.getDb().collection("users").document(dummyUid).set(userData);

            Toast.makeText(AdminDashboardActivity.this, userType + " Account Provisioned in Firestore!", Toast.LENGTH_LONG).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private static class ClubAdapter extends RecyclerView.Adapter<ClubAdapter.ClubViewHolder> {
        private final List<Club> list;

        public ClubAdapter(List<Club> list) {
            this.list = list;
        }

        public void updateList(List<Club> newList) {
            this.list.clear();
            this.list.addAll(newList);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ClubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_club, parent, false);
            return new ClubViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ClubViewHolder holder, int position) {
            Club c = list.get(position);
            holder.txtName.setText(c.getName());
            holder.txtDesc.setText(c.getSport() + " • " + (c.getDescription() != null ? c.getDescription() : "Active Club"));
            holder.txtManager.setText("Manager: " + (c.getManagerEmail() != null ? c.getManagerEmail() : "Unassigned"));

            if ("Basketball".equalsIgnoreCase(c.getSport())) {
                holder.imgIcon.setImageResource(R.drawable.ic_basketball);
            } else if ("Football".equalsIgnoreCase(c.getSport())) {
                holder.imgIcon.setImageResource(R.drawable.ic_soccer);
            } else if ("Volleyball".equalsIgnoreCase(c.getSport())) {
                holder.imgIcon.setImageResource(R.drawable.ic_volleyball);
            } else {
                holder.imgIcon.setImageResource(R.drawable.ic_trophy);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class ClubViewHolder extends RecyclerView.ViewHolder {
            TextView txtName, txtDesc, txtManager, txtStatus;
            ImageView imgIcon;

            public ClubViewHolder(@NonNull View itemView) {
                super(itemView);
                txtName = itemView.findViewById(R.id.txtClubName);
                txtDesc = itemView.findViewById(R.id.txtClubSportDesc);
                txtManager = itemView.findViewById(R.id.txtClubManagerEmail);
                txtStatus = itemView.findViewById(R.id.txtClubStatus);
                imgIcon = itemView.findViewById(R.id.imgClubSportIcon);
            }
        }
    }
}
