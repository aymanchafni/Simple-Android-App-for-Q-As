package com.ayman.hblik;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
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
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    TextInputLayout mEmail, mPassword;
    Button register, login, googleSignIn;
    EditText pEt,eEmail;
    private static final String TAG = "MainActivity";
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
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            final String user_name=user.getDisplayName();
                            final String user_id=user.getUid();

                            final Intent i =new Intent(LoginActivity.this,QuestionsActivity.class);
                            final Bundle b=new Bundle();
                            b.putString("firstName",user_name);
                            b.putString("lastName","");
                            b.putString("id_user",user_id);

                            FirebaseFirestore db=FirebaseFirestore.getInstance();
                            db.collection("userh").whereEqualTo(FieldPath.documentId(),user_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful() && task.getResult() !=null)
                                    {
                                        for (QueryDocumentSnapshot document : task.getResult()){
                                            int id_last_question=document.getLong("id_last_question").intValue();
                                            int score=document.getLong("score").intValue();

                                            b.putInt("id_last_question",id_last_question);
                                            b.putInt("score",score);

                                            break;
                                        }

                                    }else{
                                        createNewUser(user);
                                        b.putInt("id_last_question",1);
                                        b.putInt("score",0);
                                    }

                                    i.putExtras(b);
                                    startActivity(i);

                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }

    private void createNewUser(FirebaseUser user) {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        String user_id=user.getUid();
        String name=user.getDisplayName();
        String email=user.getEmail();
        Map<String, Object> data = new HashMap<>();
        data.put("id",user_id);
        data.put("id_last_question",1);
        data.put("Name",name);
        data.put("score",0);
        data.put("email",email);


        db.collection("userh").document(user_id).set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("DDDDDDDDD HHHHHHHHHHHHH","user added successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("NOOOOOOOOOOOOOOOOOOOO","lam yahduth chay2");
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
                              user_photo_id=document.getString("user_photo_id");
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
                                editor.putString("user_photo_id",user_photo_id);
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