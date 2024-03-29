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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.Objects;

public class QuestionStatsActivity extends AppCompatActivity {
    String question, option_1, option_2, option_3, option_4,option_5;
    int option1, option2,option3, option4, option5,id_question,answerNbr,score;
    TextView mQuestion,mOption1,mOption2,mOption3,mOption4,mOption5,bar1,bar2,bar3,bar4,bar5,option3_color,option4_color,option5_color;
    private static final String TAG = "QuestionStatsActivity";
    LinearLayout stat_3,stat_4,stat_5;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_stats);
        SharedPreferences preferences = getSharedPreferences("userPreferences",MODE_PRIVATE);
        final String id_user=preferences.getString("id_user",null);

        score =preferences.getInt("score",0);

        Toolbar toolbar = findViewById(R.id.toolbar_question_stats);
        toolbar.setTitle(getResources().getString(R.string.question_stats));

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_ic);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        stat_3=findViewById(R.id.stat_3);
        stat_4=findViewById(R.id.stat_4);
        stat_5=findViewById(R.id.stat_5);

        option3_color=findViewById(R.id.option3_color);

        option4_color=findViewById(R.id.option4_color);
        option5_color=findViewById(R.id.option5_color);



        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_nav_view);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
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
                        Bundle b =new Bundle();
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
        id_question = b.getInt("id_question");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("questions")
                .whereEqualTo("id", id_question)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                                DocumentSnapshot document= Objects.requireNonNull(task.getResult()).getDocuments().get(0);
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
                            Log.d(TAG, "Error getting documents: ", task.getException());

                        }
                    }
                });


    }






    private void fillContent() {
        mOption1=findViewById(R.id.option1);
        mOption2=findViewById(R.id.option2);
        mOption3=findViewById(R.id.option3);
        mOption4=findViewById(R.id.option4);
        mOption5=findViewById(R.id.option5);
        bar1=findViewById(R.id.barQ1);
        bar2=findViewById(R.id.barQ2);
        bar3=findViewById(R.id.barQ3);
        bar4=findViewById(R.id.barQ4);
        bar5=findViewById(R.id.barQ5);
        mQuestion=findViewById(R.id.questionII);

        mQuestion.setText(question);
        mOption1.setText(option_1);
        mOption2.setText(option_2);


        Log.d(TAG, "fillContent: option_1 :"+option_1);
        Log.d(TAG, "fillContent: option_2 :"+option_2);
        Log.d(TAG, "fillContent: option_3 :"+option_3);
        Log.d(TAG, "fillContent: option_4 :"+option_4);
        Log.d(TAG, "fillContent: option_5 :"+option_5);

        Log.d(TAG, "fillContent: "+option1+"    "+answerNbr);


        if(answerNbr== 0){
            changeWidth(bar1,0);
            changeWidth(bar2,0);
        }
        else {
            changeWidth(bar1,option1);
            changeWidth(bar2,option2);
        }

        if(option_3 != null) {

            mOption3.setText(option_3);
            if(answerNbr== 0){
                changeWidth(bar3,0);
            }
            else {
                     changeWidth(bar3,option3);            }
        }
        else
        {
            mOption3.setVisibility(View.GONE);
            option3_color.setVisibility(View.GONE);
            stat_3.setVisibility(View.GONE);}


        if(option_4 != null) {

            mOption4.setText(option_4);
            if(answerNbr== 0){
                changeWidth(bar4,0);
            }
            else {

                changeWidth(bar4,option4);            }
        }
        else {
            mOption4.setVisibility(View.GONE);
            option4_color.setVisibility(View.GONE);
            stat_4.setVisibility(View.GONE);
        }


        if(option_5 != null) {

            mOption5.setText(option_5);
            if(answerNbr== 0){
                changeWidth(bar5,0);
            }
            else {
                       changeWidth(bar5,option5);
            }
        }
        else {
            mOption5.setVisibility(View.GONE);
            option5_color.setVisibility(View.GONE);
            stat_5.setVisibility(View.GONE);
        }

    }

    private void changeWidth(TextView view,int option){
        float height = view.getLayoutParams().height;

        float proportion=(float)option/answerNbr;
        float newHeight=height*(proportion);

        Log.d(TAG, "changeWidth: "+height+"   "+newHeight+"   "+ (int)newHeight
        +"   option : "+option+"  ansN : "+answerNbr);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100,  (int)newHeight);
        view.setLayoutParams(layoutParams);
        view.setText(new DecimalFormat("##.#").format(proportion*100)+"%");
    }
}
