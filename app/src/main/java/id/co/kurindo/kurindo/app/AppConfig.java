package id.co.kurindo.kurindo.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import id.co.kurindo.kurindo.model.PacketService;
import id.co.kurindo.kurindo.model.Vehicle;

/**
 * Created by DwiM on 11/8/2016.
 */
public class AppConfig {
    public static String API_VERSION = "v1";

    //public static String BASE_HOST = "https://kurindo.id";//+"/"+API_VERSION;
    //public static String BASE_HOST = "http://10.0.2.2/kurindo";
    //public static String BASE_HOST = "http://192.168.43.25/kurindo";
    public static String BASE_HOST = "http://172.30.3.58/kurindo";
    public static String API_HOST = BASE_HOST+"/"+API_VERSION;
    public static String HOST = API_HOST;
    //public static String API_HOST = HOST;
    public static String USER_AGENT = "KURINDROID";

    public static String FCM_TOKEN = null;
    public static String SECRET_KEY = "AIzaSyCIVhOu83YxppEIlQEtqV0KCEW-JFDWAKA-KURINDROID";

    //active link since v1 API
    public static String URL_BACKEND_SIGNUP = HOST + "/backend/sign_up/json";
    public static String URL_BACKEND_SIGNIN = HOST + "/backend/sign_in/json";
    public static String URL_BACKEND_RECOVERY_PHONE = HOST + "/backend/recovery/json";
    public static  String URL_BACKEND_ACTIVATION_PHONE = HOST + "/backend/activation/json";

    public static String URL_MY_SHOP_LIST = API_HOST + "/shop/myshops";
    public static String URL_SHOP_PREPIC_LIST = API_HOST + "/shop/prepic";
    public static String URL_SHOP_ADD =API_HOST + "/shop/add";
    public static String URL_SHOP_UPDATE=API_HOST + "/shop/update";
    public static String URL_SHOP_LOCATIONBASED_LIST = HOST + "/shop/locationbased/list";
    public static String URL_SHOP_CITYBASED = HOST + "/shop/citybased/";

    public static String URL_SHOP_PRODUCT_ADD =API_HOST + "/shop/product/add";
    public static String URL_SHOP_PRODUCT_UPDATE=API_HOST + "/shop/product/update";

    public static String URL_SHOP_LIST = HOST + "/shops/list/{page}";
    public static String URL_SHOP_PRODUCTS = HOST + "/shop/{shop_id}/products/{page}";
    public static String URL_SHOP_CITY_LIST = API_HOST + "/shop/city_list";

    public static String URL_PRICE_KM= API_HOST + "/tariff/km";
    public static String URL_CALC_PRICE_KM= API_HOST + "/calculate/km";
    public static String URL_LOGGING = HOST + "/logging";
    public static String URL_UPDATE_POSITION = HOST + "/position/update";
    public static String URL_REGISTER_FCM = HOST + "/refresh/newfirebase";
    public static String URL_MINAT_FORM = HOST + "/minat";

    public static String URL_LIST_KURIR_LOCATIONBASED = HOST + "/tusers/online";
    public static String URL_LIST_NEWKURIR_LOCATIONBASED = HOST + "/tusers/new";

    public static String URL_USER_CITY_UPDATE =   API_HOST + "/tuser/city/update";
    public static  String URL_ACCOUNT_STATUS_P = HOST + "/tuser/status";
    public static String URL_KURIR_APPROVED =  HOST + "/tuser/kurir/approved";

    public static  String URL_SENT_MESSAGE = HOST + "/send_message";
    public static  String URL_SENT_KERJASAMA= HOST + "/message/kerjasama";

    public static String URL_DOSEND_ORDER = API_HOST + "/torder/dosend";
    public static String URL_TORDER_HISTORY = API_HOST + "/torder/history";
    public static String URL_TORDER_ACTION = API_HOST + "/torder/action";
    public static String URL_TORDER_MYORDERS = API_HOST + "/torder/myorders";
    public static String URL_TORDER_REALTIME = API_HOST + "/torder/realtime";
    public static String URL_TORDER_MYTASKS = API_HOST + "/torder/myjob";
    public static String URL_TORDER_ADDPIC = API_HOST + "/torder/addpic";;
    public static String URL_TORDER_REJECT = API_HOST + "/torder/reject";
    public static String URL_TORDER_RETRIEVE_AWB = API_HOST + "/torder/awb";

