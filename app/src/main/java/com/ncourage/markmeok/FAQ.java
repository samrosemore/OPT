package com.ncourage.markmeok;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

import android.content.Intent;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class FAQ extends AppCompatActivity
{
    private FirebaseFirestore db;
    private FAQ fq;
    private ExpandableListAdapter listAdapter;
    private ExpandableListView listView;
    private ArrayList<String> headers;
    private HashMap<String, String> items;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        fq = this;
        headers = new ArrayList<>();
        items = new HashMap<>();

        db = FirebaseFirestore.getInstance();
        db.collection("FAQ").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
            {
                if(queryDocumentSnapshots != null)
                {
                    for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
                    {
                        if(doc.exists() && doc.contains("Question") && doc.contains("Answer"))
                        {
                            String question = doc.getString("Question");
                            String answer = doc.getString("Answer");

                            headers.add(question);
                            items.put(question, answer);


                        }
                    }
                    listView = findViewById(R.id.faqs);
                    listAdapter = new ExpandableListAdapter(fq, headers, items);
                    listView.setAdapter(listAdapter);
                }

            }
        });
    }
}
