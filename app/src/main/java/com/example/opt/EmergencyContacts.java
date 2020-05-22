package com.example.opt;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.json.JSONException;
import org.json.JSONObject;


public class EmergencyContacts extends Fragment
{


    private View view;
    private UserInfo userInfo;

    private EditText contact1;
    private EditText onePhoneNumber;
    private EditText oneEmail;

    private EditText contact2;
    private EditText twoPhoneNumber;
    private EditText twoEmail;


    private FirebaseFirestore db;
    private String uID;
    private String[] strArgs;
    private long[] longArgs;


    private EmergencyContacts emContacts;
    private Handler handler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_emergency_contacts, container, false);
        //now to set these values


        //self reference


        //firebase stuff
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.uID = user.getUid();
        db = FirebaseFirestore.getInstance();

        emContacts = this;

        db.collection("EmergencyContacts").document(uID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e)
            {
                strArgs = new String[4];
                strArgs[0] = (String) documentSnapshot.getData().get("contactOne");
                strArgs[1] = (String) documentSnapshot.getData().get("contactTwo");
                strArgs[2] = (String) documentSnapshot.getData().get("oneEmail");
                strArgs[3] = (String) documentSnapshot.getData().get("twoEmail");

                longArgs = new long[2];
                longArgs[0] = (long) documentSnapshot.getData().get("onePhoneNumber");
                longArgs[1] = (long) documentSnapshot.getData().get("twoPhoneNumber");


                contact1 = (EditText) view.findViewById(R.id.contact1FullName);
                onePhoneNumber = (EditText) view.findViewById(R.id.contact1PhoneNumber);
                oneEmail = (EditText) view.findViewById(R.id.contact1Email);
                //now to set these values
                contact2 = (EditText) view.findViewById(R.id.contact2FullName);
                twoPhoneNumber = (EditText) view.findViewById(R.id.contact2PhoneNumber);
                twoEmail = (EditText) view.findViewById(R.id.contact2Email);

                emContacts.receive(strArgs, longArgs);

                //on thread outside main AI thread ... create handler and that stuff to solve the error
                contact1.setText(strArgs[0]);
                contact2.setText(strArgs[1]);

                oneEmail.setText(strArgs[2]);
                twoEmail.setText(strArgs[3]);

                onePhoneNumber.setText("" + longArgs[0]);
                twoPhoneNumber.setText("" + longArgs[1]);
            }
        });

        final Toast toast = Toast.makeText(getActivity(), "successfully updated emergency contacts", Toast.LENGTH_LONG);


        Button button = (Button) view.findViewById(R.id.statusChangeEM);

        //experimental
        final EmergencyContacts emContacts = this;
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String strPhoneOne = ((EditText) view.findViewById(R.id.contact1PhoneNumber)).getText().toString();
                String strPhoneTwo = ((EditText) view.findViewById(R.id.contact2PhoneNumber)).getText().toString();

                if(Verification.checkPhoneNumber(strPhoneOne) && Verification.checkPhoneNumber(strPhoneTwo))
                {
                    String contactEmail = ((EditText) view.findViewById(R.id.contact1Email)).getText().toString();
                    String twoEmail = ((EditText) view.findViewById(R.id.contact2Email)).getText().toString();

                    if(Verification.checkEmail(contactEmail) && Verification.checkEmail(twoEmail))
                    {
                        String contactOne = ((EditText) view.findViewById(R.id.contact1FullName)).getText().toString();
                        long onePhoneNumber = Integer.parseInt(strPhoneOne);


                        String contactTwo = ((EditText) view.findViewById(R.id.contact2FullName)).getText().toString();
                        long twoPhoneNumber = Integer.parseInt(strPhoneTwo);


                        db.collection("EmergencyContacts").document(uID).update("contactOne", contactOne);
                        db.collection("EmergencyContacts").document(uID).update("contactTwo", contactTwo);

                        db.collection("EmergencyContacts").document(uID).update("oneEmail", contactEmail);
                        db.collection("EmergencyContacts").document(uID).update("onePhoneNumber", onePhoneNumber);

                        db.collection("EmergencyContacts").document(uID).update("twoEmail", twoEmail);
                        db.collection("EmergencyContacts").document(uID).update("twoPhoneNumber", twoPhoneNumber);
                    }
                }

            }
        });

        //just in case
        handler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message inputMessage)
            {


            }
        };






        return view;

    }



    @Nullable
    @Override
    public View getView()
    {
        return view;

    }

    private void receive(String[] strArgs, long[] longArgs)  {
        try
        {
            String jsonString = new JSONObject().put("contactOne", strArgs[0])
                    .put("contactTwo", strArgs[1])
                    .put("oneEmail", strArgs[2])
                    .put("twoEmail", strArgs[3])
                    .put("onePhoneNumber", longArgs[0])
                    .put("twoPhoneNumber", longArgs[1]).toString();
            // Message message = handler.obtainMessage(1, );
            //  message.sendToTarget();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }





}
