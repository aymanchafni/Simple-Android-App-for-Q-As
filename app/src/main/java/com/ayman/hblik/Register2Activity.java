package com.ayman.hblik;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.validator.EmailValidator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Register2Activity extends AppCompatActivity {


    TextInputLayout mEmail,mPassword,mConfirmPassword;
    Button gotoQuestions;
    ImageView chooseProfilePhoto;
    CircleImageView profilePhoto;
    EditText pcEt;
    Bitmap profile;
    private String id_user;
    private static final String TAG = "Register2Activity";
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register2);
        Toolbar toolbar = findViewById(R.id.toolbar_register2);
        toolbar.setTitle(getResources().getString(R.string.sign_up));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_ic);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //
        mEmail=findViewById(R.id.emailR);
        mPassword=findViewById(R.id.passwordR);
        mConfirmPassword=findViewById(R.id.passwordConfirm);
        pcEt=findViewById(R.id.pcEt);


        gotoQuestions=findViewById(R.id.gotoQuestions);
        chooseProfilePhoto=findViewById(R.id.chooseProfilePhotoB);
        profilePhoto=findViewById(R.id.profilePhotoRegister);


        gotoQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDone();
            }
        });

        chooseProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChoosePhoto();
            }
        });
        pcEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //do what you want on the press of 'done'
                    gotoQuestions.performClick();
                }
                return false;
            }
        });





    }



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
                profilePhoto.setImageBitmap(bitmap);
                saveBitmap(bitmap);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
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
                Toast.makeText(Register2Activity.this, "Photo was not saved", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }

    private void onDone() {
        if(FieldError())
        {return;}

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        final Map<String, Object> user = new HashMap<>();

        Bundle b = getIntent().getExtras();
        assert b != null;

        final String fName = b.getString("first_name");
        final String lName = b.getString("last_name");
        final String birthday=b.getString("birthday");
        final String email = Objects.requireNonNull(mEmail.getEditText()).getText().toString().trim();
        db.collection("userh")
                .whereEqualTo("email",email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()) {
                            if (!Objects.requireNonNull(task.getResult()).isEmpty()) {
                                Log.d(TAG, "onComplete: "+task.getResult().getDocuments());
                                mEmail.setError("This email already exists !");

                            } else {

                                String password = Objects.requireNonNull(mPassword.getEditText()).getText().toString().trim();


                                user.put("firstName", fName);
                                user.put("lastName", lName);
                                user.put("birthday", birthday);
                                user.put("email", email);
                                user.put("password", password);
                                user.put("id_last_question", 1);
                                user.put("score", 0);
                                user.put("verified", false);


// Add a new document with a generated ID
                                db.collection("userh")
                                        .add(user)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                id_user = documentReference.getId();
                                                if (profile != null)
                                                    savePhotoInFirebase(profile);


                                            }
                                        })

                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error adding document", e);
                                            }
                                        });

                                mAuth = FirebaseAuth.getInstance();
                                mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                                            Log.d(TAG, "signInAnonymously:success");

                                            mAuth.getCurrentUser().updateEmail(email);
                                            mAuth.getCurrentUser().sendEmailVerification();
                                            Log.d(TAG, "mail sent................"+mAuth.getCurrentUser().sendEmailVerification());
                                        } else {
                                            Toast.makeText(Register2Activity.this, "Unexpected problem..Please try again.", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                                Intent i = new Intent(Register2Activity.this, EmailVerificationActivity.class);
                                startActivity(i);

                            }
                        }
                        else {
                            Toast.makeText(Register2Activity.this, "Error connecting to server", Toast.LENGTH_SHORT).show();
                        }
                    }
                });



//todo add user to database even if not verified but add cdt verified to login and add this field to every user registered withe email-pass

    }
    private boolean FieldError() {

        mEmail.setError(null);
        mPassword.setError(null);
        mConfirmPassword.setError(null);


        String email = Objects.requireNonNull(mEmail.getEditText()).getText().toString().trim();
        String password = Objects.requireNonNull(mPassword.getEditText()).getText().toString().trim();
        String passwordConfirm = Objects.requireNonNull(mConfirmPassword.getEditText()).getText().toString().trim();

        if(password.equals("") || email.equals("") || passwordConfirm.equals("")
                || !email.contains("@") || !email.contains(".") || password.length()<8 || !password.equals(passwordConfirm) )
        {

            if (email.equals("")) {
                mEmail.setError("Field can't be empty");
            }
            if (password.equals("")) {
                mPassword.setError("Field can't be empty");
            }
            if (passwordConfirm.equals("")) {
                mConfirmPassword.setError("Field can't be empty");
            }


            EmailValidator validator = EmailValidator.getInstance();
            if (!validator.isValid(email)) {
                mEmail.setError("Invalid email");
            }



            if (password.length() < 8) {
                mPassword.setError("Password too short");
            }

            if (!password.equals(passwordConfirm)) {
                mConfirmPassword.setError("Enter the same password");
            }
            return true;
        }

        return false;

    }






}

