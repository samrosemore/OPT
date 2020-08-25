package com.ncourage.markmeok;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder>
{
    //for group listings
    private ArrayList<String> groupID;
    private Context context;
    private String purpose;

    //for adding a new group
    private ArrayList<String> participantsAll;
    private ArrayList<String> participantsUIDS;
    private ArrayList<String> checkedInOn;

    //for invitations
    private ArrayList<String> invitations;
    private ArrayList<String> hosts;

    private FirebaseFirestore db;

    //just a generic version
    private ArrayList<String> genericElements;

    private ListAdapter listAdapter;

    private String defaultGroup;



    public ListAdapter(Context context, ArrayList<String> elements, String purpose)
    {
        this.context = context;
        this.purpose = purpose;

        if(this.purpose.equals("groups"))
        {
            this.groupID = elements;
        }
        else
        {
            this.genericElements = elements;
        }

        this.db = FirebaseFirestore.getInstance();
        this.listAdapter = this;
    }
    public ListAdapter(Context context, ArrayList<String> elements, String defaultGroup, String purpose)
    {
        this.context = context;
        this.purpose = purpose;
        this.defaultGroup = defaultGroup;

        if(this.purpose.equals("groups"))
        {
            this.groupID = elements;
        }
        else
        {
            this.genericElements = elements;
        }

        this.db = FirebaseFirestore.getInstance();
        this.listAdapter = this;
    }

    public ListAdapter(Context context, ArrayList<String> participantsAll, ArrayList<String> participantUIDs, ArrayList<String> checkedInOn, String purpose)
    {
        this.context = context;
        this.participantsAll = participantsAll;
        this.participantsUIDS = participantUIDs;
        this.checkedInOn = checkedInOn;
        this.purpose = purpose;
        this.listAdapter = this;
    }

    public ListAdapter(Context context, ArrayList<String> invitations, ArrayList<String> hosts, String purpose)
    {
        this.context = context;
        this.invitations = invitations;
        this.hosts = hosts;
        this.purpose = purpose;
        this.listAdapter = this;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view;

        LayoutInflater inflater = LayoutInflater.from(context);
        if(purpose.equals("groups"))
        {
            view = inflater.inflate(R.layout.row, parent, false);
            return new MyViewHolder(view);
        }
        else if(purpose.equals("listUsersInGroup"))
        {
            view = inflater.inflate(R.layout.row_layout, parent, false);
            return new MyViewHolder(view);
        }
        else if(purpose.equals("invitations"))
        {
            view = inflater.inflate(R.layout.invitation_row, parent, false);
            return new MyViewHolder(view);
        }
        else
        {
            view = inflater.inflate(R.layout.row1, parent, false);
            return new MyViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ListAdapter.MyViewHolder holder, final int position)
    {



        if(purpose.equals("groups"))
        {

            //convert each group ID into a group name
            db.collection("Groups").document(groupID.get(position)).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                {
                    if(task.isSuccessful())
                    {
                        DocumentSnapshot doc = task.getResult();
                        if(doc != null && doc.exists())
                        {
                            holder.groups.setText((String) doc.get("groupName"));
                            holder.mainLayout.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {

                                    Intent toSelectedGroup = new Intent(context, MainActivity.class);
                                    toSelectedGroup.putExtra("groupName", groupID.get(position));
                                    context.startActivity(toSelectedGroup);
                                }
                            });

                        }
                        else
                        {
                            db.collection("users").document(FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        DocumentSnapshot doc = task.getResult();
                                        if(doc != null && doc.exists())
                                        {
                                            ArrayList<String> groups =  (ArrayList<String>) doc.get("Groups");
                                            groups.remove(groupID.get(position));

                                            Map<String, Object> propertiesUpdated = new HashMap<String, Object>();
                                            propertiesUpdated.put("Groups", groups);

                                            db.collection("users").document(FirebaseAuth.getInstance().getUid()).update(propertiesUpdated);

                                            groupID.remove(position);
                                            listAdapter.notifyItemRemoved(position);
                                        }
                                    }
                                }
                            });

                        }
                    }
                }
            });

            if(defaultGroup.equals(groupID.get(position)))
            {
                holder.mainLayout.removeView(holder.defaultBtn);
            }
            else
            {
                holder.defaultBtn.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        final String group = groupID.get(position);
                        db.collection("users").document(FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task)
                            {
                                if(task.isSuccessful())
                                {
                                    DocumentSnapshot doc = task.getResult();
                                    if(doc != null && doc.exists())
                                    {
                                        Map<String, Object> propertiesUpdated = new HashMap<String, Object>();
                                        propertiesUpdated.put("defaultGroup", group);
                                        db.collection("users").document(FirebaseAuth.getInstance().getUid()).update(propertiesUpdated);
                                        Intent toHomeScreen = new Intent(context, GroupListings.class);
                                        context.startActivity(toHomeScreen);
                                    }
                                }
                            }
                        });

                    }
                });
            }




            holder.deleteGroup.setOnClickListener(new View.OnClickListener()
            {


                @Override
                public void onClick(View v)
                {
                    //prevent double click
                    holder.deleteGroup.setClickable(false);

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    //just have to delete the user from the database (user information and group data)
                                    db.collection("Groups").document(groupID.get(position)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                DocumentSnapshot doc = task.getResult();
                                                if(doc != null && doc.exists())
                                                {
                                                    ArrayList<String> groupParticipants = (ArrayList<String>) doc.get("users");
                                                    ArrayList<String> checkedInOn = (ArrayList<String>) doc.get("checkedInOn");

                                                    if(groupParticipants.contains(FirebaseAuth.getInstance().getUid()))
                                                    {
                                                        groupParticipants.remove(FirebaseAuth.getInstance().getUid());

                                                    }
                                                    if(checkedInOn.contains(FirebaseAuth.getInstance().getUid()))
                                                    {
                                                        checkedInOn.remove(FirebaseAuth.getInstance().getUid());
                                                    }


                                                    if(groupParticipants.size() <= 2 || checkedInOn.size() == 0)
                                                    {
                                                        db.collection("Groups").document(groupID.get(position)).delete();
                                                    }
                                                    else
                                                    {
                                                        Map<String, Object> propertiesUpdated = new HashMap<String, Object>();
                                                        propertiesUpdated.put("users", groupParticipants);
                                                        propertiesUpdated.put("checkedInOn", checkedInOn);
                                                        db.collection("Groups").document(groupID.get(position)).update(propertiesUpdated);
                                                    }





                                                }
                                            }

                                        }
                                    });

                                    db.collection("users").document(FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                DocumentSnapshot doc = task.getResult();
                                                if(doc != null && doc.exists())
                                                {
                                                    ArrayList<String> groups =  (ArrayList<String>) doc.get("Groups");
                                                    groups.remove(groupID.get(position));

                                                    Map<String, Object> propertiesUpdated = new HashMap<String, Object>();
                                                    propertiesUpdated.put("Groups", groups);

                                                    db.collection("users").document(FirebaseAuth.getInstance().getUid()).update(propertiesUpdated);
                                                }
                                            }
                                        }
                                    });

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure you want to delete this group?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();





                }
            });





        }
        else if(purpose.equals("listUsersInGroup"))
        {
            holder.participants.setText(participantsAll.get(position));
            holder.genericCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if(isChecked)
                    {
                        //get user ID and add it to checkedOnList
                        checkedInOn.add(participantsUIDS.get(position));
                    }
                    // if it gets unchecked
                    else
                    {
                        checkedInOn.remove(participantsUIDS.get(position));
                    }

                }
            });

            holder.deleteNewUSer.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    participantsUIDS.remove(position);
                    participantsAll.remove(position);
                    listAdapter.notifyItemRemoved(position);

                }
            });
        }
        else if(purpose.equals("invitations"))
        {
            holder.invitationTextView.setText(invitations.get(position));
            holder.hostTextView.setText(hosts.get(position));

            //add a listener
            holder.mainLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //do something
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    Utilities.acceptPendingUser(invitations.get(position), FirebaseAuth.getInstance().getUid());

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure you want to join this group?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            });

        }
        else
        {
            db.collection("users").document(genericElements.get(position)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                {
                    if(task.isSuccessful())
                    {
                        DocumentSnapshot doc = task.getResult();
                        if(doc != null&& doc.exists())
                        {
                            holder.generic.setText(doc.getString("fullName"));
                        }
                    }
                }
            });



        }

    }

    @Override
    public int getItemCount()
    {
        if(purpose.equals("groups"))
        {
            return groupID.size();
        }
        else if(purpose.equals("listUsersInGroup"))
        {
            return participantsAll.size();
        }
        else if(purpose.equals("invitations"))
        {
            return invitations.size();
        }
        else
        {
            return genericElements.size();
        }


    }

    public void setDefaultGroup(String defaultGroup) {
        this.defaultGroup = defaultGroup;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        ConstraintLayout mainLayout;
        TextView groups;
        TextView defaultBtn;

        //only will be used when purpose = listUsersInGroup
        CheckBox genericCheckBox;
        TextView participants;

        //only will be used when purpose = invitations
        TextView invitationTextView;
        TextView hostTextView;

        TextView generic;

        //beta testing
        ImageButton deleteNewUSer;
        ImageButton deleteGroup;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);

            if(purpose.equals("groups"))
            {
                mainLayout = (ConstraintLayout)  itemView.findViewById(R.id.mainLayout);
                groups = (TextView) itemView.findViewById(R.id.groupName);
                defaultBtn = (Button) itemView.findViewById(R.id.makeDefaultBtn);
                deleteGroup = (ImageButton) itemView.findViewById(R.id.deleteGroup);
            }
            else if(purpose.equals("listUsersInGroup"))
            {
                mainLayout = (ConstraintLayout) itemView.findViewById(R.id.newGroupLayout);
                participants = (TextView) itemView.findViewById(R.id.usersInGroup);
                genericCheckBox = (CheckBox) itemView.findViewById(R.id.genericCheckBox);
                deleteNewUSer = (ImageButton) itemView.findViewById(R.id.deleteNewUser);
            }
            else if(purpose.equals("invitations"))
            {
                mainLayout = (ConstraintLayout)  itemView.findViewById(R.id.invitationRow);
                invitationTextView = (TextView) itemView.findViewById(R.id.inviationGroupName);
                hostTextView = (TextView) itemView.findViewById(R.id.inviationHost);
            }
            else
            {
                //reusing the row layout from the group list
                mainLayout = (ConstraintLayout)  itemView.findViewById(R.id.mainLayout1);
                generic = (TextView) itemView.findViewById(R.id.personName);
            }


        }

    }
}
