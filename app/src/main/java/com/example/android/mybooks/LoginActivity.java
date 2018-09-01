package com.example.android.mybooks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import static com.example.android.mybooks.R.layout.activity_login;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_login);
        // set the click event on the START button
        setClickEventOnStartButton();
    }

    // When clicking the START button, the user can go to the main view (main item list view)
    private void setClickEventOnStartButton() {
        final Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // call mainList view
                Intent _MainListActivity = new Intent(getApplicationContext(), MainListActivity.class);
                startActivity(_MainListActivity);
            }
        });
    }

}
