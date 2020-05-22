package com.example.opt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

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
            propertiesToAdd.put("startTime", (new Date().getTime())/1000); //current time in SECONDS (SWIFT STORES SHIT IN SECONDS...DON'T ASK ME WHY ... ITS STUPID"
            propertiesToAdd.put("numWarnings", numWarnings);
            propertiesToAdd.put("timeBetweenWarnings", timeBetweenIntervals);

            db.collection("users").document(uID).set(propertiesToAdd).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid)
                {
                    Intent intent = new Intent(mSettings.getApplicationContext(), MEmergencyContacts.class);
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
