package com.example.markit.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.markit.R;
import com.example.markit.activities.ChatActivity;
import com.example.markit.activities.UserActivity;
import com.example.markit.adapters.RecentConversationsAdapter;
import com.example.markit.listeners.ConversionListener;
import com.example.markit.models.ChatMessage;
import com.example.markit.models.User;
import com.example.markit.utilities.Constants;
import com.example.markit.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentMessage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMessage extends Fragment implements ConversionListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private PreferenceManager preferenceManager;
    private List<ChatMessage> conversations;
    private RecentConversationsAdapter conversationsAdapter;
    private FirebaseFirestore database;


    public FragmentMessage() {
    }

    public static FragmentMessage newInstance(String param1, String param2) {
        FragmentMessage fragment = new FragmentMessage();
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
        View view =  inflater.inflate(R.layout.fragment_message, container, false);
        loadUserDetails(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
        setListeners();
        listenConversation();

    }

    public void init() {
        conversations = new ArrayList<>();
        conversationsAdapter = new RecentConversationsAdapter(conversations, this);
        RecyclerView conversationRecyclerView = (RecyclerView) getView().findViewById(R.id.conversationsRecyclerView);
        conversationRecyclerView.setAdapter(conversationsAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void setListeners(){
        FloatingActionButton fabNewChat = (FloatingActionButton) getView().findViewById(R.id.fabNewChat);
        fabNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getView().getContext(), UserActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getView().getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void listenConversation() {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
            if (error != null) {
                return;
            }
            if (value != null) {
                for (DocumentChange documentChange : value.getDocumentChanges()) {
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String recieverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.senderId = senderId;
                        chatMessage.receiverId = recieverId;
                        if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {
                            chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_RECIEVER_IMAGE);
                            chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                            chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        } else {
                            chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                            chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                            chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        }
                        chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                        chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                        conversations.add(chatMessage);
                    } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                        for (int i = 0; i < conversations.size(); i++) {
                            String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                            String recieverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);

                            if (conversations.get(i).senderId.equals(senderId) && conversations.get(i).receiverId.equals(recieverId)) {
                                conversations.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                                conversations.get(i).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                                break;
                            }

                        }
                    }
                }
                Collections.sort(conversations, (mess1, mess2) -> mess2.dateObject.compareTo(mess1.dateObject));
                conversationsAdapter.notifyDataSetChanged();
                RecyclerView conversationsRecyclerView = (RecyclerView) getView().findViewById(R.id.conversationsRecyclerView);
                ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
                conversationsRecyclerView.smoothScrollToPosition(0);
                conversationsRecyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

            }
        }
    };

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
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        showToast("Token updated successfully");
//                    }
//                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Unable to update token");
                    }
                });
    }

    private void loadUserDetails(View view) {
        TextView textName = (TextView) view.findViewById(R.id.textName);
        RoundedImageView imageProfile = (RoundedImageView) view.findViewById(R.id.imageProfile);

        String name = preferenceManager.getString(Constants.KEY_NAME);
        String undecodedImage = preferenceManager.getString(Constants.KEY_IMAGE);
        byte[] decodedImageArr = Base64.decode(undecodedImage, Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(decodedImageArr, 0, decodedImageArr.length);

        textName.setText(name);
        imageProfile.setImageBitmap(image);

    }

    @Override
    public void onConversionListener(User user) {
        Intent intent = new Intent(getActivity().getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }
}