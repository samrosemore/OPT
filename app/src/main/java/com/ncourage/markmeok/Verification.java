package com.ncourage.markmeok;

public class Verification
{
    //for every method...will return true if it passes the test
    public static boolean checkEmail(String email)
    {
        return email.contains("@");
    }
    public static boolean checkPassword(String password)
    {
        return (!password.isEmpty() && (password.length() >= 6));
    }
    public static boolean checkName(String name)
    {
        return !name.isEmpty();
    }


    public static boolean checkPhoneNumber(String strPhoneNumber)
    {
        return !(strPhoneNumber.matches("^[a-zA-Z]*$"));
    }
    public static boolean checkNumber(String strPhoneNumber)
    {
        return !(strPhoneNumber.matches("^[a-zA-Z]*$"));
    }
}
