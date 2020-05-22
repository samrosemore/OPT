package com.example.opt;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    }

    public void onSubmit(View view)
    {
        //preparing for button click
        EditText fullName = (EditText) findViewById(R.id.fullNameSignUp);
        username = (EditText) findViewById(R.id.usernameSignUp);
        EditText password = (EditText) findViewById(R.id.passwordSignUp);
        EditText phoneNumber = (EditText) findViewById(R.id.phoneNumSignUp);
        EditText email = (EditText) findViewById(R.id.emailSignUp);

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Intent toMSettings = new Intent(getApplicationContext(), MSettings.class);

                startActivity(toMSettings);
            }
        });





    }
}
