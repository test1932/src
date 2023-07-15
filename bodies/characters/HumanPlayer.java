package bodies.characters;

import java.util.HashMap;

public class HumanPlayer extends AbstractPlayer {
    public enum Keys {Left, Right, Up, Down, Graze, Melee, Weak, Strong}
    private HashMap<Integer, Keys> keyMapping;

    public HumanPlayer(Boolean isLeft) {
        super(isLeft);
    }

    public void updateKey(Integer code, Keys val) {
        keyMapping.put(code, val);
    }

    public Keys getInputAction(Integer code) {
        return keyMapping.get(code);
    }
}
