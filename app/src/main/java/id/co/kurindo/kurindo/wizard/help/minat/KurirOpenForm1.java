package id.co.kurindo.kurindo.wizard.help.minat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stepstone.stepper.VerificationError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import id.co.kurindo.kurindo.R;

/**
 * Created by dwim on 3/15/2017.
 */

public class KurirOpenForm1 extends KurindoOpenForm1 {
    private static final String TAG = "KurirOpenForm1";
    VerificationError invalid = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_kurindoopen1);

        tvHelpTitle.setText("Petunjuk untuk menjadi Kurir Kurindo");
        ivHelpIcon.setImageResource(R.drawable.do_jek_icon);
        try {
            //File myFile = new File("/sdcard/filename.txt");
            //FileInputStream iStr = new FileInputStream(myFile);
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(getContext().getResources().openRawResource(R.raw.open_kurir_guide) ));
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
        return R.string.kurir_open_form;
    }

}
