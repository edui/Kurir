package id.co.kurindo.kurindo.util;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;

/**
 * Created by Andreas Schrade on 14.12.2015.
 */
public class LogUtil {
    private static final String LOG_PREFIX = "KurindoLog_";

    private static final boolean LOGGING_ENABLED = true;
    private static final int MAX_TAG_LENGTH = 23;
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();

    public static String makeLogTag(Class clazz) {
        return makeLogTag(clazz.getSimpleName());
    }


    public static String makeLogTag(String str) {
        if (str.length() > MAX_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }

        return LOG_PREFIX + str;
    }


    public static void logD(final String tag, String message) {
        if (LOGGING_ENABLED) {
            //if (Log.isLoggable(tag, Log.DEBUG)) {
                Log.d(tag, message);
            //}
        }
    }

    public static void logD(final String tag, String message, Throwable cause) {
        if (LOGGING_ENABLED) {
            //if (Log.isLoggable(tag, Log.DEBUG)) {
                Log.d(tag, message, cause);
            //}
        }
    }

    public static void logV(final String tag, String message) {
        if (LOGGING_ENABLED) {
            //if (Log.isLoggable(tag, Log.VERBOSE)) {
                Log.v(tag, message);
            //}
        }
    }

    public static void logV(final String tag, String message, Throwable cause) {
        if (LOGGING_ENABLED) {
            //if (Log.isLoggable(tag, Log.VERBOSE)) {
                Log.v(tag, message, cause);
            //}
        }
    }

    public static void logI(final String tag, String message) {
        if (LOGGING_ENABLED) {
            Log.i(tag, message);
        }
    }

    public static void logI(final String tag, String message, Throwable cause) {
        if (LOGGING_ENABLED) {
            Log.i(tag, message, cause);
        }
    }

    public static void logW(final String tag, String message) {
        if (LOGGING_ENABLED) {
            Log.w(tag, message);
        }
    }

    public static void logW(final String tag, String message, Throwable cause) {
        if (LOGGING_ENABLED) {
            Log.w(tag, message, cause);
        }
    }

    public static void logE(final String tag, String message) {
        if (LOGGING_ENABLED) {
            Log.e(tag, message);
        }
    }

    public static void logE(final String tag, String message, Throwable cause) {
        if (LOGGING_ENABLED) {
            Log.e(tag, message, cause);
        }
    }

    public static void logE(String tag, Throwable throwable) {
        if (LOGGING_ENABLED) {
            Log.e(tag, getStackTrace(throwable));
        }
    }
    public static String getStackTrace(Throwable e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return "\r\n" + sw.toString() + "\r\n";
        } catch (Exception e2) {
            return "bad getErrorInfoFromException";
        }
    }
    public static void logToServer(final String tag, final String activity, final String user) {
        String tag_string_req = "req_logToServer_"+tag;
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGGING, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(tag, "logToServer: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                    } else {
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(tag, "logToServer Error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("form-tag", tag);
                params.put("form-activity", activity);
                params.put("form-user", user);
                //params.put("form-token", token);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

        /**
         * Utility class
         */
    private LogUtil() {
    }
}