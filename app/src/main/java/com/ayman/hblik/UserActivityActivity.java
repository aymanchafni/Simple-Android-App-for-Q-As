package com.ayman.hblik;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

    private ArrayList<String> questions=new ArrayList<>();
    private ArrayList<String> answerNbrs=new ArrayList<>();
    private ArrayList<String> ids=new ArrayList<>();
    private int score;
    private String id_user;
    TextView mName,mScore;
    CircleImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_activity);

        SharedPreferences preferences = getSharedPreferences("userPreferences",MODE_PRIVATE);
        id_user=preferences.getString("id_user",null);
        String firstName =preferences.getString("first_name",null);
        String lastName =preferences.getString("last_name",null);
        score =preferences.getInt("score",0);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
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
                        if (score < 25) {
                            Toast.makeText(UserActivityActivity.this, "You should have at least 25 pts", Toast.LENGTH_SHORT).show();
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


        View v0 = navigationView.getHeaderView(0);
        imageView = v0.findViewById(R.id.profilePhoto);
        View v = navigationView.getHeaderView(0);
        mName = v.findViewById(R.id.name);
        mScore = findViewById(R.id.score);
        mName.setText(getResources().getString(R.string.name,firstName,lastName));
        mScore.setText(getResources().getString(R.string.score,score));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.nav_settings:
                        Intent i =new Intent(UserActivityActivity.this,SettingsActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.nav_help:
                        Intent j =new Intent(UserActivityActivity.this,HelpActivity.class);
                        startActivity(j);
                        return true;
                    case R.id.nav_report:
                        Intent k =new Intent(UserActivityActivity.this,ReportActivity.class);
                        startActivity(k);
                        return true;
                    case R.id.nav_share:
                        return true;
                    case R.id.nav_rate_us:
                        return true;
                    case R.id.nav_log_out:
                        SharedPreferences preferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
                        preferences.edit().clear().apply();
                        Intent intent =new Intent(UserActivityActivity.this,LoginActivity.class);
                        FirebaseAuth mAuth;
                        mAuth= FirebaseAuth.getInstance();
                        mAuth.signOut();
                        LoginManager.getInstance().logOut();
                        startActivity(intent);
                        finish();
                        return true;
                }
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        setProfilePhoto(id_user);

    initQuestions();
    }
    @Override
    public void onBackPressed(){
        Intent i = new Intent(this,HomeActivity.class);
        startActivity(i);
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
                            initRecyclerView();


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
