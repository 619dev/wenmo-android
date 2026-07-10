package ink.wenmo.ime.engine;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Temporary Android implementation. The stable API will be backed by the Rust engine. */
public final class LocalInputEngine implements InputEngine {
    private final Map<String, List<String>> simplified = new HashMap<>();
    private final Map<String, List<String>> traditionalWords = new HashMap<>();

    private final StringBuilder composition = new StringBuilder();
    private boolean traditional;

    public LocalInputEngine(Context context) {
        loadDictionary(context);
    }

    @Override public void type(char value) {
        if (value >= 'a' && value <= 'z') composition.append(value);
    }
    @Override public void backspace() {
        if (composition.length() > 0) composition.deleteCharAt(composition.length() - 1);
    }
    @Override public void clear() { composition.setLength(0); }
    @Override public String composition() { return composition.toString(); }
    @Override public List<String> candidates() {
        if (composition.length() == 0) return Collections.emptyList();
        return (traditional ? traditionalWords : simplified)
            .getOrDefault(composition.toString().toLowerCase(Locale.ROOT), Collections.emptyList());
    }
    @Override public void setTraditional(boolean value) { traditional = value; }
    @Override public boolean isTraditional() { return traditional; }

    private void loadDictionary(Context context) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                context.getAssets().open("cedict_pinyin.tsv"), StandardCharsets.UTF_8), 64 * 1024)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty() || line.charAt(0) == '#') continue;
                String[] fields = line.split("\\t", 3);
                if (fields.length != 3) continue;
                add(simplified, fields[0], fields[1]);
                add(traditionalWords, fields[0], fields[2]);
            }
        } catch (IOException error) {
            throw new IllegalStateException("Bundled pinyin dictionary is unavailable", error);
        }
    }

    private static void add(Map<String, List<String>> dictionary, String key, String word) {
        List<String> values = dictionary.computeIfAbsent(key, ignored -> new ArrayList<>());
        if (values.size() < 32 && !values.contains(word)) values.add(word);
    }
}
