package com.ncourage.markmeok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class NewGroup extends AppCompatActivity
{

    private ArrayList<String> participants;
    private EditText newParticipant;
    private ListAdapter adapter;
    private NewGroup newGroup;
    private FirebaseFirestore db;

    private ArrayList<String> participantsUIDs;
    private ArrayList<String> checkedInOn;
    private EditText nameOfGroup;

    private String pastGroups;

    private String documentID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        db = FirebaseFirestore.getInstance();
        participantsUIDs = new ArrayList<>();
        checkedInOn = new ArrayList<>();

        RecyclerView recView = (RecyclerView) findViewById(R.id.newParticipantList);

        participants = new ArrayList<>();
        adapter = new ListAdapter(this, participants, participantsUIDs, checkedInOn, "listUsersInGroup");
        recView.setAdapter(adapter);
        recView.setLayoutManager(new LinearLayoutManager(this));

        newParticipant = (EditText) findViewById(R.id.newParticipant);

        pastGroups = null;

        //back button
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent back = new Intent(newGroup, GroupListings.class);
                startActivity(back);
            }
        });


        Button newParticipantButton = (Button) findViewById(R.id.newParticipantBtn);
        newParticipantButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Task<QuerySnapshot> queryEmail = db.collection("users").whereEqualTo("email", newParticipant.getText().toString()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    try
                                    {
                                        if(task.getResult().size() == 0)
                                        {
                                            Toast.makeText(newGroup, "Email not recongnized...please check for typos", Toast.LENGTH_LONG).show();
                                        }
                                        for (QueryDocumentSnapshot document : task.getResult())
                                        {
                                            participantsUIDs.add(document.getId());
                                            participants.add((String) document.get("fullName"));
                                            adapter.notifyItemInserted(participants.size() - 1);
                                        }
                                    }
                                    catch(Exception e)
                                    {
                                        Toast.makeText(newGroup, "Email not recongnized...please check for typos", Toast.LENGTH_LONG).show();
                                    }

                                }
                                else
                                {
                                    Toast.makeText(newGroup, "Email not recongnized...please check for typos", Toast.LENGTH_LONG).show();
                                }
                            }
                        });


            }

        });

        newGroup = this;
        Button submitParticipants = (Button) findViewById(R.id.newGroupSubmit);
        submitParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(participants.size() == 0)
                {
                    Toast.makeText(newGroup, "Please add participants to the group", Toast.LENGTH_LONG).show();
                }
                else
                {
                    nameOfGroup = (EditText) findViewById(R.id.nameOfGroup);

                    Map<String, Object> propertiesToAdd = new HashMap<>();

                    //will have to stringify the participantsUIDs
                    propertiesToAdd.put("groupName", nameOfGroup.getText().toString());
                    propertiesToAdd.put("pendingUsers", Utilities.stringifyArrayList(participantsUIDs));
                    propertiesToAdd.put("users", "");
                    propertiesToAdd.put("checkedInOn", Utilities.stringifyArrayList(checkedInOn));
                    propertiesToAdd.put("host", FirebaseAuth.getInstance().getUid());


                    db.collection("Groups")
                            .add(propertiesToAdd)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>()
                            {
                                @Override
                                public void onSuccess(DocumentReference documentReference)
                                {
                                    updateUserInformation();
                                    documentID = documentReference.getId();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    Toast.makeText(newGroup, "error connecting to database", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
    }

    //only updates THIS users information.... invitations added to other users accounts in ms settings
    private void updateUserInformation()
    {

        db.collection("users").document(FirebaseAuth.getInstance().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists())
                    {
                        if(document.contains("Groups"))
                        {
                            pastGroups = (String) document.getData().get("Groups");
                            pastGroups += documentID + ",";
                            Map<String, Object> propertiesToAdd = new HashMap<>();
                            propertiesToAdd.put("Groups", pastGroups);
                            db.collection("users").document(FirebaseAuth.getInstance().getUid()).set(propertiesToAdd)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            Intent toMSettings = new Intent(newGroup, MSettings.class);
                                            toMSettings.putExtra("groupName", documentID);
                                            toMSettings.putExtra("otherUsers", participantsUIDs);
                                            startActivity(toMSettings);
                                        }
                                    });
                        }
                        else
                        {
                            Map<String, Object> propertiesToAdd = new HashMap<>();
                            propertiesToAdd.put("Groups", documentID + ",");
                            db.collection("users").document(FirebaseAuth.getInstance().getUid()).set(propertiesToAdd)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            Intent toMSettings = new Intent(newGroup, MSettings.class);
                                            toMSettings.putExtra("groupName", documentID);
                                            toMSettings.putExtra("otherUsers", participantsUIDs);
                                            startActivity(toMSettings);
                                        }
                                    });

                        }
                    }
                }
                else
                {
                    Toast.makeText(newGroup, "error connecting to database", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

}
