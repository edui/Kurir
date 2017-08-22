package id.co.kurindo.kurindo.wizard.dosend;

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
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.lamudi.phonefield.PhoneInputLayout;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.LoginActivity;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.comp.ProgressDialogCustom;
import id.co.kurindo.kurindo.helper.DoSendHelper;
import id.co.kurindo.kurindo.helper.SessionManager;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.map.PickAnAddressActivity;
import id.co.kurindo.kurindo.model.TPacket;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

import static android.app.Activity.RESULT_OK;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by dwim on 2/7/2017.
 */

public class DoSendMultiFormFragment extends BaseStepFragment implements Step {
    private static final String TAG = "DoSendFormFragment";
    VerificationError invalid = null;
    private static final int PICKUP_LOCATION = 1;
    private static final int PICKUP_DESTINATION = 2;

    Context context;
    String doType = AppConfig.KEY_DOSEND;
    ProgressDialog progressBar;


    @Bind(R.id.input_telepon_pengirim)
    PhoneInputLayout phonePengirimInput;
    @Bind(R.id.input_nama_pengirim)
    EditText inputnamaPengirim;
    @Bind(R.id.tvOrigin)
    protected TextView tvOrigin;
    @Bind(R.id.ivAddOriginNotes)
    protected ImageView ivAddOriginNotes;
    @Bind(R.id.etOriginNotes)
    protected EditText etOriginNotes;

    @Bind((R.id.llLayout))
    LinearLayout baseLayout;
    @Bind((R.id.layoutDestination))
    LinearLayout layoutDestination;

    @Bind(R.id.tvTotalPrice)
    TextView tvTotalPrice;
    @Bind(R.id.btnAddItem)
    Button btnAddItem;

    int item = 0;
    BigDecimal total = new BigDecimal(0);
    HashMap<String, View> map = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        session = new SessionManager(context);
        if(!session.isLoggedIn()){
            ((BaseActivity)getActivity()).showActivity(LoginActivity.class);
            getActivity().finish();
            return ;
        }
        Bundle bundle = getArguments();
        if(bundle != null){
            String d = bundle.getString("doType");
            if(d != null) doType = d;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_dosend_form_multi);
        progressBar = new ProgressDialogCustom(context);
        phonePengirimInput.setDefaultCountry(AppConfig.DEFAULT_COUNTRY);
        phonePengirimInput.setHint(R.string.telepon_pengirim);

