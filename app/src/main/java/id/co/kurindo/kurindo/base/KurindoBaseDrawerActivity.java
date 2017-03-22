package id.co.kurindo.kurindo.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

import id.co.kurindo.kurindo.AdminMonitorOrderActivity;
import id.co.kurindo.kurindo.BuildConfig;
import id.co.kurindo.kurindo.KerjasamaActivity;
import id.co.kurindo.kurindo.KurirActivity;
import id.co.kurindo.kurindo.LoginActivity;
import id.co.kurindo.kurindo.LuarKotaFragment;
import id.co.kurindo.kurindo.MonitorOrderActivity;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.SettingsActivity;
import id.co.kurindo.kurindo.ShoppingCartActivity;
import id.co.kurindo.kurindo.TabFragment;
import id.co.kurindo.kurindo.helper.SQLiteHandler;
import id.co.kurindo.kurindo.helper.SessionManager;
import id.co.kurindo.kurindo.helper.ShopAdmHelper;
import id.co.kurindo.kurindo.wizard.dosend.DoSendOrderActivity;
import id.co.kurindo.kurindo.wizard.help.KurirOpenActivity;
import id.co.kurindo.kurindo.wizard.help.ShopOpenActivity;
import id.co.kurindo.kurindo.wizard.shopadm.AddShopActivity;

import static java.security.AccessController.getContext;

/**
 * Created by aspire on 11/24/2016.
 */

