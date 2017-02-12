package id.co.kurindo.kurindo.adapter;

/**
 * Created by DwiM on 11/9/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.model.Payment;

/**
 * Created by DwiM on 11/9/2016.
 */
public class PaymentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Payment> data = new ArrayList<>();
    private String[] bgColors;
    int selected = -1;
    private OnItemClickListener itemClickListener;

    public PaymentAdapter(Context context, List<Payment> data, OnItemClickListener mOnItemClickListener) {
        this.context = context;
        this.data = data;
        this.itemClickListener = mOnItemClickListener;
        bgColors = context.getResources().getStringArray(R.array.list_bg);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_payment, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Payment payment = data.get(position);
        ((MyItemHolder) holder).titleText.setText( payment.getText() );
        if(payment.getDescription() == null || payment.getDescription().isEmpty()) {
            ((MyItemHolder) holder).descriptionText.setVisibility(View.GONE);
        }else{
            ((MyItemHolder) holder).descriptionText.setVisibility(View.VISIBLE);
            ((MyItemHolder) holder).descriptionText.setText( payment.getDescription() );
        }
        if(payment.getAction() == null || payment.getAction().isEmpty()){
            ((MyItemHolder) holder).actionText.setVisibility(View.GONE);
        }else{
            ((MyItemHolder) holder).actionText.setVisibility(View.VISIBLE);
            ((MyItemHolder) holder).actionText.setText(payment.getAction());
            ((MyItemHolder) holder).actionText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onActionButtonClick(v, position);
                }
            });
        }
        //String color = bgColors[position % bgColors.length];
        ((MyItemHolder) holder).radioButton.setChecked(position == selected);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public Payment getItem(int pos){
        return data.get(pos);
    }
    public void selected(int pos){
        selected = pos;
        notifyItemRangeChanged(0, data.size());
    }
    public  int getSelectedPosition(){
        return selected;
    }
    public static class MyItemHolder extends RecyclerView.ViewHolder {
        protected TextView titleText;
        protected TextView descriptionText;
        protected TextView actionText;
        RadioButton radioButton;

        public MyItemHolder(View itemView) {
            super(itemView);
            this.titleText= (TextView) itemView.findViewById(R.id.text);
            this.descriptionText= (TextView) itemView.findViewById(R.id.description);
            this.actionText= (TextView) itemView.findViewById(R.id.action);
            this.radioButton= (RadioButton) itemView.findViewById(R.id.radio_selected);
        }
    }

    public interface OnItemClickListener {
        void onActionButtonClick(View view, int position);
    }
}