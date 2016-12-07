package com.greenmagics.voip;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Interpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.util.ArrayList;
import java.util.List;

public class GroupChat extends AppCompatActivity {
ArrayList<Model> list2 = new ArrayList<>();
    EditText txt ;
    MyReceiver receiver ;
    databaseconnection conn ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        conn = new databaseconnection(this);
        conn.open();

       // startService(new Intent(getBaseContext(),ChatService.class));

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.greenmagics.smartvoip.SipCallReceiver");
        receiver = new MyReceiver();
        registerReceiver(receiver,filter);


        txt = (EditText)findViewById(R.id.editTextgroupchat);
        Button add = (Button) findViewById(R.id.buttongrchat);
        ListView listView = (ListView) findViewById(R.id.listViewgroupchat);

        try
        {
            SharedPreferences prefs = getSharedPreferences("registeration",0);
           String room =  prefs.getString("room name","");
            Model model = new Model(room);
            list2.add(model);
        }
        catch(Exception e)
        {

        }



        try {
            if (list2.size() > 0) {
                ArrayAdapter adapter = new adapter(this, list2);
                listView.setAdapter(adapter);
            }
        } catch (Exception e) {

        }


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String confText = txt.getText().toString();
                    SharedPreferences prefs = getSharedPreferences("registeration",0);
                    SharedPreferences.Editor editor =   prefs.edit();
                    editor.putString("room name",confText);
                    editor.apply();

                    Intent i = new Intent(getBaseContext(),GroupChat.class);

                    startActivity(i);
                }
                catch (Exception e)
                {

                }


            }
        });



    }


    public class adapter extends ArrayAdapter<Interpolator.Result> {


        List<Model> arrayList = new ArrayList<Model>();
        ArrayList<String> array = new ArrayList<>();

        LayoutInflater vi;
        int pos;
        String text;

        TextView txtview ;


        public adapter(Context context, List d) {
            super(context, android.R.layout.simple_list_item_1, d);

            arrayList = d;
            array.clear();


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
            v = vi.inflate(R.layout.groupchat_element, null);

            txtview = (TextView) v.findViewById(R.id.textViewGc);



            ImageButton message = (ImageButton) v.findViewById(R.id.imageButtonGc);




            Model model = arrayList.get(position);

            text = model.text;






            txtview.setText(text);





            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity main = new MainActivity();
                    try {
                        main.createInstantGroup(text);
                    }
                    catch (Exception e)
                    {

                    }

                    try {
                        main.joinInstantGroup(text);
                    }
                    catch (Exception e)
                    {

                    }


                    Intent intent = new Intent(getBaseContext(),Chat_Activity.class);
                    intent.putExtra("name",text);
                    intent.putExtra("groupchatfrom","groupchatfrom");


                    conn.truncateTable();

                    startActivity(intent);


                }
            });



            return v;

        }
    }


    //create model for custom adapter


    public class Model {

        String text = "";



        public Model(String txt) {

            text = txt;



        }

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
