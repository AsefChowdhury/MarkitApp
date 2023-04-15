package com.example.markit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.markit.R;
import com.example.markit.databinding.ActivityChangePasswordBinding;
import com.example.markit.utilities.Constants;
import com.example.markit.utilities.PerferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {

    ActivityChangePasswordBinding binding;
    PerferenceManager perferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        perferenceManager = new PerferenceManager(getApplicationContext());
        setListeners();


    }

    private void setListeners() {
        binding.buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();

                String newPassword = binding.editNewPassword.getText().toString();
                String confirmPassword = binding.editConfirmPassword.getText().toString();

                if (checkPassword(newPassword, confirmPassword)) {
                    changePassword(newPassword);
                } else {
                    binding.editNewPassword.setText(null);
                    binding.editConfirmPassword.setText(null);
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

    private void changePassword(String newPassword) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String userId = perferenceManager.getString(Constants.KEY_USER_ID);
        DocumentReference userRef = database.collection(Constants.KEY_COLLECTION_USERS).document(userId);

        Map<String, Object> data = new HashMap<>();
        data.put(Constants.KEY_PASSWORD, newPassword);

        userRef.update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        showToast("Succefully changed password");
                        signOut();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Couldnt change password");
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

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private Boolean checkPassword(String pass1, String pass2) {
        if (pass1.equals(pass2)) {
            return true;
        }
        showToast("Passwords does not match!!!");
        return false;
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
}