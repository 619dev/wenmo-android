package ink.wenmo.ime.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Temporary Android implementation. The stable API will be backed by the Rust engine. */
public final class LocalInputEngine implements InputEngine {
    private static final Map<String, List<String>> SIMPLIFIED = new HashMap<>();
    private static final Map<String, List<String>> TRADITIONAL = new HashMap<>();
    static {
        SIMPLIFIED.put("ni", List.of("你", "呢", "泥", "拟"));
        SIMPLIFIED.put("hao", List.of("好", "号", "浩", "豪"));
        SIMPLIFIED.put("nihao", List.of("你好"));
        SIMPLIFIED.put("wen", List.of("问", "文", "闻", "稳"));
        SIMPLIFIED.put("mo", List.of("墨", "莫", "末", "默"));
        SIMPLIFIED.put("wenmo", List.of("问墨", "文墨"));
        SIMPLIFIED.put("zhongguo", List.of("中国"));
        TRADITIONAL.put("ni", List.of("你", "呢", "泥", "擬"));
        TRADITIONAL.put("hao", List.of("好", "號", "浩", "豪"));
        TRADITIONAL.put("nihao", List.of("你好"));
        TRADITIONAL.put("wen", List.of("問", "文", "聞", "穩"));
        TRADITIONAL.put("mo", List.of("墨", "莫", "末", "默"));
        TRADITIONAL.put("wenmo", List.of("問墨", "文墨"));
        TRADITIONAL.put("zhongguo", List.of("中國"));
    }

    private final StringBuilder composition = new StringBuilder();
    private boolean traditional;

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
        return (traditional ? TRADITIONAL : SIMPLIFIED)
            .getOrDefault(composition.toString().toLowerCase(Locale.ROOT), Collections.emptyList());
    }
    @Override public void setTraditional(boolean value) { traditional = value; }
    @Override public boolean isTraditional() { return traditional; }
}

