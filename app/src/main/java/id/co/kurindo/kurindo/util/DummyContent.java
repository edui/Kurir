package id.co.kurindo.kurindo.util;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.News;
import id.co.kurindo.kurindo.model.Product;
import id.co.kurindo.kurindo.model.Shop;
import id.co.kurindo.kurindo.model.TUser;


/**
 * Just dummy content. Nothing special.
 *
 * Created by Andreas Schrade on 14.12.2015.
 */
public class DummyContent {

    /**
     * An array of sample items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<>();

    public static final List<Shop> SHOPS = new ArrayList<>();

    public static final List<News> NEWS= new ArrayList<>();
    /**
     * A map of sample items. Key: sample ID; Value: Item.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<>(5);

    public static final Map<String, Shop> SHOP_MAP = new HashMap<>(5);
    public static final Map<String, News> NEWS_MAP = new HashMap<>(5);
    public static final Map<String, List<Product>> PRODUCT_MAP = new HashMap<>(5);

    public static Shop shop;

    static {
        addItem(new DummyItem("1", R.drawable.banner_mobile_application_0, "Quote #1", "Steve Jobs", "Focusing is about saying No."));
        addItem(new DummyItem("2", R.drawable.banner_mobile_application_0, "Quote #2", "Napoleon Hill","A quitter never wins and a winner never quits."));
        addItem(new DummyItem("3", R.drawable.banner_mobile_application_0, "Quote #3", "Pablo Picaso", "Action is the foundational key to all success."));
        addItem(new DummyItem("4", R.drawable.banner_mobile_application_0, "Quote #4", "Napoleon Hill","Our only limitations are those we set up in our own minds."));
        addItem(new DummyItem("5", R.drawable.banner_mobile_application_0, "Quote #5", "Steve Jobs","Deciding what not do do is as important as deciding what to do."));
/*
        Shop shop = new Shop(1, "KUR001", "KURINDO", R.drawable.cover_kurindo, R.drawable.cover_kurindo, "087812134646", "Balikpapan Regency, CLuster Celebration i3", "OPEN", "BPN02", "Balikpapan Selatan  Tengah");
        List<Product> products = new ArrayList<>();
        Product p = new Product(1, "KUR010", "KURIR", "JASA PENGIRIMAN DALAM KOTA", R.drawable.do_send_icon, "10.000", "1", "0", "AVAILABLE", "1-dec-2016");
        products.add(p);
        Product p1 = new Product(2, "KUR011", "MOBIL", "JASA RENTAL MOBIL", R.drawable.do_car_icon, "100.000", "1", "0", "AVAILABLE", "1-dec-2016");
        products.add(p1);
        Product p2 = new Product(3, "KUR012", "OJEK", "JASA PENGANTARAN OJEK", R.drawable.do_jek_icon, "10.000", "1", "0", "AVAILABLE", "1-dec-2016");
        products.add(p2);
        Product p3 = new Product(4, "KUR013", "LAUNDRY", "JASA CUCI LAUNDRY", R.drawable.do_wash_icon, "10.000", "1", "0", "AVAILABLE", "1-dec-2016");
        products.add(p3);
        Product p4 = new Product(5, "KUR014", "SERVICEAC", "JASA SERVICE PERBAIKAN AC", R.drawable.do_service_icon, "10.000", "1", "0", "AVAILABLE", "1-dec-2016");
        products.add(p4);
        Product p5 = new Product(6, "KUR015", "TERAPI", "JASA TERAPI HIJAMAH", R.drawable.do_hijamah_icon, "10.000", "1", "0", "AVAILABLE", "1-dec-2016");
        products.add(p5);
        Product p6 = new Product(7, "KUR016", "PINDAHAN", "JASA PINDAH PICKUP", R.drawable.do_move_icon, "10.000", "1", "0", "AVAILABLE", "1-dec-2016");
        products.add(p6);
        //shop.setProducts(products);
        addShop(shop);
        addProduct(shop.getId(),products);
*/