        return v;
    }



    @OnClick(R.id.ivAddOriginIcon)
    public void onClick_IconOrigin(){
        showPopupWindow("Daftar Lokasi", R.drawable.destination_pin);
    }
    @OnClick(R.id.ivAddOriginNotes)
    public void onClick_ivAddOriginNotes(){
        etOriginNotes.setVisibility(etOriginNotes.isShown()? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.tvOrigin)
    public void onClick_tvOrigin(){
        Intent intent = new Intent(context, PickAnAddressActivity.class);
        intent.putExtra("type", PICKUP_LOCATION);
        intent.putExtra("id", ""+item);
        startActivityForResult(intent, PICKUP_LOCATION);
    }

    @OnClick(R.id.btnAddItem)
    public void onClick_btnAddItem(){
        layoutDestination.removeView(btnAddItem);
        generateForm(item++);
        layoutDestination.addView(btnAddItem);
    }

    public void generateForm(final int count){
        CardView cv = new CardView(context);
        LinearLayout ll = new LinearLayout(new ContextThemeWrapper(context, R.style.Widget_CardContent));
        ll.setOrientation(LinearLayout.VERTICAL);
        cv.addView(ll);
        layoutDestination.addView(cv);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = 8;
        params.setMargins(margin, margin+2, margin, margin);
        cv.setLayoutParams(params);

        DoSendHelper.getInstance().addDoSendOrder(AppConfig.CASH_PAYMENT, AppConfig.PACKET_SDS, "1", total.doubleValue());
        TUser s = DoSendHelper.getInstance().addDestination();

        ll.addView(generateRemoveButton(cv, s, count));
        ll.addView(generateElementLokasi(s));
        //ll.addView(generateElementPickup(s));
    }


    private View generateRemoveButton(final CardView cardView, final TUser data, int count) {
        LinearLayout lin = new LinearLayout(context);
        lin.setOrientation(LinearLayout.HORIZONTAL);

        TextView tv0 = new TextView(context);
        tv0.setText("Alamat Tujuan "+((char) (item+1)));
        tv0.setTypeface(null, Typeface.BOLD);
        tv0.setTextSize(11);
        LinearLayout.LayoutParams params0 = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        //params0.bottomMargin = 10;
        params0.weight = 0.8f;
        tv0.setLayoutParams(params0);

        lin.addView(tv0);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.weight = 0.2f;
        params.setMargins(0,0,0, 20);

        if(count > 0){
            Button btnRemove = new Button(context);
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
            TextView tvNote = new TextView(context);
            tvNote.setLayoutParams(params);
            lin.addView(tvNote);
        }
        return lin;
    }
    private void removeForm(final CardView cardView, TUser data) {
        layoutDestination.removeView(cardView);
    }

    private View generateElementLokasi(final TUser data) {
        LinearLayout lin = new LinearLayout(context);
        lin.setOrientation(LinearLayout.VERTICAL);
        TextView tv = new TextView(context);
        tv.setText("Set Tujuan Anda");
        lin.addView(tv);
        LinearLayout lin2 = new LinearLayout(context);
        lin2.setOrientation(LinearLayout.HORIZONTAL);
        lin.addView(lin2);

        final EditText ed1 = new EditText(context);
        ed1.setHint("Nama Penerima");
        ed1.addTextChangedListener(new TextWatcher() {
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
                        data.setFirstname(s.toString());
                    }
                };
                handler.postDelayed(runnable, 800);
            }
        });
        //ed.setVisibility(View.GONE);

        final PhoneInputLayout ed2 = new PhoneInputLayout(context);
        ed2.setHint(R.string.telepon_penerima);
        ed2.setDefaultCountry(AppConfig.DEFAULT_COUNTRY);
        ed2.getEditText().addTextChangedListener(new TextWatcher() {
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
                        data.setPhone(s.toString());
                    }
                };
                handler.postDelayed(runnable, 800);
            }
        });
        //ed.setVisibility(View.GONE);

        final EditText ed = new EditText(context);
        ed.setMinLines(1);
        ed.setMaxLines(2);
        ed.setHint("Catatan Tambahan");
        //ed.setVisibility(View.GONE);
        ed.addTextChangedListener(new TextWatcher() {
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
                        data.getAddress().setNotes(s.toString());
                    }
                };
                handler.postDelayed(runnable, 800);
            }
        });

        lin.addView(ed1);
        lin.addView(ed2);
        lin.addView(ed);

        final TextView tv2 = new TextView(context);
        tv2.setText("Klik untuk Set Tujuan ");
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
                DoSendHelper.getInstance().setDestination(data);
                Intent intent = new Intent(context, PickAnAddressActivity.class);
                intent.putExtra("type", PICKUP_DESTINATION);
                intent.putExtra("id", ""+v.getId());
                startActivityForResult(intent, PICKUP_DESTINATION);
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.weight = 0.9f;
        tv2.setLayoutParams(params);

        ImageButton imgIcon = new ImageButton(context);
        imgIcon.setImageResource(R.drawable.destination_pin);
        imgIcon.setBackgroundResource( R.color.white);
        imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow("Add Location", R.drawable.destination_pin);
            }
        });
        LinearLayout.LayoutParams paramsImgIcon = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        paramsImgIcon.weight = 0.05f;
        imgIcon.setLayoutParams(paramsImgIcon);

        ImageButton img = new ImageButton(context);
        img.setImageResource(R.drawable.ic_description_black_18dp);
        img.setBackgroundResource( R.color.white);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed.setVisibility(ed.isShown()? View.GONE: View.VISIBLE);
                ed1.setVisibility(ed1.isShown()? View.GONE: View.VISIBLE);
                ed2.setVisibility(ed2.isShown()? View.GONE: View.VISIBLE);
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

    private View generateElementPickup(final TUser s) {
        LinearLayout lin = new LinearLayout(context);
        lin.setOrientation(LinearLayout.VERTICAL);
        LinearLayout lin1 = new LinearLayout(context);
        lin1.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams paramLin1 = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        paramLin1.gravity = Gravity.CENTER;
        lin1.setLayoutParams(paramLin1);
        lin.addView(lin1);

        final TextView tv1 = new TextView(context);
        tv1.setText("Pickup Time");
        lin1.addView(tv1);
        LinearLayout.LayoutParams paramsTv1 = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        paramsTv1.weight = 0.25f;
        tv1.setLayoutParams(paramsTv1);

        Switch sw = new Switch(context);
        lin1.addView(sw);
        LinearLayout.LayoutParams paramsSw = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        paramsSw.weight = 0.1f;
        sw.setLayoutParams(paramsSw);

        final TextView tv2 = new TextView(context);
        tv2.setText("Drop Time");
        lin1.addView(tv2);
        LinearLayout.LayoutParams paramsTv2 = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        paramsTv2.weight = 0.25f;
        tv2.setLayoutParams(paramsTv2);

        final TextView tv3 = new TextView(context, null, R.style.AppTheme_Button);
        tv3.setText("00:00");
        lin1.addView(tv3);
        LinearLayout.LayoutParams paramsTv3 = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        paramsTv3.weight = 0.4f;
        paramsTv3.gravity = Gravity.CENTER;
        tv3.setLayoutParams(paramsTv3);
        tv3.setBackgroundResource(R.color.colorPrimary);
        //setStyle(tv3, R.style.AppTheme_Button);
        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean on = isChecked;
                if(on)
                {
                    tv1.setBackgroundResource(R.color.cardview_light_background);
                    tv2.setBackgroundResource(R.color.orange);
                }
                else
                {
                    tv1.setBackgroundResource(R.color.orange);
                    tv2.setBackgroundResource(R.color.cardview_light_background);
                }
            }
        });
        sw.setChecked(true);

        return lin;
    }

    public void setStyle(TextView textView, int resId){
        if (Build.VERSION.SDK_INT < 23) {
            textView.setTextAppearance(context, resId);
        } else {
            textView.setTextAppearance(resId);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == PICKUP_LOCATION){
            if(resultCode == RESULT_OK){
                TUser origin = ViewHelper.getInstance().getTUser();
                if(origin != null && origin.getAddress() != null){
                    tvOrigin.setText(origin.getAddress().toStringFormatted());
                    if(origin.getPhone() != null && !origin.getPhone().isEmpty()){
                        phonePengirimInput.setPhoneNumber(origin.getPhone());
                    }
                    if(origin.getName() != null && !origin.getName().isEmpty()){
                        inputnamaPengirim.setText(origin.getName());
                    }
                    DoSendHelper.getInstance().updateOrigin(origin);
                    ViewHelper.getInstance().setTUser(null);
                }
            }
        }else if(requestCode == PICKUP_DESTINATION){
            if(resultCode == RESULT_OK){
                TUser destination = ViewHelper.getInstance().getTUser();
                if(destination != null && destination.getAddress() != null){
                    String idStr = ViewHelper.getInstance().getId();
                    int id = item;
                    try {
                        id = Integer.parseInt(idStr);
                    }catch (Exception e){}
                    TextView tv = (TextView) map.get(""+id);
                    if(tv != null) {
                        tv.setText(destination.getAddress().toStringFormatted());
                        TUser d = DoSendHelper.getInstance().find(DoSendHelper.getInstance().getDestination());
                        if(d != null){
                            DoSendHelper.getInstance().setDestination(destination);
                        }
                        ViewHelper.getInstance().setTUser(null);
                    }
                }

            }
        }
    }

    @Override
    public int getName() {
        return 0;
    }

    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    public static Fragment newInstance(String doType) {
        Bundle bundle = new Bundle();
        bundle.putString("doType", doType);
        DoSendMultiFormFragment f = new DoSendMultiFormFragment();
        f.setArguments(bundle);
        return f;
    }

    protected void showPopupWindow(String title, int imageResourceId) {

    }

}
