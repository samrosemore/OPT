package com.ncourage.markmeok;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class JsonReader extends Thread {

    //only for check in
    private Home home;
    //only for login
    private Login login;
    //will be used in  login and signup
    private String action;
    private String username;
    private String password;
    private String jsonResult;

    //will only be used in sign up
    private String fullName;
    private String email;
    private String phoneNumber;
    private SignUp signUp;


    //will be used for custom settings (mandatory)
    private MSettings mSettings;
    private String timePeriod;
    private int numWarnings;
    private int timeBetweenWarnings;

    //will be used for custom settings (non mandotry)
    private Settings settings;

    //only for Emergency Contact
    private EmergencyContacts emergencyContacts;

    //only be used for emergency contacts
    private MEmergencyContacts mEmergencyContacts;
    private String contactOne;
    private int onePhoneNumber;
    private String oneEmail;
    private String contactTwo;
    private int twoPhoneNumber;
    private String twoEmail;





    public JsonReader(String username, Home home)
    {
        this.action = "checkIn";
        this.username = username;
        this.home = home;
    }

    //to access with login
    public JsonReader(String username, String password, Login login)
    {
        //if they are using this constructor then its automatically a login attempt
        this.action = "login";
        this.username = username;
        this.password = password;
        this.login = login;
    }

    //to access with signUp
    public JsonReader(String fullName, String phoneNumber, String email, String username, String password, SignUp signUp)
    {
        this.action = "signUp";
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.fullName = fullName;
        this.signUp = signUp;
    }

    //to access with all others commands
    public JsonReader(MSettings mSettings)
    {
        this.mSettings = mSettings;
    }

    public JsonReader(Settings settings)
    {
        this.settings = settings;
    }


    public JsonReader(MEmergencyContacts mEmergencyContacts, String username, String contactOne, int onePhoneNumber, String oneEmail, String contactTwo, int twoPhoneNumber, String twoEmail)
    {
        this.mEmergencyContacts = mEmergencyContacts;
        this.action = "addEmergencyContacts";
        this.username = username;

        //now heres the fun part

        this.contactOne = contactOne;
        this.onePhoneNumber = onePhoneNumber;
        this.oneEmail = oneEmail;

        this.contactTwo = contactTwo;
        this.twoPhoneNumber = twoPhoneNumber;
        this.twoEmail = twoEmail;
    }

    public JsonReader(EmergencyContacts EmergencyContacts, String username, String contactOne, int onePhoneNumber, String oneEmail, String contactTwo, int twoPhoneNumber, String twoEmail)
    {
        this.emergencyContacts = EmergencyContacts;
        this.action = "updateEmergencyContacts";
        this.username = username;

        //now heres the fun part

        this.contactOne = contactOne;
        this.onePhoneNumber = onePhoneNumber;
        this.oneEmail = oneEmail;

        this.contactTwo = contactTwo;
        this.twoPhoneNumber = twoPhoneNumber;
        this.twoEmail = twoEmail;
    }



    @Override
    public void run()
    {
        if(action.equals("login"))
        {
            this.jsonResult = attemptLogin();
        }
        else if(action.equals("signUp"))
        {
            this.jsonResult = attemptSignUp();
        }
        else if(action.equals("updateCustomSettings"))
        {
            this.jsonResult = updateCustomSettings();
        }
        else if(action.equals("addEmergencyContacts"))
        {
           this.jsonResult = alterEmergencyContact();
        }
        else if(action.equals("updateEmergencyContacts"))
        {
            this.jsonResult = alterEmergencyContact();
        }
        else if(action.equals("checkIn"))
        {
            this.jsonResult = checkIn();
        }


        sendResultUpstream();
        super.run();




    }
    private String checkIn()
    {
        try {
            URL myScript = new URL("http://192.168.1.153/opt/userManagment.php");
            HttpURLConnection conn = (HttpURLConnection) myScript.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Chrome/81.0.4044.122");

            conn.setDoOutput(true);
            conn.setDoInput(true);

            Uri.Builder builder = new Uri.Builder();
            builder.appendQueryParameter("Action", this.action);
            builder.appendQueryParameter("username", this.username);

            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, StandardCharsets.UTF_8));

            writer.write(query);
            writer.flush();
            writer.close();
            os.close();


            BufferedReader bReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            StringBuilder stringBuilder = new StringBuilder();


            while((line = bReader.readLine()) != null)
            {
                stringBuilder.append(line);
            }


            return stringBuilder.toString();



        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private String alterEmergencyContact()
    {
        try {
            URL myScript = new URL("http://192.168.1.153/opt/userManagment.php");
            HttpURLConnection conn = (HttpURLConnection) myScript.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Chrome/81.0.4044.122");

            conn.setDoOutput(true);
            conn.setDoInput(true);

            Uri.Builder builder = new Uri.Builder();
            builder.appendQueryParameter("Action", this.action);
            builder.appendQueryParameter("username", this.username);

            builder.appendQueryParameter("contactOne", this.contactOne);
            builder.appendQueryParameter("onePhoneNumber", String.valueOf(this.onePhoneNumber));
            builder.appendQueryParameter("oneEmail", this.oneEmail);

            builder.appendQueryParameter("contactTwo", this.contactTwo);
            builder.appendQueryParameter("twoPhoneNumber", String.valueOf(this.twoPhoneNumber));
            builder.appendQueryParameter("twoEmail", this.twoEmail);



            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            writer.write(query);
            writer.flush();
            writer.close();
            os.close();


            BufferedReader bReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            StringBuilder stringBuilder = new StringBuilder();


            while((line = bReader.readLine()) != null)
            {
                stringBuilder.append(line);
            }

            return stringBuilder.toString();



        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private String updateCustomSettings()
    {
        try {
            URL myScript = new URL("http://192.168.1.153/opt/userManagment.php");
            HttpURLConnection conn = (HttpURLConnection) myScript.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Chrome/81.0.4044.122");

            conn.setDoOutput(true);
            conn.setDoInput(true);

            Uri.Builder builder = new Uri.Builder();
            builder.appendQueryParameter("Action", this.action);
            builder.appendQueryParameter("username", this.username);
            builder.appendQueryParameter("timePeriod", this.timePeriod);
            builder.appendQueryParameter("numWarnings", String.valueOf(this.numWarnings));
            builder.appendQueryParameter("timeBetweenWarnings", String.valueOf(this.timeBetweenWarnings));
            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            writer.write(query);
            writer.flush();
            writer.close();
            os.close();


            BufferedReader bReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            StringBuilder stringBuilder = new StringBuilder();


            while((line = bReader.readLine()) != null)
            {
                stringBuilder.append(line);
            }

            return stringBuilder.toString();



        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private String attemptSignUp()
    {
        try {
            URL myScript = new URL("http://192.168.1.153/opt/userManagment.php");
            HttpURLConnection conn = (HttpURLConnection) myScript.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Chrome/81.0.4044.122");

            conn.setDoOutput(true);
            conn.setDoInput(true);

            Uri.Builder builder = new Uri.Builder();
            builder.appendQueryParameter("Action", this.action);
            builder.appendQueryParameter("fullName", this.fullName);
            builder.appendQueryParameter("phoneNumber", this.phoneNumber);
            builder.appendQueryParameter("email", this.email);
            builder.appendQueryParameter("username", this.username);
            builder.appendQueryParameter("password", this.password);
            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            writer.write(query);
            writer.flush();
            writer.close();
            os.close();


            BufferedReader bReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            StringBuilder stringBuilder = new StringBuilder();


            while((line = bReader.readLine()) != null)
            {
                stringBuilder.append(line);
            }

            return stringBuilder.toString();



        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private String attemptLogin()
    {
        try {
            URL myScript = new URL("http://192.168.1.153/opt/userManagment.php");
            HttpURLConnection conn = (HttpURLConnection) myScript.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Chrome/81.0.4044.122");

            conn.setDoOutput(true);
            conn.setDoInput(true);

            Uri.Builder builder = new Uri.Builder();
            builder.appendQueryParameter("Action", this.action);
            builder.appendQueryParameter("username", this.username);
            builder.appendQueryParameter("password", this.password);
            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, StandardCharsets.UTF_8));

            writer.write(query);
            writer.flush();
            writer.close();
            os.close();


            BufferedReader bReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            StringBuilder stringBuilder = new StringBuilder();


            while((line = bReader.readLine()) != null)
            {
                stringBuilder.append(line);
            }


            return stringBuilder.toString();



        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void sendResultUpstream()
    {

        if(this.action.equals("login"))
        {
            //this.login.receive(jsonResult);
        }
        else if(this.action.equals("signUp"))
        {
            //this.signUp.receive(jsonResult);
        }
        //this will be sent to the mandatory settings class
        else if(this.action.equals("updateCustomSettings") && mSettings != null)
        {
            //this.mSettings.receive(jsonResult);
        }
        //this will be sent to the non mandatory settings class
        else if(this.action.equals("updateCustomSettings") && settings != null)
        {
            //this.settings.receive(jsonResult);
        }
        else if(this.action.equals("addEmergencyContacts"))
        {
            //this.mEmergencyContacts.receive(jsonResult);
        }
        else if(this.action.equals("alterEmergencyContacts"))
        {
            //this.emergencyContacts.receive(jsonResult);
        }
        else if(this.action.equals("checkIn"))
        {
            //this.home.receive(jsonResult);
        }


    }


    public void setUsername(String x)
    {
        this.username = x;
    }
    public void setAction(String x)
    {
        this.action = x;
    }

    public void setCustomSettings(String timePeriod, int numWarnings, int timeBetweenWarnings)
    {
        this.timePeriod = timePeriod;
        this.numWarnings = numWarnings;
        this.timeBetweenWarnings = timeBetweenWarnings;
    }


}
