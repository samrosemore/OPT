package com.ncourage.markmeok;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class Invitations extends Fragment
{

    private View view;
    private FirebaseFirestore db;

    private ArrayList<String> invitations;
    private ArrayList<String> hosts;

    private ListAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_invitations, container, false);

        invitations = new ArrayList<>();
        hosts = new ArrayList<>();

        RecyclerView invitationView = (RecyclerView) view.findViewById(R.id.invitationView);
        adapter = new ListAdapter(this.getContext(), invitations, hosts, "invitations");
        invitationView.setAdapter(adapter);
        invitationView.setLayoutManager(new LinearLayoutManager(this.getContext()));


        db = FirebaseFirestore.getInstance();
        db.collection("users").document(FirebaseAuth.getInstance().getUid()).collection("invitation")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
                    {
                        for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                        {
                            db.collection("Groups").document(doc.getId()).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                                    {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        if(documentSnapshot != null && documentSnapshot.exists())
                                        {
                                            invitations.add((String) documentSnapshot.get("groupName"));
                                        }
                                    }
                                }
                            });

                            hosts.add(doc.getString("host"));
                            adapter.notifyItemInserted(invitations.size() - 1);
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
