package com.ayman.hblik;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    EditText curr_pass,new_pass;
    ImageView change_photo;
    CircleImageView new_photo;
    Button button_save;
    ProgressBar progressBar;
    String id_user,user_pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        toolbar.setTitle(getResources().getString(R.string.settings));

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.back_ic);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        curr_pass=findViewById(R.id.curr_pass);
        new_pass=findViewById(R.id.new_pass);
        change_photo=findViewById(R.id.change_photo);
        change_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     onChoosePhoto();
            }
        });
        new_photo=findViewById(R.id.new_photo);
        button_save=findViewById(R.id.button_save);
        progressBar=findViewById(R.id.progress_settings);
        progressBar.setVisibility(View.GONE);

        SharedPreferences preferences = getSharedPreferences("userPreferences",MODE_PRIVATE);
        id_user=preferences.getString("id_user",null);

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change();
            }
        });

    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private void change() {
        progressBar.setVisibility(View.VISIBLE);
        final String pass=new_pass.getText().toString().trim();
        if(pass.length()<8 && pass.length()>0){
            new_pass.setError("at least 8 characters");
            progressBar.setVisibility(View.GONE);
            return;
        }

        else if(pass.length()==0){
            if(profile!=null)
                savePhotoInFirebase(profile);

            else
                Toast.makeText(this, "No changes", Toast.LENGTH_SHORT).show();

            progressBar.setVisibility(View.GONE);
            return;
        }
        else{
        final String pass0=curr_pass.getText().toString().trim();
        if(curr_pass.length()<8) {
            curr_pass.setError("Incorrect password");
            progressBar.setVisibility(View.GONE);
            return;
        }
        db.collection("userh").document(id_user)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful() && task.getResult()!=null){
                            DocumentSnapshot document =task.getResult();
                            user_pass=document.getString("password");

                            if(profile!=null){
                                savePhotoInFirebase(profile);
                            }
                            if(user_pass==null){
                                curr_pass.setError("Incorrect");
                                progressBar.setVisibility(View.GONE);

                            }
                            else if(user_pass.equals(pass0)){
                                WriteBatch batch = db.batch();
                                DocumentReference sfRef = db.collection("userh").document(id_user);
                                batch.update(sfRef, "password", pass);
                                batch.commit();

                                Log.d(TAG, "onComplete: "+pass0+"  "+user_pass+"  "+pass);
                            }
                            else{
                                curr_pass.setError("Incorrect password");

                            }


                        }
                    }
                });
        }

        try {
            wait(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        progressBar.setVisibility(View.GONE);



    }

    private void savePhotoInFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        Log.d(TAG, "savePhotoInFirebase: "+id_user);
        StorageReference mountainsRef = storageRef.child("profilePhotos/"+id_user+".png");

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }

    Bitmap profile;
    private void saveBitmap(Bitmap bitmap){
        profile=bitmap;
    }
    private static final int GET_FROM_GALLERY = 3;
    private void onChoosePhoto() {

        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                new_photo.setImageBitmap(bitmap);
                saveBitmap(bitmap);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


}


