package id.co.kurindo.kurindo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.co.kurindo.kurindo.adapter.ButtonAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.base.RecyclerItemClickListener;
import id.co.kurindo.kurindo.map.MapsActivity;
import id.co.kurindo.kurindo.model.ImageModel;
import id.co.kurindo.kurindo.model.News;
import id.co.kurindo.kurindo.model.Product;
import id.co.kurindo.kurindo.task.ListenableAsyncTask;

/**
 * Created by DwiM on 11/9/2016.
 */
public class HomeFragment extends Fragment implements  BaseSliderView.OnSliderClickListener {
    ButtonAdapter mAdapter;
    RecyclerView mRecyclerView;
    ArrayList<ImageModel> data = new ArrayList<>();
    private ListenableAsyncTask loadNewsTask;
    SliderLayout sliderShow;
    int reloadCounter = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_layout, null);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);

        sliderShow = (SliderLayout) view.findViewById(R.id.slider);
        //sliderShow.setCustomIndicator((PagerIndicator) view.findViewById(R.id.custom_indicator));
        /*
        List<News> banners = AppController.banners;
        if(banners != null){
            if(banners.size() > 0 && (reloadCounter < 2 || reloadCounter > 7)){
                sliderShow.removeAllSliders();
                for (int i = 0; i < banners.size(); i++) {
                    DefaultSliderView sliderView = new DefaultSliderView(view.getContext());
                    sliderView
                            .image(banners.get(i).getImg())
                            .setScaleType(BaseSliderView.ScaleType.FitCenter);
                            //.setOnSliderClickListener(this);
                    sliderView.setOnImageLoadListener(new BaseSliderView.ImageLoadListener() {
                        @Override
                        public void onStart(BaseSliderView baseSliderView) {
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onEnd(boolean b, BaseSliderView baseSliderView) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    sliderView.bundle(new Bundle());
                    sliderView.getBundle().putString("extra", banners.get(i).getTitle());
                    sliderShow.addSlider(sliderView);
                }
                if(reloadCounter > 7) reloadCounter = 0;
            }
            reloadCounter++;
        }else{
        */
            DefaultSliderView sliderView = new DefaultSliderView(view.getContext());
            sliderView
                    .image(R.drawable.banner_mobile_application_0)
                    .setScaleType(BaseSliderView.ScaleType.FitCenter);
                    //.setOnSliderClickListener(this);
            sliderView.setOnImageLoadListener(new BaseSliderView.ImageLoadListener() {
                @Override
                public void onStart(BaseSliderView baseSliderView) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(boolean b, BaseSliderView baseSliderView) {
                    progressBar.setVisibility(View.GONE);
                }
            });
            sliderView.bundle(new Bundle());
            sliderView.getBundle().putString("extra", "Kurindo");
            sliderShow.addSlider(sliderView);

            sliderView = new DefaultSliderView(view.getContext());
            sliderView
                    .image(R.drawable.banner_mobile_application_1)
                    .setScaleType(BaseSliderView.ScaleType.FitCenter);
            //.setOnSliderClickListener(this);
            sliderView.setOnImageLoadListener(new BaseSliderView.ImageLoadListener() {
                @Override
                public void onStart(BaseSliderView baseSliderView) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(boolean b, BaseSliderView baseSliderView) {
                    progressBar.setVisibility(View.GONE);
                }
            });
            sliderView.bundle(new Bundle());
            sliderView.getBundle().putString("extra", "Kurindo");
            sliderShow.addSlider(sliderView);

        sliderView = new DefaultSliderView(view.getContext());
        sliderView
                .image(R.drawable.banner_mobile_10)
                .setScaleType(BaseSliderView.ScaleType.FitCenter);
        //.setOnSliderClickListener(this);
        sliderView.setOnImageLoadListener(new BaseSliderView.ImageLoadListener() {
            @Override
            public void onStart(BaseSliderView baseSliderView) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEnd(boolean b, BaseSliderView baseSliderView) {
                progressBar.setVisibility(View.GONE);
            }
        });
        sliderView.bundle(new Bundle());
        sliderView.getBundle().putString("extra", "Kurindo");
        sliderShow.addSlider(sliderView);


        sliderView = new DefaultSliderView(view.getContext());
        sliderView
                .image(R.drawable.banner_mobile_11)
                .setScaleType(BaseSliderView.ScaleType.FitCenter);
        //.setOnSliderClickListener(this);
        sliderView.setOnImageLoadListener(new BaseSliderView.ImageLoadListener() {
            @Override
            public void onStart(BaseSliderView baseSliderView) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEnd(boolean b, BaseSliderView baseSliderView) {
                progressBar.setVisibility(View.GONE);
            }
        });
        sliderView.bundle(new Bundle());
        sliderView.getBundle().putString("extra", "Kurindo");
        sliderShow.addSlider(sliderView);
            //Toast.makeText(getContext(), "Could not connect to server.", Toast.LENGTH_SHORT).show();
        //}
        List<News> banners = AppController.banners;
        if(banners != null){
            for (int i = 0; i < banners.size(); i++) {
                sliderView = new DefaultSliderView(view.getContext());
                sliderView.image(banners.get(i).getImg())
                        .setScaleType(BaseSliderView.ScaleType.FitCenter);
                //.setOnSliderClickListener(this);
                sliderView.setOnImageLoadListener(new BaseSliderView.ImageLoadListener() {
                    @Override
                    public void onStart(BaseSliderView baseSliderView) {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onEnd(boolean b, BaseSliderView baseSliderView) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
                sliderView.bundle(new Bundle());
                sliderView.getBundle().putString("extra", banners.get(i).getTitle());
                sliderShow.addSlider(sliderView);
            }
        }

        setupMenuData();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new ButtonAdapter(getContext(), data);
        mRecyclerView.setAdapter(mAdapter);

        //mRecyclerView.addOnItemTouchListener(getRecyclerItemClickListener());
        mRecyclerView.addOnItemTouchListener(getRecyclerItemClickListener2());
        return  view;
    }
    public RecyclerItemClickListener getRecyclerItemClickListener2(){
        return new RecyclerItemClickListener(getContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ImageModel model = data.get(position);
                        Bundle bundle = new Bundle();
                        bundle.putInt("do_type", model.getDrawable());
                        ((BaseActivity)getActivity()).showActivity(MapsActivity.class, bundle);
                    }
                });
    }

    public RecyclerItemClickListener getRecyclerItemClickListener(){
        return new RecyclerItemClickListener(getContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ImageModel model = data.get(position);
                        //Toast.makeText(getContext(), model.getName(), Toast.LENGTH_LONG).show();
                        if(model.getDrawable() == R.drawable.do_send_icon){
                            ((MainDrawerActivity)getActivity()).showActivity(PacketOrderActivity.class);
                        }else if(model.getDrawable() == R.drawable.do_jek_icon){
                            Bundle bundle = new Bundle();
                            Product product = new Product();
                            product.setId(1);
                            product.setShopid(1);
                            product.setType("A");
                            product.setName("Ojek Antar");
                            product.setDescription(
                                    "- Tarif per KM Rp 1500, (acuan GoogleMaps)\n" +
                                            "- Ojek laki-laki untuk penumpang laki-laki\n" +
                                            "- Ojek perempuan untuk penumpang perempuan (booking paling lambat 6jam sebelumnya)\n"
                            );
                            product.setCode(AppConfig.KEY_DOJEK);
                            product.setQuantity(1);
                            product.setCreated(AppConfig.getSimpleDateFormat().format(new Date()));
                            product.setPrice(new BigDecimal(0));
                            product.setDrawable(model.getDrawable());
                            //bundle.putSerializable("product", product);
                            bundle.putParcelable("product", product);
                            ((MainDrawerActivity)getActivity()).showActivity(SimpleOrderActivity.class, bundle);
                        }else if(model.getDrawable() == R.drawable.do_wash_icon){
                            Bundle bundle = new Bundle();
                            Product product = new Product();
                            product.setId(4);
                            product.setShopid(1);
                            product.setType("A");
                            product.setName("Jasa Cuci");
                            product.setCode(AppConfig.KEY_DOWASH);
                            product.setDescription(
                                    "- Order Minimal 4 Kg\n" +
                                            "- Harga per Kg = Rp 10.000,-\n" +
                                            "- Harga sudah termasuk ongkos kirim (ambil & antar)\n"
                            );
                            product.setQuantity(1);
                            product.setCreated(AppConfig.getSimpleDateFormat().format(new Date()));
                            product.setPrice(new BigDecimal(0));
                            product.setDrawable(model.getDrawable());
                            //bundle.putSerializable("product", product);
                            bundle.putParcelable("product", product);
                            ((MainDrawerActivity)getActivity()).showActivity(SimpleOrderActivity.class, bundle);
                        }else if(model.getDrawable() == R.drawable.do_service_icon){
                            Bundle bundle = new Bundle();
                            Product product = new Product();
                            product.setId(5);
                            product.setShopid(1);
                            product.setType("A");
                            product.setName("Servis AC");
                            product.setCode(AppConfig.KEY_DOSERVICE);
                            product.setDescription(
                                    "- Biaya CUCI AC mulai dari Rp 70.000,- (tergantung BTU/PK unit AC)\n" +
                                            "- Biaya ISI FREON mulai dari Rp 125.000,- (tergantung BTU/PK unit AC)\n" +
                                            "- Garansi servis selama 2 minggu\n" +
                                            "- Melayani perbaikan dan pemasangan atau pemindahan AC\n"
                            );
                            product.setQuantity(1);
                            product.setCreated(AppConfig.getSimpleDateFormat().format(new Date()));
                            product.setPrice(new BigDecimal(0));
                            product.setDrawable(model.getDrawable());
                            //bundle.putSerializable("product", product);
                            bundle.putParcelable("product", product);
                            ((MainDrawerActivity)getActivity()).showActivity(SimpleOrderActivity.class, bundle);
                        }else if(model.getDrawable() == R.drawable.do_hijamah_icon){
                            Bundle bundle = new Bundle();
                            Product product = new Product();
                            product.setId(6);
                            product.setShopid(1);
                            product.setType("A");
                            product.setName("Terapi Hijamah");
                            product.setCode(AppConfig.KEY_DOHIJAMAH);
                            product.setDescription("");
                            product.setQuantity(1);
                            product.setCreated(AppConfig.getSimpleDateFormat().format(new Date()));
                            product.setPrice(new BigDecimal(0));
                            product.setDrawable(model.getDrawable());
                            //bundle.putSerializable("product", product);
                            bundle.putParcelable("product", product);
                            ((MainDrawerActivity)getActivity()).showActivity(SimpleOrderActivity.class, bundle);
                        }else if(model.getDrawable() == R.drawable.do_car_icon){
                            Bundle bundle = new Bundle();
                            Product product = new Product();
                            product.setId(2);
                            product.setShopid(1);
                            product.setType("A");
                            product.setName("Rental Mobil");
                            product.setCode(AppConfig.KEY_DOCAR);
                            product.setDescription(
                                    "- Tarif sewa Mobil per hari Dalam Kota (24jam) Rp 300.0000,-\n" +
                                            "- Tarif Sopir per hari Rp 150.000,-\n" +
                                            "- Tidak termasuk BBM & Makan Sopir\n" +
                                            "- Booking paling lambat 1 hari sebelumnya\n" +
                                            "- Armada yang tersedia: Avansa, Xenia, Innova, Ertiga3. Untuk tipe lain mohon konfirmasi terlebih dahulu.\n"
                            );
                            product.setQuantity(1);
                            product.setCreated(AppConfig.getSimpleDateFormat().format(new Date()));
                            product.setPrice(new BigDecimal(0));
                            product.setDrawable(model.getDrawable());
                            //bundle.putSerializable("product", product);
                            bundle.putParcelable("product", product);
                            ((MainDrawerActivity)getActivity()).showActivity(SimpleOrderActivity.class, bundle);
                        }else if(model.getDrawable() == R.drawable.do_move_icon){
                            Bundle bundle = new Bundle();
                            Product product = new Product();
                            product.setId(7);
                            product.setShopid(1);
                            product.setType("A");
                            product.setName("Jasa Pindah");
                            product.setCode(AppConfig.KEY_DOMOVE);
                            product.setDescription(
                                    "- Tarif angkut sekali jalan Rp 100.000,-\n" +
                                            "- Sewa angkut 24 jam mulai Rp 300.000,-\n"
                            );
                            product.setQuantity(1);
                            product.setCreated(AppConfig.getSimpleDateFormat().format(new Date()));
                            product.setPrice(new BigDecimal(0));
                            product.setDrawable(model.getDrawable());
                            //bundle.putSerializable("product", product);
                            bundle.putParcelable("product", product);
                            ((MainDrawerActivity)getActivity()).showActivity(SimpleOrderActivity.class, bundle);
                        }else if(model.getDrawable() == R.drawable.doclient_icon){
                            Bundle bundle = new Bundle();
                            bundle.putString("class", "kerjasama");
                            ((MainDrawerActivity)getActivity()).showActivity(KerjasamaActivity.class, bundle);
                        }
                    }
                });
    }
    private void setupMenuData() {
        data.clear();
        ImageModel model1 = new ImageModel(R.drawable.do_send_icon, "Kirim Paket");
        data.add(model1);
        ImageModel model2 = new ImageModel(R.drawable.do_jek_icon, "Ojek Antar");
        data.add(model2);
        ImageModel model3 = new ImageModel(R.drawable.do_wash_icon, "Jasa Cuci");
        data.add(model3);
        ImageModel model4 = new ImageModel(R.drawable.do_service_icon, "Service AC");
        data.add(model4);
        ImageModel model5 = new ImageModel(R.drawable.do_hijamah_icon, "Terapi Hijamah");
        data.add(model5);
        ImageModel model6 = new ImageModel(R.drawable.do_car_icon, "Auto Rental");
        data.add(model6);
        ImageModel model7 = new ImageModel(R.drawable.do_move_icon, "Jasa Pindah");
        data.add(model7);
        ImageModel model8 = new ImageModel(R.drawable.doclient_icon, "Kerjasama");
        data.add(model8);
    }

    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(getContext(), slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderShow.startAutoCycle();
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderShow.stopAutoCycle();
    }
}
