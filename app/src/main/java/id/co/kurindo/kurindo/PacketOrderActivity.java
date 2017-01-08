package id.co.kurindo.kurindo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.co.kurindo.kurindo.adapter.CityAdapter;
import id.co.kurindo.kurindo.adapter.NewsAdapter;
import id.co.kurindo.kurindo.adapter.PacketServiceAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.base.KurindoActivity;
import id.co.kurindo.kurindo.helper.CheckoutHelper;
import id.co.kurindo.kurindo.helper.SQLiteHandler;
import id.co.kurindo.kurindo.helper.SessionManager;
import id.co.kurindo.kurindo.model.City;
import id.co.kurindo.kurindo.model.Packet;
import id.co.kurindo.kurindo.model.PacketService;
import id.co.kurindo.kurindo.model.Product;
import id.co.kurindo.kurindo.model.User;

/**
 * Created by DwiM on 11/6/2016.
 */
public class PacketOrderActivity extends KurindoActivity {
    private static final String TAG = "PacketOrderActivity";

    @Override
    public Bundle getBundleParams() {
        return getIntent().getExtras();
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    @Override
    public Class getFragmentClass() {
        return PacketOrderFragment.class;
    }

}
