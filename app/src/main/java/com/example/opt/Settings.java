package com.example.opt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class Settings extends Fragment
{
    private String username;
    private View view;
    private JsonReader jsonReader;
    private Handler handler;
    private Spinner spinnerPeriod;
    private Spinner spinnerInterval;
    private EditText numWarningsInput;
    private Button button;

    private UserInfo userInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        this.userInfo = getArguments().getParcelable("userInfo");
        this.userInfo.getTimePeriod();

        spinnerPeriod = (Spinner) view.findViewById(R.id.spinnerPeriod);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.periodTimes, R.layout.spinner_item);
        spinnerPeriod.setAdapter(arrayAdapter);

        spinnerPeriod.setSelection(arrayAdapter.getPosition(this.userInfo.getTimePeriod() + " hours"));


        spinnerInterval = (Spinner) view.findViewById(R.id.spinnerInterval);
        ArrayAdapter<CharSequence> arrayAdapter1 = ArrayAdapter.createFromResource(this.getActivity(), R.array.intervalTimes, R.layout.spinner_item);
        spinnerInterval.setAdapter(arrayAdapter1);

        spinnerInterval.setSelection(arrayAdapter.getPosition("" + this.userInfo.getTimeBetweenWarnings() + " hours"));

        numWarningsInput = (EditText) view.findViewById(R.id.numWarningsInput);

        int warnings = userInfo.getNumWarnings();
        numWarningsInput.setText("" + userInfo.getNumWarnings());

        //getting username from main activity class to know which row to update in sql
        this.username = userInfo.getUsername();

        jsonReader = new JsonReader(this);
        button = view.findViewById(R.id.statusChange);
        jsonReader.setUsername(username);
        jsonReader.setAction("updateCustomSettings");

        button.setOnClickListener(new View.OnClickListener()

        {

            @Override
            public void onClick(View v)
            {
                //getting other values
                String timePeriod = spinnerPeriod.getSelectedItem().toString();
                int numWarnings = Integer.parseInt(numWarningsInput.getText().toString());

                String stringTimeIntervals = spinnerInterval.getSelectedItem().toString();
                int spaceIndex = stringTimeIntervals.indexOf(" ");

                int timeBetweenIntervals = Integer.parseInt(stringTimeIntervals.substring(0, spaceIndex));

                jsonReader.setCustomSettings(timePeriod, numWarnings, timeBetweenIntervals);
                jsonReader.start();

            }
        });

        final Toast toast = Toast.makeText(getActivity(), "successfully updated user prefrences", Toast.LENGTH_LONG);
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

        return view;

    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }

    public void receive(String jsonResult)
    {
        String success = "0";
        try{
            JSONObject jObject = new JSONObject(jsonResult);
            String strMessage = jObject.getString("message");
            success = jObject.getString("success");

            this.userInfo.updateCustomSettings(jObject.getInt("timePeriod"), jObject.getInt("numWarnings"), jObject.getInt("timeBetweenWarnings"));
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        Message message = handler.obtainMessage(Integer.parseInt(success));
        message.sendToTarget();
    }


}
