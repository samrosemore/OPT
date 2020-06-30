package com.ncourage.markmeok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{


    BottomAppBar bottomAppBar;
    private boolean isAdmin = false;


    private static final int MY_RESPONSE_CODE = 7110;
    private List<AuthUI.IdpConfig> providers;

    private MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        bottomAppBar = (BottomAppBar) findViewById(R.id.bottomAppBar);
        setSupportActionBar(bottomAppBar);

        mainActivity = this;

        //load administrative purpose
        //also check if the group was deleted
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Groups").document(getIntent().getStringExtra("groupName")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();
                    if(doc != null && doc.exists())
                    {
                        ArrayList<String> checkedInOn = Utilities.unStringify(doc.getString("checkedInOn"));
                        if(checkedInOn.contains(FirebaseAuth.getInstance().getUid()))
                        {
                            isAdmin = false;
                        }
                        else
                        {
                            isAdmin = true;
                        }
                        Home starterFragment = new Home();

                        Bundle b = new Bundle();
                        b.putString("groupName", getIntent().getStringExtra("groupName"));
                        b.putBoolean("isAdmin", isAdmin);
                        starterFragment.setArguments(b);

                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, starterFragment).commit();

                        bottomAppBar.setOnMenuItemClickListener(navListener);
                    }

                }
            }

        });




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
            Bundle b = new Bundle();
            switch(item.getItemId())
            {
                case R.id.nav_em_contacts:
                {
                    selectedFragment = new EmergencyContacts();
                    b.putString("groupName", getIntent().getStringExtra("groupName"));
                    b.putBoolean("isAdmin", isAdmin);
                    selectedFragment.setArguments(b);
                    break;
                }
                case R.id.nav_setting:
                {
                    selectedFragment = new Settings();
                    b.putString("groupName", getIntent().getStringExtra("groupName"));
                    b.putBoolean("isAdmin", isAdmin);
                    selectedFragment.setArguments(b);
                    break;
                }
                default:
                {

                    selectedFragment = new Home();
                    b.putString("groupName", getIntent().getStringExtra("groupName"));
                    b.putBoolean("isAdmin", isAdmin);
                    selectedFragment.setArguments(b);
                    break;
                }
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, selectedFragment).commit();

            return true;
        }
    };


}
