package ivamluz.marvelshelf.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ivamluz.marvelshelf.BuildConfig;


public class DatabaseHelper extends SQLiteOpenHelper {

    //Path to the device folder with databases
    public static String DB_PATH;

    //Database file name
    public static String DB_NAME;
    public SQLiteDatabase database;
    public final Context context;

    public SQLiteDatabase getDb() {
        return database;
    }

    public DatabaseHelper(Context context, String databaseName) {
        super(context, databaseName, null, 1);
        this.context = context;
        //Write a full path to the databases of your application
        DB_PATH = Environment.getDataDirectory() + "/data/" + BuildConfig.APPLICATION_ID + "/databases/";
        DB_NAME = databaseName;
        openDataBase();
    }

    //This piece of code will create a database if it’s not yet created
    public void createDataBase() {
        boolean dbExist = checkDataBase();
        if (!dbExist) {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e(this.getClass().toString(), "Copying error");
                throw new Error("Error copying database!");
            }
        } else {
            Log.i(this.getClass().toString(), "Database already exists");
        }
    }

    //Performing a database existence check
    private boolean checkDataBase() {
        SQLiteDatabase checkDb = null;
        try {
            String path = DB_PATH + DB_NAME;
            checkDb = SQLiteDatabase.openDatabase(path, null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException e) {
            Log.e(this.getClass().toString(), "Error while checking db");
        }
        //Android doesn’t like resource leaks, everything should
        // be closed
        if (checkDb != null) {
            checkDb.close();
        }
        return checkDb != null;
    }

    //Method for copying the database
    private void copyDataBase() throws IOException {
        //Open a stream for reading from our ready-made database
        //The stream source is located in the assets
        InputStream externalDbStream = context.getAssets().open(DB_NAME);

        //Path to the created empty database on your Android device
        String outFileName = DB_PATH + DB_NAME;

        //Now create a stream for writing the database byte by byte
        OutputStream localDbStream = new FileOutputStream(outFileName);

        //Copying the database
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = externalDbStream.read(buffer)) > 0) {
            localDbStream.write(buffer, 0, bytesRead);
        }
        //Don’t forget to close the streams
        localDbStream.close();
        externalDbStream.close();
    }

    public SQLiteDatabase openDataBase() throws SQLException {
        String path = DB_PATH + DB_NAME;
        if (database == null) {
            createDataBase();
            database = SQLiteDatabase.openDatabase(path, null,
                    SQLiteDatabase.OPEN_READWRITE);
        }
        return database;
    }

    @Override
    public synchronized void close() {
        if (database != null) {
            database.close();
        }
        super.close();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {}
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}


///**
// * Created by iluz on 5/1/16.
// * Credits: http://blog.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/
// */
//public class DatabaseHelper extends SQLiteOpenHelper {
//    private static final int DATABASE_VERSION = 1;
//
//    //The Android's default system path of your application database.
//    private static String sDbFolderPath = Environment.getDataDirectory() + "/data/" + BuildConfig.APPLICATION_ID + "/databases/";
//
//    private static String sDbName = "marvel_shelf.db";
//    private static String sFullDbPath = sDbFolderPath + sDbName;
//
//    private SQLiteDatabase mDatabase;
//
//    private final Context mContext;
//
//    /**
//     * Constructor
//     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
//     *
//     * @param context
//     */
//    public DatabaseHelper(Context context) {
//        super(context, sDbName, null, DATABASE_VERSION);
//        this.mContext = context;
//    }
//
//    /**
//     * Creates a empty database on the system and rewrites it with your own database.
//     */
//    public void createDataBase() throws IOException {
//        if (databaseExists()) {
//            //do nothing - database already exist
//            return;
//        }
//
//        //By calling this method and empty database will be created into the default system path
//        //of your application so we are gonna be able to overwrite that database with our database.
//        this.getReadableDatabase();
//
//        try {
//            copyDatabase();
//        } catch (IOException e) {
//            throw new Error("Error copying database");
//        }
//    }
//
//    public void openDataBase() throws SQLException {
//        mDatabase = SQLiteDatabase.openDatabase(sFullDbPath, null, SQLiteDatabase.OPEN_READONLY);
//    }
//
//    @Override
//    public synchronized void close() {
//        if (mDatabase != null)
//            mDatabase.close();
//
//        super.close();
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//
//    }
//
//    /**
//     * Check if the database already exist to avoid re-copying the file each time you open the application.
//     *
//     * @return true if it exists, false if it doesn't
//     */
//    private boolean databaseExists() {
//        SQLiteDatabase checkDB = null;
//
//        try {
//            checkDB = SQLiteDatabase.openDatabase(sFullDbPath, null, SQLiteDatabase.OPEN_READONLY);
//        } catch (SQLiteException e) {
//            //database does't exist yet.
//        }
//
//        if (checkDB != null) {
//            checkDB.close();
//        }
//
//        return (checkDB != null);
//    }
//
//    /**
//     * Copies your database from your local assets-folder to the just created empty database in the
//     * system folder, from where it can be accessed and handled.
//     * This is done by transfering bytestream.
//     */
//    private void copyDatabase() throws IOException {
//        //Open your local db as the input stream
//        InputStream myInput = mContext.getAssets().open(sDbName);
//
//        //Open the empty db as the output stream
//        OutputStream myOutput = new FileOutputStream(sFullDbPath);
//
//        //transfer bytes from the inputfile to the outputfile
//        byte[] buffer = new byte[1024];
//        int length;
//        while ((length = myInput.read(buffer)) > 0) {
//            myOutput.write(buffer, 0, length);
//        }
//
//        //Close the streams
//        myOutput.flush();
//        myOutput.close();
//        myInput.close();
//    }
//
//    // Add your public helper methods to access and get content from the database.
//    // You could return cursors by doing "return mDatabase.query(....)" so it'd be easy
//    // to you to create adapters for your views.
//
//}
