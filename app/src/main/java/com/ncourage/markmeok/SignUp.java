package com.ncourage.markmeok;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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

    private EditText username;
    private View view;
    private EditText fullName;
    private EditText email;
    private EditText phoneNumber;

    FirebaseFirestore db;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.sign_up_fragment, container, false);

        db = FirebaseFirestore.getInstance();

        Button signUpButton = (Button) view.findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                fullName = (EditText) view.findViewById(R.id.fullNameSignUp);
                username = (EditText) view.findViewById(R.id.usernameSignUp);
                EditText password = (EditText) view.findViewById(R.id.passwordSignUp);
                phoneNumber = (EditText) view.findViewById(R.id.phoneNumSignUp);
                email = (EditText) view.findViewById(R.id.emailSignUp);

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        //now store some extra information in the users database
                        Map<String, Object> propertiesToAdd = new HashMap<>();
                        propertiesToAdd.put("fullName", fullName.getText().toString());
                        propertiesToAdd.put("username", username.getText().toString());
                        propertiesToAdd.put("email", email.getText().toString());
                        propertiesToAdd.put("phoneNumber", phoneNumber.getText().toString());

                        db.collection("users").document(FirebaseAuth.getInstance().getUid()).set(propertiesToAdd);


                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                        fragmentTransaction.replace(R.id.welcome_frame, new Login());
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

                    }
                });


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
