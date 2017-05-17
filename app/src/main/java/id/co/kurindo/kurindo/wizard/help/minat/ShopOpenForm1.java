package id.co.kurindo.kurindo.wizard.help.minat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.wizard.help.minat.KurindoOpenForm1;

/**
 * Created by dwim on 3/15/2017.
 */

public class ShopOpenForm1 extends KurindoOpenForm1 {
    private static final String TAG = "ShopOpenForm1";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_kurindoopen1);

        tvHelpTitle.setText("Petunjuk untuk membuka Toko di Kurindo");
        ivHelpIcon.setImageResource(R.drawable.do_shop_icon);
        try {
            //File myFile = new File("/sdcard/filename.txt");
            //FileInputStream iStr = new FileInputStream(myFile);
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(getContext().getResources().openRawResource(R.raw.open_shop_guide) ));
            String TextLine= "";
            String TextBuffer = "";
            while ((TextLine= fileReader.readLine()) != null) {
                TextBuffer += TextLine+ "\n";
            }
            //viewText.setText(Html.fromHtml(TextBuffer));
            tvHelpContent.setText(TextBuffer);
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return v;
    }

    @Override
    public int getName() {
        return R.string.shop_open_form;
    }

}
