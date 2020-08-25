package com.ncourage.markmeok;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class HelpScreen extends AppCompatActivity
{
    HelpScreen help = this;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_screen);

        ImageButton imgBtn = (ImageButton) findViewById(R.id.backFromHelp);
        imgBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent toGroupListings = new Intent(help, GroupListings.class);
                startActivity(toGroupListings);
            }
        });
    }
}
