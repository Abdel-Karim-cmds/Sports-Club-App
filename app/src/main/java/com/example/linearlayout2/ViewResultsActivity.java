package com.example.linearlayout2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linearlayout2.firebase.FirebaseManager;
import com.example.linearlayout2.models.Fixture;

import java.util.ArrayList;
import java.util.List;

public class ViewResultsActivity extends AppCompatActivity {

    private String selectedSport = "All";
    private RecyclerView recyclerFixtures;
    private ProgressBar fixturesProgress;
    private TextView txtEmptyFixtures, txtResultsTitle;
    private FixtureAdapter adapter;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_results);

        firebaseManager = FirebaseManager.getInstance();

        if (getIntent() != null && getIntent().hasExtra("EXTRA_SPORT_NAME")) {
            selectedSport = getIntent().getStringExtra("EXTRA_SPORT_NAME");
        }

        txtResultsTitle = findViewById(R.id.txtResultsTitle);
        if (txtResultsTitle != null && selectedSport != null) {
            txtResultsTitle.setText(selectedSport + " Matches & Scores");
        }

        recyclerFixtures = findViewById(R.id.recyclerFixtures);
        fixturesProgress = findViewById(R.id.fixturesProgress);
        txtEmptyFixtures = findViewById(R.id.txtEmptyFixtures);

        recyclerFixtures.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FixtureAdapter(new ArrayList<>());
        recyclerFixtures.setAdapter(adapter);

        loadFixtures();
    }

    private void loadFixtures() {
        fixturesProgress.setVisibility(View.VISIBLE);
        firebaseManager.getFixtures(selectedSport, new FirebaseManager.DataCallback<List<Fixture>>() {
            @Override
            public void onSuccess(List<Fixture> data) {
                fixturesProgress.setVisibility(View.GONE);
                adapter.updateList(data);

                if (data.isEmpty()) {
                    txtEmptyFixtures.setVisibility(View.VISIBLE);
                } else {
                    txtEmptyFixtures.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String error) {
                fixturesProgress.setVisibility(View.GONE);
                txtEmptyFixtures.setVisibility(View.VISIBLE);
            }
        });
    }

    private static class FixtureAdapter extends RecyclerView.Adapter<FixtureAdapter.FixtureViewHolder> {
        private final List<Fixture> list;

        public FixtureAdapter(List<Fixture> list) {
            this.list = list;
        }

        public void updateList(List<Fixture> newList) {
            this.list.clear();
            this.list.addAll(newList);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public FixtureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fixture, parent, false);
            return new FixtureViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull FixtureViewHolder holder, int position) {
            Fixture f = list.get(position);
            holder.txtDate.setText(f.getDate() + " • " + f.getSport());
            holder.txtHome.setText(f.getHomeTeam());
            holder.txtAway.setText(f.getAwayTeam());

            if ("Finished".equalsIgnoreCase(f.getStatus())) {
                holder.txtScore.setText(f.getHomeScore() + " - " + f.getAwayScore());
                holder.txtStatus.setText("Finished");
                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_approved);
            } else {
                holder.txtScore.setText("VS");
                holder.txtStatus.setText("Upcoming");
                holder.txtStatus.setBackgroundResource(R.drawable.bg_status_pending);
            }

            holder.txtVenue.setText("Venue: " + (f.getVenue() != null ? f.getVenue() : "Main Arena"));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class FixtureViewHolder extends RecyclerView.ViewHolder {
            TextView txtDate, txtStatus, txtHome, txtAway, txtScore, txtVenue;

            public FixtureViewHolder(@NonNull View itemView) {
                super(itemView);
                txtDate = itemView.findViewById(R.id.txtFixtureDate);
                txtStatus = itemView.findViewById(R.id.txtFixtureStatus);
                txtHome = itemView.findViewById(R.id.txtHomeTeam);
                txtAway = itemView.findViewById(R.id.txtAwayTeam);
                txtScore = itemView.findViewById(R.id.txtMatchScore);
                txtVenue = itemView.findViewById(R.id.txtVenue);
            }
        }
    }
}