        /*Shop shop = new Shop(2, "HAMASA001", "HAMASAFARM", "","hamasafarm_iconshop.png", "hamasafarm_bannershop.png", "08xxx", "Balikpapan Regency, Cluster Celebration i3", "OPEN", "BPN02", "Balikpapan Tengah & Kota");
        List<Product> products = new ArrayList<>();
        Product p = new Product(8, "HAMASA010", "Selada Merah", "Selada Merah", R.drawable.hamasa010, "10.000", "1", "0", "AVAILABLE", "1-dec-2016");
        products.add(p);
        Product p8 = new Product(9, "HAMASA011", "Selada Hijau", "Selada Hijau", R.drawable.hamasa011, "10.000", "1", "0", "AVAILABLE", "1-dec-2016");
        products.add(p8);
        Product p9 = new Product(10, "HAMASA012", "Sawi Hijau", "Sawi Hijau", R.drawable.hamasa012, "10.000", "1", "0", "AVAILABLE", "1-dec-2016");
        products.add(p9);
        Product p10 = new Product(11, "HAMASA013", "Phakk choy", "Phakk Choy", R.drawable.hamasa013, "10.000", "1", "0", "AVAILABLE", "1-dec-2016");
        products.add(p10);
        //shop.setProducts(products);
        addShop(shop);
        addProduct(shop.getId(),products);
        */

        //Shop shop = new Shop(3, "BURYAMI001", "BURYAMI", "Bubur Ayam Istimewa\nDiolah dari bahan segar dan bumbu rempah asli & sehat, Non MSG", "buryami_iconshop.png", "buryami_bannershop.png", "0811542188", "Jl. Syarifudin Yoes \ndepan HER Guesthouse (seberang Pelangi B Point)\nOpen at 06.30 - 11.00", "OPEN", "BPN02", "Balikpapan Tengah & Kota");
        Shop shop = new Shop(3, "BURYAMI001", "BURYAMI", "Bubur Ayam Istimewa\nDiolah dari bahan segar dan bumbu rempah asli & sehat, Non MSG", "buryami_iconshop.png", "buryami_bannershop.png", "OPEN");
        List<Product> products = new ArrayList<>();
        Product p11 = new Product(12, "BURYAMI010", "Bubur Ayam Komplit", "Bubur Ayam Komplit", R.drawable.buryami010, "10.000", "1", "0", "AVAILABLE", "1-dec-2016");
        products.add(p11);
        Product p12 = new Product(13, "BURYAMI011", "Bubur Ayam Basah", "Bubur Ayam Basah", R.drawable.buryami011, "10.000", "1", "0", "AVAILABLE", "1-dec-2016");
        products.add(p12);
        //shop.setProducts(products);
        TUser u = new TUser();
        Address addr = new Address();
        addr.setLocation(new LatLng(-0.3220503,117.429754));
        u.setAddress(addr);
        shop.setPic(u);
        addShop(shop);
        addProduct(shop.getId(),products);

        //shop = new Shop(4, "HONEYMOON001", "HONEYMOON", "","honeymoon_iconshop.png", "honeymoon_bannershop.png", "Info dan pemesanan: 0812-5630-2990", "Balikpapan Regency\nCastarica JE3 02", "OPEN");
        shop = new Shop(4, "HONEYMOON001", "HONEYMOON", "","honeymoon_iconshop.png", "honeymoon_bannershop.png", "OPEN");
        products = new ArrayList<>();
        Product p = new Product(13, "HONEYMOON001", "Madu Premium", "Madu Premium", R.drawable.honeymoon001, "10.000", "1", "0", "AVAILABLE", "1-dec-2016");
        products.add(p);
        p = new Product(14, "HONEYMOON002", "Madu Organik", "Madu Organik", R.drawable.honeymoon002, "10.000", "1", "0", "AVAILABLE", "1-dec-2016");
        products.add(p);
        //shop.setProducts(products);
        u = new TUser();
        addr = new Address();
        addr.setLocation(new LatLng(-0.3420503,117.429754));
        u.setAddress(addr);
        shop.setPic(u);
        addShop(shop);
        addProduct(shop.getId(),products);

