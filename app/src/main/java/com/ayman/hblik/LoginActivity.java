package com.ayman.hblik;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class LoginActivity extends AppCompatActivity {

    TextInputLayout mEmail, mPassword;
    Button register, login, googleSignIn;
    EditText pEt,eEmail;
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    String firstName;
    String lastName;
    String id_user;
    String user_photo_id;
int score;
    private int id_last_question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        mEmail = findViewById(R.id.emailIn);
        eEmail=findViewById(R.id.eEmail);
        mPassword = findViewById(R.id.passwordIn);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
        googleSignIn=findViewById(R.id.googleSignIn);
        pEt = findViewById(R.id.pEt);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogin();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegister();
            }
        });
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });

        pEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //do what you want on the press of 'done'
                    login.performClick();
                }
                return false;
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
    }
    // [END on_start_check_user]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                // [END_EXCLUDE]
             }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential);

         firstName=acct.getGivenName();
         lastName =acct.getFamilyName();
        String email=acct.getEmail();
        Uri photo_google_uri = acct.getPhotoUrl();
        Bitmap bitmap = null;

        try {

            bitmap = getThumbnail(photo_google_uri);
            savePhotoInFirebase(bitmap);
            Log.d(TAG, "firebaseAuthWithGoogle: I M here");

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }



        Map<String,Object> data = new HashMap<>();

        FirebaseFirestore db=FirebaseFirestore.getInstance();

        data.put("firstName",firstName);
        data.put("lastName",lastName);
        data.put("email",email);
        data.put("birthday",null);
        data.put("signed_google","yes");
        data.put("id_last_question",1);
        data.put("score",0);

        Task<DocumentReference> adding = db.collection("userh").add(data);
        
        adding.addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()) {
                    id_user = task.getResult().getId();
                    preferences = getSharedPreferences("userPreferences", 0);

                    editor = preferences.edit();
                    editor.putString("first_name", firstName);
                    editor.putString("last_name", lastName);
                    editor.putInt("score", 0);
                    editor.putInt("id_last_question", 1);
                    editor.putString("id_user", id_user);
                    editor.putString("user_photo_id", user_photo_id);
                    editor.apply();

                    Intent i = new Intent(LoginActivity.this, QuestionsActivity.class);
                    startActivity(i);
                }else {
                    Log.d(TAG, "onComplete: error adding docCOD");
                }

            }
        });
        
        

    }

    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException {
       double THUMBNAIL_SIZE=1000;
        InputStream input = this.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither=true;//optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }
    private void savePhotoInFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
       
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


    // [END auth_with_google]

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void onRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


    private void onLogin() {


        String email = mEmail.getEditText().getText().toString().trim();
        String password = mPassword.getEditText().getText().toString().trim();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("userh")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
                                Toast.makeText(LoginActivity.this, "email or password incorrect", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else {



                            Intent i = new Intent(getApplicationContext(), QuestionsActivity.class);



                            for (QueryDocumentSnapshot document : task.getResult()) {
                              firstName=document.getString("firstName");
                              lastName=document.getString("lastName");
                              id_user=document.getId();
                              score =document.getLong("score").intValue();
                                id_last_question =document.getLong("id_last_question").intValue();


                            //todo : do this code in RegisterActivity so that wz can take user's infos once and let it also here in case the user deleted the app or its data...

                                break;
                            }
                                preferences=getSharedPreferences("userPreferences",0);

                                editor=preferences.edit();
                                editor.putString("first_name",firstName);
                                editor.putString("last_name",lastName);
                                editor.putInt("score",score);
                                editor.putInt("id_last_question", id_last_question);
                                editor.putString("id_user",id_user);
                                editor.apply();

                            startActivity(i);
                            Log.d(TAG, "onComplete: task successeful");

                        }

                        }

                        else {
                             Log.d(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(LoginActivity.this, "email or password incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
}