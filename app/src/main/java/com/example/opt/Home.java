package com.example.opt;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class Home extends Fragment
{
    private View view;
    private Button statusChange;
    private ImageView statusNotifier;
    private TextView status;
    private UserInfo userInfo;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        final Resources res = getResources();

        //user info backup
        this.userInfo = getArguments().getParcelable("userInfo");


        statusChange = (Button) view.findViewById(R.id.statusChange);
        statusNotifier = (ImageView) view.findViewById(R.id.imageView);
        status = (TextView) view.findViewById(R.id.status);

        statusChange.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(statusChange.getText() == res.getString(R.string.enableQuestion))
                {
                    /*
                        do some stuff to the sql database
                     */
                    statusNotifier.setImageResource(R.drawable.check);
                    status.setText(res.getString(R.string.enabledText));


                }
                //else its to disable
                else
                {
                    /*
                        do some stuff to the sql database
                     */
                    statusNotifier.setImageResource(R.drawable.x);
                    status.setText(res.getString(R.string.disabledText));
                    statusChange.setText(res.getString(R.string.enableQuestion));
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
