package id.co.kurindo.kurindo.wizard.help.minat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

/**
 * Created by dwim on 3/15/2017.
 */

public class KurindoOpenForm1 extends BaseStepFragment implements Step {
    private static final String TAG = "KurindoOpenForm1";

    @Bind(R.id.tvHelpContent)
    TextView tvHelpContent;
    @Bind(R.id.tvHelpTitle)
    TextView tvHelpTitle;
    @Bind(R.id.ivHelpIcon)
    ImageView ivHelpIcon;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_kurindoopen1);


        tvHelpTitle.setText("Petunjuk untuk menjadi Mitra Cabang Kurindo");
        ivHelpIcon.setImageResource(R.drawable.doclient_icon);
        try {
            //File myFile = new File("/sdcard/filename.txt");
            //FileInputStream iStr = new FileInputStream(myFile);
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(getContext().getResources().openRawResource(R.raw.open_kurindo_guide) ));
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

    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
