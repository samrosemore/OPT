package com.example.opt;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MSettings extends AppCompatActivity
{

    private String username;

    private JsonReader jsonReader;
    private Handler handler;
    private Spinner spinnerPeriod;
    private Spinner spinnerInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_settings);

        this.username = getIntent().getStringExtra("username");

        spinnerPeriod = (Spinner) findViewById(R.id.mSpinnerPeriod);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.periodTimes, R.layout.spinner_item);
        spinnerPeriod.setAdapter(arrayAdapter);

        spinnerInterval = (Spinner) findViewById(R.id.mSpinnerInterval);
        ArrayAdapter<CharSequence> arrayAdapter1 = ArrayAdapter.createFromResource(this, R.array.intervalTimes, R.layout.spinner_item);
        spinnerInterval.setAdapter(arrayAdapter1);


        handler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message inputMessage)
            {
                if(inputMessage.what == 1)
                {
                    //will change this later
                    Intent toEmScreen = new Intent(getApplicationContext(), MEmergencyContacts.class);
                    toEmScreen.putExtra("username", username);
                    startActivity(toEmScreen);
                }


            }
        };
    }
    public void receive(String jsonResult)
    {
        String success = "0";
        try{
            JSONObject jObject = new JSONObject(jsonResult);
            String strMessage = jObject.getString("message");
            success = jObject.getString("success");
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }

        Message message = handler.obtainMessage(Integer.parseInt(success));
        message.sendToTarget();
    }

    public void onSubmit(View view)
    {
        jsonReader = new JsonReader(this);

        jsonReader.setUsername(username);
        jsonReader.setAction("updateCustomSettings");

        //getting other values

        String timePeriod = spinnerPeriod.getSelectedItem().toString();

        EditText mNumWarningsInput = (EditText) findViewById(R.id.mNumWarningsInput);
        int numWarnings = Integer.parseInt(mNumWarningsInput.getText().toString());


        String stringTimeIntervals = spinnerInterval.getSelectedItem().toString();
        int spaceIndex = stringTimeIntervals.indexOf(" ");


        int timeBetweenIntervals = Integer.parseInt(stringTimeIntervals.substring(0, spaceIndex));

        jsonReader.setCustomSettings(timePeriod, numWarnings, timeBetweenIntervals);


        jsonReader.start();
    }
}
