package ink.wenmo.ime;

import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ink.wenmo.ime.engine.InputEngine;
import ink.wenmo.ime.engine.LocalInputEngine;

public final class WenmoInputMethodService extends InputMethodService {
    private InputEngine engine;
    private LinearLayout candidates;
    private TextView composition;
    private Button scriptToggle;

    @Override public View onCreateInputView() {
        if (engine == null) engine = new LocalInputEngine(getApplicationContext());
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(4), dp(4), dp(4), dp(6));
        root.setBackgroundColor(Color.rgb(231, 230, 226));
        root.setOnApplyWindowInsetsListener((view, insets) -> {
            int bottom;
            if (android.os.Build.VERSION.SDK_INT >= 30) {
                bottom = insets.getInsets(WindowInsets.Type.navigationBars()).bottom;
            } else {
                bottom = insets.getSystemWindowInsetBottom();
            }
            view.setPadding(dp(4), dp(4), dp(4), Math.max(dp(6), bottom + dp(4)));
            return insets;
        });

        LinearLayout toolbar = row();
        composition = new TextView(this);
        composition.setTextSize(18);
        composition.setTextColor(Color.rgb(32, 33, 36));
        composition.setGravity(Gravity.CENTER_VERTICAL);
        toolbar.addView(composition, new LinearLayout.LayoutParams(0, dp(42), 1));
        scriptToggle = key("简", v -> toggleScript());
        toolbar.addView(scriptToggle, fixed(54, 42));
        toolbar.addView(key("⌄", v -> requestHideSelf(0)), fixed(54, 42));
        root.addView(toolbar);

        HorizontalScrollView scroller = new HorizontalScrollView(this);
        candidates = row();
        scroller.addView(candidates);
        root.addView(scroller, new LinearLayout.LayoutParams(-1, dp(44)));

        String[] rows = {"qwertyuiop", "asdfghjkl", "zxcvbnm"};
        for (String letters : rows) {
            LinearLayout row = row();
            for (char letter : letters.toCharArray()) {
                row.addView(key(String.valueOf(letter), v -> type(letter)), new LinearLayout.LayoutParams(0, dp(48), 1));
            }
            root.addView(row);
        }

        LinearLayout bottom = row();
        bottom.addView(key("🌐", v -> switchToNextInputMethod(false)), fixed(54, 48));
        bottom.addView(key("123", v -> commitRaw("123")), fixed(62, 48));
        bottom.addView(key("空格", v -> space()), new LinearLayout.LayoutParams(0, dp(48), 1));
        bottom.addView(key("⌫", v -> backspace()), fixed(62, 48));
        bottom.addView(key("回车", v -> enter()), fixed(68, 48));
        root.addView(bottom);
        refresh();
        return root;
    }

    @Override public void onStartInput(android.view.inputmethod.EditorInfo info, boolean restarting) {
        super.onStartInput(info, restarting);
        if (engine == null) engine = new LocalInputEngine(getApplicationContext());
        engine.clear();
        refresh();
    }

    private void type(char letter) { engine.type(letter); refresh(); }

    private void select(String value) {
        InputConnection connection = getCurrentInputConnection();
        if (connection != null) connection.commitText(value, 1);
        engine.clear();
        refresh();
    }

    private void space() {
        if (!engine.candidates().isEmpty()) select(engine.candidates().get(0));
        else if (!engine.composition().isEmpty()) select(engine.composition());
        else commitRaw(" ");
    }

    private void backspace() {
        if (!engine.composition().isEmpty()) { engine.backspace(); refresh(); return; }
        InputConnection connection = getCurrentInputConnection();
        if (connection != null) connection.deleteSurroundingText(1, 0);
    }

    private void enter() {
        if (!engine.composition().isEmpty()) space();
        else sendDefaultEditorAction(true);
    }

    private void commitRaw(String value) {
        InputConnection connection = getCurrentInputConnection();
        if (connection != null) connection.commitText(value, 1);
    }

    private void toggleScript() {
        engine.setTraditional(!engine.isTraditional());
        refresh();
    }

    private void refresh() {
        if (composition == null || candidates == null) return;
        composition.setText(engine.composition());
        scriptToggle.setText(engine.isTraditional() ? "繁" : "简");
        candidates.removeAllViews();
        for (String candidate : engine.candidates()) {
            candidates.addView(key(candidate, v -> select(candidate)), fixed(72, 42));
        }
    }

    private LinearLayout row() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        return row;
    }

    private Button key(String label, View.OnClickListener listener) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextSize(15);
        button.setAllCaps(false);
        button.setPadding(0, 0, 0, 0);
        button.setOnClickListener(listener);
        return button;
    }

    private LinearLayout.LayoutParams fixed(int width, int height) {
        return new LinearLayout.LayoutParams(dp(width), dp(height));
    }

    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
