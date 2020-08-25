package com.ncourage.markmeok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity
{
    private EditText resetEditText;
    private ResetPassword resetPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        resetPassword = this;

        resetEditText = findViewById(R.id.resetEditText);

        Button btn = findViewById(R.id.resetBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(resetEditText.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(resetPassword, "Email Was Sent", Toast.LENGTH_SHORT).show();
                                    Intent toLoginScreen = new Intent(resetPassword, WelcomePage.class);
                                    startActivity(toLoginScreen);
                                } else {
                                    Toast.makeText(resetPassword, "Email Failed To Send", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });



    }
}