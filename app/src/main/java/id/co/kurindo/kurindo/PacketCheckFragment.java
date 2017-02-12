package id.co.kurindo.kurindo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import id.co.kurindo.kurindo.adapter.PacketAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.base.RecyclerItemClickListener;
import id.co.kurindo.kurindo.model.Packet;

/**
 * Created by DwiM on 12/13/2016.
 */

public class PacketCheckFragment extends BaseFragment {
    private static final String TAG = "PacketCheckFragment";

    private static final int REQUEST_PACKET_CHECK = 0;
    @Bind(R.id.input_nomor_resi)
    EditText _nomorResiText;
    @Bind(R.id.ButtonPacketCheck)
    AppCompatButton _buttonPacketCheck;

    @Bind(R.id.list)
    RecyclerView traceResultView;

    PacketAdapter adapter;
    ProgressDialog progressDialog;

    private List<Packet> packets = new ArrayList<>();
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflateAndBind(inflater, container, R.layout.fragment_packet_check);

        setup_packet_check();

        return rootView;
    }

    private void setup_packet_check() {
        progressDialog = new ProgressDialog(getActivity(),R.style.AppTheme);

        traceResultView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        traceResultView.setHasFixedSize(true);

        adapter = new PacketAdapter(getContext(), packets);
        traceResultView.setAdapter(adapter);

        traceResultView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //Intent intent = new Intent(getContext(), DetailImageActivity.class);
                        //intent.putParcelableArrayListExtra("orders", orders);
                        //intent.putExtra("pos", position);
                        //startActivity(intent);
                        Bundle bundle = new Bundle();
                        Packet packet = packets.get(position);
                        ArrayList<Packet> list = new ArrayList<Packet>();
                        list.add(packet);
                        bundle.putParcelableArrayList("packets", list);
                        Intent intent = new Intent(getActivity(), PacketShowActivity.class);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 0);

                    }
                })
        );

        _buttonPacketCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process_packet_check();
            }
        });

    }

    private void process_packet_check() {
        Log.d(TAG, "process_packet_check");
        progressDialog.show();

        if (!validate()) {
            onProcessPacketCheckFailed();
            return;
        }

        processing_packet_check();
    }

    private void processing_packet_check() {
        String tag_string_req = "req_packet_check";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PACKET_TRACE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Tracing Packet Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        BasePacketMonitoringFragment.parsePackets(packets, jObj);
                        onProcessPacketCheckSuccess();
                    }else{
                        String message = jObj.getString("message");

                        Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_LONG).show();
                        _buttonPacketCheck.setEnabled(true);
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    _buttonPacketCheck.setEnabled(true);
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Tracing Packet Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                _buttonPacketCheck.setEnabled(true);
                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("awb_number", _nomorResiText.getText().toString());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void onProcessPacketCheckSuccess() {
        adapter.notifyDataSetChanged();
        _buttonPacketCheck.setEnabled(true);
        progressDialog.dismiss();
    }

    private void onProcessPacketCheckFailed() {
        Toast.makeText(getContext(), "Data tidak valid. Silahkan dilengkapi.", Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
        _buttonPacketCheck.setEnabled(true);
    }

    private boolean validate() {
        boolean valid = true;
        String nomorResi = _nomorResiText.getText().toString();

        if (nomorResi.isEmpty() || nomorResi.length() < 8 ) {
            _nomorResiText.setError("Tuliskan Nomor Resi. Nomor Resi kurang lengkap.");
            valid = false;
        } else {
            _nomorResiText.setError(null);
        }
        return  valid;
    }
}
