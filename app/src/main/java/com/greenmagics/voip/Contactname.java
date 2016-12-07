package com.greenmagics.voip;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.Collection;

public class Contactname extends AppCompatActivity {

    EditText text1;
    EditText text2;
    MyReceiver receiver ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactname);

        //startService(new Intent(getBaseContext(),ChatService.class));

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.greenmagics.smartvoip.SipCallReceiver");
        receiver = new MyReceiver();
        registerReceiver(receiver,filter);

        text1 = (EditText) findViewById(R.id.editTextcontact);
        text2 = (EditText) findViewById(R.id.editTextcontact2);
        Button button = (Button) findViewById(R.id.buttoncontact);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sipuser =   text1.getText().toString();
                String openfireuser =   text2.getText().toString();


                Intent intent = new Intent(getBaseContext(),UsersActivity.class);
                intent.putExtra("openfireuser",openfireuser);
                intent.putExtra("sipuser",sipuser);


                startActivity(intent);




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
