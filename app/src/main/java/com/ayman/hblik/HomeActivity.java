package com.ayman.hblik;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class HomeActivity extends AppCompatActivity {
    //todo edit content
    //todo edit other activities content like settings and help
SharedPreferences preferences;
int score;
String id_user;
TextView Hscore,mName;
ImageView profileImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        LinearLayout Hask,Hanswer,Hstats,Hexit;

        Hscore=findViewById(R.id.Hscore);
        Hask=findViewById(R.id.Hask);
        Hanswer=findViewById(R.id.Hanswer);
        Hstats=findViewById(R.id.Hstats);
        Hexit=findViewById(R.id.Hexit);

        preferences=getSharedPreferences("userPreferences",0);
        id_user=preferences.getString("id_user",null);
        String firstName =preferences.getString("first_name",null);
        String lastName =preferences.getString("last_name",null);
        score =preferences.getInt("score",0);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.menu_home));

        /*toolbar.setNavigationIcon(R.drawable.menuic);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
*/
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.nav_settings:
                        Intent i =new Intent(HomeActivity.this,SettingsActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.nav_help:
                        Intent j =new Intent(HomeActivity.this,HelpActivity.class);
                        startActivity(j);
                        return true;

                    case R.id.nav_share:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                "Hey check out my app at: https://play.google.com");
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        return true;
                    case R.id.nav_rate_us:
                        Intent t = new Intent(HomeActivity.this,RateUsActivity.class);
                        startActivity(t);
                        return true;
                    case R.id.nav_log_out:
                        SharedPreferences preferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
                        preferences.edit().clear().apply();
                        Intent intent =new Intent(HomeActivity.this,LoginActivity.class);
                        FirebaseAuth mAuth;
                        mAuth= FirebaseAuth.getInstance();
                        mAuth.signOut();
                        LoginManager.getInstance().logOut();

                        startActivity(intent);
                        finish();
                        return true;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });



        View v0 = navigationView.getHeaderView(0);
        profileImg = v0.findViewById(R.id.profilePhoto);
        View v = navigationView.getHeaderView(0);
        mName = v.findViewById(R.id.name);
        mName.setText(getResources().getString(R.string.name,firstName,lastName));
        setProfilePhoto(id_user);




        Hscore.setText(getResources().getString(R.string.score_home,score));

        Hask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(score<25)
                {
                    Toast.makeText(HomeActivity.this, "You should have a least 25 pts", Toast.LENGTH_SHORT).show();
                }

               else{
                    Intent i = new Intent(HomeActivity.this,CreateQuestionActivity.class);
                    startActivity(i);
                }
            }
        });

        Hanswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, QuestionsActivity.class);
                startActivity(i);
            }
        });


        Hstats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, UserActivityActivity.class);
                startActivity(i);
            }
        });

        Hexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
                preferences.edit().clear().apply();
                Intent intent =new Intent(HomeActivity.this,LoginActivity.class);
                FirebaseAuth mAuth;
                mAuth= FirebaseAuth.getInstance();
                mAuth.signOut();
                LoginManager.getInstance().logOut();

                startActivity(intent);
                finish();
            }
        });

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
                profileImg.setImageBitmap(bmp);            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }

        });

    }

    @Override
    public void onBackPressed(){
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

}
