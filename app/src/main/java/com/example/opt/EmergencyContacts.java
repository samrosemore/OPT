package com.example.opt;

import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;


public class EmergencyContacts extends Fragment
{

    private String username;
    private JsonReader jsonReader;
    private Handler handler;
    private View view;
    private UserInfo userInfo;

    private EditText contact1;
    private EditText onePhoneNumber;
    private EditText oneEmail;

    private EditText contact2;
    private EditText twoPhoneNumber;
    private EditText twoEmail;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_emergency_contacts, container, false);

        this.userInfo = getArguments().getParcelable("userInfo");
        this.username = userInfo.getUsername();

        //now to set these values
        contact1 = (EditText) view.findViewById(R.id.contact1FullName);
        contact1.setText(userInfo.getContactOne());

        onePhoneNumber = (EditText) view.findViewById(R.id.contact1PhoneNumber);
        onePhoneNumber.setText("" + userInfo.getOnePhoneNumber());

        oneEmail = (EditText) view.findViewById(R.id.contact1Email);
        oneEmail.setText(userInfo.getOneEmail());

        //now to set these values
        contact2 = (EditText) view.findViewById(R.id.contact2FullName);
        contact2.setText(userInfo.getContactTwo());

        twoPhoneNumber = (EditText) view.findViewById(R.id.contact2PhoneNumber);
        twoPhoneNumber.setText("" + userInfo.getTwoPhoneNumber());

        twoEmail = (EditText) view.findViewById(R.id.contact2Email);
        twoEmail.setText(userInfo.getOneEmail());







        final Toast toast = Toast.makeText(getActivity(), "successfully updated emergency contacts", Toast.LENGTH_LONG);
        handler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message inputMessage)
            {
                if(inputMessage.what == 1)
                {
                   toast.show();
                }

            }
        };

        Button button = (Button) view.findViewById(R.id.statusChangeEM);

        //experimental
        final EmergencyContacts emContacts = this;
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String contactOne = ((EditText) view.findViewById(R.id.contact1FullName)).getText().toString();
                int onePhoneNumber = Integer.parseInt(((EditText) view.findViewById(R.id.contact1PhoneNumber)).getText().toString());
                String contactEmail = ((EditText) view.findViewById(R.id.contact1Email)).getText().toString();

                String contactTwo = ((EditText) view.findViewById(R.id.contact2FullName)).getText().toString();
                int twoPhoneNumber = Integer.parseInt(((EditText) view.findViewById(R.id.contact2PhoneNumber)).getText().toString());
                String twoEmail = ((EditText) view.findViewById(R.id.contact2Email)).getText().toString();

                userInfo.updateEmergencyContacts(contactOne, onePhoneNumber, contactEmail, contactTwo, twoPhoneNumber, twoEmail);

                jsonReader = new JsonReader(emContacts, username, contactOne, onePhoneNumber, contactEmail, contactTwo, twoPhoneNumber, twoEmail);
                jsonReader.start();
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

    public void receive(String jsonResult)
    {
        String success = "0";
        try{
            JSONObject jObject = new JSONObject(jsonResult);
            String strMessage = jObject.getString("message");
            success = jObject.getString("success");
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        Message message = handler.obtainMessage(Integer.parseInt(success));
        message.sendToTarget();
    }
}
