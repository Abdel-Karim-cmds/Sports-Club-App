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
import com.example.linearlayout2.models.Announcement;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementsActivity extends AppCompatActivity {

    private RecyclerView recyclerAnnouncements;
    private ProgressBar annProgress;
    private TextView txtEmptyAnnouncements;
    private AnnouncementAdapter adapter;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements);

        firebaseManager = FirebaseManager.getInstance();

        recyclerAnnouncements = findViewById(R.id.recyclerAnnouncements);
        annProgress = findViewById(R.id.annProgress);
        txtEmptyAnnouncements = findViewById(R.id.txtEmptyAnnouncements);

        recyclerAnnouncements.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AnnouncementAdapter(new ArrayList<>());
        recyclerAnnouncements.setAdapter(adapter);

        loadAnnouncements();
    }

    private void loadAnnouncements() {
        annProgress.setVisibility(View.VISIBLE);
        firebaseManager.getAnnouncements(new FirebaseManager.DataCallback<List<Announcement>>() {
            @Override
            public void onSuccess(List<Announcement> data) {
                annProgress.setVisibility(View.GONE);
                adapter.updateList(data);

                if (data.isEmpty()) {
                    txtEmptyAnnouncements.setVisibility(View.VISIBLE);
                } else {
                    txtEmptyAnnouncements.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String error) {
                annProgress.setVisibility(View.GONE);
                txtEmptyAnnouncements.setVisibility(View.VISIBLE);
            }
        });
    }

    private static class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnViewHolder> {
        private final List<Announcement> list;

        public AnnouncementAdapter(List<Announcement> list) {
            this.list = list;
        }

        public void updateList(List<Announcement> newList) {
            this.list.clear();
            this.list.addAll(newList);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public AnnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announcement, parent, false);
            return new AnnViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull AnnViewHolder holder, int position) {
            Announcement a = list.get(position);
            holder.txtTitle.setText(a.getTitle());
            holder.txtContent.setText(a.getContent());
            holder.txtDate.setText(a.getDate());
            holder.txtCategory.setText(a.getCategory() != null ? a.getCategory() : "Notice");
            holder.txtAuthor.setText("Published by " + (a.getAuthor() != null ? a.getAuthor() : "USIU Sports Board"));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class AnnViewHolder extends RecyclerView.ViewHolder {
            TextView txtTitle, txtContent, txtDate, txtCategory, txtAuthor;

            public AnnViewHolder(@NonNull View itemView) {
                super(itemView);
                txtTitle = itemView.findViewById(R.id.txtAnnTitle);
                txtContent = itemView.findViewById(R.id.txtAnnContent);
                txtDate = itemView.findViewById(R.id.txtAnnDate);
                txtCategory = itemView.findViewById(R.id.txtAnnCategory);
                txtAuthor = itemView.findViewById(R.id.txtAnnAuthor);
            }
        }
    }
}
