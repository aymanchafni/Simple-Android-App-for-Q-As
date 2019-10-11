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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivityActivity extends AppCompatActivity {
    private static final String TAG = "UserActivityActivity";
    ProgressBar progress;
    private ArrayList<String> questions=new ArrayList<>();
    private ArrayList<String> answerNbrs=new ArrayList<>();
    private ArrayList<String> ids=new ArrayList<>();
    private int score;
    private String id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_activity);
        progress=findViewById(R.id.progress_user_activity);

        SharedPreferences preferences = getSharedPreferences("userPreferences",MODE_PRIVATE);
        id_user=preferences.getString("id_user",null);

        score =preferences.getInt("score",0);


        Toolbar toolbar = findViewById(R.id.toolbar_user_activity);
        toolbar.setTitle(getResources().getString(R.string.my_activity));

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_ic);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




        final BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                switch (id) {
                    case R.id.nav_home:
                        Intent h = new Intent(UserActivityActivity.this, HomeActivity.class);
                        startActivity(h);
                        break;
                    case R.id.nav_answer:
                        Intent j = new Intent(UserActivityActivity.this, QuestionsActivity.class);

                        startActivity(j);
                        break;
                    case R.id.nav_ask:
                        if (score < 50) {
                            Toast.makeText(UserActivityActivity.this, "You should have at least 50 pts", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(UserActivityActivity.this, CreateQuestionActivity.class);
                            startActivity(intent);
                        }

                        break;
                    case R.id.MyQuestions:
                        Log.d("e", "onNavigationItemSelected: starting userA");
                        Intent i = new Intent(UserActivityActivity.this, UserActivityActivity.class);
                        Bundle b =new Bundle();
                        b.putString("id_user", id_user);
                        i.putExtras(b);
                        startActivity(i);
                        break;
                }
                return true;
            }
        });
        bottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(false);
        bottomNavigationView.getMenu().findItem(R.id.MyQuestions).setChecked(true);



    initQuestions();
    }





    @Override
    public void onBackPressed(){
        Intent i = new Intent(this,HomeActivity.class);
        startActivity(i);
    }



    private void initQuestions() {
        Log.d(TAG, "initQuestions: called. ");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("questions")
                .whereEqualTo("id_questioner", id_user)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                                     String question,id;
                                     int answerNbr,Id;
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                question=document.getString("question");
                                answerNbr= Objects.requireNonNull(document.getLong("total_answers")).intValue();
                                Id= Objects.requireNonNull(document.getLong("id")).intValue();
                                id=Integer.toString(Id);

                                questions.add(question);
                                answerNbrs.add(""+answerNbr);
                                ids.add(id);

                                Log.d(TAG, "onComplete: question added!");
                                Log.d(TAG, "onComplete: answerNbr added");
                            }

                            if(questions.isEmpty())
                                Toast.makeText(UserActivityActivity.this, "you don't have any question yet", Toast.LENGTH_SHORT).show();
                            else
                            initRecyclerView();

                            progress.setVisibility(View.GONE);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());

                        }
                    }
                });
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: called. ");
        Log.d(TAG, "initRecyclerView: "+questions);
        RecyclerView recyclerView=findViewById(R.id.recyclerview);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this,questions,answerNbrs,ids);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}
