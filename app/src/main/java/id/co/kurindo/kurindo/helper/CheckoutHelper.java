package id.co.kurindo.kurindo.helper;

import com.android.tonyvu.sc.model.Saleable;
import com.android.tonyvu.sc.util.CartHelper;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import id.co.kurindo.kurindo.model.CartItem;
import id.co.kurindo.kurindo.model.City;
import id.co.kurindo.kurindo.model.Order;
import id.co.kurindo.kurindo.model.Packet;
import id.co.kurindo.kurindo.model.Product;
import id.co.kurindo.kurindo.model.Recipient;
import id.co.kurindo.kurindo.model.ShippingCost;
import id.co.kurindo.kurindo.model.User;

/**
 * Created by DwiM on 12/3/2016.
 */

public class CheckoutHelper {
    private Set<Recipient> recipients = new LinkedHashSet<>();
    private Set selectedItems = new LinkedHashSet();
    private User buyer;
    private City city;
    private String payment;
    private Set<Product> products;
    private Order order;
    private String packetType;
    Map<Integer, ShippingCost> shippingCost;
    private Set<Packet> packets;

    private static CheckoutHelper helper;
    public static CheckoutHelper getInstance(){
        if(helper == null) helper = new CheckoutHelper();
        return helper;
    }

    public Set<Recipient> getRecipients() {
        return recipients;
    }

    public Set getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(Set selectedItems) {
        this.selectedItems = selectedItems;
    }
    public void setSelection(int pos) {
        selectedItems.add(pos);
    }
    public void toggleSelection(Integer pos) {
        if (selectedItems.add(pos)) {
            selectedItems.remove(pos);
        }
        else {
            selectedItems.add(pos);
        }
    }

    public void clearSelections() {
        selectedItems.clear();
    }

    public void setRecipients(Set<Recipient> recipients) {
        this.recipients = recipients;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public void addRecipient(Recipient r) {
        getRecipients().add(r);
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public String getPacketType() {
        return packetType;
    }

    public void setPacketType(String packetType) {
        this.packetType = packetType;
    }

    public Set<Packet> getPackets() {
        return packets;
    }

    public void setPackets(Set<Packet> packets) {
        this.packets = packets;
    }

    public Set<Product> getProducts() {
        Set<Product> products = new LinkedHashSet<>() ;
        for (Map.Entry<Saleable, Integer> entry : CartHelper.getCart().getItemWithQuantity().entrySet()) {
            Product p = (Product) entry.getKey();
            p.setQuantity(entry.getValue());
            products.add(p);
        }
        return products;
    }
    public Set<Map> getProductsAsParam() {
        Set<Map> products = new LinkedHashSet<>() ;
        for (Map.Entry<Saleable, Integer> entry : CartHelper.getCart().getItemWithQuantity().entrySet()) {
            Product p = (Product) entry.getKey();
            Map b = new HashMap();
            b.put("id", p.getId());
            b.put("code", p.getCode());
            b.put("shopid", p.getShopid());
            b.put("price", p.getPrice().doubleValue());
            b.put("discount", p.getDiscount().doubleValue());
            b.put("quantity", entry.getValue());
            b.put("notes", p.getNotes());
            products.add(b);
        }
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public Order getOrder() {
        return order;
    }

    public Set<CartItem> getCartItems(Map<Saleable, Integer> itemMap){
        Set<CartItem> sets = new LinkedHashSet<>();
        for (Map.Entry<Saleable, Integer> entry : CartHelper.getCart().getItemWithQuantity().entrySet()) {
            CartItem item = new CartItem();
            Product p = (Product) entry.getKey();
            p.setQuantity(entry.getValue());
            item.setProduct(p);
            item.setQuantity(p.getQuantity());
            sets.add(item);
        }
        return sets;
    }

    public Map<Integer, ShippingCost> getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(Map<Integer, ShippingCost> shippingCost) {
        this.shippingCost = shippingCost;
    }

    public void setFinishOrder(Order order) {
        this.order = order;
        this.order.setPayment(payment);
        this.order.setBuyer(buyer);
        //this.order.setProducts(CartHelper.getCart().getItemWithQuantity());
        this.order.setItems(getCartItems(CartHelper.getCart().getItemWithQuantity()));
        this.order.setTotalPrice(CartHelper.getCart().getTotalPrice());
        this.order.setTotalQuantity(CartHelper.getCart().getTotalQuantity());
        this.order.setRecipients(getRecipients());
        this.products.clear();
        this.recipients.clear();
        this.selectedItems.clear();
        this.packets.clear();

        CartHelper.getCart().clear();
    }


}
