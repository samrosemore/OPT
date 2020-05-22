package com.example.opt;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class Login extends Fragment
{

    private View view;
    private TextView toSignUp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_welcome, container, false);

        toSignUp = (TextView) view.findViewById(R.id.toSignUpPage);
        toSignUp.setLinksClickable(true);

        toSignUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

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
