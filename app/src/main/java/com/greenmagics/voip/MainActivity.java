package com.greenmagics.voip;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityJid;

import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    DomainBareJid serviceName = null ;
    String severname ="";

    String localip = getLocalIp() ;
    String Remoteip = "";
    String openfireDomain = "";
    String openfirePort = "5222";
    String port = "5060";
    String protocol ="UDP";
    String username = "";
    String password = "";
    String openfireusername = "";
    String openfirepassword = "";
    SipProfile sipProfile3 ;
    SipManager sipManager = null ;

    String XmppconnectionStatus = "";
    String SipConnectionStatus = "";

    EditText edit1 ;
    EditText edit2;
    EditText edit3;
    EditText edit4;
    EditText edit5;
    EditText edit6;

    Roster roster ;
    ArrayList<String> arrayList = new ArrayList<>();
    String nameMsg ;
    String Message  ;
    static AbstractXMPPConnection connection ;

    static databaseconnection conn ;


    MyReceiver receiver ;
   // ArrayList<String>entriesnames = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clearreg();

        conn = new databaseconnection(this);
        conn.open();
        try {
            conn.truncateTable();
        }
        catch (Exception e)
        {

        }



        IntentFilter filter = new IntentFilter();
        filter.addAction("com.greenmagics.smartvoip.SipCallReceiver");





        if(sipManager == null) {
            sipManager = SipManager.newInstance(this);
        }

        receiver = new MyReceiver();
        registerReceiver(receiver,filter);

        try {
            //check if registeration info still working
            SharedPreferences prefs = getSharedPreferences("registeration", 0);

            Cursor cursor = conn.GetAllItems4();

            while(cursor.moveToNext())
            {
                 Remoteip =   cursor.getString(cursor.getColumnIndex("sipdomain"));
                openfireDomain = cursor.getString(cursor.getColumnIndex("xmppdomain"));
                username = cursor.getString(cursor.getColumnIndex("sipuser"));
                password = cursor.getString(cursor.getColumnIndex("sippass"));
                openfireusername = cursor.getString(cursor.getColumnIndex("openfireuser"));
                openfirepassword = cursor.getString(cursor.getColumnIndex("openfirepass"));
            }
               // Remoteip = prefs.getString("sipdomain", "");
               // openfireDomain = prefs.getString("openfiredomain", "");
               // username = prefs.getString("sipname", "");
               // password = prefs.getString("sippass", "");
               // openfireusername = prefs.getString("openfirename", "");
               // openfirepassword = prefs.getString("openfirepass", "");
                RegisterSip();
                RegisterXmpp();



                    Intent intent = new Intent(getBaseContext(), UsersActivity.class);

            startService(new Intent(getBaseContext(),ChatService.class));

                    //intent.putStringArrayListExtra("array",entriesnames);
                    startActivity(intent);





        }
        catch(Exception e)
        {

        }



        edit1 = (EditText)findViewById(R.id.editText);
        edit2 = (EditText)findViewById(R.id.editText2);
        edit3 = (EditText)findViewById(R.id.editText3);
        edit4 = (EditText)findViewById(R.id.editText4);
        edit5 = (EditText)findViewById(R.id.editText5);
        edit6 = (EditText)findViewById(R.id.editText6);
        Button Register = (Button) findViewById(R.id.button);

        Button closeapp = (Button) findViewById(R.id.closeapp);


        closeapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    stopService(new Intent(getBaseContext(),ChatService.class));
                    clearreg();

                    System.exit(0);
                }
                catch (Exception e)
                {

                }
            }
        });




        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Remoteip = edit1.getText().toString();
                openfireDomain = edit2.getText().toString();
                username = edit3.getText().toString();
                password = edit4.getText().toString();
                openfireusername = edit5.getText().toString();
                openfirepassword = edit6.getText().toString();


                try {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {


                    RegisterSip();

                    RegisterXmpp();





                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getBaseContext(), "Connected Successfully", Toast.LENGTH_LONG).show();
                                    }
                                });


                        SharedPreferences prefs = getSharedPreferences("registeration", 0);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("openfirename", openfireusername);
                        editor.putString("sipname", username);
                        editor.putString("openfirepass", openfirepassword);
                        editor.putString("sippass", password);
                        editor.putString("sipdomain", Remoteip);
                        editor.putString("openfiredomain", openfireDomain);
                        editor.apply();

                        conn.insertitem4(Remoteip, openfireDomain, username, password, openfireusername, openfirepassword);


                        startService(new Intent(getBaseContext(), ChatService.class));
                        Intent intent = new Intent(getBaseContext(), UsersActivity.class);

                        startActivity(intent);



                        }
                    }).start();
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Error in connection", Toast.LENGTH_LONG).show();
                }


            }

        });






    }


    public void RegisterXmpp()
    {




//make xmpp connection

        try {
            serviceName = JidCreate.domainBareFrom(openfireDomain);

        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }


        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(openfireusername,openfirepassword)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setHost(openfireDomain)
                .setPort(Integer.parseInt(openfirePort))
                .setServiceName(serviceName)
                .setDebuggerEnabled(true).build();







        connection = new XMPPTCPConnection(config);


        connection.addConnectionListener(new ConnectionListener() {


            @Override
            public void connected(XMPPConnection xmppConnection) {
                Log.d("Connection","connected");
                XmppconnectionStatus = "connected";
            }

            @Override
            public void authenticated(XMPPConnection xmppConnection, boolean b) {

            }

            @Override
            public void connectionClosed() {
                Log.d("state","closed");

            }

            @Override
            public void connectionClosedOnError(Exception e) {
                Log.d("state","closedon error");

            }

            @Override
            public void reconnectionSuccessful() {

            }

            @Override
            public void reconnectingIn(int i) {

            }

            @Override
            public void reconnectionFailed(Exception e) {

            }
        });



        //






        new Thread(new Runnable() {
            @Override
            public void run() {


                try {

                    connection.connect();

                } catch (XMPPException e) {
                    Log.d("error", "error connect");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SmackException e) {
                    e.printStackTrace();
                }

                try {

                    connection.login();
                    Log.d("state", "logged in");

                } catch (XMPPException e) {
                    Log.d("error", "error login");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SmackException e) {
                    e.printStackTrace();
                }


                Presence presence = new Presence(Presence.Type.available);
                presence.setStatus("I’m Available");
                try {
                    connection.sendPacket(presence);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }






            }
        }).start();
            }



    public void sendMessage(final  String name ,final String Msg)
    {


        new Thread(new Runnable() {
            @Override
            public void run() {


                    severname = connection.getServiceName().intern();


                EntityJid jid = null ;
                try {

                       // jid = JidCreate.entityBareFrom(name + "@conference." + severname);

                        jid = JidCreate.entityBareFrom(name + "@" + severname);

                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }

                Chat chat = ChatManager.getInstanceFor(connection).createChat(jid, new ChatMessageListener() {

                    @Override
                    public void processMessage(Chat chat, Message message) {



                    }
                });





                try {
                    chat.sendMessage(Msg);



                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }



            }
        }).start();



    }

    public void sendMessageGroupChat (final  String name ,final String Msg)
    {


        new Thread(new Runnable() {
            @Override
            public void run() {





                        severname = connection.getServiceName().intern();
                        // Get the MultiUserChatManager
                        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
                        EntityBareJid jid = null ;

                        try {
                            jid = JidCreate.entityBareFrom(name+"@conference."+severname);
                        } catch (XmppStringprepException e) {
                            e.printStackTrace();
                        }
// Get a MultiUserChat using MultiUserChatManager
                        MultiUserChat muc = manager.getMultiUserChat(jid);


                Message msg = new Message();
                msg.setBody(Msg);
                msg.setType(org.jivesoftware.smack.packet.Message.Type.groupchat);


                try {
                    muc.sendMessage(msg);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }).start();



    }

public void createInstantGroup(final String Gpname)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {


                severname = connection.getServiceName().intern();
        // Get the MultiUserChatManager
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        EntityBareJid jid = null ;

        try {
            jid = JidCreate.entityBareFrom(Gpname+"@conference."+severname);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
// Get a MultiUserChat using MultiUserChatManager
        MultiUserChat muc = manager.getMultiUserChat(jid);


                Resourcepart part = null ;
                try {
                    part = Resourcepart.from("mohamed");
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }

// Create the room
        try {
            muc.createOrJoin(part);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MultiUserChatException.MucAlreadyJoinedException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }catch (MultiUserChatException.NotAMucServiceException e) {
            e.printStackTrace();
        }


// Send an empty room configuration form which indicates that we want
// an instant room
        try {

            muc.sendConfigurationForm(new Form(DataForm.Type.submit));
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

            }
        }).start();

    }


    public void joinInstantGroup(final String Gpname)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {


                severname = connection.getServiceName().intern();
                // Get the MultiUserChatManager
                MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);

                EntityBareJid jid = null ;

                try {
                    jid = JidCreate.entityBareFrom(Gpname+"@conference."+severname);
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }
// Get a MultiUserChat using MultiUserChatManager
                MultiUserChat muc = manager.getMultiUserChat(jid);


// Create the room
                Resourcepart part = null ;
                try {
                    part = Resourcepart.from("mohamed");
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }

                    try {
                        muc.join(part);
                    } catch (SmackException.NoResponseException e) {
                        e.printStackTrace();
                    } catch (XMPPException.XMPPErrorException e) {
                        e.printStackTrace();
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (MultiUserChatException.NotAMucServiceException e) {
                        e.printStackTrace();
                    }


// Send an empty room configuration form which indicates that we want
// an instant room
                try {
                    muc.sendConfigurationForm(new Form(DataForm.Type.submit));
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }






    public void RegisterSip()
    {

        Thread thread =  new Thread(new Runnable() {
            @Override
            public void run() {


                try {




                    Intent intent = new Intent();
                    intent.setAction("com.greenmagics.smartvoip.SipCallReceiver");
                    PendingIntent pi = PendingIntent.getBroadcast(getBaseContext(), 0, intent, Intent.FILL_IN_DATA);


                    SipProfile.Builder builder3 = new SipProfile.Builder(username, Remoteip);
                    builder3.setPassword(password);
                    builder3.setProtocol(protocol);
                    builder3.setPort(5060);
                    sipProfile3 = builder3.build();


                    sipManager.setRegistrationListener(sipProfile3.getUriString(), new SipRegistrationListener() {
                        @Override
                        public void onRegistering(String localProfileUri) {
                            Log.d("state", "Registering");
                        }

                        @Override
                        public void onRegistrationDone(String localProfileUri, long expiryTime) {
                            Log.d("state", "Done");
                            SipConnectionStatus="connected";

                        }

                        @Override
                        public void onRegistrationFailed(String localProfileUri, int errorCode, String errorMessage) {

                            Log.d("state", errorMessage);
                        }
                    });
                    sipManager.open(sipProfile3, pi, null);



                       // sipManager.register(sipProfile3, 400, null);


















                } catch (android.net.sip.SipException e) {
                    e.printStackTrace();
                    Log.d("error", e.getMessage());
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }});
        thread.start();


    }



    public String getLocalIp()
    {
        StringBuilder IFCONFIG=new StringBuilder();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                        IFCONFIG.append(inetAddress.getHostAddress().toString()+"\n");
                    }

                }
            }
        } catch (SocketException ex) {
            Log.e("LOG_TAG", ex.toString());
        }


        String  android_id = IFCONFIG.toString();
        return android_id ;
    }

    public void clearreg()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    SipProfile.Builder builder3 = new SipProfile.Builder(username, Remoteip);
                    builder3.setPassword(password);
                    builder3.setProtocol(protocol);
                    builder3.setPort(5060);
                    sipProfile3 = builder3.build();


                    sipManager.close(sipProfile3.getUriString());
                    sipManager.unregister(sipProfile3, new SipRegistrationListener() {
                        @Override
                        public void onRegistering(String localProfileUri) {

                        }

                        @Override
                        public void onRegistrationDone(String localProfileUri, long expiryTime) {

                        }

                        @Override
                        public void onRegistrationFailed(String localProfileUri, int errorCode, String errorMessage) {

                        }
                    });


                    if(connection.isConnected()) {
                        connection.disconnect();
                    }
                } catch (SipException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Thread(new Runnable() {
            @Override
            public void run() {
                unregisterReceiver(receiver);
                //stopService(new Intent(getBaseContext(),ChatService.class));
            }
        }).start();
    }
}
