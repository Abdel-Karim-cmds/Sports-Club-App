package com.example.linearlayout2;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linearlayout2.firebase.FirebaseManager;
import com.example.linearlayout2.models.Player;

import java.util.ArrayList;
import java.util.List;

public class ViewPlayersActivity extends AppCompatActivity {

    private String selectedSport = "All";
    private RecyclerView recyclerPlayers;
    private ProgressBar rosterProgress;
    private TextView txtEmptyRoster, txtRosterTitle;
    private EditText editSearchPlayer;
    private PlayerAdapter adapter;
    private final List<Player> fullPlayerList = new ArrayList<>();
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_players);

        firebaseManager = FirebaseManager.getInstance();

        if (getIntent() != null && getIntent().hasExtra("EXTRA_SPORT_NAME")) {
            selectedSport = getIntent().getStringExtra("EXTRA_SPORT_NAME");
        }

        txtRosterTitle = findViewById(R.id.txtRosterTitle);
        if (txtRosterTitle != null && selectedSport != null) {
            txtRosterTitle.setText(selectedSport + " Roster");
        }

        recyclerPlayers = findViewById(R.id.recyclerPlayers);
        rosterProgress = findViewById(R.id.rosterProgress);
        txtEmptyRoster = findViewById(R.id.txtEmptyRoster);
        editSearchPlayer = findViewById(R.id.editSearchPlayer);

        recyclerPlayers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlayerAdapter(new ArrayList<>());
        recyclerPlayers.setAdapter(adapter);

        editSearchPlayer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPlayers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadPlayers();
    }

    private void loadPlayers() {
        rosterProgress.setVisibility(View.VISIBLE);
        firebaseManager.getPlayers(selectedSport, new FirebaseManager.DataCallback<List<Player>>() {
            @Override
            public void onSuccess(List<Player> data) {
                rosterProgress.setVisibility(View.GONE);
                fullPlayerList.clear();
                fullPlayerList.addAll(data);
                adapter.updateList(fullPlayerList);

                if (fullPlayerList.isEmpty()) {
                    txtEmptyRoster.setVisibility(View.VISIBLE);
                } else {
                    txtEmptyRoster.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String error) {
                rosterProgress.setVisibility(View.GONE);
                txtEmptyRoster.setVisibility(View.VISIBLE);
            }
        });
    }

    private void filterPlayers(String query) {
        if (query.trim().isEmpty()) {
            adapter.updateList(fullPlayerList);
            return;
        }
        String lower = query.toLowerCase().trim();
        List<Player> filtered = new ArrayList<>();
        for (Player p : fullPlayerList) {
            if ((p.getName() != null && p.getName().toLowerCase().contains(lower)) ||
                (p.getPosition() != null && p.getPosition().toLowerCase().contains(lower)) ||
                (p.getSchool() != null && p.getSchool().toLowerCase().contains(lower))) {
                filtered.add(p);
            }
        }
        adapter.updateList(filtered);
    }

    private static class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {
        private final List<Player> players;

        public PlayerAdapter(List<Player> players) {
            this.players = players;
        }

        public void updateList(List<Player> newList) {
            this.players.clear();
            this.players.addAll(newList);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
            return new PlayerViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
            Player p = players.get(position);
            holder.txtName.setText(p.getName());
            holder.txtSub.setText(p.getPosition() + " • " + p.getSchool());
            holder.txtStats.setText("Stats/Bio: " + (p.getStats() != null ? p.getStats() : p.getGender() + " Squad"));
            holder.txtSportBadge.setText(p.getSport());
        }

        @Override
        public int getItemCount() {
            return players.size();
        }

        static class PlayerViewHolder extends RecyclerView.ViewHolder {
            TextView txtName, txtSub, txtStats, txtSportBadge;

            public PlayerViewHolder(@NonNull View itemView) {
                super(itemView);
                txtName = itemView.findViewById(R.id.txtPlayerName);
                txtSub = itemView.findViewById(R.id.txtPlayerSub);
                txtStats = itemView.findViewById(R.id.txtPlayerStats);
                txtSportBadge = itemView.findViewById(R.id.txtPlayerSportBadge);
            }
        }
    }
}
