package com.example.linearlayout2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class applyToBasketballTeam extends AppCompatActivity {

    EditText playerName, playerPosition;
    RadioGroup rgGender;
    Spinner selectSchools;

    Button applyBB;
    FirebaseFirestore firestore;




        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_apply_to_basketball_team);

            ViewCompat.setOnApplyWindowInsetsListener(
                    findViewById(R.id.main),
                    (v, insets) -> {

                        Insets systemBars =
                                insets.getInsets(
                                        WindowInsetsCompat.Type.systemBars()
                                );

                        v.setPadding(
                                systemBars.left,
                                systemBars.top,
                                systemBars.right,
                                systemBars.bottom
                        );

                        return insets;
                    }
            );

            playerName = findViewById(R.id.bbplayername);
            playerPosition = findViewById(R.id.bbposition);
            rgGender = findViewById(R.id.radioGender);
            selectSchools = findViewById(R.id.selectSchool);
            applyBB = findViewById(R.id.btnApplyBB);

            firestore = FirebaseFirestore.getInstance();

            applyBB.setOnClickListener( view -> savePlayer() );
        }

        private void savePlayer(){

            String name = playerName.getText().toString().trim();
            String position = playerPosition.getText().toString().trim();

            if(name.isEmpty()){

                playerName.setError("Enter player name");
                playerName.requestFocus();
                return;
            }

            if(position.isEmpty()){

                playerPosition.setError("Enter playing position");
                playerPosition.requestFocus();
                return;
            }

            int selectgenderID = rgGender.getCheckedRadioButtonId();

            if(selectgenderID == -1){
                Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedGender = findViewById(selectgenderID);

            String gender = selectedGender.getText().toString();

            if(selectSchools.getSelectedItemPosition()==0){
                Toast.makeText(this, "Please select a school", Toast.LENGTH_SHORT).show();
                return;
            }

            String School = selectSchools.getSelectedItem().toString();

            Map<String, Object> player = new HashMap<>();

            player.put("Player Name", name);
            player.put("Playing Position", position);
            player.put("Gender", gender);
            player.put("school", School);

            firestore.collection("BasketballPlayers").add(player).addOnSuccessListener(documentReference -> {

                Toast.makeText(this, "player added", Toast.LENGTH_SHORT).show();

            }).addOnFailureListener(e ->{

                Toast.makeText(this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();

            } );
        }



}