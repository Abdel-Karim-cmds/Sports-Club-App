package com.example.linearlayout2.firebase;

import android.util.Log;

import com.example.linearlayout2.models.Announcement;
import com.example.linearlayout2.models.ApplicationRecord;
import com.example.linearlayout2.models.Club;
import com.example.linearlayout2.models.Fixture;
import com.example.linearlayout2.models.Player;
import com.example.linearlayout2.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FirebaseManager {

    private static final String TAG = "FirebaseManager";
    private static FirebaseManager instance;

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    private FirebaseManager() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public String getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : "";
    }

    public String getCurrentUserEmail() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getEmail() : "Guest User";
    }

    public void signOut() {
        auth.signOut();
    }

    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }


    public void saveUser(User user, DataCallback<Void> callback) {
        if (user.getUid() == null || user.getUid().isEmpty()) {
            if (callback != null) callback.onError("User UID cannot be empty");
            return;
        }
        db.collection("users").document(user.getUid()).set(user)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onSuccess(null);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onError(e.getMessage());
                });
    }

    public void getUserByUid(String uid, DataCallback<User> callback) {
        if (uid == null || uid.isEmpty()) {
            if (callback != null) callback.onError("UID is empty");
            return;
        }
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        User user = doc.toObject(User.class);
                        if (user != null && user.getUid() == null) user.setUid(doc.getId());
                        if (callback != null) callback.onSuccess(user);
                    } else {
                        if (callback != null) callback.onError("User not found");
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onError(e.getMessage());
                });
    }

    public void getAllUsers(DataCallback<List<User>> callback) {
        db.collection("users").get()
                .addOnSuccessListener(snapshots -> {
                    List<User> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        User u = doc.toObject(User.class);
                        if (u.getUid() == null) u.setUid(doc.getId());
                        list.add(u);
                    }
                    if (callback != null) callback.onSuccess(list);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onError(e.getMessage());
                });
    }


    public void registerClub(Club club, DataCallback<String> callback) {
        if (club.getCreatedAt() == 0) club.setCreatedAt(System.currentTimeMillis());
        if (club.getStatus() == null) club.setStatus("Active");

        db.collection("clubs").add(club).addOnSuccessListener(docRef -> {
            club.setId(docRef.getId());
            docRef.update("id", docRef.getId());
            callback.onSuccess(docRef.getId());
        }).addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getAllClubs(DataCallback<List<Club>> callback) {
        db.collection("clubs").get().addOnSuccessListener(snapshots -> {
            List<Club> clubs = new ArrayList<>();
            for (QueryDocumentSnapshot doc : snapshots) {
                Club club = doc.toObject(Club.class);
                if (club.getId() == null) club.setId(doc.getId());
                clubs.add(club);
            }
            callback.onSuccess(clubs);
        }).addOnFailureListener(e -> callback.onError(e.getMessage()));
    }


    public void getAllApplications(DataCallback<List<ApplicationRecord>> callback) {
        db.collection("applications").get().addOnSuccessListener(snapshots -> {
            List<ApplicationRecord> records = new ArrayList<>();
            for (QueryDocumentSnapshot doc : snapshots) {
                ApplicationRecord rec = doc.toObject(ApplicationRecord.class);
                if (rec.getId() == null) rec.setId(doc.getId());
                records.add(rec);
            }
            callback.onSuccess(records);
        }).addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void updateApplicationStatus(String appId, String status, ApplicationRecord record, DataCallback<Void> callback) {
        db.collection("applications").document(appId).update("status", status).addOnSuccessListener(aVoid -> {
            if ("Approved".equalsIgnoreCase(status) && record != null) {
                Player p = new Player(
                        appId,
                        record.getPlayerName(),
                        record.getSport(),
                        record.getPosition(),
                        record.getGender(),
                        record.getSchool() != null ? record.getSchool() : record.getSport() + " Squad",
                        "+254 700-USIU",
                        "Approved Member"
                );
                db.collection("players").document(appId).set(p);
            }
            callback.onSuccess(null);
        }).addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void createFixture(Fixture fixture, DataCallback<String> callback) {
        db.collection("fixtures").add(fixture).addOnSuccessListener(docRef -> {
            fixture.setId(docRef.getId());
            docRef.update("id", docRef.getId());
            callback.onSuccess(docRef.getId());
        }).addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void postAnnouncement(Announcement announcement, DataCallback<String> callback) {
        db.collection("announcements").add(announcement).addOnSuccessListener(docRef -> {
            announcement.setId(docRef.getId());
            docRef.update("id", docRef.getId());
            callback.onSuccess(docRef.getId());
        }).addOnFailureListener(e -> callback.onError(e.getMessage()));
    }


    public void getPlayers(String sport, DataCallback<List<Player>> callback) {
        Query query = db.collection("players");
        if (sport != null && !sport.equals("All")) {
            query = query.whereEqualTo("sport", sport);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Player> players = new ArrayList<>();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Player player = doc.toObject(Player.class);
                if (player.getId() == null) player.setId(doc.getId());
                players.add(player);
            }
            callback.onSuccess(players);
        }).addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void submitApplication(ApplicationRecord appRecord, DataCallback<String> callback) {
        String uid = getCurrentUserId();
        appRecord.setUserId(uid);
        if (appRecord.getSubmittedAt() == null) {
            appRecord.setSubmittedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date()));
        }
        if (appRecord.getStatus() == null) {
            appRecord.setStatus("Pending");
        }

        db.collection("applications").add(appRecord).addOnSuccessListener(docRef -> {
            appRecord.setId(docRef.getId());
            docRef.update("id", docRef.getId());
            callback.onSuccess(docRef.getId());
        }).addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getUserApplications(DataCallback<List<ApplicationRecord>> callback) {
        String uid = getCurrentUserId();
        db.collection("applications").whereEqualTo("userId", uid).get().addOnSuccessListener(snapshots -> {
            List<ApplicationRecord> records = new ArrayList<>();
            for (QueryDocumentSnapshot doc : snapshots) {
                ApplicationRecord rec = doc.toObject(ApplicationRecord.class);
                if (rec.getId() == null) rec.setId(doc.getId());
                records.add(rec);
            }
            callback.onSuccess(records);
        }).addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getFixtures(String sport, DataCallback<List<Fixture>> callback) {
        Query query = db.collection("fixtures");
        if (sport != null && !sport.equals("All")) {
            query = query.whereEqualTo("sport", sport);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Fixture> fixtures = new ArrayList<>();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Fixture f = doc.toObject(Fixture.class);
                if (f.getId() == null) f.setId(doc.getId());
                fixtures.add(f);
            }
            callback.onSuccess(fixtures);
        }).addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getAnnouncements(DataCallback<List<Announcement>> callback) {
        db.collection("announcements").get().addOnSuccessListener(snapshots -> {
            List<Announcement> announcements = new ArrayList<>();
            for (QueryDocumentSnapshot doc : snapshots) {
                Announcement a = doc.toObject(Announcement.class);
                if (a.getId() == null) a.setId(doc.getId());
                announcements.add(a);
            }
            callback.onSuccess(announcements);
        }).addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public String getManagerSport() {
        String email = getCurrentUserEmail();
        if (email != null) {
            String lower = email.toLowerCase();
            if (lower.contains("basketball")) return "Basketball";
            if (lower.contains("football")) return "Football";
            if (lower.contains("volleyball")) return "Volleyball";
            if (lower.contains("hockey")) return "Field Hockey";
            if (lower.contains("tennis")) return "Tennis";
            if (lower.contains("swimming") || lower.contains("swim")) return "Swimming";
        }
        return "All";
    }
}
