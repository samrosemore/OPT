package com.example.opt;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class Welcome extends Fragment
{

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_welcome, container, false);

        Button login = (Button) view.findViewById(R.id.loginWelcomeScreen);
        Button signUp = (Button) view.findViewById(R.id.signUpWelcomeScreen);

        login.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                fragmentTransaction.replace(R.id.frame_layout, new Login());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        signUp.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v)
            {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                fragmentTransaction.replace(R.id.frame_layout, new Login());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
    @Nullable
    @Override
    public View getView()
    {
        return view;
    }
}
