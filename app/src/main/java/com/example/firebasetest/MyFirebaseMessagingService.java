package com.example.firebasetest;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService{
    @Override
    public void onNewToken(@NonNull String token) {
//        Log.e("TAG", "onNewToken: "+token );
        super.onNewToken(token);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.e("TAG", "From: :"+message.getFrom());
        if(!message.getData().isEmpty()){
            Log.e("TAG", "Message data payload: "+message.getData());
        }
        // Check if message contains a notification payload.
        if (message.getNotification()!=null){
            Log.e("TAG", "Message Notification Body:"+ message.getNotification().getBody());
        }
    }

    @Override
    public void onDeletedMessages() {
        Log.e("TAG", "onNewToken:onDeletedMessages " );
        super.onDeletedMessages();

    }
}
