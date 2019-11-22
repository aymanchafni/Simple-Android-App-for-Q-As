package com.ayman.hblik;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private  final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText curr_pass, new_pass;
    private ProgressBar progressBar;
    private String id_user, user_pass;

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

        curr_pass = findViewById(R.id.curr_pass);
        new_pass = findViewById(R.id.new_pass);

        Button button_save = findViewById(R.id.button_save);
        progressBar = findViewById(R.id.progress_settings);
        progressBar.setVisibility(View.GONE);

        SharedPreferences preferences = getSharedPreferences("userPreferences", MODE_PRIVATE);
        id_user = preferences.getString("id_user", null);

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_modifications();
            }
        });

        db.collection("userh").document(id_user)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String sign_in_method = Objects.requireNonNull(task.getResult()).getString("sign_in_method");
                    if (sign_in_method != null) {
                        if (sign_in_method.equals("fb")) {
                            curr_pass.setHint("Signed in with facebook");
                            curr_pass.setEnabled(false);
                            new_pass.setHint("Signed in with facebook");
                            new_pass.setEnabled(false);
                        } else if (sign_in_method.equals("gg")) {
                            curr_pass.setHint("Signed in with google");
                            curr_pass.setEnabled(false);
                            new_pass.setHint("Signed in with google");
                            new_pass.setEnabled(false);
                        }
                    }

                }
            }
        });

    }

    private void save_modifications() {
        progressBar.setVisibility(View.VISIBLE);

        if (!LoginActivity.ConnectivityHelper.isConnectedToNetwork(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        final String pass = new_pass.getText().toString().trim();
        if (pass.length() < 8 && pass.length() > 0) {
            new_pass.setError("at least 8 characters");
            progressBar.setVisibility(View.GONE);
            return;
        } else if (pass.length() == 0) {
            Toast.makeText(this, "No changes", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        } else {
            final String pass0 = curr_pass.getText().toString().trim();
            if (curr_pass.length() < 8) {
                curr_pass.setError("Incorrect password");
                progressBar.setVisibility(View.GONE);
                return;
            }
            db.collection("userh").document(id_user)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                DocumentSnapshot document = task.getResult();
                                user_pass = document.getString("password");


                                if (user_pass == null) {
                                    curr_pass.setError("Incorrect password");
                                    progressBar.setVisibility(View.GONE);

                                } else if (user_pass.equals(pass0)) {
                                    WriteBatch batch = db.batch();
                                    DocumentReference sfRef = db.collection("userh").document(id_user);
                                    batch.update(sfRef, "password", pass);
                                    batch.commit();

                                    curr_pass.setText("");
                                    new_pass.setText("");


                                    Toast.makeText(SettingsActivity.this, "   Saved   ", Toast.LENGTH_SHORT).show();

                                } else {
                                    curr_pass.setError("Incorrect password");

                                }


                            } else {
                                Toast.makeText(SettingsActivity.this, "Error has occured !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


        progressBar.setVisibility(View.GONE);


    }


}


