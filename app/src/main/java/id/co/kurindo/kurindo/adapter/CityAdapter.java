package id.co.kurindo.kurindo.adapter;

/**
 * Created by DwiM on 11/9/2016.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.model.City;
import id.co.kurindo.kurindo.model.News;

/**
 * Created by DwiM on 11/9/2016.
 */
public class CityAdapter extends BaseAdapter {

    Context context;
    List<City> data = new ArrayList<>();
    private String[] bgColors;

    public CityAdapter(Context context, List<City> data) {
        this.context = context;
        this.data = data;
        bgColors = context.getResources().getStringArray(R.array.list_bg);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_city, parent, false);
        TextView code = (TextView) v.findViewById(R.id.city_code);
        TextView text = (TextView) v.findViewById(R.id.city_text);

        code.setText(String.valueOf(data.get(position).getCode()));
        text.setText(data.get(position).getText());

        String color = bgColors[position % bgColors.length];
        code.setBackgroundColor(Color.parseColor(color));
        return v;
    }
}