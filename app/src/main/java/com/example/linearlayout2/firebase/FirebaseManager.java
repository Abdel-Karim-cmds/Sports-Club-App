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

    // ==========================================
    // SEED SAMPLE DATA IF FIRESTORE IS EMPTY
    // ==========================================

    public void seedInitialDataIfEmpty() {
        db.collection("users").limit(1).get().addOnSuccessListener(snapshot -> {
            if (snapshot.isEmpty()) {
                seedUsers();
            }
        });

        db.collection("clubs").limit(1).get().addOnSuccessListener(snapshot -> {
            if (snapshot.isEmpty()) {
                seedClubs();
            }
        });

        db.collection("players").limit(1).get().addOnSuccessListener(snapshot -> {
            if (snapshot.isEmpty()) {
                seedPlayers();
            }
        });

        db.collection("fixtures").limit(1).get().addOnSuccessListener(snapshot -> {
            if (snapshot.isEmpty()) {
                seedFixtures();
            }
        });

        db.collection("announcements").limit(1).get().addOnSuccessListener(snapshot -> {
            if (snapshot.isEmpty()) {
                seedAnnouncements();
            }
        });

        ensureDefaultAccountsExist();
    }

    private void seedUsers() {
        List<User> sampleUsers = new ArrayList<>();
        sampleUsers.add(new User("admin_seed_uid", "System Admin", "admin@usiu.ac.ke", "ADMIN", System.currentTimeMillis()));
        sampleUsers.add(new User("manager_basketball_uid", "Basketball Manager", "basketball@usiu.ac.ke", "CLUB_MANAGER", System.currentTimeMillis()));
        sampleUsers.add(new User("manager_football_uid", "Football Manager", "football@usiu.ac.ke", "CLUB_MANAGER", System.currentTimeMillis()));
        sampleUsers.add(new User("manager_volleyball_uid", "Volleyball Manager", "volleyball@usiu.ac.ke", "CLUB_MANAGER", System.currentTimeMillis()));
        sampleUsers.add(new User("manager_hockey_uid", "Hockey Manager", "hockey@usiu.ac.ke", "CLUB_MANAGER", System.currentTimeMillis()));
        sampleUsers.add(new User("manager_tennis_uid", "Tennis Manager", "tennis@usiu.ac.ke", "CLUB_MANAGER", System.currentTimeMillis()));
        sampleUsers.add(new User("manager_swimming_uid", "Swimming Manager", "swimming@usiu.ac.ke", "CLUB_MANAGER", System.currentTimeMillis()));
        sampleUsers.add(new User("manager_default_uid", "USIU Club Manager", "manager@usiu.ac.ke", "CLUB_MANAGER", System.currentTimeMillis()));
        sampleUsers.add(new User("athlete_seed_uid", "Student Athlete", "athlete@usiu.ac.ke", "ATHLETE", System.currentTimeMillis()));

        for (User u : sampleUsers) {
            db.collection("users").document(u.getUid()).set(u);
        }
        Log.d(TAG, "Sample users table seeded in Firestore");
    }

    private void ensureDefaultAccountsExist() {
        // Create Admin user in Auth if needed
        provisionAccount("admin@usiu.ac.ke", "admin123", "System Admin", "ADMIN");

        // Create Sport Manager users in Auth if needed
        provisionAccount("basketball@usiu.ac.ke", "manager123", "Basketball Manager", "CLUB_MANAGER");
        provisionAccount("football@usiu.ac.ke", "manager123", "Football Manager", "CLUB_MANAGER");
        provisionAccount("volleyball@usiu.ac.ke", "manager123", "Volleyball Manager", "CLUB_MANAGER");
        provisionAccount("hockey@usiu.ac.ke", "manager123", "Hockey Manager", "CLUB_MANAGER");
        provisionAccount("tennis@usiu.ac.ke", "manager123", "Tennis Manager", "CLUB_MANAGER");
        provisionAccount("swimming@usiu.ac.ke", "manager123", "Swimming Manager", "CLUB_MANAGER");
        provisionAccount("manager@usiu.ac.ke", "manager123", "USIU Club Manager", "CLUB_MANAGER");
    }

    private void provisionAccount(String email, String password, String name, String userType) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if (authResult.getUser() != null) {
                        String uid = authResult.getUser().getUid();
                        User u = new User(uid, name, email, userType, System.currentTimeMillis());
                        db.collection("users").document(uid).set(u);
                        Log.d(TAG, "Provisioned account: " + email);
                    }
                })
                .addOnFailureListener(e -> {
                    // Account might already exist, update Firestore record by querying by email
                    db.collection("users").whereEqualTo("email", email).get().addOnSuccessListener(snapshots -> {
                        if (snapshots.isEmpty()) {
                            String docId = "user_" + email.replace("@", "_").replace(".", "_");
                            User u = new User(docId, name, email, userType, System.currentTimeMillis());
                            db.collection("users").document(docId).set(u);
                        }
                    });
                });
    }

    private void seedClubs() {
        List<Club> sampleClubs = new ArrayList<>();
        sampleClubs.add(new Club("c1", "USIU Basketball Club", "Basketball", "Premier varsity basketball program competing in regional leagues.", "basketball@usiu.ac.ke", "Active", System.currentTimeMillis()));
        sampleClubs.add(new Club("c2", "USIU Football Club", "Football", "Intercollegiate football team and academy squad.", "football@usiu.ac.ke", "Active", System.currentTimeMillis()));
        sampleClubs.add(new Club("c3", "USIU Spikers Volleyball", "Volleyball", "Men's and women's competitive court & beach volleyball.", "volleyball@usiu.ac.ke", "Active", System.currentTimeMillis()));
        sampleClubs.add(new Club("c4", "USIU Field Hockey Club", "Field Hockey", "Established hockey squad with regular weekend fixtures.", "hockey@usiu.ac.ke", "Active", System.currentTimeMillis()));
        sampleClubs.add(new Club("c5", "USIU Tennis Academy", "Tennis", "Singles and doubles competitive ladder and coaching.", "tennis@usiu.ac.ke", "Active", System.currentTimeMillis()));
        sampleClubs.add(new Club("c6", "USIU Aquatics Swim Club", "Swimming", "Competitive swimming squad for freestyle, relay and meets.", "swimming@usiu.ac.ke", "Active", System.currentTimeMillis()));

        for (Club c : sampleClubs) {
            db.collection("clubs").document(c.getId()).set(c);
        }
        Log.d(TAG, "Sample clubs seeded");
    }

    private void seedPlayers() {
        List<Player> samplePlayers = new ArrayList<>();
        samplePlayers.add(new Player("p1", "Marcus Johnson", "Basketball", "Point Guard", "Male", "USIU Basketball Club", "+254 700-0101", "18.5 PPG, 6.2 APG"));
        samplePlayers.add(new Player("p2", "Elena Rostova", "Basketball", "Shooting Guard", "Female", "USIU Basketball Club", "+254 700-0102", "14.2 PPG, 4.1 RPG"));
        samplePlayers.add(new Player("p4", "Carlos Silva", "Football", "Striker", "Male", "USIU Football Club", "+254 700-0104", "12 Goals in 10 Games"));
        samplePlayers.add(new Player("p6", "Aisha Abubakar", "Volleyball", "Outside Hitter", "Female", "USIU Volleyball Club", "+254 700-0106", "154 Kills, 28 Aces"));

        for (Player p : samplePlayers) {
            db.collection("players").document(p.getId()).set(p);
        }
        Log.d(TAG, "Sample players seeded");
    }

    private void seedFixtures() {
        List<Fixture> sampleFixtures = new ArrayList<>();
        sampleFixtures.add(new Fixture("f1", "Basketball", "USIU Tigers", "Tigers Varsity", "84", "78", "Yesterday", "Main Arena", "Finished"));
        sampleFixtures.add(new Fixture("f2", "Basketball", "USIU Tigers", "Eagles Club", "0", "0", "Tomorrow, 6:00 PM", "Sports Complex Court 1", "Upcoming"));
        sampleFixtures.add(new Fixture("f3", "Football", "USIU Strikers", "City United", "3", "1", "3 days ago", "USIU Central Pitch", "Finished"));

        for (Fixture f : sampleFixtures) {
            db.collection("fixtures").document(f.getId()).set(f);
        }
        Log.d(TAG, "Sample fixtures seeded");
    }

    private void seedAnnouncements() {
        List<Announcement> sampleAnnouncements = new ArrayList<>();
        sampleAnnouncements.add(new Announcement("a1", "Annual Club Tryouts 2026", "Registration is open for Basketball, Football, Volleyball, Hockey, Tennis, and Swimming! Fill out your application in the app now.", "Today", "Tryouts", "Club Manager"));
        sampleAnnouncements.add(new Announcement("a2", "Inter-University Tournament Finals", "Come support USIU Tigers this Saturday at 6:00 PM in the Main Arena!", "2 days ago", "Match", "Sports Board"));

        for (Announcement a : sampleAnnouncements) {
            db.collection("announcements").document(a.getId()).set(a);
        }
        Log.d(TAG, "Sample announcements seeded");
    }

    // ==========================================
    // USERS TABLE OPERATIONS
    // ==========================================

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

    // ==========================================
    // ADMIN FUNCTIONS: CLUBS MANAGEMENT
    // ==========================================

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
            if (clubs.isEmpty()) clubs = getLocalFallbackClubs();
            callback.onSuccess(clubs);
        }).addOnFailureListener(e -> callback.onSuccess(getLocalFallbackClubs()));
    }

    private List<Club> getLocalFallbackClubs() {
        List<Club> list = new ArrayList<>();
        list.add(new Club("c1", "USIU Basketball Club", "Basketball", "Premier varsity basketball squad.", "basketball@usiu.ac.ke", "Active", System.currentTimeMillis()));
        list.add(new Club("c2", "USIU Football Club", "Football", "Intercollegiate football team.", "football@usiu.ac.ke", "Active", System.currentTimeMillis()));
        list.add(new Club("c3", "USIU Spikers Volleyball", "Volleyball", "Men's and women's court volleyball.", "volleyball@usiu.ac.ke", "Active", System.currentTimeMillis()));
        return list;
    }

    // ==========================================
    // CLUB MANAGER FUNCTIONS: APPLICATIONS & FIXTURES
    // ==========================================

    public void getAllApplications(DataCallback<List<ApplicationRecord>> callback) {
        db.collection("applications").get().addOnSuccessListener(snapshots -> {
            List<ApplicationRecord> records = new ArrayList<>();
            for (QueryDocumentSnapshot doc : snapshots) {
                ApplicationRecord rec = doc.toObject(ApplicationRecord.class);
                if (rec.getId() == null) rec.setId(doc.getId());
                records.add(rec);
            }
            if (records.isEmpty()) records = getLocalFallbackApplications();
            callback.onSuccess(records);
        }).addOnFailureListener(e -> callback.onSuccess(getLocalFallbackApplications()));
    }

    private List<ApplicationRecord> getLocalFallbackApplications() {
        List<ApplicationRecord> list = new ArrayList<>();
        list.add(new ApplicationRecord("app1", "user1", "James Kimani", "Basketball", "Point Guard", "Male", "School of Technology", "Pending", "Today"));
        list.add(new ApplicationRecord("app2", "user2", "Sarah Odhiambo", "Football", "Striker", "Female", "School of Business", "Pending", "Yesterday"));
        list.add(new ApplicationRecord("app3", "user3", "Brian Ochieng", "Volleyball", "Outside Hitter", "Male", "School of Health Sciences", "Pending", "2 days ago"));
        return list;
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

    // ==========================================
    // GENERAL PLAYERS & DATA QUERIES
    // ==========================================

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
            if (players.isEmpty()) players = getLocalFallbackPlayers(sport);
            callback.onSuccess(players);
        }).addOnFailureListener(e -> callback.onSuccess(getLocalFallbackPlayers(sport)));
    }

    private List<Player> getLocalFallbackPlayers(String sport) {
        List<Player> list = new ArrayList<>();
        list.add(new Player("p1", "Marcus Johnson", "Basketball", "Point Guard", "Male", "USIU Basketball Club", "+254 700-0101", "18.5 PPG, 6.2 APG"));
        list.add(new Player("p2", "Elena Rostova", "Basketball", "Shooting Guard", "Female", "USIU Basketball Club", "+254 700-0102", "14.2 PPG, 4.1 RPG"));
        list.add(new Player("p4", "Carlos Silva", "Football", "Striker", "Male", "USIU Football Club", "+254 700-0104", "12 Goals in 10 Games"));

        if (sport == null || sport.equals("All")) return list;
        List<Player> filtered = new ArrayList<>();
        for (Player p : list) {
            if (p.getSport().equalsIgnoreCase(sport)) filtered.add(p);
        }
        return filtered.isEmpty() ? list : filtered;
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
            if (fixtures.isEmpty()) fixtures = getLocalFallbackFixtures(sport);
            callback.onSuccess(fixtures);
        }).addOnFailureListener(e -> callback.onSuccess(getLocalFallbackFixtures(sport)));
    }

    private List<Fixture> getLocalFallbackFixtures(String sport) {
        List<Fixture> list = new ArrayList<>();
        list.add(new Fixture("f1", "Basketball", "USIU Tigers", "Tigers Varsity", "84", "78", "Yesterday", "Main Arena", "Finished"));
        list.add(new Fixture("f2", "Basketball", "USIU Tigers", "Eagles Club", "0", "0", "Tomorrow, 6:00 PM", "Sports Complex Court 1", "Upcoming"));
        list.add(new Fixture("f3", "Football", "USIU Strikers", "City United", "3", "1", "3 days ago", "USIU Central Pitch", "Finished"));

        if (sport == null || sport.equals("All")) return list;
        List<Fixture> filtered = new ArrayList<>();
        for (Fixture f : list) {
            if (f.getSport().equalsIgnoreCase(sport)) filtered.add(f);
        }
        return filtered.isEmpty() ? list : filtered;
    }

    public void getAnnouncements(DataCallback<List<Announcement>> callback) {
        db.collection("announcements").get().addOnSuccessListener(snapshots -> {
            List<Announcement> announcements = new ArrayList<>();
            for (QueryDocumentSnapshot doc : snapshots) {
                Announcement a = doc.toObject(Announcement.class);
                if (a.getId() == null) a.setId(doc.getId());
                announcements.add(a);
            }
            if (announcements.isEmpty()) announcements = getLocalFallbackAnnouncements();
            callback.onSuccess(announcements);
        }).addOnFailureListener(e -> callback.onSuccess(getLocalFallbackAnnouncements()));
    }

    private List<Announcement> getLocalFallbackAnnouncements() {
        List<Announcement> list = new ArrayList<>();
        list.add(new Announcement("a1", "Annual Sports Club Tryouts 2026", "Registration is open for Basketball, Football, Volleyball, Hockey, Tennis, and Swimming! Apply inside the app.", "Today", "Tryouts", "Club Manager"));
        list.add(new Announcement("a2", "Inter-University Tournament Finals", "Come support USIU Tigers this Saturday at 6:00 PM in the Main Arena!", "2 days ago", "Match", "Sports Board"));
        return list;
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