    public static String URL_PRICE_REQUEST = API_HOST + "/tariff/do";
    public static String URL_NEWS= HOST + "/news";
    public static String URL_RETRIEVE_BRANCHES = API_HOST + "/branch/service";
    public static String URL_RETRIEVE_SERVICES = API_HOST + "/do/services";
    public static String URL_RETRIEVE_CAR_VEHICLES = API_HOST + "/do/cars/vehicle/list";


    //active link before v1 API
    // Server user login url
    public static String URL_LOGIN = HOST + "/backend/login/json";
    public static String URL_REGISTER = HOST + "/backend/signup/json";

    public static  String URL_ACCOUNT_ACTIVATION = HOST + "/activation";
    public static String URL_ACCOUNT_RECOVERY = HOST + "/recovery";
    public static  String URL_ACCOUNT_STATUS = HOST + "/user/status";

    public static String URL_PLACE_ORDER = HOST + "/packet/addordernew";
    public static String URL_PLACE_ORDER_SHOP = HOST + "/shop/addordernew";

    public static String URL_ORDER_ADDPIC = HOST + "/order/addpic";;
    public static String URL_ORDER_REJECT = HOST + "/order/reject";
    public static String URL_ORDER_ACTION = HOST + "/order/action";
    public static String URL_ORDER_REALTIME = HOST + "/order/realtime";
    public static String URL_ORDER_MYTASKS = HOST + "/order/myjob";
    public static String URL_ORDER_MYORDERS = HOST + "/order/myorders";
    public static String URL_ORDER_HISTORY = HOST + "/order/history";

    public static String URL_PACKET_TRACE= HOST + "/packet/trace";
    public static String URL_PACKET_PRICE= HOST + "/packet/tariff";
    public static String URL_PACKET_PRICE_GROUP = HOST + "/packet/tariffgroup";
    public static String URL_PACKET_REALTIME= HOST + "/packet/realtime";
    public static String URL_PACKET_MYTASKS= HOST + "/packet/myjob";
    public static String URL_PACKET_ACTION = HOST + "/packet/action";
    public static String URL_PACKET_HISTORY = HOST + "/packet/history";

    public static String URL_LIST_CITY = HOST + "/city/listbytype/{type}/{parent}";
    public static String URL_LIST_CITY_KURINDO = HOST + "/city/list/kurir";
    public static String URL_LIST_KURIR = HOST + "/users/online/{type}";
    public static String URL_LIST_NEWKURIR = HOST + "/users/new/{type}";



    public static final String KEY_KURIR= "KURIR";
    public static final String KEY_AGENT= "AGEN";
    public static final String KEY_PELANGGAN= "PELANGGAN";
    public static final String KEY_SHOPPIC= "SHOPPIC";
    public static final String KEY_SHOPRESS = "SHOPRESS";
    public static final String KEY_SHOPKURIR= "SHOPKURIR";
    public static final String KEY_KURIRSHOP= "KURIRSHOP";
    public static final String KEY_SHOPKEC= "SHOPKEC";
    public static final String KEY_SHOPKAB= "SHOPKAB";
    public static final String KEY_SHOPPROV = "SHOPPROV";
    public static final String KEY_SHOPNEG= "SHOPNEG";
    public static final String KEY_MITRA = "MITRA";

    public static final String KEY_OPERATOR= "OPERATOR";
    public static final String KEY_ADMINISTRATOR= "ADMIN";
    public static final String KEY_ADMINKEC= "ADMINKEC";
    public static final String KEY_ADMINKAB= "ADMINKAB";
    public static final String KEY_ADMINPROV = "ADMINPROV";
    public static final String KEY_ADMINNEG= "ADMINNEG";
    public static final String KEY_ROOT= "ROOT";

