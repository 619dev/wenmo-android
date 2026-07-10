package ink.wenmo.ime.engine;

import java.util.List;

public interface InputEngine {
    void type(char value);
    void backspace();
    void clear();
    String composition();
    List<String> candidates();
    void setTraditional(boolean traditional);
    boolean isTraditional();
}

