package com.ayman.hblik;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    private static final String ADMOB_AD_UNIT_ID = "ca-app-pub-4453425711318249/9140874106";
    private static int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        ImageView Hask, Hanswer, Hstats;
        LinearLayout Hexit;

        TextView hscore = findViewById(R.id.Hscore);
        Hask = findViewById(R.id.Hask);
        Hanswer = findViewById(R.id.Hanswer);
        Hstats = findViewById(R.id.Hstats);
        Hexit = findViewById(R.id.Hexit);

        //todo edit content
        //todo edit other activities content like settings and help
        SharedPreferences preferences = getSharedPreferences("userPreferences", 0);

        score = preferences.getInt("score", 0);

        Toolbar toolbar = findViewById(R.id.toolbar_home);

        setSupportActionBar(toolbar);


        hscore.setText(getResources().getString(R.string.score_home, score));

        Hask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (score < 25) {
                    Toast.makeText(HomeActivity.this, "You should have a least 25 pts", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(HomeActivity.this, CreateQuestionActivity.class);
                    startActivity(i);
                }
            }
        });

        Hanswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, QuestionsActivity.class);
                startActivity(i);
            }
        });


        Hstats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, UserActivityActivity.class);
                startActivity(i);
            }
        });

        Hexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
                preferences.edit().clear().apply();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                FirebaseAuth mAuth;
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                LoginManager.getInstance().logOut();

                startActivity(intent);
                finish();
            }
        });

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(ADMOB_AD_UNIT_ID);

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });


    }


    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    public void actionHome(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.nav_settings:
                Intent i = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.nav_help:
                Intent j = new Intent(HomeActivity.this, HelpActivity.class);
                startActivity(j);
                break;

            case R.id.nav_share:
                final String appPackageName = getPackageName();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out my app at : https://play.google.com/store/apps/details?id=" + appPackageName);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.nav_rate_us:
                Intent t = new Intent(HomeActivity.this, RateUsActivity.class);
                startActivity(t);
                break;

        }
    }
}
