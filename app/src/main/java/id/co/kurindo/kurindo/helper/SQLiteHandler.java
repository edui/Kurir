package id.co.kurindo.kurindo.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.City;
import id.co.kurindo.kurindo.model.TUser;
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
    private static final String TABLE_USER_ADDRESS = "user_address";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "data_type";
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
    private static final String KEY_NIK = "nik";
    private static final String KEY_SIMC = "simc";

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
    private static final String KEY_LATITUDE = "latitue";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_NOTES= "notes";

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

    public void addUser(TUser u ){
        addUser(u.getFirstname(), u.getLastname(), u.getEmail(), u.getPhone(), u.getGender(), u.getPhone(), u.getRole(),
                (u.getAddress() ==null||u.getAddress().getCity() == null ? null:u.getAddress().getCity().getCode()), u.getApi_key(), u.isActive(), u.isApproved(), u.getCreated_at());
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
    public String getUserEmail() {
        String str = null;
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            str = cursor.getString(3);
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching "+KEY_EMAIL+" from Sqlite: "+str);

        return str;
    }

    public String getUserPhone() {
        String str = null;
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            str = cursor.getString(4);
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching "+KEY_PHONE+" from Sqlite: "+str);

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
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"+ KEY_PHONE + " TEXT "+ ","+ KEY_GENDER + " TEXT," + KEY_ALAMAT + " TEXT,"+ KEY_CITY + " TEXT,"+ KEY_CITYTEXT + " TEXT,"
                + KEY_RT + " TEXT," + KEY_RW + " TEXT," + KEY_DUSUN + " TEXT,"+ KEY_DESA + " TEXT," + KEY_KECAMATAN + " TEXT,"
                + KEY_KABUPATEN + " TEXT,"+ KEY_PROPINSI + " TEXT" + ","+ KEY_NEGARA + " TEXT"+ ","+ KEY_KODEPOS + " TEXT, "
                + KEY_LATITUDE + " TEXT," + KEY_LONGITUDE + " TEXT"
                +")";
        db.execSQL(CREATE_TABLE);

        Log.d(TAG, TABLE_RECIPIENT+" tables created");
    }

    public void onUpgradeTableRecipient(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPIENT);

        // Create tables again
        onCreateTableRecipient(db);
    }
/*
    public void addRecipient(Recipient recipient) {
        //addRecipient(recipient.getName(), recipient.getTelepon(), recipient.getAddress().getAlamat(), recipient.getAddress().getCity());
        addRecipient(recipient.getName(), recipient.getTelepon(), recipient.getGender(), recipient.getAddress().getAlamat(), recipient.getAddress().getCity().getCode(), recipient.getAddress().getCity().getText()
        ,recipient.getAddress().getRt(),recipient.getAddress().getRw(),recipient.getAddress().getDusun(),recipient.getAddress().getDesa(),recipient.getAddress().getKecamatan()
                ,recipient.getAddress().getKabupaten(),recipient.getAddress().getPropinsi(),recipient.getAddress().getNegara(),recipient.getAddress().getKodepos(), recipient.getAddress().getLocation().latitude, recipient.getAddress().getLocation().longitude);
    }*/
    public void addRecipient(String name, String telepon, String gender,String alamat, String city, String cityText) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); //
        values.put(KEY_PHONE, telepon); //
        values.put(KEY_GENDER, gender); //
        values.put(KEY_ALAMAT, alamat); //
        values.put(KEY_CITY, city); //
        values.put(KEY_CITYTEXT, cityText); //

        // Inserting Row
        long id = db.insert(TABLE_RECIPIENT, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New recipient inserted into sqlite: " + id);
    }

    public void addRecipient(String name, String telepon, String gender,String alamat, String city, String cityText, double lat, double lng) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); //
        values.put(KEY_PHONE, telepon); //
        values.put(KEY_GENDER, gender); //
        values.put(KEY_ALAMAT, alamat); //
        values.put(KEY_CITY, city); //
        values.put(KEY_CITYTEXT, cityText); //
        values.put(KEY_LATITUDE, lat); //
        values.put(KEY_LONGITUDE, lng); //

        // Inserting Row
        long id = db.insert(TABLE_RECIPIENT, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New recipient inserted into sqlite: " + id);
    }
    public void addRecipient(String name, String telepon,  String gender, String alamat, String city, String cityText, String rt, String rw, String dusun, String desa, String kec, String kab, String prop, String negara, String kodepos) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_PHONE, telepon); // Name
        values.put(KEY_GENDER, gender); // Name
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

    public void addRecipient(String name, String telepon,  String gender, String alamat, String city, String cityText, String rt, String rw, String dusun, String desa, String kec, String kab, String prop, String negara, String kodepos, double lat, double lng) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_PHONE, telepon); // Name
        values.put(KEY_GENDER, gender); // Name
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
        values.put(KEY_LATITUDE, lat);
        values.put(KEY_LONGITUDE, lng);

        // Inserting Row
        long id = db.insert(TABLE_RECIPIENT, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New recipient inserted into sqlite: " + id);
    }
