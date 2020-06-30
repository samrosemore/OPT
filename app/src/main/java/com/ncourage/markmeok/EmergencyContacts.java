package com.ncourage.markmeok;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;


public class EmergencyContacts extends Fragment
{


    private View view;
    private UserInfo userInfo;



    private FirebaseFirestore db;


    private EmergencyContacts emContacts;

    private ArrayList<String> pendingUsers;
    private ArrayList<String> checkedInOn;
    private ArrayList<String> currentUsers;

    private RecyclerView pendingREC;
    private RecyclerView checkedREC;
    private RecyclerView usersREC;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_emergency_contacts, container, false);
        //now to set these values

        pendingUsers = new ArrayList<>();
        checkedInOn = new ArrayList<>();
        currentUsers = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        emContacts = this;
        db.collection("Groups").document(getArguments().getString("groupName")).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e)
            {
                if(documentSnapshot != null && documentSnapshot.exists())
                {
                    pendingUsers = Utilities.unStringify(documentSnapshot.getString("pendingUsers"));
                    checkedInOn = Utilities.unStringify(documentSnapshot.getString("checkedInOn"));
                    currentUsers = Utilities.unStringify(documentSnapshot.getString("users"));

                    //get 3 recycler views
                    pendingREC = (RecyclerView) view.findViewById(R.id.pendingUsers);
                    ListAdapter adapter1 = new ListAdapter(emContacts.getContext(), pendingUsers, "");
                    pendingREC.setAdapter(adapter1);
                    pendingREC.setLayoutManager(new LinearLayoutManager(emContacts.getContext()));

                    checkedREC = (RecyclerView) view.findViewById(R.id.checkedInOn);
                    ListAdapter adapter2 = new ListAdapter(emContacts.getContext(), checkedInOn, "");
                    checkedREC.setAdapter(adapter2);
                    checkedREC.setLayoutManager(new LinearLayoutManager(emContacts.getContext()));


                    usersREC = (RecyclerView) view.findViewById(R.id.currentUsers);
                    ListAdapter adapter3 = new ListAdapter(emContacts.getContext(), currentUsers, "");
                    usersREC.setAdapter(adapter3);
                    usersREC.setLayoutManager(new LinearLayoutManager(emContacts.getContext()));

                }
            }
        });

        return view;

    }



    @Nullable
    @Override
    public View getView()
    {
        return view;

    }






}
