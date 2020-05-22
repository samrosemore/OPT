package com.example.opt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MEmergencyContacts extends AppCompatActivity
{


    private String username;
    private JsonReader jsonReader;
    private Handler handler;

    private String uID;
    private FirebaseFirestore db;

    private MEmergencyContacts mEC;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_emergency_contacts);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.uID = user.getUid();
        db = FirebaseFirestore.getInstance();

        mEC = this;

    }

    public void onSubmit(View view)
    {
        //validate first
        String strPhoneOne = ((EditText) findViewById(R.id.mContact1PhoneNumber)).getText().toString();
        String strPhoneTwo = ((EditText) findViewById(R.id.mContact2PhoneNumber)).getText().toString();

        if(Verification.checkPhoneNumber(strPhoneOne) && Verification.checkPhoneNumber(strPhoneTwo))
        {
            String twoEmail = ((EditText) findViewById(R.id.mContact2Email)).getText().toString();
            String contactEmail = ((EditText) findViewById(R.id.mContact1Email)).getText().toString();

            if(Verification.checkEmail(contactEmail) && Verification.checkEmail(twoEmail))
            {
                String contactOne = ((EditText) findViewById(R.id.mContact1FullName)).getText().toString();
                long onePhoneNumber = Integer.parseInt(strPhoneOne);
                String contactTwo = ((EditText) findViewById(R.id.mContact2FullName)).getText().toString();
                long twoPhoneNumber = Integer.parseInt(strPhoneTwo);


                Map<String, Object> propertiesToAdd = new HashMap<>();
                propertiesToAdd.put("contactOne", contactOne);
                propertiesToAdd.put("contactTwo", contactTwo);
                propertiesToAdd.put("oneEmail", contactEmail);
                propertiesToAdd.put("onePhoneNumber", onePhoneNumber);
                propertiesToAdd.put("twoEmail", twoEmail);
                propertiesToAdd.put("twoPhoneNumber", twoPhoneNumber);

                db.collection("EmergencyContacts").document(uID).set(propertiesToAdd).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Intent intent = new Intent(mEC.getApplicationContext(), MEmergencyContacts.class);
                        startActivity(intent);
                    }
                });


            }
            else
            {
                Toast.makeText(this, "Please Enter a Valid Email", Toast.LENGTH_SHORT).show();
            }
        }

        else
        {
            Toast.makeText(this, "Please Enter a Valid Phone Number", Toast.LENGTH_SHORT).show();
        }

    }

}
