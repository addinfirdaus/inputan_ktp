package com.test.inputanktp;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class Dashboard extends AppCompatActivity {

    MaterialCardView transcation,report ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        transcation = findViewById(R.id.card_transaction);
        transcation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, Transaction.class);
                startActivity(intent);

            }
        });

        report = findViewById(R.id.card_report);;

    }
}