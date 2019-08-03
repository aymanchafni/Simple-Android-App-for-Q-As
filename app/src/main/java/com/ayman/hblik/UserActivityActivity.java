package com.ayman.hblik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivityActivity extends AppCompatActivity {
    private static final String TAG = "UserActivityActivity";

    private ArrayList<String> questions=new ArrayList<>();
    private ArrayList<String> answerNbrs=new ArrayList<>();
    private ArrayList<String> ids=new ArrayList<>();

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
        int score =preferences.getInt("score",0);
        String user_photo_id =preferences.getString("user_photo_id",null);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

        View v0 = navigationView.getHeaderView(0);
        imageView = v0.findViewById(R.id.profilePhoto);
        View v = navigationView.getHeaderView(0);
        mName = v.findViewById(R.id.name);
        mScore = findViewById(R.id.score);
        mName.setText(firstName+" "+lastName);
        mScore.setText("score : "+score);



        setProfilePhoto(user_photo_id);




    initQuestions();
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
                                     int answerNbr;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                question=document.getString("question");
                                answerNbr=document.getLong("total_answers").intValue();
                                id=Integer.toString(document.getLong("id").intValue());

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
