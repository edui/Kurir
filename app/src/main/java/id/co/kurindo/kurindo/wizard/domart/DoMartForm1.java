package id.co.kurindo.kurindo.wizard.domart;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.adapter.PacketServiceAdapter;
import id.co.kurindo.kurindo.adapter.TUserAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.base.RecyclerItemClickListener;
import id.co.kurindo.kurindo.comp.ProgressDialogCustom;
import id.co.kurindo.kurindo.helper.DoMartHelper;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.map.PickAnAddressActivity;
import id.co.kurindo.kurindo.model.DoMart;
import id.co.kurindo.kurindo.model.PacketService;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPrice;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.util.ParserUtil;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

import static android.app.Activity.RESULT_OK;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static id.co.kurindo.kurindo.util.LogUtil.makeLogTag;

/**
 * Created by dwim on 3/15/2017.
 */

public class DoMartForm1 extends BaseStepFragment implements Step {
    private static final String TAG = makeLogTag(DoMartForm1.class);
    private static final int PICKUP_LOCATION = 1;
    private static final int PICKUP_DESTINATION = 2;

    HashMap<String, View> map = new HashMap<>();

    VerificationError invalid = null;
    ProgressDialog progressBar;
    Context context;
    boolean next = false;
    int item = 0;

    @Bind(R.id.input_service_code)
    protected Spinner _serviceCodeText;
    protected PacketServiceAdapter packetServiceAdapter;

    @Bind(R.id.tvDestination)
    protected TextView tvDestination;
    @Bind(R.id.ivAddDestinationNotes)
    protected ImageView ivAddDestinationNotes;
    @Bind(R.id.etDestinationNotes)
    protected EditText etDestinationNotes;
    //@Bind(R.id.ivAddDestinationIcon)
    //protected ImageView ivIconDestination;
    protected TUserAdapter tUserAdapter;
    protected ArrayList<TUser> data = new ArrayList<>();

    @Bind((R.id.llLayout))
    LinearLayout baseLayout;
    @Bind((R.id.layoutFooter))
    LinearLayout layoutFooter;

    @Bind(R.id.tvTotalPrice)
    TextView tvTotalPrice;
    @Bind(R.id.btnAddItem)
    Button btnAddItem;

    @Bind(R.id.tvPickupTime)
    TextView tvPickupTime;
    @Bind(R.id.tvPickupTimeText)
    TextView tvPickupTimeText;
    @Bind(R.id.tvDropTimeText)
    TextView tvDropTimeText;
    @Bind(R.id.swChooseTime)
    Switch swChooseTime;

    int hour;
    int minute;

    protected String serviceCode = AppConfig.PACKET_SDS;

