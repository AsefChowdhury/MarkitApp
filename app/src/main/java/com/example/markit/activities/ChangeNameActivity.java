package com.example.markit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.markit.databinding.ActivityChangeNameBinding;
import com.example.markit.utilities.Constants;
import com.example.markit.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ChangeNameActivity extends AppCompatActivity {

    ActivityChangeNameBinding binding;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeNameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        setListeners();
    }

    private void setListeners() {
        binding.frameBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.editNewName.getText().toString();
                String newName = binding.editConfirmName.getText().toString();

                if (checkName(name, newName)) {
                    changeNameInCovo(name);
                    changeName(name);

                } else {
                    binding.editNewName.setText(null);
                    binding.editConfirmName.setText(null);
                    closeKeyboard();
                }
            }
        });
    }

    private void changeName(String name) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);
        DocumentReference userRef = database.collection(Constants.KEY_COLLECTION_USERS).document(userId);

        Map<String, Object> data = new HashMap<>();
        data.put(Constants.KEY_NAME, name);

        userRef.update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        showToast("Name changed Succefully");
                        signOut();
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Couldn't change name");
                        binding.editNewName.setText(null);
                        binding.editConfirmName.setText(null);
                        closeKeyboard();
                    }
                });
    }

    private void changeNameInCovo(String name) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);

        // Query all conversations that include the user
        Query conversationQuery = db.collection("conversations")
                .whereEqualTo("senderId", userId);


        conversationQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot conversation : task.getResult()) {
                        // Update the conversation document with new user information
                        String conversationId = conversation.getId();
                        Map<String, Object> updateData = new HashMap<>();
                        if (conversation.getString("senderId").equals(userId)) {
                            // User is the sender
                            updateData.put("senderName", name);

                        } else {
                            // User is the receiver
                            updateData.put("receiverName", name);

                        }
                        db.collection("conversations").document(conversationId).update(updateData);
                    }
                } else {
                    Log.d("Firebase", "Error getting conversations: ", task.getException());
                }
            }
        });


        // Query all conversations that include the user
        conversationQuery = db.collection("conversations")
                .whereEqualTo("recieverId", userId);


        conversationQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot conversation : task.getResult()) {
                        // Update the conversation document with new user information
                        String conversationId = conversation.getId();
                        Map<String, Object> updateData = new HashMap<>();
                        if (conversation.getString("senderId").equals(userId)) {
                            updateData.put("senderName", name);

                        } else {
                            // User is the receiver
                            updateData.put("receiverName", name);

                        }
                        db.collection("conversations").document(conversationId).update(updateData);
                    }
                } else {
                    Log.d("Firebase", "Error getting conversations: ", task.getException());
                }
            }
        });

    }

    private boolean checkName(String name, String newName) {
        if (name.equals(newName)) {
            return true;
        }
        showToast("Names don't match");
        return false;
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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