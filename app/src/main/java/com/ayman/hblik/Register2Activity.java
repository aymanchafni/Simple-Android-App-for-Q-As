package com.ayman.hblik;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

import org.apache.commons.validator.EmailValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register2Activity extends AppCompatActivity {


    private TextInputLayout mEmail, mPassword, mConfirmPassword;
    private Button signUp;
    private ProgressBar progress;
    private static FirebaseAuth mAuth;

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
        progress = findViewById(R.id.progress_register);
        mEmail = findViewById(R.id.emailR);
        mPassword = findViewById(R.id.passwordR);
        mConfirmPassword = findViewById(R.id.passwordConfirm);
        EditText pcEt = findViewById(R.id.pcEt);


        signUp = findViewById(R.id.signUp);


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDone();
            }
        });


        pcEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //do what you want on the press of 'done'
                    signUp.performClick();
                }
                return false;
            }
        });


    }




    private void onDone() {

        if (!LoginActivity.ConnectivityHelper.isConnectedToNetwork(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            progress.setVisibility(View.GONE);
            return;
        } else if (FieldError()) {
            return;
        }

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        final Map<String, Object> user = new HashMap<>();

        Bundle b = getIntent().getExtras();
        assert b != null;

        final String fName = b.getString("first_name");
        final String lName = b.getString("last_name");
        final String birthday = b.getString("birthday");
        final String email = Objects.requireNonNull(mEmail.getEditText()).getText().toString().trim();
        db.collection("userh")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            if (!Objects.requireNonNull(task.getResult()).isEmpty()) {
                                mEmail.setError("This email already exists !");

                            } else {

                                String password = Objects.requireNonNull(mPassword.getEditText()).getText().toString().trim();


                                user.put("firstName", fName);
                                user.put("lastName", lName);
                                user.put("birthday", birthday);
                                user.put("email", email);
                                user.put("password", password);
                                user.put("id_last_question", 0);
                                user.put("score", 0);
                                user.put("verified", false);


// Add a new document with a generated ID
                                mAuth = FirebaseAuth.getInstance();
                                mAuth.createUserWithEmailAndPassword(email, "AnOnYmOuS").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                                            db.collection("userh")
                                                    .add(user)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {

                                                            mAuth.getCurrentUser().sendEmailVerification();

                                                            Intent i = new Intent(Register2Activity.this, EmailVerificationActivity.class);
                                                            startActivity(i);

                                                        }
                                                    })

                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                        }
                                                    });


                                        } else {
                                            Toast.makeText(Register2Activity.this, "Error connecting to server", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });


                            }
                        } else {
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

        if (password.equals("") || email.equals("") || passwordConfirm.equals("")
                || !email.contains("@") || !email.contains(".") || password.length() < 8 || !password.equals(passwordConfirm)) {

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

