package id.co.kurindo.kurindo.adapter;

/**
 * Created by DwiM on 11/9/2016.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.model.User;

/**
 * Created by DwiM on 11/9/2016.
 */
public class TKurirAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<TUser> data = new ArrayList<>();
   // private String[] bgColors;
    int selected = -1;
    boolean multiple= false;
    private OnItemClickListener itemClickListener;

    public TKurirAdapter(Context context, List<TUser> data, OnItemClickListener mOnItemClickListener) {
        //super(context, data);
        this.context = context;
        this.data = data;
        this.itemClickListener = mOnItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_kurir, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vholder, final int position) {
        MyItemHolder holder = (MyItemHolder) vholder;
        TUser r = data.get(position);

        String joined = "Joined: "+AppConfig.getTimeAgo(r.getCreated_at());
        if(r.getAddress() != null && r.getAddress().getAlamat() != null ){
            holder.alamat.setText(r.getAddress().getAlamat());
        }
        if(r.getAddress() != null && r.getAddress().getCity() != null ){
            holder.city.setText(r.getAddress().toStringKecKab()+"\n"+joined);
        }else{
            holder.city.setText("Unknown"+"\n"+joined);
        }
        holder.name.setText(r.getFirstname() + " "+r.getLastname());
        holder.phone.setText(r.getPhone());
        holder.nik.setText("NIK: "+r.getNik());
        //String skills = "\n ( "+AppConfig.KEY_DOSEND +", "+AppConfig.KEY_DOJEK + " )";
        //holder.simc.setText("SIM C: "+r.getSimc() + (r.getSkills() ==null ? "" : "\n"+r.getSkills()));
        holder.simc.setText("SIM C: "+r.getSimc() );

        if(r.getFirstname().isEmpty()) holder.name.setVisibility(View.GONE);
        if(r.getPhone().isEmpty()) holder.phone.setVisibility(View.GONE);
        if(r.getNik()==null || r.getNik().isEmpty()) holder.nik.setVisibility(View.GONE);
        if(r.getSimc()==null || r.getSimc().isEmpty()) holder.simc.setVisibility(View.GONE);

        //String color = bgColors[position % bgColors.length];
        if(multiple){
            for (int i = 0; i < selectedItems.size(); i++) {
                if(position == selectedItems.keyAt(i)) {
                    holder.radioSelected.setChecked(true);
                    break;
                }
            }
        }else{
            holder.radioSelected.setChecked(position == selected);
        }
        holder.alamat.setBackgroundColor(position == selected? Color.CYAN : Color.TRANSPARENT);
        holder.city.setBackgroundColor(position == selected? Color.CYAN : Color.TRANSPARENT);
        if(itemClickListener != null){
            holder.approvedBtn.setVisibility(View.VISIBLE);
            holder.approvedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onApprovedButtonClick(v, position, fiturMap);
                }
            });

            holder.callBtn.setVisibility(View.VISIBLE);
            holder.callBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onCallButtonClick(v, position);
                }
            });
            holder.removeBtn.setVisibility(View.VISIBLE);
            holder.removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onRemoveButtonClick(v, position);
                }
            });
        }else{
            holder.approvedBtn.setVisibility(View.GONE);
            holder.callBtn.setVisibility(View.GONE);
        }

        setup_checkbox(r, holder);
    }

    private void setup_checkbox(TUser r, MyItemHolder holder) {

        holder.chkDoSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v);
            }
        });
        holder.chkDoJek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v);
            }
        });
        holder.chkDoMart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v);
            }
        });
        holder.chkDoShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v);
            }
        });
        holder.chkDoCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v);
            }
        });
        holder.chkDoMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v);
            }
        });
        holder.chkDoWash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v);
            }
        });
        holder.chkDoService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v);
            }
        });

        if(r != null && r.getSkills() != null){
            holder.chkDoSend.setChecked(false);
            holder.chkDoJek.setChecked(false);
            holder.chkDoShop.setChecked(false);
            holder.chkDoMart.setChecked(false);
            holder.chkDoCar.setChecked(false);
            holder.chkDoMove.setChecked(false);
            holder.chkDoWash.setChecked(false);
            holder.chkDoService.setChecked(false);
            String[] skills = r.getSkills().split(",");
            for (int i = 0; i < skills.length; i++) {
                String skill = skills[i];
                if(skill.equalsIgnoreCase(AppConfig.KEY_DOSEND)) holder.chkDoSend.setChecked(true);
                if(skill.equalsIgnoreCase(AppConfig.KEY_DOJEK)) holder.chkDoJek.setChecked(true);
                if(skill.equalsIgnoreCase(AppConfig.KEY_DOMART)) holder.chkDoMart.setChecked(true);
                if(skill.equalsIgnoreCase(AppConfig.KEY_DOSHOP)) holder.chkDoShop.setChecked(true);
                if(skill.equalsIgnoreCase(AppConfig.KEY_DOCAR)) holder.chkDoCar.setChecked(true);
                if(skill.equalsIgnoreCase(AppConfig.KEY_DOMOVE)) holder.chkDoMove.setChecked(true);
                if(skill.equalsIgnoreCase(AppConfig.KEY_DOWASH)) holder.chkDoWash.setChecked(true);
                if(skill.equalsIgnoreCase(AppConfig.KEY_DOSERVICE)) holder.chkDoService.setChecked(true);
            }
        }

    }

    HashMap<String,String> fiturMap = new HashMap<>();

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()){
            case R.id.chkDoSend:
                if(checked)
                    fiturMap.put(AppConfig.KEY_DOSEND, AppConfig.KEY_DOSEND);
                else
                    fiturMap.remove(AppConfig.KEY_DOSEND);
                break;
            case R.id.chkDoJek:
                if(checked)
                    fiturMap.put(AppConfig.KEY_DOJEK, AppConfig.KEY_DOJEK);
                else
                    fiturMap.remove(AppConfig.KEY_DOJEK);
                break;
            case R.id.chkDoMart:
                if(checked)
                    fiturMap.put(AppConfig.KEY_DOMART, AppConfig.KEY_DOMART);
                else
                    fiturMap.remove(AppConfig.KEY_DOMART);
                break;
            case R.id.chkDoShop:
                if(checked)
                    fiturMap.put(AppConfig.KEY_DOSHOP, AppConfig.KEY_DOSHOP);
                else
                    fiturMap.remove(AppConfig.KEY_DOSHOP);
                break;
            case R.id.chkDoCar:
                if(checked)
                    fiturMap.put(AppConfig.KEY_DOCAR, AppConfig.KEY_DOCAR);
                else
                    fiturMap.remove(AppConfig.KEY_DOCAR);
                break;
            case R.id.chkDoMove:
                if(checked)
                    fiturMap.put(AppConfig.KEY_DOMOVE, AppConfig.KEY_DOMOVE);
                else
                    fiturMap.remove(AppConfig.KEY_DOMOVE);
                break;
            case R.id.chkDoWash:
                if(checked)
                    fiturMap.put(AppConfig.KEY_DOWASH, AppConfig.KEY_DOWASH);
                else
                    fiturMap.remove(AppConfig.KEY_DOWASH);
                break;
            case R.id.chkDoService:
                if(checked)
                    fiturMap.put(AppConfig.KEY_DOSERVICE, AppConfig.KEY_DOSERVICE);
                else
                    fiturMap.remove(AppConfig.KEY_DOSERVICE);
                break;
        }
    }
    public void setMultipleMode(boolean multiple){
        this.multiple = multiple;
    }
    public void selected(int pos){
        selected = pos;
        notifyItemRangeChanged(0, data.size());
    }
    @Override
    public int getItemCount() {
        return data.size();
    }


    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    public void setSelection(int pos) {
        selectedItems.put(pos, true);
        notifyItemChanged(pos);
    }
    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        }
        else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        protected RadioButton radioSelected;
        protected TextView alamat;
        protected TextView city;
        protected TextView name;
        protected TextView phone;
        protected TextView nik;
        protected TextView simc;
        protected ImageButton approvedBtn;
        protected ImageButton callBtn;
        protected ImageButton removeBtn;

        CheckBox chkDoSend;
        CheckBox chkDoJek;
        CheckBox chkDoMart;
        CheckBox chkDoShop;
        CheckBox chkDoCar;
        CheckBox chkDoMove;
        CheckBox chkDoWash;
        CheckBox chkDoService;
        public MyItemHolder(View itemView) {
            super(itemView);
            this.alamat= (TextView) itemView.findViewById(R.id.address_text);
            this.city= (TextView) itemView.findViewById(R.id.city_text);
            this.radioSelected= (RadioButton) itemView.findViewById(R.id.radio_selected);
            this.name= (TextView) itemView.findViewById(R.id.name_text);
            this.phone= (TextView) itemView.findViewById(R.id.telepon_text);
            this.approvedBtn = (ImageButton) itemView.findViewById(R.id.approvedBtn);
            this.callBtn = (ImageButton) itemView.findViewById(R.id.callBtn);
            this.removeBtn = (ImageButton) itemView.findViewById(R.id.removeBtn);
            this.nik= (TextView) itemView.findViewById(R.id.nik_text);
            this.simc= (TextView) itemView.findViewById(R.id.simc_text);

            this.chkDoSend = (CheckBox) itemView.findViewById(R.id.chkDoSend);
            this.chkDoJek = (CheckBox) itemView.findViewById(R.id.chkDoJek);
            this.chkDoMart= (CheckBox) itemView.findViewById(R.id.chkDoMart);
            this.chkDoShop= (CheckBox) itemView.findViewById(R.id.chkDoShop);
            this.chkDoWash= (CheckBox) itemView.findViewById(R.id.chkDoWash);
            this.chkDoService= (CheckBox) itemView.findViewById(R.id.chkDoService);
            this.chkDoCar= (CheckBox) itemView.findViewById(R.id.chkDoCar);
            this.chkDoMove= (CheckBox) itemView.findViewById(R.id.chkDoMove);

        }
    }

    public interface OnItemClickListener {
        void onApprovedButtonClick(View view, int position, HashMap<String, String> fiturMap);
        void onCallButtonClick(View view, int position);
        void onRemoveButtonClick(View v, int position);
    }
}