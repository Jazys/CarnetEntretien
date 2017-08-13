package fr.julienj.carnetentretien.bdd;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BddAccess extends SQLiteOpenHelper{
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/fr.julienj.carnetentretien/databases/";
    // Data Base Name.
    private static  String DATABASE_NAME;
    // Data Base Version.
    private static int DATABASE_VERSION = 1;
    // Table Names of Data Base.
    static  String TABLE_Name;

    public Context context;
    static SQLiteDatabase sqliteDataBase;


    public BddAccess(Context context, String db_name, int db_version) {
        super(context, db_name, null ,db_version);
        DATABASE_NAME=db_name;
        DATABASE_VERSION=db_version;
        this.context = context;
    }

    public void createDataBase() throws IOException{
        //check if the database exists

        //Pour forcer le recreation de la DB
        context.deleteDatabase(DATABASE_NAME);

        boolean databaseExist = checkDataBase();


        if(databaseExist){
        }else{
            this.getWritableDatabase();
            copyDataBase();
            System.out.println("OK CREATE DB");
        }// end if else dbExist
    } // end createDataBase().

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    public boolean checkDataBase(){
        File databaseFile = new File(DB_PATH + DATABASE_NAME);
        return databaseFile.exists();
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transferring byte stream.
     * */
    private void copyDataBase() throws IOException{
        //Open your local db as the input stream
        InputStream myInput = context.getAssets().open(DATABASE_NAME);
        // Path to the just created empty db
        String outFileName = DB_PATH + DATABASE_NAME;
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        //transfer bytes from the input file to the output file
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    /**
     * This method opens the data base connection.
     * First it create the path up till data base of the device.
     * Then create connection with data base.
     */
    public void openDataBase() throws SQLException{
        //Open the database
        String myPath = DB_PATH + DATABASE_NAME;
        sqliteDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    /**
     * This Method is used to close the data base connection.
     */
    @Override
    public synchronized void close() {
        if(sqliteDataBase != null)
            sqliteDataBase.close();
        super.close();
    }

    /**
     * Apply your methods and class to fetch data using raw or queries on data base using
     * following demo example code as:
     */
    public Cursor getNearDecheterie(String table_name,String num_dep, double latitude, double longitude){
        String query = "select * From "+table_name+ " where dep='"+num_dep+"'";
        Cursor cursor = sqliteDataBase.rawQuery(query, null);
        return cursor;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // No need to write the create table query.
        // As we are using Pre built data base.
        // Which is ReadOnly.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No need to write the update table query.
        // As we are using Pre built data base.
        // Which is ReadOnly.
        // We should not update it as requirements of application.
    }
}