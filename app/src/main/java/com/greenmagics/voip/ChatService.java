package com.greenmagics.voip;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.util.StringUtils;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

public class ChatService extends Service {
    MainActivity mainActivity = new MainActivity();



    String body ="";
    NotificationManager mNotificationManager ;
    public ChatService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        StanzaFilter filter = new StanzaFilter()
        {


            @Override
            public boolean accept(Stanza stanza) {
                return true;
            }
        };


        MainActivity.connection.addAsyncStanzaListener(new StanzaListener() {
            @Override
            public void processPacket(org.jivesoftware.smack.packet.Stanza stanza) throws SmackException.NotConnectedException, InterruptedException {









                try {




                    Message msg = (Message) stanza;

                    body = msg.getBody();

                    if(body != null) {


                        String from = msg.getFrom().asBareJid().intern();

                        String groupChatFrom  = null;
                        if(from != null && from.contains("conference")) {
                            try {

                                groupChatFrom = msg.getFrom().getResourceOrNull().intern();
                                Log.d("groupchat", groupChatFrom);
                            } catch (Exception e) {

                            }
                        }

                        from = from.substring(0,from.lastIndexOf("@"));

                        //sendNotification(body);
                        Log.d("stringservice", body);
                        Intent i = new Intent(getBaseContext(),Chat_Activity.class);

                        try {
                            if (groupChatFrom != null && groupChatFrom.length() > 1) {
                                i.putExtra("Incomingmessagegroup", groupChatFrom + " : " + body);
                                i.putExtra("groupchatfrom","groupchatfrom");


                            } else

                            {
                                i.putExtra("Incomingmessage", from + " : " + body);

                            }
                        }
                        catch (Exception e)
                        {

                        }
                        i.putExtra("from",from);





                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);




                        startActivity(i);



                    }






                }
                catch (Exception e)
                {


                    Log.d("error",e.getMessage());
                }





            }
        },filter);











        return START_NOT_STICKY ;
    }


    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        Uri ur = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        // .setSmallIcon(R.drawable.ic_stat_gcm)
                        .setContentTitle("Incoming Message")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setSound(ur)
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(0, mBuilder.build());
    }
}
