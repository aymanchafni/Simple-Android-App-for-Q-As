package com.ayman.hblik;

import android.annotation.SuppressLint;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class QuestionsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView mName, mScore, mQuestion;
    RadioGroup radio;
    BottomNavigationItemView askMi;
    private static final String TAG = "QuestionsActivity";
    RadioButton rb1, rb2, rb3, rb4, rb5;
    Button submitB;
    int id_last_question, optionChosen, score;
    String id_user;
    DocumentSnapshot documentSnapshot;
    CircleImageView imageView;
    private int empty_interval;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    FirebaseAuth mAuth;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        SharedPreferences preferences = getSharedPreferences("userPreferences",MODE_PRIVATE);
        id_user=preferences.getString("id_user",null);
        String firstName =preferences.getString("first_name",null);
        String lastName =preferences.getString("last_name",null);

        score =preferences.getInt("score",0);
        id_last_question=preferences.getInt("id_last_question",0);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                switch (id){
                    case R.id.MyQuestions:
                        Log.d("e", "onNavigationItemSelected: starting userA");
                        Intent i = new Intent(QuestionsActivity.this, UserActivityActivity.class);
                        Bundle b =new Bundle();
                        b.putString("id_user", id_user);
                        i.putExtras(b);
                        startActivity(i);
                        return true;

                    case R.id.nav_home:
                        Intent h = new Intent(QuestionsActivity.this, HomeActivity.class);
                        startActivity(h);
                        return true;
                    case R.id.nav_answer:
                        Intent j = new Intent(QuestionsActivity.this, QuestionsActivity.class);
                        startActivity(j);
                        return true;
                    case R.id.nav_ask:
                        if (score < 25) {
                            Toast.makeText(QuestionsActivity.this, "You should have at least 25 pts", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(QuestionsActivity.this, CreateQuestionActivity.class);
                            startActivity(intent);
                        }

                        return true;


                }
               return false;
            }
        });
        bottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(false);
        bottomNavigationView.getMenu().findItem(R.id.nav_answer).setChecked(true);

        View v0 = navigationView.getHeaderView(0);
        imageView = v0.findViewById(R.id.profilePhoto);
        View v = navigationView.getHeaderView(0);
        mName = v.findViewById(R.id.name);
        mScore = findViewById(R.id.score);
        mQuestion = findViewById(R.id.question);
        submitB = findViewById(R.id.submit);
        radio=findViewById(R.id.radio);
        rb1 = findViewById(R.id.radioButton1);
        rb2 = findViewById(R.id.radioButton2);
        rb3 = findViewById(R.id.radioButton3);
        rb4 = findViewById(R.id.radioButton4);
        rb5 = findViewById(R.id.radioButton5);




        mName.setText(getResources().getString(R.string.name,firstName,lastName));

        setProfilePhoto(id_user);

        submitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit();
            }
        });

        askMi=findViewById(R.id.nav_ask);

        fill_new_content_start();



    }

    private void setProfilePhoto(String id){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // [START download_create_reference]
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a reference with an initial file path and name
        StorageReference pathReference = storageRef.child("profilePhotos/"+id+".png");


        final long ONE_MEGABYTE = 1024 * 1024;
        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
// Set the Bitmap data to the ImageView
                imageView.setImageBitmap(bmp);            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }

        });

    }

    public void onChoice(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioButton1:
                if (checked) {
                    rb1.setBackgroundColor(0x9ABFBFD8);
                    rb2.setBackgroundColor(0xFFFFFFFF);
                    rb3.setBackgroundColor(0xFFFFFFFF);
                    rb4.setBackgroundColor(0xFFFFFFFF);
                    rb5.setBackgroundColor(0xFFFFFFFF);
                    optionChosen = 1;
                }
                break;
            case R.id.radioButton2:
                if (checked) {
                    rb2.setBackgroundColor(0x9ABFBFD8);
                    rb1.setBackgroundColor(0xFFFFFFFF);
                    rb3.setBackgroundColor(0xFFFFFFFF);
                    rb4.setBackgroundColor(0xFFFFFFFF);
                    rb5.setBackgroundColor(0xFFFFFFFF);

                    optionChosen = 2;
                }
                break;
            case R.id.radioButton3:
                if (checked) {
                    rb3.setBackgroundColor(0x9ABFBFD8);
                    rb1.setBackgroundColor(0xFFFFFFFF);
                    rb2.setBackgroundColor(0xFFFFFFFF);
                    rb4.setBackgroundColor(0xFFFFFFFF);
                    rb5.setBackgroundColor(0xFFFFFFFF);
                    optionChosen = 3;
                }
                break;
            case R.id.radioButton4:
                if (checked) {
                    rb4.setBackgroundColor(0x9ABFBFD8);
                    rb1.setBackgroundColor(0xFFFFFFFF);
                    rb3.setBackgroundColor(0xFFFFFFFF);
                    rb2.setBackgroundColor(0xFFFFFFFF);
                    rb5.setBackgroundColor(0xFFFFFFFF);
                    optionChosen = 4;
                }
                break;
            case R.id.radioButton5:
                if (checked) {
                    rb5.setBackgroundColor(0x9ABFBFD8);
                    rb1.setBackgroundColor(0xFFFFFFFF);
                    rb3.setBackgroundColor(0xFFFFFFFF);
                    rb4.setBackgroundColor(0xFFFFFFFF);
                    rb2.setBackgroundColor(0xFFFFFFFF);
                    optionChosen = 5;
                }
                break;
        }
    }

    int quest_skipped=0;

    private void onSubmit() {
        if(ButtonsUnchecked())
        {
            return;
        }
        fill_new_content();

    }
    int total_answers;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("RestrictedApi")
    private void fill_new_content() {
        radio.clearCheck();
        rb1.setBackgroundColor(0xFFFFFFFF);
        rb2.setBackgroundColor(0xFFFFFFFF);
        rb3.setBackgroundColor(0xFFFFFFFF);
        rb4.setBackgroundColor(0xFFFFFFFF);
        rb5.setBackgroundColor(0xFFFFFFFF);


        id_last_question+=empty_interval+quest_skipped+1;
        quest_skipped=0;
        score+=5;
        mScore.setText(getResources().getString(R.string.score,score));



        db.collection("questions")
                .whereGreaterThanOrEqualTo("id", id_last_question)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful() && !Objects.requireNonNull(task.getResult()).isEmpty()) {
                            String question,option1,option2,option3,option4,option5,id_questioner;
                            int answerNbr;

                            final DocumentReference sfDocRef = db.collection("questions").document(documentSnapshot.getId());
                            final DocumentReference sfDocRef2 = db.collection("userh").document(id_user);

                            db.runTransaction(new Transaction.Function<Void>() {
                                @Override
                                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                                    DocumentSnapshot snapshot = transaction.get(sfDocRef);
                                    int times_option_chosen= Objects.requireNonNull(snapshot.getLong("option_" + optionChosen)).intValue();
                                    transaction.update(sfDocRef, "option_"+optionChosen, times_option_chosen+1);

                                    // Success
                                    return null;
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("AAAAAAAAAAA", "Transaction success!");
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("AAAAAAAAAAAAA", "Transaction failure.", e);
                                        }
                                    });

                            WriteBatch batch = db.batch();

                            batch.update(sfDocRef, "total_answers", total_answers+1);
                            batch.update(sfDocRef2, "score", score);
                            preferences=getSharedPreferences("userPreferences",0);
                                editor=preferences.edit();
                                editor.putInt("score",score);
                                editor.apply();
                            batch.update(sfDocRef2, "id_last_question", id_last_question+quest_skipped+empty_interval);
                            editor.putInt("id_last_question",id_last_question);
                            editor.apply();
                            batch.commit();



                            for (QueryDocumentSnapshot document : task.getResult()) {

                                answerNbr= Objects.requireNonNull(document.getLong("answerNbr")).intValue();
                                total_answers= Objects.requireNonNull(document.getLong("total_answers")).intValue();
                                id_questioner=document.getString("id_questioner");

                                assert id_questioner != null;
                                if(answerNbr <= total_answers || id_questioner.equals(id_user)) {
                                    quest_skipped++;
                                    continue;
                                }

                                int current_quest_id= Objects.requireNonNull(document.getLong("id")).intValue();
                               empty_interval=current_quest_id-id_last_question;

                                question=document.getString("question");
                                option1=document.getString("option1");
                                option2=document.getString("option2");
                                option3=document.getString("option3");
                                option4=document.getString("option4");
                                option5=document.getString("option5");

                                documentSnapshot=document;

                                mQuestion.setText(question);
                                rb1.setText(option1);
                                rb2.setText(option2);
                                if(option3 != null) {
                                    rb3.setVisibility(View.VISIBLE);
                                    rb3.setText(option3);
                                }
                                else
                                    rb3.setVisibility(View.GONE);
                                if(option4 != null) {
                                    rb4.setVisibility(View.VISIBLE);
                                    rb4.setText(option4);
                                }
                                else
                                    rb4.setVisibility(View.GONE);
                                if(option5 != null) {
                                    rb5.setVisibility(View.VISIBLE);
                                    rb5.setText(option5);
                                }
                                else
                                    rb5.setVisibility(View.GONE);

                               break;
                            }


                        } else {
                            Log.d("e", "Error getting documents: ", task.getException());
                            Toast.makeText(QuestionsActivity.this, "oups ! try later", Toast.LENGTH_SHORT).show();
                            rb1.setVisibility(View.INVISIBLE);
                            rb2.setVisibility(View.INVISIBLE);
                            rb3.setVisibility(View.INVISIBLE);
                            rb4.setVisibility(View.INVISIBLE);
                            rb5.setVisibility(View.INVISIBLE);
                            mQuestion.setText("");

                        }

                    }
                });




    }

    @SuppressLint("RestrictedApi")
    private void fill_new_content_start() {

        mScore.setText(getResources().getString(R.string.score,score));



        db.collection("questions")
                .whereGreaterThanOrEqualTo("id", id_last_question)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !Objects.requireNonNull(task.getResult()).isEmpty()) {
                            String question,option1,option2,option3,option4,option5;
                            int answerNbr;

                            for (QueryDocumentSnapshot document : task.getResult()) {




                                answerNbr= Objects.requireNonNull(document.getLong("answerNbr")).intValue();
                                total_answers= Objects.requireNonNull(document.getLong("total_answers")).intValue();


                                if(answerNbr <= total_answers) {
                                    quest_skipped++;
                                    continue;
                                }

                                question=document.getString("question");
                                option1=document.getString("option1");
                                option2=document.getString("option2");
                                option3=document.getString("option3");
                                option4=document.getString("option4");
                                option5=document.getString("option5");
                                documentSnapshot=document;

                                mQuestion.setText(question);
                                rb1.setText(option1);
                                rb2.setText(option2);
                                if(option3 != null) {
                                    rb3.setVisibility(View.VISIBLE);
                                    rb3.setText(option3);
                                }
                                else
                                    rb3.setVisibility(View.GONE);
                                if(option4 != null) {
                                    rb4.setVisibility(View.VISIBLE);
                                    rb4.setText(option4);
                                }
                                else
                                    rb4.setVisibility(View.GONE);
                                if(option5 != null) {
                                    rb5.setVisibility(View.VISIBLE);
                                    rb5.setText(option5);
                                }
                                else
                                    rb5.setVisibility(View.GONE);

                                break;
                            }


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(QuestionsActivity.this, "sorry ! no questions left", Toast.LENGTH_SHORT).show();
                            Toast.makeText(QuestionsActivity.this, "oups ! try later", Toast.LENGTH_SHORT).show();
                            rb1.setVisibility(View.INVISIBLE);
                            rb2.setVisibility(View.INVISIBLE);
                            rb3.setVisibility(View.INVISIBLE);
                            rb4.setVisibility(View.INVISIBLE);
                            rb5.setVisibility(View.INVISIBLE);
                            mQuestion.setText("");
                        }
                    }
                });


    }

    private boolean ButtonsUnchecked()
    {
        if(!rb1.isChecked() && !rb2.isChecked() && !rb3.isChecked() && !rb4.isChecked() && !rb5.isChecked())
        {
            Toast.makeText(this, "please pick a choice", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent i = new Intent(this,HomeActivity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_settings:
               Intent i =new Intent(this,SettingsActivity.class);
               startActivity(i);
                return true;
            case R.id.nav_help:
                Intent j =new Intent(this,HelpActivity.class);
                startActivity(j);
                return true;
            case R.id.nav_report:
                Intent k =new Intent(this,ReportActivity.class);
                startActivity(k);
                return true;
            case R.id.nav_share:
                return true;
            case R.id.nav_rate_us:
                return true;
            case R.id.nav_log_out:
                SharedPreferences preferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
                preferences.edit().clear().apply();
                mAuth=FirebaseAuth.getInstance();
                mAuth.signOut();
                LoginManager.getInstance().logOut();

                Intent intent =new Intent(QuestionsActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
        }


    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);

            return true;
    }
}