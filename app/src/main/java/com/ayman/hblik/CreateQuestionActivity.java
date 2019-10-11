package com.ayman.hblik;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

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
        String firstName =preferences.getString("first_name",null);
        String lastName =preferences.getString("last_name",null);
        score =preferences.getInt("score",0);

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
                        if (score < 50) {
                            Toast.makeText(CreateQuestionActivity.this, "You should have at least 50 pts", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(CreateQuestionActivity.this, CreateQuestionActivity.class);
                            startActivity(intent);
                        }

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


        if(score<150){
            mOption3.setVisibility(View.GONE);
            mOption4.setVisibility(View.GONE);
            mOption5.setVisibility(View.GONE);

            mNotif_s.setVisibility(View.VISIBLE);
            mNotif_3.setVisibility(View.VISIBLE);
        }

        else if(score<250){
            mOption4.setVisibility(View.GONE);
            mOption5.setVisibility(View.GONE);

            mNotif_s.setVisibility(View.VISIBLE);
            mNotif_4.setVisibility(View.VISIBLE);

        }

        else if(score<350){
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





    Map<String, Object> data = new HashMap<>();
    FirebaseFirestore db;
    String question;
    String option1;
    String option2;
    String option3;
    String option4;
    String option5;
    int AnsNbr;
    private void onPost() {
        if(CfieldEmpty()){
            return;
        }



       db = FirebaseFirestore.getInstance();

        question=mQuestion.getText().toString();
        option1=mOption1.getText().toString();
        option2=mOption2.getText().toString();
        AnsNbr=Integer.parseInt(mAnsNbr.getText().toString());
        option3=mOption3.getText().toString();
        option4=mOption4.getText().toString();
        option5=mOption5.getText().toString();



        if(score>=350){
            if(!option3.equals("")){
                score=score-100;
            }
            if(!option4.equals("")){
                score=score-100;
            }
            if(!option5.equals("")){
                score=score-100;
            }
        }


        else if(score>=250){
            if(!option3.equals("")){
                score=score-100;
            }
            if(!option4.equals("")){
                score=score-100;
            }
        }

        else if(score>=150){
            if(!option3.equals("")){
                score=score-100;
            }
        }




        final int calculated_score = score - 50*AnsNbr;
        if(calculated_score<0){
            Toast.makeText(this, "You don't have enough score !\n1 answer for 50 points ", Toast.LENGTH_SHORT).show();
            mAnsNbr.setError("Decrease the number of answers");
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
}
