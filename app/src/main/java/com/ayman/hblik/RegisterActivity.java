package com.ayman.hblik;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.validator.EmailValidator;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class RegisterActivity extends AppCompatActivity {

    TextInputLayout mfName,mlName,mBirthday,mEmail,mPassword,mConfirmPassword;
    Button backToPrincipal,gotoQuestions,chooseProfilePhoto;
    EditText pcEt;
    Bitmap profile;
    private String user_photo_id,id_user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        mfName=findViewById(R.id.fName);
        mlName=findViewById(R.id.lName);
        mBirthday=findViewById(R.id.birthday);
        mEmail=findViewById(R.id.emailR);
        mPassword=findViewById(R.id.passwordR);
        mConfirmPassword=findViewById(R.id.passwordConfirm);
        pcEt=findViewById(R.id.pcEt);


        backToPrincipal=findViewById(R.id.backToPrincipal);
        gotoQuestions=findViewById(R.id.gotoQuestions);
        chooseProfilePhoto=findViewById(R.id.chooseProfilePhotoB);

        backToPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });

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
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                saveBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
        UUID uuid=new UUID(64,128);
        String photoId=uuid.toString();
        user_photo_id =photoId;
        StorageReference mountainsRef = storageRef.child("profilePhotos/"+photoId+".png");

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

    private void onBack(){
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);

    }

    private void onDone() {
        if(FieldError())
        {return;}

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();

        String fName =mfName.getEditText().getText().toString().trim();
        String lName =mlName.getEditText().getText().toString().trim();
        String birthday =mBirthday.getEditText().getText().toString().trim();
        String email =mEmail.getEditText().getText().toString().trim();
        String password =mPassword.getEditText().getText().toString().trim();

        user.put("firstName", fName);
        user.put("lastName", lName);
        user.put("birthday", birthday);
        user.put("email", email);
        user.put("password", password);
        user.put("id_last_question",1);
        user.put("score",0);
        user.put("user_photo_id",user_photo_id);



// Add a new document with a generated ID
        db.collection("userh")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Y", "DocumentSnapshot added with ID: " + documentReference.getId());
                        id_user= documentReference.getId();
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("N", "Error adding document", e);
                    }
                });

        savePhotoInFirebase(profile);


        Intent i =new Intent(this,LoginActivity.class);
        startActivity(i);


    }
private boolean FieldError() {
    mfName.setError(null);
    mlName.setError(null);
    mBirthday.setError(null);
    mEmail.setError(null);
    mPassword.setError(null);
    mConfirmPassword.setError(null);

    String fName =mfName.getEditText().getText().toString().trim();
    String lName =mlName.getEditText().getText().toString().trim();
    String birthday =mBirthday.getEditText().getText().toString().trim();
    String email =mEmail.getEditText().getText().toString().trim();
    String password =mPassword.getEditText().getText().toString().trim();
    String passwordConfirm =mConfirmPassword.getEditText().getText().toString().trim();

if(fName.equals("") || lName.equals("") || birthday.equals("") || password.equals("") || email.equals("") || passwordConfirm.equals("")
        || fName.length()==1 || lName.length()==1 || fName.length()>15 || lName.length()>15 || !birthday.contains("-") || !email.contains("@") || !email.contains(".") || password.length()<8 || !password.equals(passwordConfirm) )
{
    if (fName.equals("")) {
        mfName.setError("Field can't be empty");
    }


    if (lName.equals("")) {
        mlName.setError("Field can't be empty");
    }

    if (birthday.equals("")) {
        mBirthday.setError("Field can't be empty");
    }
    if (email.equals("")) {
        mEmail.setError("Field can't be empty");
    }
    if (password.equals("")) {
        mPassword.setError("Field can't be empty");
    }
    if (passwordConfirm.equals("")) {
        mConfirmPassword.setError("Field can't be empty");
    }

    if (fName.length() == 1) {
        mfName.setError("First name too short");
    }

    if (fName.length() > 15) {
        mfName.setError("First name too long");
    }

    if (lName.length() == 1) {
        mlName.setError("Last name too short");
    }
    if (lName.length() > 15) {
        mlName.setError("Last name too long");
    }

    EmailValidator validator = EmailValidator.getInstance();
    if (!validator.isValid(email)) {
        mEmail.setError("Invalid email");
    }

    if (!birthday.contains("-") && !birthday.contains("/")) {
        mBirthday.setError("Invalid date format");
    }

    //TODO: instead of the textView, create 3 comboBoxes for year, month and day
    //TODO like that i don't have to check if the date is correct or not

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
