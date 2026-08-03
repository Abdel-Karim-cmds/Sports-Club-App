package com.example.linearlayout2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class basketballDashboard extends AppCompatActivity {

    private CardView cardApply, cardPlayers, cardResults, cardAnnouncements;
    private TextView txtTitle, txtSportDesc;
    private ImageView imgSportHeaderIcon;
    private String selectedSport = "Basketball";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_basketball_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent() != null && getIntent().hasExtra("EXTRA_SPORT_NAME")) {
            selectedSport = getIntent().getStringExtra("EXTRA_SPORT_NAME");
        }

        txtTitle = findViewById(R.id.txtTitle);
        txtSportDesc = findViewById(R.id.txtSportDesc);
        imgSportHeaderIcon = findViewById(R.id.imgSportHeaderIcon);

        if (txtTitle != null) {
            txtTitle.setText(selectedSport + " Dashboard");
        }

        configureSportHeader();

        cardApply = findViewById(R.id.cardApply);
        cardPlayers = findViewById(R.id.cardPlayers);
        cardResults = findViewById(R.id.cardResults);
        cardAnnouncements = findViewById(R.id.cardAnnouncements);

        if (cardApply != null) {
            cardApply.setOnClickListener(v -> {
                Intent intent = new Intent(basketballDashboard.this, applyToBasketballTeam.class);
                intent.putExtra("EXTRA_SPORT_NAME", selectedSport);
                startActivity(intent);
            });
        }

        if (cardPlayers != null) {
            cardPlayers.setOnClickListener(v -> {
                Intent intent = new Intent(basketballDashboard.this, ViewPlayersActivity.class);
                intent.putExtra("EXTRA_SPORT_NAME", selectedSport);
                startActivity(intent);
            });
        }

        if (cardResults != null) {
            cardResults.setOnClickListener(v -> {
                Intent intent = new Intent(basketballDashboard.this, ViewResultsActivity.class);
                intent.putExtra("EXTRA_SPORT_NAME", selectedSport);
                startActivity(intent);
            });
        }

        if (cardAnnouncements != null) {
            cardAnnouncements.setOnClickListener(v -> {
                Intent intent = new Intent(basketballDashboard.this, AnnouncementsActivity.class);
                intent.putExtra("EXTRA_SPORT_NAME", selectedSport);
                startActivity(intent);
            });
        }
    }

    private void configureSportHeader() {
        if (imgSportHeaderIcon == null) return;

        switch (selectedSport) {
            case "Football":
                imgSportHeaderIcon.setImageResource(R.drawable.ic_soccer);
                if (txtSportDesc != null) txtSportDesc.setText("Football is the world's most popular sport. 11 players compete to score in the opposing net through team strategy and skilled footwork.");
                break;
            case "Volleyball":
                imgSportHeaderIcon.setImageResource(R.drawable.ic_volleyball);
                if (txtSportDesc != null) txtSportDesc.setText("Volleyball features high-flying action with two teams of 6 players scoring points by grounding a ball on the opponent's court.");
                break;
            case "Field Hockey":
                imgSportHeaderIcon.setImageResource(R.drawable.ic_hockey);
                if (txtSportDesc != null) txtSportDesc.setText("Field Hockey demands speed and precision as players maneuver a hard ball with curved sticks to score goals.");
                break;
            case "Tennis":
                imgSportHeaderIcon.setImageResource(R.drawable.ic_tennis);
                if (txtSportDesc != null) txtSportDesc.setText("Tennis tests agility and power in singles or doubles matches played across regulation net courts.");
                break;
            case "Swimming":
                imgSportHeaderIcon.setImageResource(R.drawable.ic_swimming);
                if (txtSportDesc != null) txtSportDesc.setText("USIU Aquatic Squad features freestyle, backstroke, breaststroke, and butterfly competitive swimming events.");
                break;
            case "Basketball":
            default:
                imgSportHeaderIcon.setImageResource(R.drawable.ic_basketball);
                if (txtSportDesc != null) txtSportDesc.setText(getString(R.string.aboutBB));
                break;
        }
    }
}