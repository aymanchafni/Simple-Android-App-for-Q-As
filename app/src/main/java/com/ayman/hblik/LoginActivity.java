package com.ayman.hblik;

import android.annotation.SuppressLint;
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
import com.facebook.login.LoginManager;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class LoginActivity extends AppCompatActivity {
    private ProgressBar progress;
    private static AccessToken accessToken;
    private FirebaseFirestore db;
    TextInputLayout mEmail, mPassword;
    Button login;
    SignInButton googleSignIn;
    EditText pEt, eEmail;
    TextView register;
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private  GoogleSignInClient mGoogleSignInClient;
    private static SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
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
        SharedPreferences preferences = getSharedPreferences("userPreferences", 0);
        editor = preferences.edit();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ConnectivityHelper.isConnectedToNetwork(LoginActivity.this)){
                    Toast.makeText(LoginActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
                    return;
                }

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
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // [START_EXCLUDE]
                progress.setVisibility(View.GONE);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // [START_EXCLUDE]
                progress.setVisibility(View.GONE);
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
                progress.setVisibility(View.GONE);
                Toast.makeText(this, "Error getting google account", Toast.LENGTH_SHORT).show();

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
                            final String firstName=object.getString("first_name");
                            final String lastName=object.getString("last_name");
                            final String fb_photo_url=object.getJSONObject("picture").getJSONObject("data").getString("url");


                            SignInGoogleFb(firstName,lastName,email,null, fb_photo_url,true);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Error getting facebook account", Toast.LENGTH_SHORT).show();

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
                            Log.d(TAG, "onComplete: "+ Objects.requireNonNull(task.getResult()).getUser());
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                            progress.setVisibility(View.GONE);

                        }

                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {

        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    final String email = acct.getEmail();
                    final String firstName = acct.getGivenName();
                    final String lastName = acct.getFamilyName();
                    final Uri photo_google_uri = acct.getPhotoUrl();

                    assert photo_google_uri != null;
                    SignInGoogleFb(firstName,lastName,email,null,photo_google_uri.toString(),false);


                }else {
                    Toast.makeText(LoginActivity.this, "Error connecting to google account", Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);

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

    FirebaseUser user;

    private void onLogin() {

        progress.setVisibility(View.VISIBLE);
        final String email = Objects.requireNonNull(mEmail.getEditText()).getText().toString().trim();
        final String password = Objects.requireNonNull(mPassword.getEditText()).getText().toString().trim();
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
                                mEmail.setError(null);
                                mPassword.setError(null);
                                progress.setVisibility(View.GONE);
                            }

                            else {

                                final Intent i = new Intent(getApplicationContext(), HomeActivity.class);

                                final DocumentSnapshot document = task.getResult().getDocuments().get(0);

                                    final boolean verified=Objects.requireNonNull(document.getBoolean("verified"));
                                    final String id_user = document.getId();
                                     //todo review this mess-----------------------------------------
                                    if(!verified){
                                        mAuth.signInWithEmailAndPassword(email,"AnOnYmOuS")
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if(task.isSuccessful() && task.getResult()!=null){
                                                     user = task.getResult().getUser();
                                                    Log.d(TAG, "onComplete: user "+user);
                                                    if(!user.isEmailVerified())
                                                    {
                                                        Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                                                        final TextView tv=findViewById(R.id.send_another_verification);
                                                        tv.setVisibility(View.VISIBLE);
                                                        tv.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                user.sendEmailVerification();
                                                                tv.setVisibility(View.GONE);

                                                                Toast.makeText(LoginActivity.this, "we have sent to you another verification email !", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                        progress.setVisibility(View.GONE);
                                                        //return;
                                                    }
                                                    else{
                                                        WriteBatch batch = db.batch();
                                                        DocumentReference sfRef = db.collection("userh").document(id_user);
                                                        batch.update(sfRef, "verified", true);
                                                        batch.commit();

                                                        getUserInfo(document,id_user);


                                                        startActivity(i);
                                                    }
                                                }
                                                else {
                                                    Log.d(TAG, "onComplete: no user "+user);

                                                }
                                            }
                                        });


                                    }

                                //getUserInfo(document);
                                else {
                                    getUserInfo(document,id_user);

                                startActivity(i);
                                Log.d(TAG, "onComplete: task successeful");


                                }

                            }

                        } else {

                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(LoginActivity.this, "Error connecting to the server", Toast.LENGTH_SHORT).show();
                            progress.setVisibility(View.GONE);

                        }
                    }

                    private void getUserInfo(DocumentSnapshot document,String id_user) {
                        String firstName = document.getString("firstName");
                        String lastName = document.getString("lastName");
                        final int score = Objects.requireNonNull(document.getLong("score")).intValue();
                        final int id_last_question = Objects.requireNonNull(document.getLong("id_last_question")).intValue();

                        //todo : do this code in RegisterActivity so that wz can take user's infos once and let it also here in case the user deleted the app or its data...


                        editor.putString("first_name", firstName);
                        editor.putString("last_name", lastName);
                        editor.putInt("score", score);
                        editor.putInt("id_last_question", id_last_question);
                        editor.putString("id_user", id_user);
                        editor.apply();

                    }
                });


    }

    private void SignInGoogleFb(final String firstName,final String lastName,final String email, final String birthday, final String uri, final Boolean fb) {
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
                                 data.put("sign_in_method", "fb");
                                else
                                    data.put("sign_in_method", "gg");

                                data.put("id_last_question", 1);
                                data.put("score", 0);

                                db.collection("userh").add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "onComplete: i'm heree");
                                            final String id_user = Objects.requireNonNull(task.getResult()).getId();

                                            editor.putString("first_name", firstName);
                                            editor.putString("last_name", lastName);
                                            editor.putInt("score", 0);
                                            editor.putInt("id_last_question", 1);
                                            editor.putString("id_user", id_user);
                                            editor.apply();
                                            assert uri != null;
                                            try {
                                                saveGooglePhotoInFirebase(uri,id_user);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                Log.d(TAG, "onComplete: " + e);
                                                Log.d(TAG, "onComplete: " + uri);
                                            }
                                            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                            startActivity(i);
                                        } else {
                                            Log.d(TAG, "Error connecting to the server");
                                            progress.setVisibility(View.GONE);

                                        }

                                    }
                                });

                            } else {

                                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                    final String sign_in_method=document.getString("sign_in_method");


                               if(sign_in_method == null){
                                   Toast.makeText(LoginActivity.this, "This email is already associated with a HBLIk account", Toast.LENGTH_SHORT).show();
                                   progress.setVisibility(View.GONE);

                               }

                                else if(sign_in_method.equals("fb")){
                                        if(fb){
                                            signin_proc(document);
                                        }
                                        else{
                                            Toast.makeText(LoginActivity.this, "Email of this facebook account already associated with another account", Toast.LENGTH_SHORT).show();
                                            LoginManager.getInstance().logOut();
                                            progress.setVisibility(View.GONE);

                                        }
                                    }
                                else if(sign_in_method.equals("gg")){
                                    if(!fb){
                                        signin_proc(document);
                                    }
                                    else{
                                        Toast.makeText(LoginActivity.this, "Email of this google account already associated with another account", Toast.LENGTH_SHORT).show();
                                        progress.setVisibility(View.GONE);

                                    }
                                }



                                    //------------------------------------------------------------------------


                            }
                        }else {
                            Toast.makeText(LoginActivity.this, "Error connecting to the server", Toast.LENGTH_SHORT).show();
                            progress.setVisibility(View.GONE);

                        }

                    }

                    private void signin_proc(DocumentSnapshot document) {
                        final String id_user = document.getId();
                        final String firstName = document.getString("firstName");
                        final String lastName = document.getString("lastName");
                        final int score = Objects.requireNonNull(document.getLong("score")).intValue();
                        final int id_last_question = Objects.requireNonNull(document.getLong("id_last_question")).intValue();

                        editor.putString("first_name", firstName);
                        editor.putString("last_name", lastName);
                        editor.putInt("score", score);
                        editor.putInt("id_last_question", id_last_question);
                        editor.putString("id_user", id_user);
                        editor.apply();

                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(i);
                    }
                });


    }


    public void saveGooglePhotoInFirebase(String uri,String id_user) throws IOException {
if(uri==null)
    return;

        DownloadFromURL download = new DownloadFromURL();
        download.execute(uri,id_user);

    }

     @SuppressLint("StaticFieldLeak")
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

                StorageReference mountainsRef = storageRef.child("profilePhotos/" + fileUrl[1] + ".png");

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
        static boolean isConnectedToNetwork(Context context) {
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



