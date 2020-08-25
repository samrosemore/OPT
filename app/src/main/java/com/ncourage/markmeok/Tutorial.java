package com.ncourage.markmeok;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class Tutorial extends AppCompatActivity {

    private Tutorial tut;
    private WebView webView;
    private boolean initialBootup;
    private ConstraintLayout layout;
    private ImageButton backFromTutorial;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        tut = this;

        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("https://www.google.com");

        Button btn = findViewById(R.id.fromTutorial);
        backFromTutorial = findViewById(R.id.backFromTutorial);

        layout = findViewById(R.id.tutorialLayout);

        initialBootup = getIntent().getBooleanExtra("initialBootup", true);

        if(initialBootup)
        {
            btn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent toNewGroup = new Intent(tut.getApplicationContext(), NewGroup.class);
                    toNewGroup.putExtra("defaultGroup", true);
                    startActivity(toNewGroup);
                }
            });
            layout.removeView(backFromTutorial);
        }
        else
        {
            backFromTutorial.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toGroups = new Intent(tut.getApplicationContext(), GroupListings.class);
                    startActivity(toGroups);
                }
            });
            layout.removeView(btn);
        }




    }


}
