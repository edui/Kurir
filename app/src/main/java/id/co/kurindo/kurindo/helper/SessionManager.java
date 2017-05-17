package id.co.kurindo.kurindo.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import id.co.kurindo.kurindo.app.AppConfig;

/**
 * Created by DwiM on 11/8/2016.
 */
public class SessionManager {
        // LogCat tag
        private static String TAG = SessionManager.class.getSimpleName();

        // Shared Preferences
        SharedPreferences pref;

        SharedPreferences.Editor editor;
        Context _context;

        // Shared pref mode
        int PRIVATE_MODE = 0;

        // Shared preferences file name
        private static final String PREF_NAME = "KURINDOAppLogin";

        private static final String KEY_IS_AUTO_LOGGEDIN = "isAutoLoggedIn";
        private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
        private static final String KEY_IS_ACTIVATED= "isActivated";
        private static final String KEY_ROLE = "keyRole";
        private static final String KEY_CITY= "keyCity";

        public SessionManager(Context context) {
            this._context = context;
            pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            editor = pref.edit();
        }

        public void setAutoLogin(boolean isLoggedIn) {
            editor.putBoolean(KEY_IS_AUTO_LOGGEDIN, isLoggedIn);
            // commit changes
            editor.commit();
        }

        public void setLogin(boolean isLoggedIn) {
            editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
            // commit changes
            editor.commit();
        }
        public void setActive(boolean isActive) {
            editor.putBoolean(KEY_IS_ACTIVATED, isActive);
            // commit changes
            editor.commit();
        }

        public void setLoginData(String role, String city) {

            editor.putString(KEY_ROLE, role);

            editor.putString(KEY_CITY, city);

            // commit changes
            editor.commit();

            Log.d(TAG, "User login session modified!");
        }

        public void setLogout() {
            setLogin(false);
            setAutoLogin(false);
            setLoginData(null,null);
            setActive(true);
        }
        public boolean isAutoLoggedIn(){
            return pref.getBoolean(KEY_IS_AUTO_LOGGEDIN, false);
        }

        public boolean isActivated(){
            return pref.getBoolean(KEY_IS_ACTIVATED, false);
        }
        public boolean isLoggedIn(){
            return pref.getBoolean(KEY_IS_LOGGEDIN, false);
        }
        public boolean isKurir(){
            String role = pref.getString(KEY_ROLE, "client");
            return (role.equals(AppConfig.KEY_KURIR) || role.equals(AppConfig.KEY_SHOPKURIR) || role.equals(AppConfig.KEY_KURIRSHOP));
        }
        public boolean isShopPic(){
            String role = pref.getString(KEY_ROLE, "client");
            return (role.equals(AppConfig.KEY_SHOPPIC) || role.equals(AppConfig.KEY_SHOPKEC) || role.equals(AppConfig.KEY_SHOPKAB) || role.equals(AppConfig.KEY_SHOPPROV) || role.equals(AppConfig.KEY_SHOPNEG)) ;
        }
        public boolean isAgent(){
            String role = pref.getString(KEY_ROLE, "client");
            return role.equals(AppConfig.KEY_AGENT);
        }
        public boolean isPelanggan(){
            String role = pref.getString(KEY_ROLE, "client");
            return role.equals(AppConfig.KEY_PELANGGAN);
        }
        public boolean isOperator(){
            String role = pref.getString(KEY_ROLE, "client");
            return role.equalsIgnoreCase(AppConfig.KEY_OPERATOR);
        }
    public boolean isAdministrator(){
        String role = pref.getString(KEY_ROLE, "client");
        return (role.equalsIgnoreCase(AppConfig.KEY_ADMINISTRATOR) || role.equalsIgnoreCase(AppConfig.KEY_ADMINKEC) || role.equalsIgnoreCase(AppConfig.KEY_ADMINKAB) || role.equalsIgnoreCase(AppConfig.KEY_ADMINPROV) || role.equalsIgnoreCase(AppConfig.KEY_ADMINNEG));
    }

    public boolean isSuperAdministrator(){
        String role = pref.getString(KEY_ROLE, "client");
        return (role.equalsIgnoreCase(AppConfig.KEY_ADMINISTRATOR));
    }

    public void clear() {
    }
}
