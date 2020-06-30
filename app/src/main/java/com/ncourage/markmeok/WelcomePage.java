package com.ncourage.markmeok;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

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