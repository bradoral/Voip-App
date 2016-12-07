package com.greenmagics.voip;


        import java.io.File;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.widget.Toast;





public class databaseconnection
{

    String number = "number";
    String name = "name";
    String Rowid = "ROWID";

    String message = "message";

    String confnum = "confnum";

    String sipdomain = "sipdomain";
    String xmppdomain = "xmppdomain";
    String sipuser = "sipuser";
    String sippass = "sippass";
    String openfireuser = "openfireuser";
    String openfirepass = "openfirepass";



    final static String Databasename = "VoipDatabase" ;
    final String tablename = "modeltext";
   final String tablename2 = "chat";
    final String tablename3 = "conference";
    final String tablename4 = "reg";
    final static int DatabaseVersion = 1 ;
    final static String DatabaseCreate = "create table modeltext (ROWID integer primary key autoincrement, "
            + "name text not null, number text not null);";
   final static String DatabaseCreate2 = "create table chat (ROWID integer primary key autoincrement, "
        + "message text not null);";

    final static String DatabaseCreate3 = "create table conference (ROWID integer primary key autoincrement, "
            + "confnum text not null);";

    final static String DatabaseCreate4 = "create table reg (ROWID integer primary key autoincrement, "
            + "sipdomain text not null, xmppdomain text not null,sipuser text not null,sippass text not null," +
            "openfireuser text not null,openfirepass text not null);";


    static Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public databaseconnection(Context ctx)
    {
        this.context = ctx ;
        DBHelper = new DatabaseHelper(context);
    }


    private static class DatabaseHelper extends SQLiteOpenHelper
    {

        public DatabaseHelper(Context context)
        {
            super(context,Databasename, null, DatabaseVersion);

        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try
            {
                db.execSQL(DatabaseCreate);
                db.execSQL(DatabaseCreate2);
                db.execSQL(DatabaseCreate3);
                db.execSQL(DatabaseCreate4);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Toast.makeText(context, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data", Toast.LENGTH_SHORT).show();
            db.execSQL("DROP TABLE IF EXISTS modeltext");
            db.execSQL("DROP TABLE IF EXISTS chat");
            db.execSQL("DROP TABLE IF EXISTS conference");
            db.execSQL("DROP TABLE IF EXISTS reg");
            onCreate(db);

        }

    }




    public databaseconnection open ()
    {

        db =   DBHelper.getWritableDatabase();
        return this ;
    }


    public void  close ()
    {

        DBHelper.close();
    }


    public long insertitem (String name,String number)
    {
        ContentValues values = new ContentValues();
        values.put(this.name, name);
        values.put(this.number, number);

        return db.insert(tablename, null, values);
    }

    public long insertitem2 (String msg)
    {
        ContentValues values = new ContentValues();
        values.put(this.message, msg);


        return db.insert(tablename2, null, values);
    }

    public long insertitem3 (String conf)
    {
        ContentValues values = new ContentValues();
        values.put(this.confnum, conf);


        return db.insert(tablename3, null, values);
    }
    public long insertitem4 (String sipdomain,String xmppdomain,String sipuser,String sippass,String xmppuser,String xmpppass)
    {
        ContentValues values = new ContentValues();
        values.put(this.sipdomain, sipdomain);
        values.put(this.xmppdomain,xmppdomain);
        values.put(this.sipuser,sipuser);
        values.put(this.sippass,sippass);
        values.put(this.openfireuser,xmppuser);
        values.put(this.openfirepass,xmpppass);


        return db.insert(tablename4, null, values);
    }


    public void truncateTable()
{
    try {
        db.execSQL("DROP TABLE IF EXISTS chat");
        db.execSQL(DatabaseCreate2);
    }
    catch (Exception e)
    {
        e.printStackTrace();
    }

}

    public void truncateTable4()
    {
        try {
            db.execSQL("DROP TABLE IF EXISTS reg");
            db.execSQL(DatabaseCreate4);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }




    public boolean deletecontact (String id)
    {

        return  db.delete(tablename,name+"= '"+id+"'",null) >0;

    }

    public boolean deletecontact3 (String id)
    {

        return  db.delete(tablename3,confnum+"= '"+id+"'",null) >0;

    }


    public Cursor GetAllItems ()
    {
        Cursor cursor = db.query(tablename,new String []{Rowid,name,number},null,null
                ,null,null,null);


        return cursor ;

    }

    public Cursor GetAllItems2 ()
    {
        Cursor cursor = db.query(tablename2,new String []{Rowid,message},null,null
                ,null,null,null);


        return cursor ;

    }


    public Cursor GetAllItems3 ()
    {
        Cursor cursor = db.query(tablename3,new String []{Rowid,confnum},null,null
                ,null,null,null);


        return cursor ;

    }


    public Cursor GetAllItems4 ()
    {
        Cursor cursor = db.query(tablename4,new String []{Rowid,sipdomain,xmppdomain,sipuser,sippass,openfireuser,openfirepass},null,null
                ,null,null,null);


        return cursor ;

    }


    public Cursor getItem (long rowid)
    {
        Cursor cursor = db.query(true,tablename,new String []{Rowid,name,number},
                Rowid + "=" + rowid,null,null,null,null,null);

        if(cursor !=null)
        {
            cursor.moveToFirst();
        }
        return cursor ;

    }




    public boolean updateitem (long rowid,String name,String number)
    {
        ContentValues values = new ContentValues();
        values.put(this.name, name);
        values.put(this.number, number);


        return db.update(tablename, values,Rowid +  "=" + rowid, null) > 0;
    }


    public void alterAutoincrement ()
    {
        db.execSQL("DROP TABLE IF EXISTS modeltext");
        db.execSQL(DatabaseCreate);

    }

    public Cursor Retrievelastvalue ()
    {
        Cursor cursor =db.query(tablename, new String [] {"MAX(ROWID)"},null ,null, null, null, null, null);
        return cursor ;
    }








    public File returnDataPath ()
    {
        File dbpath	   = context.getDatabasePath(Databasename);

        return dbpath ;

    }

}