/*
    public Set<Recipient> getRecipientList() {
        Set<Recipient> result = new LinkedHashSet<>();
        String selectQuery = "SELECT  * FROM " + TABLE_RECIPIENT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Address addr = new Address();
            addr.setAlamat(cursor.getString(4));
            addr.setCity(new City(cursor.getString(5), cursor.getString(6)));
            addr.setLocation(new LatLng(cursor.getDouble(16), cursor.getDouble(17)));
            Recipient r = new Recipient(cursor.getString(1), cursor.getString(2), cursor.getString(3), addr);
            result.add(r);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Fetching Recipient from Sqlite: ");
        return result;
    }*/

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
    public void updateUserPhone(String phone, String role, boolean active, boolean approved) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PHONE, phone); // Email
        values.put(KEY_ROLE, role);
        values.put(KEY_ACTIVE, (active ? 1:0));
        values.put(KEY_APPROVED, (approved ? 1:0));

        String where = KEY_PHONE+" = ?";
        String[] whereArgs = {phone};
        long id = db.update(TABLE_USER, values, where, whereArgs);
        db.close(); // Closing database connection

        Log.d(TAG, "User data updated into sqlite: " + id);
    }

    public void updateUserPhone(String phone, String role, String city, String api, boolean active, boolean approved) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PHONE, phone); // Email
        values.put(KEY_ROLE, role);
        values.put(KEY_CITY, city);
        values.put(KEY_API, api);
        values.put(KEY_ACTIVE, (active ? 1:0));
        values.put(KEY_APPROVED, (approved ? 1:0));

        String where = KEY_PHONE+" = ?";
        String[] whereArgs = {phone};
        long id = db.update(TABLE_USER, values, where, whereArgs);
        db.close(); // Closing database connection

        Log.d(TAG, "User data updated into sqlite: " + id);
    }

    public void updateUserCity(String email, String city) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_CITY, city);

        String where = "email = ?";
        String[] whereArgs = {email};
        long id = db.update(TABLE_USER, values, where, whereArgs);
        db.close(); // Closing database connection

        Log.d(TAG, "User data updated into sqlite: " + id);
    }

    //================================
    public void onCreateUserAddress(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_USER_ADDRESS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TYPE + " TEXT,"
                + KEY_FIRSTNAME + " TEXT,"+ KEY_LASTNAME + " TEXT," + KEY_EMAIL + " TEXT UNIQUE," + KEY_PHONE + " TEXT ,"
                + KEY_ROLE + " TEXT," + KEY_GENDER + " TEXT , "+ KEY_NIK + " TEXT,"+ KEY_SIMC + " TEXT, "
                + KEY_API + " TEXT," + KEY_ACTIVE+ " BOOLEAN," + KEY_APPROVED + " BOOLEAN, " + KEY_CREATED_AT + " TEXT, "
                + KEY_LATITUDE + " TEXT," + KEY_LONGITUDE + " TEXT, "+ KEY_ALAMAT + " TEXT, "
                + KEY_RT + " TEXT," + KEY_RW + " TEXT," + KEY_DUSUN + " TEXT,"+ KEY_DESA + " TEXT," + KEY_KECAMATAN + " TEXT,"
                + KEY_KABUPATEN + " TEXT,"+ KEY_PROPINSI + " TEXT" + ","+ KEY_NEGARA + " TEXT"+ ","+ KEY_KODEPOS + " TEXT, "
                + KEY_NOTES + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);

        Log.d(TAG, "Database tables "+TABLE_USER_ADDRESS+" created");
    }

    // Upgrading database
    public void onUpgradeUserAddress(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_ADDRESS);

        // Create tables again
        onCreateUserAddress(db);
    }
    public void addAddress(TUser user, String type) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, type);
        values.put(KEY_FIRSTNAME, user.getFirstname());
        values.put(KEY_LASTNAME, user.getLastname());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_PHONE, user.getPhone());
        values.put(KEY_ROLE, user.getRole());
        values.put(KEY_GENDER, user.getGender());
        values.put(KEY_NIK, user.getNik());
        values.put(KEY_SIMC, user.getSimc());
        values.put(KEY_API, user.getApi_key());
        values.put(KEY_LATITUDE, (user.getAddress() == null || user.getAddress().getLocation() == null ? 0 : user.getAddress().getLocation().latitude ));
        values.put(KEY_LONGITUDE, (user.getAddress() == null || user.getAddress().getLocation() == null ? 0 : user.getAddress().getLocation().longitude ));
        values.put(KEY_ALAMAT, user.getAddress().getAlamat());
        values.put(KEY_RT, user.getAddress().getRt());
        values.put(KEY_RW, user.getAddress().getRw());
        values.put(KEY_DUSUN, user.getAddress().getDusun());
        values.put(KEY_DESA, user.getAddress().getDesa());
        values.put(KEY_KECAMATAN, user.getAddress().getKecamatan());
        values.put(KEY_KABUPATEN, user.getAddress().getKabupaten());
        values.put(KEY_PROPINSI, user.getAddress().getPropinsi());
        values.put(KEY_NEGARA, user.getAddress().getNegara());
        values.put(KEY_KODEPOS, user.getAddress().getKodepos());
        values.put(KEY_NOTES, user.getAddress().getNotes());

        // Inserting Row
        long id = db.insert(TABLE_USER_ADDRESS, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New address inserted into sqlite: " + id);
    }
    public void addAddress(TUser user) {
        addAddress(user, "ADDRESS");
    }

    public Set<TUser> getAddressList() {
        Set<TUser> result = new LinkedHashSet<>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER_ADDRESS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {


            //TUser r = new TUser(cursor.getString(1), cursor.getString(2), cursor.getString(3), addr);
            TUser r = new TUser();

            r.setFirstname(cursor.getString(2));
            r.setLastname(cursor.getString(3));
            r.setEmail(cursor.getString(4));
            r.setPhone(cursor.getString(5));
            r.setRole(cursor.getString(6));
            r.setGender(cursor.getString(7));
            r.setNik(cursor.getString(8));
            r.setSimc(cursor.getString(9));
            r.setApi_key(cursor.getString(10));
            r.setActive(Boolean.parseBoolean( cursor.getString(11) ));
            r.setApproved(Boolean.parseBoolean( cursor.getString(12) ));
            r.setCreated_at(cursor.getString(13));

            Address addr = new Address();
            addr.setAlamat(cursor.getString(16));
            addr.setCity(new City(cursor.getString(20), cursor.getString(20)));
            addr.setLocation(new LatLng(cursor.getDouble(14), cursor.getDouble(15)));
            addr.setRt(cursor.getString(16));
            addr.setRw(cursor.getString(17));
            addr.setDusun(cursor.getString(18));
            addr.setDesa(cursor.getString(20));
            addr.setKecamatan(cursor.getString(21));
            addr.setKabupaten(cursor.getString(22));
            addr.setPropinsi(cursor.getString(23));
            addr.setNegara(cursor.getString(24));
            addr.setNotes(cursor.getString(26));
            r.setAddress(addr);

            result.add(r);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Fetching Recipient from Sqlite: ");
        return result;
    }

    public TUser toTUser(HashMap<String, String> params) {
        TUser user = null;
        if(params != null && params.size() > 0){
            user = new TUser();
            user.setFirstname(params.get(KEY_FIRSTNAME));
            user.setLastname(params.get(KEY_LASTNAME));
            user.setEmail(params.get(KEY_EMAIL));
            user.setPhone(params.get(KEY_PHONE));
            user.setRole(params.get(KEY_ROLE));
            user.setGender(params.get(KEY_GENDER));
            user.setSimc(params.get(KEY_SIMC));
            user.setNik(params.get(KEY_NIK));
            user.setApi_key(params.get(KEY_API));
            //user.setActive(Boolean.parseBoolean(params.get(KEY_ACTIVE)));
            //user.setApproved(Boolean.parseBoolean(params.get(KEY_APPROVED)));
            Address addr = new Address();
            addr.setAlamat(params.get(KEY_CITY));
            addr.setKecamatan(params.get(KEY_KECAMATAN));
            addr.setCity(new City(params.get(KEY_CITY), params.get(KEY_CITYTEXT)));
            user.setAddress(addr);
        }
        return user;
    }

    public TUser getUser() {
        return toTUser(getUserDetails());
    }

    public void updateUserCityP(String phone, String city) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PHONE, phone); // Email
        values.put(KEY_CITY, city);

        String where = KEY_PHONE+" = ?";
        String[] whereArgs = {phone};
        long id = db.update(TABLE_USER, values, where, whereArgs);
        db.close(); // Closing database connection

        Log.d(TAG, "User data updated into sqlite: " + id);
    }


    public TUser getUserAddress(String phone) {
        TUser r = new TUser();
        String selectQuery = "SELECT  * FROM " + TABLE_USER_ADDRESS + " WHERE phone = '"+phone+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            r = new TUser();

            r.setFirstname(cursor.getString(2));
            r.setLastname(cursor.getString(3));
            r.setEmail(cursor.getString(4));
            r.setPhone(cursor.getString(5));
            r.setRole(cursor.getString(6));
            r.setGender(cursor.getString(7));
            r.setNik(cursor.getString(8));
            r.setSimc(cursor.getString(9));
            r.setApi_key(cursor.getString(10));
            r.setActive(Boolean.parseBoolean( cursor.getString(11) ));
            r.setApproved(Boolean.parseBoolean( cursor.getString(12) ));
            r.setCreated_at(cursor.getString(13));

            Address addr = new Address();
            addr.setAlamat(cursor.getString(16));
            addr.setCity(new City(cursor.getString(20), cursor.getString(20)));
            addr.setLocation(new LatLng(cursor.getDouble(14), cursor.getDouble(15)));
            addr.setRt(cursor.getString(16));
            addr.setRw(cursor.getString(17));
            addr.setDusun(cursor.getString(18));
            addr.setDesa(cursor.getString(20));
            addr.setKecamatan(cursor.getString(21));
            addr.setKabupaten(cursor.getString(22));
            addr.setPropinsi(cursor.getString(23));
            addr.setNegara(cursor.getString(24));
            addr.setNotes(cursor.getString(26));
            r.setAddress(addr);
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Fetching Recipient from Sqlite: ");
        return r;
    }

}