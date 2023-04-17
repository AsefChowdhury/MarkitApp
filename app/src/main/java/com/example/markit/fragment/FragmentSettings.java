package com.example.markit.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.markit.R;
import com.example.markit.activities.AboutUsActivity;
import com.example.markit.activities.ChangeEmailActivity;
import com.example.markit.activities.ChangeNameActivity;
import com.example.markit.activities.ChangePasswordActivity;
import com.example.markit.activities.SignInActivity;
import com.example.markit.utilities.Constants;
import com.example.markit.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.HashMap;


public class FragmentSettings extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;


    private PreferenceManager preferenceManager;

    public FragmentSettings() {
    }

    public static FragmentSettings newInstance(String param1, String param2) {
        FragmentSettings fragment = new FragmentSettings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        preferenceManager = new PreferenceManager(getContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        loadUserDetails(view);
        getToken();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setListeners();

    }
    private void setListeners(){
        AppCompatImageView imageSignOut = (AppCompatImageView) getView().findViewById(R.id.imageSignOut);
        imageSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        MaterialButton buttonChangePassword = (MaterialButton) getView().findViewById(R.id.buttonChangePassword);
        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getView().getContext(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        MaterialButton buttonChangeEmail = (MaterialButton) getView().findViewById(R.id.buttonChangeEmail);
        buttonChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getView().getContext(), ChangeEmailActivity.class);
                startActivity(intent);
            }
        });

        MaterialButton buttonChangeName = (MaterialButton) getView().findViewById(R.id.buttonChangeName);
        buttonChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getView().getContext(), ChangeNameActivity.class);
                startActivity(intent);
            }
        });

        MaterialButton buttonAboutUs = (MaterialButton) getView().findViewById(R.id.buttonAboutUs);
        buttonAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getView().getContext(), AboutUsActivity.class);
                startActivity(intent);
            }
        });
    }
    private void loadUserDetails(View view) {
        TextView textName = (TextView) view.findViewById(R.id.textName);
        RoundedImageView imageProfile = (RoundedImageView) view.findViewById(R.id.imageProfile);
        TextView textEmail = (TextView) view.findViewById(R.id.textEmail);

        String name = preferenceManager.getString(Constants.KEY_NAME);
        String email = preferenceManager.getString(Constants.KEY_EMAIL);
        String undecodedImage = preferenceManager.getString(Constants.KEY_IMAGE);
        byte[] decodedImageArr = Base64.decode(undecodedImage, Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(decodedImageArr, 0, decodedImageArr.length);

        textName.setText(name);
        textEmail.setText(email);
        imageProfile.setImageBitmap(image);

    }

    private void showToast(String message) {
        Toast.makeText(getView().getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                updateToken(s);
            }
        });
    }

    private void updateToken(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );

        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //showToast("Token updated successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Unable to update token");
                    }
                });
    }

    private void signOut(){
        showToast("Signing Out");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );

        HashMap<String, Object> data = new HashMap<>();
        data.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        preferenceManager.clear();
                        Intent intent = new Intent(getView().getContext(), SignInActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Unable to SignOut");
                    }
                });

    }
}