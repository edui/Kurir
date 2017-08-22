package id.co.kurindo.kurindo.wizard.docar;

/**
 * Created by aspire on 3/26/2017.
 */

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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.adapter.RentalViewAdapter;
import id.co.kurindo.kurindo.adapter.TUserAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.base.RecyclerItemClickListener;
import id.co.kurindo.kurindo.comp.ProgressDialogCustom;
import id.co.kurindo.kurindo.helper.DoCarHelper;
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

public class DoCarForm6 extends BaseStepFragment implements Step {
    private static final String TAG = "DoCarForm6";
    VerificationError invalid = null;

    Context context;
    ProgressDialog progressDialog;
    int hour;
    int minute;

    @Bind(R.id.input_telepon_pengirim)
    PhoneInputLayout phoneInput;
    @Bind(R.id.input_nama_pengirim)
    EditText inputnamaPengirim;

    @Bind(R.id.tvPickupTime)
    TextView tvPickupTime;

    @Bind(R.id.list)
    RecyclerView list;
    RentalViewAdapter rentalViewAdapter;

    @Bind(R.id.rg_cara_bayar)
    RadioGroup rgCaraBayar;
    @Bind(R.id.tvTotalPrice)
    TextView tvTotalPrice;

    @Bind(R.id.tvOrigin)
    protected TextView tvOrigin;
    @Bind(R.id.ivAddOriginNotes)
    protected ImageView ivAddOriginNotes;
    @Bind(R.id.etOriginNotes)
    protected EditText etOriginNotes;
    protected TUserAdapter tUserAdapter;
    protected ArrayList<TUser> data = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_docar6);
        context = getContext();
        progressDialog= new ProgressDialogCustom(context);
        //cek_with_calendar_time();
        setup_vehicle();
        return v;
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

        Calendar c = Calendar.getInstance();
        c.setTime(DoCarHelper.getInstance().getRental().getStartDate());
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        Calendar d = Calendar.getInstance();
        d.setTime(DoCarHelper.getInstance().getRental().getEndDate());
        d.set(Calendar.HOUR_OF_DAY, hour);
        d.set(Calendar.MINUTE, minute);

        DoCarHelper.getInstance().getRental().setDateRange(c.getTime(), d.getTime());
        //rentalViewAdapter.notifyDataSetChanged();
        setup_vehicle();
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
    public int getName() {
        return R.string.docar_form;
    }

    @Override
    public VerificationError verifyStep() {
        if(!validate()){
            return new VerificationError("Invalid Data");
        }
        if(tvOrigin.getText().toString().isEmpty()){
            return new VerificationError("Tentukan Lokasi penjemputan.");
        }


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

        showConfirmationDialog("Konfirmasi Pesanan","Konfirmasi, Data yang Anda masukkan sudah benar?\nAnda akan menggunakan layanan "+ AppConfig.KEY_DOCAR+". ", YesClickListener, NoClickListener);

        // loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) { }

        return invalid;
    }

    private void submit_order() {

    }

    private void place_an_order(Handler handler) {
        LogUtil.logD(TAG, "place_an_order");

        progressDialog.setMessage("Sedang memproses Pesanan....");
        progressDialog.show();

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting(); builder.serializeNulls();
        builder.excludeFieldsWithoutExposeAnnotation();

        Type latLng = new TypeToken<LatLng>() {}.getType();
        JsonSerializer<LatLng> serializer = new LatLngSerializer();
        builder.registerTypeAdapter(latLng, serializer);

        Gson gson = builder.create();

        Map<String, String> params = new HashMap<>();
        params.put("user_agent", AppConfig.USER_AGENT);
        DoCarRental rental = DoCarHelper.getInstance().getRental();
        rental.getUser().setFirstname(inputnamaPengirim.getText().toString());
        rental.getUser().setPhone(phoneInput.getPhoneNumber());
        rental.getUser().getAddress().setNotes(etOriginNotes.getText().toString());
        TOrder order = DoCarHelper.getInstance().addDoCarOrder(AppConfig.PACKET_NDS, AppConfig.KEY_RENTAL, rental.getCalculatePrice(context, rental.getVehicle()).doubleValue());
        order.setDocar(rental);
        order.setPlace(rental.getUser());
        order.setPickup(rental.getStart_date());

        String orderStr = gson.toJson(order);
        LogUtil.logD(TAG, "place_an_order: "+orderStr);
        params.put("order", orderStr);
        process_order(params, handler);
    }
    private void process_order(Map<String, String> params, final Handler handler) {
        String url = AppConfig.URL_DOSEND_ORDER;
        addRequest("request_docar_order", Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, "request_docar_order Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean OK = "OK".equalsIgnoreCase(jObj.getString("status"));
                    if(OK){
                        String awb = jObj.getString("awb");
                        String status = jObj.getString("order_status");
                        String statusText = jObj.getString("order_status_text");

                        DoCarHelper.getInstance().getOrder().setAwb(awb);
                        DoCarHelper.getInstance().getOrder().setStatus(status);
                        DoCarHelper.getInstance().getOrder().setStatusText(statusText);

                        ViewHelper.getInstance().setOrder(DoCarHelper.getInstance().getOrder());

                        invalid = null;
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    invalid = new VerificationError("Json error: " + e.getMessage());
                }
                progressDialog.dismiss();
                handler.handleMessage(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                invalid = new VerificationError("NetworkError : " + volleyError.getMessage());
                progressDialog.dismiss();
                handler.handleMessage(null);
            }
        }, params, getKurindoHeaders());
    }


    private boolean validate() {
        boolean valid = true;
        if(phoneInput.isValid()){
           phoneInput.setError(null);
        }else{
            phoneInput.setError("Nomor Telepon / WA tidak valid");
            valid = false;
        }

        if(inputnamaPengirim.getText().toString().isEmpty()){
            inputnamaPengirim.setError("Masukkan nama");
            valid = false;
        }else{
            inputnamaPengirim.setError(null);
        }

        return valid;
    }

    @Override
    public void onSelected() {
        setup_user_info();
        setup_cara_bayar();
    }

    private void setup_cara_bayar() {
        rgCaraBayar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String cara_bayar = getString(R.string.label_tunai);
                switch (checkedId){
                    case R.id.radio_cash:
                        cara_bayar = getString(R.string.label_tunai);
                        break;
                    case R.id.radio_transfer_dp:
                        cara_bayar = getString(R.string.label_transfer_uangmuka);
                        break;
                    case R.id.radio_transfer_semua:
                        cara_bayar = getString(R.string.label_transfer_semua);
                        break;
                }
                DoCarHelper.getInstance().getRental().setPayment(cara_bayar);
            }
        });
        rgCaraBayar.check(R.id.radio_cash);
    }

    private void setup_vehicle() {
        List<DoCarRental> datas = new ArrayList<>();
        datas.add(DoCarHelper.getInstance().getRental());
        rentalViewAdapter=  new RentalViewAdapter(context, datas, null);
        list.setAdapter(rentalViewAdapter);
        list.setLayoutManager(new GridLayoutManager(context, 1));
        list.setHasFixedSize(true);
    }

    private void setup_user_info() {
        phoneInput.setDefaultCountry(AppConfig.DEFAULT_COUNTRY);
        phoneInput.setHint(R.string.telepon);
        DoCarRental rental = DoCarHelper.getInstance().getRental();
        tvTotalPrice.setText(AppConfig.formatCurrency( rental == null ? 0 : rental.getCalculatePrice(context, rental.getVehicle()).doubleValue()));
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == AppConfig.PICKUP_LOCATION) {
            if (resultCode == RESULT_OK) {
                TUser origin = ViewHelper.getInstance().getTUser();
                if (origin != null && origin.getAddress() != null) {
                    DoCarRental rental  = DoCarHelper.getInstance().getRental();
                    if(in_coverageArea(origin, rental)){
                        tvOrigin.setText(origin.getAddress().toStringFormatted());
                        DoCarHelper.getInstance().getRental().setUser(origin);
                        if(origin.getAddress().getNotes() != null && !origin.getAddress().getNotes().isEmpty()){
                            etOriginNotes.setText(origin.getAddress().getNotes());
                            etOriginNotes.setVisibility(View.VISIBLE);
                        }else{
                            etOriginNotes.setText("");
                            etOriginNotes.setVisibility(View.GONE);
                        }
                    }else{
                        Toast.makeText(context, "Lokasi anda di luar jangkauan mobil ini.", Toast.LENGTH_SHORT).show();
                        DoCarHelper.getInstance().getRental().setUser(null);
                        tvOrigin.setText("Set Lokasi Penjemputan");
                        etOriginNotes.setText("");
                        etOriginNotes.setVisibility(View.GONE);
                    }
                    //add to
                    ViewHelper.getInstance().setTUser(null);
                }

            }
        }
    }

    private boolean in_coverageArea(TUser origin, DoCarRental rental) {
        boolean inrange = false;
        inrange = inrange  || (origin.getAddress().getKecamatan().equalsIgnoreCase(rental.getVehicle().getKota()));
        inrange = inrange  || (origin.getAddress().getKabupaten().equalsIgnoreCase(rental.getVehicle().getKota()));
        inrange = inrange  || (origin.getAddress().getPropinsi().equalsIgnoreCase(rental.getVehicle().getKota()));
        return inrange;
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
                DoCarHelper.getInstance().getRental().setUser(p);
                tvOrigin.setText(p.getAddress().toStringFormatted());
                if(p.getAddress().getNotes() != null && !p.getAddress().getNotes().isEmpty()) {
                    etOriginNotes.setText(p.getAddress().getNotes());
                    etOriginNotes.setVisibility(View.VISIBLE);
                    DoCarHelper.getInstance().getRental().getUser().getAddress().setNotes(p.getAddress().getNotes());
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