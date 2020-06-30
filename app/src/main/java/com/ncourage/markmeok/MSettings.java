package com.ncourage.markmeok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;


public class MSettings extends AppCompatActivity
{


    private Spinner spinnerPeriod;
    private Spinner spinnerInterval;

    private String uID;
    private FirebaseFirestore db;

    private MSettings mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_settings);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.uID = user.getUid();
        db = FirebaseFirestore.getInstance();

        spinnerPeriod = (Spinner) findViewById(R.id.mSpinnerPeriod);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.periodTimes, R.layout.spinner_item);
        spinnerPeriod.setAdapter(arrayAdapter);

        spinnerInterval = (Spinner) findViewById(R.id.mSpinnerInterval);
        ArrayAdapter<CharSequence> arrayAdapter1 = ArrayAdapter.createFromResource(this, R.array.intervalTimes, R.layout.spinner_item);
        spinnerInterval.setAdapter(arrayAdapter1);

        mSettings = this;


    }


    public void onSubmit(View view)
    {


        //getting other values



        EditText mNumWarningsInput = (EditText) findViewById(R.id.mNumWarningsInput);

        if(Verification.checkNumber(mNumWarningsInput.getText().toString()))
        {
            int numWarnings = Integer.parseInt(mNumWarningsInput.getText().toString());

            String strTimePeriod = spinnerPeriod.getSelectedItem().toString();
            int spaceIndex0 = strTimePeriod.indexOf(" ");
            int timePeriod = Integer.parseInt(strTimePeriod.substring(0, spaceIndex0));

            String stringTimeIntervals = spinnerInterval.getSelectedItem().toString();
            int spaceIndex = stringTimeIntervals.indexOf(" ");
            int timeBetweenIntervals = Integer.parseInt(stringTimeIntervals.substring(0, spaceIndex));

            Map<String, Object> propertiesToAdd = new HashMap<>();
            propertiesToAdd.put("timePeriod", timePeriod);
            propertiesToAdd.put("startingTime", (new Date().getTime())/1000.0); //current time in SECONDS (SWIFT STORES SHIT IN SECONDS...DON'T ASK ME WHY ... ITS STUPID"
            propertiesToAdd.put("numWarnings", numWarnings);
            propertiesToAdd.put("timeBetweenWarnings", timeBetweenIntervals);

            db.collection("Groups").document(getIntent().getStringExtra("groupName")).set(propertiesToAdd,  SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            //send invitations to other users


                            for(String s: getIntent().getStringArrayListExtra("otherUsers"))
                            {
                                Map<String, Object> propertiesToAdd = new HashMap<>();


                                propertiesToAdd.put("host", FirebaseAuth.getInstance().getUid());

                                db.collection("users").document(s).collection("invitation").document(getIntent().getStringExtra("groupName")).set(propertiesToAdd);
                            }

                            Intent intent = new Intent(mSettings.getApplicationContext(), GroupListings.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });



        }
        else
        {
            Toast.makeText(this, "Please Enter a Valid Number for the Number of Warnings", Toast.LENGTH_SHORT).show();
        }





    }
}