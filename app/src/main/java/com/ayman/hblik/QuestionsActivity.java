package com.ayman.hblik;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class QuestionsActivity extends AppCompatActivity {
    private static final String ADMOB_AD_UNIT_ID = "ca-app-pub-4453425711318249/9934602092";
    private static String id_user;
    private static DocumentSnapshot documentSnapshot;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    private CircleImageView image_question, image_option1, image_option2, image_option3, image_option4, image_option5;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView mQuestion, skipB, mScore;
    private TextView rb1, rb2, rb3, rb4, rb5;
    private Button submitB;
    private LinearLayout container_option1, container_option2, container_option3, container_option4, container_option5;
    private static int count_getPhoto_calls = 0,nb_options = 2,optionChosen = 0,total_answers,id_last_question,score;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        preferences = getSharedPreferences("userPreferences", MODE_PRIVATE);
        id_user = preferences.getString("id_user", null);
        //String firstName =preferences.getString("first_name",null);
        //String lastName =preferences.getString("last_name",null);

        score = preferences.getInt("score", 0);
        id_last_question = preferences.getInt("id_last_question", 0);


        Toolbar toolbar = findViewById(R.id.toolbar_answer);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override

            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.MyQuestions:
                        Intent i = new Intent(QuestionsActivity.this, UserActivityActivity.class);
                        Bundle b = new Bundle();
                        b.putString("id_user", id_user);
                        i.putExtras(b);
                        startActivity(i);
                        return true;

                    case R.id.nav_home:
                        Intent h = new Intent(QuestionsActivity.this, HomeActivity.class);
                        startActivity(h);
                        return true;
                    case R.id.nav_answer:
                        Intent j = new Intent(QuestionsActivity.this, QuestionsActivity.class);
                        startActivity(j);
                        return true;
                    case R.id.nav_ask:
                        if (score < 25) {
                            Toast.makeText(QuestionsActivity.this, "You should have at least 25 pts", Toast.LENGTH_SHORT).show();
                            return false;
                        } else {
                            Intent intent = new Intent(QuestionsActivity.this, CreateQuestionActivity.class);
                            startActivity(intent);
                            return true;

                        }

                }
                return false;
            }
        });
        bottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(false);
        bottomNavigationView.getMenu().findItem(R.id.nav_answer).setChecked(true);


        mScore = findViewById(R.id.score);
        mQuestion = findViewById(R.id.question);
        submitB = findViewById(R.id.submit);
        skipB = findViewById(R.id.skip);
        rb1 = findViewById(R.id.radioButton1);
        rb2 = findViewById(R.id.radioButton2);
        rb3 = findViewById(R.id.radioButton3);
        rb4 = findViewById(R.id.radioButton4);
        rb5 = findViewById(R.id.radioButton5);

        container_option1 = findViewById(R.id.container_option1);
        container_option2 = findViewById(R.id.container_option2);
        container_option3 = findViewById(R.id.container_option3);
        container_option4 = findViewById(R.id.container_option4);
        container_option5 = findViewById(R.id.container_option5);

        image_question = findViewById(R.id.image_question);
        image_option1 = findViewById(R.id.image_option1);
        image_option2 = findViewById(R.id.image_option2);
        image_option3 = findViewById(R.id.image_option3);
        image_option4 = findViewById(R.id.image_option4);
        image_option5 = findViewById(R.id.image_option5);

        image_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageDialog(0);
            }
        });

        image_option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageDialog(1);
            }
        });

        image_option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageDialog(2);

            }
        });

        image_option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageDialog(3);

            }
        });

        image_option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageDialog(4);

            }
        });

        image_option5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageDialog(5);


            }
        });


        submitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit();
            }
        });
        skipB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fill_new_content_start();
            }
        });


        fill_new_content_start();


        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(ADMOB_AD_UNIT_ID);

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

    private void openImageDialog(int i) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_image);
        dialog.setTitle("This is my custom dialog box");
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.rgb(100,100,100)));
        // Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(null);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);


        ImageView image = dialog.findViewById(R.id.dialog_image);
        image.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                dialog.dismiss();
                return false;
            }
        });

        switch (i) {
            case 0:
                if (image_question.getDrawable() != null)
                    image.setImageDrawable(image_question.getDrawable());
                break;

            case 1:
                image.setImageDrawable(image_option1.getDrawable());
                break;

            case 2:
                image.setImageDrawable(image_option2.getDrawable());
                break;

            case 3:
                image.setImageDrawable(image_option3.getDrawable());
                break;

            case 4:
                image.setImageDrawable(image_option4.getDrawable());
                break;

            case 5:
                image.setImageDrawable(image_option5.getDrawable());
                break;

            default:
                break;
        }

        if (image.getDrawable() != null)
            dialog.show();
    }

    private void getPhotoFromStorage(String id, final ImageView image) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // [START download_create_reference]
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a reference with an initial file path and name
        StorageReference pathReference;

        pathReference = storageRef.child("Question&OptionsPhotos/" + id + ".png");


        final long MEGABYTES = 5 * 1024 * 1024;
        pathReference.getBytes(MEGABYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
// Set the Bitmap data to the ImageView
                image.setImageBitmap(bmp);
                count_getPhoto_calls++;
                if (count_getPhoto_calls == nb_options)
                    submitB.setVisibility(View.VISIBLE);
                skipB.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                count_getPhoto_calls++;
                if (count_getPhoto_calls == nb_options)
                    submitB.setVisibility(View.VISIBLE);
                skipB.setVisibility(View.VISIBLE);
            }

        });

    }

    public void onChoice(View view) {

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioButton1:
                container_option1.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border));
                container_option2.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
                container_option3.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
                container_option4.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
                container_option5.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));

                optionChosen = 1;

                break;
            case R.id.radioButton2:
                container_option2.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border));
                container_option1.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
                container_option3.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
                container_option4.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
                container_option5.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));

                optionChosen = 2;

                break;
            case R.id.radioButton3:
                container_option3.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border));
                container_option2.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
                container_option1.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
                container_option4.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
                container_option5.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
                optionChosen = 3;

                break;
            case R.id.radioButton4:
                container_option4.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border));
                container_option2.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
                container_option3.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
                container_option1.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
                container_option5.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
                optionChosen = 4;

                break;
            case R.id.radioButton5:
                container_option5.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border));
                container_option2.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
                container_option3.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
                container_option4.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
                container_option1.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
                optionChosen = 5;

                break;
        }
    }

    private void onSubmit() {
        if (!LoginActivity.ConnectivityHelper.isConnectedToNetwork(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            //progress.setVisibility(View.GONE);
        } else if (optionChosen == 0) {
            Toast.makeText(this, "please pick a choice", Toast.LENGTH_SHORT).show();
        } else
            fill_new_content();

    }

    @SuppressLint("RestrictedApi")
    private void fill_new_content() {

        container_option5.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
        container_option2.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
        container_option3.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
        container_option4.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
        container_option1.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
        image_question.setImageResource(0);
        image_option1.setImageResource(0);
        image_option2.setImageResource(0);
        image_option3.setImageResource(0);
        image_option4.setImageResource(0);
        image_option5.setImageResource(0);
        rb1.setText("");
        rb2.setText("");
        rb3.setText("");
        rb4.setText("");
        rb5.setText("");
        count_getPhoto_calls = 0;
        nb_options = 2;


        submitB.setVisibility(View.GONE);
        skipB.setVisibility(View.GONE);


        db.collection("questions")
                .whereGreaterThan("id", id_last_question)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            final DocumentReference sfDocRef = db.collection("questions").document(documentSnapshot.getId());
                            final DocumentReference sfDocRef2 = db.collection("userh").document(id_user);


                            db.runTransaction(new Transaction.Function<Void>() {
                                @Override
                                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                                    DocumentSnapshot snapshot = transaction.get(sfDocRef);
                                    int times_option_chosen = Objects.requireNonNull(snapshot.getLong("option_" + optionChosen)).intValue();
                                    transaction.update(sfDocRef, "option_" + optionChosen, times_option_chosen + 1);

                                    // Success
                                    return null;
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    WriteBatch batch = db.batch();

                                    batch.update(sfDocRef, "total_answers", total_answers + 1);

                                    score += 5;
                                    batch.update(sfDocRef2, "score", score);
                                    batch.commit();
                                    mScore.setText(getResources().getString(R.string.score, score));

                                    editor = preferences.edit();
                                    editor.putInt("score", score);
                                    editor.apply();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {


                                        }
                                    });



                            if (!Objects.requireNonNull(task.getResult()).isEmpty()) {
                                String question, option1, option2, option3, option4, option5, id_questioner;
                                int answerNbr;
                                int count = 0;

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    count++;

                                    answerNbr = Objects.requireNonNull(document.getLong("answerNbr")).intValue();
                                    total_answers = Objects.requireNonNull(document.getLong("total_answers")).intValue();
                                    id_questioner = document.getString("id_questioner");

                                    assert id_questioner != null;
                                    if (answerNbr <= total_answers || id_questioner.equals(id_user)) {
                                        if (count == task.getResult().size()) {
                                            id_last_question = Objects.requireNonNull(document.getLong("id")).intValue();

                                            WriteBatch batch100 = db.batch();
                                            batch100.update(sfDocRef2, "id_last_question", id_last_question);
                                            batch100.commit();
                                            fill_new_content_start();
                                        }
                                        continue;
                                    }

                                    question = document.getString("question");
                                    option1 = document.getString("option1");
                                    option2 = document.getString("option2");
                                    option3 = document.getString("option3");
                                    option4 = document.getString("option4");
                                    option5 = document.getString("option5");

                                    id_last_question = Objects.requireNonNull(document.getLong("id")).intValue();

                                    WriteBatch batch101 = db.batch();
                                    batch101.update(sfDocRef2, "id_last_question", id_last_question);
                                    batch101.commit();

                                    editor.putInt("id_last_question", id_last_question);
                                    editor.apply();

                                    documentSnapshot = document;
                                    mQuestion.setText(question);
                                    getPhotoFromStorage(id_last_question + "0", image_question);

                                    rb1.setText(getResources().getString(R.string.A, option1));
                                    getPhotoFromStorage(id_last_question + "1", image_option1);
                                    rb2.setText(getResources().getString(R.string.B, option2));
                                    getPhotoFromStorage(id_last_question + "2", image_option2);

                                    if (option3 != null) {
                                        nb_options++;
                                        container_option3.setVisibility(View.VISIBLE);
                                        rb3.setText(getResources().getString(R.string.C, option3));
                                        getPhotoFromStorage(id_last_question + "3", image_option3);

                                    } else
                                        container_option3.setVisibility(View.GONE);

                                    if (option4 != null) {
                                        nb_options++;
                                        container_option4.setVisibility(View.VISIBLE);
                                        rb4.setText(getResources().getString(R.string.D, option4));
                                        getPhotoFromStorage(id_last_question + "4", image_option4);

                                    } else
                                        container_option4.setVisibility(View.GONE);

                                    if (option5 != null) {
                                        nb_options++;
                                        container_option5.setVisibility(View.VISIBLE);
                                        rb5.setText(getResources().getString(R.string.E, option5));
                                        getPhotoFromStorage(id_last_question + "5", image_option5);

                                    } else
                                        container_option5.setVisibility(View.GONE);


                                    break;

                                }


                            } else {
                                Toast.makeText(QuestionsActivity.this, "oups ! try later", Toast.LENGTH_SHORT).show();
                                rb1.setText("");
                                rb2.setText("");
                                rb3.setText("");
                                rb4.setText("");
                                rb5.setText("");
                                mQuestion.setText("");
                                image_option1.setImageResource(0);
                                image_option2.setImageResource(0);
                                image_option3.setImageResource(0);
                                image_option4.setImageResource(0);
                                image_option5.setImageResource(0);
                                image_question.setImageResource(0);

                                optionChosen = 0;

                            }
                        } else {
                            Toast.makeText(QuestionsActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show();
                            rb1.setText("");
                            rb2.setText("");
                            rb3.setText("");
                            rb4.setText("");
                            rb5.setText("");
                            mQuestion.setText("");
                            image_option1.setImageResource(0);
                            image_option2.setImageResource(0);
                            image_option3.setImageResource(0);
                            image_option4.setImageResource(0);
                            image_option5.setImageResource(0);
                            image_question.setImageResource(0);
                            optionChosen = 0;

                        }


                    }
                });


    }

    @SuppressLint("RestrictedApi")
    private void fill_new_content_start() {
        if (!LoginActivity.ConnectivityHelper.isConnectedToNetwork(this)) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            //progress.setVisibility(View.GONE);
            return;
        }
        container_option5.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
        container_option2.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
        container_option3.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
        container_option4.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
        container_option1.setBackground(ContextCompat.getDrawable(this, R.drawable.layout_border_0));
        image_question.setImageResource(0);
        image_option1.setImageResource(0);
        image_option2.setImageResource(0);
        image_option3.setImageResource(0);
        image_option4.setImageResource(0);
        image_option5.setImageResource(0);
        rb1.setText("");
        rb2.setText("");
        rb3.setText("");
        rb4.setText("");
        rb5.setText("");
        count_getPhoto_calls = 0;
        nb_options = 2;
        optionChosen = 0;

        mScore.setText(getResources().getString(R.string.score, score));


        db.collection("questions")
                .whereGreaterThan("id", id_last_question)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !Objects.requireNonNull(task.getResult()).isEmpty()) {
                            String question, option1, option2, option3, option4, option5;
                            int answerNbr;

                            int count = 0;
                            final DocumentReference sfDocRef2 = db.collection("userh").document(id_user);

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                count++;

                                answerNbr = Objects.requireNonNull(document.getLong("answerNbr")).intValue();
                                total_answers = Objects.requireNonNull(document.getLong("total_answers")).intValue();

                                String id_questioner = document.getString("id_questioner");

                                if (answerNbr <= total_answers || id_user.equals(id_questioner)) {
                                    if (count == task.getResult().size()) {
                                        id_last_question = Objects.requireNonNull(document.getLong("id")).intValue();

                                        WriteBatch batch100 = db.batch();
                                        batch100.update(sfDocRef2, "id_last_question", id_last_question);
                                        batch100.commit();
                                        fill_new_content_start();
                                    }
                                    continue;
                                }

                                question = document.getString("question");
                                option1 = document.getString("option1");
                                option2 = document.getString("option2");
                                option3 = document.getString("option3");
                                option4 = document.getString("option4");
                                option5 = document.getString("option5");

                                WriteBatch batch = db.batch();
                                editor = preferences.edit();

                                id_last_question = Objects.requireNonNull(document.getLong("id")).intValue();
                                batch.update(sfDocRef2, "id_last_question", id_last_question);
                                editor.putInt("id_last_question", id_last_question);
                                editor.apply();
                                batch.commit();

                                documentSnapshot = document;

                                mQuestion.setText(question);
                                getPhotoFromStorage(id_last_question + "0", image_question);

                                rb1.setText(getResources().getString(R.string.A, option1));
                                getPhotoFromStorage(id_last_question + "1", image_option1);
                                rb2.setText(getResources().getString(R.string.B, option2));
                                getPhotoFromStorage(id_last_question + "2", image_option2);

                                if (option3 != null) {
                                    nb_options++;
                                    container_option3.setVisibility(View.VISIBLE);
                                    rb3.setText(getResources().getString(R.string.C, option3));
                                    getPhotoFromStorage(id_last_question + "3", image_option3);

                                } else
                                    container_option3.setVisibility(View.GONE);

                                if (option4 != null) {
                                    nb_options++;
                                    container_option4.setVisibility(View.VISIBLE);
                                    rb4.setText(getResources().getString(R.string.D, option4));
                                    getPhotoFromStorage(id_last_question + "4", image_option4);

                                } else
                                    container_option4.setVisibility(View.GONE);

                                if (option5 != null) {
                                    nb_options++;
                                    container_option5.setVisibility(View.VISIBLE);
                                    rb5.setText(getResources().getString(R.string.E, option5));
                                    getPhotoFromStorage(id_last_question + "5", image_option5);

                                } else
                                    container_option5.setVisibility(View.GONE);


                                break;

                            }

                        } else {
                            Toast.makeText(QuestionsActivity.this, "oups ! try later", Toast.LENGTH_SHORT).show();
                            rb1.setText("");
                            rb2.setText("");
                            rb3.setText("");
                            rb4.setText("");
                            rb5.setText("");
                            submitB.setVisibility(View.GONE);
                            skipB.setVisibility(View.GONE);
                            mQuestion.setText("");
                            image_option1.setImageResource(0);
                            image_option2.setImageResource(0);
                            image_option3.setImageResource(0);
                            image_option4.setImageResource(0);
                            image_option5.setImageResource(0);
                            image_question.setImageResource(0);
                        }
                    }
                });


    }


    @Override
    public void onBackPressed() {

        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    //@SuppressWarnings("StatementWithEmptyBody")

   /* @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_settings:
               Intent i =new Intent(this,SettingsActivity.class);
               startActivity(i);
                return true;
            case R.id.nav_help:
                Intent j =new Intent(this,HelpActivity.class);
                startActivity(j);
                return true;

            case R.id.nav_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out my app at: https://www.play.google.com/Store");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            case R.id.nav_rate_us:
                Intent t = new Intent(QuestionsActivity.this,RateUsActivity.class);
                startActivity(t);
                return true;
            case R.id.nav_log_out:
                preferences.edit().clear().apply();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                LoginManager.getInstance().logOut();

                Intent intent =new Intent(QuestionsActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
        }


    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);

            return true;
    }*/
}