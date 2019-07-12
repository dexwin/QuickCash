//package com.acesdatasystems.quickcash.model;
//
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.support.v4.app.NotificationCompat;
//import android.util.Log;
//
//import com.acesdatasystems.quickcash.HomeActivity;
//import com.acesdatasystems.quickcash.R;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
///**
// * Created by Sheriff on 11/19/2018.
// */
//
//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        // ...
//
//        // TODO(developer): Handle FCM messages here.
//        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        Log.d("NOTIFICATION", "From: " + remoteMessage.getFrom());
//
//
//        Intent intent = new Intent(this, HomeActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
//        notificationBuilder.setContentTitle(remoteMessage.getNotification().getTitle());
//        notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
//        notificationBuilder.setAutoCancel(false);
//        notificationBuilder.setSmallIcon(R.drawable.qclogo_small);
//        notificationBuilder.setContentIntent(pendingIntent);
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0,notificationBuilder.build());
//        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.d("NOTIFICATION", "Message data payload: " + remoteMessage.getData());
//
//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
////                scheduleJob();
//            } else {
//                // Handle message within 10 seconds
////                handleNow();
//            }
//
//        }
//
//        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.d("NOTIFICATION", "Message Notification Body: " + remoteMessage.getNotification().getBody());
//        }
//
//        // Also if you intend on generating your own notifications as a result of a received FCM
//        // message, here is where that should be initiated. See sendNotification method below.
//    }
//
//
//
//
//
//}
