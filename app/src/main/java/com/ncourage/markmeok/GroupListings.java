package com.ncourage.markmeok;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;


public class GroupListings extends AppCompatActivity {

    private GroupListings gl;
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


        //main line
        setSupportActionBar(bottomAppBar);

        bottomAppBar.setOnMenuItemClickListener(navListener);

        TextView signOutOption = (TextView) findViewById(R.id.signOutOption);
        signOutOption.setLinksClickable(true);
        signOutOption.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FirebaseAuth.getInstance().signOut();
                Intent toLoginScreen = new Intent(gl, WelcomePage.class);
                startActivity(toLoginScreen);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.group_menu, menu);
        return true;
    }


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
            return true;
        }
    };

}
