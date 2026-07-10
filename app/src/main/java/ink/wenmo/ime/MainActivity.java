package ink.wenmo.ime;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class MainActivity extends Activity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        int pad = dp(24);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(pad, dp(64), pad, pad);
        root.setBackgroundColor(Color.rgb(247, 243, 234));

        TextView title = text(getString(R.string.setup_title), 28);
        TextView body = text(getString(R.string.setup_body), 17);
        body.setPadding(0, dp(20), 0, dp(28));
        Button enable = new Button(this);
        enable.setText(R.string.enable_ime);
        enable.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)));
        Button choose = new Button(this);
        choose.setText(R.string.choose_ime);
        choose.setOnClickListener(v -> ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showInputMethodPicker());

        root.addView(title, matchWrap());
        root.addView(body, matchWrap());
        root.addView(enable, matchWrap());
        root.addView(choose, matchWrap());
        setContentView(root);
    }

    private TextView text(String value, int sp) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(sp);
        view.setTextColor(Color.rgb(32, 33, 36));
        return view;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}

