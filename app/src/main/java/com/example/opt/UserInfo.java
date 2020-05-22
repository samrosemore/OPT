package com.example.opt;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class UserInfo implements Parcelable
{

    private String username;
    private String contactOne;
    private String contactTwo;
    private int onePhoneNumber;
    private int twoPhoneNumber;
    private String oneEmail;
    private String twoEmail;
    private int numWarnings;
    private int timePeriod;
    private int timeBetweenWarnings;
    private int isCheckedIn;
    private String startTime;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");





    public UserInfo(String username)
    {

        this.username = username;

        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();

        //setting up the basics
        Query emergencyContacts = dRef.child("EmergencyContacts").equalTo(username, "username");




        emergencyContacts.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                contactOne = String.valueOf(dataSnapshot.child("contactOne"));
                contactTwo = String.valueOf(dataSnapshot.child("contactTwo"));

                onePhoneNumber = Integer.parseInt(String.valueOf(dataSnapshot.child("onePhoneNumber")));
                oneEmail = String.valueOf(dataSnapshot.child("oneEmail"));

                twoPhoneNumber = Integer.parseInt(String.valueOf(dataSnapshot.child("twoPhoneNumber")));
                twoEmail = String.valueOf(dataSnapshot.child("twoEmail"));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query customSettings = dRef.child("users").equalTo(username, "username");
        customSettings.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numWarnings = Integer.parseInt(String.valueOf(dataSnapshot.child("numWarnings")));
                timePeriod = Integer.parseInt(String.valueOf(dataSnapshot.child("timePeriod")));
                timeBetweenWarnings = Integer.parseInt(String.valueOf(dataSnapshot.child("timeBetweenWarnings")));

                //also throwing in here startTime because...why not
                startTime = String.valueOf(dataSnapshot.child("startTime"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });







    }

    protected UserInfo(Parcel in) {
        username = in.readString();
        contactOne = in.readString();
        contactTwo = in.readString();
        onePhoneNumber = in.readInt();
        twoPhoneNumber = in.readInt();
        oneEmail = in.readString();
        twoEmail = in.readString();
        numWarnings = in.readInt();
        timePeriod = in.readInt();
        timeBetweenWarnings = in.readInt();
        isCheckedIn = in.readByte();
        startTime = in.readString();

    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public int getNumWarnings() {
        return numWarnings;
    }

    public int getOnePhoneNumber() {
        return onePhoneNumber;
    }

    public int getTimeBetweenWarnings() {
        return timeBetweenWarnings;
    }

    public int getTimePeriod() {
        return timePeriod;
    }

    public int getTwoPhoneNumber() {
        return twoPhoneNumber;
    }

    public String getContactOne() {
        return contactOne;
    }

    public String getContactTwo() {
        return contactTwo;
    }

    public String getOneEmail() {
        return oneEmail;
    }

    public String getTwoEmail() {
        return twoEmail;
    }

    public String getUsername() {
        return username;
    }





    public void updateCustomSettings(int timePeriod, int numWarnings, int timeBetweenWarnings)
    {
        this.numWarnings = numWarnings;
        this.timePeriod = timePeriod;
        this.timeBetweenWarnings = timeBetweenWarnings;
    }

    public void updateEmergencyContacts(String contactOne, int onePhoneNumber, String oneEmail, String contactTwo, int twoPhoneNumber, String twoEmail)
    {
        this.contactOne = contactOne;
        this.onePhoneNumber = onePhoneNumber;
        this.oneEmail = oneEmail;

        this.contactTwo = contactTwo;
        this.twoPhoneNumber = twoPhoneNumber;
        this.twoEmail = twoEmail;
    }




    public int[] calcTimeLeft()
    {
        try
        {
            Date currentTime = new Date();
            Date temp = format.parse(startTime);

            Calendar cal = Calendar.getInstance(); // creates calendar
            cal.setTime(temp); // sets calendar time/date
            cal.add(Calendar.HOUR, timePeriod); // adds one hour
            Date startingTimeStamp = cal.getTime();




            long diff = startingTimeStamp.getTime() - currentTime.getTime();

            int diffMin = ((int) (diff / (60 * 1000))) % 60;


            int diffHours = (int) (diff / (60 * 60 * 1000));


            return new int[]{diffHours, diffMin};



        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }


        return null;
    }

    public void updateStartTime(Date date)
    {
        format.format(date);
        startTime = date.toString();
    }


    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(contactOne);
        dest.writeString(contactTwo);
        dest.writeInt(onePhoneNumber);
        dest.writeInt(twoPhoneNumber);
        dest.writeString(oneEmail);
        dest.writeString(twoEmail);
        dest.writeInt(numWarnings);
        dest.writeInt(timePeriod);
        dest.writeInt(timeBetweenWarnings);
        dest.writeInt(isCheckedIn);
        dest.writeString(startTime);
    }
}
