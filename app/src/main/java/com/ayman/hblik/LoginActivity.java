package com.ayman.hblik;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class LoginActivity extends AppCompatActivity {
    ProgressBar progress;
    AccessToken accessToken;
    FirebaseFirestore db;
    TextInputLayout mEmail, mPassword;
    Button login;
    SignInButton googleSignIn;
    EditText pEt, eEmail;
    TextView register;
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

    private GoogleSignInClient mGoogleSignInClient;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    String firstName;
    String lastName;
    String id_user;

    int score;
    private int id_last_question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        Objects.requireNonNull(getSupportActionBar()).hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        progress=findViewById(R.id.progress);
        mEmail = findViewById(R.id.emailIn);
        eEmail = findViewById(R.id.eEmail);
        mPassword = findViewById(R.id.passwordIn);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
        googleSignIn = findViewById(R.id.googleSignIn);
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
        db = FirebaseFirestore.getInstance();

        // [END initialize_auth]

        // [START initialize_fblogin]
        // Initialize Facebook Login button
        LoginButton loginButton = findViewById(R.id.facebookSignIn);
        mCallbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                accessToken=loginResult.getAccessToken();
                handleFacebookAccessToken(accessToken);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // [START_EXCLUDE]

                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
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

        if(!ConnectivityHelper.isConnectedToNetwork(this)){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            progress.setVisibility(View.GONE);
        }

        else if (FacebookSdk.isFacebookRequestCode(requestCode)) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        fbLogin();
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        else if (requestCode == RC_SIGN_IN) {


            // todo handle the error of not being connected to internet everywhere
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
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
    private void fbLogin(){
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("LoginActivity", response.toString());

                        // Application code
                        try {
                            String email = object.getString("email");
                            //todo ask for permission from facebook to use users birthday and why not gender too;
                            //todo complete the whole fb sign_in setup

                            //String birthday=object.getString("birthday");
                            firstName=object.getString("first_name");
                            lastName=object.getString("last_name");
                            String fb_photo_url=object.getJSONObject("picture").getJSONObject("data").getString("url");


                            SignInGoogleFb(email,null, fb_photo_url,true);

                            Intent i =new Intent(LoginActivity.this,HomeActivity.class);
                            startActivity(i);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email,picture{url},birthday");
        request.setParameters(parameters);
        request.executeAsync();


    }

    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "onComplete: "+ task.getResult().getUser());
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {

        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        //showProgressDialog();
        // [END_EXCLUDE]
        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    final String email = acct.getEmail();
                    firstName = acct.getGivenName();
                    lastName = acct.getFamilyName();
                    final Uri photo_google_uri = acct.getPhotoUrl();

                    assert photo_google_uri != null;
                    SignInGoogleFb(email,null,photo_google_uri.toString(),false);


                }
            }
        });

        //hideProgressDialog();
    }


    // [END auth_with_google]

    private void googleSignIn() {
        progress.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void onRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


    private void onLogin() {

        progress.setVisibility(View.VISIBLE);
        String email = Objects.requireNonNull(mEmail.getEditText()).getText().toString().trim();
        String password = Objects.requireNonNull(mPassword.getEditText()).getText().toString().trim();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("userh")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                        if (task.isSuccessful()) {
                            if (Objects.requireNonNull(task.getResult()).isEmpty()) {
                                Toast.makeText(LoginActivity.this, "email or password incorrect", Toast.LENGTH_SHORT).show();
                            }

                            else {




                                Intent i = new Intent(getApplicationContext(), HomeActivity.class);


                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    boolean verified=Objects.requireNonNull(document.getBoolean("verified"));
                                    id_user = document.getId();
                                    if(!verified){
                                        if(!Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified())
                                        {
                                            Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                                            TextView tv=findViewById(R.id.send_another_verification);
                                            tv.setVisibility(View.VISIBLE);
                                            tv.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    mAuth.getCurrentUser().sendEmailVerification();
                                                    Toast.makeText(LoginActivity.this, "we have sent to you another verification email !", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            return;
                                        }
                                        else{
                                            WriteBatch batch = db.batch();
                                            DocumentReference sfRef = db.collection("userh").document(id_user);
                                            batch.update(sfRef, "verified", true);
                                            batch.commit();
                                        }
                                    }
                                    firstName = document.getString("firstName");
                                    lastName = document.getString("lastName");
                                    score = Objects.requireNonNull(document.getLong("score")).intValue();
                                    id_last_question = Objects.requireNonNull(document.getLong("id_last_question")).intValue();


                                    //todo : do this code in RegisterActivity so that wz can take user's infos once and let it also here in case the user deleted the app or its data...

                                    break;
                                }
                                preferences = getSharedPreferences("userPreferences", 0);

                                editor = preferences.edit();
                                editor.putString("first_name", firstName);
                                editor.putString("last_name", lastName);
                                editor.putInt("score", score);
                                editor.putInt("id_last_question", id_last_question);
                                editor.putString("id_user", id_user);
                                editor.apply();

                                startActivity(i);
                                Log.d(TAG, "onComplete: task successeful");

                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(LoginActivity.this, "email or password incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void SignInGoogleFb(final String email, final String birthday, final String uri, final Boolean fb) {
        db.collection("userh")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                        if (task.isSuccessful()) {
                            if (Objects.requireNonNull(task.getResult()).isEmpty()) {


                                Log.d(TAG, "onComplete: i'm here!");


                                Map<String, Object> data = new HashMap<>();

                                data.put("firstName", firstName);
                                data.put("lastName", lastName);
                                data.put("email", email);
                                data.put("birthday", birthday);
                                if(fb)
                                 data.put("sign_in_method", "facebook");
                                else
                                    data.put("sign_in_method", "google");

                                data.put("id_last_question", 1);
                                data.put("score", 0);

                                db.collection("userh").add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "onComplete: i'm heree");
                                            id_user = Objects.requireNonNull(task.getResult()).getId();
                                            preferences = getSharedPreferences("userPreferences", 0);

                                            editor = preferences.edit();
                                            editor.putString("first_name", firstName);
                                            editor.putString("last_name", lastName);
                                            editor.putInt("score", 0);
                                            editor.putInt("id_last_question", 1);
                                            editor.putString("id_user", id_user);
                                            editor.apply();
                                            assert uri != null;
                                            try {
                                                saveGooglePhotoInFirebase(uri);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                Log.d(TAG, "onComplete: " + e);
                                                Log.d(TAG, "onComplete: " + uri);
                                            }
                                            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                            startActivity(i);
                                        } else {
                                            Log.d(TAG, "onComplete: error adding docCOD");
                                        }

                                    }
                                });

                            } else {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    id_user = document.getId();
                                    firstName = document.getString("firstName");
                                    lastName = document.getString("lastName");
                                    score = Objects.requireNonNull(document.getLong("score")).intValue();
                                    id_last_question = Objects.requireNonNull(document.getLong("id_last_question")).intValue();
                                    preferences = getSharedPreferences("userPreferences", 0);

                                    editor = preferences.edit();
                                    editor.putString("first_name", firstName);
                                    editor.putString("last_name", lastName);
                                    editor.putInt("score", score);
                                    editor.putInt("id_last_question", id_last_question);
                                    editor.putString("id_user", id_user);
                                    editor.apply();

                                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(i);
                                    break;
                                }
                            }
                        }
                    }
                });


    }


    public void saveGooglePhotoInFirebase(String uri) throws IOException {
if(uri==null)
    return;

        DownloadFromURL download = new DownloadFromURL();
        download.execute(uri);

    }

     class DownloadFromURL extends AsyncTask<String, String, Bitmap> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Bitmap doInBackground(String... fileUrl) {
            int count;
            Bitmap bmp = null;
            try {
                URL url = new URL(fileUrl[0]);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                // show progress bar 0-100%
                int fileLength = urlConnection.getContentLength();
                InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);
                bmp = BitmapFactory.decodeStream(inputStream);

                byte[] data = new byte[1024];
                long total = 0;
                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / fileLength));
                }
                // flushing output

                inputStream.close();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data1 = baos.toByteArray();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                StorageReference mountainsRef = storageRef.child("profilePhotos/" + id_user + ".png");

                UploadTask uploadTask = mountainsRef.putBytes(data1);
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
            } catch (IOException ex) {
                ex.printStackTrace();
                Log.d(TAG, "doInBackground: "+ex);
            }


            return bmp;
        }

        // progress bar Updating

        protected void onProgressUpdate(String... progress) {
            // progress percentage
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {

        }

        //progress dialog

    }


    public static class ConnectivityHelper {
        public static boolean isConnectedToNetwork(Context context) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            boolean isConnected = false;
            if (connectivityManager != null) {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                isConnected = (activeNetwork != null) && (activeNetwork.isConnectedOrConnecting());
            }

            return isConnected;
        }
    }
}



