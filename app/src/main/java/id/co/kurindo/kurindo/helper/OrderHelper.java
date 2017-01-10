package id.co.kurindo.kurindo.helper;

import id.co.kurindo.kurindo.model.Order;

/**
 * Created by dwim on 1/11/2017.
 */

public class OrderHelper {

    private Order order;

    private static OrderHelper helper;
    public static OrderHelper getInstance(){
        if(helper == null) helper = new OrderHelper();
        return helper;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
