package com.example.markit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.markit.R;
import com.example.markit.databinding.ActivityChangeEmailBinding;
import com.example.markit.utilities.Constants;
import com.example.markit.utilities.PerferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ChangeEmailActivity extends AppCompatActivity {

    ActivityChangeEmailBinding binding;
    PerferenceManager perferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        perferenceManager = new PerferenceManager(getApplicationContext());

        setListeners();


    }

    private void setListeners() {
        binding.buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.editNewEmail.getText().toString();
                String newEmail = binding.editConfirmEmail.getText().toString();

                if (checkEmail(email, newEmail)) {
                    changeEmail(newEmail);
                } else {
                    binding.editConfirmEmail.setText(null);
                    binding.editNewEmail.setText(null);
                    closeKeyboard();
                }

            }
        });

        binding.frameBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private boolean checkEmail(String email1, String email2) {
        if (!email1.toLowerCase().trim().equals(email2.toLowerCase().trim())) {
            showToast("Email does not match!!");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email1).matches()) {
            showToast("Enter valid email!!");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email2).matches()) {
            showToast("Enter valid email!!");
            return false;
        }

        return true;
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void changeEmail(String newEmail) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String userId = perferenceManager.getString(Constants.KEY_USER_ID).toString();
        DocumentReference userRef = database.collection(Constants.KEY_COLLECTION_USERS).document(userId);

        Map<String, Object> data = new HashMap<>();
        data.put(Constants.KEY_EMAIL, newEmail);

        userRef.update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        showToast("Updated email successfully");
                        signOut();
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Couldn't change email");
                        binding.editConfirmEmail.setText(null);
                        binding.editNewEmail.setText(null);
                    }
                });

    }

    private void signOut(){
        showToast("Signing Out");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                perferenceManager.getString(Constants.KEY_USER_ID)
        );

        HashMap<String, Object> data = new HashMap<>();
        data.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        perferenceManager.clear();
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Unable to SignOut");
                    }
                });

    }

    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}