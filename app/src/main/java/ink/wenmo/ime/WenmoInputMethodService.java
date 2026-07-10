package ink.wenmo.ime;

import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.view.Gravity;
import android.text.InputType;
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
    private LinearLayout keyboardPanel;
    private TextView composition;
    private Button scriptToggle;
    private KeyboardMode keyboardMode = KeyboardMode.ALPHABETIC;

    private enum KeyboardMode { ALPHABETIC, NUMBER, SYMBOL }

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
        composition.setTextSize(16);
        composition.setTextColor(Color.rgb(32, 33, 36));
        composition.setGravity(Gravity.CENTER_VERTICAL);
        composition.setPadding(dp(8), 0, dp(6), 0);
        toolbar.addView(composition, new LinearLayout.LayoutParams(-2, dp(42)));

        HorizontalScrollView scroller = new HorizontalScrollView(this);
        scroller.setHorizontalScrollBarEnabled(false);
        candidates = row();
        scroller.addView(candidates);
        toolbar.addView(scroller, new LinearLayout.LayoutParams(0, dp(42), 1));

        scriptToggle = key("简", v -> toggleScript());
        toolbar.addView(scriptToggle, fixed(48, 42));
        toolbar.addView(key("⌄", v -> requestHideSelf(0)), fixed(48, 42));
        root.addView(toolbar);

        keyboardPanel = new LinearLayout(this);
        keyboardPanel.setOrientation(LinearLayout.VERTICAL);
        root.addView(keyboardPanel, new LinearLayout.LayoutParams(-1, -2));
        showKeyboard(keyboardMode);
        refresh();
        return root;
    }

    @Override public void onStartInput(android.view.inputmethod.EditorInfo info, boolean restarting) {
        super.onStartInput(info, restarting);
        if (engine == null) engine = new LocalInputEngine(getApplicationContext());
        engine.clear();
        int inputClass = info.inputType & InputType.TYPE_MASK_CLASS;
        if (inputClass == InputType.TYPE_CLASS_NUMBER
                || inputClass == InputType.TYPE_CLASS_PHONE
                || inputClass == InputType.TYPE_CLASS_DATETIME) {
            showKeyboard(KeyboardMode.NUMBER);
        } else {
            showKeyboard(KeyboardMode.ALPHABETIC);
        }
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

    private void switchKeyboard(KeyboardMode mode) {
        if (keyboardMode == KeyboardMode.ALPHABETIC && mode != KeyboardMode.ALPHABETIC) {
            if (!engine.candidates().isEmpty()) select(engine.candidates().get(0));
            else if (!engine.composition().isEmpty()) select(engine.composition());
        }
        showKeyboard(mode);
        refresh();
    }

    private void showKeyboard(KeyboardMode mode) {
        keyboardMode = mode;
        if (keyboardPanel == null) return;
        keyboardPanel.removeAllViews();
        switch (mode) {
            case ALPHABETIC -> buildAlphabeticKeyboard();
            case NUMBER -> buildNumberKeyboard();
            case SYMBOL -> buildSymbolKeyboard();
        }
    }

    private void buildAlphabeticKeyboard() {
        for (String letters : new String[]{"qwertyuiop", "asdfghjkl", "zxcvbnm"}) {
            LinearLayout keyRow = row();
            for (char letter : letters.toCharArray()) {
                keyRow.addView(key(String.valueOf(letter), v -> type(letter)), weightedKey());
            }
            keyboardPanel.addView(keyRow);
        }
        LinearLayout bottom = row();
        bottom.addView(key("🌐", v -> switchToNextInputMethod(false)), fixed(48, 48));
        bottom.addView(key("123", v -> switchKeyboard(KeyboardMode.NUMBER)), fixed(52, 48));
        bottom.addView(key("符", v -> switchKeyboard(KeyboardMode.SYMBOL)), fixed(48, 48));
        bottom.addView(key("空格", v -> space()), weightedKey());
        bottom.addView(key("⌫", v -> backspace()), fixed(54, 48));
        bottom.addView(key("回车", v -> enter()), fixed(62, 48));
        keyboardPanel.addView(bottom);
    }

    private void buildNumberKeyboard() {
        addTextKeyRow("1", "2", "3", "+");
        addTextKeyRow("4", "5", "6", "-");
        addTextKeyRow("7", "8", "9", ".");

        LinearLayout bottom = row();
        bottom.addView(key("ABC", v -> switchKeyboard(KeyboardMode.ALPHABETIC)), fixed(62, 48));
        bottom.addView(key("#+=", v -> switchKeyboard(KeyboardMode.SYMBOL)), fixed(58, 48));
        bottom.addView(key("0", v -> commitRaw("0")), weightedKey());
        bottom.addView(key("⌫", v -> backspace()), fixed(58, 48));
        bottom.addView(key("回车", v -> enter()), fixed(68, 48));
        keyboardPanel.addView(bottom);
    }

    private void buildSymbolKeyboard() {
        addTextKeyRow("，", "。", "？", "！", "；", "：", "、", "…", "—", "·");
        addTextKeyRow("（", "）", "【", "】", "《", "》", "“", "”", "‘", "’");
        addTextKeyRow("@", "#", "$", "%", "&", "*", "+", "-", "=", "/");

        LinearLayout bottom = row();
        bottom.addView(key("ABC", v -> switchKeyboard(KeyboardMode.ALPHABETIC)), fixed(62, 48));
        bottom.addView(key("123", v -> switchKeyboard(KeyboardMode.NUMBER)), fixed(58, 48));
        bottom.addView(key("空格", v -> commitRaw(" ")), weightedKey());
        bottom.addView(key("⌫", v -> backspace()), fixed(58, 48));
        bottom.addView(key("回车", v -> enter()), fixed(68, 48));
        keyboardPanel.addView(bottom);
    }

    private void addTextKeyRow(String... labels) {
        LinearLayout keyRow = row();
        for (String label : labels) {
            keyRow.addView(key(label, v -> commitRaw(label)), weightedKey());
        }
        keyboardPanel.addView(keyRow);
    }

    private void toggleScript() {
        engine.setTraditional(!engine.isTraditional());
        refresh();
    }

    private void refresh() {
        if (composition == null || candidates == null) return;
        if (keyboardMode == KeyboardMode.ALPHABETIC) composition.setText(engine.composition());
        else if (keyboardMode == KeyboardMode.NUMBER) composition.setText("数字");
        else composition.setText("常用符号");
        scriptToggle.setText(engine.isTraditional() ? "繁" : "简");
        scriptToggle.setVisibility(keyboardMode == KeyboardMode.ALPHABETIC ? View.VISIBLE : View.INVISIBLE);
        candidates.removeAllViews();
        if (keyboardMode == KeyboardMode.ALPHABETIC) {
            for (String candidate : engine.candidates()) {
                candidates.addView(key(candidate, v -> select(candidate)), fixed(64, 42));
            }
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

    private LinearLayout.LayoutParams weightedKey() {
        return new LinearLayout.LayoutParams(0, dp(48), 1);
    }

    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
