package com.ayman.hblik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

public class LaunchActivity extends AppCompatActivity {

    SharedPreferences preferences;
    ImageView imageLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
       imageLoading=findViewById(R.id.imageLoading);

       //todo : create loading layout ...
        preferences =getSharedPreferences("userPreferences",0);

        if(preferences.getAll().size()==0){
            Intent i =new Intent(this,LoginActivity.class);
            startActivity(i);
        }
        else {
            Intent i =new Intent(this,QuestionsActivity.class);
            startActivity(i);
        }

    }
}