        //shop = new Shop(5, "MANGONCEL001", "MANGONCEL", "","mangoncel_iconshop.png", "mangoncel_bannershop.png", "Info dan pemesanan:\nIkhwan 085387180678\nAkhwat 081254406406", "Jl.Indrakila, Strat 3, \nGg.pandega, RT 32, No 56 \n\nInstagram : @mang_oncel", "OPEN", "BPN02", "Balikpapan Tenagh & Kota");
        shop = new Shop(5, "MANGONCEL001", "MANGONCEL", "","mangoncel_iconshop.png", "mangoncel_bannershop.png", "OPEN");
        products = new ArrayList<>();
        p = new Product(15, "MANGONCEL001", "Es Krim Cup Coklat", "Es Krim Cup Coklat", R.drawable.mangoncel001, "10.000", "1", "0", "AVAILABLE", "1-dec-2016");
        products.add(p);
        p = new Product(16, "MANGONCEL002", "Es Krim Cup Aneka Rasa", "Es Krim Cup Aneka Rasa", R.drawable.mangoncel002, "10.000", "1", "0", "AVAILABLE", "1-dec-2016");
        products.add(p);
        p = new Product(17, "MANGONCEL003", "Es Krim Cup Vanilla", "Es Krim Cup Vanilla", R.drawable.mangoncel003, "10.000", "1", "0", "AVAILABLE", "1-dec-2016");
        products.add(p);
        p = new Product(18, "MANGONCEL004", "Es Krim Cup Strawberry", "Es Krim Cup Strawberry", R.drawable.mangoncel004, "10.000", "1", "0", "AVAILABLE", "1-dec-2016");
        products.add(p);
        //shop.setProducts(products);
        u = new TUser();
        addr = new Address();
        addr.setLocation(new LatLng(-0.3520503,117.429754));
        u.setAddress(addr);
        shop.setPic(u);
        addShop(shop);
        addProduct(shop.getId(),products);


        News n = new News(2, "DO-Send", "DO-Send", "DO-Send", R.drawable.banner_dosend);
        addNews(n);
        n = new News(3, "DO-Jek", "DO-Jek", "DO-Jek", R.drawable.banner_dojek);
        addNews(n);
        n = new News(4, "DO-Wash", "DO-Wash", "DO-Wash", R.drawable.banner_dowash);
        addNews(n);
        n = new News(5, "DO-Hijamah", "DO-Hijamah", "DO-Hijamah", R.drawable.banner_dohijamah);
        addNews(n);
        n = new News(6, "DO-Car", "DO-Car", "DO-Car", R.drawable.banner_docar);
        addNews(n);

        n = new News(7, "DO-Service", "DO-Service", "DO-Service", R.drawable.banner_doservice);
        addNews(n);
        n = new News(8, "DO-Move", "DO-Move", "DO-Move", R.drawable.banner_domove);
        addNews(n);
        n = new News(9, "DO-Client", "DO-Client", "DO-Client", R.drawable.banner_doclient);
        addNews(n);
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }
    private static void addShop(Shop item) {
        SHOPS.add(item);
        SHOP_MAP.put(""+item.getId(), item);
    }
    private static void addNews(News item) {
        NEWS.add(item);
        NEWS_MAP.put(""+item.getTitle(), item);
    }
    private static void addProduct(int shop, List<Product> item) {
        PRODUCT_MAP.put(""+shop, item);
    }

    public static class DummyItem {
        public final String id;
        public final int photoId;
        public final String title;
        public final String author;
        public final String content;

        public DummyItem(String id, int photoId, String title, String author, String content) {
            this.id = id;
            this.photoId = photoId;
            this.title = title;
            this.author = author;
            this.content = content;
        }
    }

/*    public static void main(String[] args){
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        String str ="'products':[{'product':{'id':'1','type':'A','code':'KUR010','name':'KURIR','description':'JASA PENGIRIMAN DALAM KOTA','stock':'1','price':'9000','price_calc':'9000','discount':'0'},'quantity':'1'}\n" +
                "]" ;

        Set<CartItem> products = gson.fromJson(str, new TypeToken<LinkedHashSet<CartItem>>(){}.getType());

    }
*/
}
