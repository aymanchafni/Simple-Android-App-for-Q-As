package com.ayman.hblik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class AskOrAnsActivity extends AppCompatActivity {
TextView textView;
TextView mName,mScore;
CircleImageView imageView;
NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_or_ans);
        textView =  findViewById(R.id.textview);
        SharedPreferences preferences = getSharedPreferences("userPreferences",MODE_PRIVATE);
        String id_user=preferences.getString("id_user",null);
        String name =preferences.getString("name",null);
        int score =preferences.getInt("score",0);
        String user_photo_id =preferences.getString("user_photo_id",null);

        View v0 = navigationView.getHeaderView(0);
        imageView = v0.findViewById(R.id.profilePhoto);
        View v = navigationView.getHeaderView(0);
        mName = v.findViewById(R.id.name);
        mScore = findViewById(R.id.score);
        mName.setText(name);
        mScore.setText(score);


        setProfilePhoto(user_photo_id);



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
}
