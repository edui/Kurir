package id.co.kurindo.kurindo.base;

import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.helper.SQLiteHandler;
import id.co.kurindo.kurindo.helper.SessionManager;
import id.co.kurindo.kurindo.util.LogUtil;

import static id.co.kurindo.kurindo.util.LogUtil.makeLogTag;

/**
 * The base class for all fragment classes.
 *
 */
public class BaseFragment extends Fragment {

    private static final String TAG = makeLogTag(BaseFragment.class);
    protected SQLiteHandler db;
    protected SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new SQLiteHandler(getActivity());
        session = new SessionManager(getActivity());
    }

    /**
     * Inflates the layout and binds the view via ButterKnife.
     * @param inflater the inflater
     * @param container the layout container
     * @param layout the layout resource
     * @return the inflated view
     */
    public View inflateAndBind(LayoutInflater inflater, ViewGroup container, int layout) {
        View view = inflater.inflate(layout, container, false);
        ButterKnife.bind(this, view);

        //LogUtil.logD(TAG, ">>> view inflated");
        return view;
    }

    protected void showErrorDialog(String title, String message){
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());

        // set title
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        // create alert dialog
        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    protected void showConfirmationDialog(String title, String message, DialogInterface.OnClickListener YesListener, DialogInterface.OnClickListener NoListener){
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(getContext());

        // set title
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, YesListener)
                .setNegativeButton(R.string.no, NoListener);

        // create alert dialog
        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    protected void showPopupWindow(String title, String content, int imageResourceId) {

        // Create custom dialog object
        final Dialog dialog = new Dialog(getActivity());
        // Include dialog.xml file
        dialog.setContentView(R.layout.popup);
        // Set dialog title
        dialog.setTitle("Popup Dialog");

        // set values for custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.textDialog);
        text.setText(content);
        ImageView image = (ImageView) dialog.findViewById(R.id.imageDialog);
        if(imageResourceId == 0) imageResourceId  = R.drawable.icon_syarat_ketentuan;
        image.setImageResource(imageResourceId);
        TextView textTitleDialog = (TextView) dialog.findViewById(R.id.textTitleDialog);
        if(title != null) textTitleDialog.setText(title);

        dialog.show();

        ImageButton declineButton = (ImageButton) dialog.findViewById(R.id.btncancelcat);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });
    }
    protected void showPopupWindow(String title, int fileResourceId, int imageResourceId) {

        // Create custom dialog object
        final Dialog dialog = new Dialog(getActivity());
        // Include dialog.xml file
        dialog.setContentView(R.layout.popup);
        // Set dialog title
        dialog.setTitle("Popup Dialog");

        // set values for custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.textDialog);
        String TextBuffer = readFile(fileResourceId);
        text.setText(TextBuffer);
        ImageView image = (ImageView) dialog.findViewById(R.id.imageDialog);
        if(imageResourceId == 0) imageResourceId  = R.drawable.icon_syarat_ketentuan;
        image.setImageResource(imageResourceId);
        TextView textTitleDialog = (TextView) dialog.findViewById(R.id.textTitleDialog);
        if(title != null) textTitleDialog.setText(title);

        dialog.show();

        ImageButton declineButton = (ImageButton) dialog.findViewById(R.id.btncancelcat);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });

    }

    private String readFile(int fileResourceId) {
        String TextBuffer ="";
        try {
            //File myFile = new File("/sdcard/filename.txt");
            //FileInputStream iStr = new FileInputStream(myFile);
            if(fileResourceId == 0) fileResourceId = R.raw.snk_file;
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(getContext().getResources().openRawResource(fileResourceId) ));
            String TextLine= "";
            while ((TextLine= fileReader.readLine()) != null) {
                TextBuffer += TextLine+ "\n";
            }
            //viewText.setText(Html.fromHtml(TextBuffer));
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return TextBuffer;
    }

    public void addRequest(final String tag_string_req, int method, String url, Response.Listener responseListener, Response.ErrorListener errorListener, final Map<String, String> params, final Map<String, String> headers){
        final StringRequest strReq = new StringRequest(method,url, responseListener, errorListener){
            protected Map<String, String> getParams() throws AuthFailureError {
                if(params == null) return super.getParams();
                return params;
            }
            public Map<String, String> getHeaders() throws AuthFailureError{
                if(headers == null) return super.getHeaders();
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public Map<String,String> getKurindoHeaders() {
        Map<String, String> headers = null;
        String api = db.getUserApi();
        if(api != null && !api.isEmpty()){
            headers = new HashMap<>();
            headers.put("Api", api);
            headers.put("Authorization", api);
        }
        return headers;
    }

}
