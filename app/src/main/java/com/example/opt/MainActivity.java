package com.example.opt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomappbar.BottomAppBar;

public class MainActivity extends AppCompatActivity
{

    BottomAppBar bottomAppBar;
    private UserInfo userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUserInfo();


        bottomAppBar = (BottomAppBar) findViewById(R.id.bottomAppBar);
        setSupportActionBar(bottomAppBar);

        Home starterFragment = new Home();
        Bundle bundle = new Bundle();
        bundle.putParcelable("userInfo", userInfo);
        starterFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, starterFragment).commit();

        bottomAppBar.setOnMenuItemClickListener(navListener);


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
                    Bundle bundle = new Bundle();

                    bundle.putParcelable("userInfo", userInfo);
                    selectedFragment.setArguments(bundle);
                    break;
                }
                case R.id.nav_setting:
                {
                    selectedFragment = new Settings();
                    Bundle bundle = new Bundle();

                    bundle.putParcelable("userInfo", userInfo);
                    selectedFragment.setArguments(bundle);
                    break;
                }
                default:
                {
                    selectedFragment = new Home();
                    Bundle bundle = new Bundle();

                    bundle.putParcelable("userInfo", userInfo);
                    selectedFragment.setArguments(bundle);
                    break;
                }
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, selectedFragment).commit();

            return true;
        }
    };

    public void initializeUserInfo()
    {
        this.userInfo = new UserInfo(getIntent().getStringExtra("jsonResult"));
    }
}
