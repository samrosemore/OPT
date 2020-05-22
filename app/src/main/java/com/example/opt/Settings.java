package com.example.opt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Period;
import java.util.Date;
import java.util.Objects;
import java.util.TimerTask;


public class Settings extends Fragment
{

    private View view;

    private Spinner spinnerPeriod;
    private Spinner spinnerInterval;
    private EditText numWarningsInput;

    private int timePeriod;
    private int numWarnings;
    private int timeBetweenWarnings;

    private Button button;
    private String uID;
    private FirebaseFirestore db;

    private UserInfo userInfo;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.uID = user.getUid();
        db = FirebaseFirestore.getInstance();


        db.collection("users").document(uID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e)
            {
                if(documentSnapshot != null && documentSnapshot.exists())
                {
                    if(documentSnapshot.getData().containsKey("timePeriod"))
                    {
                        timePeriod = ((Long) documentSnapshot.getData().get("timePeriod")).intValue();

                        spinnerPeriod = (Spinner) view.findViewById(R.id.spinnerPeriod);
                        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.periodTimes, R.layout.spinner_item);

                        spinnerPeriod.setAdapter(arrayAdapter);

                        spinnerPeriod.setSelection(arrayAdapter.getPosition(timePeriod + " hours"));
                    }

                    if(documentSnapshot.getData().containsKey("timeBetweenWarnings"))
                    {
                        timeBetweenWarnings = ((Long) documentSnapshot.getData().get("timeBetweenWarnings")).intValue();
                        spinnerInterval = (Spinner) view.findViewById(R.id.spinnerInterval);
                        ArrayAdapter<CharSequence> arrayAdapter1 = ArrayAdapter.createFromResource(view.getContext(), R.array.intervalTimes, R.layout.spinner_item);
                        spinnerInterval.setAdapter(arrayAdapter1);

                        spinnerInterval.setSelection(arrayAdapter1.getPosition(timeBetweenWarnings + " hours"));
                    }
                    if(documentSnapshot.getData().containsKey("numWarnings"))
                    {
                        numWarnings = ((Long) documentSnapshot.getData().get("numWarnings")).intValue();
                        numWarningsInput = (EditText) view.findViewById(R.id.numWarningsInput);
                        numWarningsInput.setText("" + numWarnings);
                    }

                }
            }
        });








        button = view.findViewById(R.id.statusChange);


        button.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {


                if(Verification.checkNumber(numWarningsInput.getText().toString()))
                {
                    int numWarnings = Integer.parseInt(numWarningsInput.getText().toString());

                    //getting other values
                    String strTimePeriod = spinnerPeriod.getSelectedItem().toString();
                    int spaceIndex0 = strTimePeriod.indexOf(" ");
                    int timePeriod = Integer.parseInt(strTimePeriod.substring(0, spaceIndex0));

                    String stringTimeIntervals = spinnerInterval.getSelectedItem().toString();
                    int spaceIndex = stringTimeIntervals.indexOf(" ");
                    int timeBetweenIntervals = Integer.parseInt(stringTimeIntervals.substring(0, spaceIndex));

                    db.collection("users").document(uID).update("timePeriod", timePeriod);
                    db.collection("users").document(uID).update("numWarnings", numWarnings);
                    db.collection("users").document(uID).update("timeBetweenWarnings", timeBetweenIntervals);

                }
                else
                {
                    Toast.makeText(view.getContext(), "Please Enter A Valid Number for the Number of Warnings", Toast.LENGTH_SHORT).show();
                }



            }
        });



        return view;

    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }




}
