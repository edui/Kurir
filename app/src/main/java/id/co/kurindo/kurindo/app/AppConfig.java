package id.co.kurindo.kurindo.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import id.co.kurindo.kurindo.model.CartItem;
import id.co.kurindo.kurindo.model.Order;
import id.co.kurindo.kurindo.model.Packet;
import id.co.kurindo.kurindo.model.Product;
import id.co.kurindo.kurindo.model.Recipient;
import id.co.kurindo.kurindo.model.User;

/**
 * Created by DwiM on 11/8/2016.
 */
public class AppConfig {
    public static String HOST = "https://kurindo.co.id";
    //public static String HOST = "http://10.0.2.2/kurindo";
    //public static String HOST = "http://192.168.43.25/kurindo";
    public static String USER_AGENT = "KURINDROID";

    public static String SECRET_KEY = "AIzaSyCIVhOu83YxppEIlQEtqV0KCEW-JFDWAKA-KURINDROID";

    // Server user login url
    public static String URL_LOGIN = HOST + "/backend/login/json";

    // Server user register url
    public static String URL_REGISTER = HOST + "/backend/signup/json";

    public static  String URL_ACCOUNT_ACTIVATION = HOST + "/activation";
    public static String URL_ACCOUNT_RECOVERY = HOST + "/recovery";

    public static  String URL_ACCOUNT_STATUS = HOST + "/user/status";

    public static  String URL_SENT_MESSAGE = HOST + "/send_message";
    public static  String URL_SENT_KERJASAMA= HOST + "/message_kerjasama";

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
    public static String URL_KURIR_APPROVED =  HOST + "/user/kurir/approved";


    public static String URL_NEWS= HOST + "/news";
    public static String URL_SHOP_LIST = HOST + "/shops/list/{page}";
    public static String URL_SHOP_PRODUCTS = HOST + "/shop/{shop_id}/product/{page}";


    public static final String KEY_KURIR= "KURIR";
    public static final String KEY_AGENT= "AGEN";
    public static final String KEY_PELANGGAN= "PELANGGAN";
    public static final String KEY_SHOPOWNER= "SHOPOWNER";
    public static final String KEY_OPERATOR= "OPERATOR";
    public static final String KEY_ADMINISTRATOR= "ADMIN";
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
    public static final String KEY_DOSEND = "DO-SEND";
    public static final String KEY_DOJEK = "DO-JEK";
    public static final String KEY_DOWASH = "DO-WASH";
    public static final String KEY_DOSERVICE = "DO-SERVICE";
    public static final String KEY_DOHIJAMAH = "DO-HIJAMAH";
    public static final String KEY_DOCAR = "DO-CAR";
    public static final String KEY_DOMOVE = "DO-MOVE";
    public static final String KEY_DOSHOP = "DO-SHOP";

    public static final String CLOSED = "CLOSED";
    public static final String OPEN = "OPEN";

    public static SimpleDateFormat sdf;

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

    public static String formatCurrency(double amount)
    {
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setGroupingUsed(true);
        //Currency currency = Currency.getInstance(Constant.CURRENCY);
        //format.setCurrency(currency);
       return Constant.CURRENCY+ ""+format.format(amount);
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

    public static int getResourceId(String resourceName, String type){
        return Resources.getSystem().getIdentifier(resourceName, type, null);
    }

    private static String bannerUrl =HOST+"/img/banner/";
    private static String shopUrl =HOST+"/img/shop/";

    public static String urlBannerImage(String banner) {
        return bannerUrl+""+banner;
    }

    public static String urlShopImage(String image) {
        return shopUrl +""+image;
    }

    public static String urlProductImage(String imageName) {
        return shopUrl + ""+imageName;
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
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
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

    public static SimpleDateFormat getSimpleDateFormat(){
        if(sdf == null) sdf = new SimpleDateFormat("yyyy-MM-dd h:m:s");
        return sdf;
    }
}