    public static final String KEY_KUR050= "KUR050";
    public static final String KEY_KUR100= "KUR100";
    public static final String KEY_KUR101= "KUR101";
    public static final String KEY_KUR200= "KUR200";
    public static final String KEY_KUR300= "KUR300";
    public static final String KEY_KUR310= "KUR310";
    public static final String KEY_KUR350= "KUR350";
    public static final String KEY_KUR400= "KUR400";
    public static final String KEY_KUR500= "KUR500";
    public static final String KEY_KUR900= "KUR900";
    public static final String KEY_KUR910= "KUR910";
    public static final String KEY_KUR999= "KUR999";
    public static final String PACKET_SDS = "SDS";
    public static final String PACKET_NDS = "NDS";
    public static final String PACKET_ENS = "ENS";
    public static final String PACKET_NNS = "NNS";
    public static final String KEY_DOSEND = "DO-SEND";
    public static final String KEY_DOJEK = "DO-JEK";
    public static final String KEY_DOWASH = "DO-WASH";
    public static final String KEY_DOSERVICE = "DO-SERVICE";
    public static final String KEY_DOHIJAMAH = "DO-HIJAMAH";
    public static final String KEY_DOCAR = "DO-CAR";
    public static final String KEY_DOMOVE = "DO-MOVE";
    public static final String KEY_DOSHOP = "DO-SHOP";
    public static final String KEY_DOMART = "DO-MART";

    public static final String KEY_RENTAL = "RENTAL";
    public static final String KEY_WISATA = "WISATA";

    public static final String KEY_CHARGE = "CHARGE";

    public static final String DEFAULT_COUNTRY = "ID";
    public static final String CLOSED = "CLOSED";
    public static final String OPEN = "OPEN";
    public static final String ISI_SALDO = "ISI SALDO";
    public static int MAX_DOSEND_COVERAGE_KM = 35000;
    public static int MAX_DOMOVE_COVERAGE_KM = 150000;
    public static int MAX_DOJEK_COVERAGE_KM = 50000;
    public static int MAX_DOCAR_COVERAGE_KM = 300000;
    public static float DEFAULT_TILT_MAP = 0;
    public static float DEFAULT_ZOOM_MAP = 15f;
    public static float MAP_ZOOM_OUT = 10f;

    public static SimpleDateFormat sdf;
    public static float BRAND_MAX_WIDTH = 150;
    public static float BRAND_MAX_HEIGHT = 150;
    public static float BACKDROP_MAX_WIDTH = 388;
    public static float BACKDROP_MAX_HEIGHT = 160;
    public static float BANNER_MAX_WIDTH = 388;
    public static float BANNER_MAX_HEIGHT = 160;
    public static int START_SDS = 5;
    public static int START_ENS = 18;
    public static int END_ENS = 22;
    public static int MIN_WEIGHT_DOWASH = 3;
    public static int MIN_WEIGHT_DOSEND = 3;
    public static String CASH_PAYMENT = "TUNAI";
    public static String HOMEBASE = "HOMEBASE";

    public static String DOMOVE_PICKUP_BAK = "PICKUP_BAK";
    public static String DOMOVE_BLIND_VAN = "BLIND_VAN";
    public static String DOMOVE_ENGKEL_BAK = "ENGKEL_BAK";
    public static String DOMOVE_ENGKEL_BOX = "ENGKEL_BOX";

    public static BigDecimal DOMART_FEE = new BigDecimal(2500);

