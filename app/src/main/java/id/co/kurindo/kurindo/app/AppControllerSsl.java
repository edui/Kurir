package id.co.kurindo.kurindo.app;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.io.InputStream;
import java.security.KeyStore;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.model.News;

/**
 * Created by DwiM on 11/8/2016.
 * Ref:
 * http://blog.applegrew.com/2015/04/using-pinned-self-signed-ssl-certificate-with-android-volley/
 * https://www.codeproject.com/articles/826045/android-security-implementation-of-self-signed-ssl
 * http://ogrelab.ikratko.com/using-android-volley-with-self-signed-certificate/
 */
public class AppControllerSsl extends AppController {

    public static final String TAG = AppControllerSsl.class.getSimpleName();

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack(null, newSslSocketFactory()));
        }
        return mRequestQueue;
    }

    private SSLSocketFactory newSslSocketFactory() {
        try {
            // Get an instance of the Bouncy Castle KeyStore format
            KeyStore trusted = KeyStore.getInstance("BKS");
            // Get the raw resource, which contains the keystore with
            // your trusted certificates (root and any intermediate certs)
            InputStream in = getApplicationContext().getResources().openRawResource(R.raw.keystore);
            try {
                // Initialize the keystore with the provided trusted certificates
                // Provide the password of the keystore

                trusted.load(in, "KEYSTORE_PASSWORD".toCharArray());
            } finally {
                in.close();
            }

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(trusted);

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            SSLSocketFactory sf = context.getSocketFactory();
            return sf;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

}