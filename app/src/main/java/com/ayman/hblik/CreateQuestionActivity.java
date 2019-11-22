package com.ayman.hblik;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateQuestionActivity extends AppCompatActivity {
    private static final String ADMOB_AD_UNIT_ID_BANNER = "ca-app-pub-4453425711318249/2056112072";

    private EditText mQuestion, mOption3, mOption4, mOption5, mOption1, mOption2, mAnsNbr;
    private static String id_questioner;
    private static int score,count_id;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Bitmap bitmap0, bitmap1, bitmap2, bitmap3, bitmap4, bitmap5;
    private ImageView photo_question, photo_option1, photo_option2, photo_option3, photo_option4, photo_option5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);

        SharedPreferences preferences = getSharedPreferences("userPreferences", MODE_PRIVATE);
        id_questioner = preferences.getString("id_user", null);
        score = preferences.getInt("score", 0);
        TextView mScore;
        mScore = findViewById(R.id.score1);
        mScore.setText(getResources().getString(R.string.score, score));

        Toolbar toolbar = findViewById(R.id.toolbar_create_question);
        toolbar.setTitle(getResources().getString(R.string.create_question));

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_ic);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.nav_home:
                        Intent h = new Intent(CreateQuestionActivity.this, HomeActivity.class);
                        startActivity(h);
                        break;
                    case R.id.nav_answer:
                        Intent j = new Intent(CreateQuestionActivity.this, QuestionsActivity.class);

                        startActivity(j);
                        break;
                    case R.id.nav_ask:

                        break;
                    case R.id.MyQuestions:
                        Intent i = new Intent(CreateQuestionActivity.this, UserActivityActivity.class);
                        Bundle b = new Bundle();
                        b.putString("id_user", id_questioner);
                        i.putExtras(b);
                        startActivity(i);
                        break;
                }
                return true;
            }
        });
        bottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(false);
        bottomNavigationView.getMenu().findItem(R.id.nav_ask).setChecked(true);

        photo_question = findViewById(R.id.photo_question);
        photo_option1 = findViewById(R.id.photo_option1);
        photo_option2 = findViewById(R.id.photo_option2);
        photo_option3 = findViewById(R.id.photo_option3);
        photo_option4 = findViewById(R.id.photo_option4);
        photo_option5 = findViewById(R.id.photo_option5);


        findViewById(R.id.attach_photo_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetPhotoInBitmap(0);
            }
        });
        findViewById(R.id.attach_photo_option1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetPhotoInBitmap(1);

            }
        });

        findViewById(R.id.attach_photo_option2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetPhotoInBitmap(2);
            }
        });

        ImageView attach_photo3 = findViewById(R.id.attach_photo_option3);

        attach_photo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetPhotoInBitmap(3);

            }
        });

        ImageView attach_photo4 = findViewById(R.id.attach_photo_option4);


        attach_photo4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetPhotoInBitmap(4);

            }
        });

        ImageView attach_photo5=findViewById(R.id.attach_photo_option5);

        attach_photo5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetPhotoInBitmap(5);

            }
        });
        LinearLayout mNotif_s = findViewById(R.id.notif_s);
        LinearLayout mNotif_3 = findViewById(R.id.notif_3);
        LinearLayout mNotif_4 = findViewById(R.id.notif_4);
        LinearLayout mNotif_5 = findViewById(R.id.notif_5);
        //todo modify conditions to create a question
        Button postB = findViewById(R.id.postB);
        mQuestion = findViewById(R.id.mQuestion);
        mOption1 = findViewById(R.id.mOption1);
        mOption2 = findViewById(R.id.mOption2);
        mOption3 = findViewById(R.id.mOption3);
        mOption4 = findViewById(R.id.mOption4);
        mOption5 = findViewById(R.id.mOption5);
        mAnsNbr = findViewById(R.id.mAnsNbr);


        postB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPost();
            }
        });


        if (score < 45) {
            mOption3.setVisibility(View.GONE);
            mOption4.setVisibility(View.GONE);
            mOption5.setVisibility(View.GONE);
            attach_photo3.setVisibility(View.GONE);
            attach_photo4.setVisibility(View.GONE);
            attach_photo5.setVisibility(View.GONE);


            mNotif_s.setVisibility(View.VISIBLE);
            mNotif_3.setVisibility(View.VISIBLE);
        } else if (score < 65) {
            mOption4.setVisibility(View.GONE);
            mOption5.setVisibility(View.GONE);
            attach_photo4.setVisibility(View.GONE);
            attach_photo5.setVisibility(View.GONE);

            mNotif_s.setVisibility(View.VISIBLE);
            mNotif_4.setVisibility(View.VISIBLE);

        } else if (score < 85) {
            mOption5.setVisibility(View.GONE);
            attach_photo5.setVisibility(View.GONE);

            mNotif_s.setVisibility(View.VISIBLE);
            mNotif_5.setVisibility(View.VISIBLE);

        }


        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(ADMOB_AD_UNIT_ID_BANNER);

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });

    }

    private void GetPhotoInBitmap(int code) {
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), code);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                switch (requestCode) {
                    case 0:
                        bitmap0 = bitmap;
                        photo_question.setImageBitmap(bitmap);
                        break;

                    case 1:
                        bitmap1 = bitmap;
                        photo_option1.setImageBitmap(bitmap);
                        break;

                    case 2:
                        bitmap2 = bitmap;
                        photo_option2.setImageBitmap(bitmap);
                        break;

                    case 3:
                        bitmap3 = bitmap;
                        photo_option3.setImageBitmap(bitmap);
                        break;

                    case 4:
                        bitmap4 = bitmap;
                        photo_option4.setImageBitmap(bitmap);
                        break;

                    case 5:
                        bitmap5 = bitmap;
                        photo_option5.setImageBitmap(bitmap);
                        break;

                    default:

                        break;

                }


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }

    private void savePhotoInFirebase(Bitmap bitmap, String id) {
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            ;
            StorageReference mountainsRef = storageRef.child("Question&OptionsPhotos/" + id + ".png");

            UploadTask uploadTask = mountainsRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(CreateQuestionActivity.this, "Photo was not saved", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                }
            });
        }
    }

    private void onPost() {

        if (!LoginActivity.ConnectivityHelper.isConnectedToNetwork(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            //progressBar.setVisibility(View.GONE);
            return;
        } else if (CfieldEmpty()) {
            return;
        }


        final Map<String, Object> data = new HashMap<>();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final String question = mQuestion.getText().toString().trim();
        final String option1 = mOption1.getText().toString().trim();
        final String option2 = mOption2.getText().toString().trim();
        final String option3 = mOption3.getText().toString().trim();
        final String option4 = mOption4.getText().toString().trim();
        final String option5 = mOption5.getText().toString().trim();
        final int AnsNbr = Integer.parseInt(mAnsNbr.getText().toString().trim());
        int options_score = score;


        if (options_score >= 85) {
            if (!option3.equals("")) {
                options_score = options_score - 20;
            }
            if (!option4.equals("")) {
                options_score = options_score - 20;
            }
            if (!option5.equals("")) {
                options_score = options_score - 20;
            }
        } else if (options_score >= 65) {
            if (!option3.equals("")) {
                options_score = options_score - 20;
            }
            if (!option4.equals("")) {
                options_score = options_score - 20;
            }
        } else if (options_score >= 45) {
            if (!option3.equals("")) {
                options_score = options_score - 20;
            }
        }

        options_score = options_score - 20;


        final int calculated_score = options_score - 5 * AnsNbr;
        if (calculated_score < 0) {
            Toast.makeText(this, "You don't have enough score !\n1 answer for 5 points ", Toast.LENGTH_SHORT).show();
            mAnsNbr.setError("Decrease the number of answers or options");

        } else {
            final DocumentReference sfDocRef = db.collection("questions").document("qXGHFFXNNANcUr2P0fEm");

            db.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                    DocumentSnapshot snapshot = transaction.get(sfDocRef);
                    count_id = Objects.requireNonNull(snapshot.getLong("count_id")).intValue();

                    data.put("id", count_id);

                    transaction.update(sfDocRef, "count_id", count_id + 1);

                    // Success
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    data.put("question", question);
                    data.put("option1", option1);
                    data.put("option_1", 0);
                    data.put("option2", option2);
                    data.put("option_2", 0);
                    data.put("answerNbr", AnsNbr);
                    data.put("total_answers", 0);
                    data.put("id_questioner", id_questioner);


                    if (!option3.equals("")) {
                        data.put("option3", option3);
                        data.put("option_3", 0);

                    }
                    if (!option4.equals("")) {
                        data.put("option4", option4);
                        data.put("option_4", 0);

                    }
                    if (!option5.equals("")) {
                        data.put("option5", option5);
                        data.put("option_5", 0);

                    }

                    db.collection("questions")
                            .add(data);
                    savePhotoInFirebase(bitmap0, count_id + "0");
                    savePhotoInFirebase(bitmap1, count_id + "1");
                    savePhotoInFirebase(bitmap2, count_id + "2");
                    savePhotoInFirebase(bitmap3, count_id + "3");
                    savePhotoInFirebase(bitmap4, count_id + "4");
                    savePhotoInFirebase(bitmap5, count_id + "5");


                    final DocumentReference sfDocRef2 = db.collection("userh").document(id_questioner);
                    final WriteBatch batch = db.batch();
                    batch.update(sfDocRef2, "score", calculated_score);
                    batch.commit();

                    preferences = getSharedPreferences("userPreferences", 0);
                    editor = preferences.edit();
                    editor.putInt("score", calculated_score);
                    editor.apply();

                    Intent i = new Intent(CreateQuestionActivity.this, UserActivityActivity.class);
                    startActivity(i);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateQuestionActivity.this, "Error communicating with the server", Toast.LENGTH_SHORT).show();

                }
            });


        }
    }

    private boolean CfieldEmpty() {
        mQuestion.setError(null);
        mOption1.setError(null);
        mOption2.setError(null);

        String question = mQuestion.getText().toString();
        String option1 = mOption1.getText().toString();
        String option2 = mOption2.getText().toString();

        if (question.equals("")) {
            mQuestion.setError("Ask Something");
            return true;
        }
        if (option1.equals("")) {
            mOption1.setError("This field is compulsory");
            return true;
        }
        if (option2.equals("")) {
            mOption2.setError("This field is compulsory");
            return true;
        }
        return false;
    }

    public void gotoHelp(View view) {
        Intent i = new Intent(this, HelpActivity.class);
        startActivity(i);

    }
}
