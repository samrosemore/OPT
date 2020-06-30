package com.ncourage.markmeok;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;


public class Groups_Frag extends Fragment
{

    private View view;
    private FirebaseFirestore db;
    private String uid;
    private Groups_Frag frag;

    private ArrayList<String> groups;
    private ArrayList<Long> minutesLeft;
    private ArrayList<Long> hoursLeft;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_groups_, container, false);



        db = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getUid();

        frag = this;
        groups = new ArrayList<>();
        minutesLeft = new ArrayList<>();
        hoursLeft = new ArrayList<>();

        db.collection("users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>()
        {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e)
            {
                if(documentSnapshot != null && documentSnapshot.exists())
                {
                    if(documentSnapshot.contains("Groups"))
                    {
                        String groupString = (String) documentSnapshot.getData().get("Groups");
                        groups = Utilities.unStringify(groupString);

                        RecyclerView recView = (RecyclerView) view.findViewById(R.id.recView);


                        ListAdapter adapter = new ListAdapter(frag.getContext(), groups, "groups");
                        recView.setAdapter(adapter);
                        recView.setLayoutManager(new LinearLayoutManager(frag.getContext()));
                    }

                }


                else
                {
                    Toast.makeText(frag.getContext(), "Error connecting to internet", Toast.LENGTH_LONG).show();
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
