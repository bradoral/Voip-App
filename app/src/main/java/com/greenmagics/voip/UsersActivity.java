package com.greenmagics.voip;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Interpolator;
import android.graphics.drawable.Drawable;

import android.net.sip.SipAudioCall;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UsersActivity extends AppCompatActivity {


    Model model;
    ListView view;
    Button buttonFrag;
    Button conferenceBut ;
    Button groupchatbut ;
    SharedPreferences prefs;
    List<UsersActivity.Model> list2 = new ArrayList<>();
    databaseconnection conn;
    TextView txtview2;
    TextView txtview;
    MyReceiver receiver ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        //startService(new Intent(getBaseContext(),ChatService.class));




        conn = new databaseconnection(this);
        conn.open();


        txtview = (TextView) findViewById(R.id.textView3);
        txtview2 = (TextView) findViewById(R.id.textView4);
        view = (ListView) findViewById(R.id.listView);
        buttonFrag = (Button) findViewById(R.id.button2);

        conferenceBut = (Button) findViewById(R.id.button3);

        groupchatbut = (Button) findViewById(R.id.button4);

        prefs = getSharedPreferences("registeration", 0);
        String openfirename = prefs.getString("openfirename", "");
        String sipname = prefs.getString("sipname", "");

        txtview.setText(openfirename);
        txtview2.setText(sipname);

        //for contacts added


        IntentFilter filter = new IntentFilter();
        filter.addAction("com.greenmagics.smartvoip.SipCallReceiver");
        receiver = new MyReceiver();
        registerReceiver(receiver,filter);


        Button logout = (Button) findViewById(R.id.buttonlogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conn.truncateTable4();

                MainActivity mainActivity = new MainActivity();
                mainActivity.clearreg();
                stopService(new Intent(getBaseContext(),ChatService.class));

                Intent i = new Intent(getBaseContext(),MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(i);
            }
        });


        try {

            Intent i = getIntent();
            String name = i.getStringExtra("openfireuser");
            String number = i.getStringExtra("sipuser");


            if (name != null && number != null) {


                conn.insertitem(name, number);


            }


        } catch (Exception e) {

        }


        try {

            Cursor cursor = conn.GetAllItems();

            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String number = cursor.getString(cursor.getColumnIndex("number"));
                model = new Model(name, number);
                list2.add(model);

            }
        } catch (Exception e) {

        }


        try {
            if (list2.size() > 0) {
                ArrayAdapter adapter = new adapter(this, list2);
                view.setAdapter(adapter);
            }
        } catch (Exception e) {

        }


        //put listener for add contact fragment

        buttonFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getBaseContext(), Contactname.class);
                startActivity(intent);
            }
        });


        conferenceBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Voice_Conference.class);
                startActivity(intent);
            }
        });


        groupchatbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),GroupChat.class);
                startActivity(intent);
            }
        });
    }


    public class adapter extends ArrayAdapter<Interpolator.Result> {


        List<Model> arrayList = new ArrayList<Model>();
        ArrayList<String> array = new ArrayList<>();
        ArrayList<String> array2 = new ArrayList<>();
        LayoutInflater vi;
        int pos;
        String text;
        String text2;
        TextView txtview ;
        TextView txtview2 ;

        public adapter(Context context, List d) {
            super(context, android.R.layout.simple_list_item_1, d);

            arrayList = d;
            array.clear();
            array2.clear();

        }


        @Override
        public int getCount() {
            return arrayList.size();
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            pos = position;
            vi = LayoutInflater.from(getBaseContext());
            v = vi.inflate(R.layout.list_element, null);

            txtview = (TextView) v.findViewById(R.id.textView2);
            txtview2 = (TextView) v.findViewById(R.id.textView6);


            ImageButton call = (ImageButton) v.findViewById(R.id.imageButton);
            ImageButton message = (ImageButton) v.findViewById(R.id.imageButton2);
            ImageButton delete = (ImageButton) v.findViewById(R.id.imageButton3);


            Model model = arrayList.get(position);

            text = model.text;
            text2 = model.text2;


            array.add(text);
            array2.add(text2);

            txtview.setText(text);
            txtview2.setText(text2);


            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {


                        String name = array.get(position);


                        conn.deletecontact(name);
                        // conn.alterAutoincrement();

                        Intent i = new Intent(getBaseContext(), UsersActivity.class);
                        startActivity(i);

                    } catch (Exception t) {

                        t.printStackTrace();
                        Log.d("delete", t.getMessage());
                    }
                }
            });

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String number = array2.get(position);
                    String mynumber = txtview2.getText().toString();

                    Intent i = new Intent(getBaseContext(), Call_Activity.class);
                    i.putExtra("number", number);
                    i.putExtra("mynumber", mynumber);
                    startActivity(i);
                }
            });

            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = array.get(position);

                    Intent i = new Intent(getBaseContext(), Chat_Activity.class);
                    i.putExtra("name", name);

                    conn.truncateTable();
                    startActivity(i);
                }
            });

            return v;

        }
    }


    //create model for custom adapter


    public class Model {

        String text = "";
        String text2 = "";


        public Model(String txt, String txt2) {

            text = txt;
            text2 = txt2;


        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getBaseContext(),MainActivity.class);
        startActivity(i);
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



