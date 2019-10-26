package com.ayman.hblik;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class EmailVerificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);
        Toolbar toolbar = findViewById(R.id.toolbar_email_verification);
        toolbar.setTitle(getResources().getString(R.string.email_verification));

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_ic);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(EmailVerificationActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });

    }


    @Override
    public void onBackPressed() {
        Intent i =new Intent(this,RegisterActivity.class);
        startActivity(i);
    }
}
