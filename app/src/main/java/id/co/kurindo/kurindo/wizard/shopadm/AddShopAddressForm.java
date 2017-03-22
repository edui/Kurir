package id.co.kurindo.kurindo.wizard.shopadm;

import android.support.annotation.NonNull;
import android.widget.TextView;

import com.stepstone.stepper.VerificationError;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.helper.ShopAdmHelper;
import id.co.kurindo.kurindo.map.SinglePinLocationMapFragment;
import id.co.kurindo.kurindo.model.Address;

/**
 * Created by dwim on 3/15/2017.
 */

public class AddShopAddressForm extends SinglePinLocationMapFragment {
    private static final String TAG = "AddShopAddressForm";
    @Bind(R.id.tvAlamat)
    TextView tvAlamat;
    @Bind(R.id.tvKecamatan)
    TextView tvKecamatan;
    @Bind(R.id.tvKabupaten)
    TextView tvKabupaten;
    @Bind(R.id.tvPropinsi)
    TextView tvPropinsi;
    @Bind(R.id.tvNegara)
    TextView tvNegara;

    protected int getLayout() {
        return R.layout.fragment_maps_pin_addressform;
    }

    public void onClick_mLocationMarkerText(){
        super.onClick_mLocationMarkerText();
        displayAddressText();
        hidepanel(false);
        originMode = false;
    }

    private void displayAddressText() {
        if(origin != null && origin.getAddress() != null){
            Address addr = origin.getAddress();
            tvAlamat.setText(addr.getAlamat());
            tvKecamatan.setText(addr.getKecamatan());
            tvKabupaten.setText(addr.getKabupaten());
            tvPropinsi.setText(addr.getPropinsi());
            tvNegara.setText(addr.getNegara());
        }
    }


    @Override
    public VerificationError verifyStep() {
        if(originMode) return new VerificationError("Set Alamat Toko Anda");
        if(origin ==null || origin.getAddress() == null || origin.getAddress().getLocation() == null) return new VerificationError("Set Alamat Toko Anda");

        if(ShopAdmHelper.getInstance().getShop() != null){
            ShopAdmHelper.getInstance().getShop().getPic().setAddress(origin.getAddress());
        }
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
