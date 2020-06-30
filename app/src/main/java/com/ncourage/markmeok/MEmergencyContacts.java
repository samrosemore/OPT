package com.ncourage.markmeok;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class MEmergencyContacts extends Fragment
{


    private String username;
    private JsonReader jsonReader;
    private Handler handler;

    private String uID;
    private FirebaseFirestore db;

    private MEmergencyContacts mEC;
    private View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.memergency_fragment, container, false);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.uID = user.getUid();
        db = FirebaseFirestore.getInstance();

        mEC = this;

        Button submit = (Button) view.findViewById(R.id.submitBtnEMC);

        submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {
                //validate first
                String strPhoneOne = ((EditText) view.findViewById(R.id.mContact1PhoneNumber)).getText().toString();
                String strPhoneTwo = ((EditText) view.findViewById(R.id.mContact2PhoneNumber)).getText().toString();

                if(Verification.checkPhoneNumber(strPhoneOne) && Verification.checkPhoneNumber(strPhoneTwo))
                {
                    String twoEmail = ((EditText) view.findViewById(R.id.mContact2Email)).getText().toString();
                    String contactEmail = ((EditText) view.findViewById(R.id.mContact1Email)).getText().toString();

                    if(Verification.checkEmail(contactEmail) && Verification.checkEmail(twoEmail))
                    {
                        String contactOne = ((EditText) view.findViewById(R.id.mContact1FullName)).getText().toString();
                        long onePhoneNumber = Integer.parseInt(strPhoneOne);
                        String contactTwo = ((EditText) view.findViewById(R.id.mContact2FullName)).getText().toString();
                        long twoPhoneNumber = Integer.parseInt(strPhoneTwo);


                        Map<String, Object> propertiesToAdd = new HashMap<>();
                        propertiesToAdd.put("contactOne", contactOne);
                        propertiesToAdd.put("contactTwo", contactTwo);
                        propertiesToAdd.put("oneEmail", contactEmail);
                        propertiesToAdd.put("onePhoneNumber", onePhoneNumber);
                        propertiesToAdd.put("twoEmail", twoEmail);
                        propertiesToAdd.put("twoPhoneNumber", twoPhoneNumber);

                        db.collection("EmergencyContacts").document(uID).set(propertiesToAdd).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                                fragmentTransaction.replace(R.id.welcome_frame, new Login());
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }
                        });


                    }
                    else
                    {
                        Toast.makeText(mEC.getContext(), "Please Enter a Valid Email", Toast.LENGTH_SHORT).show();
                    }
                }

                else
                {
                    Toast.makeText(mEC.getContext(), "Please Enter a Valid Phone Number", Toast.LENGTH_SHORT).show();
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
