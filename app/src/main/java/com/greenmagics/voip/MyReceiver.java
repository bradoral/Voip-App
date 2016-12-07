package com.greenmagics.voip;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.util.Log;

import java.io.IOException;

public class MyReceiver extends BroadcastReceiver {
    SipManager sipManager;
    static SipAudioCall call;
    MediaPlayer mediaPlayer ;
    MainActivity mainActivity = new MainActivity();
    public MyReceiver() {
    }

    @Override
    public void onReceive(final Context context,final Intent intent) {






       mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (SecurityException e1) {
            e1.printStackTrace();
        } catch (IllegalStateException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        new AlertDialog.Builder(context).setTitle("Incoming Call")
        .setMessage("Incoming Call")
        .setPositiveButton("Accept",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {



                try {

                    if(mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }

                    if (sipManager == null) {
                        sipManager = SipManager.newInstance(context);
                    }


                    SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                        @Override
                        public void onRinging(SipAudioCall call, final SipProfile caller) {
                            try {




                                call.answerCall(0);
                                call.startAudio();















                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    call = sipManager.takeAudioCall(intent, listener);

                    call.answerCall(30);

                    call.startAudio();
                    call.setSpeakerMode(true);
                    if(call.isMuted()) {
                        call.toggleMute();
                    }


                    Intent i = new Intent(context,Call_Activity.class);
                    context.startActivity(i);


                } catch (Exception e) {

                    if (call != null) {
                        call.close();
                    }
                }



            }
        }).setNegativeButton("Refuse",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                try {

                    if(mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    if (sipManager == null) {
                        sipManager = SipManager.newInstance(context);
                    }


                    SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                        @Override
                        public void onRinging(SipAudioCall call, final SipProfile caller) {
                            try {


















                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    call = sipManager.takeAudioCall(intent, listener);

                    call.endCall();


                } catch (Exception e) {

                    if (call != null) {
                        call.close();
                    }
                }
            }
        }).show();



    }
    }

