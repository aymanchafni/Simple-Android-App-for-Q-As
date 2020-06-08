package com.ayman.hblik;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;


public class RegisterActivity extends AppCompatActivity {

    TextInputLayout mfName,mlName;
    ImageView next_register;
    Spinner spinner_day,spinner_month,spinner_year;
    String birth_day,birth_year,birth_month;
    TextView mBirthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_register);
        Toolbar toolbar = findViewById(R.id.toolbar_register);
        toolbar.setTitle(getResources().getString(R.string.sign_up));

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.back_ic);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBirthday=findViewById(R.id.birthday);
        mfName=findViewById(R.id.fName);
        mlName=findViewById(R.id.lName);
       //
        spinner_day = findViewById(R.id.birth_day);
        ArrayAdapter<CharSequence> adapter_day = ArrayAdapter.createFromResource(this,
                R.array.days_array, android.R.layout.simple_spinner_item);
        adapter_day.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_day.setAdapter(adapter_day);

        spinner_day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                birth_day=parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //
        spinner_month = findViewById(R.id.birth_month);
        ArrayAdapter<CharSequence> adapter_month = ArrayAdapter.createFromResource(this,
                R.array.months_array, R.layout.simple_spinner_item);
        adapter_month.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_month.setAdapter(adapter_month);

        spinner_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if(pos<9)
                 birth_month='0'+Integer.toString(pos+1);
                else
                    birth_month=Integer.toString(pos+1);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //
        spinner_year = findViewById(R.id.birth_year);
        ArrayAdapter<CharSequence> adapter_year = ArrayAdapter.createFromResource(this,
                R.array.years_array, android.R.layout.simple_spinner_item);
        adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_year.setAdapter(adapter_year);

        spinner_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                birth_year=parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //

       next_register=findViewById(R.id.next_register);
       next_register.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(FieldError()){
                   return;
               }
               Intent i = new Intent(RegisterActivity.this,Register2Activity.class);
               Bundle b=new Bundle();

               String first_name= Objects.requireNonNull(mfName.getEditText()).getText().toString().trim();
               String last_name= Objects.requireNonNull(mlName.getEditText()).getText().toString().trim();
               String birthday=birth_day+'/'+birth_month+'/'+birth_year;

               b.putString("first_name",first_name);
               b.putString("last_name",last_name);
               b.putString("birthday",birthday);

               i.putExtras(b);

               startActivity(i);
               finish();
           }
       });





    }



private boolean FieldError() {
    mfName.setError(null);
    mlName.setError(null);
    mBirthday.setError(null);


    String fName = Objects.requireNonNull(mfName.getEditText()).getText().toString().trim();
    String lName = Objects.requireNonNull(mlName.getEditText()).getText().toString().trim();

if(fName.equals("") || lName.equals("") || birth_day == null || birth_month == null  || birth_year == null
        || fName.length()==1 || lName.length()==1 || fName.length()>15 || lName.length()>15)
{
    if (fName.equals("")) {
        mfName.setError("Field can't be empty");
    }


    if (lName.equals("")) {
        mlName.setError("Field can't be empty");
    }

    if (birth_day == null || birth_month == null  || birth_year == null) {
        mBirthday.setError("Field can't be empty");
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

return true;
}

return false;

    }






}
