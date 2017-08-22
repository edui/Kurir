package id.co.kurindo.kurindo.helper;

import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.DoCarRental;
import id.co.kurindo.kurindo.model.TOrder;

/**
 * Created by dwim on 2/6/2017.
 */

public class DoHijamahHelper extends OrderViaMapHelper{

    static DoHijamahHelper helper;

    public static DoHijamahHelper getInstance(){
        if(helper == null) helper = new DoHijamahHelper();
        return helper;
    }

    @Override
    public void clearAll() {
        super.clearAll();
        helper = null;
    }

    public TOrder addDoHijamahOrder(String serviceCode, String subtype, double price) {
        addOrder(AppConfig.CASH_PAYMENT, serviceCode, price);
        addNewProduct(AppConfig.KEY_DOHIJAMAH);
        order.setService_type(AppConfig.KEY_DOHIJAMAH);
        order.setSub_type(subtype);
        return order;
    }

}
