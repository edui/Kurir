package id.co.kurindo.kurindo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.co.kurindo.kurindo.base.BaseFragment;

/**
 * Created by DwiM on 11/14/2016.
 */
public class SnKFragment extends BaseFragment {
    @Bind(R.id.contentText)
    TextView contentText;
    @Bind(R.id.textView)
    TextView viewText;
    @Bind(R.id.ivProductImage)
    ImageView ivProductImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflateAndBind(inflater, container, R.layout.snk_layout);
        //contentText = (TextView) rootView.findViewById(R.id.contentText);
        //viewText = (TextView) rootView.findViewById(R.id.textView);
        viewText.setText("Syarat & Ketentuan");
        ivProductImage.setImageResource(R.drawable.icon_syarat_ketentuan);
        try {
            //File myFile = new File("/sdcard/filename.txt");
            //FileInputStream iStr = new FileInputStream(myFile);
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(getContext().getResources().openRawResource(R.raw.snk_file) ));
            String TextLine= "";
            String TextBuffer = "";
            while ((TextLine= fileReader.readLine()) != null) {
                TextBuffer += TextLine+ "\n";
            }
            //viewText.setText(Html.fromHtml(TextBuffer));
            contentText.setText(TextBuffer);
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rootView ;
    }
}
