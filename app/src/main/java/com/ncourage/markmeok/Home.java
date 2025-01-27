package com.ncourage.markmeok;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;



public class Home extends Fragment
{
    private View view;

    //these will eventually be deleted
    private Button statusChange;

    private TextView status;
    private TextView digitalTimer;



    private Handler handler;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private double startTime;
    private double timePeriod;
    private boolean shouldStartTimer;

    private Timer timer;
    private FirebaseFirestore db;


    private String uID;

    //self reference
    private Home home;
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        view = inflater.inflate(R.layout.fragment_home, container, false);


        timer = new Timer();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uID = user.getUid();




        statusChange = (Button) view.findViewById(R.id.statusChange);

        digitalTimer = (TextView) view.findViewById(R.id.digitalTimer);

        db = FirebaseFirestore.getInstance();

        //self reference
        home = this;

        db.collection("Groups").document(getArguments().getString("groupName")).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e)
            {
                if(documentSnapshot != null && documentSnapshot.exists())
                {

                    TextView groupName = view.findViewById(R.id.selectedGroupName);
                    groupName.setText(documentSnapshot.getString("groupName"));
                    startTime =  (Double) documentSnapshot.getData().get("startingTime"); //IN SECONDS
                    timePeriod =  ((Long) documentSnapshot.getData().get("timePeriod")).doubleValue() * 3600.0; //CONVERTED TO SECONDS


                    if(documentSnapshot.contains("startTimer"))
                    {
                        shouldStartTimer = (boolean) documentSnapshot.getData().get("startTimer");
                    }
                    else
                    {
                        shouldStartTimer = true;
                    }

                    if(shouldStartTimer)
                    {
                        timer.scheduleAtFixedRate(new TimerTask()
                        {

                            @Override
                            public void run()
                            {

                                home.receive(calcTimeLeft());
                            }

                        }, 0, 1000);
                    }
                    else
                    {
                        home.receive((int) (timePeriod/3600));
                    }

                }
            }
        });






















        statusChange.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                db.collection("Groups").document(getArguments().getString("groupName")).update("startingTime", new Date().getTime()/1000.0);
            }
        });



        TextView backToGroupListings = view.findViewById(R.id.backToGroupListings);
        backToGroupListings.setLinksClickable(true);

        backToGroupListings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent toGroups = new Intent(getActivity(), GroupListings.class);
                startActivity(toGroups);
            }
        });



        handler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message inputMessage)
            {
                if(inputMessage.what == 1)
                {
                    //this means startTimer == false
                    String message = inputMessage.obj + ":00";
                    digitalTimer.setText(message);
                }
                else if(inputMessage.what == 2)
                {
                    digitalTimer.setText((String) inputMessage.obj);
                }

            }
        };



        return view;
    }



    public void receive(int[] timeDimensions)
    {
        String time;

        if(timeDimensions[0] <= 0 && timeDimensions[1] <= 0)
        {
            time = "00:00";
        }
        else if(timeDimensions[1] < 10)
        {
            time = timeDimensions[0] + ":0" + timeDimensions[1];
        }
        else
        {
            time = timeDimensions[0] + ":" + timeDimensions[1];
        }

        Message message = handler.obtainMessage(2, time);
        message.sendToTarget();
    }

    public void receive(int timePeriod)
    {
        Message message = handler.obtainMessage(1, timePeriod);
        message.sendToTarget();
    }




    @Nullable
    @Override
    public View getView()
    {
        return view;
    }

    public int[] calcTimeLeft()
    {

            double currentTime = new Date().getTime() / 1000.0; //Seconds

            double timeToCompare = startTime + timePeriod; //Seconds

            double diff = timeToCompare - currentTime;

            int diffMin = ((int) (diff / (60))) % 60;


            int diffHours = (int) (diff / (60 * 60));


            return new int[]{diffHours, diffMin};


    }


}
