package com.example.opt;

import android.content.Intent;
import android.content.res.Resources;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    private Timer timer;
    private FirebaseFirestore db;


    private String uID;

    //self reference
    private Home home;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_home, container, false);


        timer = new Timer();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uID = user.getUid();




        statusChange = (Button) view.findViewById(R.id.statusChange);
        status = (TextView) view.findViewById(R.id.status);
        digitalTimer = (TextView) view.findViewById(R.id.digitalTimer);

        db = FirebaseFirestore.getInstance();

        db.collection("users").document(uID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e)
            {
                if(documentSnapshot != null && documentSnapshot.exists())
                {

                    startTime =  (Double) documentSnapshot.getData().get("startTime"); //IN SECONDS
                    timePeriod =  ((Long) documentSnapshot.getData().get("timePeriod")).doubleValue() * 3600.0; //CONVERTED TO SECONDS

                    timer.scheduleAtFixedRate(new TimerTask()
                    {

                        @Override
                        public void run()
                        {

                            home.receive(calcTimeLeft());
                        }

                    }, 0, 1000);
                }
            }
        });



                //self reference
                home = this;

















        statusChange.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


                db.collection("users").document(uID).update("startTime", new Date().getTime()/1000.0);
            }
        });

        TextView signOutOption = view.findViewById(R.id.signOutOption);
        signOutOption.setLinksClickable(true);

        signOutOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent toLogin = new Intent(getActivity(), MainActivity.class);
                startActivity(toLogin);

            }
        });



        handler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message inputMessage)
            {
                if(inputMessage.what == 1)
                {
                    //status.setText(res.getString(R.string.enabledText));
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
        String time = timeDimensions[0] + ":" + timeDimensions[1];
        Message message = handler.obtainMessage(2, time);
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
