package com.ncourage.markmeok;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Login extends Fragment
{

    private View view;
    private TextView toSignUp;
    private Login login;

    private LoadingThread loadingThread;

    private ImageView loadingIcon;
    private FrameLayout frameLayout;
    private LinearLayout linearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.login_fragment, container, false);

        toSignUp = (TextView) view.findViewById(R.id.toSignUpPage);
        toSignUp.setLinksClickable(true);
        login = this;

        linearLayout = (LinearLayout) view.findViewById(R.id.loginLinear);
        frameLayout = (FrameLayout) view.findViewById(R.id.progressBarHolder);
        loadingIcon = (ImageView) view.findViewById(R.id.loadingIcon);

        loadingThread = new LoadingThread(loadingIcon, frameLayout, linearLayout);

        toSignUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                fragmentTransaction.replace(R.id.welcome_frame, new SignUp());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        TextView legal = view.findViewById(R.id.legal);
        SpannableString span = new SpannableString(legal.getText());

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/"));
                startActivity(intent);
            }
        };

        ClickableSpan clickableSpan2 = new ClickableSpan()
        {
            @Override
            public void onClick(@NonNull View widget)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/"));
                startActivity(intent);

            }
        };
        //By Logging In You Agree to the Terms of Agreement and Privacy Policy
        span.setSpan(clickableSpan1, 26, 49, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(clickableSpan2, 54, 67, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        legal.setText(span);
        legal.setMovementMethod(LinkMovementMethod.getInstance());

        Button loginBtn = (Button) view.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //start loading icon and stop it later


                //other information
                EditText email = (EditText) view.findViewById(R.id.emailLogin1);
                EditText password = (EditText) view.findViewById(R.id.passwordLogin1);
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                if(email.getText().toString().equals("")  || password.getText().toString().equals(""))
                {
                    Toast.makeText(login.getContext(), "authentication failed", Toast.LENGTH_LONG).show();

                }
                else
                {
                    //loadingThread.start();
                    AlphaAnimation animation1 = new AlphaAnimation(1.0f, 0.3f);

                    animation1.setDuration(1000);


                    linearLayout.setAnimation(animation1);

                    frameLayout.setVisibility(View.VISIBLE);
                    final AnimationDrawable animation = (AnimationDrawable) loadingIcon.getDrawable();
                    animation.start();

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString().toLowerCase(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        // Sign in success, update UI with the signed-in user's information

                                        final FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();
                                        linearLayout.setAlpha(0.3f);
                                        FirebaseInstanceId.getInstance().getInstanceId()
                                                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                        if (!task.isSuccessful())
                                                        {
                                                            //loadingThread.stop();
                                                            Toast.makeText(login.getContext(), "Authentication Error Code 101",
                                                                    Toast.LENGTH_SHORT).show();
                                                            animation.stop();
                                                            linearLayout.setAlpha((float) 1.0);
                                                            frameLayout.setVisibility(View.INVISIBLE);

                                                        }
                                                        else
                                                        {
                                                            // Get new Instance ID token
                                                            String token = task.getResult().getToken();

                                                            //adding to firecloud database
                                                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                                                            Map<String, String> propertiesToAdd = new HashMap<>();
                                                            propertiesToAdd.put("rt", token);
                                                            //this will happen in the background
                                                            db.collection("users").document(user.getUid()).set(propertiesToAdd, SetOptions.merge());

                                                            db.collection("users").document(user.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>()
                                                            {
                                                                @Override
                                                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error)
                                                                {
                                                                    if(documentSnapshot != null && documentSnapshot.exists() && error == null)
                                                                    {
                                                                        //if database contains a default group...we wanna send the user straight there
                                                                        if(documentSnapshot.contains("defaultGroup"))
                                                                        {
                                                                            String docID = documentSnapshot.getString("defaultGroup");
                                                                            Intent toSelectedGroup = new Intent(login.getContext(), MainActivity.class);
                                                                            toSelectedGroup.putExtra("groupName", docID);
                                                                            startActivity(toSelectedGroup);
                                                                        }
                                                                        else
                                                                        {
                                                                            Intent toHomeScreen = new Intent(login.getActivity(), GroupListings.class);
                                                                            startActivity(toHomeScreen);
                                                                        }
                                                                    }


                                                                }
                                                            });
                                                        }


                                                    }
                                                });


                                    }
                                    else
                                    {
                                        // If sign in fails, display a message to the user.
                                        //loadingThread.stop();
                                        Toast.makeText(login.getContext(), "Authentication failed.",
                                                Toast.LENGTH_LONG).show();

                                        animation.stop();
                                        linearLayout.setAlpha((float) 1.0);
                                        frameLayout.setVisibility(View.INVISIBLE);
                                    }


                                }
                            }).addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    System.out.println(Arrays.toString(e.getStackTrace()));
                                }
                            });
                }


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
