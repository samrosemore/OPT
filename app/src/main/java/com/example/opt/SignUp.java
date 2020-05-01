package com.example.opt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUp extends AppCompatActivity
{



    //need this for latter administrative purposes
    private EditText username;

    private JsonReader jsonReader;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);





        final Toast toast = Toast.makeText(this, "username already taken...try again", Toast.LENGTH_LONG);
        handler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message inputMessage)
            {
                if(inputMessage.what == 1)
                {
                    Intent toMSettings = new Intent(getApplicationContext(), MSettings.class);
                    toMSettings.putExtra("username", username.getText().toString());
                    startActivity(toMSettings);
                }
                else
                {
                    toast.show();
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
        //preparing for button click
        EditText fullName = (EditText) findViewById(R.id.fullNameSignUp);
        username = (EditText) findViewById(R.id.usernameSignUp);
        EditText password = (EditText) findViewById(R.id.passwordSignUp);
        EditText phoneNumber = (EditText) findViewById(R.id.phoneNumSignUp);
        EditText email = (EditText) findViewById(R.id.emailSignUp);

        jsonReader = new JsonReader(fullName.getText().toString(), phoneNumber.getText().toString(), email.getText().toString(),
                username.getText().toString(), password.getText().toString(), this);
        jsonReader.start();
    }
}