    public static boolean isNightService(String service){
        return service.equalsIgnoreCase(PACKET_ENS) || service.equalsIgnoreCase(PACKET_NNS);
    }
    public static String getStatusText(String status){
        String statusText = "";
        switch (status){
            case KEY_KUR050:
                statusText = "PAKET MENUNGGU PESANAN SIAP.";
                break;
            case KEY_KUR100:
                statusText = "PAKET TERSEDIA KEMBALI.";
                break;
            case KEY_KUR101:
                statusText = "PAKET DIBOOKING KURIR. SIAP MEMBANTU.";
                break;
            case KEY_KUR200:
                statusText= "KURIR KURINDO OTW MENGAMBIL PAKET.";
                break;
            case KEY_KUR300:
                statusText= "PAKET SUDAH DIBAWA KURIR.";
                break;
            case KEY_KUR310:
                statusText= "KURIR OTW MENGANTAR PAKET.";
                break;
            case KEY_KUR350:
                statusText= "PAKET DIKIRIM ULANG KE TUJUAN.";
                break;
            case KEY_KUR400:
                statusText= "PAKET TERTUNDA. PENERIMA TIDAK DITEMPAT.";
                break;
            case KEY_KUR500:
                statusText= "PAKET SUDAH DITERIMA (SAMPAI DI TUJUAN).";
                break;
            case KEY_KUR900:
                statusText= "PAKET DIBATALKAN.";
                break;
            case KEY_KUR910:
                statusText= "PAKET DIKEMBALIKAN KE PENGIRIM.";
            default:
        }
        return statusText;
    }

    public static final String CURRENCY = "Rp ";
    public static String KilometerShort = " km";