public class KurindoBaseDrawerActivity extends BaseDrawerActivity {
    private static final int REQUEST_ADD_SHOP = 1;
    protected SessionManager session;
    protected SQLiteHandler db;
    protected AppCompatButton loginBtn;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // SQLite database handler
        db = new SQLiteHandler(this);
        //db.onUpgrade(db.getWritableDatabase(),0,1);
        // Session manager
        session = new SessionManager(this);
        initialize();
    }
    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_item_settings:
                return true;
            case R.id.action_cart:
                showActivity(ShoppingCartActivity.class);
                return true;
            default:
            //case android.R.id.home:
                openDrawer();
                return true;

            //default:  return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Handles the navigation item click and starts the corresponding activity.
     * @param item the selected navigation item
     */
    protected void goToNavDrawerItem(int item) {
        Bundle bundle = new Bundle();
        switch (item) {
            case R.id.nav_item_kiriman_luar_kota:
                showFragment(LuarKotaFragment.class);
                break;
            case R.id.nav_item_kiriman_dalam_kota:
                showActivity(DoSendOrderActivity.class);
                break;
            case R.id.nav_item_cek_resi :
                showActivity(MonitorOrderActivity.class);
                break;
            case R.id.nav_item_home :
                showFragment(TabFragment.class);
                break;
            case R.id.nav_item_syarat:
                bundle.putString("class", "snk");
                showActivity(KerjasamaActivity.class, bundle);
                break;
            case R.id.nav_item_kerjasam:
                bundle.putString("class", "kerjasama");
                showActivity(KerjasamaActivity.class, bundle);
                break;
            case R.id.nav_item_aboutus:
                bundle.putString("class", "aboutus");
                showActivity(KerjasamaActivity.class, bundle);
                break;
            case R.id.nav_item_kurir_monitor_paket:
                bundle.putString("extra", "KURIR");
                showActivity(AdminMonitorOrderActivity.class, bundle);
                //showActivity(MonitorPacketActivity.class);
                break;
            case R.id.nav_item_admin_monitor_paket_kurir:
                showActivity(MonitorOrderActivity.class);
                break;
            case R.id.nav_item_admin_daftar_kurir:
                showActivity(KurirActivity.class);
                break;
            case R.id.nav_item_admin_monitor:
                bundle.putString("extra", "ADMIN");
                showActivity(AdminMonitorOrderActivity.class, bundle);
                break;
            case R.id.nav_item_history_order:
                bundle.putString("extra", "PELANGGAN");
                showActivity(MonitorOrderActivity.class, bundle);
                break;

            case R.id.nav_item_shop_add:
                Intent intent = new Intent(getApplicationContext(), AddShopActivity.class);
                startActivityForResult(intent, REQUEST_ADD_SHOP);
                break;
            case R.id.nav_item_shop_list:
                showActivity(id.co.kurindo.kurindo.ShopListActivity.class);
                break;
            case R.id.nav_item_shop_city_list:
                showActivity(id.co.kurindo.kurindo.map.MapsActivity.class);
                break;

            case R.id.nav_item_shop_open:
                showActivity(ShopOpenActivity.class);
                break;
            case R.id.nav_item_kurir_open:
                showActivity(KurirOpenActivity.class);
                break;

            case R.id.nav_item_settings:
                showActivity(SettingsActivity.class);
                break;
            case R.id.nav_item_maps:
                showActivity(id.co.kurindo.kurindo.map.MapsActivity.class);
                break;
            case R.id.nav_item_logout:
                logoutUser();
                showActivity(LoginActivity.class);
                finish();
                break;
        }
    }

    protected void initialize(){
        // Check if user is already logged in or not
        if (session.isLoggedIn())
        {
            boolean admin = false;
            boolean kurir = false;
            boolean agent = false;
            boolean pelanggan = false;
            boolean shoppic = false;
/*
            boolean shopkurir = false;
            boolean kurirshop = false;
            boolean shopkec = false;
            boolean shopkab = false;
            boolean shopprop = false;
            boolean shopneg = false;
            boolean adminkec = false;
            boolean adminkab = false;
            boolean adminprop = false;
            boolean adminneg = false;*/

            if(session.isKurir()){
                pelanggan = true;
                kurir = true;
            }
            if(session.isShopPic()) shoppic= true;
            if(session.isAgent()) agent = true;
            if(session.isPelanggan()) pelanggan = true;
            if(session.isAdministrator()) {
                admin = true;
            }

            navigationView.getMenu().setGroupVisible(R.id.group_customer_id, pelanggan);
            navigationView.getMenu().setGroupVisible(R.id.group_agent_id, agent);
            navigationView.getMenu().setGroupVisible(R.id.group_kurir_id, kurir);
            navigationView.getMenu().setGroupVisible(R.id.group_admin_id, admin);

            setupHeader(true);
        } else
        {
            navigationView.getMenu().setGroupVisible(R.id.group_kurir_id,false);
            navigationView.getMenu().setGroupVisible(R.id.group_agent_id, false);
            navigationView.getMenu().setGroupVisible(R.id.group_customer_id, false);
            navigationView.getMenu().setGroupVisible(R.id.group_admin_id, false);
            navigationView.getMenu().setGroupVisible(R.id.group_shop_id, false);

            setupHeader(false);
        }

        updateCartCount();
    }
    public void setupHeader(boolean param){
        View headerLayout = navigationView.getHeaderView(0);
        TextView txt = (TextView) headerLayout.findViewById(R.id.headerTxt);
        TextView versiontxt = (TextView) headerLayout.findViewById(R.id.versiTxt);
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        versiontxt.setText("Versi "+versionName +" (build "+versionCode+")");

        loginBtn = (AppCompatButton) headerLayout.findViewById(R.id.mainLoginBtn);
        if(param){
            HashMap user = db.getUserDetails();
            txt.setText(user.get("firstname") + " " + user.get("lastname") +" ("+user.get("role")+")");
            navigationView.getMenu().findItem(R.id.nav_item_logout).setVisible(true);
            loginBtn.setVisibility(View.GONE);
        }else{
            txt.setText("Silahkan Login");
            navigationView.getMenu().findItem (R.id.nav_item_logout).setVisible(false);
            loginBtn.setVisibility(View.VISIBLE);
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showActivity(LoginActivity.class);
                }
            });
        }


    }
    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user packets from sqlite users table
     * */
    private void logoutUser() {
        session.setLogout();
        //db.deleteUsers();

        initialize();
    }
    @Override
    protected void onResume() {
        super.onResume();
        initialize();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_SHOP) {
            if (resultCode == RESULT_OK) {
                ShopAdmHelper.getInstance().setShop(null);
                showActivity(id.co.kurindo.kurindo.ShopListActivity.class);
            }
        }

    }
}
