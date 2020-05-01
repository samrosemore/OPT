package com.example.opt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity
{
    private Button loginButton;
    private TextView toSignUpPage;
    private JsonReader jsonReader;
    private Handler handler;

    private EditText username;
    private EditText password;

    private String jsonResult;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //two options - Login in or redirect to sign up screen
        loginButton = (Button) findViewById(R.id.loginButton);
        toSignUpPage = (TextView) findViewById(R.id.toSignUpPage);
        toSignUpPage.setLinksClickable(true);







        final Toast toast = Toast.makeText(this, "wrong username password combination", Toast.LENGTH_LONG);
         handler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message inputMessage)
            {
                if(inputMessage.what == 1)
                {

                    Intent toHomeScreen = new Intent(getApplicationContext(), MainActivity.class);

                    //will delete later
                    toHomeScreen.putExtra("username", username.getText().toString());
                    //


                    toHomeScreen.putExtra("jsonResult", jsonResult);



                    startActivity(toHomeScreen);
                }
                else
                {
                    toast.show();
                }
            }
        };

        toSignUpPage.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent toSignUpActivity = new Intent(getApplicationContext(), SignUp.class);
                startActivity(toSignUpActivity);
            }
        });



    }

    public void receive(String jsonResult)
    {
        //for safekeeping
        this.jsonResult = jsonResult;


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
        username = (EditText) this.findViewById(R.id.usernameLogin);
        password = (EditText) this.findViewById(R.id.passwordLogin);

        jsonReader = new JsonReader(username.getText().toString(), password.getText().toString(), this);
        jsonReader.start();


    }




}
