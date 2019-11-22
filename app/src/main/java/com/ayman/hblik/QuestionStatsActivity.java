package com.ayman.hblik;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.Objects;

public class QuestionStatsActivity extends AppCompatActivity {
    private static final String ADMOB_AD_UNIT_ID = "ca-app-pub-4453425711318249/5588641906";
    private static String question, option_1, option_2, option_3, option_4, option_5;
    private static int option1, option2, option3, option4, option5, answerNbr, score;
    private TextView option3_color, option4_color, option5_color;
    private LinearLayout stat_3, stat_4, stat_5;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_stats);
        SharedPreferences preferences = getSharedPreferences("userPreferences", MODE_PRIVATE);
        final String id_user = preferences.getString("id_user", null);

        score = preferences.getInt("score", 0);
       TextView mScore = findViewById(R.id.score);
        mScore.setText(getResources().getString(R.string.score, score));

        Toolbar toolbar = findViewById(R.id.toolbar_question_stats);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        stat_3 = findViewById(R.id.stat_3);
        stat_4 = findViewById(R.id.stat_4);
        stat_5 = findViewById(R.id.stat_5);

        option3_color = findViewById(R.id.option3_color);

        option4_color = findViewById(R.id.option4_color);
        option5_color = findViewById(R.id.option5_color);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.nav_home:
                        Intent h = new Intent(QuestionStatsActivity.this, HomeActivity.class);
                        startActivity(h);
                        return true;
                    case R.id.nav_answer:
                        Intent j = new Intent(QuestionStatsActivity.this, QuestionsActivity.class);
                        startActivity(j);
                        return true;
                    case R.id.nav_ask:
                        if (score < 25) {
                            Toast.makeText(QuestionStatsActivity.this, "You should have at least 25 pts", Toast.LENGTH_SHORT).show();
                            return false;
                        } else {
                            Intent intent = new Intent(QuestionStatsActivity.this, CreateQuestionActivity.class);
                            startActivity(intent);
                            return true;
                        }


                    case R.id.MyQuestions:
                        Log.d("e", "onNavigationItemSelected: starting userA");
                        Intent i = new Intent(QuestionStatsActivity.this, UserActivityActivity.class);
                        Bundle b = new Bundle();
                        b.putString("id_user", id_user);
                        i.putExtras(b);
                        startActivity(i);
                        return true;
                }
                return false;
            }
        });

        bottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(false);
        bottomNavigationView.getMenu().findItem(R.id.MyQuestions).setChecked(true);


        Bundle b = getIntent().getExtras();
        assert b != null;
        answerNbr = b.getInt("answerNbr");
        question = b.getString("question");
        int id_question = b.getInt("id_question");


        db.collection("questions")
                .whereEqualTo("id", id_question)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            DocumentSnapshot document = Objects.requireNonNull(task.getResult()).getDocuments().get(0);
                            option1 = Objects.requireNonNull(document.getLong("option_1")).intValue();
                            option2 = Objects.requireNonNull(document.getLong("option_2")).intValue();
                            if (document.getLong("option_3") != null) {
                                option3 = Objects.requireNonNull(document.getLong("option_3")).intValue();
                            }
                            if (document.getLong("option_4") != null) {
                                option4 = Objects.requireNonNull(document.getLong("option_4")).intValue();
                            }
                            if (document.getLong("option_5") != null) {
                                option5 = Objects.requireNonNull(document.getLong("option_5")).intValue();
                            }

                            option_1 = document.getString("option1");
                            option_2 = document.getString("option2");
                            option_3 = document.getString("option3");
                            option_4 = document.getString("option4");
                            option_5 = document.getString("option5");


                            fillContent();
                        } else {

                        }
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


    private void fillContent() {
        TextView mOption1 = findViewById(R.id.option1);
        TextView mOption2 = findViewById(R.id.option2);
        TextView mOption3 = findViewById(R.id.option3);
        TextView mOption4 = findViewById(R.id.option4);
        TextView mOption5 = findViewById(R.id.option5);
        TextView bar1 = findViewById(R.id.barQ1);
        TextView bar2 = findViewById(R.id.barQ2);
        TextView bar3 = findViewById(R.id.barQ3);
        TextView bar4 = findViewById(R.id.barQ4);
        TextView bar5 = findViewById(R.id.barQ5);
        TextView bar1T = findViewById(R.id.barQ1Top);
        TextView bar2T = findViewById(R.id.barQ2Top);
        TextView bar3T = findViewById(R.id.barQ3Top);
        TextView bar4T = findViewById(R.id.barQ4Top);
        TextView bar5T = findViewById(R.id.barQ5Top);
        TextView mQuestion = findViewById(R.id.questionII);

        mQuestion.setText(question);
        mOption1.setText(option_1);
        mOption2.setText(option_2);





        if (answerNbr == 0) {
            Toast.makeText(this, "No answers yet !", Toast.LENGTH_SHORT).show();
            makeMinimalWidth(bar1T, bar1);
            makeMinimalWidth(bar2T, bar2);
        } else {
            if (option1 == 0)
                makeMinimalWidth(bar1T, bar1);
            else
                changeWidth(bar1T, bar1, option1);

            if (option2 == 0)
                makeMinimalWidth(bar2T, bar2);
            else
                changeWidth(bar2T, bar2, option2);
        }

        if (option_3 != null) {

            mOption3.setText(option_3);
            if (answerNbr == 0) {
                makeMinimalWidth(bar3T, bar3);
            } else {
                if (option3 == 0)
                    makeMinimalWidth(bar3T, bar3);
                else
                    changeWidth(bar3T, bar3, option3);
            }
        } else {
            mOption3.setVisibility(View.GONE);
            option3_color.setVisibility(View.GONE);
            stat_3.setVisibility(View.GONE);
        }


        if (option_4 != null) {

            mOption4.setText(option_4);
            if (answerNbr == 0) {
                makeMinimalWidth(bar4T, bar4);
            } else {

                if (option4 == 0)
                    makeMinimalWidth(bar4T, bar4);
                else
                    changeWidth(bar4T, bar4, option4);
            }
        } else {
            mOption4.setVisibility(View.GONE);
            option4_color.setVisibility(View.GONE);
            stat_4.setVisibility(View.GONE);
        }


        if (option_5 != null) {

            mOption5.setText(option_5);
            if (answerNbr == 0) {
                makeMinimalWidth(bar5T, bar5);
            } else {
                if (option5 == 0)
                    makeMinimalWidth(bar5T, bar5);
                else
                    changeWidth(bar5T, bar5, option5);
            }
        } else {
            mOption5.setVisibility(View.GONE);
            option5_color.setVisibility(View.GONE);
            stat_5.setVisibility(View.GONE);
        }

    }

    @SuppressLint("SetTextI18n")
    private void changeWidth(TextView viewt, TextView view, int option) {
        float height = view.getLayoutParams().height;

        float proportion = (float) option / answerNbr;
        float newHeight = height * (proportion);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (0.3 * height), (int) newHeight);
        view.setLayoutParams(layoutParams);
        if (proportion > 0.15)
            view.setText(new DecimalFormat("##.#").format(proportion * 100) + "%");
        else
            viewt.setText(new DecimalFormat("##.#").format(proportion * 100) + "%");
    }

    private void makeMinimalWidth(TextView viewt, TextView view) {
        float height = view.getLayoutParams().height;

        float newHeight = (float) (height * (0.01));


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (0.3 * height), (int) newHeight);
        view.setLayoutParams(layoutParams);
        viewt.setText("0%");
    }
}
