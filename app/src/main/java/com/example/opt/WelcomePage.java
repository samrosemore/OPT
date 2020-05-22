package com.example.opt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomePage extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        Welcome welcomeFragment = new Welcome();

        getSupportFragmentManager().beginTransaction().replace(R.id.welcome_frame, welcomeFragment).commit();



    }
}
