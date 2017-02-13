package id.co.kurindo.kurindo.helper;

import id.co.kurindo.kurindo.model.Product;
import id.co.kurindo.kurindo.model.Shop;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPacket;

/**
 * Created by dwim on 2/12/2017.
 */

public class ViewHelper {
    TOrder order;
    TPacket packet;
    Shop shop;
    Product product;

    private static ViewHelper helper;
    public static ViewHelper getInstance(){
        if(helper == null) helper = new ViewHelper();
        return helper;
    }

    public TOrder getOrder() {
        return order;
    }

    public void setOrder(TOrder order) {
        this.order = order;
    }

    public TPacket getPacket() {
        return packet;
    }

    public void setPacket(TPacket packet) {
        this.packet = packet;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Shop getShop() {
        return shop;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
