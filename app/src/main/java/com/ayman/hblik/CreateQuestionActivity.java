package com.ayman.hblik;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateQuestionActivity extends AppCompatActivity {
//todo modify conditions to create a question
   Button postB;
   EditText mQuestion,mOption3,mOption4,mOption5, mOption1,mOption2,mAnsNbr;
   String id_questioner;
    LinearLayout mNotif_s,mNotif_3,mNotif_4,mNotif_5,mNotif_0;
    int score;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private static final String TAG = "CreateQuestionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);

        SharedPreferences preferences = getSharedPreferences("userPreferences",MODE_PRIVATE);
        id_questioner=preferences.getString("id_user",null);
        score =preferences.getInt("score",0);
        TextView mScore;
        mScore=findViewById(R.id.score1);
        mScore.setText(getResources().getString(R.string.score,score));

        Toolbar toolbar = findViewById(R.id.toolbar_create_question);
        toolbar.setTitle(getResources().getString(R.string.create_question));

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_ic);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




        mNotif_s=findViewById(R.id.notif_s);
        mNotif_3=findViewById(R.id.notif_3);
        mNotif_4=findViewById(R.id.notif_4);
        mNotif_5=findViewById(R.id.notif_5);



        final BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                switch (id) {
                    case R.id.nav_home:
                        Intent h = new Intent(CreateQuestionActivity.this, HomeActivity.class);
                        startActivity(h);
                        break;
                    case R.id.nav_answer:
                        Intent j = new Intent(CreateQuestionActivity.this, QuestionsActivity.class);

                        startActivity(j);
                        break;
                    case R.id.nav_ask:

                        break;
                    case R.id.MyQuestions:
                        Log.d("e", "onNavigationItemSelected: starting userA");
                        Intent i = new Intent(CreateQuestionActivity.this, UserActivityActivity.class);
                        Bundle b =new Bundle();
                        b.putString("id_user", id_questioner);
                        i.putExtras(b);
                        startActivity(i);
                        break;
                }
                return true;
            }
        });
        bottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(false);
        bottomNavigationView.getMenu().findItem(R.id.nav_ask).setChecked(true);





        postB=findViewById(R.id.postB);
        mQuestion=findViewById(R.id.mQuestion);
        mOption1=findViewById(R.id.mOption1);
        mOption2=findViewById(R.id.mOption2);
        mOption3=findViewById(R.id.mOption3);
        mOption4=findViewById(R.id.mOption4);
        mOption5=findViewById(R.id.mOption5);
        mAnsNbr=findViewById(R.id.mAnsNbr);


        postB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPost();
            }
        });


        if(score<45){
            mOption3.setVisibility(View.GONE);
            mOption4.setVisibility(View.GONE);
            mOption5.setVisibility(View.GONE);

            mNotif_s.setVisibility(View.VISIBLE);
            mNotif_3.setVisibility(View.VISIBLE);
        }

        else if(score<65){
            mOption4.setVisibility(View.GONE);
            mOption5.setVisibility(View.GONE);

            mNotif_s.setVisibility(View.VISIBLE);
            mNotif_4.setVisibility(View.VISIBLE);

        }

        else if(score<85){
            mOption5.setVisibility(View.GONE);

            mNotif_s.setVisibility(View.VISIBLE);
            mNotif_5.setVisibility(View.VISIBLE);

        }

    }



    @Override
    public void onBackPressed(){
        Intent i = new Intent(this,HomeActivity.class);
        startActivity(i);
    }






    private void onPost() {

        if(!LoginActivity.ConnectivityHelper.isConnectedToNetwork(this)){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            //progressBar.setVisibility(View.GONE);
            return;
        }

        else if(CfieldEmpty()){
            return;
        }



        final Map<String, Object> data = new HashMap<>();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final String question=mQuestion.getText().toString();
        final String option1=mOption1.getText().toString();
        final String option2=mOption2.getText().toString();
        final String option3=mOption3.getText().toString();
        final String option4=mOption4.getText().toString();
        final String option5=mOption5.getText().toString();
        final int AnsNbr=Integer.parseInt(mAnsNbr.getText().toString());
        int options_score=score;



        if(options_score>=85){
            if(!option3.equals("")){
                options_score=options_score-20;
            }
            if(!option4.equals("")){
                options_score=options_score-20;
            }
            if(!option5.equals("")){
                options_score=options_score-20;
            }
        }


        else if(options_score>=65){
            if(!option3.equals("")){
                options_score=options_score-20;
            }
            if(!option4.equals("")){
                options_score=options_score-20;
            }
        }

        else if(options_score>=45){
            if(!option3.equals("")){
                options_score=options_score-20;
            }
        }

        options_score=options_score-20;


        final int calculated_score = options_score - 5*AnsNbr;
        if(calculated_score<0){
            Toast.makeText(this, "You don't have enough score !\n1 answer for 5 points ", Toast.LENGTH_SHORT).show();
            mAnsNbr.setError("Decrease the number of answers or options");

        }

        else{
        final DocumentReference sfDocRef = db.collection("questions").document("qXGHFFXNNANcUr2P0fEm");

        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                DocumentSnapshot snapshot = transaction.get(sfDocRef);
                int count_id= Objects.requireNonNull(snapshot.getLong("count_id")).intValue();

                data.put("id",count_id);

                transaction.update(sfDocRef, "count_id", count_id+1);

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");
                data.put("question", question);
                data.put("option1",option1);
                data.put("option_1",0);
                data.put("option2",option2);
                data.put("option_2",0);
                data.put("answerNbr",AnsNbr);
                data.put("total_answers",0);
                data.put("id_questioner", id_questioner);



                if(!option3.equals("")) {
                    data.put("option3", option3);
                    data.put("option_3", 0);

                }
                if(!option4.equals("")) {
                    data.put("option4", option4);
                    data.put("option_4", 0);

                }
                if(!option5.equals("")) {
                    data.put("option5", option5);
                    data.put("option_5", 0);

                }

                db.collection("questions")
                        .add(data);

                final DocumentReference sfDocRef2 = db.collection("userh").document(id_questioner);
                final WriteBatch batch = db.batch();
                batch.update(sfDocRef2, "score", calculated_score);



                preferences=getSharedPreferences("userPreferences",0);
                editor=preferences.edit();
                editor.putInt("score",calculated_score);
                editor.apply();

                Intent i =new Intent(CreateQuestionActivity.this, UserActivityActivity.class);
                startActivity(i);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateQuestionActivity.this, "Error communicating with the server", Toast.LENGTH_SHORT).show();

            }
        });



}}

    private boolean CfieldEmpty() { 
        mQuestion.setError(null);
        mOption1.setError(null);
        mOption2.setError(null);

       String question=mQuestion.getText().toString();
       String option1=mOption1.getText().toString();
       String option2=mOption2.getText().toString();

        if(question.equals(""))
        {
            mQuestion.setError("Ask Something");
            return true;
        }
        if(option1.equals(""))
        {
            mOption1.setError("This field is compulsory");
            return true;
        }
        if(option2.equals(""))
        {
            mOption2.setError("This field is compulsory");
            return true;
        }
        return false;
    }

    public void gotoHelp(View view) {
        Intent i=new Intent(this,HelpActivity.class);
        startActivity(i);

    }
}