    public static String formatCurrency(double amount)
    {
       return CURRENCY+ ""+formatAmmount(amount);
    }
    public static String formatAmmount(double amount)
    {
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setGroupingUsed(true);
        //Currency currency = Currency.getInstance(Constant.CURRENCY);
        //format.setCurrency(currency);
        return format.format(amount);
    }
    public static String formatDate(String pdate)
    {
        SimpleDateFormat format = new SimpleDateFormat("DD-MMM-YYYY HH:mm:ss");
        String formated = "";
        try {
            Date dt = format.parse(pdate);
            formated = format.format(dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formated;
    }

    public static String getOrderStatusText(String status) {
        String statusText = "";
        switch (status){
            case KEY_KUR050:
                statusText = "PESANAN MASIH DISIAPKAN.";
                break;
            case KEY_KUR100:
                statusText = "PESANAN MASUK TERCATAT.";
                break;
            case KEY_KUR101:
                statusText = "PESANAN DALAM PROSES.";
                break;
            case KEY_KUR200:
                statusText= "PESANAN SIAP DIKIRIM.";
                break;
            case KEY_KUR300:
                statusText= "PESANAN SUDAH DIBAWA KURIR.";
                break;
            case KEY_KUR310:
                statusText= "KURIR OTW MENGANTAR PESANAN.";
                break;
            case KEY_KUR350:
                statusText= "PESANAN DIKIRIM ULANG KE TUJUAN.";
                break;
            case KEY_KUR400:
                statusText= "PESANAN TERTUNDA. PENERIMA TIDAK DITEMPAT.";
                break;
            case KEY_KUR500:
                statusText= "PESANAN SUDAH DITERIMA (SAMPAI DI TUJUAN).";
                break;
            case KEY_KUR900:
                statusText= "PESANAN DIBATALKAN.";
                break;
            case KEY_KUR910:
                statusText= "PESANAN DIKEMBALIKAN KE PENGIRIM.";
            case KEY_KUR999:
                statusText= "PESANAN DITOLAK.";
            default:
        }
        return statusText;
    }
    public static List<PacketService> getPacketServiceList(String doType) {
        List<PacketService> packetServiceList = new ArrayList<>();
        PacketService service = new PacketService("SDS", "Same Day Service", (doType!=null && doType.equalsIgnoreCase(KEY_DOSEND)? "Kiriman ":"Layanan ")+"Express (hari yang sama)");
        packetServiceList.add(service);

        PacketService service3 = new PacketService("ENS", "Extra Night Service", (doType!=null && doType.equalsIgnoreCase(KEY_DOSEND)? "Kiriman ":"Layanan ")+"Express setelah jam 6 malam");
        packetServiceList.add(service3);

        PacketService service2 = new PacketService("NDS", "Next Day Service", (doType!=null && doType.equalsIgnoreCase(KEY_DOSEND)? "Kiriman ":"Layanan ")+"Ekonomis untuk Esok hari");
        packetServiceList.add(service2);

        PacketService service4 = new PacketService("NNS", "NextDay Night Service", (doType!=null && doType.equalsIgnoreCase(KEY_DOSEND)? "Kiriman ":"Layanan ")+"setelah jam 6 malam Esok hari");
        packetServiceList.add(service4);

        return packetServiceList;
    }
    public static int getResourceId(String resourceName, String type){
        return Resources.getSystem().getIdentifier(resourceName, type, null);
    }

    private static String bannerUrl =BASE_HOST+"/img/banner/";
    private static String shopUrl =BASE_HOST+"/img/shop/";
    private static String vehicleUrl =BASE_HOST+"/img/vehicle/";

    public static String urlBannerImage(String banner) {
        return bannerUrl+""+banner;
    }

    public static String urlShopImage(String image) {
        return shopUrl +""+image;
    }

    public static String urlProductImage(String imageName) {
        return shopUrl + ""+imageName;
    }

    public static String urlVehicleImage(String image) {
        return vehicleUrl + ""+image;
    }

    public static Bitmap encodeContentsToBarcode(String data, BarcodeFormat format) {
        MultiFormatWriter writer =new MultiFormatWriter();


        String finaldata = Uri.encode(data, "utf-8");

        BitMatrix bm = null;
        try {
            bm = writer.encode(finaldata, format,250, 250);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int width = 250; int height = 250;
        Bitmap ImageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < width; i++) {//width
            for (int j = 0; j < height; j++) {//height
                ImageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK: Color.WHITE);
            }
        }

        return ImageBitmap;
    }


    public static String get_api_with_secret(String url){

        return "";
    }

    public static String getTimeAgo(String dateInput) {
        String timeformat = dateInput;
        try {
            long now = System.currentTimeMillis();
            //SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
            SimpleDateFormat dateFormat = getDateTimeServerFormat();
            Date convertedDate = dateFormat.parse(dateInput);
            CharSequence relavetime1 = DateUtils.getRelativeTimeSpanString(
                    convertedDate.getTime(),
                    now,
                    DateUtils.SECOND_IN_MILLIS);
            timeformat = String.valueOf(relavetime1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeformat;
    }

    public static void startActivity(String className, Bundle extras, Context context){
        Class cls = null;
        try {
            cls = Class.forName(className);
        }catch(ClassNotFoundException e){
            //means you made a wrong input in firebase console
        }
        Intent i = new Intent(context, cls);
        i.putExtras(extras);
        context.startActivity(i);
    }

    public static SimpleDateFormat getDateTimeServerFormat(){
        if(sdf == null) sdf = new SimpleDateFormat("yyyy-MM-dd HH:m:s");
        //if(sdf == null) sdf = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
        return sdf;
    }
    public static SimpleDateFormat getDateFormat(){
        if(sdf == null) sdf = new SimpleDateFormat("dd-MMM-yyyy");
        //if(sdf == null) sdf = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
        return sdf;
    }
    public static SimpleDateFormat getDateTimeFormat(){
        if(sdf == null) sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        //if(sdf == null) sdf = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
        return sdf;
    }

    public static String formatKmDecimal(double amount) {
        DecimalFormat format = new DecimalFormat(".##");
        format.setGroupingUsed(true);
        format.setRoundingMode(RoundingMode.UP);
        return format.format(amount) + KilometerShort;
    }
    public static String formatKm(double amount)
    {
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setGroupingUsed(true);
        //Currency currency = Currency.getInstance(Constant.CURRENCY);
        //format.setCurrency(currency);
        String str = format.format(amount) + " m";
        if(amount > 1000){
            str = format.format(amount/1000) + " km";
        }
        return str;
    }

    public static String pad(int c) {
        if(c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    public static String formatPickup(int hour, int minute, String serviceCode) {
        Calendar c = Calendar.getInstance();
        if(serviceCode.equalsIgnoreCase(AppConfig.PACKET_NDS)){
            c.add(Calendar.DATE, 1);
        }
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        return getDateTimeServerFormat().format(c.getTime());
    }

    public static List<Vehicle> getDoCarServices() {
        List<Vehicle> datas = new ArrayList<>();
        datas.add(new Vehicle("Shuttle - Antar - Jemput", "Layanan antar jemput dari suatu tempat ke tujuan yang diinginkan.", "Avanza/Xenia, Innova", null, "do_car_icon.png"));
        datas.add(new Vehicle("Rental Mobil", "Layanan sewa mobil untuk dalam kota maupun luar kota.", "Avanza/Xenia, Innova", null, "avanza1.png"));
        //datas.add(new Vehicle("Wisata", "Layanan sewa armada untuk wisata.", "Bus, Bison", null, "bus1"));
        return datas;
    }

    public static List<Vehicle> getDoCarWisataServices() {
        List<Vehicle> datas = new ArrayList<>();
        datas.add(new Vehicle("Bus Besar", "Layanan wisata jarak jauh dan dekat dengan nyaman.", "Hino, Marcedes Benz", null, "bus1.png"));
        datas.add(new Vehicle("Bus Sedang", "Layanan wisata jarak dekat.", "Bus", null, "bus2.png"));
        datas.add(new Vehicle("Bus Kecil", "Layanan wisata jarak dekat.", "AC", null, "bus4.png"));
        datas.add(new Vehicle("Bison", "Layanan wisata jarak dekat untuk keluarga.", "AC", null, "elf1.png"));
        return datas;
    }

    public static List<Vehicle> getDoCarRentalServices() {
        List<Vehicle> datas = new ArrayList<>();
        Vehicle v1 = new Vehicle("Innova", "Layanan nyaman.", "A, B, C", null, "do_car_icon");
        v1.setRating(5);
        v1.setAc(5);
        v1.setKebersihan(5);
        v1.setKondisi(5);
        v1.setBbm("Bensin");
        v1.setTahun("2015");
        v1.setMerk("Toyota");
        v1.setJenis("MPV");
        v1.setTransmisi("Manual");
        v1.setDayamuat("6");
        datas.add(v1);
        v1 = new Vehicle("Avanza", "Layanan nyaman.", "G", null, "do_car_icon");
        v1.setRating(5);
        v1.setAc(5);
        v1.setKebersihan(5);
        v1.setKondisi(5);
        v1.setBbm("Bensin");
        v1.setTahun("2015");
        v1.setMerk("Toyota");
        v1.setJenis("MPV");
        v1.setTransmisi("Manual");
        v1.setDayamuat("6");
        datas.add(v1);
        v1 = new Vehicle("Xenia", "Layanan nyaman.", "L", null, "do_car_icon");
        v1.setRating(5);
        v1.setAc(5);
        v1.setKebersihan(5);
        v1.setKondisi(5);
        v1.setBbm("Bensin");
        v1.setTahun("2015");
        v1.setMerk("Daihatsu");
        v1.setJenis("MPV");
        v1.setTransmisi("Manual");
        v1.setDayamuat("6");
        datas.add(v1);
        return datas;
    }

    public static List<Vehicle> getDoMoveServices() {
        List<Vehicle> datas = new ArrayList<>();
        datas.add(new Vehicle("PICKUP BAK", "Mobil Pickup dengan bak.", "Ukuran 200x130x120", "Berat Maks 1000Kg", "do_move_icon"));
        datas.add(new Vehicle("PICKUP BOX / BLIND VAN", "Mobil Pickup dengan box.", "Ukuran 200x130x130", "Berat Maks 1000Kg", "do_move_icon"));
        datas.add(new Vehicle("ENGKEL BAK", "Mobil Pickup dengan bak.", "Ukuran 300x160x130", "Berat Maks 2000Kg", "do_move_icon"));
        datas.add(new Vehicle("ENGKEL BOX", "Mobil Box tertutup.", "Ukuran 300x160x160", "Berat Maks 2000Kg", "do_move_icon"));
        return datas;
    }

}
