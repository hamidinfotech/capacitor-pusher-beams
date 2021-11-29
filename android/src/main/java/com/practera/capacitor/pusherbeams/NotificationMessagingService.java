package com.practera.capacitor.pusherbeams;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.RemoteMessage;
import com.pusher.pushnotifications.fcm.MessagingService;

public abstract class NotificationMessagingService extends MessagingService {

    @Override
    public abstract void onMessageReceived(@NonNull RemoteMessage remoteMessage);
}
