package com.example.opt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;


public class MEmergencyContacts extends AppCompatActivity
{


    private String username;
    private JsonReader jsonReader;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_emergency_contacts);

        this.username = getIntent().getStringExtra("username");

        handler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message inputMessage)
            {
                if(inputMessage.what == 1)
                {
                    Intent toLogin = new Intent(getApplicationContext(), Login.class);
                    startActivity(toLogin);
                }

            }
        };
    }

    public void onSubmit(View view)
    {
        String contactOne = ((EditText) findViewById(R.id.mContact1FullName)).getText().toString();
        int onePhoneNumber = Integer.parseInt(((EditText) findViewById(R.id.mContact1PhoneNumber)).getText().toString());
        String contactEmail = ((EditText) findViewById(R.id.mContact1Email)).getText().toString();

        String contactTwo = ((EditText) findViewById(R.id.mContact2FullName)).getText().toString();
        int twoPhoneNumber = Integer.parseInt(((EditText) findViewById(R.id.mContact2PhoneNumber)).getText().toString());
        String twoEmail = ((EditText) findViewById(R.id.mContact2Email)).getText().toString();

        jsonReader = new JsonReader(this, username, contactOne, onePhoneNumber, contactEmail, contactTwo, twoPhoneNumber, twoEmail);
        jsonReader.start();
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
}
