package id.co.kurindo.kurindo.helper;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.DoService;
import id.co.kurindo.kurindo.model.TOrder;

/**
 * Created by aspire on 3/30/2017.
 */

public class DoServiceHelper {
    private static DoServiceHelper helper;
    public static DoServiceHelper getInstance(){
        if(helper == null) helper = new DoServiceHelper();
        return helper;
    }

    protected TOrder order;
    protected DoService doService;
    protected Set<DoService> services;

    public void addOrder(BigDecimal price, String service_type){
        if(order ==null) order = new TOrder();
        order.setService_type(service_type);
        order.setService_code(AppConfig.PACKET_NDS);
        order.setTotalPrice(price);
        order.setPayment(AppConfig.CASH_PAYMENT);
    }

    public void addService(String layanan, String acType, BigDecimal price, int qty, String notes, String location, BigDecimal discount){
        DoService doService = new DoService();
        doService.setKode_layanan(layanan);
        doService.setJenis_barang(acType);
        doService.setPrice(price);
        doService.setQty(qty);
        doService.setNotes(notes);
        doService.setLocation(location);
        doService.setDiscount(discount);
        if(services == null) {
            services = new HashSet<>();
            order.setServices(services);
        }
        services.add(doService);
    }

    public DoService getDoService(){
        return doService;
    }

    public TOrder getOrder() {
        return order;
    }

    public void clearOrder() {
        order = null;
        services = null;
    }

    public DoService addService() {
        DoService doService = new DoService();
        if(services == null) {
            services = new HashSet<>();
            order.setServices(services);
        }
        services.add(doService);
        return doService;
    }

    public void removeService(DoService data) {
        if(services != null && data != null)
            services.remove(data);
    }

    public Set<DoService> getServices() {
        return services;
    }

    public void setServices(Set<DoService> services) {
        this.services = services;
    }

    public void addService(DoService ds) {
        if(services == null) {
            services = new HashSet<>();
            order.setServices(services);
        }
        services.add(ds);
    }
}
