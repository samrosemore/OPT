package com.example.opt;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
{


    BottomAppBar bottomAppBar;


    private static final int MY_RESPONSE_CODE = 7110;
    private List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(FirebaseAuth.getInstance().getCurrentUser() == null)
        {
            providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.PhoneBuilder().build()
            );
            showSignInOptions();
        }
        else
        {
            bottomAppBar = (BottomAppBar) findViewById(R.id.bottomAppBar);
            setSupportActionBar(bottomAppBar);

            Home starterFragment = new Home();

            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, starterFragment).commit();

            bottomAppBar.setOnMenuItemClickListener(navListener);
        }




    }

    private void showSignInOptions()
    {
        Resources res = getResources();

        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).setLogo(R.drawable.ok_boomer).build(), MY_RESPONSE_CODE

        );
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == MY_RESPONSE_CODE)
        {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK)
            {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private BottomAppBar.OnMenuItemClickListener navListener = new BottomAppBar.OnMenuItemClickListener()
    {

        @Override
        public boolean onMenuItemClick(MenuItem item)
        {
            Fragment selectedFragment = null;

            switch(item.getItemId())
            {
                case R.id.nav_em_contacts:
                {
                    selectedFragment = new EmergencyContacts();

                    break;
                }
                case R.id.nav_setting:
                {
                    selectedFragment = new Settings();

                    break;
                }
                default:
                {
                    selectedFragment = new Home();

                    break;
                }
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, selectedFragment).commit();

            return true;
        }
    };


}
