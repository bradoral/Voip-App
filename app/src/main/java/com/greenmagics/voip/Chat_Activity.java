package com.greenmagics.voip;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;



import java.util.ArrayList;

public class Chat_Activity extends AppCompatActivity {

    MyReceiver receiver ;
    String myname ;
    String name ;
    EditText txt ;
    String message ;
    ArrayList<String> array = new ArrayList<>();
    databaseconnection conn ;
    String groupchatfrom ;
    String incomingmessagegroup ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_);

        //startService(new Intent(getBaseContext(),ChatService.class));

        conn = new databaseconnection(this);
        conn.open();



        String incomingmessage = getIntent().getStringExtra("Incomingmessage");
        String from = getIntent().getStringExtra("from");

        try {
            groupchatfrom = getIntent().getStringExtra("groupchatfrom");
            incomingmessagegroup = getIntent().getStringExtra("Incomingmessagegroup");
        }
        catch (Exception e)
        {

        }

        if(incomingmessage != null) {
            try {

                conn.insertitem2(incomingmessage);
            } catch (Exception e) {

            }
        }

        else if(incomingmessagegroup != null)
        {
            try {
                conn.insertitem2(incomingmessagegroup);
            } catch (Exception e) {

            }
        }

        try {

          Cursor cursor =   conn.GetAllItems2();
            while (cursor.moveToNext())
            {
                String ms =         cursor.getString(cursor.getColumnIndex("message"));
                array.add(ms);
            }
        }
        catch ( Exception e)
        {

        }

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.greenmagics.smartvoip.SipCallReceiver");
        receiver = new MyReceiver();
        registerReceiver(receiver,filter);


        name = getIntent().getStringExtra("name");

        txt =(EditText) findViewById(R.id.editText7);
        Button button = (Button) findViewById(R.id.button4);
        ListView list = (ListView) findViewById(R.id.listView);



            SharedPreferences prefs = getSharedPreferences("registeration",0);
            myname =     prefs.getString("openfirename","");


        if(name == null)
        {
            name = from ;
        }



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, array);
        list.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message =  txt.getText().toString();


                Intent i = new Intent(getBaseContext(),Chat_Activity.class);
                MainActivity actv = new MainActivity();

                if( groupchatfrom != null && groupchatfrom.length()>2)
                {
                    actv.sendMessageGroupChat(name, message);
                    i.putExtra("groupchatfrom","groupchatfrom");
                }
                else {
                    actv.sendMessage(name, message);
                    conn.insertitem2(myname +" : "+message);
                }




                i.putExtra("name",name);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);








            }
        });





    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        conn.truncateTable();
        Intent intent = new Intent(getBaseContext(),UsersActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Thread(new Runnable() {
            @Override
            public void run() {
                unregisterReceiver(receiver);
              //  stopService(new Intent(getBaseContext(),ChatService.class));
            }
        }).start();
    }
}