    BigDecimal total = new BigDecimal(0);
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_domart1);

        context = getContext();
        progressBar = new ProgressDialogCustom(context);

        packetServiceAdapter = new PacketServiceAdapter(context, AppConfig.getPacketServiceList(AppConfig.KEY_DOMART), 1);
        _serviceCodeText.setAdapter(packetServiceAdapter);
        _serviceCodeText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                serviceCode = ((PacketService) parent.getItemAtPosition(position)).getCode();
                cek_with_calendar_time();
                //if (canDrawRoute()) requestprice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        swChooseTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean on = isChecked;
                if(on)
                {
                    tvPickupTimeText.setBackgroundResource(R.color.cardview_light_background);
                    tvDropTimeText.setBackgroundResource(R.color.orange);
                }
                else
                {
                    tvPickupTimeText.setBackgroundResource(R.color.orange);
                    tvDropTimeText.setBackgroundResource(R.color.cardview_light_background);
                }
            }
        });
        swChooseTime.setChecked(true);
        swChooseTime.toggle();
        return v;
    }
    private void cek_with_calendar_time() {
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        cek_time();
    }

    private void cek_time() {
        if(serviceCode != null){
            if(AppConfig.isNightService(serviceCode)){
                if(hour+1 >= AppConfig.START_ENS) {
                    hour++;
                } else if(hour+1 >= AppConfig.END_ENS) {
                    hour = AppConfig.START_SDS +2;
                    _serviceCodeText.setSelection(2); //nds
                } else {
                    hour = AppConfig.START_ENS;
                }
            }else if(serviceCode.equalsIgnoreCase(AppConfig.PACKET_SDS)){
                if(hour + 1 < AppConfig.START_SDS) {
                    hour = AppConfig.START_SDS +2;
                    _serviceCodeText.setSelection(0); //sds
                } else if(hour+1 >= AppConfig.END_ENS) {
                    hour = AppConfig.START_SDS +2;
                    _serviceCodeText.setSelection(2); //nds
                } else if(hour+1 >= AppConfig.START_ENS) {
                    hour++;
                    _serviceCodeText.setSelection(2); //nds
                }else{
                    hour++;
                    _serviceCodeText.setSelection(0); //sds
                }
            }else if(serviceCode.equalsIgnoreCase(AppConfig.PACKET_NDS)){
                if(hour + 1 < AppConfig.START_SDS) {
                    hour = AppConfig.START_SDS +2;
                    _serviceCodeText.setSelection(2); //nds
                } else if(hour+1 >= AppConfig.END_ENS) {
                    hour = AppConfig.START_SDS +2;
                } else if(hour+1 >= AppConfig.START_ENS) {
                    hour = AppConfig.START_SDS +2;
                    //hour++;
                    //_serviceCodeText.setSelection(3);
                }
            }
        }
        tvPickupTime.setText(AppConfig.pad(hour)+":"+AppConfig.pad(minute));
    }


    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
            if(selectedHour < AppConfig.START_SDS || selectedHour > AppConfig.END_ENS){
                Toast.makeText(context, "Jam pelayanan antara "+AppConfig.START_SDS +" - "+AppConfig.END_ENS, Toast.LENGTH_SHORT).show();
                return;
            }
            hour = selectedHour;
            minute = selectedMinute;
            cek_time();
        }
    };
    @OnClick(R.id.tvPickupTime)
    public void onClick_tvPickupTime(){
        TimePickerDialog d = new TimePickerDialog(context, timePickerListener, hour, minute, true);
        d.show();
    }
    @OnClick(R.id.ivAddDestinationIcon)
    public void onClick_IconDestination(){
        showPopupWindow("Daftar Lokasi", R.drawable.destination_pin);
    }
    @OnClick(R.id.ivAddDestinationNotes)
    public void onClick_ivAddDestinationNotes(){
        etDestinationNotes.setVisibility(etDestinationNotes.isShown()? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.tvDestination)
    public void onClick_tvDestination(){
        Intent intent = new Intent(context, PickAnAddressActivity.class);
        intent.putExtra("type", PICKUP_DESTINATION);
        intent.putExtra("id", ""+item);
        startActivityForResult(intent, PICKUP_DESTINATION);
    }
    @OnClick(R.id.btnAddItem)
    public void onClick_btnAddItem(){
        baseLayout.removeView(layoutFooter);
        generateForm(item++);
        baseLayout.addView(layoutFooter);
    }

    public void generateForm(final int count){
        CardView cv = new CardView(getContext());
        LinearLayout ll = new LinearLayout(new ContextThemeWrapper(context, R.style.Widget_CardContent));
        ll.setOrientation(LinearLayout.VERTICAL);
        cv.addView(ll);
        baseLayout.addView(cv);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = 8;
        params.setMargins(margin, margin+2, margin, margin);
        cv.setLayoutParams(params);

        DoMartHelper.getInstance().addOrder(total, AppConfig.KEY_DOMART);
        DoMart s = DoMartHelper.getInstance().addMart();

        ll.addView(generateRemoveButton(cv, s, count));

        ll.addView(generateElementNote(s));
        ll.addView(generateElementEstHarga(s));
        ll.addView(generateElementLokasi(s));
    }

    private View generateElementLokasi(final DoMart data) {
        LinearLayout lin = new LinearLayout(getContext());
        lin.setOrientation(LinearLayout.VERTICAL);
        TextView tv = new TextView(getContext());
        tv.setText("Set Lokasi Toko");
        lin.addView(tv);
        LinearLayout lin2 = new LinearLayout(getContext());
        lin2.setOrientation(LinearLayout.HORIZONTAL);
        lin.addView(lin2);

        final EditText ed = new EditText(getContext());
        ed.setMinLines(1);
        ed.setMaxLines(2);
        ed.setHint("Tambahkan catatan lokasi");
        ed.setVisibility(View.GONE);
        lin.addView(ed);

        final TextView tv2 = new TextView(getContext());
        tv2.setText("Klik untuk Set Lokasi Toko");
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            tv2.setId(item);
        }else{
            tv2.setId(View.generateViewId());
        }
        map.put(""+tv2.getId(), tv2);
        //tv2.setId(item);
        tv2.setMaxLines(2);
        tv2.setMinLines(2);
        tv2.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show pin location
                DoMartHelper.getInstance().setDoMart(data);
                Intent intent = new Intent(context, PickAnAddressActivity.class);
                intent.putExtra("type", PICKUP_LOCATION);
                intent.putExtra("id", ""+v.getId());
                startActivityForResult(intent, PICKUP_LOCATION);
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.weight = 0.9f;
        tv2.setLayoutParams(params);

        ImageButton imgIcon = new ImageButton(getContext());
        imgIcon.setImageResource(R.drawable.origin_pin);
        imgIcon.setBackgroundResource( R.color.white);
        imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow("Add Shop Location", R.drawable.origin_pin);
            }
        });
        LinearLayout.LayoutParams paramsImgIcon = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        paramsImgIcon.weight = 0.05f;
        imgIcon.setLayoutParams(paramsImgIcon);

        ImageButton img = new ImageButton(getContext());
        img.setImageResource(R.drawable.ic_description_black_18dp);
        img.setBackgroundResource( R.color.white);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed.setVisibility(ed.isShown()? View.GONE: View.VISIBLE);
            }
        });
        LinearLayout.LayoutParams paramsImg = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        paramsImg.weight = 0.05f;
        img.setLayoutParams(paramsImg);

        lin2.addView(imgIcon);
        lin2.addView(tv2);
        lin2.addView(img);

        return lin;
    }

    private View generateElementEstHarga(final DoMart data) {
        LinearLayout lin = new LinearLayout(getContext());
        lin.setOrientation(LinearLayout.HORIZONTAL);
        TextView tv = new TextView(getContext());
        tv.setText("Estimasi Harga (Rp)");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        params.weight = 0.6f;
        tv.setLayoutParams(params);
        lin.addView(tv);
        EditText tvNote = new EditText(getContext());
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        params2.weight = 0.4f;
        tvNote.setLayoutParams(params2);
        tvNote.setGravity(Gravity.LEFT);
        tvNote.setInputType(InputType.TYPE_CLASS_NUMBER);
        tvNote.setHint("Berapa Rp estimasi harganya?");
        //tvNote.addTextChangedListener(new NumberTextWatcher(tvNote));
        tvNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            Handler handler = new Handler(Looper.getMainLooper());
            Runnable runnable;
            @Override
            public void afterTextChanged(final Editable s) {
                handler.removeCallbacks(runnable);
                runnable =new Runnable() {
                    @Override
                    public void run() {
                        data.setEstHarga(s.toString());
                        calculate_price();
                    }
                };
                handler.postDelayed(runnable, 800);
            }
        });
        lin.addView(tvNote);
        return lin;
    }

    private View generateElementNote(final DoMart data) {
        LinearLayout lin = new LinearLayout(getContext());
        lin.setOrientation(LinearLayout.VERTICAL);
        TextView tv = new TextView(getContext());
        tv.setText("Daftar Belanja");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        //params.weight = 0.6f;
        tv.setLayoutParams(params);
        lin.addView(tv);
        EditText tvNote = new EditText(getContext());
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        //params2.weight = 0.4f;
        tvNote.setLayoutParams(params2);
        tvNote.setGravity(Gravity.LEFT);
        tvNote.setMinLines(1);
        tvNote.setMaxLines(10);
        tvNote.setHint("Tuliskan detil daftar belanja anda di sini.");
        tvNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            Handler handler = new Handler(Looper.getMainLooper());
            Runnable runnable;
            @Override
            public void afterTextChanged(final Editable s) {
                handler.removeCallbacks(runnable);
                runnable =new Runnable() {
                    @Override
                    public void run() {
                        data.setNotes(s.toString());
                    }
                };
                handler.postDelayed(runnable, 800);
            }
        });
        lin.addView(tvNote);
        return lin;
    }

    private View generateRemoveButton(final CardView cardView, final DoMart data, int count) {
        LinearLayout lin = new LinearLayout(getContext());
        lin.setOrientation(LinearLayout.HORIZONTAL);

        TextView tv0 = new TextView(getContext());
        tv0.setText("Titipan Belanja Toko "+((char) (item+1)));
        tv0.setTypeface(null, Typeface.BOLD);
        tv0.setTextSize(12);
        LinearLayout.LayoutParams params0 = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        //params0.bottomMargin = 10;
        params0.weight = 0.8f;
        tv0.setLayoutParams(params0);

        lin.addView(tv0);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.weight = 0.2f;
        params.setMargins(0,0,0, 20);

        if(count > 0){
            Button btnRemove = new Button(getContext());
            btnRemove.setText("Remove");
            btnRemove.setLayoutParams(params);
            btnRemove.setTextSize(12);
            btnRemove.setTextColor(Color.WHITE);
            btnRemove.setBackgroundResource(R.color.orange);
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeForm(cardView, data);
                }
            });
            lin.addView(btnRemove);
        }else{
            TextView tvNote = new TextView(getContext());
            tvNote.setLayoutParams(params);
            lin.addView(tvNote);
        }
        return lin;
    }

    private void removeForm(final CardView cardView, DoMart data) {
        baseLayout.removeView(cardView);
        //DoServiceHelper.getInstance().removeService(data);
        //calculate_price();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == PICKUP_DESTINATION){
            if(resultCode == RESULT_OK){
                TUser origin = ViewHelper.getInstance().getTUser();
                if(origin != null && origin.getAddress() != null){
                    tvDestination.setText(origin.getAddress().toStringFormatted());
                    DoMartHelper.getInstance().getOrder().setPlace(origin);
                    ViewHelper.getInstance().setTUser(null);
                }
            }
        }else if(requestCode == PICKUP_LOCATION){
            if(resultCode == RESULT_OK){
                TUser origin = ViewHelper.getInstance().getTUser();
                if(origin != null && origin.getAddress() != null){
                    String idStr = ViewHelper.getInstance().getId();
                    int id = item;
                    try {
                        id = Integer.parseInt(idStr);
                    }catch (Exception e){}
                    TextView tv = (TextView) map.get(""+id);
                    if(tv != null) {
                        tv.setText(origin.getAddress().toStringFormatted());
                        DoMart d = DoMartHelper.getInstance().find(DoMartHelper.getInstance().getDoMart());
                        if(d != null){
                            d.setOrigin(origin);
                        }
                        ViewHelper.getInstance().setTUser(null);
                    }
                }

            }
        }
    }

    @Override
    public int getName() {
        return R.string.domart_form;
    }

    @Override
    public VerificationError verifyStep() {
        if(!validate()){
            return invalid;
        }
        TOrder order = DoMartHelper.getInstance().getOrder();
        Calendar start = Calendar.getInstance();
        start.add(Calendar.DATE, 1);
        start.set(Calendar.HOUR_OF_DAY, hour);
        start.set(Calendar.MINUTE, minute);
        if(swChooseTime.isChecked()){
            order.setDroptime(AppConfig.getDateTimeServerFormat().format( start.getTime() ));
        }else{
            order.setPickup(AppConfig.getDateTimeServerFormat().format( start.getTime() ));
        }

        next = true;
        return null;
    }
    @Override
    public void onSelected() {
        if(!next){
            baseLayout.removeView(layoutFooter);
            //baseLayout.removeView(btnAddItem);
            //cek_with_calendar_time();
            DoMartHelper.getInstance().clearOrder();
            generateForm(item++);
            baseLayout.addView(layoutFooter);
            //baseLayout.addView(btnAddItem);
            retrieve_price();
        }
        cek_with_calendar_time();

    }
    private void retrieve_price() {
        final String tag_string_Req = "retrieve_price";
        String url = AppConfig.URL_PRICE_REQUEST;
        final HashMap<String, String> params = new HashMap();
        params.put("do-type", AppConfig.KEY_DOMART);
        addRequest(tag_string_Req, Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, tag_string_Req+" Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    String message = jObj.getString("message");
                    boolean OK = "OK".equalsIgnoreCase(message);
                    if(OK){
                        JSONArray datas = jObj.getJSONArray("data");
                        if(datas != null && datas.length() > 0) {
                            DoMartHelper.getInstance().getPriceMaps().clear();
                            ParserUtil parser = new ParserUtil();
                            for (int i = 0; i < datas.length(); i++) {
                                TPrice price = parser.parserTPrice(datas.getJSONObject(i));
                                DoMartHelper.getInstance().getPriceMaps().put(price.getService_code(), price);
                            }
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    //invalid = new VerificationError("Json error: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        }, params, getKurindoHeaders());
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
    private void calculate_price() {
        TOrder order =  DoMartHelper.getInstance().getOrder();
        Set<DoMart> marts = order.getMarts();
        if(marts != null){
            Object[] datas  = marts.toArray();
            total = new BigDecimal(0);
            for (int i = 0; i < datas.length; i++) {
                DoMart data = (DoMart) datas[i];
                BigDecimal sub = new BigDecimal(data.getEstHarga());
                total = total.add(sub);
                data.setPrice_unit(sub);
                data.setPrice(sub);
            }
            order.setTotalPrice(total);
            order.setTotalQuantity(datas.length);
        }
        tvTotalPrice.setText("TOTAL : "+AppConfig.formatCurrency( total.intValue() ));
        next = false;
    }

    public boolean validate(){
        boolean valid = true;
        if(total.doubleValue() == 0){
            invalid =  new VerificationError("Anda belum mengisi daftar belanja. Harga belum sepakat.");
            valid = false;
            return valid;
        }
        TOrder order =  DoMartHelper.getInstance().getOrder();
        Set<DoMart> marts = order.getMarts();
        if(marts == null) {
            invalid = new VerificationError("Anda belum mengisi daftar belanja.");
            valid = false;
            return valid;
        }

        Object[] datas  = marts.toArray();
        for (int i = 0; i < datas.length; i++) {
            DoMart data = (DoMart) datas[i];
            if(data.getOrigin() == null ) {
                invalid = new VerificationError("Anda belum menentukan "+(i > 0? "salah satu":"")+" lokasi toko.");
                valid = false;
                return valid;
            }else
                if(data.getNotes()==null || data.getNotes().isEmpty()){
                    invalid = new VerificationError("Anda belum mengisi daftar belanja.");
                    valid = false;
                    return valid;
            }
        }
        if(order.getPlace() == null || order.getPlace().getAddress() == null ){
            invalid = new VerificationError("Anda belum menentukan alamat tujuan.");
            valid = false;
            return valid;
        }

        return valid;
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
                    tvDestination.setText(p.getAddress().toStringFormatted());
                    if(p.getAddress().getNotes() != null && !p.getAddress().getNotes().isEmpty()) {
                        etDestinationNotes.setText(p.getAddress().getNotes());
                        etDestinationNotes.setVisibility(View.VISIBLE);
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
