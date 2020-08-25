package com.ncourage.markmeok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.firestore.SetOptions;


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

    private ArrayList<String> pastGroups;

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
                Task<QuerySnapshot> queryEmail = db.collection("users").whereEqualTo("email", newParticipant.getText().toString().toLowerCase()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    try
                                    {
                                        if(task.getResult().size() == 0)
                                        {

                                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    switch (which){
                                                        case DialogInterface.BUTTON_POSITIVE:
                                                            //Yes button clicked
                                                            //send referal to non user

                                                            String subject = "You've been invited to join the MarkMeOK community!";
                                                            String androidInvitationLink = "https://play.google.com/store/apps/details?id=com.ncourage.markmeok";
                                                            String swiftInvitationLink = "https://itunes.apple.com/us/app/urbanspoon/id1521328421";
                                                            String msg = "Join the community by clicking on the links below. Android: "
                                                                    + androidInvitationLink + " or IOS: " + swiftInvitationLink;
                                                            String msgHtml = "<p>Join the community by clicking on the links below. <br> <a href='"+ androidInvitationLink +"'>Android</a> <br>"
                                                                    + "<a href=" + swiftInvitationLink + ">IOS</a>!</p>";

                                                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                                                            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                                                            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                                            intent.putExtra(Intent.EXTRA_TEXT, msg);
                                                            intent.putExtra(Intent.EXTRA_HTML_TEXT, msgHtml);
                                                            if (intent.resolveActivity(getPackageManager()) != null) {
                                                                startActivity(intent);

                                                            }
                                                            //will play around with this later.....need to get
                                                            //a playstore id first
                                                            break;

                                                        case DialogInterface.BUTTON_NEGATIVE:
                                                            //No button clicked
                                                            break;
                                                    }
                                                }
                                            };

                                            AlertDialog.Builder builder = new AlertDialog.Builder(newGroup);
                                            builder.setMessage("User not registered... would you like us to send the user an invitation?").setPositiveButton("Yes", dialogClickListener)
                                                    .setNegativeButton("No", dialogClickListener).show();
                                        }
                                        else
                                        {
                                            for (QueryDocumentSnapshot document : task.getResult())
                                            {
                                                if(document.getId() == FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                {
                                                    Toast.makeText(newGroup, "You are already in the group", Toast.LENGTH_LONG).show();
                                                }
                                                else
                                                {
                                                    participantsUIDs.add(document.getId());
                                                    participants.add((String) document.get("fullName"));
                                                    adapter.notifyItemInserted(participants.size() - 1);
                                                }

                                            }
                                        }

                                    }
                                    catch(Exception e)
                                    {
                                        Toast.makeText(newGroup, "please check your connection to wifi", Toast.LENGTH_LONG).show();
                                    }

                                }
                                else
                                {
                                    Toast.makeText(newGroup, "please check your connection to wifi", Toast.LENGTH_LONG).show();
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
                    propertiesToAdd.put("pendingUsers", participantsUIDs);
                    propertiesToAdd.put("startTimer", false);
                    propertiesToAdd.put("users", new ArrayList<String>());
                    propertiesToAdd.put("checkedInOn", checkedInOn);
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

                            pastGroups = (ArrayList<String>) document.getData().get("Groups");

                            if(pastGroups.size() == 0)
                            {
                                //have to update default group property
                                pastGroups.add(documentID);
                                Map<String, Object> propertiesToAdd = new HashMap<>();
                                propertiesToAdd.put("Groups", pastGroups);
                                propertiesToAdd.put("defaultGroup", documentID);
                                db.collection("users").document(FirebaseAuth.getInstance().getUid()).set(propertiesToAdd)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                Intent toMSettings = new Intent(newGroup, MSettings.class);
                                                toMSettings.putExtra("groupName", documentID);
                                                toMSettings.putExtra("nameOfGroup", nameOfGroup.getText().toString());
                                                toMSettings.putExtra("otherUsers", participantsUIDs);
                                                startActivity(toMSettings);
                                            }
                                        });
                            }
                            else
                            {
                                pastGroups.add(documentID);
                                Map<String, Object> propertiesToAdd = new HashMap<>();
                                propertiesToAdd.put("Groups", pastGroups);
                                db.collection("users").document(FirebaseAuth.getInstance().getUid()).set(propertiesToAdd)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                Intent toMSettings = new Intent(newGroup, MSettings.class);
                                                toMSettings.putExtra("groupName", documentID);
                                                toMSettings.putExtra("nameOfGroup", nameOfGroup.getText().toString());
                                                toMSettings.putExtra("otherUsers", participantsUIDs);
                                                startActivity(toMSettings);
                                            }
                                        });
                            }


                        }
                        else
                        {
                            //if "Groups" key isnt defined, then that automatically means that this group is the default group
                            Map<String, Object> propertiesToAdd = new HashMap<>();

                            pastGroups = new ArrayList<>();
                            pastGroups.add(documentID);
                            propertiesToAdd.put("Groups", pastGroups);
                            propertiesToAdd.put("defaultGroup", documentID);
                            db.collection("users").document(FirebaseAuth.getInstance().getUid()).set(propertiesToAdd, SetOptions.merge())
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
