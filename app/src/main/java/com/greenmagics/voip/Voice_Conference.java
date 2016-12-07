package com.greenmagics.voip;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
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

import java.util.ArrayList;
import java.util.List;

public class Voice_Conference extends AppCompatActivity {

    EditText txt ;
    databaseconnection conn ;
    ArrayList<Model>list2 = new ArrayList<>();
    Model model ;
    MyReceiver receiver ;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice__conference);

       // startService(new Intent(getBaseContext(),ChatService.class));

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.greenmagics.smartvoip.SipCallReceiver");
        receiver = new MyReceiver();
        registerReceiver(receiver,filter);

        conn = new databaseconnection(this);
        conn.open();



        try {

            Cursor cursor = conn.GetAllItems3();

            while (cursor.moveToNext()) {
                String confnum = cursor.getString(cursor.getColumnIndex("confnum"));

                model = new Model(confnum);
                list2.add(model);

            }
        } catch (Exception e) {

        }






        txt = (EditText)findViewById(R.id.editTextconference);
        Button add = (Button) findViewById(R.id.buttonconf);
        ListView listView = (ListView) findViewById(R.id.listViewconference);

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
                    conn.insertitem3(confText);
                    Intent i = new Intent(getBaseContext(),Voice_Conference.class);
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
            v = vi.inflate(R.layout.conf_element_layout, null);

            txtview = (TextView) v.findViewById(R.id.conftextView2);



            ImageButton call = (ImageButton) v.findViewById(R.id.confimageButton);

            ImageButton delete = (ImageButton) v.findViewById(R.id.confimageButton3);


            Model model = arrayList.get(position);

            text = model.text;






            txtview.setText(text);



            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {


                        String name = text ;


                        conn.deletecontact3(name);
                        // conn.alterAutoincrement();

                        Intent i = new Intent(getBaseContext(), Voice_Conference.class);
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

                    SharedPreferences prefs = getSharedPreferences("registeration",0);
                    String mynumber =  prefs.getString("sipname","");
                    String number = text ;

                    Intent i = new Intent(getBaseContext(), Call_Activity.class);
                    i.putExtra("number", number);
                    i.putExtra("mynumber", mynumber);
                    i.putExtra("conference","conference");
                    startActivity(i);
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getBaseContext(),UsersActivity.class);
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
