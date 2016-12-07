package com.greenmagics.voip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipSession;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.text.ParseException;

public class Call_Activity extends AppCompatActivity {

    String number ;
    String mynumber ;
    SipManager sipManager ;
    SipProfile sipProfile;
    SipProfile profilepeer;
    SipAudioCall call ;
    MyReceiver receiver ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_);

       // startService(new Intent(getBaseContext(),ChatService.class));

        final String conference_mark =       getIntent().getStringExtra("conference");


        try {
            number = getIntent().getStringExtra("number");

            Log.d("number",number);

            SharedPreferences prefs = getSharedPreferences("registeration", 0);
            String openfiredomain = prefs.getString("openfiredomain", "");
            String sipdomain = prefs.getString("sipdomain", "");
            mynumber = prefs.getString("sipname", "");
            String password = prefs.getString("sippass", "");


            if (sipManager == null) {
                sipManager = SipManager.newInstance(getBaseContext());
            }


            IntentFilter filter = new IntentFilter();
            filter.addAction("com.greenmagics.smartvoip.SipCallReceiver");
            receiver = new MyReceiver();
            registerReceiver(receiver,filter);



            SipProfile.Builder builder = null;

            try {
                builder = new SipProfile.Builder(mynumber, sipdomain);

            } catch (ParseException e) {
                Log.d("error profile", e.getMessage());
            }

            //builder.setProfileName("myprofile");
            builder.setPort(5060);
            builder.setProtocol("UDP");
            builder.setPassword(password);


            //builder.setSendKeepAlive(true);
            sipProfile = builder.build();

            SipProfile.Builder builder1 = null;

            try {
                builder1 = new SipProfile.Builder(number, sipdomain);
            } catch (ParseException e) {
                Log.d("error profile", e.getMessage());
            }

            profilepeer = builder1.build();

            new Thread(new Runnable() {
                @Override
                public void run() {


                    try {


                        call = sipManager.makeAudioCall(sipProfile, profilepeer, new SipAudioCall.Listener()

                        {

                            @Override
                            public void onError(SipAudioCall call, int errorCode, String errorMessage) {
                                Log.d("call error", errorMessage);
                            }

                            @Override
                            public void onCallEstablished(SipAudioCall call) {
                                call.startAudio();
                            }
                        }, 400);


                    } catch (Exception e) {

                    }


                }
            }).start();
        }
        catch (Exception e)
        {

        }


        ImageButton endCall = (ImageButton)findViewById(R.id.imageButton6);
        ImageButton mute = (ImageButton)findViewById(R.id.imageButton5);
        ImageButton hold = (ImageButton)findViewById(R.id.imageButton4);




        endCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                    try {
                        call.endCall();
                        
                        call.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }




                try {
                    MyReceiver.call.endCall();

                } catch (Exception e) {
                    e.printStackTrace();
                }


                    Intent i = new Intent(getBaseContext(), UsersActivity.class);
                    startActivity(i);




            }
        });


        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    call.toggleMute();
                }
                catch (Exception e)
                {

                }

                try {
                    MyReceiver.call.toggleMute();
                }
                catch (Exception e)
                {

                }




            }
        });

        hold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(call.isOnHold())
                    {
                        call.continueCall(0);
                    }
                    else {
                        call.holdCall(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if(MyReceiver.call.isOnHold())
                    {
                        MyReceiver.call.continueCall(0);
                    }
                    else {
                        MyReceiver.call.holdCall(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }



            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Thread(new Runnable() {
            @Override
            public void run() {
                unregisterReceiver(receiver);
               // stopService(new Intent(getBaseContext(),ChatService.class));
            }
        }).start();

    }
}
