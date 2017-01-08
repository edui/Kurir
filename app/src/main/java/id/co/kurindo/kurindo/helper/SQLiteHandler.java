package id.co.kurindo.kurindo.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.City;
import id.co.kurindo.kurindo.model.Recipient;
import id.co.kurindo.kurindo.model.Sender;
import id.co.kurindo.kurindo.model.User;

/**
 * Created by DwiM on 11/8/2016.
 */
public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "kurindoapp_api";

    // Login table name
    private static final String TABLE_USER = "user";
    private static final String TABLE_RECIPIENT = "recipient";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_UID = "uid";
    private static final String KEY_ROLE = "role";
    private static final String KEY_CITY = "city";
    private static final String KEY_CITYTEXT = "cityText";
    private static final String KEY_ACTIVE = "active";
    private static final String KEY_APPROVED = "approved";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_API = "api_key";

    private static final String KEY_NAME = "name";
    private static final String KEY_ALAMAT = "alamat";
    private static final String KEY_RT = "rt";
    private static final String KEY_RW = "rw";
    private static final String KEY_DUSUN = "dusun";
    private static final String KEY_DESA = "desa";
    private static final String KEY_KECAMATAN = "kecamatan";
    private static final String KEY_KABUPATEN = "kabupaten";
    private static final String KEY_PROPINSI = "propinsi";
    private static final String KEY_NEGARA = "negara";
    private static final String KEY_KODEPOS = "kodepos";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_FIRSTNAME + " TEXT,"+ KEY_LASTNAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_PHONE + " TEXT ," + KEY_UID + " TEXT,"
                + KEY_ROLE + " TEXT," + KEY_CITY + " TEXT,"
                + KEY_API + " TEXT,"
                + KEY_ACTIVE+ " BOOLEAN," + KEY_APPROVED + " BOOLEAN,"
                + KEY_CREATED_AT + " TEXT, " + KEY_GENDER + " TEXT " + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String firstname, String lastname, String email, String phone, String gender, String uid, String role, String city, String api, boolean active, boolean approved, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FIRSTNAME, firstname); // Name
        values.put(KEY_LASTNAME, lastname); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // Email
        values.put(KEY_PHONE, phone);
        values.put(KEY_ROLE, role);
        values.put(KEY_CITY, city);
        values.put(KEY_API, api);
        values.put(KEY_ACTIVE, (active ? 1:0));
        values.put(KEY_APPROVED, (approved ? 1:0));
        values.put(KEY_CREATED_AT, created_at); // Created At
        values.put(KEY_GENDER, gender);

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user =  parseToUser(cursor);
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    private HashMap<String, String> parseToUser(Cursor cursor){
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_FIRSTNAME, cursor.getString(1));
        user.put(KEY_LASTNAME, cursor.getString(2));
        user.put(KEY_EMAIL, cursor.getString(3));
        user.put(KEY_PHONE, cursor.getString(4));
        user.put(KEY_UID, cursor.getString(5));
        user.put(KEY_ROLE, cursor.getString(6));
        user.put(KEY_CITY, cursor.getString(7));
        user.put(KEY_ACTIVE, (cursor.getInt(9) == 1)? ""+true: ""+false) ;
        user.put(KEY_APPROVED, (cursor.getInt(10) == 1)? ""+true: ""+false) ;
        user.put(KEY_CREATED_AT, cursor.getString(11));
        try {
            user.put(KEY_GENDER, cursor.getString(12));
        }catch (Exception e){}

        return user;
    }
    public HashMap<String, String> getUserDetailsByEmail(String email) {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE email = '"+ email + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user =  parseToUser(cursor);

        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }
    /**
     * Getting user data from database
     * */
    public String getUserRole(String uid) {
        String selectQuery = "SELECT role FROM " + TABLE_USER + " WHERE uid = '"+ uid + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        String str = "";
        if (cursor.getCount() > 0) {
            str = cursor.getString(0);
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user role from Sqlite: " + uid);

        return str;
    }

    public String getUserRoleByEmail(String email) {
        String selectQuery = "SELECT role FROM " + TABLE_USER + " WHERE email = '"+ email+ "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        String str = "";
        if (cursor.getCount() > 0) {
            str = cursor.getString(1);
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user role from Sqlite: " + email);

        return str;
    }

    public String getApiKeyByEmail(String email) {
        String selectQuery = "SELECT "+KEY_API+" FROM " + TABLE_USER + " WHERE email = '"+ email+ "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        String str = "";
        if (cursor.getCount() > 0) {
            str = cursor.getString(1);
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching api from Sqlite: " + email);

        return str;
    }
    public String getUserApi() {
        String str = null;
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            str = cursor.getString(8);
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching "+KEY_API+" from Sqlite: "+str);

        return str;
    }
    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    public void activatedUsers(String email){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues update = new ContentValues();
        update.put(KEY_ACTIVE, 1);

        int i = db.update(TABLE_USER, update, "email = '"+email+"'", null);
        db.close();
        Log.d(TAG, "activatedUsers ( "+ i +" )into  sqlite" + email);
    }

    public void userApproved(String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues update = new ContentValues();
        update.put(KEY_APPROVED, 1);

        int i = db.update(TABLE_USER, update, "email = '"+email+"'", null);
        db.close();
        Log.d(TAG, "userApproved ( "+ i +" ) into sqlite"+email);
    }


    /////////////
    public void onCreateTableRecipient(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_RECIPIENT+ "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"+ KEY_PHONE + " TEXT ," + KEY_ALAMAT + " TEXT,"+ KEY_CITY + " TEXT,"+ KEY_CITYTEXT + " TEXT,"
                + KEY_RT + " TEXT," + KEY_RW + " TEXT," + KEY_DUSUN + " TEXT,"+ KEY_DESA + " TEXT," + KEY_KECAMATAN + " TEXT,"
                + KEY_KABUPATEN + " TEXT,"+ KEY_PROPINSI + " TEXT" + ","+ KEY_NEGARA + " TEXT"+ ","+ KEY_KODEPOS + " TEXT"+")";
        db.execSQL(CREATE_TABLE);

        Log.d(TAG, TABLE_RECIPIENT+" tables created");
    }

    public void onUpgradeTableRecipient(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPIENT);

        // Create tables again
        onCreateTableRecipient(db);
    }

    public void addRecipient(Recipient recipient) {
        //addRecipient(recipient.getName(), recipient.getTelepon(), recipient.getAddress().getAlamat(), recipient.getAddress().getCity());
        addRecipient(recipient.getName(), recipient.getTelepon(), recipient.getAddress().getAlamat(), recipient.getAddress().getCity().getCode(), recipient.getAddress().getCity().getText()
        ,recipient.getAddress().getRt(),recipient.getAddress().getRw(),recipient.getAddress().getDusun(),recipient.getAddress().getDesa(),recipient.getAddress().getKecamatan()
                ,recipient.getAddress().getKabupaten(),recipient.getAddress().getPropinsi(),recipient.getAddress().getNegara(),recipient.getAddress().getKodepos());
    }
    public void addRecipient(String name, String telepon, String alamat, String city, String cityText) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); //
        values.put(KEY_PHONE, telepon); //
        values.put(KEY_ALAMAT, alamat); //
        values.put(KEY_CITY, city); //
        values.put(KEY_CITYTEXT, cityText); //

        // Inserting Row
        long id = db.insert(TABLE_RECIPIENT, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New recipient inserted into sqlite: " + id);
    }

    public void addRecipient(String name, String telepon, String alamat, String city, String cityText, String rt, String rw, String dusun, String desa, String kec, String kab, String prop, String negara, String kodepos) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_PHONE, telepon); // Name
        values.put(KEY_ALAMAT, alamat); //
        values.put(KEY_CITY, city); //
        values.put(KEY_CITYTEXT, cityText); //
        values.put(KEY_RT, rt); //
        values.put(KEY_RW, rw);
        values.put(KEY_DUSUN, dusun);
        values.put(KEY_DESA, desa);
        values.put(KEY_KECAMATAN, kec);
        values.put(KEY_KABUPATEN, kab);
        values.put(KEY_PROPINSI, prop);
        values.put(KEY_NEGARA, negara);
        values.put(KEY_KODEPOS, kodepos);

        // Inserting Row
        long id = db.insert(TABLE_RECIPIENT, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New recipient inserted into sqlite: " + id);
    }

    public Set<Recipient> getRecipientList() {
        Set<Recipient> result = new LinkedHashSet<>();
        String selectQuery = "SELECT  * FROM " + TABLE_RECIPIENT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Address addr = new Address();
            addr.setAlamat(cursor.getString(3));
            addr.setCity(new City(cursor.getString(4), cursor.getString(5)));
            Recipient r = new Recipient(cursor.getString(1), cursor.getString(2), addr);
            result.add(r);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Fetching Recipient from Sqlite: ");
        return result;
    }

    public User toUser(HashMap<String, String> params){
        User user = null;
        if(params != null && params.size() > 0){
            user = new User();
            user.setFirstname(params.get(KEY_FIRSTNAME));
            user.setLastname(params.get(KEY_LASTNAME));
            user.setEmail(params.get(KEY_EMAIL));
            user.setPhone(params.get(KEY_PHONE));
            user.setUid(params.get(KEY_UID));
            user.setRole(params.get(KEY_ROLE));
            user.setCity(params.get(KEY_CITY));
            user.setGender(params.get(KEY_GENDER));
            Address addr = new Address();
            addr.setAlamat("");
            addr.setCity(new City(params.get(KEY_CITY), params.get(KEY_CITYTEXT)));
            user.setAddress(addr);
        }
        return user;
    }

    public void updateUser(String email, String role, boolean active, boolean approved) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_ROLE, role);
        values.put(KEY_ACTIVE, (active ? 1:0));
        values.put(KEY_APPROVED, (approved ? 1:0));

        String where = "email = ?";
        String[] whereArgs = {email};
        long id = db.update(TABLE_USER, values, where, whereArgs);
        db.close(); // Closing database connection

        Log.d(TAG, "User data updated into sqlite: " + id);
    }

    public void updateUserCity(String email, String city) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_ROLE, city);

        String where = "email = ?";
        String[] whereArgs = {email};
        long id = db.update(TABLE_USER, values, where, whereArgs);
        db.close(); // Closing database connection

        Log.d(TAG, "User data updated into sqlite: " + id);
    }

}