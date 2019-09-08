package com.ayman.hblik;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
SharedPreferences preferences;
int score;
String id_user;
TextView Hscore;
ImageView Hask,Hanswer,Hhelp,Hsettings,Hrate,Hreport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Hscore=findViewById(R.id.Hscore);
        Hask=findViewById(R.id.Hask);
        Hanswer=findViewById(R.id.Hanswer);
        Hhelp=findViewById(R.id.Hhelp);
        Hrate=findViewById(R.id.Hrate);
        Hsettings=findViewById(R.id.Hsettings);
        Hreport=findViewById(R.id.Hreport);




        preferences=getSharedPreferences("userPreferences",0);
        id_user=preferences.getString("id_user",null);
        score=preferences.getInt("score",0);
        if(score<25)
        {
            Hask.setEnabled(false);
        }


        Hscore.setText(getResources().getString(R.string.score_home,score));

        Hask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this,CreateQuestionActivity.class);
                startActivity(i);
            }
        });

        Hanswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, QuestionsActivity.class);
                startActivity(i);
            }
        });


        Hhelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, HelpActivity.class);
                startActivity(i);
            }
        });

        Hsettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });

        Hrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, QuestionsActivity.class);
                startActivity(i);
            }
        });

        Hreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, ReportActivity.class);
                startActivity(i);
            }
        });

        final BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                switch (id) {
                    case R.id.nav_home:
                        Intent h = new Intent(HomeActivity.this, HomeActivity.class);
                        startActivity(h);
                        break;
                    case R.id.nav_answer:
                        Intent j = new Intent(HomeActivity.this, QuestionsActivity.class);

                        startActivity(j);
                        break;
                    case R.id.nav_ask:
                        if (score < 25) {
                            Toast.makeText(HomeActivity.this, "You should have at least 25 pts", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(HomeActivity.this, CreateQuestionActivity.class);
                            startActivity(intent);
                        }

                        break;
                    case R.id.MyQuestions:
                        Log.d("e", "onNavigationItemSelected: starting userA");
                        Intent i = new Intent(HomeActivity.this, UserActivityActivity.class);
                        Bundle b =new Bundle();
                        b.putString("id_user", id_user);
                        i.putExtras(b);
                        startActivity(i);
                        break;
                }
                return true;
            }
        });
        bottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);


    }
    @Override
    public void onBackPressed(){
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}
