package id.co.kurindo.kurindo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Ratan on 7/29/2015.
 */
public class LuarKotaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.luarkota_layout, container, false);
        TextView contentText = (TextView) rootView.findViewById(R.id.contentText);

        return rootView;
    }
}
