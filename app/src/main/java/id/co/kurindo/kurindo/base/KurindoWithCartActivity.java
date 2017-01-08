package id.co.kurindo.kurindo.base;

import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.tonyvu.sc.model.Cart;
import com.android.tonyvu.sc.util.CartHelper;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.ShoppingCartActivity;

/**
 * Created by dwim on 1/5/2017.
 */

public abstract class KurindoWithCartActivity extends BaseActivity {
    protected TextView cart_txtView;
    public int cart_count = 0;
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_cart);
        MenuItemCompat.setActionView(item, R.layout.action_bar_notification_icon);
        View menu_cartlist = MenuItemCompat.getActionView(item);
        //View menu_cartlist = item.getActionView();

        cart_txtView = (TextView) menu_cartlist.findViewById(R.id.item_txtView);
        updateCartCount();
        menu_cartlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(ShoppingCartActivity.class);
            }
        });
        return true;
    }
    public void updateCartCount() {
        Cart cart = CartHelper.getCart();
        final int new_count = cart.getProducts() == null ? 0 :cart.getProducts().size();
        cart_count = new_count;
        if (cart_txtView == null) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (new_count== 0)
                    cart_txtView.setVisibility(View.INVISIBLE);
                else {
                    cart_txtView.setVisibility(View.VISIBLE);
                    cart_txtView.setText(Integer.toString(new_count));
                }
            }
        });
    }

}
