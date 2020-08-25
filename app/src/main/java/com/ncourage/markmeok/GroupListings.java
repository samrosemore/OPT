package com.ncourage.markmeok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.rpc.Help;


public class GroupListings extends AppCompatActivity {

    private GroupListings gl;
    private DrawerLayout dl;
    private NavigationView navView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_listings);




        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_groups, new Groups_Frag()).commit();


        BottomAppBar bottomAppBar = (BottomAppBar) findViewById(R.id.bottomAppBarGroups);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        gl = this;

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(gl, NewGroup.class);
                startActivity(intent);
            }
        });

        //adding interface for side menu
        dl = findViewById(R.id.drawerActivity);
        navView = findViewById(R.id.navView);
        navView.setNavigationItemSelectedListener(drawerListener);


        //main line
        setSupportActionBar(bottomAppBar);
        bottomAppBar.setOnMenuItemClickListener(navListener);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch(which)
                {
                    case DialogInterface.BUTTON_POSITIVE:
                        FirebaseAuth.getInstance().signOut();
                        Intent toLoginScreen = new Intent(gl, WelcomePage.class);
                        startActivity(toLoginScreen);
                        break;
                    default:
                        break;

                }
            }
        };




    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.group_menu, menu);
        return true;
    }



    private NavigationView.OnNavigationItemSelectedListener drawerListener = new NavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            if(item.getItemId() == R.id.signOut)
            {
                FirebaseAuth.getInstance().signOut();
                Intent toLoginScreen = new Intent(gl, WelcomePage.class);
                startActivity(toLoginScreen);
            }
            else if(item.getItemId() == R.id.faq)
            {
                Intent toFAQ = new Intent(gl, FAQ.class);
                startActivity(toFAQ);
            }
            else if(item.getItemId() == R.id.about)
            {
                Intent toAbout = new Intent(gl, HelpScreen.class);
                startActivity(toAbout);
            }
            else if(item.getItemId() == R.id.tutorial)
            {
                Intent toTutorial = new Intent(gl, Tutorial.class);
                toTutorial.putExtra("initialBootup", false);
                startActivity(toTutorial);
            }
            return false;
        }
    };



    private BottomAppBar.OnMenuItemClickListener navListener = new BottomAppBar.OnMenuItemClickListener()
    {
        @Override
        public boolean onMenuItemClick(MenuItem item)
        {
            TextView curr = (TextView) findViewById(R.id.groupsTitle);
            if(item.getItemId() == R.id.nav_groups)
            {
                curr.setText("Groups");
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_groups, new Groups_Frag()).commit();
            }
            else if(item.getItemId() == R.id.nav_invites)
            {
                curr.setText("Invitations");
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_groups, new Invitations()).commit();
            }
            else if(item.getItemId() == R.id.nav_menu)
            {
                if(dl.isDrawerOpen(Gravity.LEFT))
                {
                    dl.closeDrawer(Gravity.LEFT);
                }
                else
                {
                    dl.openDrawer(Gravity.LEFT);
                }

            }

            return true;
        }
    };

}
