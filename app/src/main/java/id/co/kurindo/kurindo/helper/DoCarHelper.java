package id.co.kurindo.kurindo.helper;

import java.math.BigDecimal;

import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.DoCarRental;
import id.co.kurindo.kurindo.model.Route;
import id.co.kurindo.kurindo.model.TOrder;

/**
 * Created by dwim on 2/6/2017.
 */

public class DoCarHelper extends OrderViaMapHelper{

    static DoCarHelper helper;

    public static DoCarHelper getInstance(){
        if(helper == null) helper = new DoCarHelper();
        return helper;
    }

    DoCarRental rental;
    @Override
    public void clearAll() {
        rental=null;
        super.clearAll();
    }
    public DoCarRental addRental(){
        if(rental == null){
            rental = new DoCarRental();
        }
        return rental;
    }

    public DoCarRental getRental() {
        return rental;
    }

    public TOrder addDoCarOrder(String serviceCode, double price) {
        addOrder(AppConfig.CASH_PAYMENT, serviceCode, price);
        //addNewProduct(AppConfig.KEY_DOCAR);
        order.setService_type(AppConfig.KEY_DOCAR);
        return order;
    }

}
