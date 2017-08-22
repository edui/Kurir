package id.co.kurindo.kurindo.wizard.dohijamah;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.lamudi.phonefield.PhoneInputLayout;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.adapter.TUserAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.base.RecyclerItemClickListener;
import id.co.kurindo.kurindo.comp.ProgressDialogCustom;
import id.co.kurindo.kurindo.helper.DoCarHelper;
import id.co.kurindo.kurindo.helper.DoHijamahHelper;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.map.PickAnAddressActivity;
import id.co.kurindo.kurindo.model.DoCarRental;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.util.LatLngSerializer;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

import static android.app.Activity.RESULT_OK;

/**
 * Created by dwim on 3/15/2017.
 */

public class DoHijamahForm1 extends BaseStepFragment implements Step {
    private static final String TAG = "DoHijamahForm1";
    VerificationError invalid = null;
    Context context;
    ProgressDialog progressBar;

    @Bind(R.id.pilihLayanan)
    Spinner pilihLayanan;
    @Bind(R.id.pilihLokasi)
    Spinner pilihLokasi;

    @Bind(R.id.quantityStr)
    TextView quantityStr;
    @Bind(R.id.incrementBtn)
    AppCompatButton incrementBtn;
    @Bind(R.id.decrementBtn) AppCompatButton decrementBtn;

    @Bind(R.id.tvTotalPrice)
    TextView tvTotalPrice;


    @Bind(R.id.input_telepon_pengirim)
    PhoneInputLayout phoneInput;
    @Bind(R.id.input_nama_pengirim)
    EditText inputnamaPengirim;
    @Bind(R.id.tvOrigin)
    protected TextView tvOrigin;
    @Bind(R.id.ivAddOriginNotes)
    protected ImageView ivAddOriginNotes;
    @Bind(R.id.etOriginNotes)
    protected EditText etOriginNotes;
    protected TUserAdapter tUserAdapter;
    protected ArrayList<TUser> data = new ArrayList<>();

    @Bind(R.id.tvPickupTime)
    TextView tvPickupTime;
    int hour;
    int minute;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        progressBar = new ProgressDialogCustom(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_dohijamah1);

        DoHijamahHelper.getInstance().addDoHijamahOrder(AppConfig.PACKET_NDS, null, 0);

        phoneInput.setDefaultCountry(AppConfig.DEFAULT_COUNTRY);
        phoneInput.setHint(R.string.telepon);
        cek_with_calendar_time();

