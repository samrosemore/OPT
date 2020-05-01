package com.example.opt;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

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

    public UserInfo(String Json)
    {
        try
        {
            JSONObject jObject = new JSONObject(Json);
            this.username = jObject.getString("username");
            this.contactOne = jObject.getString("contactOne");
            this.contactTwo = jObject.getString("contactTwo");
            this.onePhoneNumber = jObject.getInt("onePhoneNumber");
            this.twoPhoneNumber = jObject.getInt("twoPhoneNumber");
            this.oneEmail = jObject.getString("oneEmail");
            this.twoEmail = jObject.getString("twoEmail");
            this.numWarnings = jObject.getInt("numWarnings");
            this.timePeriod = jObject.getInt("timePeriod");
            this.timeBetweenWarnings = jObject.getInt("timeBetweenWarnings");


        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }


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
    }
}
