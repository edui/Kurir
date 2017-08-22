package id.co.kurindo.kurindo.firebase;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.co.kurindo.kurindo.LoginActivity;
import id.co.kurindo.kurindo.MainDrawerActivity;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.TOrderShowActivity;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.notification.CompletedOrderPopupActivity;
import id.co.kurindo.kurindo.notification.FullscreenNotificationActivity;
import id.co.kurindo.kurindo.notification.KurirCompletedOrderPopupActivity;
import id.co.kurindo.kurindo.notification.KurirNewOrderPopupActivity;
import id.co.kurindo.kurindo.notification.NewOrderPopupActivity;
import id.co.kurindo.kurindo.util.LogUtil;

/**
 * Created by dwim on 12/30/2016.
 */

public class KurindoFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "KurindoFirebaseMessagingService";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);

        String action = intent.getAction();
        if(intent.getExtras() != null && intent.getExtras().get("kurindo") != null) {
            String strNotificaiton = intent.getExtras().get("gcm.notification.title").toString();
            String strKurindo= intent.getExtras().get("kurindo").toString();
            String strMessage= intent.getExtras().get("message").toString();

            Map<String, String> data = new HashMap<>();
            //data.put("kurindo", "{'action':'action', 'code':'INFO', 'awb':'awb','phone':'phone'}");
            data.put("kurindo", strKurindo);

            sendNotificationAsActivity(strMessage, data);
        }
        LogUtil.logD("onHandleIntent","Received notification action: " + action);
        /*if (ACTION_1.equals(action)) {
                // TODO: handle action 1.
                // If you want to cancel the notification: NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
         }*/
    }


    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @SuppressLint("LongLogTag")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        LogUtil.logD(TAG, "From: " + remoteMessage.getFrom());

        Map<String, String> data = null;
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            LogUtil.logD(TAG, "Message data payload: " + remoteMessage.getData());

            data = remoteMessage.getData();

        }

        // Check if message contains a notification payload.
        RemoteMessage.Notification notif = remoteMessage.getNotification();
        if (notif != null) {
            LogUtil.logD(TAG, "Message Notification Body: " + notif.getBody());
        }

        String messageBody = (notif != null ? notif.getBody() : "MessageBody");

        if(data != null){
            // Also if you intend on generating your own notifications as a result of a received FCM
            // message, here is where that should be initiated. See sendNotification method below.
            //fullscreenNotification(messageBody, data);
            //if(AppController.isActivityVisible()){
            sendNotification(messageBody, data);
            //}else{
            //}

            //sendNotificationBigStyle(messageBody);
        }else{
            sendNotificationBigStyle(messageBody);
        }
    }

    private void sendNotificationAsActivity(String messageBody, Map<String, String> data) {
        Intent intent =new Intent("android.intent.category.LAUNCHER");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                | Intent.FLAG_ACTIVITY_NO_USER_ACTION);

        id.co.kurindo.kurindo.model.Notification dataNotification = new id.co.kurindo.kurindo.model.Notification();
        String data1 = data.get("kurindo");
        String status = "INFO";
        try {
            JSONObject obj = new JSONObject(data1);
            if (obj != null) {

                String actionData = obj.isNull("action") ? "":obj.getString("action");
                String awb = obj.isNull("awb") ? "":obj.getString("awb");
                String code = obj.isNull("code") ? "":obj.getString("code");
                String kotaPengirim = obj.isNull("kota_pengirim") ? "":obj.getString("kota_pengirim");
                String kotaPenerima = obj.isNull("kota_penerima") ? "":obj.getString("kota_penerima");
                String tag = obj.isNull("tag") ? "":obj.getString("tag");
                String price= obj.isNull("price") ? "":obj.getString("price");
                status = obj.isNull("type") ? "":obj.getString("type");
                dataNotification.setAction(actionData);
                dataNotification.setAwb(awb);
                dataNotification.setStatus(status);
                dataNotification.setTag(tag);
                dataNotification.setPrice(price);
                dataNotification.setKotaPengirim(kotaPengirim);
                dataNotification.setKotaPenerima(kotaPenerima);
                dataNotification.setMessage(messageBody);

            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        intent.putExtra("notification", dataNotification);
        if(status.equalsIgnoreCase(AppConfig.KEY_KUR100) || status.equalsIgnoreCase("NEW")) {
            if (AppController.session.isKurir()) {
                intent.setClass(AppController.applicationContext, KurirNewOrderPopupActivity.class);
                //intent.setClass(AppController.applicationContext, NewOrderPopupActivity.class);
            }else if (AppController.session.isAdministrator()) {
                intent.setClass(AppController.applicationContext, FullscreenNotificationActivity.class);
            }
        }else  if(status.equalsIgnoreCase("ASSIGNED") || status.equalsIgnoreCase(AppConfig.KEY_KUR101) || status.equalsIgnoreCase(AppConfig.KEY_KUR200)  ){
            //if (AppController.session.isPelanggan()) {
                intent.putExtra("awb", dataNotification.getAwb());
                intent.putExtra("load", true);
                intent.putExtra("reset", true);
                intent.setClass(AppController.applicationContext, TOrderShowActivity.class);
            /*}else{
                intent.setClass(AppController.applicationContext, FullscreenNotificationActivity.class);
            }*/
        }else  if(status.equalsIgnoreCase(AppConfig.KEY_KUR500)){
            if (AppController.session.isPelanggan()) {
                intent.setClass(AppController.applicationContext, CompletedOrderPopupActivity.class);
            } else {
                intent.setClass(AppController.applicationContext, FullscreenNotificationActivity.class);
                //intent.setClass(AppController.applicationContext, KurirCompletedOrderPopupActivity.class);
            }
        }else{
            intent.setClass(AppController.applicationContext, FullscreenNotificationActivity.class);
        }
        //getApplicationContext().startActivity(intent);
        PendingIntent i2 = PendingIntent.getActivity(AppController.applicationContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        try {
            i2.send(AppController.applicationContext, 0, intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody, Map<String, String> data) {
        Intent intent = new Intent(this, MainDrawerActivity.class);
        String tag = "";
        if(data != null && data.size() > 0){
            String data1 = data.get("kurindo");
            try {
                JSONObject obj = new JSONObject(data1);
                if(obj != null){

                    String action = obj.getString("action");
                    if(action != null && action.equalsIgnoreCase("detil_pesanan")) {
                        intent = new Intent(this, TOrderShowActivity.class);
                        String awb = obj.getString("awb");
                        intent.putExtra("awb", awb);
                        intent.putExtra("load", true);
                    }else if(action != null && action.equalsIgnoreCase("activation")){
                        intent = new Intent(this, LoginActivity.class);
                        intent.putExtra("activation", true);
                        String code = obj.getString("code");
                        intent.putExtra("code", code);
                        String phone = obj.getString("phone");
                        intent.putExtra("phone", phone);
                    }
                    intent.setAction(action);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("KURINDO "+tag)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
    private void fullscreenNotification(String messageBody, Map<String, String> data){
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
        .setSmallIcon(android.R.drawable.btn_star)
        .setContentTitle("KURINDO")
        .setContentText(""+messageBody)
        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
        .setAutoCancel(true)
        .setSound(defaultSoundUri)
        .setPriority(Notification.PRIORITY_HIGH);

        Intent intent = new Intent(this, FullscreenNotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 113,intent, PendingIntent.FLAG_UPDATE_CURRENT);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        builder.setContentIntent(pendingIntent);
        builder.setFullScreenIntent(pendingIntent, true);
        builder.addAction(R.drawable.reject_booking_icon, "Reject", pendingIntent);
        builder.addAction(R.drawable.accept_booking_icon, "Accept", pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    private void sendNotificationBigStyle(String messageBody) {
        Intent intent = new Intent(this, MainDrawerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("KURINDO ")
                .setContentText(messageBody)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
    private void sendNotificationGroup(String messageBody) {
        Intent intent = new Intent(this, MainDrawerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Push Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        Notification notification = new NotificationCompat.InboxStyle(notificationBuilder)
                .addLine("First Message")
                .addLine("Second Message")
                .addLine("Third Message")
                .addLine("Fourth Message")
                .setBigContentTitle("Here Your Messages")
                .setSummaryText("+3 more")
                .build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(getApplicationContext(), KurindoFirebaseMessagingService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 2000, pendingIntent);
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent intent = new Intent(getApplicationContext(), KurindoFirebaseMessagingService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 2000, pendingIntent);
        super.onTaskRemoved(rootIntent);
    }
}