        return v;
    }
    private void setup_spinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.dohijamah_layanan_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pilihLayanan.setAdapter(adapter);
        pilihLayanan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),R.array.doservice_lokasi_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pilihLokasi.setAdapter(adapter2);

    }

    private void setup_button() {
        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increment(v);
                calculate_price();}
        });
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrement(v);
                calculate_price();
            }
        });

    }

    private void calculate_price() {

    }


    public void increment(View view){
        int q = 0;
        try{ q = Integer.parseInt(quantityStr.getText().toString());}catch (Exception e){};
        q++;
        quantityStr.setText(""+q);
    }
    public void decrement(View view){
        int q = 0;
        try{ q = Integer.parseInt(quantityStr.getText().toString());}catch (Exception e){};
        q--;
        if(q < 0) q =0;
        quantityStr.setText(""+q);
    }


    private void cek_with_calendar_time() {
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        check_time(hour, minute);
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
            /*if(selectedHour < AppConfig.START_SDS || selectedHour > AppConfig.END_ENS){
                Toast.makeText(context, "Jam pelayanan antara "+AppConfig.START_SDS +" - "+AppConfig.END_ENS, Toast.LENGTH_SHORT).show();
                return;
            }*/
            check_time(selectedHour, selectedMinute);
        }
    };

    private void check_time(int selectedHour, int selectedMinute) {
        //Calendar c = Calendar.getInstance();
        hour = selectedHour;
        minute = selectedMinute;
        tvPickupTime.setText(AppConfig.pad(hour)+":"+AppConfig.pad(minute));
        DoHijamahHelper.getInstance().getOrder().setPickup(AppConfig.formatPickup(hour, minute, AppConfig.PACKET_NDS));
    }

    @OnClick(R.id.tvPickupTime)
    public void onClick_tvPickupTime(){
        TimePickerDialog d = new TimePickerDialog(context, timePickerListener, hour, minute, true);
        d.show();
    }

    @OnClick(R.id.ivAddOriginIcon)
    public void onClick_IconOrigin(){
        showPopupWindow("Daftar Lokasi", R.drawable.origin_pin);
    }
    @OnClick(R.id.ivAddOriginNotes)
    public void onClick_ivAddOriginNotes(){
        etOriginNotes.setVisibility(etOriginNotes.isShown()? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.tvOrigin)
    public void onClick_tvOrigin(){
        Intent intent = new Intent(context, PickAnAddressActivity.class);
        intent.putExtra("type", AppConfig.PICKUP_LOCATION);
        intent.putExtra("id", ""+1);
        startActivityForResult(intent, AppConfig.PICKUP_LOCATION);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == AppConfig.PICKUP_LOCATION) {
            if (resultCode == RESULT_OK) {
                TUser origin = ViewHelper.getInstance().getTUser();
                if (origin != null && origin.getAddress() != null) {
                    DoHijamahHelper.getInstance().getOrder().setPlace(origin);
                    tvOrigin.setText(origin.getAddress().toStringFormatted());
                    if(origin.getAddress().getNotes() != null && !origin.getAddress().getNotes().isEmpty()){
                        etOriginNotes.setText(origin.getAddress().getNotes());
                        etOriginNotes.setVisibility(View.VISIBLE);
                    }else{
                        etOriginNotes.setText("");
                        etOriginNotes.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

                @Override
    public int getName() {
        return R.string.dohijamah_form;
    }

    @Override
    public VerificationError verifyStep() {
        if(pilihLayanan.getSelectedItem() == null || pilihLayanan.getSelectedItem().toString().isEmpty() || pilihLayanan.getSelectedItem().toString().equalsIgnoreCase("-Pilih-")){
            return new VerificationError("Layanan belum dipilih.");
        }
        int q = 0;
        try{ q = Integer.parseInt(quantityStr.getText().toString());}catch (Exception e){};
        if(q == 0) return new VerificationError("Jumlah pasien belum ditentukan.");

        if(pilihLokasi.getSelectedItem() == null || pilihLokasi.getSelectedItem().toString().isEmpty() || pilihLokasi.getSelectedItem().toString().equalsIgnoreCase("-Pilih-")){
            return new VerificationError("Lokasi belum dipilih.");
        }else if(!phoneInput.isValid()){
            return new VerificationError("Telepon /WA tidak valid.");
        }else if(DoHijamahHelper.getInstance().getOrder().getPlace() == null || DoHijamahHelper.getInstance().getOrder().getPlace().getAddress()==null){
            return new VerificationError("Lokasi belum diset.");
        }

        handle_order();
        return null;
    }

    private void handle_order() {

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException("RuntimeException");
            }
        };

        DialogInterface.OnClickListener YesClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                place_an_order(handler);
            }
        };
        DialogInterface.OnClickListener NoClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.handleMessage(null);
            }
        };

        showConfirmationDialog("Konfirmasi Pesanan","Anda akan menggunakan layanan "+ AppConfig.KEY_DOHIJAMAH+". ", YesClickListener, NoClickListener);

        // loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) { }

    }

    private void place_an_order(Handler handler) {
        LogUtil.logD(TAG, "place_an_order");

        progressBar.setMessage("Sedang memproses Pesanan....");
        progressBar.show();

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting(); builder.serializeNulls();
        builder.excludeFieldsWithoutExposeAnnotation();

        Type latLng = new TypeToken<LatLng>() {}.getType();
        JsonSerializer<LatLng> serializer = new LatLngSerializer();
        builder.registerTypeAdapter(latLng, serializer);

        Gson gson = builder.create();

        Map<String, String> params = new HashMap<>();
        params.put("user_agent", AppConfig.USER_AGENT);

        TOrder order = DoHijamahHelper.getInstance().getOrder();
        order.getPlace().setFirstname(inputnamaPengirim.getText().toString());
        order.getPlace().setPhone(phoneInput.getPhoneNumber());
        order.getPlace().getAddress().setNotes(etOriginNotes.getText().toString());

        String orderStr = gson.toJson(order);
        LogUtil.logD(TAG, "place_an_order: "+orderStr);
        params.put("order", orderStr);
        process_order(params, handler);
    }
    private void process_order(Map<String, String> params, final Handler handler) {
        String url = AppConfig.URL_DOSEND_ORDER;
        addRequest("request_dohijamah_order", Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, "request_dohijamah_order Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean OK = "OK".equalsIgnoreCase(jObj.getString("status"));
                    if(OK){
                        String awb = jObj.getString("awb");
                        String status = jObj.getString("order_status");
                        String statusText = jObj.getString("order_status_text");

                        DoHijamahHelper.getInstance().getOrder().setAwb(awb);
                        DoHijamahHelper.getInstance().getOrder().setStatus(status);
                        DoHijamahHelper.getInstance().getOrder().setStatusText(statusText);

                        ViewHelper.getInstance().setOrder(DoHijamahHelper.getInstance().getOrder());

                        invalid = null;
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    invalid = new VerificationError("Json error: " + e.getMessage());
                }
                progressBar.dismiss();
                handler.handleMessage(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                invalid = new VerificationError("NetworkError : " + volleyError.getMessage());
                progressBar.dismiss();
                handler.handleMessage(null);
            }
        }, params, getKurindoHeaders());
    }


    @Override
    public void onSelected() {
        setup_spinner();
        setup_button();
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    protected void showPopupWindow(String title, int imageResourceId) {
        data.clear();
        data.addAll(db.getAddressList());

        // Create custom dialog object
        final Dialog dialog = new Dialog(getContext());
        // Include dialog.xml file
        dialog.setContentView(R.layout.popup_list);
        // Set dialog title
        dialog.setTitle("Popup Dialog");

        RecyclerView list = (RecyclerView) dialog.findViewById(R.id.popupList);
        list.setLayoutManager(new GridLayoutManager(context, 1));
        list.setHasFixedSize(true);
        list.setAdapter(tUserAdapter);
        list.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TUser p = data.get(position);
                DoHijamahHelper.getInstance().getOrder().setPlace(p);
                tvOrigin.setText(p.getAddress().toStringFormatted());
                if(p.getAddress().getNotes() != null && !p.getAddress().getNotes().isEmpty()) {
                    etOriginNotes.setText(p.getAddress().getNotes());
                    etOriginNotes.setVisibility(View.VISIBLE);
                    DoHijamahHelper.getInstance().getOrder().getPlace().getAddress().setNotes(p.getAddress().getNotes());
                }
                dialog.dismiss();
            }
        }));

        // set values for custom dialog components - text, image and button
        //TextView text = (TextView) dialog.findViewById(R.id.textDialog);
        //text.setText(content);
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
}
