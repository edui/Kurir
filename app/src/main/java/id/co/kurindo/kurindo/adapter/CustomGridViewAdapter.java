package id.co.kurindo.kurindo.adapter;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.model.ImageModel;

/**
 * Created by DwiM on 11/16/2016.
 */

public class CustomGridViewAdapter extends ArrayAdapter<AppCompatButton> {
    Context context;
    ArrayList<AppCompatButton> data = new ArrayList<AppCompatButton>();

    public CustomGridViewAdapter(Context context, int layoutResourceId,
                                 ArrayList<AppCompatButton> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AppCompatButton item = data.get(position);

        return item;
    }
}


