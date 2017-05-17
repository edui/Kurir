package id.co.kurindo.kurindo.comp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;

import id.co.kurindo.kurindo.R;

/**
 * Created by DwiM on 4/16/2017.
 */

public class ProgressDialogCustom extends ProgressDialog {
    public ProgressDialogCustom(Context context) {
        super(context, getStyle());
        setMessage("Loading. Please Wait ...");
        setCancelable(false);
    }

    private static int getStyle() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            return R.style.AppAlertTheme;
        } else {
            return R.style.AppAlertTheme19;
        }
    }
}