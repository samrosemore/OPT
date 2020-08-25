package com.ncourage.markmeok;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends Fragment
{


    private View view;
    private EditText fullName;
    private EditText email;
    private EditText phoneNumber;
    private EditText password;
    private SignUp signUp;

    FirebaseFirestore db;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.sign_up_fragment, container, false);
        signUp = this;
        db = FirebaseFirestore.getInstance();

        TextView legal = view.findViewById(R.id.legal1);
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

        Button signUpButton = (Button) view.findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                fullName = (EditText) view.findViewById(R.id.fullNameSignUp);
                password = (EditText) view.findViewById(R.id.passwordSignUp);
                phoneNumber = (EditText) view.findViewById(R.id.phoneNumSignUp);
                email = (EditText) view.findViewById(R.id.emailSignUp);

                if(Verification.checkEmail(email.getText().toString()) && Verification.checkPassword(password.getText().toString())
                        && Verification.checkPhoneNumber(phoneNumber.getText().toString()) && Verification.checkName(fullName.getText().toString()))
                {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString().toLowerCase(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            //now store some extra information in the users database
                            Map<String, Object> propertiesToAdd = new HashMap<>();
                            propertiesToAdd.put("fullName", fullName.getText().toString());
                            propertiesToAdd.put("email", email.getText().toString().toLowerCase());
                            propertiesToAdd.put("phoneNumber", phoneNumber.getText().toString());
                            propertiesToAdd.put("initialBootup", true);

                            if(task.isSuccessful() && user != null && !user.getUid().equals(""))
                            {
                                String uid = user.getUid();
                                db.collection("users").document(uid).set(propertiesToAdd).addOnCompleteListener(
                                        new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                                    fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                                                    fragmentTransaction.replace(R.id.welcome_frame, new Login());
                                                    fragmentTransaction.addToBackStack(null);
                                                    fragmentTransaction.commit();
                                                }
                                                else
                                                {
                                                    Toast.makeText(signUp.getContext(), "Sign Up Failed...Please Try Again", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                );
                            }
                            else
                            {
                                Toast.makeText(signUp.getContext(), "Sign Up Failed...Please try again", Toast.LENGTH_LONG).show();
                            }






                        }
                    });
                }
                else
                {
                    Toast.makeText(signUp.getContext(), "Please fill out all the fields correctly", Toast.LENGTH_LONG).show();
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
