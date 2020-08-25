package com.ncourage.markmeok;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Utilities
{
    @SuppressLint("StaticFieldLeak")
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static String stringifyArrayList(ArrayList<String> arrList)
    {
        String result = "";
        for(String s: arrList)
        {
            result += (s + ",");
        }
        return result;
    }
    public static ArrayList<String> unStringify(String str)
    {
        ArrayList<String> result = new ArrayList<>();
        while(str.contains(","))
        {
            //gets the FIRST occurrence of this comma
            int index = str.indexOf(",");
            result.add(str.substring(0, index));

            if(index+1 < str.length())
            {
                str = str.substring(index+1);
            }
            else
            {
                break;
            }

        }
        return result;
    }


    //have to figure out how to make this return a boolean
    public static void acceptPendingUser(final String group, final String uid)
    {
        db.collection("Groups").document(group).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(task.isSuccessful())
                {
                    DocumentSnapshot doc = task.getResult();
                    if(doc != null && doc.exists())
                    {

                        ArrayList<String> pendingUsers = (ArrayList<String>)  doc.get("pendingUsers");
                        pendingUsers.remove(uid);



                        ArrayList<String> users = (ArrayList<String>) doc.get("users");

                        //main line
                        users.add(uid);

                        Map<String, Object> propertiesToAdd = new HashMap<>();
                        propertiesToAdd.put("users", users);
                        propertiesToAdd.put("pendingUsers", pendingUsers);
                        propertiesToAdd.put("startTimer", true);

                        db.collection("Groups").document(group).update(propertiesToAdd);


                    }
                }

            }
        });
        //delete invitation
        db.collection("users").document(uid).collection("invitation").document(group).delete();
        //update user profile
        db.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        if(document.contains("Groups"))
                        {

                            ArrayList<String> pastGroups = (ArrayList<String>) document.getData().get("Groups");
                            pastGroups.add(group);
                            Map<String, Object> propertiesToAdd = new HashMap<>();
                            propertiesToAdd.put("Groups", pastGroups);
                            db.collection("users").document(uid).set(propertiesToAdd, SetOptions.merge())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                           //do something here
                                        }
                                    });
                        }
                        else
                        {
                            Map<String, Object> propertiesToAdd = new HashMap<>();
                            ArrayList<String> temp = new ArrayList<>();
                            temp.add(group);
                            propertiesToAdd.put("Groups", temp);
                            db.collection("users").document(uid).set(propertiesToAdd, SetOptions.merge())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            //do something here
                                        }
                                    });

                        }
                    }
                }
                else
                {
                    //Toast.makeText(newGroup, "error connecting to database", Toast.LENGTH_LONG).show();
                }
            }

        });
    }






}
